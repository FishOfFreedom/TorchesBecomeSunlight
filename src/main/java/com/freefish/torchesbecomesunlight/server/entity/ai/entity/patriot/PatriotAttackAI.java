package com.freefish.torchesbecomesunlight.server.entity.ai.entity.patriot;


import com.freefish.torchesbecomesunlight.server.animation.AnimationActHandler;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.shield.Patriot;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster.SnowNova;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import software.bernie.shadowed.eliotlash.mclib.utils.MathHelper;

import java.util.EnumSet;

import static com.freefish.torchesbecomesunlight.server.animation.IAnimatedEntity.NO_ANIMATION;

public class PatriotAttackAI extends Goal {
    private final Patriot patriot;

    private int repath;
    private double targetX;
    private double targetY;
    private double targetZ;
    private int timeNormalAttack;
    private int timeSinceRun;
    private int timeSinceThrow;
    private int timeSinceStomp;
    private int timeSinceStrengthen;
    private int timeSinceHunt;

    public PatriotAttackAI(Patriot patriot) {
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
        this.repath = 0;
    }

    @Override
    public void stop() {
        this.patriot.getNavigation().stop();
    }

    @Override
    public void tick() {
        LivingEntity target = this.patriot.getTarget();
        if (target == null) return;
        RandomSource random = patriot.getRandom();
        if(!(this.patriot.getAnimation() == NO_ANIMATION||patriot.getAnimation()==Patriot.RUN)) return;
        walk();
        if(patriot.getAnimation()==Patriot.RUN) return;

        double dist = this.patriot.distanceToSqr(this.targetX, this.targetY, this.targetZ);

        if(timeSinceStrengthen>=100){
            timeSinceStrengthen=0;
            AnimationActHandler.INSTANCE.sendAnimationMessage(this.patriot, Patriot.STRENGTHEN);
        }
        if (dist > 8  && timeSinceThrow > 120) {
            timeSinceThrow = 0;
            AnimationActHandler.INSTANCE.sendAnimationMessage(this.patriot, Patriot.THROW);
        }
        else if(dist >= 20 &&timeSinceRun>=80) {
            timeSinceRun = 0;
            AnimationActHandler.INSTANCE.sendAnimationMessage(patriot, Patriot.RUN);
        } else if (target.getY() - this.patriot.getY() >= -1 && target.getY() - this.patriot.getY() <= 3) {
            if (dist < 7D * 7D && Math.abs(MathHelper.wrapDegrees(this.patriot.getAngleBetweenEntities(target, this.patriot) - this.patriot.yBodyRot)) < 35.0D) {
                if(timeSinceStomp>=100){
                    timeSinceStomp=0;
                    AnimationActHandler.INSTANCE.sendAnimationMessage(this.patriot, Patriot.STOMP);
                }
                else  if(timeSinceHunt>=100&&patriot.getHealth()<patriot.getMaxHealth()/2){
                    timeSinceHunt=0;
                    AnimationActHandler.INSTANCE.sendAnimationMessage(this.patriot, Patriot.HUNT);
                }
                else if(shouldFollowUp(3.5-timeNormalAttack*1.5)) {
                    if (random.nextDouble() >= 0.66)
                        AnimationActHandler.INSTANCE.sendAnimationMessage(this.patriot, Patriot.ATTACK1);
                    else if (random.nextDouble() >= 0.33)
                        AnimationActHandler.INSTANCE.sendAnimationMessage(this.patriot, Patriot.PIERCE2);
                    else
                        AnimationActHandler.INSTANCE.sendAnimationMessage(this.patriot, Patriot.PROPEL1);
                }

            }
        }
        timeSinceRun++;
        timeSinceThrow++;
        timeSinceStomp++;
        timeSinceStrengthen++;
        timeSinceHunt++;
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
            double dist = this.patriot.distanceToSqr(this.targetX, this.targetY, this.targetZ);
            if (--this.repath <= 0 && (
                    this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D ||
                            target.distanceToSqr(this.targetX, this.targetY, this.targetZ) >= 1.0D) ||
                    this.patriot.getNavigation().isDone()
            ) {
                this.targetX = target.getX();
                this.targetY = target.getY();
                this.targetZ = target.getZ();
                this.repath = 4 + this.patriot.getRandom().nextInt(7);
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
        if(patriot.getAnimation()==Patriot.RUN)
            return this.patriot.getNavigation().moveTo(target, 0.8);
        else
            return this.patriot.getNavigation().moveTo(target, 0.25);
    }
}
