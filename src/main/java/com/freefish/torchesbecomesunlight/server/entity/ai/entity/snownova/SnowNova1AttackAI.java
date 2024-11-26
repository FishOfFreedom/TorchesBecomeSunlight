package com.freefish.torchesbecomesunlight.server.entity.ai.entity.snownova;

import com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster.FrostNova;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import com.freefish.torchesbecomesunlight.server.capability.frozen.FrozenCapabilityProvider;
import com.freefish.torchesbecomesunlight.server.entity.effect.IceWallEntity;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

public class SnowNova1AttackAI extends Goal {
    private final FrostNova mob;
    private int timeSinceDash;
    private int timeSinceIceJump;
    private int timeSinceIceGround;
    private int timeSinceIceWind;
    private int timeSinceIceBlade;

    private double targetX;
    private double targetY;
    private double targetZ;
    private int rePath;

    public SnowNova1AttackAI(FrostNova mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK,Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        return mob.getTarget() != null&&mob.getTarget().isAlive();
    }

    public void start() {
        this.mob.setAggressive(true);
        this.rePath = 0;
    }

    public void stop() {
        super.stop();
        this.mob.setAggressive(false);
        this.mob.getMoveControl().strafe(0, 0);
    }

    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target == null) return;
        RandomSource random = mob.getRandom();
        final float cycleSpeed = 8.5f;

        timeSinceDash++;
        timeSinceIceJump++;
        mob.timeSinceIceBomb++;
        timeSinceIceGround++;
        timeSinceIceWind++;
        timeSinceIceBlade++;

        if(!(mob.getAnimation()== FrostNova.NO_ANIMATION||mob.getAnimation()== FrostNova.DASH_RUN)) return;
        if(mob.getAnimation()== FrostNova.DASH_RUN){
            walk();
            return;
        }

        target.getCapability(FrozenCapabilityProvider.FROZEN_CAPABILITY).ifPresent(data -> {
            if(data.isFrozen) {
                timeSinceDash += 4;
                timeSinceIceBlade +=2;
            }
        });

        double distToTarget = this.mob.distanceTo(target);
        if(mob.cycleTime > 0){
            float speed = ((float) Math.PI*2)/(cycleSpeed/mob.cycleRadius);
            Vec3 cycle = mob.updateCyclePosition(1.0f/speed);
            this.mob.getMoveControl().strafe(0, 0);
            moveMode(cycle);
        }
        else {
            if (distToTarget > 0.6) {
                walk();
            }
            if(distToTarget >= 6 && distToTarget <= 16 &&timeSinceDash>=160) {
                timeSinceDash = 0;
                AnimationActHandler.INSTANCE.sendAnimationMessage(mob, FrostNova.DASH_RUN);
            } else if(distToTarget <=5 && timeSinceIceJump>=100){
                timeSinceIceJump = 0;
                AnimationActHandler.INSTANCE.sendAnimationMessage(mob, FrostNova.ICE_JUMP);
            } else if(distToTarget <=3 && mob.timeSinceIceBomb>=100){
                mob.timeSinceIceBomb = 0;
                if(mob.getState()==0) {
                    mob.timeSinceIceBomb = 50;
                    AnimationActHandler.INSTANCE.sendAnimationMessage(mob, FrostNova.REMOTE_2);
                }
                else
                    AnimationActHandler.INSTANCE.sendAnimationMessage(mob, FrostNova.ICE_BOMB);
            }else if(mob.getState()==1&&distToTarget <=11 && timeSinceIceGround>=140){
                timeSinceIceGround = 0;
                AnimationActHandler.INSTANCE.sendAnimationMessage(mob, FrostNova.ICE_GROUND);
            }
            else if(distToTarget > 7 && distToTarget < 9.5 && timeSinceIceWind>=100){
                timeSinceIceWind = 0;
                AnimationActHandler.INSTANCE.sendAnimationMessage(mob, FrostNova.ICE_WIND);
            }
            else if(mob.getState()==1&&distToTarget >= 10 && timeSinceIceBlade>=50){
                timeSinceIceBlade = 0;
                AnimationActHandler.INSTANCE.sendAnimationMessage(mob, FrostNova.REMOTE_3);
            }
            if(mob.getState()==0) {
                if (distToTarget <= 2 + target.getBbWidth()/2) {
                    if (random.nextFloat() < 0.5)
                        AnimationActHandler.INSTANCE.sendAnimationMessage(mob, FrostNova.ATTACK_1);
                    else
                        AnimationActHandler.INSTANCE.sendAnimationMessage(mob, FrostNova.ATTACK_2);
                }
            }
            else {
                List<IceWallEntity> iceWall = mob.level().getEntitiesOfClass(IceWallEntity.class,mob.getBoundingBox().inflate(3));
                if(distToTarget <=9&&mob.timeSinceBackJump>=120) {
                    mob.timeSinceBackJump = 0;
                    AnimationActHandler.INSTANCE.sendAnimationMessage(mob, FrostNova.BACK_JUMP);
                }
                else if (distToTarget <= 13&&iceWall.isEmpty()) {
                    AnimationActHandler.INSTANCE.sendAnimationMessage(mob, FrostNova.REMOTE_1);
                    int add = 0;
                    if(target instanceof Player) add = 20;
                    mob.startCycle(30+add);
                }
            }
        }
    }

    private void walk(){
        LivingEntity target = mob.getTarget();
        if(target!=null) {
            double dist = this.mob.distanceToSqr(this.targetX, this.targetY, this.targetZ);
            if (--this.rePath <= 0 && (
                    this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D ||
                            target.distanceToSqr(this.targetX, this.targetY, this.targetZ) >= 1.0D) ||
                    this.mob.getNavigation().isDone()
            ) {
                this.targetX = target.getX();
                this.targetY = target.getY();
                this.targetZ = target.getZ();
                this.rePath = 4 + this.mob.getRandom().nextInt(7);
                if (dist > 1024D) {
                    this.rePath += 10;
                } else if (dist > 256D) {
                    this.rePath += 5;
                }
                if (!moveMode(target)) {
                    this.rePath += 15;
                }
            }
        }
    }

    private boolean moveMode(LivingEntity target){
        if(mob.getAnimation()== FrostNova.DASH_RUN)
            return this.mob.getNavigation().moveTo(target, 1.1);
        else  {
            if (mob.getState() == 0)
                return this.mob.getNavigation().moveTo(target, 0.6);
            else
                return this.mob.getNavigation().moveTo(target, 0.35);
        }
    }

    private void moveMode(Vec3 vec3){
        if(mob.getState()==0) {
            this.mob.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, 0.6);
        } else {
            this.mob.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, 0.35);
        }
    }
}
