package com.freefish.torchesbecomesunlight.server.init.generator.advancement.criterion;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 1.21 起 TRIGGER_TYPE 注册表会在 bootstrap 后冻结，
 * 原先在静态初始化里调 CriteriaTriggers.register 会撞上「Registry is already frozen」，
 * 故改走 DeferredRegister。
 */
public class TriggerHandler {
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS =
            DeferredRegister.create(Registries.TRIGGER_TYPE, TorchesBecomeSunlight.MOD_ID);

    public static final DeferredHolder<CriterionTrigger<?>, ChainedAdvancementTrigger> CHAINED_ADVANCEMENT_TRIGGER =
            TRIGGERS.register("chained_advancement", ChainedAdvancementTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, StringAdvancementTrigger> STRING_ADVANCEMENT_TRIGGER =
            TRIGGERS.register("string_advancement", StringAdvancementTrigger::new);

    public static void register(IEventBus bus) {
        TRIGGERS.register(bus);
    }

    public static void init() {}
}
