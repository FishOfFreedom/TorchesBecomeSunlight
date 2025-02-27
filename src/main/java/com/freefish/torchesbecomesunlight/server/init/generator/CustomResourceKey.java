package com.freefish.torchesbecomesunlight.server.init.generator;


import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class CustomResourceKey {
    public static final ResourceKey<DamageType> DEMON_ATTACK = registerResourceKey(Registries.DAMAGE_TYPE, "pursuer_demon_attack");
    public static final ResourceKey<DamageType> FROST_ATTACK = registerResourceKey(Registries.DAMAGE_TYPE, "frostnova_frost");

    private static <T> ResourceKey<T> registerResourceKey(ResourceKey<Registry<T>> registry, String key) {
        return ResourceKey.create(registry, new ResourceLocation(TorchesBecomeSunlight.MOD_ID, key));
    }
}
