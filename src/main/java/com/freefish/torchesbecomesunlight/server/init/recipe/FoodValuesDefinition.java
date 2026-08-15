package com.freefish.torchesbecomesunlight.server.init.recipe;

import net.minecraft.world.item.crafting.SingleRecipeInput;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.core.HolderLookup;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import com.freefish.torchesbecomesunlight.server.util.TBSJsonUtils;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class FoodValuesDefinition implements Recipe<RecipeInput> {
    private final Set<ResourceLocation> names;
    private final FoodValues foodValues;
    private final boolean item;

    public FoodValuesDefinition(Set<ResourceLocation> names, FoodValues foodValues, boolean item) {
        this.names = ImmutableSet.copyOf(names);
        this.foodValues = foodValues;
        this.item = item;
    }

    public Set<ResourceLocation> getNames() {
        return names;
    }

    public FoodValues getFoodValues() {
        return foodValues;
    }

    public boolean isItem() {
        return item;
    }

    @Override
    public boolean matches(RecipeInput pContainer, Level pLevel) {
        var stack = pContainer.getItem(0);
        return item ? names.stream().anyMatch(name -> name.equals(BuiltInRegistries.ITEM.getKey(stack.getItem()))) :
                names.stream().anyMatch(name -> stack.is(ItemTags.create(name)));
    }

    @Override
    public ItemStack assemble(RecipeInput pContainer, HolderLookup.Provider pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    @Nonnull
    public static FoodValues getFoodValues(ItemStack stack, Level level) {
        var allDefs = level.getRecipeManager().getRecipesFor(RecipesHandle.FOOD_ATTRIBUTE_RECIPE_TYPE.get(), new SingleRecipeInput(stack), level);
        if (allDefs.isEmpty()) {
            return FoodValues.create();
        }
        return allDefs.stream().filter(def -> def.value().isItem()).findFirst().map(h -> h.value().getFoodValues()).orElseGet(() -> {
            var foodValues = FoodValues.create();
            var maxCount = -1L;
            var tagDefs = new HashMap<ResourceLocation, FoodValues>();
            allDefs.forEach(def -> def.value().getNames().forEach(name -> tagDefs.put(name, def.value().getFoodValues())));
            for (var tag : stack.getTags().map(TagKey::location).filter(tagDefs::containsKey).toList()) {
                var count = tag.getPath().chars().filter(c -> c == '/').count();
                if (count < maxCount) {
                    continue;
                }
                if (count > maxCount) {
                    maxCount = count;
                    foodValues.clear();
                }
                tagDefs.get(tag).entrySet().forEach(entry -> foodValues.put(entry.getKey(),
                        Math.max(foodValues.get(entry.getKey()), entry.getValue())));
            }
            return foodValues;
        });
    }

    @Nonnull
    public static FoodValuesDefinition getFoodDefinition(ItemStack stack, Level level) {
        var allDefs = level.getRecipeManager().getRecipesFor(RecipesHandle.FOOD_ATTRIBUTE_RECIPE_TYPE.get(), new SingleRecipeInput(stack), level);
        return allDefs.stream().filter(def -> def.value().isItem()).findFirst().map(net.minecraft.world.item.crafting.RecipeHolder::value).orElse(null);
    }

    public static Set<ItemStack> getMatchedItems(FoodCategory category, Level level) {
        // make vanilla items and Crock Pot mod items at the top of the collection
        var builder = ImmutableSortedSet.orderedBy(
                Comparator.comparing((ItemStack stack) -> getFoodValues(stack, level).get(category))
                        .thenComparing(stack -> BuiltInRegistries.ITEM.getKey(stack.getItem()),
                                Comparator.comparing((ResourceLocation key) -> !"minecraft".equals(key.getNamespace()))
                                        .thenComparing(Comparator.naturalOrder())
                        )
        );
        var allDefs = level.getRecipeManager().getAllRecipesFor(RecipesHandle.FOOD_ATTRIBUTE_RECIPE_TYPE.get()).stream().filter(def -> def.value().getFoodValues().has(category)).toList();
        allDefs.stream().filter(def -> def.value().isItem()).forEach(itemDef -> itemDef.value().getNames().forEach(name -> {
            var item = BuiltInRegistries.ITEM.get(name);
            if (item != null && item != Items.AIR) {
                builder.add(item.getDefaultInstance());
            }
        }));
        allDefs.stream().filter(def -> !def.value().isItem()).forEach(tagDef -> tagDef.value().getNames().forEach(name -> {
            var tag = ItemTags.create(name);
            if (BuiltInRegistries.ITEM.getTag(tag).isPresent()) {
                // get all items with the tag
                var tagIngredient = new Ingredient.TagValue(tag);
                tagIngredient.getItems().forEach(stack -> {
                    // use getFoodValues method to make sure there's no higher priority definition
                    if (getFoodValues(stack, level).has(category)) {
                        builder.add(stack);
                    }
                });
            }
        }));
        return builder.build();
    }

    @Override
    @Nonnull
    public RecipeSerializer<?> getSerializer() {
        return RecipesHandle.FOOD_ATTRIBUTE_SERIALIZER.get();
    }

    @Override
    @Nonnull
    public RecipeType<?> getType() {
        return RecipesHandle.FOOD_ATTRIBUTE_RECIPE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<FoodValuesDefinition> {

        /** 1.21 起配方序列化统一走 Codec；原 fromJson/fromNetwork 的手写路径已不存在 */
        public static final MapCodec<FoodValuesDefinition> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                ResourceLocation.CODEC.listOf().optionalFieldOf("items", List.of()).forGetter(
                        d -> d.isItem() ? List.copyOf(d.getNames()) : List.of()),
                ResourceLocation.CODEC.listOf().optionalFieldOf("tags", List.of()).forGetter(
                        d -> d.isItem() ? List.of() : List.copyOf(d.getNames())),
                Codec.unboundedMap(Codec.STRING, Codec.FLOAT).fieldOf("values").forGetter(
                        d -> d.getFoodValues().toValueMap()),
                FoodValues.MobEffectInstance.CODEC.listOf().optionalFieldOf("effects", List.of()).forGetter(
                        d -> List.copyOf(d.getFoodValues().getEffects()))
        ).apply(inst, (items, tags, values, effects) -> new FoodValuesDefinition(
                items.isEmpty() ? Set.copyOf(tags) : Set.copyOf(items),
                FoodValues.fromCodec(values, effects),
                !items.isEmpty())));

        public static final StreamCodec<RegistryFriendlyByteBuf, FoodValuesDefinition> STREAM_CODEC =
                StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        @Override
        public MapCodec<FoodValuesDefinition> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FoodValuesDefinition> streamCodec() {
            return STREAM_CODEC;
        }

        private static FoodValuesDefinition fromNetwork(RegistryFriendlyByteBuf buffer) {
            var isItem = buffer.readBoolean();
            var names = new HashSet<ResourceLocation>();
            var length = buffer.readVarInt();
            for (var i = 0; i < length; i++) {
                names.add(buffer.readResourceLocation());
            }
            var foodValues = FoodValues.fromNetwork(buffer);
            return new FoodValuesDefinition(names, foodValues, isItem);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, FoodValuesDefinition recipe) {
            buffer.writeBoolean(recipe.isItem());
            buffer.writeVarInt(recipe.getNames().size());
            recipe.getNames().forEach(buffer::writeResourceLocation);
            recipe.getFoodValues().toNetwork(buffer);
        }
    }
}
