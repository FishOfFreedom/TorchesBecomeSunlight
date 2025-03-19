package com.freefish.torchesbecomesunlight.server.item.food;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

public class TBSFood extends Item {
    public TBSFood(Properties pProperties) {
        super(pProperties.food(new FoodProperties.Builder().build()));
    }

    @Override
    public boolean isEdible() {
        return super.isEdible();
    }
}
