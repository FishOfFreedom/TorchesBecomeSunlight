package com.freefish.torchesbecomesunlight.server.init.generator.advancement.criterion;

import net.minecraft.advancements.CriteriaTriggers;

public class TriggerHandler {
    public static final ChainedAdvancementTrigger CHAINED_ADVANCEMENT_TRIGGER = CriteriaTriggers.register(new ChainedAdvancementTrigger());
    public static final StringAdvancementTrigger STRING_ADVANCEMENT_TRIGGER = CriteriaTriggers.register(new StringAdvancementTrigger());

    public static void init() {}
}
