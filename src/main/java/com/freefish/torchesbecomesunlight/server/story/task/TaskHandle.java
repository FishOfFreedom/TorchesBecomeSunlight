package com.freefish.torchesbecomesunlight.server.story.task;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class TaskHandle{
    public static final BiMap<String,TaskType<?>> STRING_TASK_TYPE = HashBiMap.create();

    public static final KillEntityTaskType KILL_ENTITY_TASK = new KillEntityTaskType(KillEntityTask::new);

    static {
        STRING_TASK_TYPE.put("kill_entity",KILL_ENTITY_TASK);
    }
}
