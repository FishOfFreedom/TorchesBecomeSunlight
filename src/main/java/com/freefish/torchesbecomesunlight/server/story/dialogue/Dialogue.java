package com.freefish.torchesbecomesunlight.server.story.dialogue;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.function.Function;

public class Dialogue {
    public int getIndex() {
        return index;
    }

    private final int index;

    private String message;

    public String getMessage() {
        return Component.translatable(message).getString();
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DialogueTrigger> getOptions() {
        return options;
    }

    private final List<DialogueTrigger> options;

    private final Function<Entity,String> dMessage;

    public Dialogue(String message, List<DialogueTrigger> options, Dialogue nextDialogue,int speakerNumber,int dialogueTime) {
        this.message = message;
        this.options = options;
        this.nextDialogue = nextDialogue;
        this.speakerNumber =speakerNumber;
        this.dialogueTime = dialogueTime;

        DialogueStore.dialogueList.add(this);
        index = DialogueStore.dialogueAmount++;
        dMessage = null;
    }

    public Dialogue(Function<Entity,String> dMessage, List<DialogueTrigger> options, Dialogue nextDialogue,int speakerNumber,int dialogueTime) {
        this.message = "";
        this.options = options;
        this.nextDialogue = nextDialogue;
        this.speakerNumber =speakerNumber;
        this.dialogueTime = dialogueTime;

        DialogueStore.dialogueList.add(this);
        index = DialogueStore.dialogueAmount++;
        this.dMessage = dMessage;
    }

    public Dialogue getNextDialogue() {
        return nextDialogue;
    }

    private Dialogue nextDialogue;

    public int getSpeakerNumber() {
        return speakerNumber;
    }

    private final int speakerNumber;

    public String  trigger(Entity entity) {
        if (this.dMessage != null) {
            String trigger = this.dMessage.apply(entity);
            setMessage(trigger);
            return trigger;
        }
        return null;
    }

    private final int dialogueTime;

    public int getDialogueTime() {
        return dialogueTime;
    }
}

