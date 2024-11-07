package com.freefish.torchesbecomesunlight.server.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class Collapsal extends MobEffect {
    public Collapsal() {
        super(MobEffectCategory.HARMFUL, 9643043);
    }

    @Override
    public boolean isBeneficial() {
        return false;
    }
}
