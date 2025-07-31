package com.freefish.torchesbecomesunlight.server.story.data.choose;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.function.Supplier;

public class ChooseType<T extends Choose> {
    public static BiMap<String, ChooseType<?>> CHOOSE_TYPES = HashBiMap.create();

    static  {
        new ChooseType<>(IsItemInHandChoose.ID, IsItemInHandChoose::new);
        new ChooseType<>(OpFindRosmontisChoose.ID, OpFindRosmontisChoose::new);
    }

    private Supplier<T> supplier;

    public ChooseType(String s, Supplier<T> supplier) {
        this.supplier = supplier;
        CHOOSE_TYPES.put(s,this);
    }

    public T create(){
        return supplier.get();
    }
}
