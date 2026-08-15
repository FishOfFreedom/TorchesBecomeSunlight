package com.freefish.torchesbecomesunlight.server.init.village;

import net.minecraft.core.registries.BuiltInRegistries;
import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.mojang.serialization.Codec;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Optional;

public class MemoryModuleTypeHandle {
    public static final DeferredRegister<MemoryModuleType<?>> MEMORY_MODULES = DeferredRegister.create(BuiltInRegistries.MEMORY_MODULE_TYPE,TorchesBecomeSunlight.MOD_ID);

    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<GlobalPos>> ARMOR_STAND_POS = registry("armor_stand_pos",GlobalPos.CODEC);
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Boolean>> BEHAVIOR_RUN_ONE = registry("behavior_run_one",Codec.BOOL);

    private static <U> DeferredHolder<MemoryModuleType<?>, MemoryModuleType<U>> registry(String name, Codec<U> codec){
        return MEMORY_MODULES.register(name,()->
                new MemoryModuleType<>(Optional.of(codec)));
    }
}
