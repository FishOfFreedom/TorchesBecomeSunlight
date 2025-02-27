package com.freefish.torchesbecomesunlight.server.init.generator;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.mojang.serialization.Codec;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BiomeModifiersHandler
{
    public static final DeferredRegister<Codec<? extends BiomeModifier>> REG = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, TorchesBecomeSunlight.MOD_ID);
    public static final RegistryObject<Codec<? extends BiomeModifier>> TBS_MOB_SPAWNS = REG.register("torchesbecomesunlight_mob_spawns", MobSpawnBiomeModifier::makeCodec);
}
