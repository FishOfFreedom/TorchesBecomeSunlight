package com.freefish.torchesbecomesunlight.compat.jei;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.init.recipe.CuttingBoardRecipe;
import com.freefish.torchesbecomesunlight.server.init.recipe.OvenRecipe;
import com.freefish.torchesbecomesunlight.server.init.recipe.StewPotRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.List;

@mezz.jei.api.JeiPlugin
public class JeiPlugin implements IModPlugin {
    public static final RecipeType<OvenRecipe> OVEN_RECIPE_TYPE = RecipeType.create(TorchesBecomeSunlight.MOD_ID, "oven_recipe", OvenRecipe.class);
    public static final RecipeType<StewPotRecipe> STEW_POT_RECIPE_RECIPE_TYPE = RecipeType.create(TorchesBecomeSunlight.MOD_ID, "stew_pot_recipe", StewPotRecipe.class);
    public static final RecipeType<CuttingBoardRecipe> CUTTING_BOARD_RECIPE_RECIPE_TYPE = RecipeType.create(TorchesBecomeSunlight.MOD_ID, "cutting_board_recipe", CuttingBoardRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "main");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(
                new OvenRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
                new StewPotRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
                new CuttingBoardRecipeCategory(registry.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registry) {
        List<OvenRecipe> recipeList = new ArrayList<>();
        List<StewPotRecipe> recipeList1 = new ArrayList<>();
        List<CuttingBoardRecipe> recipeList2 = new ArrayList<>();

        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
        for (Recipe<?> i : manager.getRecipes()) {
            if (i instanceof OvenRecipe glyphRecipe) {
                recipeList.add(glyphRecipe);
            }else if(i instanceof StewPotRecipe glyphRecipe) {
                recipeList1.add(glyphRecipe);
            } else if(i instanceof CuttingBoardRecipe glyphRecipe) {
                recipeList2.add(glyphRecipe);
            }
        }

        registry.addRecipes(OVEN_RECIPE_TYPE, recipeList);
        registry.addRecipes(STEW_POT_RECIPE_RECIPE_TYPE, recipeList1);
        registry.addRecipes(CUTTING_BOARD_RECIPE_RECIPE_TYPE, recipeList2);
    }
}