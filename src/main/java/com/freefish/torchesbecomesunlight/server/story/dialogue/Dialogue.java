package com.freefish.torchesbecomesunlight.server.story.dialogue;

import net.minecraft.world.entity.Entity;

import java.util.List;

public class Dialogue {
    public int getIndex() {
        return index;
    }

    private final int index;

    private final String message;

    public String getMessage() {
        return message;
    }

    public List<DialogueTrigger> getOptions() {
        return options;
    }

    private final List<DialogueTrigger> options;

    public Dialogue(String message, List<DialogueTrigger> options, Dialogue nextDialogue,int speakerNumber) {
        this.message = message;
        this.options = options;
        this.nextDialogue = nextDialogue;
        this.speakerNumber =speakerNumber;

        DialogueStore.dialogueList.add(this);
        index = DialogueStore.dialogueAmount++;
    }

    public Dialogue getNextDialogue() {
        return nextDialogue;
    }

    private final Dialogue nextDialogue;

    public int getSpeakerNumber() {
        return speakerNumber;
    }

    private final int speakerNumber;
}
