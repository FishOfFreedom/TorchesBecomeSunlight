package com.freefish.torchesbecomesunlight.server.entity.ai.entity.snownova;

import com.freefish.torchesbecomesunlight.server.animation.AnimationActHandler;
import com.freefish.torchesbecomesunlight.server.capability.frozen.FrozenCapabilityProvider;
import com.freefish.torchesbecomesunlight.server.entity.effect.IceWallEntity;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster.SnowNova;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

public class SnowNova1AttackAI extends Goal {
    private final SnowNova mob;
    private int timeSinceDash;
    private int timeSinceIceJump;
    private int timeSinceIceGround;
    private int timeSinceIceWind;

    private double targetX;
    private double targetY;
    private double targetZ;
    private int rePath;

    public SnowNova1AttackAI(SnowNova mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return mob.getTarget() != null&&mob.getTarget().isAlive()&&mob.getAnimation()== SnowNova.NO_ANIMATION;
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
        RandomSource random = mob.getRandom();
        final float cycleSpeed = 7.3f;
        if (target != null) {
            target.getCapability(FrozenCapabilityProvider.FROZEN_CAPABILITY).ifPresent(data -> {
                if(data.isFrozen)
                    timeSinceDash += 2;
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
                if(distToTarget >= 10 && distToTarget <= 14 &&timeSinceDash>=80) {
                    timeSinceDash = 0;
                    AnimationActHandler.INSTANCE.sendAnimationMessage(mob, SnowNova.DASH);
                } else
                if(distToTarget <=5 && timeSinceIceJump>=100){
                    timeSinceIceJump = 0;
                    AnimationActHandler.INSTANCE.sendAnimationMessage(mob, SnowNova.ICE_JUMP);
                } else
                if(distToTarget <=3 && mob.timeSinceIceBomb>=100){
                    mob.timeSinceIceBomb = 0;
                    if(mob.getState()==0) {
                        mob.timeSinceIceBomb = 50;
                        AnimationActHandler.INSTANCE.sendAnimationMessage(mob, SnowNova.REMOTE_2);
                    }
                    else
                        AnimationActHandler.INSTANCE.sendAnimationMessage(mob, SnowNova.ICE_BOMB);
                }else
                if(mob.getState()==1&&distToTarget <=11 && timeSinceIceGround>=60){
                    timeSinceIceGround = 0;
                    AnimationActHandler.INSTANCE.sendAnimationMessage(mob, SnowNova.ICE_GROUND);
                }
                else if(distToTarget <= 6 && timeSinceIceWind>=40){
                    timeSinceIceWind = 0;
                    AnimationActHandler.INSTANCE.sendAnimationMessage(mob, SnowNova.ICE_WIND);
                }
                if(mob.getState()==0) {
                    if (distToTarget <= 2 + target.getBbWidth()/2) {
                        if (random.nextFloat() < 0.5)
                            AnimationActHandler.INSTANCE.sendAnimationMessage(mob, SnowNova.ATTACK_1);
                        else
                            AnimationActHandler.INSTANCE.sendAnimationMessage(mob, SnowNova.ATTACK_2);
                    }
                }
                else {
                    if(distToTarget <=10)
                        AnimationActHandler.INSTANCE.sendAnimationMessage(mob, SnowNova.BACK_JUMP);
                    else if (distToTarget <= 13) {
                        AnimationActHandler.INSTANCE.sendAnimationMessage(mob, SnowNova.REMOTE_1);
                        mob.startCycle(20);
                    }
                }
            }
            timeSinceDash++;
            timeSinceIceJump++;
            mob.timeSinceIceBomb++;
            timeSinceIceGround++;
            timeSinceIceWind++;
        }
    }

    private void walk(){
        LivingEntity target = mob.getTarget();
        if(target!=null) {
            double dist = this.mob.distanceToSqr(this.targetX, this.targetY, this.targetZ);
            this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
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
        List<IceWallEntity> iceWall = mob.level().getEntitiesOfClass(IceWallEntity.class,mob.getBoundingBox().inflate(6),
                iceWallEntity -> iceWallEntity.distanceTo(mob)<4.5);
        if(iceWall.isEmpty()) {
            if (mob.getState() == 0)
                return this.mob.getNavigation().moveTo(target, 0.6);
            else
                return this.mob.getNavigation().moveTo(target, 0.35);
        }
        else {
            this.mob.getNavigation().stop();
            return false;
        }
    }

    private void moveMode(Vec3 vec3){
        List<IceWallEntity> iceWall = mob.level().getEntitiesOfClass(IceWallEntity.class,mob.getBoundingBox().inflate(6),
                iceWallEntity -> iceWallEntity.distanceTo(mob)<4.5);
        if(iceWall.isEmpty()) {
            if(mob.getState()==0) {
                this.mob.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, 0.6);
            } else {
                this.mob.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, 0.35);
            }
        }
        else this.mob.getNavigation().stop();
    }
}
