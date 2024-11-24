package com.freefish.torchesbecomesunlight.server.entity.ai.entity;

import com.freefish.torchesbecomesunlight.server.entity.effect.PursuerEffectEntity;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationAct;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import com.freefish.torchesbecomesunlight.server.entity.ursus.Pursuer;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

import static com.freefish.torchesbecomesunlight.server.util.animation.IAnimatedEntity.NO_ANIMATION;

public class PursuerAttackAI extends Goal {
    private Pursuer pursuer;

    private int repath;
    private double targetX;
    private double targetY;
    private double targetZ;
    private int timeSinceRemote1;
    private int timeSinceRemote2;
    private int timeSinceJump;
    private int timeSinceFastMove;
    private int timeSinceTele1;
    private int timeSinceBlackHole;
    private int timeActInterval;
    private int timeSinceSkill;

    public PursuerAttackAI(Pursuer pursuer){
        this.pursuer = pursuer;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.pursuer.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public void start() {
        this.repath = 0;
    }

    @Override
    public void stop() {
        this.pursuer.getNavigation().stop();
    }

    @Override
    public void tick() {
        LivingEntity target = this.pursuer.getTarget();
        if (target == null) return;
        RandomSource random = pursuer.getRandom();
        AnimationAct animationAct = pursuer.getAnimation();
        timeSinceRemote1++;timeSinceJump++;
        timeSinceFastMove++;timeSinceTele1++;
        timeActInterval++;timeSinceRemote2++;
        if(pursuer.getPredicate()!=0){
            timeSinceBlackHole++;
            if(pursuer.getHealth()<pursuer.getMaxHealth()/4)
                timeSinceSkill++;
        }

        if(animationAct == Pursuer.BLACKHOLE) resetPath();

        if(!(animationAct == NO_ANIMATION)) {
            stop();
            return;
        }

        walk();
        double dist = this.pursuer.distanceToSqr(this.targetX, this.targetY, this.targetZ);
//todo pursuer

        if(pursuer.getPredicate()==0) {
            if (dist > 5 && timeSinceRemote1 >= 100) {
                timeSinceRemote1 = 0;
                AnimationActHandler.INSTANCE.sendAnimationMessage(this.pursuer, Pursuer.REMOTE_1);
            }if (dist < 7 && timeSinceRemote2 >= 100) {
                timeSinceRemote2 = 0;
                AnimationActHandler.INSTANCE.sendAnimationMessage(this.pursuer, Pursuer.REMOTE_2);
            } else if (dist > 6 && timeSinceJump >= 160) {
                timeSinceJump = 0;
                AnimationActHandler.INSTANCE.sendAnimationMessage(this.pursuer, Pursuer.JUMP);
            } else if (dist > 9 && timeSinceFastMove >= 160) {
                timeSinceFastMove = 0;
                AnimationActHandler.INSTANCE.sendAnimationMessage(this.pursuer, Pursuer.FASTMOVE);
            } else if (target.getY() - this.pursuer.getY() >= -1 && target.getY() - this.pursuer.getY() <= 3) {
                if (dist < 12*12 && Math.abs(MathUtils.wrapDegrees(this.pursuer.getAngleBetweenEntities(target, this.pursuer) - this.pursuer.yBodyRot)) < 35.0D&&timeActInterval>30) {
                    timeActInterval=0;
                    double rand = random.nextDouble();
                    if (rand >= 0.5)
                        AnimationActHandler.INSTANCE.sendAnimationMessage(this.pursuer, Pursuer.ATTACK_1);
                    else if (rand >= 0.2)
                        AnimationActHandler.INSTANCE.sendAnimationMessage(this.pursuer, Pursuer.ATTACK_2);
                    else {
                        AnimationActHandler.INSTANCE.sendAnimationMessage(this.pursuer, Pursuer.BACKJUMP);
                    }
                }
            }
        }
        else{
            Entity pee = pursuer.getPee();
            if(pee instanceof PursuerEffectEntity peEntity&&peEntity.isAlive()){
                if (pursuer.demonPredicate) {
                    pursuer.demonPredicate = false;
                    AnimationActHandler.INSTANCE.sendAnimationMessage(this.pursuer, Pursuer.DEMON);
                }
                else {
                    pursuer.demonPredicate = true;
                    if(random.nextBoolean())
                        AnimationActHandler.INSTANCE.sendAnimationMessage(this.pursuer, Pursuer.REMOTE_3);
                    else
                        AnimationActHandler.INSTANCE.sendAnimationMessage(this.pursuer, Pursuer.REMOTE_4);
                }
            }
            else {
                if ( dist < 10 && timeSinceSkill >= 400) {
                    timeSinceSkill = 0;
                    AnimationActHandler.INSTANCE.sendAnimationMessage(this.pursuer, Pursuer.SKILL);
                } else if (dist > 6 && dist < 12 && timeSinceBlackHole >= 240) {
                    timeSinceBlackHole = 0;
                    AnimationActHandler.INSTANCE.sendAnimationMessage(this.pursuer, Pursuer.BLACKHOLE);
                } else if (dist > 6 && timeSinceJump >= 240) {
                    timeSinceJump = 0;
                    AnimationActHandler.INSTANCE.sendAnimationMessage(this.pursuer, Pursuer.JUMP);
                } else if (dist > 5 && timeSinceTele1 >= 220) {
                    timeSinceTele1 = 0;
                    AnimationActHandler.INSTANCE.sendAnimationMessage(this.pursuer, Pursuer.TELE1);
                } else if (dist > 9 && timeSinceFastMove >= 180) {
                    timeSinceFastMove = 0;
                    AnimationActHandler.INSTANCE.sendAnimationMessage(this.pursuer, Pursuer.FASTMOVE);
                } else if (target.getY() - this.pursuer.getY() >= -1 && target.getY() - this.pursuer.getY() <= 3) {
                    if (dist < 7D * 7D && Math.abs(MathUtils.wrapDegrees(this.pursuer.getAngleBetweenEntities(target, this.pursuer) - this.pursuer.yBodyRot)) < 35.0D) {
                        double rand = random.nextDouble();
                        if (rand > 0.8)
                            AnimationActHandler.INSTANCE.sendAnimationMessage(this.pursuer, Pursuer.BATTACK1);
                        else if (rand > 0.6) {
                            AnimationActHandler.INSTANCE.sendAnimationMessage(this.pursuer, Pursuer.BATTACKM1);
                        } else if (rand > 0.4) {
                            AnimationActHandler.INSTANCE.sendAnimationMessage(this.pursuer, Pursuer.BATTACK21);
                        } else {
                            AnimationActHandler.INSTANCE.sendAnimationMessage(this.pursuer, Pursuer.BATTACK31);
                        }
                    }
                }
            }
        }
    }

    private void walk(){
        LivingEntity target = pursuer.getTarget();
        if(target!=null) {
            double dist = this.pursuer.distanceToSqr(this.targetX, this.targetY, this.targetZ);
            if (--this.repath <= 0 && (
                    this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D ||
                            target.distanceToSqr(this.targetX, this.targetY, this.targetZ) >= 0.5D) ||
                    this.pursuer.getNavigation().isDone()
            ) {
                this.targetX = target.getX();
                this.targetY = target.getY();
                this.targetZ = target.getZ();
                this.repath = 4 + this.pursuer.getRandom().nextInt(7);
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
        pursuer.getNavigation().stop();
    }

    private boolean moveMode(LivingEntity target){
        return this.pursuer.getNavigation().moveTo(target, 0.35);
    }
}
