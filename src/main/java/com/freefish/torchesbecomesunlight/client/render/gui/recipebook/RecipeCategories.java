package com.freefish.torchesbecomesunlight.client.render.gui.recipebook;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.init.ItemHandle;
import com.freefish.torchesbecomesunlight.server.init.recipe.RecipesHandle;
import com.freefish.torchesbecomesunlight.server.init.recipe.StewPotRecipe;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.client.event.RegisterRecipeBookCategoriesEvent;

import java.util.List;
import java.util.function.Supplier;

/**
 * 1.21.1 起 RecipeBookCategories 不再有 create(...)，而是一个 IExtensibleEnum，
 * 新常量必须在 META-INF/enumextensions.json 里声明，构造参数由这里的 EnumProxy 字段提供。
 * 字段名与那份 JSON 中的 "field" 一一对应，改名需同步。
 */
public class RecipeCategories
{
	public static final EnumProxy<RecipeBookCategories> COOKING_SEARCH_PROXY = new EnumProxy<>(
			RecipeBookCategories.class,
			(Supplier<List<ItemStack>>) () -> List.of(new ItemStack(Items.COMPASS)));

	public static final EnumProxy<RecipeBookCategories> COOKING_MEALS_PROXY = new EnumProxy<>(
			RecipeBookCategories.class,
			(Supplier<List<ItemStack>>) () -> List.of(new ItemStack(ItemHandle.COOKED_THIGH_MEAT.get())));

	public static final EnumProxy<RecipeBookCategories> COOKING_DRINKS_PROXY = new EnumProxy<>(
			RecipeBookCategories.class,
			(Supplier<List<ItemStack>>) () -> List.of(new ItemStack(ItemHandle.COOKED_THIGH_MEAT.get())));

	public static final EnumProxy<RecipeBookCategories> COOKING_MISC_PROXY = new EnumProxy<>(
			RecipeBookCategories.class,
			(Supplier<List<ItemStack>>) () -> List.of(
					new ItemStack(ItemHandle.COOKED_THIGH_MEAT.get()),
					new ItemStack(ItemHandle.THIGH_MEAT.get())));

	public static RecipeBookCategories cookingSearch() {
		return COOKING_SEARCH_PROXY.getValue();
	}

	public static RecipeBookCategories cookingMeals() {
		return COOKING_MEALS_PROXY.getValue();
	}

	public static RecipeBookCategories cookingDrinks() {
		return COOKING_DRINKS_PROXY.getValue();
	}

	public static RecipeBookCategories cookingMisc() {
		return COOKING_MISC_PROXY.getValue();
	}

	public static void init(RegisterRecipeBookCategoriesEvent event) {
		event.registerBookCategories(TorchesBecomeSunlight.recipeTypeCooking(),
				ImmutableList.of(cookingSearch(), cookingMeals(), cookingDrinks(), cookingMisc()));
		event.registerAggregateCategory(cookingSearch(),
				ImmutableList.of(cookingMeals(), cookingDrinks(), cookingMisc()));
		event.registerRecipeCategoryFinder(RecipesHandle.STEW_POT_RECIPE_TYPE.get(), holder ->
		{
			// 1.21 起这里拿到的是 RecipeHolder，配方本体要再取一层
			if (holder.value() instanceof StewPotRecipe cookingRecipe) {
				CookingPotRecipeBookTab tab = cookingRecipe.getRecipeBookTab();
				if (tab != null) {
					return switch (tab) {
						case MEALS -> cookingMeals();
						case DRINKS -> cookingDrinks();
						case MISC -> cookingMisc();
					};
				}
			}
			return cookingMisc();
		});
	}
}
