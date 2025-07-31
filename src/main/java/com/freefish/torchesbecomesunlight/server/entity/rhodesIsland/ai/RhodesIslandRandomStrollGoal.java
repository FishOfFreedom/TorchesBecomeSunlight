package com.freefish.torchesbecomesunlight.server.entity.rhodesIsland.ai;

import com.freefish.torchesbecomesunlight.server.entity.rhodesIsland.RhodesIslandEntity;
import com.freefish.torchesbecomesunlight.server.entity.rhodesIsland.rosmontis.Rosmontis;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class RhodesIslandRandomStrollGoal<T extends RhodesIslandEntity> extends RandomStrollGoal {
    public static final float PROBABILITY = 0.001F;
    protected final float probability;
    private int spawnPathTime;
    T mob;

    public RhodesIslandRandomStrollGoal(T pMob, double pSpeedModifier) {
        this(pMob, pSpeedModifier, 0.001F);
        mob = pMob;
    }

    public RhodesIslandRandomStrollGoal(T pMob, double pSpeedModifier, float pProbability) {
        super(pMob, pSpeedModifier);
        this.probability = pProbability;
        mob = pMob;
    }

    public boolean canUse() {
        if(mob instanceof Rosmontis r){
            if(r.isOnStoryGround){
                return false;
            }
        }

        return (mob.getTarget() == null||mob.getTarget().isAlive())&&mob.isAlive() && super.canUse();
    }

    @Nullable
    protected Vec3 getPosition() {
        if (this.mob.isInWaterOrBubble()) {
            Vec3 $$0 = LandRandomPos.getPos(this.mob, 15, 7);
            return $$0 == null ? super.getPosition() : $$0;
        } else {
            Vec3 pos = this.mob.getRandom().nextFloat() >= this.probability ? LandRandomPos.getPos(this.mob, 10, 7) : super.getPosition();
            BlockPos spawnPos = mob.getSpawnPos();
            if(spawnPos!=null&&pos!=null&&pos.distanceTo(new Vec3(spawnPos.getX(),spawnPos.getY(),spawnPos.getZ()))>20){
                spawnPathTime++;
                return new Vec3(spawnPos.getX()+0.5,spawnPos.getY(),spawnPos.getZ()+0.5);
            }
            if(pos!=null&& Math.abs(pos.y-mob.getY())>3){
                return null;
            }
            return pos;
        }
    }
}