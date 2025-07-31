package com.freefish.torchesbecomesunlight.server.story.data.generatext;

import com.freefish.rosmontislib.sync.annotation.Persisted;
import com.freefish.torchesbecomesunlight.server.story.dialogueentity.DialogueEntity;

public class FindVillagerGeneratext extends Generatext{
    public static final String ID = "find_villager";
    @Persisted
    String speakerid;

    @Override
    public String generaText(DialogueEntity dialogueEntity) {
        if(dialogueEntity != null){
            return dialogueEntity.getChatEntities(speakerid).getName().getString();
        }
        return "null";
    }

    @Override
    public String getRegisterID() {
        return ID;
    }
}
