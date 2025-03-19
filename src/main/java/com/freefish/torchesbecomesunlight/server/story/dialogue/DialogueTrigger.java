package com.freefish.torchesbecomesunlight.server.story.dialogue;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;
import java.util.function.Function;

public class DialogueTrigger{
    private String content = "";
    private final int number;
    private final Consumer<LivingEntity> action;
    private Dialogue nextDialogue;
    private final Function<Entity,Dialogue> dDialogue;
    private final Function<Entity,Boolean> cDialogue;

    public DialogueTrigger(String content,int number,Consumer<LivingEntity> action) {
        this.content = content;
        this.number = number;
        this.action = action;
        this.nextDialogue = null;
        dDialogue = null;
        cDialogue = null;
    }

    public DialogueTrigger(String content,int number,Dialogue nextDialogue,Consumer<LivingEntity> action) {
        this.content = content;
        this.number = number;
        this.action = action;
        this.nextDialogue = nextDialogue;
        dDialogue = null;
        cDialogue = null;
    }

    public DialogueTrigger(String content,int number,Dialogue nextDialogue,Consumer<LivingEntity> action,Function<Entity,Dialogue> dDialogue) {
        this.content = content;
        this.number = number;
        this.action = action;
        this.nextDialogue = nextDialogue;
        this.dDialogue = dDialogue;
        cDialogue = null;
    }

    public DialogueTrigger(String content,int number,Dialogue nextDialogue,Consumer<LivingEntity> action,Function<Entity,Dialogue> dDialogue,Function<Entity,Boolean> cDialogue) {
        this.content = content;
        this.number = number;
        this.action = action;
        this.nextDialogue = nextDialogue;
        this.dDialogue = dDialogue;
        this.cDialogue = cDialogue;
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
            this.action.accept(entity);
        }
    }

    public boolean isNoSend(LivingEntity entity) {
        if (this.cDialogue != null) {
            return this.cDialogue.apply(entity);
        }
        return false;
    }

    public void chooseDialogue(Entity entity) {
        if (this.dDialogue != null) {
            nextDialogue = dDialogue.apply(entity);
        }
    }
}
