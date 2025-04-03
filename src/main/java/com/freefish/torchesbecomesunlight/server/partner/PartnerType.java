package com.freefish.torchesbecomesunlight.server.partner;

import net.minecraft.world.entity.LivingEntity;

public class PartnerType<M extends LivingEntity, T extends Partner<M>> implements Comparable<PartnerType<M, T>> {
    private final PartnerType.IFactory<M, T> factory;
    private final String name;

    public PartnerType(String name, PartnerType.IFactory<M, T> factoryIn) {
        factory = factoryIn;
        this.name = name;
    }

    public T makeInstance(LivingEntity user) {
        return factory.create(this, (M) user);
    }

    public interface IFactory<M extends LivingEntity, T extends Partner<M>> {
        T create(PartnerType<M, T> p_create_1_, M user);
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(PartnerType<M, T> o) {
        return this.getName().compareTo(o.getName());
    }
}
