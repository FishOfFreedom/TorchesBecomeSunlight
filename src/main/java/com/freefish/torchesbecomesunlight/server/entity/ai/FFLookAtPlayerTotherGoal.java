package com.freefish.torchesbecomesunlight.server.entity.ai;

import com.freefish.torchesbecomesunlight.server.util.FFEntityUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class FFLookAtPlayerTotherGoal<T extends PathfinderMob> extends Goal {
    protected final T mob;
    @Nullable
    protected Entity lookAt;
    protected final float lookDistance;
    private int lookTime;
    private final boolean onlyHorizontal;
    protected final Class<? extends LivingEntity> lookAtType;
    protected final TargetingConditions lookAtContext;

    public FFLookAtPlayerTotherGoal(T pMob, Class<? extends LivingEntity> pLookAtType, float pLookDistance) {
        this(pMob, pLookAtType, pLookDistance, false);
        this.setFlags(EnumSet.of(Flag.MOVE,Flag.LOOK));
    }

    public FFLookAtPlayerTotherGoal(T pMob, Class<? extends LivingEntity> pLookAtType, float pLookDistance, boolean pOnlyHorizontal) {
        this.mob = pMob;
        this.lookAtType = pLookAtType;
        this.lookDistance = pLookDistance;
        this.onlyHorizontal = pOnlyHorizontal;
        this.setFlags(EnumSet.of(Flag.LOOK));
        if (pLookAtType == Player.class) {
            this.lookAtContext = TargetingConditions.forNonCombat().range((double)pLookDistance).selector((p_25531_) -> {
                return EntitySelector.notRiding(pMob).test(p_25531_);
            });
        } else {
            this.lookAtContext = TargetingConditions.forNonCombat().range((double)pLookDistance);
        }

    }

    public boolean canUse() {
        if(mob.getTarget()!=null&&mob.isAlive()){
            return false;
        }
        if (this.mob.getTarget() != null) {
            this.lookAt = this.mob.getTarget();
        }

        if (this.lookAtType == Player.class) {
            this.lookAt = this.mob.level().getNearestPlayer(this.lookAtContext, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
        } else {
            this.lookAt = this.mob.level().getNearestEntity(this.mob.level().getEntitiesOfClass(this.lookAtType, this.mob.getBoundingBox().inflate((double)this.lookDistance, 3.0, (double)this.lookDistance), (p_148124_) -> {
                return true;
            }), this.lookAtContext, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
        }

        return this.lookAt instanceof LivingEntity l&& FFEntityUtils.isLookingAtMe(mob,l);
    }

    public boolean canContinueToUse() {
        if(mob.getTarget()!=null){
            return false;
        }
        if (!this.lookAt.isAlive()) {
            return false;
        } else if (this.mob.distanceToSqr(this.lookAt) > (double)(this.lookDistance * this.lookDistance)) {
            return false;
        } else {
            return this.lookTime > 0;
        }
    }

    public void start() {
        this.lookTime = 100;
    }

    public void stop() {
        this.lookAt = null;
    }

    public void tick() {
        if (this.lookAt.isAlive()) {
            double $$0 = this.onlyHorizontal ? this.mob.getEyeY() : this.lookAt.getEyeY();
            this.mob.getLookControl().setLookAt(this.lookAt.getX(), $$0, this.lookAt.getZ());
            --this.lookTime;
        }
    }
}
