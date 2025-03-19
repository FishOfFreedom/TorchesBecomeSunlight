package com.freefish.torchesbecomesunlight.server.item.food;

import net.minecraft.world.food.FoodProperties;

public class CustomFoodProperties {
    public static final FoodProperties THIGH_MEAT = (new FoodProperties.Builder()).nutrition(4).saturationMod(0.3F).build();
    public static final FoodProperties COOKED_THIGH_MEAT = (new FoodProperties.Builder()).nutrition(1).saturationMod(0.5F).build();
}
