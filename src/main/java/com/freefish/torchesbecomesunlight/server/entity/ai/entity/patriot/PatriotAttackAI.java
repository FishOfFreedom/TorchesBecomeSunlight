package com.freefish.torchesbecomesunlight.server.entity.ai.entity.patriot;


import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.shield.Patriot;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

import static com.freefish.torchesbecomesunlight.server.util.animation.IAnimatedEntity.NO_ANIMATION;

public class PatriotAttackAI extends Goal {
    private final Patriot patriot;

    private int timeSinceRun;
    private int timeSinceStomp;
    private int timeSinceStrengthen;
    private int timeSinceHunt;
    private int timeSinceShield;

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

        timeSinceRun++;
        patriot.timeSinceThrow++;
        timeSinceStomp++;
        timeSinceStrengthen++;
        timeSinceHunt++;
        timeSinceShield++;

        if(!(this.patriot.getAnimation() == NO_ANIMATION||patriot.getAnimation()==Patriot.RUN)) return;
        walk();
        if(patriot.getAnimation()==Patriot.RUN) return;

        double dist = this.patriot.distanceTo(target);

        if(timeSinceStrengthen>=300){
            timeSinceStrengthen=0;
            AnimationActHandler.INSTANCE.sendAnimationMessage(this.patriot, Patriot.STRENGTHEN);
        }
        else if (dist > 8  && patriot.timeSinceThrow > 440) {
            patriot.timeSinceThrow = 0;
            AnimationActHandler.INSTANCE.sendAnimationMessage(this.patriot, Patriot.THROW);
        }
        else if(dist < 8 && timeSinceStomp>=160){
            timeSinceStomp=0;
            AnimationActHandler.INSTANCE.sendAnimationMessage(this.patriot, Patriot.STOMP);
        }
        else if(dist < 2 + target.getBbWidth()/2 && timeSinceShield>=120){
            timeSinceShield=0;
            AnimationActHandler.INSTANCE.sendAnimationMessage(this.patriot, Patriot.SHIELD);
        }
        else if(dist < 14 &&timeSinceHunt>=320){
            timeSinceHunt=0;
            AnimationActHandler.INSTANCE.sendAnimationMessage(this.patriot, Patriot.HUNT);
        }
        else if(dist >= 12 &&timeSinceRun>=180) {
            timeSinceRun = 0;
            AnimationActHandler.INSTANCE.sendAnimationMessage(patriot, Patriot.RUN);
        } else if (target.getY() - this.patriot.getY() >= -1 && target.getY() - this.patriot.getY() <= 3) {
            if (dist < 4D  && Math.abs(MathUtils.wrapDegrees(this.patriot.getAngleBetweenEntities(target, this.patriot) - this.patriot.yBodyRot)) < 35.0D) {
                double rand = random.nextDouble();
                if (rand >= 0.6)
                    AnimationActHandler.INSTANCE.sendAnimationMessage(this.patriot, Patriot.ATTACK1);
                else if (rand >= 0.3)
                    AnimationActHandler.INSTANCE.sendAnimationMessage(this.patriot, Patriot.PIERCE2);
                else {
                    AnimationActHandler.INSTANCE.sendAnimationMessage(this.patriot, Patriot.PROPEL1);
                }
            }
        }
    }

    private void walk(){
        LivingEntity target = patriot.getTarget();
        if(target!=null) {
            moveMode(target);
        }
    }

    private boolean moveMode(LivingEntity target){
        if(patriot.getAnimation()==Patriot.RUN)
            return this.patriot.getNavigation().moveTo(target, 0.45);
        else
            return this.patriot.getNavigation().moveTo(target, 0.19);
    }
}
