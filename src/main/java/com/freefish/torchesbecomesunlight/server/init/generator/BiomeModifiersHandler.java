package com.freefish.torchesbecomesunlight.server.init.generator;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.mojang.serialization.Codec;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class BiomeModifiersHandler
{
    public static final DeferredRegister<Codec<? extends BiomeModifier>> REG = DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, TorchesBecomeSunlight.MOD_ID);
    public static final DeferredHolder<Codec<? extends BiomeModifier>, Codec<? extends BiomeModifier>> TBS_MOB_SPAWNS = REG.register("torchesbecomesunlight_mob_spawns", MobSpawnBiomeModifier::makeCodec);
}
