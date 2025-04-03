package com.freefish.torchesbecomesunlight.server.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class TBSJsonUtils {
    public static MobEffect getMobEffects(String effectName)
    {
        ResourceLocation itemKey = new ResourceLocation(effectName);
        MobEffect item = ForgeRegistries.MOB_EFFECTS.getValue(itemKey);
        return Objects.requireNonNull(item);
    }
}
