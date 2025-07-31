package com.freefish.torchesbecomesunlight.server.block.furniture.furniturenum;

import net.minecraft.util.StringRepresentable;

public enum StewPotEnum implements StringRepresentable {
    Firewood_1("fire_1"),
    Firewood_2("fire_2"),
    Firewood_3("fire_3"),
    Firewood_4("fire_4");

    private final String name;

    StewPotEnum(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
