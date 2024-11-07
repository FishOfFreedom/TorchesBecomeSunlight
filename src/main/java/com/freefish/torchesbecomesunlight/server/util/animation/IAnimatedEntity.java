package com.freefish.torchesbecomesunlight.server.util.animation;

public interface IAnimatedEntity {
    AnimationAct NO_ANIMATION = new  AnimationAct("null",0,0);

    int getAnimationTick();

    void setAnimationTick(int tick);

    AnimationAct getAnimation();

    void setAnimation(AnimationAct animation);

    AnimationAct[] getAnimations();
}
