package com.freefish.torchesbecomesunlight.server.world.structure.processor;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 1.21 的 STRUCTURE_PROCESSOR 注册表在 bootstrap 后即冻结，
 * 原先在 commonSetup 里调 Registry.register 会撞上「Registry is already frozen」，
 * 故改走 DeferredRegister。
 */
public class ProcessorHandler {
    public static final DeferredRegister<StructureProcessorType<?>> PROCESSORS =
            DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, TorchesBecomeSunlight.MOD_ID);

    public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<BaseDecoProcessor>> BASE_DECO_PROCESSOR =
            PROCESSORS.register("base_deco_processor", () -> () -> BaseDecoProcessor.CODEC);

    public static void register(IEventBus bus) {
        PROCESSORS.register(bus);
    }

    public static void registerStructureProcessors() {}
}
