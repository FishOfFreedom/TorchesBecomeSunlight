package com.freefish.torchesbecomesunlight.server.init.generator.lootmodifier;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import com.freefish.torchesbecomesunlight.server.init.ItemHandle;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class EndCityLootModifier extends LootModifier {
    private final Item item;
    private final float probability;
    private final boolean inShipChest;

    public EndCityLootModifier(final LootItemCondition[] conditionsIn,Item item, float probability, boolean inShipChest) {
        super(conditionsIn);
        this.item = item;
        this.probability = probability;
        this.inShipChest = inShipChest;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        // 获取当前战利品表ID
        ResourceLocation lootTableId = context.getQueriedLootTableId();

        // 普通末地城箱子
        if (lootTableId.equals(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/end_city_treasure"))) {
            if (context.getRandom().nextFloat() < probability) {
                generatedLoot.add(new ItemStack(ItemHandle.TIME.get(), 1));
            }
            if (context.getRandom().nextFloat() < probability) {
                generatedLoot.add(new ItemStack(ItemHandle.LIGHT.get(), 1));
            }
        }
        // 末地船箱子（包含鞘翅）
        else if (inShipChest && lootTableId.equals(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/end_city_ship"))) {
            generatedLoot.add(new ItemStack(ItemHandle.LIGHT.get(), 1));
        }

        return generatedLoot;
    }

    public static final MapCodec<EndCityLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LOOT_CONDITIONS_CODEC.fieldOf("conditions").forGetter(lm -> lm.conditions),
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(modifier -> modifier.item),
            Codec.FLOAT.fieldOf("probability").forGetter(modifier -> modifier.probability),
            Codec.BOOL.fieldOf("in_ship_chest").forGetter(modifier -> modifier.inShipChest)
    ).apply(instance, EndCityLootModifier::new));


    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}