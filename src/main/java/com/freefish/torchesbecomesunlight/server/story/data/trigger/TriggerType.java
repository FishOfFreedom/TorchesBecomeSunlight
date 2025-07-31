package com.freefish.torchesbecomesunlight.server.story.data.trigger;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.function.Supplier;

public class TriggerType<T extends Trigger> {
    public static BiMap<String,TriggerType<?>> TRIGGER_TYPES = HashBiMap.create();

    static  {
        new TriggerType<>(EventTrigger.ID, EventTrigger::new);
        new TriggerType<>(TimerTrigger.ID, TimerTrigger::new);
    }

    private Supplier<T> supplier;

    public TriggerType(String s,Supplier<T> supplier) {
        this.supplier = supplier;
        TRIGGER_TYPES.put(s,this);
    }

    public T create(){
        return supplier.get();
    }
}