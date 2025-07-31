package com.freefish.torchesbecomesunlight.server.story.data.canoption;

import com.freefish.rosmontislib.sync.annotation.Persisted;
import com.freefish.torchesbecomesunlight.server.story.dialogueentity.DialogueEntity;
import net.minecraft.world.entity.LivingEntity;

public  class HealthLargerCanOption extends CanOption {
    public static final String ID = "health_larger";
    @Persisted
    public String speakid;
    @Persisted
    public int health;

    public boolean canOption(DialogueEntity dialogueEntity){
        LivingEntity chatEntities = dialogueEntity.getChatEntities(speakid);
        return chatEntities != null && chatEntities.getHealth() > health;
    }

    @Override
    public String getRegisterID() {
        return ID;
    }
}
