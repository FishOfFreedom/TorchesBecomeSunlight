package com.freefish.torchesbecomesunlight.server.entity.ai.entity.rhodes;

import com.freefish.torchesbecomesunlight.server.entity.rhodesIsland.rosmontis.Rosmontis;
import com.freefish.torchesbecomesunlight.server.entity.rhodesIsland.rosmontis.RosmontisInstallation;
import com.freefish.torchesbecomesunlight.server.util.FFEntityUtils;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationAct;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

import static com.freefish.torchesbecomesunlight.server.util.animation.IAnimatedEntity.NO_ANIMATION;

public class RosmontisAttackAI extends Goal {
    private final Rosmontis mob;

    private int timeSinceRemote1;
    private int timeSinceRemote2;
    private int timeSinceAttack1;
    private int timeSinceAttack2;
    private int timeSinceSkill1;
    private int timeSinceSkill2;
    private int timeSinceSkill3;
    private int timeSinceSkill4;
    private int timeSinceSkillArmor;
    private int timeSinceMove;
    private int timeSinceCycle;
    private int timeSinceAttack4;


    public RosmontisAttackAI(Rosmontis mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK,Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        return mob.getTarget() != null&&mob.getTarget().isAlive();
    }

    public void start() {
        this.mob.setAggressive(true);
        timeSinceRemote1 = 50;
        mob.timeSinceRemote3 = 310;
        timeSinceSkill1 = 200;
        timeSinceSkill2 = 150;
        boolean flad = false;
        for(RosmontisInstallation installation:mob.installations){
            if (installation == null || installation.isRemoved() || installation.animationType == -1) {
                flad = true;
                break;
            }
        }
        if(flad)
            AnimationActHandler.INSTANCE.sendAnimationMessage(mob,Rosmontis.IDLE_TO_ACT);
    }

    public void stop() {
        super.stop();
        this.mob.setAggressive(false);
        this.mob.getMoveControl().strafe(0, 0);
    }

    public void tick() {
        final float cycleSpeed = 10f;
        LivingEntity target = this.mob.getTarget();
        if (target == null) return;
        RandomSource random = mob.getRandom();
        AnimationAct animationAct = mob.getAnimation();
        timeSinceRemote1++;
        //timeSinceRemote2++;
        timeSinceAttack1++;timeSinceAttack2++;timeSinceSkill1++;
        timeSinceSkill3++;timeSinceSkillArmor++;timeSinceSkill2++;
        timeSinceSkill4++;
        timeSinceCycle++;

        double dist = mob.distanceTo(target);
        if(dist>10){
            timeSinceMove++;
        }
        if(mob.livingInstallation!=null&&mob.livingInstallation.isAlive()){
            if(mob.timeSinceRemote3<260){
                mob.timeSinceRemote3++;
            }
            if(target == mob.challengePlayer){
                mob.timeSinceRemote3 = 160;
            }
        }else {
            mob.timeSinceRemote3++;
        }
        if(!(animationAct == NO_ANIMATION)) return;

        if(mob.cycleTime > 0&&(mob.getState()||mob.getIsFlaying())){
            float speed = ((float) Math.PI * 2) / (cycleSpeed / mob.cycleRadius);
            Vec3 cycle = mob.updateCyclePosition(1.0f / speed);

            Vec3 vec3 = mob.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D);

            Vec3 vec31 = new Vec3(cycle.x - mob.getX(), 0.0D, cycle.z - mob.getZ());
            Vec3 vec32 = vec31.normalize();
            vec3 = vec3.add(vec32.x * 0.1D - vec3.x * 0.2D, vec3.y, vec32.z * 0.1D - vec3.z * 0.2D);
            mob.getLookControl().setLookAt(cycle);
            float posToPosRot = FFEntityUtils.getPosToPosRot(mob.position(), cycle);

            mob.setYRot(posToPosRot);
            mob.setYBodyRot(posToPosRot);

            mob.setDeltaMovement(vec3);

            mob.cycleTime--;
        }
        else {
            if (dist > 12||!mob.getState()) {
                walk(dist);
            } else {
                mob.cycleRadius = mob.distanceTo(target)*2;
                mob.startCycle(160);
                timeSinceCycle = 0;
                mob.setDeltaMovement(Vec3.ZERO);
            }
        }

        if (mob.timeSinceRemote3 > 360 && mob.installationIsAble(1)) {
            AnimationActHandler.INSTANCE.sendAnimationMessage(mob, Rosmontis.REMOTE_3);
            mob.timeSinceRemote3 = 0;
        } else if (!mob.getState()&&timeSinceMove > 200) {
            AnimationActHandler.INSTANCE.sendAnimationMessage(mob, Rosmontis.MOVE_1);
            timeSinceMove = 0;
        }else  if (timeSinceSkillArmor > 230) {
            AnimationActHandler.INSTANCE.sendAnimationMessage(mob, Rosmontis.SKILL_ARMOR);
            timeSinceSkillArmor = 0;
        } else if (timeSinceSkill2 > 432) {
            if(mob.getState())
                AnimationActHandler.INSTANCE.sendAnimationMessage(mob, Rosmontis.SKILL_2_FLY);
            else
                AnimationActHandler.INSTANCE.sendAnimationMessage(mob, Rosmontis.SKILL_2);
            timeSinceSkill2 = 0;
        } else if (timeSinceSkill3 > 469) {
            AnimationActHandler.INSTANCE.sendAnimationMessage(mob, Rosmontis.SKILL_3);
            timeSinceSkill3 = 0;
        } else if (timeSinceSkill1 > 300) {
            AnimationActHandler.INSTANCE.sendAnimationMessage(mob, Rosmontis.SKILL_1);
            timeSinceSkill1 = 0;
        } else if ((mob.installationIsAble(0)&&mob.installationIsAble(1)&&mob.installationIsAble(2)&&mob.installationIsAble(3))&&timeSinceSkill4 > 423) {
            AnimationActHandler.INSTANCE.sendAnimationMessage(mob, Rosmontis.SKILL_4);
            timeSinceSkill4 = 0;
        } else if(dist<6&&Math.abs(target.getY()-mob.getTarget().getY())<6){
            if(mob.installationIsAble(0)&&timeSinceAttack1>60){
                boolean b = random.nextBoolean();
                if(b){
                    AnimationActHandler.INSTANCE.sendAnimationMessage(mob, Rosmontis.ATTACK_1);
                }else {
                    if(mob.getState())
                        AnimationActHandler.INSTANCE.sendAnimationMessage(mob, Rosmontis.ATTACK_2_FLY);
                    else
                        AnimationActHandler.INSTANCE.sendAnimationMessage(mob, Rosmontis.ATTACK_2);
                }
                timeSinceAttack1=0;
            }else if(mob.installationIsAble(2)&&mob.installationIsAble(3)&&timeSinceAttack2>60){
                AnimationActHandler.INSTANCE.sendAnimationMessage(mob, Rosmontis.ATTACK_3);
                timeSinceAttack2=0;
            }else if(mob.installationIsAble(1)&&mob.installationIsAble(2)&&mob.installationIsAble(2)&&mob.installationIsAble(3)){
                AnimationActHandler.INSTANCE.sendAnimationMessage(mob, Rosmontis.ATTACK_4);
            }
        }
        else  {
             if (timeSinceRemote1 > 40) {
                 if(timeSinceRemote2>=2){
                     if(mob.getState())
                         AnimationActHandler.INSTANCE.sendAnimationMessage(mob, Rosmontis.REMOTE_2_FLY);
                     else
                        AnimationActHandler.INSTANCE.sendAnimationMessage(mob, Rosmontis.REMOTE_2);
                     timeSinceRemote2=0;
                 }
                 else  {
                     timeSinceRemote2++;
                     if(mob.getState())
                         AnimationActHandler.INSTANCE.sendAnimationMessage(mob, Rosmontis.REMOTE_1_FLY);
                     else
                         AnimationActHandler.INSTANCE.sendAnimationMessage(mob, Rosmontis.REMOTE_1);
                 }
                timeSinceRemote1 = 0;
            }
            // else if (timeSinceRemote2 > 110) {
            //    timeSinceRemote2 = 0;
            //}
        }
    }

    private void walk(double dist){

        LivingEntity target = mob.getTarget();
        if(target!=null) {
            if(!mob.getState()&&!mob.getIsFlaying()){
                moveMode(target);
            } else {
                Vec3 vec3 = mob.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D);

                Vec3 vec31 = new Vec3(target.getX() - mob.getX(), 0.0D, target.getZ() - mob.getZ());
                if (vec31.horizontalDistanceSqr() > 6.0D) {
                    Vec3 vec32 = vec31.normalize();
                    vec3 = vec3.add(vec32.x * 0.1D - vec3.x * 0.2D, vec3.y, vec32.z * 0.1D - vec3.z * 0.2D);
                }
                mob.getLookControl().setLookAt(target);

                mob.setDeltaMovement(vec3);

                float posToPosRot = FFEntityUtils.getPosToPosRot(mob.position(), mob.position().add(mob.getDeltaMovement()));

                mob.setYRot(posToPosRot);
                mob.setYBodyRot(posToPosRot);
            }
        }
    }

    private boolean moveMode(LivingEntity target){
        return this.mob.getNavigation().moveTo(target, 0.35);
    }

    private void moveMode(Vec3 vec3){
        this.mob.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, 0.35);
    }
}