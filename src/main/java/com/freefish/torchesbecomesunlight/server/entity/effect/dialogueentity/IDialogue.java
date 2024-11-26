package com.freefish.torchesbecomesunlight.server.entity.effect.dialogueentity;

import com.freefish.torchesbecomesunlight.server.story.dialogue.Dialogue;
import net.minecraft.world.entity.LivingEntity;

public interface IDialogue {
    public Dialogue getDialogue();

    LivingEntity getDialogueEntity();

    void setDialogueEntity(LivingEntity dialogueEntity);

    boolean getHasDialogue();
}
