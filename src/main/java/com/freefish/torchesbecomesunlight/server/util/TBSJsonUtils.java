package com.freefish.torchesbecomesunlight.server.util;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

public class TBSJsonUtils {
    /**
     * 1.21 起 MobEffectInstance 只接受 Holder&lt;MobEffect&gt;，故这里直接返回 Holder。
     */
    public static Holder<MobEffect> getMobEffects(String effectName) {
        ResourceLocation itemKey = ResourceLocation.parse(effectName);
        return BuiltInRegistries.MOB_EFFECT.getHolder(itemKey)
                .orElseThrow(() -> new IllegalArgumentException("Unknown mob effect: " + effectName));
    }
}
