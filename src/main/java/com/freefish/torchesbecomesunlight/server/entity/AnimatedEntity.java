package com.freefish.torchesbecomesunlight.server.entity;

import com.freefish.torchesbecomesunlight.server.util.animation.AnimationAct;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import com.freefish.torchesbecomesunlight.server.util.animation.IAnimatedEntity;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster.SnowNova;
import com.freefish.torchesbecomesunlight.server.util.AnimationWalk;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class AnimatedEntity extends FreeFishEntity implements IAnimatedEntity, GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int animationTick;
    private AnimationWalk animationWalk;
    private AnimationAct animation = NO_ANIMATION;
    private final AnimationController<AnimatedEntity> animationController = new AnimationController<AnimatedEntity>(this, "Controller", 5, this::predicate);

    protected <T extends GeoEntity> PlayState predicate(AnimationState<T> animationState) {
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
                if(animationWalk!=null){
                    float t = animationWalk.tickWalk(animationTick);
                    if(t != 0&&(getTarget() == null ||  (getTarget()!=null&&getTarget().distanceTo(this) > 1.8F+getTarget().getBbWidth()/2)))
                        move(MoverType.SELF, new Vec3(0, 0, t).yRot((float) (-this.yBodyRot / 180 * Math.PI)));
                }
                getAnimation().tickUpdate(this);
                if(getAnimationTick() >= getAnimation().getDuration())
                    getAnimation().stop(this);
            }
            if (level().isClientSide() && animationTick >= animation.getDuration()) {
                setAnimation(NO_ANIMATION);
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        boolean attack = super.hurt(source, damage);
        if (attack) {
            if (getHealth() <= 0.0F) {
                //todo
                if(this instanceof SnowNova snowNova){
                    if(!snowNova.getIsFirst()) {
                        setHealth(1);
                        snowNova.setIsFirst(true);
                        AnimationActHandler.INSTANCE.sendAnimationMessage(this, SnowNova.LULLABYE_1);
                        return false;
                    }
                }
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
        if(animationWalk != null) animationWalk=null;
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

    public void setAnimationWalk(AnimationWalk animationWalk) {
        this.animationWalk = animationWalk;
    }
}
