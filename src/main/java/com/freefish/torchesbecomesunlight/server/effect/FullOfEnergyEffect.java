package com.freefish.torchesbecomesunlight.server.effect;

import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.FrozenCapability;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class FullOfEnergyEffect extends MobEffect {
    public FullOfEnergyEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }


    @Override
    public void addAttributeModifiers(LivingEntity pLivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
        super.addAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);

    }

    @Override
    public void removeAttributeModifiers(LivingEntity pLivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
        super.removeAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
        FrozenCapability.IFrozenCapability data = CapabilityHandle.getCapability(pLivingEntity,CapabilityHandle.FROZEN_CAPABILITY);
        if(data!=null){
            data.onEffectUpdated(pLivingEntity, 0, 0, 0, 0);
        }
    }
}
