package com.freefish.torchesbecomesunlight.client.render.gui.recipebook;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.init.ItemHandle;
import com.freefish.torchesbecomesunlight.server.init.recipe.RecipesHandle;
import com.freefish.torchesbecomesunlight.server.init.recipe.StewPotRecipe;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.event.RegisterRecipeBookCategoriesEvent;

import java.util.function.Supplier;

public class RecipeCategories
{
	public static final Supplier<RecipeBookCategories> COOKING_SEARCH = Suppliers.memoize(() -> RecipeBookCategories.create("COOKING_SEARCH", new ItemStack(Items.COMPASS)));
	public static final Supplier<RecipeBookCategories> COOKING_MEALS = Suppliers.memoize(() -> RecipeBookCategories.create("COOKING_MEALS", new ItemStack(ItemHandle.COOKED_THIGH_MEAT.get())));
	public static final Supplier<RecipeBookCategories> COOKING_DRINKS = Suppliers.memoize(() -> RecipeBookCategories.create("COOKING_DRINKS", new ItemStack(ItemHandle.COOKED_THIGH_MEAT.get())));
	public static final Supplier<RecipeBookCategories> COOKING_MISC = Suppliers.memoize(() -> RecipeBookCategories.create("COOKING_MISC", new ItemStack(ItemHandle.COOKED_THIGH_MEAT.get()), new ItemStack(ItemHandle.THIGH_MEAT.get())));

	public static void init(RegisterRecipeBookCategoriesEvent event) {
		event.registerBookCategories(TorchesBecomeSunlight.RECIPE_TYPE_COOKING, ImmutableList.of(COOKING_SEARCH.get(), COOKING_MEALS.get(), COOKING_DRINKS.get(), COOKING_MISC.get()));
		event.registerAggregateCategory(COOKING_SEARCH.get(), ImmutableList.of(COOKING_MEALS.get(), COOKING_DRINKS.get(), COOKING_MISC.get()));
		event.registerRecipeCategoryFinder(RecipesHandle.STEW_POT_RECIPE_TYPE.get(), recipe ->
		{
			if (recipe instanceof StewPotRecipe cookingRecipe) {
				CookingPotRecipeBookTab tab = cookingRecipe.getRecipeBookTab();
				if (tab != null) {
					return switch (tab) {
						case MEALS -> COOKING_MEALS.get();
						case DRINKS -> COOKING_DRINKS.get();
						case MISC -> COOKING_MISC.get();
					};
				}
			}
			return COOKING_MISC.get();
		});
	}
}
