package com.freefish.torchesbecomesunlight.server.init.recipe;

import com.freefish.rosmontislib.sync.ITagSerializable;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

import static com.freefish.torchesbecomesunlight.server.util.TBSJsonUtils.getMobEffects;

public class FoodValues {
    private static final FoodCategory[] CATEGORIES = FoodCategory.values();
    private final float[] values = new float[CATEGORIES.length];
    private final Set<MobEffectInstance> effects = new HashSet<>();
    private int size;

    private FoodValues() {
    }

    public static FoodValues create() {
        return new FoodValues();
    }

    @SafeVarargs
    public static FoodValues of(Pair<FoodCategory, Float>... pairs) {
        final FoodValues foodValues = create();
        for (Pair<FoodCategory, Float> pair : pairs) {
            foodValues.put(pair.getKey(), pair.getValue());
        }
        return foodValues;
    }

    public static FoodValues of(Object... categoriesAndValues) {
        final FoodValues foodValues = create();
        FoodCategory category = null;
        for (int i = 0; i < categoriesAndValues.length; i++) {
            if (i % 2 == 0) {
                category = (FoodCategory) categoriesAndValues[i];
            } else {
                foodValues.put(category, (Float) categoriesAndValues[i]);
            }
        }
        return foodValues;
    }

    public static FoodValues of(Map<FoodCategory, Float> map) {
        final FoodValues foodValues = create();
        if (map != null) {
            map.forEach(foodValues::put);
        }
        return foodValues;
    }

    public static FoodValues merge(Collection<FoodValues> foodValues) {
        final FoodValues mergedFoodValues = create();
        foodValues.forEach(foodValue -> foodValue.entrySet().forEach(entry -> mergedFoodValues.put(entry.getKey(), entry.getValue() + mergedFoodValues.get(entry.getKey()))));
        return mergedFoodValues;
    }

    public FoodValues set(FoodCategory category, float value) {
        this.put(category, value);
        return this;
    }

    public float get(FoodCategory category) {
        if (category == null) {
            return 0.0F;
        }
        return Math.max(values[category.ordinal()], 0.0F);
    }

    public void putEffect(MobEffectInstance mobEffect){
        this.effects.add(mobEffect);
    }

    public Set<MobEffectInstance> getEffects() {
        return effects;
    }

    public float[] getFoodValues() {
        return values;
    }

    public boolean has(FoodCategory category) {
        if (category == null) {
            return false;
        }
        return values[category.ordinal()] > 0.0F;
    }

    public void put(FoodCategory category, float value) {
        if (category == null) {
            return;
        }
        if (Float.isNaN(value) || value <= 0.0F) {
            this.remove(category);
            return;
        }
        boolean hasOldValue = this.has(category);
        values[category.ordinal()] = value;
        if (!hasOldValue) {
            size++;
        }
    }

    public void add(FoodCategory category, float value) {
        if (category == null) {
            return;
        }
        boolean hasOldValue = this.has(category);
        values[category.ordinal()] += value;
        if (!hasOldValue) {
            size++;
        }
    }

    public void remove(FoodCategory category) {
        if (category == null || !this.has(category)) {
            return;
        }
        values[category.ordinal()] = 0.0F;
        size--;
    }

    public void clear() {
        Arrays.fill(values, 0.0F);
        size = 0;
    }

    public boolean isEmpty() {
        return size <= 0;
    }

    public int size() {
        return size;
    }

    public Set<Pair<FoodCategory, Float>> entrySet() {
        ImmutableSet.Builder<Pair<FoodCategory, Float>> builder = ImmutableSet.builder();
        for (int i = 0; i < values.length; i++) {
            if (values[i] > 0.0F) {
                builder.add(Pair.of(CATEGORIES[i], values[i]));
            }
        }
        return builder.build();
    }

    public static FoodValues fromJson(JsonElement json, JsonElement jsonArray) {
        if (json == null || json.isJsonNull()) {
            throw new JsonSyntaxException("Json cannot be null");
        }
        if (!json.isJsonObject()) {
            throw new JsonSyntaxException("Expected food value to be an object, was " + GsonHelper.getType(json));
        }
        if (!jsonArray.isJsonArray()) {
            throw new JsonSyntaxException("Expected food effect to be an array, was " + GsonHelper.getType(json));
        }
        final FoodValues foodValues = create();
        JsonObject obj = json.getAsJsonObject();
        obj.entrySet().forEach(entry -> {
            String category = entry.getKey().toUpperCase(Locale.ROOT);
            if (!EnumUtils.isValidEnum(FoodCategory.class, category)) {
                throw new JsonSyntaxException("Expected the key of food value to be an enum of food category, was unknown name: '" + category + "'");
            }
            if (!GsonHelper.isNumberValue(entry.getValue())) {
                throw new JsonSyntaxException("Expected the value of food value to be a number, was " + GsonHelper.getType(entry.getValue()));
            }
            foodValues.put(FoodCategory.valueOf(category), entry.getValue().getAsFloat());
        });

        JsonArray jsonEffects = jsonArray.getAsJsonArray();
        jsonEffects.forEach(entry -> {
            JsonObject effect = entry.getAsJsonObject();

            String effectId = effect.get("effect_id").getAsString();
            int level = effect.get("level").getAsInt();
            int time = effect.get("time").getAsInt();
            MobEffectInstance mobEffectInstance = new MobEffectInstance();
            mobEffectInstance.mobEffect = getMobEffects(effectId);
            mobEffectInstance.level = level;
            mobEffectInstance.time = time;

            foodValues.putEffect(mobEffectInstance);
        });

        return foodValues;
    }

    public JsonElement toJson() {
        final JsonObject obj = new JsonObject();
        this.entrySet().forEach(entry -> obj.addProperty(entry.getKey().name(), entry.getValue()));
        return obj;
    }

    public static FoodValues fromNetwork(FriendlyByteBuf buffer) {
        final FoodValues foodValues = create();
        int length = buffer.readByte();
        for (int i = 0; i < length; i++) {
            FoodCategory category = buffer.readEnum(FoodCategory.class);
            float value = buffer.readFloat();
            foodValues.put(category, value);
        }

        var effectTag = buffer.readNbt();
        for(String key: effectTag.getAllKeys()){
            MobEffectInstance instance = new MobEffectInstance();
            instance.deserializeNBT(effectTag.getCompound(key));

            foodValues.putEffect(instance);
        }

        return foodValues;
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        Set<Pair<FoodCategory, Float>> entrySet = this.entrySet();
        buffer.writeByte(entrySet.size());
        entrySet.forEach(entry -> {
            buffer.writeEnum(entry.getKey());
            buffer.writeFloat(entry.getValue());
        });
        CompoundTag effectTag = new CompoundTag();
        int i = 0;
        for(MobEffectInstance mobEffect:effects){
            effectTag.put(String.valueOf(i), mobEffect.serializeNBT());
            i++;
        }
        buffer.writeNbt(effectTag);
    }

    public static class MobEffectInstance implements ITagSerializable<CompoundTag> {
        public MobEffect mobEffect;
        public int time;
        public int level;

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putString("effect", ForgeRegistries.MOB_EFFECTS.getKey(mobEffect).toString());
            compoundTag.putInt("level",level);
            compoundTag.putInt("time",time);
            return compoundTag;
        }

        @Override
        public void deserializeNBT(CompoundTag compoundTag) {
            mobEffect =getMobEffects(compoundTag.getString("effect"));
            level = compoundTag.getInt("level");
            time =  compoundTag.getInt("time");
        }

        @Override
        public int hashCode() {
            return Objects.hash(mobEffect);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MobEffectInstance that = (MobEffectInstance) o;
            return mobEffect == that.mobEffect;
        }

        @Override
        public String toString() {
            return ForgeRegistries.MOB_EFFECTS.getKey(mobEffect).toString()+" "+time+" "+level;
        }
    }
}
