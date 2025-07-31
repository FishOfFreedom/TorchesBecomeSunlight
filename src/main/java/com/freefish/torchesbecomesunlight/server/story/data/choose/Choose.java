package com.freefish.torchesbecomesunlight.server.story.data.choose;

import com.freefish.rosmontislib.sync.IPersistedSerializable;
import com.freefish.torchesbecomesunlight.server.story.dialogueentity.DialogueEntity;

public abstract class Choose implements IPersistedSerializable {
    public abstract String changeDialogue(DialogueEntity dialogueEntity);

    public abstract String getRegisterID();
}
