package com.freefish.torchesbecomesunlight.server.init.recipe;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RecipesHandle {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, TorchesBecomeSunlight.MOD_ID);
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, TorchesBecomeSunlight.MOD_ID);

    public static final RegistryObject<RecipeType<StewPotRecipe>> STEW_POT_RECIPE_TYPE =
            RECIPE_TYPES.register("stew_pot", () -> new RecipeType<>(){});
    public static final RegistryObject<RecipeType<OvenRecipe>> OVEN_RECIPE_TYPE =
            RECIPE_TYPES.register("oven", () -> new RecipeType<>(){});
    public static final RegistryObject<RecipeType<CuttingBoardRecipe>> CUTTING_BOARD_RECIPE_TYPE =
            RECIPE_TYPES.register("cutting_board", () -> new RecipeType<>(){});
    public static final RegistryObject<RecipeType<FoodValuesDefinition>> FOOD_ATTRIBUTE_RECIPE_TYPE =
            RECIPE_TYPES.register("food_attribute", () -> new RecipeType<FoodValuesDefinition>() {
        @Override
        public String toString() {
            return TorchesBecomeSunlight.MOD_ID +":food_attribute";
        }
    });

    public static final RegistryObject<RecipeSerializer<StewPotRecipe>> STEW_POT_SERIALIZER =
            SERIALIZERS.register("stew_pot", StewPotRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<OvenRecipe>> OVEN_SERIALIZER =
            SERIALIZERS.register("oven", OvenRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<CuttingBoardRecipe>> CUTTING_BOARD_SERIALIZER =
            SERIALIZERS.register("cutting_board", CuttingBoardRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<FoodValuesDefinition>> FOOD_ATTRIBUTE_SERIALIZER =
            SERIALIZERS.register("food_attribute", FoodValuesDefinition.Serializer::new);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        RECIPE_TYPES.register(eventBus);
    }
}
