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
    private int skillHalberdTime = 0;
    private int moveHalberdBack = 0;
    private int removeHalberd = 0;
    private int moveHalberdRightLeft = 0;
    private int moveHalberdMove = 0;
    private int actHalberdChi = 0;
    private int remoteHalberdZhou = 0;
    private int remoteHalberdLian = 0;
    private int skillHalberd10 = 0;
    private int windMill = 0;

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
        float healthRadio = patriot.getHealth()/ patriot.getMaxHealth();

        skillHalberdTime++;
        moveHalberdRightLeft++;
        moveHalberdMove++;
        actHalberdChi++;
        remoteHalberdZhou++;
        remoteHalberdLian++;
        skillHalberd10++;
        windMill++;

        double dist = this.patriot.distanceTo(target);
        if(dist>8){
            removeHalberd++;
        }else {
            moveHalberdBack++;
        }

        if(!(a == NO_ANIMATION)) {
            return;
        }
        walk();

        if(dist>12&&moveHalberdRightLeft>300){
            if(random.nextInt(2)==0){
                AnimationActHandler.INSTANCE.sendAnimationMessage(patriot, MOVE_HALBERD_LEFT);
            }
            else {
                AnimationActHandler.INSTANCE.sendAnimationMessage(patriot, MOVE_HALBERD_RIGHT);
            }
            moveHalberdRightLeft = 0;
        }else if(dist<12&&healthRadio<0.5&&skillHalberdTime>200){
            AnimationActHandler.INSTANCE.sendAnimationMessage(patriot,SKILL_HALBERD_2);
            skillHalberdTime = 0;
        }else if(dist>10&&moveHalberdMove>123){
            AnimationActHandler.INSTANCE.sendAnimationMessage(patriot, MOVE_HALBERD_CYCLE);
            moveHalberdMove = 0;
        }else if(dist>10&&remoteHalberdZhou>123){
            AnimationActHandler.INSTANCE.sendAnimationMessage(patriot, REMOTE_HALBERD_RZHOU);
            remoteHalberdZhou = 0;
        }else if(skillHalberd10>523){
            AnimationActHandler.INSTANCE.sendAnimationMessage(patriot, SKILL_HALBERD_10);
            skillHalberd10 = 0;
        }else if(dist<10&&windMill>423){
            AnimationActHandler.INSTANCE.sendAnimationMessage(patriot, WIND_MILL);
            windMill = 0;
        }else if(dist>8&&removeHalberd>50){
            if(random.nextFloat()>0.5f){
                AnimationActHandler.INSTANCE.sendAnimationMessage(patriot, REMOTE_HALBERD_RL2);
            }
            else {
                AnimationActHandler.INSTANCE.sendAnimationMessage(patriot, REMOTE_HALBERD_THROW);
            }
            removeHalberd = 0;
        }
        if (target.getY() - this.patriot.getY() >= -1 && target.getY() - this.patriot.getY() <= 3) {
            if (dist < 4D * 4D && Math.abs(MathUtils.wrapDegrees(this.patriot.getAngleBetweenEntities(target, this.patriot) - this.patriot.yBodyRot)) < 35.0D) {
                if(shouldFollowUp(3.5)) {
                    if(moveHalberdBack>200){
                        AnimationActHandler.INSTANCE.sendAnimationMessage(patriot, MOVE_HALBERD_BACK);
                        removeHalberd+=50;
                        moveHalberdBack = 0;
                    }
                    else if(actHalberdChi>100){
                        float v = random.nextFloat();
                        if (v > 0.5f) {
                            AnimationActHandler.INSTANCE.sendAnimationMessage(patriot, ACK_HALBERD_CHI3);
                        } else
                            AnimationActHandler.INSTANCE.sendAnimationMessage(patriot, ACK_HALBERD_CHILEFT);
                        actHalberdChi = 0;
                    }
                    else if(remoteHalberdLian>300){
                        AnimationActHandler.INSTANCE.sendAnimationMessage(patriot, SKILL_HALBERD_LIAN);
                        remoteHalberdLian = 0;
                    }
                    else {
                        float v = random.nextFloat();
                        if (v > 0.5f) {
                            AnimationActHandler.INSTANCE.sendAnimationMessage(patriot, ACK_HALBERD_R);
                        } else
                            AnimationActHandler.INSTANCE.sendAnimationMessage(patriot, ACK_HALBERD_L);
                    }
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
            moveMode(target);
        }
    }

    private boolean moveMode(LivingEntity target){
        return this.patriot.getNavigation().moveTo(target, 0.34);
    }
}
