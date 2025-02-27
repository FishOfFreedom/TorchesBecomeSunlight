package com.freefish.torchesbecomesunlight.server.entity.projectile;

import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.init.ParticleHandler;
import com.freefish.torchesbecomesunlight.client.util.particle.util.AdvancedParticleBase;
import com.freefish.torchesbecomesunlight.client.util.particle.util.ParticleComponent;
import com.freefish.torchesbecomesunlight.server.capability.FrozenCapability;
import com.freefish.torchesbecomesunlight.server.entity.effect.EntityCameraShake;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.init.SoundHandle;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class IceBlade extends Projectile implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<Boolean> IS_HIT = SynchedEntityData.defineId(IceBlade.class, EntityDataSerializers.BOOLEAN);

    public IceBlade(EntityType<? extends IceBlade> entityType, Level level) {
        super(entityType, level);
    }

    public IceBlade(Level level, LivingEntity caster) {
        super(EntityHandle.ICE_BLADE.get(), level);
        setOwner(caster);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(IS_HIT,false);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        setIsHit(compoundTag.getBoolean("ishit"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putBoolean("ishit",isHit());
    }

    public boolean isHit(){
        return this.entityData.get(IS_HIT);
    }

    public void setIsHit(boolean isHit){
        this.entityData.set(IS_HIT,isHit);
    }

    private int hitTime;

    @Override
    public void tick() {
        super.tick();
        if(isHit()){
            if(hitTime>=1){
                discard();
            }
            if(hitTime==0){
                if(level().isClientSide){
                    AdvancedParticleBase.spawnParticle(level(),ParticleHandler.BURST_MESSY.get(),xo,yo,zo,0,0,0,true,0,0,0,0,1,1,1,1,1,0,8,true,false,new ParticleComponent[]{
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, ParticleComponent.KeyTrack.startAndEnd(0f, 16f), false),
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, ParticleComponent.KeyTrack.startAndEnd(0.8f, 0f), false)
                    });
                }
                else {
                    playSound(SoundHandle.ICE_CRYSTAL.get(), 1.0F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
                    EntityCameraShake.cameraShake(this.level(), this.position(), 16F, 0.025F, 5, 15);
                }
            }
            hitTime++;
        }

        this.move(MoverType.SELF, this.getDeltaMovement());

        if (this.tickCount > 40) {
            this.discard();
        } else {
            this.doHurtTarget();
        }

        BlockState blockState = getBlockStateOn();
        if (!level().isClientSide()&&!blockState.isAir()&&!isHit()) {
            setIsHit(true);
        }

        Vec3 vec3 = this.getDeltaMovement();
        double d6 = vec3.y;
        double d4 = vec3.horizontalDistance();

        this.setXRot((float)(Mth.atan2(d6, d4) * (double)(180F / (float)Math.PI)));
        this.setYRot((float) (Mth.atan2(vec3.x, vec3.z) * (double) (180F / (float) Math.PI)));
        spawnRing();
    }

    private void doHurtTarget() {
        if(level().isClientSide() ) return;
        if (!isHit()) {
            List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(getDeltaMovement().length()+1));
            boolean flad = false;
            Entity caster = getOwner();
            for (LivingEntity target : entities) {
                if (target == caster) continue;
                boolean flad1 = false;

                Vec3 oldPosition = new Vec3(xo,yo,zo);
                Vec3 position = position();
                Vec3 totarget = target.position().add(0,Math.min(target.getBbHeight(),getY()-target.getY()),0).subtract(position);
                Vec3 line = oldPosition.subtract(position).normalize();
                float l = (float) line.dot(totarget);
                if(l>=0) {
                    Vec3 len = line.scale(l);
                    float fa = (float) len.subtract(totarget).length();
                    if(fa<=0.1+target.getBbWidth()) flad1 = true;
                }

                if (flad1 && caster instanceof LivingEntity living) {
                    AttributeInstance attribute = living.getAttribute(Attributes.ATTACK_DAMAGE);
                    if (attribute != null) {
                        float damage = (float) attribute.getValue();

                        FrozenCapability.IFrozenCapability data = CapabilityHandle.getCapability(target, CapabilityHandle.FROZEN_CAPABILITY);
                        boolean b = data!=null&&data.getFrozen();
                        if(target.hurt(this.damageSources().mobAttack(living), b?damage*2:damage)){
                            flad=true;
                            if(b){
                                data.clearFrozen(target);
                            }
                        }
                    }
                }
            }
            if(flad){
                setIsHit(true);
            }
        }
    }

    @Override
    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    @Override
    public boolean isInWater() {
        return false;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public void spawnRing(){
        if(level().isClientSide){
            if(tickCount%2==0){
                int i1 = 2 + random.nextInt(2);
                for(int i = 0 ;i<i1;i++) {
                    Vec3 vec3 = getDeltaMovement().scale(-1).yRot(0.15f - random.nextFloat() * 0.3f);
                    AdvancedParticleBase.spawnParticle(level(), ParticleHandler.SAN.get(), getX(), getY(), getZ(), vec3.x, 0, vec3.z, true, 0, 0, 0, 0, 1.5F, 1, 1, 1, 0.75, 1, 40+random.nextInt(21), true, false, new ParticleComponent[]{
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, ParticleComponent.KeyTrack.startAndEnd(1f, 0f), false)
                    });
                }
            }
        }
    }
}
