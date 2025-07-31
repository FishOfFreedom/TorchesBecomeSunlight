package com.freefish.torchesbecomesunlight.server.entity.ai.entity;

import com.freefish.torchesbecomesunlight.server.entity.animal.Mangler;
import com.freefish.torchesbecomesunlight.server.entity.ursus.Pursuer;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationAct;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

import static com.freefish.torchesbecomesunlight.server.util.animation.IAnimatedEntity.NO_ANIMATION;

public class ManglerHaQiAttackAI extends Goal {
    private Mangler mangler;

    private int repath;
    private double targetX;
    private double targetY;
    private double targetZ;

    public ManglerHaQiAttackAI(Mangler mangler){
        this.mangler = mangler;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
    }
    @Override
    public boolean canUse() {
        LivingEntity target = this.mangler.getTarget();
        return target != null && target.isAlive()&&mangler.isAngryLevel()<=1;
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse()&&mangler.isAttacked;
    }

    @Override
    public void start() {
        this.repath = 0;
        mangler.isAttacked  = true;
        mangler.setAngryLevel(1);
    }

    @Override
    public void stop() {
        this.mangler.getNavigation().stop();
    }

    @Override
    public void tick() {
        LivingEntity target = this.mangler.getTarget();
        if (target == null) return;
        RandomSource random = mangler.getRandom();
        AnimationAct animationAct = mangler.getAnimation();

        if(animationAct == Pursuer.BLACKHOLE) resetPath();

        if(!(animationAct == NO_ANIMATION)) {
            stop();
            return;
        }

        walk();
        double dist = this.mangler.distanceTo(target);
        if(dist>10) mangler.setRun(true);
        else if (dist<6) mangler.setRun(false);

        if (target.getY() - this.mangler.getY() >= -1 && target.getY() - this.mangler.getY() <= 3) {
            if (dist < 4&& Math.abs(MathUtils.wrapDegrees(this.mangler.getAngleBetweenEntities(target, this.mangler) - this.mangler.yBodyRot)) < 35.0D) {
                AnimationActHandler.INSTANCE.sendAnimationMessage(this.mangler, Mangler.DASHATTACK);
            }
        }
    }

    private void walk(){
        LivingEntity target = mangler.getTarget();
        if(target!=null) {
            double dist = this.mangler.distanceToSqr(this.targetX, this.targetY, this.targetZ);
            if (--this.repath <= 0 && (
                    this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D ||
                            target.distanceToSqr(this.targetX, this.targetY, this.targetZ) >= 0.5D) ||
                    this.mangler.getNavigation().isDone()
            ) {
                this.targetX = target.getX();
                this.targetY = target.getY();
                this.targetZ = target.getZ();
                this.repath = 4 + this.mangler.getRandom().nextInt(7);
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

    private void resetPath(){
        targetX=0;targetY=0;targetZ=0;
        mangler.getNavigation().stop();
    }

    private boolean moveMode(LivingEntity target){
        return this.mangler.getNavigation().moveTo(target, 0.3);
    }

}
