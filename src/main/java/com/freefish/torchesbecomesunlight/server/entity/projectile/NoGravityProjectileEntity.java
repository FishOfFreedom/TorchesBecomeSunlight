package com.freefish.torchesbecomesunlight.server.entity.projectile;

import com.freefish.torchesbecomesunlight.client.util.particle.util.AdvancedParticleBase;
import com.freefish.torchesbecomesunlight.client.util.particle.util.ParticleComponent;
import com.freefish.torchesbecomesunlight.client.util.particle.util.RibbonComponent;
import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.FrozenCapability;
import com.freefish.torchesbecomesunlight.server.entity.effect.EntityCameraShake;
import com.freefish.torchesbecomesunlight.server.entity.effect.IceTuft;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.GuerrillasEntity;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster.FrostNova;
import com.freefish.torchesbecomesunlight.server.init.*;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import net.minecraft.core.BlockPos;
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

import javax.annotation.Nullable;
import java.util.List;

public abstract class NoGravityProjectileEntity extends Projectile implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int type1 = -1;
    private Vec3[] trailPositions = new Vec3[16];
    private int trailPointer = -1;

    private static final EntityDataAccessor<Boolean> IS_HIT = SynchedEntityData.defineId(IceCrystal.class, EntityDataSerializers.BOOLEAN);

    public NoGravityProjectileEntity(EntityType<? extends NoGravityProjectileEntity> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(IS_HIT,false);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        setIsHit(compoundTag.getBoolean("ishit"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putBoolean("ishit",isHit());
    }
    private int hitTime;


    @Override
    public void tick() {
        if(tickCount==1){
            if(level().isClientSide){
                AdvancedParticleBase.spawnParticle(level(),ParticleHandler.RING_BIG.get(),xo,yo,zo,0,0,0,true,0,0,0,0,1,0,0,0,1,0,8,true,false,new ParticleComponent[]{
                        new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, ParticleComponent.KeyTrack.startAndEnd(0f, 16f), false),
                        new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, ParticleComponent.KeyTrack.startAndEnd(0.8f, 0f), false)
                });
                AdvancedParticleBase.spawnParticle(level(),ParticleHandler.BURST_MESSY.get(),xo,yo,zo,0,0,0,true,0,0,0,0,1,0.5,0.5,0.5,1,0,12,true,false,new ParticleComponent[]{
                        new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, ParticleComponent.KeyTrack.startAndEnd(0f, 20f), false),
                        new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, ParticleComponent.KeyTrack.startAndEnd(0.8f, 0f), false)
                });
            }
        }
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
                    //spawnIceParticle();
                }
                else {
                    playSound(SoundHandle.ICE_CRYSTAL.get(), 1.0F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
                    EntityCameraShake.cameraShake(this.level(), this.position(), 16F, 0.025F, 5, 15);
                    //spawnIceParticle();
                }
            }
            hitTime++;
        }

        this.move(MoverType.SELF, this.getDeltaMovement());

        if(level().isClientSide){

            Vec3 trailAt = this.position();
            if (trailPointer == -1) {
                Vec3 backAt = trailAt;
                for (int i = 0; i < trailPositions.length; i++) {
                    trailPositions[i] = backAt;
                }
            }
            if (++this.trailPointer == this.trailPositions.length) {
                this.trailPointer = 0;
            }
            this.trailPositions[this.trailPointer] = trailAt;
        }

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
    }

    @Override
    public void move(MoverType pType, Vec3 pPos) {
        if(!level().isClientSide){
            double length = pPos.length();
                int len = (int) (length)+1;
                for (int i = 0; i < len; i++) {
                    Vec3 pos = position().add(pPos.scale(i / (float) len));
                    BlockState blockState = level().getBlockState(new BlockPos((int)pos.x,(int)pos.y,(int)pos.z));
                    if (!blockState.isAir()&&!isHit()) {
                        setIsHit(true);
                    }
                }
        }
        this.setPos(this.getX() + pPos.x, this.getY() + pPos.y, this.getZ() + pPos.z);
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
                        float bei = (living instanceof FrostNova frostNova&&frostNova.getAnimation()==FrostNova.LULLABYE_2)?1.5f:1;

                        if(target.hurt(this.damageSources().mobAttack(living), damage*bei)){
                            flad=true;
                            target.invulnerableTime=0;
                            FrozenCapability.IFrozenCapability data = CapabilityHandle.getCapability(target, CapabilityHandle.FROZEN_CAPABILITY);
                            if(data!=null) data.setFrozen(target,60);
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

    public boolean isHit(){
        return this.entityData.get(IS_HIT);
    }

    public void setIsHit(boolean isHit){
        this.entityData.set(IS_HIT,isHit);
    }
    @Override
    public void lerpMotion(double pX, double pY, double pZ) {
        this.setDeltaMovement(pX, pY, pZ);
    }

    public void shoot(double x, double y, double z, float inaccuracy) {
        Vec3 vec3 = (new Vec3(x, y, z)).add(this.random.triangle(0.0D, 0.0172275D * (double) inaccuracy), this.random.triangle(0.0D, 0.0172275D * (double) inaccuracy), this.random.triangle(0.0D, 0.0172275D * (double) inaccuracy));
        this.setDeltaMovement(vec3);
        double d0 = vec3.horizontalDistance();
        this.setYRot((float) (Mth.atan2(vec3.x, vec3.z) * (double) (180F / (float) Math.PI)));
        this.setXRot((float) (Mth.atan2(vec3.y, d0) * (double) (180F / (float) Math.PI)));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }
}
