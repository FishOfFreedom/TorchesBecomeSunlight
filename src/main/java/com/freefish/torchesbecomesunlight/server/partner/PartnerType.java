package com.freefish.torchesbecomesunlight.server.partner;

import lombok.Getter;
import net.minecraft.world.entity.EntityType;

import java.util.function.Supplier;

public class PartnerType<T extends Partner> {
    private Supplier<T> supplier;
    @Getter
    private EntityType<?> entityType;


    public PartnerType(EntityType<?> entityType, Supplier<T> supplier) {
        this.supplier = supplier;
        this.entityType = entityType;
    }

    public T create(){
        return supplier.get();
    }
}
