package com.freefish.torchesbecomesunlight.server.entity.ai;

import com.freefish.torchesbecomesunlight.server.entity.effect.dialogueentity.IDialogue;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class FFWaterAvoidingRandomStrollGoal<T extends PathfinderMob & IDialogue> extends RandomStrollGoal {
    public static final float PROBABILITY = 0.001F;
    protected final float probability;
    T mob;

    public FFWaterAvoidingRandomStrollGoal(T pMob, double pSpeedModifier) {
        this(pMob, pSpeedModifier, 0.001F);
        mob = pMob;
    }

    public FFWaterAvoidingRandomStrollGoal(T pMob, double pSpeedModifier, float pProbability) {
        super(pMob, pSpeedModifier);
        this.probability = pProbability;
        mob = pMob;
    }

    public boolean canUse() {
        return mob.getTarget() == null && mob.getDialogueEntity() == null&&super.canUse();
    }

    @Nullable
    protected Vec3 getPosition() {
        if (this.mob.isInWaterOrBubble()) {
            Vec3 $$0 = LandRandomPos.getPos(this.mob, 15, 7);
            return $$0 == null ? super.getPosition() : $$0;
        } else {
            return this.mob.getRandom().nextFloat() >= this.probability ? LandRandomPos.getPos(this.mob, 10, 7) : super.getPosition();
        }
    }
}
