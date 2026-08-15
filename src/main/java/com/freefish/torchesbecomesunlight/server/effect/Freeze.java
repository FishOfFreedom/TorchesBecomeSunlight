package com.freefish.torchesbecomesunlight.server.effect;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class Freeze extends MobEffect {
    public Freeze() {
        super(MobEffectCategory.BENEFICIAL, 9643043);
        // 1.21 起属性修饰符以 ResourceLocation 而非 UUID 标识
        addAttributeModifier(Attributes.MOVEMENT_SPEED,
                ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "effect.freeze.movement_speed"),
                -0.5F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    @Override
    public boolean isBeneficial() {
        return false;
    }
}
