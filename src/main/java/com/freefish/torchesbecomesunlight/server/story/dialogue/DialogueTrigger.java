package com.freefish.torchesbecomesunlight.server.story.dialogue;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public class DialogueTrigger{
    private final String content;
    private final int number;
    private final DialogueTriggerAction action;
    private final Dialogue nextDialogue;

    public DialogueTrigger(String content,int number,DialogueTriggerAction action) {
        this.content = content;
        this.number = number;
        this.action = action;
        this.nextDialogue = null;
    }

    public DialogueTrigger(String content,int number,Dialogue nextDialogue,DialogueTriggerAction action) {
        this.content = content;
        this.number = number;
        this.action = action;
        this.nextDialogue = nextDialogue;
    }

    public String getContent() {
        return content;
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
}

@FunctionalInterface
interface DialogueTriggerAction {
    void trigger(LivingEntity entity);
}
