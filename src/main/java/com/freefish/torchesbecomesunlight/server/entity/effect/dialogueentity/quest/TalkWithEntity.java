package com.freefish.torchesbecomesunlight.server.entity.effect.dialogueentity.quest;

import com.freefish.torchesbecomesunlight.server.story.dialogue.Dialogue;
import net.minecraft.world.entity.Entity;

public class TalkWithEntity extends QuestBase{
    public TalkWithEntity(String name){
        setName(name);
    }

    public boolean isTalkEntity(Entity entity) {
        return false;
    }

    public Dialogue startDialogue(){
        return null;
    }
}
