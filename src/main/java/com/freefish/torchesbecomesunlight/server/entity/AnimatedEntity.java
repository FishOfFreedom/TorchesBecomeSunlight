package com.freefish.torchesbecomesunlight.server.entity;

import com.freefish.torchesbecomesunlight.server.entity.effect.dialogueentity.IAwardEntity;
import com.freefish.torchesbecomesunlight.server.entity.ursus.UrsusEntity;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationAct;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import com.freefish.torchesbecomesunlight.server.util.animation.IAnimatedEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public abstract class AnimatedEntity extends FreeFishEntity implements IAnimatedEntity, GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private float playerXo;
    private float playerZo;
    private float stepKey;
    private float stepKeyTime;
    private Vec3 playerPosO;
    private int animationTick;
    private AnimationAct animation = NO_ANIMATION;
    private final AnimationController<AnimatedEntity> animationController = new AnimationController<AnimatedEntity>(this, "Controller", 5, this::predicate);

    protected  <T extends GeoEntity> PlayState predicate(AnimationState<T> animationState) {
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

                LivingEntity target = getTarget();
                if(target instanceof Player player){

                    float f1 = (float) target.getX() - playerXo;
                    float f3 = (float) target.getZ() - playerZo;

                    this.playerPosO = new Vec3(f1,0,f3).scale(20);

                    playerXo = (float) player.getX();
                    playerZo = (float) player.getZ();
                }
            }
            if (level().isClientSide() && animationTick >= animation.getDuration()) {
                setAnimation(NO_ANIMATION);
            }
        }

        if(level().isClientSide){
            if (stepKeyTime > 0) {
                stepKeyTime--;
            }
        }
    }

    public void bomb1(float r,float damage) {
        List<LivingEntity> list = level().getEntitiesOfClass(LivingEntity.class,getBoundingBox().inflate(r+2), livingEntity ->
                 livingEntity!=this&&livingEntity.distanceTo(this)<r+livingEntity.getBbWidth()/2);
        for(LivingEntity entityHit:list) {
            if(entityHit instanceof Player player&&player.position().subtract(position()).length()>(r/2)){
                Vec3 targetMoveVec = getTargetMoveVec(player);
                if(targetMoveVec.dot(position().subtract(player.position()))<0){
                    continue;
                }
            }

            doHurtEntity(entityHit,damageSources().mobAttack(this),damage);
            if (entityHit instanceof Player player) {
                ItemStack pPlayerItemStack = player.getUseItem();
                if (!pPlayerItemStack.isEmpty() && pPlayerItemStack.is(Items.SHIELD)) {
                    player.getCooldowns().addCooldown(Items.SHIELD, 100);
                    player.stopUsingItem();
                    level().broadcastEntityEvent(player, (byte) 30);
                }
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        boolean attack = super.hurt(source, damage);
        if (attack) {
            if (getHealth() <= 0.0F) {
                if(getDeathAnimation()!=null)
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
            this.remove(RemovalReason.KILLED);
        }
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
        animation.start(this);
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

    @OnlyIn(Dist.CLIENT)
    public void setClientVectors(int index, Vec3 pos) {
    }

//todo FrostNova is Influente
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
        float f1,f3;
        if(target instanceof Player&&playerPosO!=null) {
            return playerPosO;
        }else {
            f1 = (float) (target.getX() - target.xo);
            f3 = (float) (target.getZ() - target.zo);
            return new Vec3(f1,0,f3).scale(20);
        }
    }
}
