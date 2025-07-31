package com.freefish.torchesbecomesunlight.server.effect.forceeffect;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.entity.LivingEntity;

public abstract class ForceEffect {
    @Getter
    @Setter
    private ForceEffectInstance forceEffectInstance;
    public boolean isRemoved = false;

    public abstract void addEffect(LivingEntity living, int level);

    public abstract void removeEffect(LivingEntity living);

    public abstract void tick(LivingEntity living,int level);

    public abstract ForceEffectType<?> getType();
}
