package com.freefish.torchesbecomesunlight.server.entity;

import com.freefish.torchesbecomesunlight.client.sound.BossMusicPlayer;
import com.freefish.torchesbecomesunlight.server.entity.ai.entity.WhileDialogueAI;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.GuerrillasEntity;
import com.freefish.torchesbecomesunlight.server.util.AnimationWalk;
import com.freefish.torchesbecomesunlight.server.util.bossbar.CustomBossInfoServer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public abstract class FreeFishEntity extends PathfinderMob {
    private static final EntityDataAccessor<Boolean> HAS_DIALOGUE = SynchedEntityData.defineId(FreeFishEntity.class, EntityDataSerializers.BOOLEAN);
    private float playerXo;
    private float playerZo;

    private static final byte MUSIC_PLAY_ID = 67;
    private static final byte MUSIC_STOP_ID = 68;

    private final CustomBossInfoServer bossInfo= new CustomBossInfoServer(this);

    @OnlyIn(Dist.CLIENT)
    public int time = -1;

    public FreeFishEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCount % 4 == 0) bossInfo.update();
        if(!level().isClientSide){
            LivingEntity target = getTarget();
            if(target instanceof Player player){
                playerXo = (float) player.getX();
                playerZo = (float) player.getZ();
            }
        }
        if (!level().isClientSide && getBossMusic() != null) {
            if (canPlayMusic()) {
                this.level().broadcastEntityEvent(this, MUSIC_PLAY_ID);
            }
            else {
                this.level().broadcastEntityEvent(this, MUSIC_STOP_ID);
            }
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1,new WhileDialogueAI(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HAS_DIALOGUE,false);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        yRotO = getYRot();
        super.readAdditionalSaveData(pCompound);
        setHasDialogue(pCompound.getBoolean("has_dialogue"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("has_dialogue",getHasDialogue());
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == MUSIC_PLAY_ID) {
            BossMusicPlayer.playBossMusic(this);
        }
        else if (id == MUSIC_STOP_ID) {
            BossMusicPlayer.stopBossMusic(this);
        }
        else super.handleEntityEvent(id);
    }

    @Override
    public void die(DamageSource pDamageSource) {
        super.die(pDamageSource);
        if (!this.isRemoved()) {
            bossInfo.update();
        }
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
        }
    }

    @Override
    public void setCustomName(Component name) {
        super.setCustomName(name);
        this.bossInfo.setName(this.getDisplayName());
    }
    public boolean hasBossBar() {
        return false;
    }

    public void doRangeTrueAttack(double range, double arc,float damage,boolean isBreakingShield){
        List<LivingEntity> entitiesHit = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(range, 3, range), e -> e != this && distanceTo(e) <= range + e.getBbWidth() / 2f && e.getY() <= getY() + 3);
        for (LivingEntity entityHit : entitiesHit) {
            float entityHitAngle = (float) ((Math.atan2(entityHit.getZ() - getZ(), entityHit.getX() - getX()) * (180 / Math.PI) - 90) % 360);
            float entityAttackingAngle = getYRot() % 360;
            if (entityHitAngle < 0) {
                entityHitAngle += 360;
            }
            if (entityAttackingAngle < 0) {
                entityAttackingAngle += 360;
            }
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            float entityHitDistance = (float) Math.sqrt((entityHit.getZ() - getZ()) * (entityHit.getZ() - getZ()) + (entityHit.getX() - getX()) * (entityHit.getX() - getX())) - entityHit.getBbWidth() / 2f;
            if (entityHitDistance <= range && (entityRelativeAngle <= arc / 2 && entityRelativeAngle >= -arc / 2) || (entityRelativeAngle >= 360 - arc / 2 || entityRelativeAngle <= -360 + arc / 2)) {
                if(!(entityHit instanceof Player player&& player.isCreative())) {
                    entityHit.actuallyHurt(damageSources().mobAttack(this), damage);
                    entityHit.hurt(damageSources().mobAttack(this), 1);
                }
            }
        }
    }

    public BossEvent.BossBarColor bossBarColor() {
        return BossEvent.BossBarColor.PURPLE;
    }

    public boolean getHasDialogue(){
        return this.entityData.get(HAS_DIALOGUE);
    }

    public void setHasDialogue(boolean hasDialogue){
        this.entityData.set(HAS_DIALOGUE,hasDialogue);
    }

    public void absFaceEntity(Entity entity){
        this.getLookControl().setLookAt(entity);
        setYRot((float)(Mth.atan2(getX()-entity.getX(), entity.getZ()-getZ()) * (double)(180F / (float)Math.PI)));
    }

    public void doRangeAttack(double range, double arc,float damage,float knockback,boolean isBreakingShield){
        List<LivingEntity> entitiesHit = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(range, 3, range), e -> e != this && distanceTo(e) <= range + e.getBbWidth() / 2f && e.getY() <= getY() + 3);
        for (LivingEntity entityHit : entitiesHit) {
            float entityHitAngle = (float) ((Math.atan2(entityHit.getZ() - getZ(), entityHit.getX() - getX()) * (180 / Math.PI) - 90) % 360);
            float entityAttackingAngle = getYRot() % 360;
            if (entityHitAngle < 0) {
                entityHitAngle += 360;
            }
            if (entityAttackingAngle < 0) {
                entityAttackingAngle += 360;
            }
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            float entityHitDistance = (float) Math.sqrt((entityHit.getZ() - getZ()) * (entityHit.getZ() - getZ()) + (entityHit.getX() - getX()) * (entityHit.getX() - getX())) - entityHit.getBbWidth() / 2f;
            if (entityHitDistance <= range && (entityRelativeAngle <= arc / 2 && entityRelativeAngle >= -arc / 2) || (entityRelativeAngle >= 360 - arc / 2 || entityRelativeAngle <= -360 + arc / 2)) {
                entityHit.hurt(damageSources().mobAttack(this),damage);
                if(isBreakingShield&&entityHit instanceof Player player){
                    ItemStack pPlayerItemStack = player.getUseItem();
                    if (!pPlayerItemStack.isEmpty() && pPlayerItemStack.is(Items.SHIELD)) {
                        player.getCooldowns().addCooldown(Items.SHIELD, 100);
                        this.level().broadcastEntityEvent(player, (byte)30);
                    }
                }
                Vec3 direction = new Vec3(0, knockback*0.1, knockback).yRot((float) ((-getYRot()) / 180 * org.joml.Math.PI));
                entityHit.setDeltaMovement(entityHit.getDeltaMovement().x + direction.x, entityHit.getDeltaMovement().y+ direction.y, entityHit.getDeltaMovement().z + direction.z);
            }
        }
    }

    public void doCycleAttack(float range,float damage){
        List<LivingEntity> list = level().getEntitiesOfClass(LivingEntity.class,getBoundingBox().inflate(range+1),livingEntity ->
                !(livingEntity instanceof GuerrillasEntity)&&livingEntity.distanceTo(this)<range+livingEntity.getBbWidth()/2);
        for(LivingEntity entityHit:list) {
            if(entityHit == this) return;
            entityHit.hurt(this.damageSources().mobAttack(this), damage);
            if (entityHit instanceof Player player) {
                ItemStack pPlayerItemStack = player.getUseItem();
                if (!pPlayerItemStack.isEmpty() && pPlayerItemStack.is(Items.SHIELD)) {
                    player.getCooldowns().addCooldown(Items.SHIELD, 100);
                    level().broadcastEntityEvent(player, (byte) 30);
                }
            }
        }
    }

    public boolean doRangeAttack(double range, double arc,float damage,boolean isBreakingShield){
        boolean flag = false;
        List<LivingEntity> entitiesHit = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(range, 3, range), e -> e != this && distanceTo(e) <= range + e.getBbWidth() / 2f && e.getY() <= getY() + 3);
        for (LivingEntity entityHit : entitiesHit) {
            float entityHitAngle = (float) ((Math.atan2(entityHit.getZ() - getZ(), entityHit.getX() - getX()) * (180 / Math.PI) - 90) % 360);
            float entityAttackingAngle = getYRot() % 360;
            if (entityHitAngle < 0) {
                entityHitAngle += 360;
            }
            if (entityAttackingAngle < 0) {
                entityAttackingAngle += 360;
            }
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            float entityHitDistance = (float) Math.sqrt((entityHit.getZ() - getZ()) * (entityHit.getZ() - getZ()) + (entityHit.getX() - getX()) * (entityHit.getX() - getX())) - entityHit.getBbWidth() / 2f;
            if (entityHitDistance <= range && (entityRelativeAngle <= arc / 2 && entityRelativeAngle >= -arc / 2) || (entityRelativeAngle >= 360 - arc / 2 || entityRelativeAngle <= -360 + arc / 2)) {
            flag = entityHit.hurt(damageSources().mobAttack(this),damage);
                if(isBreakingShield&&entityHit instanceof Player player){
                    ItemStack pPlayerItemStack = player.getUseItem();
                    if (!pPlayerItemStack.isEmpty() && pPlayerItemStack.is(Items.SHIELD)) {
                        player.getCooldowns().addCooldown(Items.SHIELD, 100);
                        this.level().broadcastEntityEvent(player, (byte)30);
                    }
                }
            }
        }
        return  flag;
    }

    public void lookAtEntity(Entity livingEntity){
        getLookControl().setLookAt(livingEntity,30f,30f);
        lookAt(livingEntity,30f,30f);
    }

    public void dashForward(float speed,float yawOffset){
        Vec3 direction = new Vec3(0, speed*0.1, speed).yRot((float) ((yawOffset-getYRot()) / 180 * org.joml.Math.PI));
        setDeltaMovement(direction);
    }

    public void doRangeAttack(double range, double arc, float damage, boolean isBreakingShield, MobEffectInstance effectInstance){
        List<LivingEntity> entitiesHit = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(range, 3, range), e -> e != this && distanceTo(e) <= range + e.getBbWidth() / 2f && e.getY() <= getY() + 3);
        for (LivingEntity entityHit : entitiesHit) {
            float entityHitAngle = (float) ((Math.atan2(entityHit.getZ() - getZ(), entityHit.getX() - getX()) * (180 / Math.PI) - 90) % 360);
            float entityAttackingAngle = getYRot() % 360;
            if (entityHitAngle < 0) {
                entityHitAngle += 360;
            }
            if (entityAttackingAngle < 0) {
                entityAttackingAngle += 360;
            }
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            float entityHitDistance = (float) Math.sqrt((entityHit.getZ() - getZ()) * (entityHit.getZ() - getZ()) + (entityHit.getX() - getX()) * (entityHit.getX() - getX())) - entityHit.getBbWidth() / 2f;
            if (entityHitDistance <= range && (entityRelativeAngle <= arc / 2 && entityRelativeAngle >= -arc / 2) || (entityRelativeAngle >= 360 - arc / 2 || entityRelativeAngle <= -360 + arc / 2)) {
                entityHit.hurt(damageSources().mobAttack(this),damage);
                entityHit.addEffect(effectInstance);
                if(isBreakingShield&&entityHit instanceof Player player){
                    ItemStack pPlayerItemStack = player.getUseItem();
                    if (!pPlayerItemStack.isEmpty() && pPlayerItemStack.is(Items.SHIELD)) {
                        player.getCooldowns().addCooldown(Items.SHIELD, 100);
                        this.level().broadcastEntityEvent(player, (byte)30);
                    }
                }
            }
        }
    }

    public float getTargetMove(LivingEntity target){
        float f1,f2;
        if(target instanceof Player) {
            f1 = (float) target.getX() - playerXo;
            f2 = (float) target.getZ() - playerZo;
        }else {
            f1 = (float) (target.getX() - target.xo);
            f2 = (float) (target.getZ() - target.zo);
        }
        return (float) Math.sqrt(f1*f1+f2*f2) * 20;
    }

    public Vec3 getTargetMoveVec(LivingEntity target){
        float f1,f2;
        if(target instanceof Player) {
            f1 = (float) target.getX() - playerXo;
            f2 = (float) target.getZ() - playerZo;
        }else {
            f1 = (float) (target.getX() - target.xo);
            f2 = (float) (target.getZ() - target.zo);
        }
        return new Vec3(f1,0,f2).multiply(20,0,20);
    }
//todo
    public void setForwardMotion(float speed) {
        this.setDeltaMovement(new Vec3(0, getDeltaMovement().y, speed).yRot((float) (-this.getYRot() / 180 * Math.PI)));
    }

    public void locateEntity(){
        this.setDeltaMovement(0, this.onGround() ? 0 : this.getDeltaMovement().y, 0);
        this.setPos(xo, this.onGround() ? yo : getY(), zo);
    }

    public SoundEvent getBossMusic() {
        return null;
    }

    protected boolean canPlayMusic() {
        return !isSilent() && getTarget() instanceof Player;
    }

    public boolean canPlayerHearMusic(Player player) {
        return player != null
                && canAttack(player)
                && distanceTo(player) < 2500;
    }

    public double getAngleBetweenEntities(Entity first, Entity second) {
        return Math.atan2(second.getZ() - first.getZ(), second.getX() - first.getX()) * (180 / Math.PI) + 90;
    }

    protected void repelEntities(float x, float y, float z, float radius) {
        List<LivingEntity> nearbyEntities = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(x, y, z), e -> e != this && distanceTo(e) <= radius + e.getBbWidth() / 2f && e.getY() <= getY() + y);
        for (Entity entity : nearbyEntities) {
            if (entity.isPickable() && !entity.noPhysics) {
                double angle = (getAngleBetweenEntities(this, entity) + 90) * Math.PI / 180;
                entity.setDeltaMovement(-0.1 * Math.cos(angle), entity.getDeltaMovement().y, -0.1 * Math.sin(angle));
            }
        }
    }
//todo
    protected boolean canBePushedByEntity(Entity entity) {
        return true;
    }

    @Override
    public void push(Entity entityIn) {
        if (!this.isSleeping()) {
            if (!this.isPassengerOfSameVehicle(entityIn)) {
                if (!entityIn.noPhysics && !this.noPhysics) {
                    double d0 = entityIn.getX() - this.getX();
                    double d1 = entityIn.getZ() - this.getZ();
                    double d2 = Mth.absMax(d0, d1);
                    if (d2 >= (double) 0.01F) {
                        d2 = Math.sqrt(d2);
                        d0 = d0 / d2;
                        d1 = d1 / d2;
                        double d3 = 1.0D / d2;
                        if (d3 > 1.0D) {
                            d3 = 1.0D;
                        }

                        d0 = d0 * d3;
                        d1 = d1 * d3;
                        d0 = d0 * (double) 0.05F;
                        d1 = d1 * (double) 0.05F;
                        if (!this.isVehicle()) {
                            if (canBePushedByEntity(entityIn)) {
                                this.push(-d0, 0.0D, -d1);
                            }
                        }
                        if (!entityIn.isVehicle()) {
                            entityIn.push(d0, 0.0D, d1);
                        }
                    }
                }
            }
        }
    }
}
