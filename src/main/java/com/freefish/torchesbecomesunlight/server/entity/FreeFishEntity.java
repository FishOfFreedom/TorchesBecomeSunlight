package com.freefish.torchesbecomesunlight.server.entity;

import com.ilexiconn.llibrary.server.animation.Animation;
import com.ilexiconn.llibrary.server.animation.IAnimatedEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;

import java.util.List;
import java.util.stream.Collectors;

public abstract class FreeFishEntity extends PathfinderMob implements IAnimatedEntity, GeoEntity {
    private int animationTick;
    private Animation animation = NO_ANIMATION;
    public int frame;

    public FreeFishEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        super.tick();
        frame++;
        if (getAnimation() != NO_ANIMATION) {
            animationTick++;
            if (level().isClientSide() && animationTick >= animation.getDuration()) {
                setAnimation(NO_ANIMATION);
            }
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        yRotO = getYRot();
        super.readAdditionalSaveData(pCompound);
    }

    protected void onAnimationFinish(Animation animation) {}

    @Override
    public int getAnimationTick() {
        return this.animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        this.animationTick = tick;
    }

    @Override
    public Animation getAnimation() {
        return this.animation;
    }

    @Override
    public void setAnimation(Animation animation) {
        if (animation == NO_ANIMATION) {
            onAnimationFinish(this.animation);
        }
        this.animation = animation;
        setAnimationTick(0);
    }

    public abstract Animation getDeathAnimation();

    public abstract Animation getHurtAnimation();

    @Override
    public Animation[] getAnimations() {
        return new Animation[0];
    }

    public List<Player> getPlayersNearby(double distanceX, double distanceY, double distanceZ, double radius) {
        List<Entity> nearbyEntities = level().getEntities(this, getBoundingBox().deflate(distanceX, distanceY, distanceZ));
        List<Player> listEntityPlayers = nearbyEntities.stream().filter(entityNeighbor -> entityNeighbor instanceof Player && distanceTo(entityNeighbor) <= radius + entityNeighbor.getBbWidth() / 2f).map(entityNeighbor -> (Player) entityNeighbor).collect(Collectors.toList());
        return listEntityPlayers;
    }

    public List<LivingEntity> getAttackableEntityLivingBaseNearby(double distanceX, double distanceY, double distanceZ, double radius) {
        List<Entity> nearbyEntities = level().getEntities(this, getBoundingBox().deflate(distanceX, distanceY, distanceZ));
        List<LivingEntity> listEntityLivingBase = nearbyEntities.stream().filter(entityNeighbor -> entityNeighbor instanceof LivingEntity && ((LivingEntity)entityNeighbor).attackable() && (!(entityNeighbor instanceof Player) || !((Player)entityNeighbor).isCreative()) && distanceTo(entityNeighbor) <= radius + entityNeighbor.getBbWidth() / 2f).map(entityNeighbor -> (LivingEntity) entityNeighbor).collect(Collectors.toList());
        return listEntityLivingBase;
    }

    public  List<LivingEntity> getEntityLivingBaseNearby(double distanceX, double distanceY, double distanceZ, double radius) {
        return getEntitiesNearby(LivingEntity.class, distanceX, distanceY, distanceZ, radius);
    }

    public <T extends Entity> List<T> getEntitiesNearby(Class<T> entityClass, double r) {
        return level().getEntitiesOfClass(entityClass, getBoundingBox().inflate(r, r, r), e -> e != this && distanceTo(e) <= r + e.getBbWidth() / 2f);
    }

    public <T extends Entity> List<T> getEntitiesNearby(Class<T> entityClass, double dX, double dY, double dZ, double r) {
        return level().getEntitiesOfClass(entityClass, getBoundingBox().inflate(dX, dY, dZ), e -> e != this && distanceTo(e) <= r + e.getBbWidth() / 2f && e.getY() <= getY() + dY);
    }


    protected void repelEntities(float x, float y, float z, float radius) {
        List<LivingEntity> nearbyEntities = getEntityLivingBaseNearby(x, y, z, radius);
        for (Entity entity : nearbyEntities) {
            if (entity.canBeCollidedWith() && !entity.noPhysics) {
                double angle = (getAngleBetweenEntities(this, entity) + 90) * Math.PI / 180;
                entity.setDeltaMovement(-0.1 * Math.cos(angle), entity.getDeltaMovement().y, -0.1 * Math.sin(angle));
            }
        }
    }

    public double getAngleBetweenEntities(Entity first, Entity second) {
        return Math.atan2(second.getZ() - first.getZ(), second.getX() - first.getX()) * (180 / Math.PI) + 90;
    }
}
