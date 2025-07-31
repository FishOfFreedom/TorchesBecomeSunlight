package com.freefish.torchesbecomesunlight.server.entity.ai.entity;

import com.freefish.torchesbecomesunlight.server.entity.animal.Mangler;
import com.freefish.torchesbecomesunlight.server.entity.ursus.Pursuer;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationAct;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

import static com.freefish.torchesbecomesunlight.server.util.animation.IAnimatedEntity.NO_ANIMATION;

public class ManglerAttackAI extends Goal {
    private Mangler mangler;

    private int repath;
    private double targetX;
    private double targetY;
    private double targetZ;

    public ManglerAttackAI(Mangler mangler){
        this.mangler = mangler;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
    }
    @Override
    public boolean canUse() {
        LivingEntity target = this.mangler.getTarget();
        return target != null && target.isAlive()&&mangler.isAngryLevel()>1;
    }

    @Override
    public void start() {
        this.repath = 0;
        mangler.setRun(true);
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
        if (target.getY() - this.mangler.getY() >= -1 && target.getY() - this.mangler.getY() <= 3) {
            if (dist < 2&& Math.abs(MathUtils.wrapDegrees(this.mangler.getAngleBetweenEntities(target, this.mangler) - this.mangler.yBodyRot)) < 35.0D) {
                    float v = random.nextFloat();
                    if(v>0.5f)
                        AnimationActHandler.INSTANCE.sendAnimationMessage(this.mangler, Mangler.ATTACK2);
                    else
                        AnimationActHandler.INSTANCE.sendAnimationMessage(this.mangler, Mangler.ATTACK);
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
        return this.mangler.getNavigation().moveTo(target, 0.27);
    }

    private boolean shouldFollowUp(double bonusRange) {
        LivingEntity entityTarget = mangler.getTarget();
        if (entityTarget != null && entityTarget.isAlive()) {
            Vec3 targetMoveVec = entityTarget.getDeltaMovement();
            Vec3 betweenEntitiesVec = mangler.position().subtract(entityTarget.position());
            boolean targetComingCloser = targetMoveVec.dot(betweenEntitiesVec) < 0;
            double targetDistance = mangler.distanceTo(entityTarget);
            return targetDistance < bonusRange || (targetDistance <5 + bonusRange && targetComingCloser);
        }
        return false;
    }

}
