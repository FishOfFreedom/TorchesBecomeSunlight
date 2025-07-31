package com.freefish.torchesbecomesunlight.compat.jei;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.init.ItemHandle;
import com.freefish.torchesbecomesunlight.server.init.recipe.OvenRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nonnull;

public class OvenRecipeCategory implements IRecipeCategory<OvenRecipe> {
    public final static ResourceLocation TEXTURE = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/gui/cooking_pot.png");

    private final IDrawable background;
    private final IDrawable icon;

    public OvenRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 80);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ItemHandle.OVEN.get()));
    }

    @Override
    public RecipeType<OvenRecipe> getRecipeType() {
        return JeiPlugin.OVEN_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.torchesbecomesunlight.oven");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull OvenRecipe recipe, @Nonnull IFocusGroup focusGroup) {
        ClientLevel level = Minecraft.getInstance().level;

        NonNullList<Ingredient> ingredients = recipe.getIngredients();

        int inputStartX = 30;
        int inputStartY = 17;
        int borderSlotSize = 18;
        ff : for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                int i = row * 3 + column;
                if(i<ingredients.size()) {
                    builder.addSlot(RecipeIngredientRole.INPUT, inputStartX + (column * borderSlotSize), inputStartY + (row * borderSlotSize))
                            .addIngredients(ingredients.get(i));
                }else {
                    break ff;
                }
            }
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 124, 35).addItemStack(recipe.getResultItem(level.registryAccess()));
    }
}