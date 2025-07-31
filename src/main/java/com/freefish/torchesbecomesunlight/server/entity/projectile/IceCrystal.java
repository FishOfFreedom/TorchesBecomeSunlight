package com.freefish.torchesbecomesunlight.server.entity.projectile;

import com.freefish.torchesbecomesunlight.client.util.particle.util.AdvancedParticleBase;
import com.freefish.torchesbecomesunlight.client.util.particle.util.ParticleComponent;
import com.freefish.torchesbecomesunlight.server.effect.forceeffect.ForceEffectHandle;
import com.freefish.torchesbecomesunlight.server.effect.forceeffect.ForceEffectInstance;
import com.freefish.torchesbecomesunlight.server.entity.effect.EntityCameraShake;
import com.freefish.torchesbecomesunlight.server.entity.effect.IceTuft;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster.FrostNova;
import com.freefish.torchesbecomesunlight.server.init.DamageSourceHandle;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.init.ParticleHandler;
import com.freefish.torchesbecomesunlight.server.init.SoundHandle;
import com.freefish.torchesbecomesunlight.server.partner.Partner;
import com.freefish.torchesbecomesunlight.server.partner.PartnerUtil;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class IceCrystal extends Projectile implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int type1 = -1;
    private Vec3[] trailPositions = new Vec3[16];
    private int trailPointer = -1;
    private LivingEntity waiterEntity;
    private Vec3 waiterVec;

    private static final EntityDataAccessor<Integer> TYPE = SynchedEntityData.defineId(IceCrystal.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_HIT = SynchedEntityData.defineId(IceCrystal.class, EntityDataSerializers.BOOLEAN);

    private final int[][] off = new int[][]{new int[]{3,0},new int[]{-3,0},new int[]{0,3},new int[]{0,-3}};

    public IceCrystal(EntityType<? extends IceCrystal> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }

    public IceCrystal(Level level, LivingEntity caster) {
        this(EntityHandle.ICE_CRYSTAL.get(), level);
        setOwner(caster);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(TYPE,0);
        this.entityData.define(IS_HIT,false);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        setType(compoundTag.getInt("type"));
        setIsHit(compoundTag.getBoolean("ishit"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("type",getType1());
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

        if(getType1()==2){
            if(tickCount==27){
                if(getOwner() instanceof FrostNova frostNova){
                    float speed = 40f;
                    LivingEntity target = frostNova.getTarget();
                    if (target != null) {
                        double d0 = target.getX() - getX();
                        double d2 = target.getZ() - getZ();
                        float dist = (float) (org.joml.Math.sqrt(d0 * d0 + d2 * d2));
                        float time = dist / speed;
                        if(noWait) time = 0;

                        Vec3 targetMoveVec = frostNova.getTargetMoveVec(target).scale(time).add(target.position());

                        Vec3 move = (new Vec3(targetMoveVec.x - getX(), target.getY(0.6) - getY(), targetMoveVec.z - getZ())).normalize().scale(2);

                        this.shoot(move.x, move.y, move.z, 0);
                    }
                }else if(getOwner() instanceof Player player){
                    LivingEntity target = waiterEntity;
                    if (target != null) {
                        Vec3 targetMoveVec = target.position();

                        Vec3 move = (new Vec3(targetMoveVec.x - getX(), target.getY(0.6) - getY(), targetMoveVec.z - getZ())).normalize().scale(2);

                        this.shoot(move.x, move.y, move.z, 0);
                    }else if(waiterVec !=null){
                        Vec3 move = (new Vec3(waiterVec.x - getX(), waiterVec.y - getY(), waiterVec.z - getZ())).normalize().scale(2);

                        this.shoot(move.x, move.y, move.z, 0);
                    }
                }
            }
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
        spawnRing();
    }

    public Vec3 getTrailPosition(int pointer, float partialTick) {
        if (this.isRemoved()) {
            partialTick = 1.0F;
        }
        int i = this.trailPointer - pointer & 15;
        int j = this.trailPointer - pointer - 1 & 15;
        Vec3 d0 = this.trailPositions[j];
        Vec3 d1 = this.trailPositions[i].subtract(d0);
        return d0.add(d1.scale(partialTick)).add(getDeltaMovement());
    }

    public boolean hasTrail() {
        return trailPointer != -1;
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
                if (flad1 && caster instanceof Player living) {
                    float damage = 10;
                    if(target.hurt(DamageSourceHandle.noTriggerNoArmorAttack(living), damage)){
                        flad=true;
                        target.invulnerableTime=0;
                        ForceEffectHandle.addForceEffect(target,new ForceEffectInstance(ForceEffectHandle.FROZEN_FORCE_EFFECT,1,60));
                    }
                }
                else if (flad1 && caster instanceof LivingEntity living) {
                    AttributeInstance attribute = living.getAttribute(Attributes.ATTACK_DAMAGE);
                    if (attribute != null) {
                        float damage = (float) attribute.getValue();
                        float bei = (living instanceof FrostNova frostNova&&frostNova.getAnimation()==FrostNova.LULLABYE_2)?1.5f:1;

                        if(target.hurt(this.damageSources().mobAttack(living), damage*bei)){
                            flad=true;
                            target.invulnerableTime=0;
                            ForceEffectHandle.addForceEffect(target,new ForceEffectInstance(ForceEffectHandle.FROZEN_FORCE_EFFECT,1,60));
                        }
                    }
                }
            }
            if(flad){
                setIsHit(true);
            }
        }
    }

    private void spawnIceParticle(){
        if(getOwner() instanceof FrostNova snowNova) {
            IceTuft iceTuft = new IceTuft(EntityHandle.ICE_TUFT.get(), level());
            iceTuft.caster = snowNova;
            iceTuft.setPos(MathUtils.getFirstBlockAbove(level(),position().add(0,-4,0),4));
            if (!level().isClientSide)
                level().addFreshEntity(iceTuft);
        }
        if(level().isClientSide){
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
        controllerRegistrar.add(new AnimationController<IceCrystal>(this, "Controller", 1, this::predicate));
    }

    private PlayState predicate(AnimationState<IceCrystal> event) {
        if(getType1()==2)
            event.getController().setAnimation(RawAnimation.begin().thenPlayAndHold("normal2"));
        else
            event.getController().setAnimation(RawAnimation.begin().thenLoop("normal"));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public void setType(int type){
        this.entityData.set(TYPE,type);
        type1 = type;
    }

    public int getType1(){
        if(type1!=-1) {
            return type1;
        }
        type1 = this.entityData.get(TYPE);
        return type1;
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

    public void spawnRing(){
        if(level().isClientSide&&tickCount%2==0){
            AdvancedParticleBase.spawnParticle(level(), ParticleHandler.RING_BIG.get(), getX(), getY(), getZ(), 0, 0, 0, false, Math.toRadians(getYRot()), -Math.toRadians(getXRot()), 0, 0, 4F, 1, 1, 1, 0.75, 1, 15, true, false, new ParticleComponent[]{
                    new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, ParticleComponent.KeyTrack.startAndEnd(1f, 0f), false),
                    new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, ParticleComponent.KeyTrack.startAndEnd(1f, 10f), false),
                    new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.RED, ParticleComponent.KeyTrack.startAndEnd(  0.1f,0.8f ), false),
                    new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.GREEN, ParticleComponent.KeyTrack.startAndEnd(0.1f,0.8f ), false),
                    new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.BLUE, ParticleComponent.KeyTrack.startAndEnd( 0.1f,0.8f ), false)
            });
        }
    }

    public static void spawnWaitCrystal(Level level,Vec3 vec3,LivingEntity caster,LivingEntity targetWait){
        IceCrystal iceCrystal = new IceCrystal(EntityHandle.ICE_CRYSTAL.get(),level);
        iceCrystal.setOwner(caster);
        iceCrystal.setType(2);
        iceCrystal.setPos(vec3);
        iceCrystal.waiterEntity = targetWait;
        level.addFreshEntity(iceCrystal);
    }

    boolean noWait = false;

    public static void spawnNoWaitCrystal(Level level,Vec3 vec3,LivingEntity caster,LivingEntity targetWait){
        IceCrystal iceCrystal = new IceCrystal(EntityHandle.ICE_CRYSTAL.get(),level);
        iceCrystal.setOwner(caster);
        iceCrystal.setType(2);
        iceCrystal.setPos(vec3);


        iceCrystal.waiterEntity = targetWait;
        if(caster instanceof Mob m){
            Partner<?> partner = PartnerUtil.getPartner(m);
            if(partner!=null){
                LivingEntity instanceTarget = partner.getInstanceTarget();
                if(instanceTarget!=null&&instanceTarget.isAlive()){
                    iceCrystal.waiterEntity = instanceTarget;
                }
            }
        }

        level.addFreshEntity(iceCrystal);
        iceCrystal.noWait = true;
    }

    public static void spawnWaitCrystal(Level level,Vec3 vec3,LivingEntity caster,Vec3 waiterVec){
        IceCrystal iceCrystal = new IceCrystal(EntityHandle.ICE_CRYSTAL.get(),level);
        iceCrystal.setOwner(caster);
        iceCrystal.setType(2);
        iceCrystal.setPos(vec3);
        iceCrystal.waiterVec = waiterVec;
        level.addFreshEntity(iceCrystal);
    }
}
