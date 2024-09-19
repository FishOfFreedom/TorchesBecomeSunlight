package com.freefish.torchesbecomesunlight.server.entity.ai.entity.snownova;

import com.freefish.torchesbecomesunlight.server.animation.AnimationActHandler;
import com.freefish.torchesbecomesunlight.server.animation.IAnimatedEntity;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster.SnowNova1;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class SnowNovaAttackAI extends Goal {
    private final SnowNova1 mob;

    public SnowNovaAttackAI(SnowNova1 mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return mob.getTarget() != null&&mob.getTarget().isAlive()&&mob.getAnimation()== SnowNova1.NO_ANIMATION;
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
        RandomSource random = mob.getRandom();
        final float isRightOrLeft = 1.5f;

        if (target != null) {
            double distToTarget = this.mob.distanceTo(target);
            if(mob.cycleTime > 0){
                Vec3 cycle = mob.updateCyclePosition(0.1f);
                this.mob.getNavigation().stop();
                Vec3 position = mob.position();
                double dist = position.distanceTo(cycle);

                float strafeSpeed = (float) Math.min(1.0,dist/isRightOrLeft*0.5);
                if(true){//dist>isRightOrLeft
                    this.mob.getMoveControl().strafe(0, 0);
                    mob.getNavigation().moveTo(cycle.x,cycle.y,cycle.z,0.4);
                    mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
                }
                else {
                    if(mob.isRight)
                        mob.getMoveControl().strafe(0, strafeSpeed);
                    else
                        mob.getMoveControl().strafe(0, -strafeSpeed);
                    mob.lookAt(target, 30.0F, 30.0F);
                }
                ServerLevel level = (ServerLevel) mob.level();
                level.sendParticles(ParticleTypes.SMOKE,cycle.x,cycle.y,cycle.z,1,0,0,0,0);
            }
            else {
                if (distToTarget > 0.6) {
                    mob.getMoveControl().strafe(0, 0);
                    mob.getNavigation().moveTo(target, 0.3f);
                    mob.lookAt(target,10f,10f);
                }
                if (distToTarget <= 2) {
                    if (random.nextFloat() < 0.5)
                        AnimationActHandler.INSTANCE.sendAnimationMessage(mob, SnowNova1.ATTACK_1);
                    else
                        AnimationActHandler.INSTANCE.sendAnimationMessage(mob, SnowNova1.ATTACK_2);
                }
            }
        }
    }
}
