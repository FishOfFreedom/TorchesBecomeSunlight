package com.freefish.torchesbecomesunlight.server.partner.command.triggertype;

import java.util.function.Supplier;

public class TriggerBasicType<T extends TriggerBasic>{
    private final Supplier<T> supplier;

    public TriggerBasicType(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T create(){
        return  supplier.get();
    }
}
