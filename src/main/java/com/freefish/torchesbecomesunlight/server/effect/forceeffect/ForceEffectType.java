package com.freefish.torchesbecomesunlight.server.effect.forceeffect;

import java.util.function.Supplier;

public class ForceEffectType<T extends ForceEffect> {
    private final Supplier<T> supplier;

    public ForceEffectType(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T create(){
        return supplier.get();
    }
}
