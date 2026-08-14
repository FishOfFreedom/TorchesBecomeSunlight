package com.freefish.torchesbecomesunlight.server.init.recipe;

import net.minecraft.core.registries.BuiltInRegistries;
import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class RecipesHandle {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, TorchesBecomeSunlight.MOD_ID);
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, TorchesBecomeSunlight.MOD_ID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<StewPotRecipe>> STEW_POT_RECIPE_TYPE =
            RECIPE_TYPES.register("stew_pot", () -> new RecipeType<>(){});
    public static final DeferredHolder<RecipeType<?>, RecipeType<OvenRecipe>> OVEN_RECIPE_TYPE =
            RECIPE_TYPES.register("oven", () -> new RecipeType<>(){});
    public static final DeferredHolder<RecipeType<?>, RecipeType<CuttingBoardRecipe>> CUTTING_BOARD_RECIPE_TYPE =
            RECIPE_TYPES.register("cutting_board", () -> new RecipeType<>(){});
    public static final DeferredHolder<RecipeType<?>, RecipeType<FoodValuesDefinition>> FOOD_ATTRIBUTE_RECIPE_TYPE =
            RECIPE_TYPES.register("food_attribute", () -> new RecipeType<FoodValuesDefinition>() {
        @Override
        public String toString() {
            return TorchesBecomeSunlight.MOD_ID +":food_attribute";
        }
    });

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<StewPotRecipe>> STEW_POT_SERIALIZER =
            SERIALIZERS.register("stew_pot", StewPotRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<OvenRecipe>> OVEN_SERIALIZER =
            SERIALIZERS.register("oven", OvenRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CuttingBoardRecipe>> CUTTING_BOARD_SERIALIZER =
            SERIALIZERS.register("cutting_board", CuttingBoardRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FoodValuesDefinition>> FOOD_ATTRIBUTE_SERIALIZER =
            SERIALIZERS.register("food_attribute", FoodValuesDefinition.Serializer::new);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        RECIPE_TYPES.register(eventBus);
    }
}
