package com.freefish.torchesbecomesunlight.server.partner.command.triggertype;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.function.Supplier;

public class TriggerBasicHandle {
    public static final BiMap<String,TriggerBasicType<?>> STRING_TRIGGER = HashBiMap.create();

    public static final TriggerBasicType<TargerTrigger> TARGER_TRIGGER = register(TargerTrigger.ID,TargerTrigger::new);
    public static final TriggerBasicType<BlockTrigger> BLOCK_TRIGGER = register(BlockTrigger.ID,BlockTrigger::new);

    public static<T extends TriggerBasic> TriggerBasicType<T> register(String id, Supplier<T> supplier){
        TriggerBasicType<T> tTriggerBasicType = new TriggerBasicType<>(supplier);
        STRING_TRIGGER.put(id,tTriggerBasicType);
        return tTriggerBasicType;
    }
}
