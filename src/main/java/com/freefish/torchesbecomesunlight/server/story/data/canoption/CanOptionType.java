package com.freefish.torchesbecomesunlight.server.story.data.canoption;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.function.Supplier;

public class CanOptionType<T extends CanOption> {
    public static BiMap<String, CanOptionType<?>> CANOPTION_TYPES = HashBiMap.create();

    static  {
        new CanOptionType<>(HealthLargerCanOption.ID, HealthLargerCanOption::new);
        new CanOptionType<>(IsSeenRosmontisCanOption.ID, IsSeenRosmontisCanOption::new);
    }

    private Supplier<T> supplier;

    public CanOptionType(String s, Supplier<T> supplier) {
        this.supplier = supplier;
        CANOPTION_TYPES.put(s,this);
    }

    public T create(){
        return supplier.get();
    }
}
