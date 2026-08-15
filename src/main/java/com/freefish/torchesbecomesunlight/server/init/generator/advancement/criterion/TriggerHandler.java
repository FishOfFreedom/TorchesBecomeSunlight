package com.freefish.torchesbecomesunlight.server.init.generator.advancement.criterion;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraft.advancements.CriteriaTriggers;

public class TriggerHandler {
    // 1.21 起注册触发器要显式给出 id（原先由 CriterionTrigger#getId 提供）
    public static final ChainedAdvancementTrigger CHAINED_ADVANCEMENT_TRIGGER =
            CriteriaTriggers.register(TorchesBecomeSunlight.MOD_ID + ":chained_advancement", new ChainedAdvancementTrigger());
    public static final StringAdvancementTrigger STRING_ADVANCEMENT_TRIGGER =
            CriteriaTriggers.register(TorchesBecomeSunlight.MOD_ID + ":string_advancement", new StringAdvancementTrigger());

    public static void init() {}
}
