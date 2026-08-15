package com.freefish.torchesbecomesunlight.server.init.recipe;

import net.minecraft.core.HolderLookup;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import java.util.Optional;
import com.freefish.torchesbecomesunlight.client.render.gui.recipebook.CookingPotRecipeBookTab;
import com.google.gson.JsonArray;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.CraftingHelper;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class StewPotRecipe implements Recipe<RecipeWrapper> {
    public static final int INPUT_SLOTS = 9;
    private final String group;
    private final CookingPotRecipeBookTab tab;
    private final NonNullList<Ingredient> inputItems;
    private final ItemStack output;
    private final float experience;
    private final int cookTime;

    public StewPotRecipe(String group,@Nullable CookingPotRecipeBookTab tab, NonNullList<Ingredient> inputItems, ItemStack output, float experience, int cookTime) {
        this.group = group;
        this.tab = tab;
        this.inputItems = inputItems;
        this.output = output;

        this.experience = experience;
        this.cookTime = cookTime;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.inputItems;
    }

    @Nullable
    public CookingPotRecipeBookTab getRecipeBookTab() {
        return this.tab;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider access) {
        return this.output;
    }

    @Override
    public ItemStack assemble(RecipeWrapper inv, HolderLookup.Provider access) {
        return this.output.copy();
    }

    public float getExperience() {
        return this.experience;
    }

    public int getCookTime() {
        return this.cookTime;
    }

    @Override
    public boolean matches(RecipeWrapper inv, Level level) {
        java.util.List<ItemStack> inputs = new java.util.ArrayList<>();
        int i = 0;

        for (int j = 0; j < INPUT_SLOTS; ++j) {
            ItemStack itemstack = inv.getItem(j);
            if (!itemstack.isEmpty()) {
                ++i;
                inputs.add(itemstack);
            }
        }
        return i == this.inputItems.size() && net.neoforged.neoforge.common.util.RecipeMatcher.findMatches(inputs, this.inputItems) != null;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= this.inputItems.size();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipesHandle.STEW_POT_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipesHandle.STEW_POT_RECIPE_TYPE.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StewPotRecipe that = (StewPotRecipe) o;

        if (Float.compare(that.getExperience(), getExperience()) != 0) return false;
        if (getCookTime() != that.getCookTime()) return false;
        if (!getGroup().equals(that.getGroup())) return false;
        if (tab != that.tab) return false;
        if (!inputItems.equals(that.inputItems)) return false;
        return (output.equals(that.output));
    }

    public static class Serializer implements RecipeSerializer<StewPotRecipe> {

        /** 1.21 起配方的 JSON 与网络编解码统一走 Codec，原 fromJson/toNetwork 的手写路径已不存在 */
        public static final MapCodec<StewPotRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(r -> r.group),
                CookingPotRecipeBookTab.CODEC.optionalFieldOf("recipe_book_tab").forGetter(r -> Optional.ofNullable(r.tab)),
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").forGetter(r -> r.inputItems),
                ItemStack.CODEC.fieldOf("result").forGetter(r -> r.output),
                Codec.FLOAT.optionalFieldOf("experience", 0.0F).forGetter(r -> r.experience),
                Codec.INT.optionalFieldOf("cookingtime", 200).forGetter(r -> r.cookTime)
        ).apply(inst, (group, tab, ingredients, result, exp, time) -> new StewPotRecipe(
                group, tab.orElse(null), NonNullList.copyOf(ingredients), result, exp, time)));

        public static final StreamCodec<RegistryFriendlyByteBuf, StewPotRecipe> STREAM_CODEC =
                StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        @Override
        public MapCodec<StewPotRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, StewPotRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static StewPotRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            String group = buffer.readUtf();
            CookingPotRecipeBookTab tab = CookingPotRecipeBookTab.findByName(buffer.readUtf());
            int size = buffer.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(size, Ingredient.EMPTY);
            for (int i = 0; i < size; i++) {
                ingredients.set(i, Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            }
            ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
            float exp = buffer.readFloat();
            int time = buffer.readVarInt();
            return new StewPotRecipe(group, tab, ingredients, result, exp, time);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, StewPotRecipe recipe) {
            buffer.writeUtf(recipe.group);
            buffer.writeUtf(recipe.tab != null ? recipe.tab.toString() : "");
            buffer.writeVarInt(recipe.inputItems.size());
            for (Ingredient ingredient : recipe.inputItems) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
            }
            ItemStack.STREAM_CODEC.encode(buffer, recipe.output);
            buffer.writeFloat(recipe.experience);
            buffer.writeVarInt(recipe.cookTime);
        }
    }
}
