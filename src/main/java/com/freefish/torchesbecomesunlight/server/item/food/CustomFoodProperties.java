package com.freefish.torchesbecomesunlight.server.item.food;

import net.minecraft.world.food.FoodProperties;

public class CustomFoodProperties {
    public static final FoodProperties THIGH_MEAT = (new FoodProperties.Builder()).nutrition(6).saturationMod(0.2F).build();
    public static final FoodProperties COOKED_THIGH_MEAT = (new FoodProperties.Builder()).nutrition(10).saturationMod(0.8F).build();
    public static final FoodProperties BURDENBEAST_MEAT = (new FoodProperties.Builder()).nutrition(8).saturationMod(0.2F).build();
    public static final FoodProperties COOKED_BURDENBEAST_MEAT = (new FoodProperties.Builder()).nutrition(12).saturationMod(0.9F).build();
}
