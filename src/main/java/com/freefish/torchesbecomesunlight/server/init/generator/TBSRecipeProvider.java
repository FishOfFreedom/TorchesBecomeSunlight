package com.freefish.torchesbecomesunlight.server.init.generator;

import com.freefish.torchesbecomesunlight.server.init.ItemHandle;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class TBSRecipeProvider extends RecipeProvider {
    public TBSRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ItemHandle.BLESSING_OF_SAMI.get())
                .pattern("ABA")
                .pattern("BIB")
                .pattern("ABA")
                .define('I', Items.NETHER_STAR)
                .define('A', Items.ICE)
                .define('B', Items.SNOW_BLOCK)
                .unlockedBy("has_blessing_of_sami", has(ItemHandle.BLESSING_OF_SAMI.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ItemHandle.ICE_BROADSWORD.get())
                .pattern(" BA")
                .pattern("BIB")
                .pattern("AB ")
                .define('I', Items.NETHERITE_SWORD)
                .define('A', Items.ICE)
                .define('B', Items.DIAMOND)
                .unlockedBy("has_ice_broadsword", has(ItemHandle.ICE_BROADSWORD.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ItemHandle.GUARD_SHIELD.get())
                .pattern(" B ")
                .pattern("BIB")
                .pattern(" B ")
                .define('I', Items.SHIELD)
                .define('B', Items.DIAMOND)
                .unlockedBy("has_guard_shield", has(ItemHandle.GUARD_SHIELD.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ItemHandle.CUTTING_BOARD.get())
                .pattern("  B")
                .pattern("III")
                .pattern("   ")
                .define('I', ItemTags.PLANKS)
                .define('B', Items.IRON_SWORD)
                .unlockedBy("has_cutting_board", has(ItemHandle.CUTTING_BOARD.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ItemHandle.STEW_POT.get())
                .pattern("B B")
                .pattern("BIB")
                .pattern("AAA")
                .define('I', Items.IRON_BLOCK)
                .define('B', Items.IRON_INGOT)
                .define('A', ItemTags.LOGS)
                .unlockedBy("has_stew_pot", has(ItemHandle.STEW_POT.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ItemHandle.OVEN.get())
                .pattern("III")
                .pattern("I I")
                .pattern("BAB")
                .define('I', Items.IRON_INGOT)
                .define('B', Items.BRICK)
                .define('A', ItemTags.COALS)
                .unlockedBy("has_oven", has(ItemHandle.OVEN.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemHandle.RHODESISLAND_EYE.get())
                .pattern("AAA")
                .pattern("IEI")
                .pattern("BBB")
                .define('I', Items.GRAY_CONCRETE)
                .define('E', Items.ENDER_EYE)
                .define('B', Items.WHITE_CONCRETE)
                .define('A', Items.IRON_INGOT)
                .unlockedBy("has_rhodesisland_eye", has(ItemHandle.RHODESISLAND_EYE.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemHandle.SANKTA_STATUE_EYE.get())
                .pattern("BIB")
                .pattern("IEI")
                .pattern("BIB")
                .define('I', Items.END_STONE)
                .define('E', Items.ENDER_EYE)
                .define('B', Items.GLOWSTONE)
                .unlockedBy("has_oven", has(ItemHandle.SANKTA_STATUE_EYE.get()))
                .save(consumer);

    }
}