package com.freefish.torchesbecomesunlight.server.entity.ai.entity.snownova;

import com.freefish.torchesbecomesunlight.server.effect.forceeffect.ForceEffectHandle;
import com.freefish.torchesbecomesunlight.server.effect.forceeffect.ForceEffectInstance;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster.FrostNova;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class SnowNova1AttackAI extends Goal {
    private final FrostNova mob;
    private int timeSinceDash;
    private int timeSinceIceGround;
    private int timeSinceIceWind;
    private int timeSinceIceBlade;
    private int timeSinceLula;
    private int state1remoteIn;

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

        final float cycleSpeed = 10f;
        int state = mob.getState();
        timeSinceDash++;
        mob.timeSinceIceBomb++;
        timeSinceIceWind++;
        timeSinceIceBlade++;
        state1remoteIn++;

        if(state==1){
            timeSinceLula++;
            timeSinceIceGround++;

        }

        if(!(mob.getAnimation()== FrostNova.NO_ANIMATION||mob.getAnimation()== FrostNova.DASH_RUN)) return;
        if(mob.getAnimation()== FrostNova.DASH_RUN){
            walk();
            return;
        }

        ForceEffectInstance data = ForceEffectHandle.getForceEffect(target, ForceEffectHandle.FROZEN_FORCE_EFFECT);
        if(data!=null&&data.getLevel()>1) {
            timeSinceDash += 4;
            timeSinceIceBlade +=4;
        }

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
            if(state ==1 && distToTarget <= 20 &&timeSinceDash>=220) {
                timeSinceDash = 0;
                AnimationActHandler.INSTANCE.sendAnimationMessage(mob, FrostNova.DASH_RUN);
            } else if(distToTarget <=5 && mob.timeSinceIceJump_1>=120){
                mob.timeSinceIceJump_1 = 0;
                AnimationActHandler.INSTANCE.sendAnimationMessage(mob, FrostNova.ICE_JUMP);
            } else if(distToTarget <=3 && mob.timeSinceIceBomb>=100){
                mob.timeSinceIceBomb = 0;
                if(state ==0) {
                    mob.timeSinceIceBomb = 50;
                    AnimationActHandler.INSTANCE.sendAnimationMessage(mob, FrostNova.REMOTE_2);
                }
                else
                    AnimationActHandler.INSTANCE.sendAnimationMessage(mob, FrostNova.ICE_BOMB);
            }else if(state ==1 && timeSinceIceGround>=200){
                timeSinceIceGround = 0;
                AnimationActHandler.INSTANCE.sendAnimationMessage(mob, FrostNova.ICE_GROUND);
            }else if(state ==1 && timeSinceLula>=300){
                timeSinceLula = 0;
                AnimationActHandler.INSTANCE.sendAnimationMessage(mob, FrostNova.LULLABYE_2);
            }
            else if( distToTarget < 9.5 && timeSinceIceWind>=200){
                timeSinceIceWind = 0;
                AnimationActHandler.INSTANCE.sendAnimationMessage(mob, FrostNova.ICE_WIND);
            }
            else if(state ==1&&distToTarget >= 10 && timeSinceIceBlade>=500){
                timeSinceIceBlade = 0;
                AnimationActHandler.INSTANCE.sendAnimationMessage(mob, FrostNova.REMOTE_3);
            }
            if(state ==0) {
                if (distToTarget <= 2 + target.getBbWidth()/2) {
                    if (random.nextFloat() < 0.5)
                        AnimationActHandler.INSTANCE.sendAnimationMessage(mob, FrostNova.ATTACK_1);
                    else
                        AnimationActHandler.INSTANCE.sendAnimationMessage(mob, FrostNova.ATTACK_2);
                }
            }
            else {
                if(distToTarget <=9&&mob.timeSinceBackJump>=200) {
                    mob.timeSinceBackJump = 0;
                    AnimationActHandler.INSTANCE.sendAnimationMessage(mob, FrostNova.BACK_JUMP);
                }
                else if (distToTarget <= 13&&!mob.isInIceWall()&&state1remoteIn>=40) {
                    state1remoteIn=0;
                    AnimationActHandler.INSTANCE.sendAnimationMessage(mob, random.nextBoolean()?FrostNova.REMOTE_12:FrostNova.REMOTE_1);
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
            moveMode(target);
        }
    }

    private boolean moveMode(LivingEntity target){
        if(mob.getAnimation()== FrostNova.DASH_RUN)
            return this.mob.getNavigation().moveTo(target, 1.25);
        else  {
            if (mob.getState() == 0)
                return this.mob.getNavigation().moveTo(target, 0.65);
            else
                return this.mob.getNavigation().moveTo(target, 0.35);
        }
    }

    private void moveMode(Vec3 vec3){
        if(mob.getState()==0) {
            this.mob.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, 0.65);
        } else {
            this.mob.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, 0.35);
        }
    }
}
