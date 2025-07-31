package com.freefish.torchesbecomesunlight.server.entity.ai.entity;

import com.freefish.torchesbecomesunlight.server.entity.dlc.PathfinderBallistarius;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

import static com.freefish.torchesbecomesunlight.server.util.animation.IAnimatedEntity.NO_ANIMATION;

public class PathfinderBallistariusAttackAI extends Goal {
    private final PathfinderBallistarius shieldGuard;

    private int repath;
    private double targetX;
    private double targetY;
    private double targetZ;
    private int timeSinceRun;
    private int timeSinceStomp;
    private int timeSinceStrengthen;
    private int timeSinceShoot;
    private int timeSinceShield;

    public PathfinderBallistariusAttackAI(PathfinderBallistarius shieldGuard) {
        this.shieldGuard = shieldGuard;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.shieldGuard.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public void start() {
        shieldGuard.setAggressive(true);
        shieldGuard.isShield = true;
        this.repath = 0;
    }

    @Override
    public void stop() {
        this.shieldGuard.getNavigation().stop();
        shieldGuard.isShield = false;
        shieldGuard.setAggressive(false);
    }

    @Override
    public void tick() {
        LivingEntity target = this.shieldGuard.getTarget();
        if (target == null) return;
        RandomSource random = shieldGuard.getRandom();

        timeSinceRun++;
        timeSinceStomp++;
        timeSinceStrengthen++;
        timeSinceShield++;
        timeSinceShoot++;

        if(!(this.shieldGuard.getAnimation() == NO_ANIMATION||shieldGuard.getAnimation()==PathfinderBallistarius.RUN)) return;
        walk();
        if(shieldGuard.getAnimation()==PathfinderBallistarius.RUN) return;

        double dist = this.shieldGuard.distanceTo(target);
        if(timeSinceStrengthen>=300){
            timeSinceStrengthen=0;
            AnimationActHandler.INSTANCE.sendAnimationMessage(this.shieldGuard, PathfinderBallistarius.STRENGTHEN);
        }
        else if(dist < 6 && timeSinceStomp>=160){
            timeSinceStomp=0;
            AnimationActHandler.INSTANCE.sendAnimationMessage(this.shieldGuard, PathfinderBallistarius.STOMP);
        }
        else if(dist < 2 + target.getBbWidth()/2 && timeSinceShield>=120){
            timeSinceShield=0;
            AnimationActHandler.INSTANCE.sendAnimationMessage(this.shieldGuard, PathfinderBallistarius.SHIELD);
        }
        else if(dist > 8 && timeSinceShoot>=120){
            timeSinceShoot=0;
            AnimationActHandler.INSTANCE.sendAnimationMessage(this.shieldGuard, PathfinderBallistarius.SHOOT);
        }
        else if(dist >= 10 &&timeSinceRun>=180) {
            targetX = targetY = targetZ = 0;
            timeSinceRun = 0;
            AnimationActHandler.INSTANCE.sendAnimationMessage(shieldGuard, PathfinderBallistarius.RUN);
        } else if (target.getY() - this.shieldGuard.getY() >= -1 && target.getY() - this.shieldGuard.getY() <= 3) {
            if (dist < 6D && Math.abs(MathUtils.wrapDegrees(this.shieldGuard.getAngleBetweenEntities(target, this.shieldGuard) - this.shieldGuard.yBodyRot)) < 35.0D) {
                float v = random.nextFloat();
                if(v<0.33&&dist<2){
                    AnimationActHandler.INSTANCE.sendAnimationMessage(this.shieldGuard, PathfinderBallistarius.ATTACK);
                }
                else if(v<0.66) {
                    AnimationActHandler.INSTANCE.sendAnimationMessage(this.shieldGuard, PathfinderBallistarius.ATTACK2);
                }
                else {
                    AnimationActHandler.INSTANCE.sendAnimationMessage(this.shieldGuard, PathfinderBallistarius.ATTACK3);
                }
            }
        }
    }

    private void walk(){
        LivingEntity target = shieldGuard.getTarget();
        if(target!=null) {
            double dist = this.shieldGuard.distanceToSqr(this.targetX, this.targetY, this.targetZ);
            if (--this.repath <= 0 && (
                    this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D ||
                            target.distanceToSqr(this.targetX, this.targetY, this.targetZ) >= 1.0D) ||
                    this.shieldGuard.getNavigation().isDone()
            ) {
                this.targetX = target.getX();
                this.targetY = target.getY();
                this.targetZ = target.getZ();
                this.repath = 4 + this.shieldGuard.getRandom().nextInt(7);
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
        if(shieldGuard.getAnimation()==PathfinderBallistarius.RUN)
            return this.shieldGuard.getNavigation().moveTo(target, 0.8);
        else
            return this.shieldGuard.getNavigation().moveTo(target, 0.4);
    }
}
