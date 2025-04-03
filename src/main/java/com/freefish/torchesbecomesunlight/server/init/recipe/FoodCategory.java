package com.freefish.torchesbecomesunlight.server.init.recipe;

import net.minecraft.network.chat.TextColor;

public enum FoodCategory {
    MEAT("#FFABC7"),
    FISH("#006BFF"),
    EGG("#00FFBB"),
    VEGGIE("#00FF00");

    public final TextColor color;

    FoodCategory(String hexString) {
        this.color = TextColor.parseColor(hexString);
    }
}
