package com.freefish.torchesbecomesunlight.server.entity.effect.dialogueentity;

import com.freefish.torchesbecomesunlight.server.story.dialogue.Dialogue;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationAct;
import net.minecraft.world.entity.LivingEntity;

public interface IAwardEntity {
    void setIsAward(LivingEntity isAward);

    LivingEntity getIsAward();

    AnimationAct getAwardAnimation();
}
