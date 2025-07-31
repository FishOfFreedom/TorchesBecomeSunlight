package com.freefish.torchesbecomesunlight.server.world.structure.processor;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

public class ProcessorHandler {
    public static StructureProcessorType<BaseDecoProcessor> BASE_DECO_PROCESSOR = () -> BaseDecoProcessor.CODEC;

    public static void registerStructureProcessors() {
        register("base_deco_processor", BASE_DECO_PROCESSOR);
    }

    private static void register(String name, StructureProcessorType<?> codec) {
        Registry.register(BuiltInRegistries.STRUCTURE_PROCESSOR, new ResourceLocation(TorchesBecomeSunlight.MOD_ID, name), codec);
    }
}