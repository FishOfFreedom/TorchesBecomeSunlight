package com.freefish.torchesbecomesunlight.server.init.village;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.mojang.serialization.Codec;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;

public class MemoryModuleTypeHandle {
    public static final DeferredRegister<MemoryModuleType<?>> MEMORY_MODULES = DeferredRegister.create(ForgeRegistries.MEMORY_MODULE_TYPES,TorchesBecomeSunlight.MOD_ID);

    public static final RegistryObject<MemoryModuleType<GlobalPos>> ARMOR_STAND_POS = registry("armor_stand_pos",GlobalPos.CODEC);
    public static final RegistryObject<MemoryModuleType<Boolean>> BEHAVIOR_RUN_ONE = registry("behavior_run_one",Codec.BOOL);

    private static <U> RegistryObject<MemoryModuleType<U>> registry(String name, Codec<U> codec){
        return MEMORY_MODULES.register(name,()->
                new MemoryModuleType<>(Optional.of(codec)));
    }
}
