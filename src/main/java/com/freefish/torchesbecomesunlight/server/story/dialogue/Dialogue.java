package com.freefish.torchesbecomesunlight.server.story.dialogue;

import net.minecraft.world.entity.Entity;

import java.util.List;

public class Dialogue {
    public int getIndex() {
        return index;
    }

    private final int index;

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DialogueTrigger> getOptions() {
        return options;
    }

    private final List<DialogueTrigger> options;

    private final DMessage dMessage;

    public Dialogue(String message, List<DialogueTrigger> options, Dialogue nextDialogue,int speakerNumber) {
        this.message = message;
        this.options = options;
        this.nextDialogue = nextDialogue;
        this.speakerNumber =speakerNumber;

        DialogueStore.dialogueList.add(this);
        index = DialogueStore.dialogueAmount++;
        dMessage = null;
    }

    public Dialogue(DMessage dMessage, List<DialogueTrigger> options, Dialogue nextDialogue,int speakerNumber) {
        this.message = "";
        this.options = options;
        this.nextDialogue = nextDialogue;
        this.speakerNumber =speakerNumber;

        DialogueStore.dialogueList.add(this);
        index = DialogueStore.dialogueAmount++;
        this.dMessage = dMessage;
    }

    public Dialogue getNextDialogue() {
        return nextDialogue;
    }

    private final Dialogue nextDialogue;

    public int getSpeakerNumber() {
        return speakerNumber;
    }

    private final int speakerNumber;

    public String  trigger(Entity entity) {
        if (this.dMessage != null) {
             return this.dMessage.trigger(entity);
        }
        return null;
    }
}

@FunctionalInterface
interface DMessage {
    String trigger(Entity entity);
}
