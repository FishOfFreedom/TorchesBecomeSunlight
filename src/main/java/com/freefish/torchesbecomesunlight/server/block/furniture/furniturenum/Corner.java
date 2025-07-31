package com.freefish.torchesbecomesunlight.server.block.furniture.furniturenum;

import net.minecraft.util.StringRepresentable;

public enum Corner implements StringRepresentable {
    SOUTH("south",false),
    NORTH("north",false),
    EAST("east",true),
    WEST("west",true);

    private final String name;

    public boolean isHen() {
        return isHen;
    }

    private final boolean isHen;

    Corner(String name,boolean isHen) {
        this.name = name;
        this.isHen = isHen;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
