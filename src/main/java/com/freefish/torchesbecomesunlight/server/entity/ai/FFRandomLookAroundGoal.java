package com.freefish.torchesbecomesunlight.server.entity.ai;

import com.freefish.torchesbecomesunlight.server.entity.AnimatedEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class FFRandomLookAroundGoal<T extends AnimatedEntity> extends Goal {
    private final T mob;
    private double relX;
    private double relZ;
    private int lookTime;

    public FFRandomLookAroundGoal(T pMob) {
        this.mob = pMob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public boolean canUse() {
        if(mob.getTarget()!=null&&mob.isAlive()){
            return false;
        }
        if(!mob.canLookAnimation()){
            return false;
        }
        return this.mob.getRandom().nextFloat() < 0.02F;
    }

    public boolean canContinueToUse() {
        if(mob.getTarget()!=null){
            return false;
        }
        if(!mob.canLookAnimation()){
            return false;
        }
        return this.lookTime >= 0;
    }

    public void start() {
        double $$0 = 6.283185307179586 * this.mob.getRandom().nextDouble();
        this.relX = Math.cos($$0);
        this.relZ = Math.sin($$0);
        this.lookTime = 20 + this.mob.getRandom().nextInt(20);
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void tick() {
        --this.lookTime;
        this.mob.getLookControl().setLookAt(this.mob.getX() + this.relX, this.mob.getEyeY(), this.mob.getZ() + this.relZ);
    }
}
