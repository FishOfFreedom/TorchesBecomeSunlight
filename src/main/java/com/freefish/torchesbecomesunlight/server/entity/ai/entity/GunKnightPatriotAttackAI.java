package com.freefish.torchesbecomesunlight.server.entity.ai.entity;


import com.freefish.torchesbecomesunlight.server.entity.dlc.GunKnightPatriot;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationAct;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

import static com.freefish.torchesbecomesunlight.server.util.animation.IAnimatedEntity.NO_ANIMATION;

public class GunKnightPatriotAttackAI extends Goal {
    private final GunKnightPatriot patriot;

    private int timeSinceSummon=265;
    private int timeSinceCheng;
    private int timeSinceAll;
    private int timeSinceReload;
    private int timeSinceSkill;
    private int timeSinceShoot;

    private int normalAttackTime;

    public GunKnightPatriotAttackAI(GunKnightPatriot patriot) {
        this.patriot = patriot;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.patriot.getTarget();
        return target != null && target.isAlive();
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

        timeSinceSummon++;timeSinceCheng++;
        timeSinceAll++;timeSinceReload++;
        timeSinceSkill++;
        timeSinceShoot++;

        if(!(a == NO_ANIMATION)) {
            if(a == GunKnightPatriot.MACHINE_GUN_1||a == GunKnightPatriot.ARTILLERY_1||a == GunKnightPatriot.SHOTGUN_1){
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

        int gunMod = patriot.getGunMod();

        if(gunMod==0){
            if(normalAttackTime>=8){
                if(dist>20) patriot.transGun(1);
                else patriot.transGun(2);
                normalAttackTime=0;
            }
        }else {
            if(normalAttackTime>=8){
                if(gunMod==1) {
                    patriot.transGun(0);
                    normalAttackTime=0;
                }
                else if (gunMod==2&&dist>20) {
                    patriot.transGun(0);
                    normalAttackTime=0;
                }
            }
        }

        if(timeSinceSummon>410){
            timeSinceSummon = 0;
            AnimationActHandler.INSTANCE.sendAnimationMessage(patriot,GunKnightPatriot.SUMMON_TURRET);
        }else if(timeSinceCheng>450){
            timeSinceCheng = 0;
            AnimationActHandler.INSTANCE.sendAnimationMessage(patriot,GunKnightPatriot.SUMMON_CHENG);
        }
        else if(timeSinceAll>550){
            timeSinceAll = 0;
            AnimationActHandler.INSTANCE.sendAnimationMessage(patriot,GunKnightPatriot.ALL_SHOT);
        }
        else if(timeSinceReload>300){
            timeSinceReload = 0;
            AnimationActHandler.INSTANCE.sendAnimationMessage(patriot,GunKnightPatriot.RELOAD);
        }
        else if(timeSinceSkill>700){
            timeSinceReload = 0;
            timeSinceAll = 0;
            timeSinceCheng = 0;
            timeSinceSkill = 0;
            AnimationActHandler.INSTANCE.sendAnimationMessage(patriot,GunKnightPatriot.SKILL_START);
        }
        else if(dist<60&&dist>6&&gunMod==0&&timeSinceShoot>60){
            AnimationActHandler.INSTANCE.sendAnimationMessage(patriot,GunKnightPatriot.MACHINE_GUN_1);
            timeSinceShoot=0;
            normalAttackTime+=5;
        } else if(dist<60&&dist>6&&gunMod==1&&timeSinceShoot>35){
            AnimationActHandler.INSTANCE.sendAnimationMessage(patriot,GunKnightPatriot.ARTILLERY_1);
            timeSinceShoot=0;
            normalAttackTime+=2;
        }else if(dist<20&&dist>6&&gunMod==2&&timeSinceShoot>45){
            AnimationActHandler.INSTANCE.sendAnimationMessage(patriot,GunKnightPatriot.SHOTGUN_1);
            timeSinceShoot=0;
            normalAttackTime+=5;
        }
        else if (target.getY() - this.patriot.getY() >= -1 && target.getY() - this.patriot.getY() <= 3) {
            if (dist < 4D * 4D && Math.abs(MathUtils.wrapDegrees(this.patriot.getAngleBetweenEntities(target, this.patriot) - this.patriot.yBodyRot)) < 35.0D) {
                if(shouldFollowUp(3.5)) {
                    float v = random.nextFloat();
                    if(gunMod==2&&v<0.3)
                        AnimationActHandler.INSTANCE.sendAnimationMessage(patriot,GunKnightPatriot.SHOTGUN_1);
                    else if(v<0.6)
                        AnimationActHandler.INSTANCE.sendAnimationMessage(patriot,GunKnightPatriot.ATTACK1);
                    else
                        AnimationActHandler.INSTANCE.sendAnimationMessage(patriot,GunKnightPatriot.STOMP);
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
        return this.patriot.getNavigation().moveTo(target, 0.23);
    }
}
