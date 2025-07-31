package com.freefish.torchesbecomesunlight.server.story.data.generatext;

import com.freefish.rosmontislib.sync.IPersistedSerializable;
import com.freefish.torchesbecomesunlight.server.story.dialogueentity.DialogueEntity;

public abstract class Generatext implements IPersistedSerializable {
    public abstract String generaText(DialogueEntity dialogueEntity);

    public abstract String getRegisterID();
}
