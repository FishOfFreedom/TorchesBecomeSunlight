package com.freefish.torchesbecomesunlight.server.entity.projectile;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.util.particle.ParticleCloud;
import com.freefish.torchesbecomesunlight.client.util.particle.util.AdvancedParticleBase;
import com.freefish.torchesbecomesunlight.client.util.particle.util.ParticleComponent;
import com.freefish.torchesbecomesunlight.server.config.ConfigHandler;
import com.freefish.torchesbecomesunlight.server.entity.dlc.GunKnightPatriot;
import com.freefish.torchesbecomesunlight.server.entity.dlc.Turret;
import com.freefish.torchesbecomesunlight.server.entity.effect.EntityCameraShake;
import com.freefish.torchesbecomesunlight.server.entity.effect.FXEntity;
import com.freefish.torchesbecomesunlight.server.event.packet.toclient.ProjectileHitEntityMessage;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.init.ParticleHandler;
import com.freefish.torchesbecomesunlight.server.init.SoundHandle;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;

public class Bullet extends NoGravityProjectileEntity {
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
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        setType(compoundTag.getInt("type"));
        setIsHoly(compoundTag.getBoolean("isholy"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("type",getType1());
        compoundTag.putBoolean("isholy",isHoly());
    }

    public float getLen(float p){
        return Mth.lerp(p,oBulletLen,bulletLen);
    }

    @Override
    public void tick() {
        super.tick();

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
    }

    @Override
    public Vec3 changeDeltaMovement(Vec3 vec3) {
        int type2 = getType1();
        if(type2==1){
            vec3 = vec3.add(0,- 0.05F,0);
            setDeltaMovement(vec3);
        }
        return super.changeDeltaMovement(vec3);
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

    public int len111 = 10;

    private void doAllShootFX(Vec3 blockPos){
        if(level().isClientSide){
            bombFX();
            AdvancedParticleBase.spawnParticle(level(),ParticleHandler.RING_BIG.get(),blockPos.x,blockPos.y,blockPos.z,0,0,0,false,0,1.57,0,0,1,1,1,1,1,0,8,true,false,new ParticleComponent[]{
                    new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, ParticleComponent.KeyTrack.startAndEnd(0f, 160f), false),
                    new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, ParticleComponent.KeyTrack.startAndEnd(0.8f, 0f), false)
            });
        }
        else {
            FXEntity.SpawnFXEntity(level(),0,len111, blockPos, (LivingEntity) getOwner());
            EntityCameraShake.cameraShake(this.level(), blockPos, 20F, 1.5F, 5, 15);
        }
    }

    @Override
    public int getTickDespawn() {
        return 200;
    }

    @Override
    public boolean isNoPhysics() {
        return true;
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        Entity entity = pResult.getEntity();

        boolean b = hitEntity(entity);
        if(!level().isClientSide&&b){
            TorchesBecomeSunlight.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this), new ProjectileHitEntityMessage(this, entity.getId()));
        }

        if(isHitEntityDiscard(entity)) {
            this.discard();
        }
    }

    @Override
    public boolean hitEntity(Entity entity) {
        int type2 = getType1();
        if(level().isClientSide){
            if(type2==0||type2==4) {
                AdvancedParticleBase.spawnParticle(level(),ParticleHandler.BURST_MESSY.get(),getX(),getY(),getZ(),0,0,0,true,0,0,0,0,1,1,1,1,1,0,6,true,false,new ParticleComponent[]{
                        new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, ParticleComponent.KeyTrack.startAndEnd(0f, 10f), false),
                        new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, ParticleComponent.KeyTrack.startAndEnd(0.8f, 0f), false)
                });
                AdvancedParticleBase.spawnParticle(level(),ParticleHandler.RING_BIG.get(),getX(),getY(),getZ(),0,0,0,true,0,0,0,0,1,1,1,1,1,0,4,true,false,new ParticleComponent[]{
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
                            entity1 -> entity1.distanceTo(this) < 4);
                    for (LivingEntity hit : nearByLivingEntities) {
                        if (hit == caster) continue;
                        hit.hurt(caster.damageSources().mobAttack(caster), damage);
                    }
                    this.level().explode(caster, this.getX(), this.getY(), this.getZ(), 3, Level.ExplosionInteraction.NONE);
                }
            }
        }
        if(getType1()==2){
            doAllShootFX(entity.position());
        }

        if(!level().isClientSide){
            if(entity instanceof LivingEntity target){
                boolean flad = false;
                Entity caster = getOwner();

                if (target == caster) return false;
                if (caster instanceof Turret && entity instanceof GunKnightPatriot) return false;

                if (caster instanceof Player living) {
                    float damage = (float) ConfigHandler.COMMON.TOOLs.SACRED_GUN.attackDamage.get().doubleValue();
                    target.hurt(this.damageSources().playerAttack(living), damage);
                    target.invulnerableTime = 2;
                    flad = true;
                }else if (caster instanceof LivingEntity living) {
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

                if(flad){
                    playSound(SoundHandle.SHOOT.get(), 1.5F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
                    if (getType1() == 1||getType1()==3) {
                        this.level().explode(caster, this.getX(), this.getY(), this.getZ(), 3, Level.ExplosionInteraction.NONE);
                    }
                }
            }
            return true;
        }
        return false;
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
    protected void onHitBlock(BlockHitResult pResult) {
        BlockPos blockPos = pResult.getBlockPos();
        if (getType1() == 2) {
            doAllShootFX(blockPos.getCenter());
        } else if (getType1()==1) {
            if(getOwner() instanceof GunKnightPatriot gunKnightPatriot){
                gunKnightPatriot.addDemonArea(100,blockPos.getCenter(),4);
            }
        }
        if (getType1() == 1||getType1()==3) {
            if (getOwner() instanceof LivingEntity caster) {
                float damage = (float) caster.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
                List<LivingEntity> nearByLivingEntities = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(5),
                        entity1 -> entity1.distanceTo(this) < 4);
                for (LivingEntity hit : nearByLivingEntities) {
                    if (hit == caster) continue;
                    hit.hurt(caster.damageSources().mobAttack(caster), damage);
                }
                this.level().explode(caster, this.getX(), this.getY(), this.getZ(), 3, Level.ExplosionInteraction.NONE);
            }
        }
        super.onHitBlock(pResult);
    }

    @Override
    public boolean isHitBlockDiscard() {
        return true;
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
    }

    public int getType1(){
        return this.entityData.get(TYPE);
    }

    public boolean isHoly(){
        return this.entityData.get(IS_HOLY);
    }

    public void setIsHoly(boolean isHit){
        this.entityData.set(IS_HOLY,isHit);
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
