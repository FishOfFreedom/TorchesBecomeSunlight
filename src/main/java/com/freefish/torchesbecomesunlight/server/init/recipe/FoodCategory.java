package com.freefish.torchesbecomesunlight.server.init.recipe;

import net.minecraft.network.chat.TextColor;

public enum FoodCategory {
    MEAT("#FFABC7"),
    MONSTER("#D700FF"),
    FISH("#006BFF"),
    EGG("#00FFBB"),
    FRUIT("#FF6B00"),
    VEGGIE("#00FF00"),
    DAIRY("#00C7FF"),
    SWEETENER("#FFFF00"),
    FROZEN("#82FFFF"),
    INEDIBLE("#9B9B9B");

    public final TextColor color;

    FoodCategory(String hexString) {
        this.color = TextColor.parseColor(hexString);
    }
}
