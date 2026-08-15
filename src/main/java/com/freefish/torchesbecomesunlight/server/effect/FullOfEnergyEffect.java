package com.freefish.torchesbecomesunlight.server.effect;

import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.FrozenCapability;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class FullOfEnergyEffect extends MobEffect {
    public FullOfEnergyEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    /**
     * 1.21 的 removeAttributeModifiers(AttributeMap) 不再带 LivingEntity，
     * 故效果结束时的冻结状态复位改由 EventListener 监听 MobEffectEvent 调用这里。
     */
    public void resetFrozenState(LivingEntity livingEntity) {
        FrozenCapability.IFrozenCapability data = CapabilityHandle.getCapability(livingEntity, CapabilityHandle.FROZEN_CAPABILITY);
        if (data != null) {
            data.onEffectUpdated(livingEntity, 0, 0, 0, 0);
        }
    }
}
