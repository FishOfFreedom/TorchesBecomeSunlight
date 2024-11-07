package com.freefish.torchesbecomesunlight.server.util.animation;

import com.freefish.torchesbecomesunlight.server.entity.AnimatedEntity;
import software.bernie.geckolib.core.animation.RawAnimation;

public class  AnimationAct<T extends AnimatedEntity>{
    public RawAnimation getRawAnimation() {
        return rawAnimation;
    }

    public int getDuration() {
        return duration;
    }

    public int getPriority(){
        return priority;
    }

    private final RawAnimation rawAnimation;
    private final int duration;
    private final int priority;

    public AnimationAct(RawAnimation rawAnimation, int duration,int priority) {
        this.rawAnimation = rawAnimation;
        this.duration = duration;
        this.priority = priority;
    }

    public AnimationAct(String rawAnimation, int duration) {
        this(RawAnimation.begin().thenLoop(rawAnimation),duration,3);
    }

    public AnimationAct(String rawAnimation, int duration,int priority) {
        this(RawAnimation.begin().thenLoop(rawAnimation),duration,priority);
    }

    public void tickUpdate(T entity){
    }

    public void start(T entity){
    }

    public void stop(T entity){
        AnimationActHandler.INSTANCE.sendAnimationMessage(entity,IAnimatedEntity.NO_ANIMATION);
    }

    public static AnimationAct create(RawAnimation rawAnimation, int duration) {
        return new AnimationAct(rawAnimation,duration,3);
    }

    public static AnimationAct create(String rawAnimation, int duration) {
        return new AnimationAct(RawAnimation.begin().thenLoop(rawAnimation),duration,3);
    }
}
