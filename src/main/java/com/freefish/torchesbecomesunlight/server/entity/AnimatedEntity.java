package com.freefish.torchesbecomesunlight.server.entity;

import com.freefish.torchesbecomesunlight.client.sound.BossMusicPlayer;
import com.freefish.torchesbecomesunlight.server.animation.AnimationAct;
import com.freefish.torchesbecomesunlight.server.animation.AnimationActHandler;
import com.freefish.torchesbecomesunlight.server.animation.IAnimatedEntity;
import com.freefish.torchesbecomesunlight.server.entity.ai.entity.WhileDialogueAI;
import com.freefish.torchesbecomesunlight.server.entity.dialogueentity.DialogueEntity;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster.SnowNova;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public abstract class AnimatedEntity extends PathfinderMob implements IAnimatedEntity, GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int animationTick;
    private AnimationAct animation = NO_ANIMATION;
    private final AnimationController<AnimatedEntity> animationController = new AnimationController<AnimatedEntity>(this, "Controller", 5, this::predicate);
    private static final EntityDataAccessor<Boolean> HAS_DIALOGUE = SynchedEntityData.defineId(AnimatedEntity.class, EntityDataSerializers.BOOLEAN);
    private float playerXo;
    private float playerZo;

    private static final byte MUSIC_PLAY_ID = 67;
    private static final byte MUSIC_STOP_ID = 68;

    private <T extends GeoEntity> PlayState predicate(AnimationState<T> animationState) {
        if(getAnimation() != NO_ANIMATION)
            animationState.setAnimation(getAnimation().getRawAnimation());
        else
            basicAnimation(animationState);
        return PlayState.CONTINUE;
    }

    protected <T extends GeoEntity> void basicAnimation(AnimationState<T> event){

    }

    public AnimatedEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (getAnimation() != NO_ANIMATION) {
            animationTick++;
            if(!level().isClientSide()) {
                getAnimation().tickUpdate(this);
                if(getAnimationTick() >= getAnimation().getDuration())
                    getAnimation().stop(this);
            }
            if (level().isClientSide() && animationTick >= animation.getDuration()) {
                setAnimation(NO_ANIMATION);
            }
        }
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
    public boolean hurt(DamageSource source, float damage) {
        boolean attack = super.hurt(source, damage);
        if (attack) {
            if (getHealth() <= 0.0F) {
                if(this instanceof SnowNova snowNova){
                    if(!snowNova.getIsFirst()) {
                        setHealth(1);
                        snowNova.setIsFirst(true);
                        AnimationActHandler.INSTANCE.sendAnimationMessage(this, SnowNova.LULLABYE_1);
                        return false;
                    }
                }
                AnimationActHandler.INSTANCE.sendAnimationMessage(this, getDeathAnimation());
            }
        }
        return attack;
    }

    public abstract AnimationAct getDeathAnimation();

    //todo
    @Override
    protected void tickDeath() {// Copied from entityLiving
        ++this.deathTime;

        int deathDuration = 20;
        AnimationAct death;
        if ((death = getDeathAnimation()) != null) {
            deathDuration = death.getDuration();
        }
        if (this.deathTime >= deathDuration && !this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte)60);
            this.remove(Entity.RemovalReason.KILLED);
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
    public int getAnimationTick() {
        return this.animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        this.animationTick = tick;
    }

    @Override
    public AnimationAct getAnimation() {
        return this.animation;
    }

    @Override
    public void setAnimation(AnimationAct animation) {
        if(getAnimation()!=null) {
            if(getAnimation().getPriority()<animation.getPriority()&&getAnimationTick()<getAnimation().getDuration()) {
                return;
            }
        }
        this.animation = animation;
        setAnimationTick(0);
    }

    @Override
    public AnimationAct[] getAnimations() {
        return new AnimationAct[0];
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar event) {
        event.add(animationController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
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

    public boolean getHasDialogue(){
        return this.entityData.get(HAS_DIALOGUE);
    }

    public void setHasDialogue(boolean hasDialogue){
        this.entityData.set(HAS_DIALOGUE,hasDialogue);
    }

    public void absFaceEntity(Entity entity){
        this.getLookControl().setLookAt(entity);
        this.yBodyRot = (float)(Mth.atan2(getX()-entity.getX(), entity.getZ()-getZ()) * (double)(180F / (float)Math.PI));
    }

    public void doRangeAttack(double range, double arc,float damage,float knockback,boolean isBreakingShield){
        List<LivingEntity> entitiesHit = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(range, 3, range), e -> e != this && distanceTo(e) <= range + e.getBbWidth() / 2f && e.getY() <= getY() + 3);
        boolean hit = false;
        for (LivingEntity entityHit : entitiesHit) {
            float entityHitAngle = (float) ((Math.atan2(entityHit.getZ() - getZ(), entityHit.getX() - getX()) * (180 / Math.PI) - 90) % 360);
            float entityAttackingAngle = yBodyRot % 360;
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
                Vec3 konck = new Vec3(0,knockback*0.2,knockback).yRot((float)(Mth.atan2(getX()-entityHit.getX(), entityHit.getZ()-getZ()) * (double)(180F / (float)Math.PI)));
                entityHit.setDeltaMovement(entityHit.getDeltaMovement().x + konck.x, entityHit.getDeltaMovement().y+ konck.y, entityHit.getDeltaMovement().z+ konck.z);
                hit = true;
            }
        }
        if (hit) {
            //playSound(MMSounds.ENTITY_WROUGHT_AXE_HIT.get(), 1, 0.5F);
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

    public void setForwardMotion(float speed) {
        this.setDeltaMovement(new Vec3(0, getDeltaMovement().y, speed).yRot((float) (-this.yBodyRot / 180 * Math.PI)));
    }

    public void locateEntity(){
        setDeltaMovement(0,getDeltaMovement().y, 0);
        setPos(xo,getY(),zo);
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
}
