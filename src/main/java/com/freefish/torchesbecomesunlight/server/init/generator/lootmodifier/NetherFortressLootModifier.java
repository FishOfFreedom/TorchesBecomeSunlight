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

public class NetherFortressLootModifier extends LootModifier {
    private final Item item;
    private final float probability;

    public NetherFortressLootModifier(final LootItemCondition[] conditionsIn, Item item, float probability) {
        super(conditionsIn);
        this.item = item;
        this.probability = probability;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        // 获取当前战利品表ID
        ResourceLocation lootTableId = context.getQueriedLootTableId();

        // 普通末地城箱子
        if (lootTableId.equals(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/nether_bridge"))) {
            if (context.getRandom().nextFloat() < probability) {
                generatedLoot.add(new ItemStack(ItemHandle.DEMON_EYE.get(), 1));
            }
        }

        return generatedLoot;
    }

    public static final MapCodec<NetherFortressLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LOOT_CONDITIONS_CODEC.fieldOf("conditions").forGetter(lm -> lm.conditions),
            BuiltInRegistries.ITEM.getCodec().fieldOf("item").forGetter(modifier -> modifier.item),
            Codec.FLOAT.fieldOf("probability").forGetter(modifier -> modifier.probability)
    ).apply(instance, NetherFortressLootModifier::new));


    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}