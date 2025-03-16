package com.freefish.torchesbecomesunlight.server.entity;

import com.freefish.torchesbecomesunlight.server.story.dialogue.Dialogue;
import net.minecraft.world.entity.LivingEntity;

public interface IDialogueEntity {
    int dialogueNeedTime = 1;

    public Dialogue getDialogue();

    LivingEntity getDialogueEntity();

    void setDialogueEntity(LivingEntity dialogueEntity);

    boolean getHasDialogue();

    default void dialogueTick(){
    }
}
