package com.freefish.torchesbecomesunlight.server.effect.forceeffect;

import net.minecraft.world.entity.LivingEntity;

public class SlowMoveForceEffect extends ForceEffect{
    @Override
    public void addEffect(LivingEntity living, int level) {
    }

    @Override
    public void removeEffect(LivingEntity living) {
    }

    @Override
    public void tick(LivingEntity entity, int level) {
    }

    @Override
    public ForceEffectType<?> getType() {
        return ForceEffectHandle.SLOW_MOVE_FORCE_EFFECT;
    }
}
