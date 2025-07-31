package com.freefish.torchesbecomesunlight.server.item.food;

import com.freefish.rosmontislib.sync.IPersistedSerializable;
import com.freefish.torchesbecomesunlight.server.init.recipe.FoodCategory;
import com.freefish.torchesbecomesunlight.server.init.recipe.FoodValues;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;

public class DishAttribute implements IPersistedSerializable {
    private FoodValues foodValues;
    private float integratedNutrition;

    public DishAttribute(){
        foodValues = FoodValues.create();
        integratedNutrition = 0;
    }

    public DishAttribute(FoodValues foodValues, float integratedNutrition){
        this.foodValues = foodValues;
        this.integratedNutrition = integratedNutrition;
    }


    public void resultNutrition(RandomSource randomSource){
        float[] values = new float[4];
        values[0] = foodValues.get(FoodCategory.VEGGIE);
        values[1] =   foodValues.get(FoodCategory.MEAT);
        values[2] = foodValues.get(FoodCategory.FISH);
        values[3] =   foodValues.get(FoodCategory.EGG);

        float max = 0;
        for(int i=0;i<4;i++){
            max = Math.max(max,values[i]);
        }

        integratedNutrition = (values[0]/max+values[1]/max+values[2]/max+values[3]/max)/2;
        int i = randomSource.nextInt(3);
        float offset;
        if(i==0){
            offset =-0.1f;
        }else if(i==1){
            offset =0;
        }else {
            offset =0.1f;
        }
            for(FoodCategory category:FoodCategory.values()){
            foodValues.add(category,offset);
        }
    }

    @Override
    public CompoundTag serializeNBT() {
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
        for(FoodValues.MobEffectInstance mobEffect: foodValues.getEffects()){
            effects.put(String.valueOf(i), mobEffect.serializeNBT());
            i++;
        }
        dishAttributeData.put("effects",effects);
        dishAttributeData.putFloat("nutrition",integratedNutrition);

        return dishAttributeData;
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        foodValues = FoodValues.create();
        CompoundTag length = compoundTag.getCompound("foodvalue");
        int i = 0;

        for(FoodCategory foodCategory : FoodCategory.values()){
            float value = length.getFloat(String.valueOf(i));
            foodValues.put(foodCategory, value);
            i++;
        }

        CompoundTag effects = compoundTag.getCompound("effects");
        effects.getAllKeys().forEach((effect)->{
            CompoundTag effectID = effects.getCompound(effect);
            FoodValues.MobEffectInstance instance = new FoodValues.MobEffectInstance();
            instance.deserializeNBT(effectID);

            foodValues.putEffect(instance);
        });
        integratedNutrition=compoundTag.getFloat("nutrition");
    }

    public FoodValues getFoodValues() {
        return foodValues;
    }

    public float getIntegratedNutrition() {
        return integratedNutrition;
    }
}
