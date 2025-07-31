package com.freefish.torchesbecomesunlight.server.story.data.generatext;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.function.Supplier;

public class GeneratextType<T extends Generatext> {
    public static BiMap<String, GeneratextType<?>> GENERATEXT_TYPES = HashBiMap.create();

    static  {
        new GeneratextType<>(FindVillagerGeneratext.ID, FindVillagerGeneratext::new);
    }

    private Supplier<T> supplier;

    public GeneratextType(String s,Supplier<T> supplier) {
        this.supplier = supplier;
        GENERATEXT_TYPES.put(s,this);
    }

    public T create(){
        return supplier.get();
    }
}
