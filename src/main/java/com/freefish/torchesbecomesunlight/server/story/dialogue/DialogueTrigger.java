package com.freefish.torchesbecomesunlight.server.story.dialogue;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class DialogueTrigger{
    private String content = "";
    private final int number;
    private final DialogueTriggerAction action;
    private Dialogue nextDialogue;
    private final DDialogue dDialogue;

    public DialogueTrigger(String content,int number,DialogueTriggerAction action) {
        this.content = content;
        this.number = number;
        this.action = action;
        this.nextDialogue = null;
        dDialogue = null;
    }

    public DialogueTrigger(String content,int number,Dialogue nextDialogue,DialogueTriggerAction action) {
        this.content = content;
        this.number = number;
        this.action = action;
        this.nextDialogue = nextDialogue;
        dDialogue = null;
    }

    public DialogueTrigger(String content,int number,Dialogue nextDialogue,DialogueTriggerAction action,DDialogue dDialogue) {
        this.content = content;
        this.number = number;
        this.action = action;
        this.nextDialogue = nextDialogue;
        this.dDialogue = dDialogue;
    }

    public String getContent() {
        return Component.translatable(content).getString();
    }

    public boolean getHasTrigger(){
        return action != null;
    }

    public int getNumber(){
        return number;
    }

    public Dialogue getNextDialogue(){
        return nextDialogue;
    }

    public void trigger(LivingEntity entity) {
        if (this.action != null) {
            this.action.trigger(entity);
        }
    }

    public void chooseDialogue(Entity entity) {
        if (this.dDialogue != null) {
            nextDialogue = dDialogue.trigger(entity);
        }
    }
}

@FunctionalInterface
interface DialogueTriggerAction {
    void trigger(LivingEntity entity);
}

@FunctionalInterface
interface DDialogue {
    Dialogue trigger(Entity entity);
}
