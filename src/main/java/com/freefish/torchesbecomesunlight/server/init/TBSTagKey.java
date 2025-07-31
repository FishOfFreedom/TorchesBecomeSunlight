package com.freefish.torchesbecomesunlight.server.init;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;

public class TBSTagKey {
    public static final TagKey<Structure> EYE_OF_RHODES_ISLAND_BANSHICHU = registerTagKey(Registries.STRUCTURE, "eye_of_rhodes");
    public static final TagKey<Structure> EYE_OF_SANKTA_STATUE = registerTagKey(Registries.STRUCTURE, "eye_of_sankta_statue");

    private static <T> TagKey<T> registerTagKey(ResourceKey<Registry<T>> registry, String key) {
        return TagKey.create(registry, new ResourceLocation(TorchesBecomeSunlight.MOD_ID, key));
    }
}