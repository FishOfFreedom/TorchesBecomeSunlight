package com.freefish.torchesbecomesunlight.server.entity.effect;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;

public abstract class EffectEntity extends Entity {
    public LivingEntity caster;
    private static final EntityDataAccessor<Integer> DATA_CASTER_ID = SynchedEntityData.defineId(EffectEntity.class, EntityDataSerializers.INT);

    public EffectEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(DATA_CASTER_ID, -1);
    }

    public void setCasterId(int id) {
        entityData.set(DATA_CASTER_ID, id);
    }

    public int getCasterId() {
        return entityData.get(DATA_CASTER_ID);
    }

    public LivingEntity getCaster(){
        if(caster!=null){
            return caster;
        }
        else {
            if(level().getEntity(getCasterId()) instanceof LivingEntity livingEntity){
                caster = livingEntity;
                return caster;
            }
            else
                return null;
        }
    }

    public void setCaster(LivingEntity living){
        setCasterId(living.getId());
        caster = living;
    }

    @Override
    public void tick() {
        this.baseTick();
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    public List<LivingEntity> getNearByLivingEntities(double range) {
        return this.getNearByEntities(LivingEntity.class, range, range, range, range);
    }

    public List<LivingEntity> getNearByLivingEntities(double rangeX, double height, double rangeZ, double radius) {
        return this.getNearByEntities(LivingEntity.class, rangeX, height, rangeZ, radius);
    }

    public <T extends Entity> List<T> getNearByEntities(Class<T> entityClass, double x, double y, double z, double radius) {
        return level().getEntitiesOfClass(entityClass, getBoundingBox().inflate(x, y, z), targetEntity -> targetEntity != this &&
                distanceTo(targetEntity) <= radius + targetEntity.getBbWidth() / 2f && targetEntity.getY() <= getY() + y);
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

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public void push(Entity entityIn) {
    }

    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}