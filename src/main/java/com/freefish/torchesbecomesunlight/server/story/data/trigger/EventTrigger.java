package com.freefish.torchesbecomesunlight.server.story.data.trigger;

import com.freefish.rosmontislib.sync.annotation.Persisted;
import com.freefish.torchesbecomesunlight.server.story.dialogueentity.DialogueEntity;

public class EventTrigger extends Trigger {
    public static final String ID = "event";

    @Persisted
    public String id;

    @Override
    public void trigger(DialogueEntity dialogueEntity) {

    }

    @Override
    public String getRegisterID() {
        return ID;
    }
}