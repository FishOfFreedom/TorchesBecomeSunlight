package com.freefish.torchesbecomesunlight.server.entity.dlc.ai;

import com.freefish.torchesbecomesunlight.server.block.blockentity.BigBenBlockEntity;
import com.freefish.torchesbecomesunlight.server.entity.dlc.GunKnightEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class GunKnightRandomStrollGoal<T extends GunKnightEntity> extends RandomStrollGoal {
    public static final float PROBABILITY = 0.001F;
    protected final float probability;
    T mob;

    public GunKnightRandomStrollGoal(T pMob, double pSpeedModifier) {
        this(pMob, pSpeedModifier, 0.001F);
        mob = pMob;
    }

    public GunKnightRandomStrollGoal(T pMob, double pSpeedModifier, float pProbability) {
        super(pMob, pSpeedModifier);
        this.probability = pProbability;
        mob = pMob;
    }

    public boolean canUse() {
        BigBenBlockEntity tile = mob.getTile();
        if(tile !=null){
            if(tile.challengePlayer!=null&&tile.challengePlayer.isAlive()){
                return false;
            }
        }
        return mob.getTarget() == null&&mob.isAlive() && super.canUse();
    }

    @Nullable
    protected Vec3 getPosition() {
        if (this.mob.isInWaterOrBubble()) {
            Vec3 $$0 = LandRandomPos.getPos(this.mob, 15, 7);
            return $$0 == null ? super.getPosition() : $$0;
        } else {
            Vec3 pos = this.mob.getRandom().nextFloat() >= this.probability ? LandRandomPos.getPos(this.mob, 10, 7) : super.getPosition();
            BlockPos spawnPos = mob.getSpawnPos();
            if(spawnPos!=null&&pos!=null&&pos.distanceTo(new Vec3(spawnPos.getX(),spawnPos.getY(),spawnPos.getZ()))>10){
                return mob.position();
            }
            return pos;
        }
    }
}