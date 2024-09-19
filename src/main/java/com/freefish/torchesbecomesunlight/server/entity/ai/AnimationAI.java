package com.freefish.torchesbecomesunlight.server.entity.ai;

import com.freefish.torchesbecomesunlight.server.entity.FreeFishEntity;
import com.ilexiconn.llibrary.server.animation.Animation;
import com.ilexiconn.llibrary.server.animation.AnimationHandler;
import com.ilexiconn.llibrary.server.animation.IAnimatedEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import software.bernie.geckolib.animatable.GeoEntity;

public abstract class AnimationAI<T extends FreeFishEntity & IAnimatedEntity & GeoEntity> extends Goal {
    protected final T entity;

    protected AnimationAI(T entity) {
        this.entity = entity;
    }

    @Override
    public boolean canUse() {
        return this.test(this.entity.getAnimation());
    }

    @Override
    public void start() {
        //this.entity.hurtInterruptsAnimation = this.hurtInterruptsAnimation;
    }

    @Override
    public boolean canContinueToUse() {
        return this.test(this.entity.getAnimation()) && this.entity.getAnimationTick() < this.entity.getAnimation().getDuration();
    }

    @Override
    public void stop() {
        if (this.test(this.entity.getAnimation())) {
            AnimationHandler.INSTANCE.sendAnimationMessage(this.entity, IAnimatedEntity.NO_ANIMATION);
        }
    }

    protected abstract boolean test(Animation animation);
}
