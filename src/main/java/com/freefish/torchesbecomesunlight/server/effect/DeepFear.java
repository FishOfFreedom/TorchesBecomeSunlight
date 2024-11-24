package com.freefish.torchesbecomesunlight.server.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class DeepFear extends MobEffect {
    public DeepFear() {
        super(MobEffectCategory.HARMFUL, 9643043);
    }

    @Override
    public boolean isBeneficial() {
        return false;
    }
}
