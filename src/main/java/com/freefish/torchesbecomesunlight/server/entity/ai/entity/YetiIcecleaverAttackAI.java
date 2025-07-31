package com.freefish.torchesbecomesunlight.server.entity.ai.entity;

import com.freefish.torchesbecomesunlight.server.effect.forceeffect.ForceEffectHandle;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster.YetiIcecleaver;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

import static com.freefish.torchesbecomesunlight.server.util.animation.IAnimatedEntity.NO_ANIMATION;

public class YetiIcecleaverAttackAI extends Goal {
    private final YetiIcecleaver shieldGuard;

    private int repath;
    private double targetX;
    private double targetY;
    private double targetZ;
    private int timeSinceRun;
    private int timeSinceRemote;
    private int skillWill;

    public YetiIcecleaverAttackAI(YetiIcecleaver shieldGuard) {
        this.shieldGuard = shieldGuard;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.shieldGuard.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public void start() {
        if(!shieldGuard.isAggressive()){
            AnimationActHandler.INSTANCE.sendAnimationMessage(shieldGuard,YetiIcecleaver.IDLE_TO_ACT);
        }
        shieldGuard.setAggressive(true);
        this.repath = 0;
    }

    @Override
    public void tick() {
        LivingEntity target = this.shieldGuard.getTarget();
        if (target == null) return;
        RandomSource random = shieldGuard.getRandom();

        timeSinceRun++;
        timeSinceRemote++;

        boolean b = ForceEffectHandle.hasForceEffect(target, ForceEffectHandle.FROZEN_FORCE_EFFECT);
        if(b){
            skillWill++;
        }

        if(!(this.shieldGuard.getAnimation() == NO_ANIMATION||shieldGuard.getAnimation()== YetiIcecleaver.RUN)) return;
        walk();
        if(shieldGuard.getAnimation()==YetiIcecleaver.RUN) return;

        double dist = this.shieldGuard.distanceTo(target);
        if(dist >= 10 &&timeSinceRun>=180) {
            targetX = targetY = targetZ = 0;
            timeSinceRun = 0;
            AnimationActHandler.INSTANCE.sendAnimationMessage(shieldGuard, YetiIcecleaver.RUN);
        }else if(dist < 4 &&timeSinceRemote>=180) {
            AnimationActHandler.INSTANCE.sendAnimationMessage(shieldGuard,YetiIcecleaver.REMOTE_ICE);
            timeSinceRemote = 0;
        } else if (target.getY() - this.shieldGuard.getY() >= -1 && target.getY() - this.shieldGuard.getY() <= 3) {
            if (dist < 5D && Math.abs(MathUtils.wrapDegrees(this.shieldGuard.getAngleBetweenEntities(target, this.shieldGuard) - this.shieldGuard.yBodyRot)) < 35.0D) {
                float v = random.nextFloat();
                if(v<0.33||skillWill>20){
                    AnimationActHandler.INSTANCE.sendAnimationMessage(this.shieldGuard, YetiIcecleaver.SKILL);
                    skillWill=0;
                }
                else if(v<0.66) {
                    AnimationActHandler.INSTANCE.sendAnimationMessage(this.shieldGuard, YetiIcecleaver.ATTACK2);
                }
                else {
                    if(random.nextBoolean())
                        AnimationActHandler.INSTANCE.sendAnimationMessage(this.shieldGuard, YetiIcecleaver.HEAVY_ATTACK);
                    else
                        AnimationActHandler.INSTANCE.sendAnimationMessage(this.shieldGuard, YetiIcecleaver.HEAVY_ATTACK2);
                }
            }
        }
    }

    private void walk(){
        LivingEntity target = shieldGuard.getTarget();
        if(target!=null) {
            double dist = this.shieldGuard.distanceToSqr(this.targetX, this.targetY, this.targetZ);
            if (--this.repath <= 0 && (
                    this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D ||
                            target.distanceToSqr(this.targetX, this.targetY, this.targetZ) >= 1.0D) ||
                    this.shieldGuard.getNavigation().isDone()
            ) {
                this.targetX = target.getX();
                this.targetY = target.getY();
                this.targetZ = target.getZ();
                this.repath = 4 + this.shieldGuard.getRandom().nextInt(7);
                if (dist > 1024D) {
                    this.repath += 10;
                } else if (dist > 256D) {
                    this.repath += 5;
                }
                if (!moveMode(target)) {
                    this.repath += 15;
                }
            }
        }
    }

    private boolean moveMode(LivingEntity target){
        if(shieldGuard.getAnimation()==YetiIcecleaver.RUN)
            return this.shieldGuard.getNavigation().moveTo(target, 0.8);
        else
            return this.shieldGuard.getNavigation().moveTo(target, 0.4);
    }
}
