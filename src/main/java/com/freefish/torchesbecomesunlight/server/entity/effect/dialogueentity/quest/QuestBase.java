package com.freefish.torchesbecomesunlight.server.entity.effect.dialogueentity.quest;

public abstract class QuestBase {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
}
