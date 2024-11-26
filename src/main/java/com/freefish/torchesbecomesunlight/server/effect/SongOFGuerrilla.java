package com.freefish.torchesbecomesunlight.server.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class SongOFGuerrilla extends MobEffect {
    public SongOFGuerrilla() {
        super(MobEffectCategory.BENEFICIAL, 9643043);
        addAttributeModifier(Attributes.ATTACK_DAMAGE, "7107DE5E-7CE8-8677-940E-514C1F160890", (double)0.2F, AttributeModifier.Operation.MULTIPLY_TOTAL);
        addAttributeModifier(Attributes.ARMOR, "7107DE5E-7CE8-3474-940E-514C1F160890", (double)0.2F, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public boolean isBeneficial() {
        return true;
    }
}
