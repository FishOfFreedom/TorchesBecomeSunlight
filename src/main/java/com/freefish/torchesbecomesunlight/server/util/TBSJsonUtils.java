package com.freefish.torchesbecomesunlight.server.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Objects;

public class TBSJsonUtils {
    public static MobEffect getMobEffects(String effectName)
    {
        ResourceLocation itemKey = ResourceLocation.parse(effectName);
        MobEffect item = BuiltInRegistries.MOB_EFFECT.getValue(itemKey);
        return Objects.requireNonNull(item);
    }
}
