package com.freefish.torchesbecomesunlight.server.block.furniture.furniturenum;

import net.minecraft.util.StringRepresentable;

public enum LineRL implements StringRepresentable {
    LINE("line"),
    LINE_R("liner"),
    LINE_L("linel"),
    LINE_RL("linerl");

    private final String name;

    LineRL(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
