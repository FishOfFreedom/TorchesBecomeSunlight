package com.freefish.torchesbecomesunlight.server.block.furniture.furniturenum;

import net.minecraft.util.StringRepresentable;

public enum EightDirection implements StringRepresentable {
    NORTH("north",0),
    NORTH_EAST("north_east",45),
    EAST("east",90),
    EAST_SOUTH("east_south",135),
    SOUTH("south",180),
    SOUTH_WAST("south_west",225),
    WEST("west",270),
    WEST_NORTH("west_north",315);

    private final String name;

    public int getRot() {
        return rot;
    }

    private final int rot;
    EightDirection(String name,int rot) {
        this.name = name;
        this.rot = rot;
    }
    @Override
    public String getSerializedName() {
        return this.name;
    }
}
