package com.freefish.torchesbecomesunlight.server.item.food;

import com.freefish.torchesbecomesunlight.server.init.recipe.FoodCategory;
import com.freefish.torchesbecomesunlight.server.init.recipe.FoodValues;
import com.freefish.torchesbecomesunlight.server.util.TBSJsonUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ForgeRegistries;

public class DishAttribute {
    private final FoodValues foodValues;
    private final float integratedNutrition;

    public DishAttribute(){
        foodValues = FoodValues.create();
        integratedNutrition = 0;
    }

    public DishAttribute(CompoundTag compoundTag){
        foodValues = FoodValues.create();
        CompoundTag length = compoundTag.getCompound("foodvalue");
        int i = 0;

        for(FoodCategory foodCategory : FoodCategory.values()){
            float value = length.getFloat(String.valueOf(i));
            System.out.println(value);
            foodValues.put(foodCategory, value);
            i++;
        }

        CompoundTag effects = compoundTag.getCompound("effects");
        effects.getAllKeys().forEach((effect)->{
            String effectID = effects.getString(effect);
            foodValues.putEffect(TBSJsonUtils.getMobEffects(effectID));
        });
        integratedNutrition=compoundTag.getFloat("nutrition");
    }

    public DishAttribute(FoodValues foodValues, float integratedNutrition){
        this.foodValues = foodValues;
        this.integratedNutrition = integratedNutrition;
    }

    public CompoundTag save(){
        CompoundTag dishAttributeData = new CompoundTag();

        CompoundTag length = new CompoundTag();
        int i = 0;
        for(FoodCategory foodCategory : FoodCategory.values()){
            length.putFloat(String.valueOf(i),foodValues.get(foodCategory));
            i++;
        }
        dishAttributeData.put("foodvalue",length);

        CompoundTag effects = new CompoundTag();
        i = 0;
        for(MobEffect mobEffect: foodValues.getEffects()){
            effects.putString(String.valueOf(i), ForgeRegistries.MOB_EFFECTS.getKey(mobEffect).toString());
            i++;
        }
        dishAttributeData.put("effects",effects);
        dishAttributeData.putFloat("nutrition",integratedNutrition);

        return dishAttributeData;
    }

    public FoodValues getFoodValues() {
        return foodValues;
    }

    public float getIntegratedNutrition() {
        return integratedNutrition;
    }
}
