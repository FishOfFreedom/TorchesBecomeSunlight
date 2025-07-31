package com.freefish.torchesbecomesunlight.server.story.data.canoption;

import com.freefish.rosmontislib.sync.IPersistedSerializable;
import com.freefish.torchesbecomesunlight.server.story.dialogueentity.DialogueEntity;

public abstract class CanOption implements IPersistedSerializable{
    public abstract boolean canOption(DialogueEntity dialogueEntity);

    public abstract String getRegisterID();
}
