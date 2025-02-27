package com.freefish.torchesbecomesunlight.server.entity.projectile;

import com.freefish.torchesbecomesunlight.client.util.particle.ParticleCloud;
import com.freefish.torchesbecomesunlight.server.init.ParticleHandler;
import com.freefish.torchesbecomesunlight.client.util.particle.util.AdvancedParticleBase;
import com.freefish.torchesbecomesunlight.client.util.particle.util.ParticleComponent;
import com.freefish.torchesbecomesunlight.server.entity.dlc.GunKnightPatriot;
import com.freefish.torchesbecomesunlight.server.entity.effect.EntityCameraShake;
import com.freefish.torchesbecomesunlight.server.entity.effect.FXEntity;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.init.SoundHandle;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
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

import java.util.List;

public class Bullet extends Projectile {
    private int type1 = -1;

    private int bulletLen;
    private int oBulletLen;

    private Vec3[] trailPositions = new Vec3[16];
    private int trailPointer = -1;

    private static final EntityDataAccessor<Integer> TYPE = SynchedEntityData.defineId(Bullet.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_HIT = SynchedEntityData.defineId(Bullet.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_HOLY = SynchedEntityData.defineId(Bullet.class, EntityDataSerializers.BOOLEAN);

    public Bullet(EntityType<? extends Bullet> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }

    public Bullet(Level level, LivingEntity caster, int type) {
        this(EntityHandle.BULLET.get(), level);
        setOwner(caster);
        setType(type);
    }

    public Bullet(Level level,  int type) {
        this(EntityHandle.BULLET.get(), level);
        setType(type);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(TYPE,0);
        this.entityData.define(IS_HIT,false);
        this.entityData.define(IS_HOLY,false);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        setType(compoundTag.getInt("type"));
        setIsHit(compoundTag.getBoolean("ishit"));
        setIsHoly(compoundTag.getBoolean("isholy"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("type",getType1());
        compoundTag.putBoolean("ishit",isHit());
        compoundTag.putBoolean("isholy",isHoly());
    }

    public float getLen(float p){
        return Mth.lerp(p,oBulletLen,bulletLen);
    }

    private int hitTime;

    @Override
    public void tick() {
        super.tick();
        if(isHit()){
            if(hitTime>=1){
                discard();
                return;
            }
            if(hitTime==0){
                int type2 = getType1();
                if(level().isClientSide){
                    if(type2==0||type2==4) {
                        AdvancedParticleBase.spawnParticle(level(),ParticleHandler.BURST_MESSY.get(),xo,yo,zo,0,0,0,true,0,0,0,0,1,1,1,1,1,0,6,true,false,new ParticleComponent[]{
                                new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, ParticleComponent.KeyTrack.startAndEnd(0f, 10f), false),
                                new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, ParticleComponent.KeyTrack.startAndEnd(0.8f, 0f), false)
                        });
                        AdvancedParticleBase.spawnParticle(level(),ParticleHandler.RING_BIG.get(),xo,yo,zo,0,0,0,true,0,0,0,0,1,1,1,1,1,0,4,true,false,new ParticleComponent[]{
                                new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, ParticleComponent.KeyTrack.startAndEnd(0f, 7f), false),
                                new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, ParticleComponent.KeyTrack.startAndEnd(0.8f, 0f), false)
                        });
                    }
                }
                else {
                    if (getType1() == 1||getType1()==3) {
                        if (getOwner() instanceof LivingEntity caster) {
                            float damage = (float) caster.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
                            List<LivingEntity> nearByLivingEntities = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(5),
                                    entity -> entity.distanceTo(this) < 4);
                            for (LivingEntity hit : nearByLivingEntities) {
                                if (hit == caster) continue;
                                hit.hurt(caster.damageSources().mobAttack(caster), damage);
                            }
                            this.level().explode(caster, this.getX(), this.getY(), this.getZ(), 5, Level.ExplosionInteraction.NONE);
                        }
                    }
                }
                if (getType1() == 2) {
                    doAllShootFX();
                } else if (getType1()==1) {
                    if(getOwner() instanceof GunKnightPatriot gunKnightPatriot){
                        gunKnightPatriot.addDemonArea(100,MathUtils.getFirstBlockAbove(level(),position().add(0,-3,0),5),4);
                    }
                }
            }
            hitTime++;
        }

        this.move(MoverType.SELF, this.getDeltaMovement());

        if(level().isClientSide){
            oBulletLen = bulletLen;
            if(bulletLen<20) bulletLen++;

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
            if(isHoly()){
                Vec3 oldPos = new Vec3(xo,yo,zo);
                Vec3 offset =position().subtract(oldPos);
                int len =(int) (offset.length()) * (getType1()==2?3:1);
                for(int i =0;i<len;i++){
                    float v = random.nextFloat();
                    if(v<0.5f){
                        float scale = ((float) i) / len + v;
                        AdvancedParticleBase.spawnParticle(level(), ParticleHandler.SUN.get(), getX() + offset.x * scale, this.getY() + offset.y * scale, getZ() + offset.z * scale, 0, 0.02, 0, true, 0, 0, 0, 0, (getType1()==2?6:1)*(1 + random.nextFloat()), 1, 0.8, 0, 1, 1, 20, true, false, new ParticleComponent[]{
                                new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, new ParticleComponent.KeyTrack(new float[]{0, 1, 1, 0}, new float[]{0, 0.25f, 0.75f, 1}), false),
                        });
                    }
                }
            }
        }
        int type2 = getType1();
        if(type2==1){
            setDeltaMovement(getDeltaMovement().add(0,- 0.05F,0));
        }
        if (this.tickCount > 40) {
            this.discard();
        } else {
            this.doHurtTarget();
        }

        if(!level().isClientSide()){
            if (getType1() == 0||getType1()==4) {
                BlockState blockState = getBlockStateOn();
                if ( !blockState.isAir() && !isHit()) {
                    setIsHit(true);
                }
            } else {
                BlockState blockState = this.level().getBlockState(this.getOnPos(1.0E-5F));
                BlockState blockState2 = this.level().getBlockState(this.getOnPos(1+1.0E-5F));
                if ((!blockState.isAir()||!blockState2.isAir()) && !isHit()) {
                    setIsHit(true);
                }
            }
        }

        Vec3 vec3 = this.getDeltaMovement();
        double d6 = vec3.y;
        double d4 = vec3.horizontalDistance();

        this.setXRot((float)(Mth.atan2(d6, d4) * (double)(180F / (float)Math.PI)));
    }

    public Vec3 getTrailPosition(int pointer, float partialTick) {
        if (this.isRemoved()) {
            partialTick = 1.0F;
        }
        int i = this.trailPointer - pointer & 15;
        int j = this.trailPointer - pointer - 1 & 15;
        Vec3 d0 = this.trailPositions[j];
        Vec3 d1 = this.trailPositions[i].subtract(d0);
        return d0.add(d1.scale(partialTick));
    }

    public boolean hasTrail() {
        return trailPointer != -1;
    }

    private void doAllShootFX(){
        if(level().isClientSide){
            bombFX();
            Vec3 ground = MathUtils.getFirstBlockAbove(level(),position(),5);
            AdvancedParticleBase.spawnParticle(level(),ParticleHandler.RING_BIG.get(),ground.x,ground.y,ground.z,0,0,0,false,0,1.57,0,0,1,1,1,1,1,0,8,true,false,new ParticleComponent[]{
                    new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, ParticleComponent.KeyTrack.startAndEnd(0f, 160f), false),
                    new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, ParticleComponent.KeyTrack.startAndEnd(0.8f, 0f), false)
            });
        }
        else {
            Vec3 ground = MathUtils.getFirstBlockAbove(level(),position().add(0,-3,0),5);

            FXEntity.SpawnFXEntity(level(),0, ground, (LivingEntity) getOwner());
            EntityCameraShake.cameraShake(this.level(), this.position(), 20F, 1.5F, 5, 15);
        }
    }

    private void doHurtTarget() {
        if(level().isClientSide() ) return;
        if (!isHit()) {
            List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(getDeltaMovement().length()+1));
            boolean flad = false;
            Entity caster = getOwner();
            for (LivingEntity target : entities) {
                if (target == caster||target instanceof GunKnightPatriot) continue;
                boolean flad1 = false;

                Vec3 oldPosition = new Vec3(xo,yo,zo);
                Vec3 position = position();
                Vec3 totarget = target.position().add(0,Mth.clamp(getY()-target.getY(),0,target.getBbHeight()),0).subtract(position);
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
                        float type = 1;
                        if (getType1() == 1||getType1()==3) type = 2.5f;
                        float damage = (float) attribute.getValue();
                        float type4damage = 0;
                        if(getType1()==4){
                            type4damage = target.getBbHeight()*target.getBbHeight()+target.getBbWidth()*target.getBbWidth()/1.5f;
                        }
                        target.hurt(this.damageSources().mobAttack(living), damage * (isHoly()?1f:0.8f) * type + type4damage);
                        flad = true;
                    }
                }

            }
            if(flad){
                playSound(SoundHandle.SHOOT.get(), 1.5F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
                if (getType1() == 1||getType1()==3) {
                    this.level().explode(caster, this.getX(), this.getY(), this.getZ(), 5, Level.ExplosionInteraction.NONE);
                }
                setIsHit(true);
            }
        }
    }

    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        Vec3 vec3 = (new Vec3(x, y, z)).normalize().add(this.random.triangle(0.0D, 0.0172275D * (double) inaccuracy), this.random.triangle(0.0D, 0.0172275D * (double) inaccuracy), this.random.triangle(0.0D, 0.0172275D * (double) inaccuracy)).scale((double) velocity);
        this.setDeltaMovement(vec3);
        double d0 = vec3.horizontalDistance();
        this.setYRot((float) (Mth.atan2(vec3.x, vec3.z) * (double) (180F / (float) Math.PI)));
        this.setXRot((float) (Mth.atan2(vec3.y, d0) * (double) (180F / (float) Math.PI)));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
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

    public boolean isHoly(){
        return this.entityData.get(IS_HOLY);
    }

    public void setIsHoly(boolean isHit){
        this.entityData.set(IS_HOLY,isHit);
    }

    @Override
    public void lerpMotion(double pX, double pY, double pZ) {
        this.setDeltaMovement(pX, pY, pZ);
    }

    private void bombFX(){
        Vec3 move = Vec3.ZERO;
        for(int i=0;i<12;i++){
            for(int j=0;j<16;j++){
                Vec3 vec3 = new Vec3(0, 0, (random.nextFloat()+j/8f)).xRot((float) ((0.1+j/40f+random.nextFloat()*0.1) * org.joml.Math.PI)).yRot((float) (random.nextFloat()*0.5+(i/6f) * org.joml.Math.PI));
                level().addParticle(new ParticleCloud.CloudData(ParticleHandler.CLOUD.get(), (10-j)/10f+1,1,1, (float) (10d + random.nextDouble() * 15d), 120-j*7, ParticleCloud.EnumCloudBehavior.FIRE, 1f), getX(), getY(0.5) , getZ(), vec3.x+move.x*j/10f, vec3.y, vec3.z+move.y*j/10f);
            }
        }
    }
}
