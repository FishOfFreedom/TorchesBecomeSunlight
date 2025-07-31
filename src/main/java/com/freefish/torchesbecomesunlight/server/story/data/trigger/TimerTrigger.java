package com.freefish.torchesbecomesunlight.server.story.data.trigger;

import com.freefish.rosmontislib.sync.annotation.Persisted;
import com.freefish.torchesbecomesunlight.server.story.dialogueentity.DialogueEntity;

public class TimerTrigger extends Trigger {
    public static final String ID = "timer";

    @Persisted
    private int interval;

    @Override
    public void trigger(DialogueEntity dialogueEntity) {

    }

    @Override
    public String getRegisterID() {
        return ID;
    }
}