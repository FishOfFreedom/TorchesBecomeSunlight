package com.freefish.torchesbecomesunlight.server.story.data.trigger;

import com.freefish.rosmontislib.sync.IPersistedSerializable;
import com.freefish.torchesbecomesunlight.server.story.dialogueentity.DialogueEntity;

public abstract class Trigger implements IPersistedSerializable{
    public abstract void trigger(DialogueEntity dialogueEntity);

    public abstract String getRegisterID();
}