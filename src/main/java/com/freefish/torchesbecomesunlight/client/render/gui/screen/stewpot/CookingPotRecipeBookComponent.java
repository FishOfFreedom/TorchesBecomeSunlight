package com.freefish.torchesbecomesunlight.client.render.gui.screen.stewpot;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import javax.annotation.Nonnull;
import java.util.List;

public class CookingPotRecipeBookComponent extends RecipeBookComponent
{
	/**
	 * 1.21 的按钮贴图统一来自 GUI sprite 图集，不再是「一张图 + uv 偏移」。
	 * 原先的 textures/gui/recipe_book_buttons.png 是四态拼图，需拆成 4 张 sprite 才能继续用；
	 * 在拆图之前先复用原版筛选按钮的 sprite（功能一致，仅图标非定制）。
	 * TODO(移植): 拆分 recipe_book_buttons.png 到 textures/gui/sprites/recipe_book/ 并换回本 Mod 命名空间
	 */
	protected static final net.minecraft.client.gui.components.WidgetSprites FILTER_BUTTON_SPRITES =
			new net.minecraft.client.gui.components.WidgetSprites(
					ResourceLocation.withDefaultNamespace("recipe_book/filter_enabled"),
					ResourceLocation.withDefaultNamespace("recipe_book/filter_disabled"),
					ResourceLocation.withDefaultNamespace("recipe_book/filter_enabled_highlighted"),
					ResourceLocation.withDefaultNamespace("recipe_book/filter_disabled_highlighted"));

	@Override
	protected void initFilterButtonTextures() {
		this.filterButton.initTextureValues(FILTER_BUTTON_SPRITES);
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
	public void setupGhostRecipe(RecipeHolder<?> recipe, List<Slot> slots) {
		ItemStack resultStack = recipe.value().getResultItem(this.minecraft.level.registryAccess());
		this.ghostRecipe.setRecipe(recipe);
		if (slots.get(12).getItem().isEmpty()) {
			this.ghostRecipe.addIngredient(Ingredient.of(resultStack), (slots.get(12)).x, (slots.get(12)).y);
		}

		this.placeRecipe(this.menu.getGridWidth(), this.menu.getGridHeight(), this.menu.getResultSlotIndex(), recipe, recipe.value().getIngredients().iterator(), 0);
	}
}
