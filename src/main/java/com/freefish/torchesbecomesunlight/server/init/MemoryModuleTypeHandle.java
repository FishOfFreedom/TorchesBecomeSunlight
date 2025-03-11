package com.freefish.torchesbecomesunlight.server.init;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.mojang.serialization.Codec;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;

public class MemoryModuleTypeHandle {
    public static final DeferredRegister<MemoryModuleType<?>> MEMORY_MODULES = DeferredRegister.create(ForgeRegistries.MEMORY_MODULE_TYPES,TorchesBecomeSunlight.MOD_ID);

    public static final RegistryObject<MemoryModuleType<Boolean>> MEMORY_IS_HOME = registry("at_home",Codec.BOOL);
    public static final RegistryObject<MemoryModuleType<Boolean>> MEMORY_IS_WORK = registry("at_work",Codec.BOOL);

    private static <U> RegistryObject<MemoryModuleType<U>> registry(String name, Codec<U> codec){
        return MEMORY_MODULES.register(name,()->
                new MemoryModuleType<>(Optional.of(codec)));
    }
}
