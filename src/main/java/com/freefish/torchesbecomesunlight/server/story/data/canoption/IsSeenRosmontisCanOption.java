package com.freefish.torchesbecomesunlight.server.story.data.canoption;

import com.freefish.rosmontislib.sync.annotation.Persisted;
import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.PlayerCapability;
import com.freefish.torchesbecomesunlight.server.story.dialogueentity.DialogueEntity;
import net.minecraft.world.entity.LivingEntity;

public  class IsSeenRosmontisCanOption extends CanOption {
    public static final String ID = "is_seen_rosmontis";
    @Persisted
    public String speakid;

    public boolean canOption(DialogueEntity dialogueEntity){
        LivingEntity chatEntities = dialogueEntity.getChatEntities(speakid);
        PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(chatEntities, CapabilityHandle.PLAYER_CAPABILITY);
        return chatEntities != null&&capability!=null&&capability.getPlayerStory().isSeenRosmontis();
    }

    @Override
    public String getRegisterID() {
        return ID;
    }
}
