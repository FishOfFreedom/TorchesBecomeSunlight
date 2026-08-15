package com.freefish.torchesbecomesunlight.server.init.generator;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.init.SpawnHandler;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class MobSpawnBiomeModifier implements BiomeModifier
{
    private static final DeferredHolder<com.mojang.serialization.MapCodec<? extends BiomeModifier>, com.mojang.serialization.MapCodec<? extends BiomeModifier>> SERIALIZER =
            DeferredHolder.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "mowzie_mob_spawns"));

    public MobSpawnBiomeModifier() {

    }

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.ADD) {
            SpawnHandler.addBiomeSpawns(biome, builder);
        }
    }

    @Override
    public com.mojang.serialization.MapCodec<? extends BiomeModifier> codec() {
        return SERIALIZER.get();
    }

    public static com.mojang.serialization.MapCodec<MobSpawnBiomeModifier> makeCodec() {
        return com.mojang.serialization.MapCodec.unit(MobSpawnBiomeModifier::new);
    }
}
