package com.freefish.torchesbecomesunlight.server.block.furniture.furniturenum;

import net.minecraft.util.StringRepresentable;

public enum Door implements StringRepresentable {
    OPEN_R("open_r"),
    OPEN_L("open_l"),
    CLOSE_R("close_r"),
    CLOSE_L("close_l");

    private final String name;

    Door(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
