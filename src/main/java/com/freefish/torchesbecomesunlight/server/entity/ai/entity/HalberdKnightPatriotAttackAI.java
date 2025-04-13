package com.freefish.torchesbecomesunlight.server.entity.ai.entity;

import com.freefish.torchesbecomesunlight.server.entity.ITwoStateEntity;
import com.freefish.torchesbecomesunlight.server.entity.dlc.GunKnightPatriot;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationAct;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

import static com.freefish.torchesbecomesunlight.server.entity.dlc.GunKnightPatriotAnimations.*;
import static com.freefish.torchesbecomesunlight.server.util.animation.IAnimatedEntity.NO_ANIMATION;

public class HalberdKnightPatriotAttackAI extends Goal {
    private final GunKnightPatriot patriot;

    public HalberdKnightPatriotAttackAI(GunKnightPatriot patriot) {
        this.patriot = patriot;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.patriot.getTarget();
        return target != null && target.isAlive()&& patriot.getSpawnState()== ITwoStateEntity.State.TWO;
    }

    @Override
    public void start() {
        patriot.setAggressive(true);
    }

    @Override
    public void stop() {
        patriot.setAggressive(false);
        this.patriot.getNavigation().stop();
    }

    @Override
    public void tick() {
        LivingEntity target = this.patriot.getTarget();
        if (target == null) return;
        RandomSource random = patriot.getRandom();
        AnimationAct a = patriot.getAnimation();

        if(!(a == NO_ANIMATION)) {
            if(a == MACHINE_GUN_1||a == ARTILLERY_1||a == SHOTGUN_1){
                double dist1 = this.patriot.distanceTo(target);
                if(dist1>6)
                    walk();
                else
                    patriot.getNavigation().stop();
            }
            return;
        }
        walk();

        double dist = this.patriot.distanceTo(target);
        if (target.getY() - this.patriot.getY() >= -1 && target.getY() - this.patriot.getY() <= 3) {
            if (dist < 4D * 4D && Math.abs(MathUtils.wrapDegrees(this.patriot.getAngleBetweenEntities(target, this.patriot) - this.patriot.yBodyRot)) < 35.0D) {
                if(shouldFollowUp(3.5)) {
                    float v = random.nextFloat();
                    if(v>0.5f){
                        AnimationActHandler.INSTANCE.sendAnimationMessage(patriot,ACK_HALBERD_R);
                    }
                    else
                        AnimationActHandler.INSTANCE.sendAnimationMessage(patriot,ACK_HALBERD_L);
                }
            }
        }
    }

    private boolean shouldFollowUp(double bonusRange) {
        LivingEntity entityTarget = patriot.getTarget();
        if (entityTarget != null && entityTarget.isAlive()) {
            Vec3 targetMoveVec = entityTarget.getDeltaMovement();
            Vec3 betweenEntitiesVec = patriot.position().subtract(entityTarget.position());
            boolean targetComingCloser = targetMoveVec.dot(betweenEntitiesVec) < 0;
            double targetDistance = patriot.distanceTo(entityTarget);
            return targetDistance < bonusRange || (targetDistance <5 + bonusRange && targetComingCloser);
        }
        return false;
    }

    private void walk(){
        LivingEntity target = patriot.getTarget();
        if(target!=null) {
            //moveMode(target);
        }
    }

    private boolean moveMode(LivingEntity target){
        return this.patriot.getNavigation().moveTo(target, 0.34);
    }
}
