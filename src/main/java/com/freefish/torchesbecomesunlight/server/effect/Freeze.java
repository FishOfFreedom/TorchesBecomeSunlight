package com.freefish.torchesbecomesunlight.server.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class Freeze extends MobEffect {
    public Freeze() {
        super(MobEffectCategory.BENEFICIAL, 9643043);
        addAttributeModifier(Attributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890", (double)-0.5F, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public boolean isBeneficial() {
        return false;
    }
}
