package com.freefish.torchesbecomesunlight.client.render.gui.screen.stewpot;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import javax.annotation.Nonnull;
import java.util.List;

public class CookingPotRecipeBookComponent extends RecipeBookComponent
{
	protected static final ResourceLocation RECIPE_BOOK_BUTTONS = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/gui/recipe_book_buttons.png");

	@Override
	protected void initFilterButtonTextures() {
		this.filterButton.initTextureValues(0, 0, 28, 18, RECIPE_BOOK_BUTTONS);
	}

	public void hide() {
		this.setVisible(false);
	}

	@Override
	@Nonnull
	protected Component getRecipeFilterName() {
		return Component.literal("container.recipe_book.cookable");
	}

	@Override
	public void setupGhostRecipe(Recipe<?> recipe, List<Slot> slots) {
		ItemStack resultStack = recipe.getResultItem(this.minecraft.level.registryAccess());
		this.ghostRecipe.setRecipe(recipe);
		if (slots.get(12).getItem().isEmpty()) {
			this.ghostRecipe.addIngredient(Ingredient.of(resultStack), (slots.get(12)).x, (slots.get(12)).y);
		}

		this.placeRecipe(this.menu.getGridWidth(), this.menu.getGridHeight(), this.menu.getResultSlotIndex(), recipe, recipe.getIngredients().iterator(), 0);
	}
}
