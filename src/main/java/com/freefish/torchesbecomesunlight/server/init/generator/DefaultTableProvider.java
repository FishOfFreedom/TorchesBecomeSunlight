package com.freefish.torchesbecomesunlight.server.init.generator;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.init.BlockHandle;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.init.ItemHandle;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithLootingCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultTableProvider extends LootTableProvider {
    public DefaultTableProvider(PackOutput pOutput) {
        super(pOutput, new HashSet<>(), List.of(new LootTableProvider.SubProviderEntry(ModBlockLootSubProvider::new, LootContextParamSets.BLOCK), new LootTableProvider.SubProviderEntry(ModEntityLootSubProvider::new, LootContextParamSets.ENTITY)));
    }

    private static final float[] DEFAULT_SAPLING_DROP_RATES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};

    public static class ModBlockLootSubProvider extends BlockLootSubProvider {
        public List<Block> list = new ArrayList<>();

        protected ModBlockLootSubProvider() {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), new HashMap<>());
        }

        @Override
        protected void generate() {
            registerDropSelf(BlockHandle.STEW_POT);
            registerDropSelf(BlockHandle.CUTTING_BOARD);
            registerDropSelf(BlockHandle.OVEN);
        }

        @Override
        protected void add(Block pBlock, LootTable.Builder pBuilder) {
            list.add(pBlock);
            super.add(pBlock, pBuilder);
        }

        @Override
        protected void add(Block pBlock, Function<Block, LootTable.Builder> pFactory) {
            list.add(pBlock);
            super.add(pBlock, pFactory);
        }

        protected void registerSlabItemTable(Block p_124291_) {
            list.add(p_124291_);
            this.add(p_124291_, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                    .add(applyExplosionDecay(p_124291_, LootItem.lootTableItem(p_124291_).apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F))
                            .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(p_124291_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SlabBlock.TYPE, SlabType.DOUBLE))))))));

        }

        // Override and ignore the missing loot table error
        @Override
        public void generate(BiConsumer<ResourceLocation, LootTable.Builder> p_249322_) {
            this.generate();
            Set<ResourceLocation> set = new HashSet<>();

            for (Block block : list) {
                if (block.isEnabled(this.enabledFeatures)) {
                    ResourceLocation resourcelocation = block.getLootTable();
                    if (resourcelocation != BuiltInLootTables.EMPTY && set.add(resourcelocation)) {
                        LootTable.Builder loottable$builder = this.map.remove(resourcelocation);
                        if (loottable$builder == null) {
                            continue;
                        }

                        p_249322_.accept(resourcelocation, loottable$builder);
                    }
                }
            }
        }

        protected <T extends Comparable<T> & StringRepresentable> void registerBedCondition(Block block, Property<T> prop, T isValue) {
            list.add(block);
            this.add(block, LootTable.lootTable().withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(block).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(prop, isValue)))))));
        }

        public void registerLeavesAndSticks(Block leaves, Block sapling) {
            list.add(leaves);
            this.add(leaves, l_state -> createLeavesDrops(l_state, sapling, DEFAULT_SAPLING_DROP_RATES));
        }

        public void registerDropDoor(Block block) {
            list.add(block);
            this.add(block, createDoorTable(block));
        }

        public void registerDropSelf(Block block) {
            list.add(block);
            dropSelf(block);
        }

        public void registerDropSelf(RegistryObject<Block> block) {
            list.add(block.get());
            dropSelf(block.get());
        }

        public void registerDrop(Block input, ItemLike output) {
            list.add(input);
            dropOther(input, output);
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return ForgeRegistries.BLOCKS.getValues().stream().filter(block -> ForgeRegistries.BLOCKS.getKey(block).getNamespace().equals(TorchesBecomeSunlight.MOD_ID)).collect(Collectors.toList());
        }
    }

    public static class ModEntityLootSubProvider extends EntityLootSubProvider {
        private final Map<EntityType<?>, Map<ResourceLocation, LootTable.Builder>> map = Maps.newHashMap();

        protected ModEntityLootSubProvider() {
            super(FeatureFlags.REGISTRY.allFlags());
        }

        @Override
        public void generate() {
            add(EntityHandle.MANGLER.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ItemHandle.THIGH_MEAT.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                                    .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)))))
            );
            add(EntityHandle.BURDENBEAST.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ItemHandle.BURDENBEAST_MEAT.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                                    .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)))))
            );
            add(EntityHandle.FROST_NOVA.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ItemHandle.WINTER_PASS.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                            ))
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ItemHandle.WINTER_SCRATCH.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                            )
                    ));
            add(EntityHandle.YETI_ICE_LEAVER.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ItemHandle.ICE_BROADSWORD.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                                    .when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.01F, 0.01F))
                            )
                    ));
            add(EntityHandle.PATRIOT.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ItemHandle.INFECTED_HALBERD.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                            ))
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ItemHandle.INFECTED_SHIELD.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                            )
                    ));
            add(EntityHandle.SHIELD_GUARD.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ItemHandle.GUARD_SHIELD.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                                    .when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.01F, 0.01F))
                            )
                    ));
            //rhodes
            add(EntityHandle.ROSMONTIS.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ItemHandle.ROSMONTIS_EMBRACE.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                            ))
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ItemHandle.PHANTOM_GRASP.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                            )
                    ));
            add(EntityHandle.PREPARATION_OP.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ItemHandle.RHODES_SHIELD.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                                    .when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.01F, 0.01F))
                            ))
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ItemHandle.RHODES_KNIFE.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                                    .when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.01F, 0.01F))
                            )
                    ));
            //ursus
            add(EntityHandle.PURSUER.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ItemHandle.URSUS_MACHETE.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                            )
                    ));
            add(EntityHandle.PATROL_CAPTAIN.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ItemHandle.MACHETE.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                                    .when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.01F, 0.01F))
                            )
                    )
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ItemHandle.PATROL_CAPTAIN_BOOTS.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                                    .when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.01F, 0.01F))
                            )
                    )
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ItemHandle.PATROL_CAPTAIN_CHESTPLATE.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                                    .when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.01F, 0.01F))
                            )
                    )
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ItemHandle.PATROL_CAPTAIN_HELMET.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                                    .when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.01F, 0.01F))
                            )
                    )
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ItemHandle.PATROL_CAPTAIN_LEGGINGS.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                                    .when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.01F, 0.01F))
                            )
                    )

            );
            //dlc
            add(EntityHandle.GUN_KNIGHT_PATRIOT.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ItemHandle.SACRED_HALBERD.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                            )
                    )
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ItemHandle.SANKTA_RING.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                            )
                    )
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ItemHandle.GUN.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                            )
                    ));
            add(EntityHandle.PATHFINDER_BALL.get(), LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ItemHandle.INFECTED_GUN.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                            )
                    )
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(ItemHandle.PATHFINDER_SHIELD.get())
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                            )
                    ));
        }

        @Override
        protected void add(EntityType<?> pEntityType, LootTable.Builder pBuilder) {
            super.add(pEntityType, pBuilder);
            this.map.put(pEntityType, ImmutableMap.of(pEntityType.getDefaultLootTable(), pBuilder));
        }

        @Override
        protected void add(EntityType<?> pEntityType, ResourceLocation pLootTableLocation, LootTable.Builder pBuilder) {
            super.add(pEntityType, pLootTableLocation, pBuilder);
            this.map.computeIfAbsent(pEntityType, (p_249004_) -> {
                return Maps.newHashMap();
            }).put(pLootTableLocation, pBuilder);
        }

        @Override
        public void generate(BiConsumer<ResourceLocation, LootTable.Builder> p_251751_) {
            this.generate();
            Set<ResourceLocation> set = Sets.newHashSet();
            this.getKnownEntityTypes().map(EntityType::builtInRegistryHolder).forEach((p_249003_) -> {
                EntityType<?> entitytype = p_249003_.value();
                if (canHaveLootTable(entitytype)) {
                    Map<ResourceLocation, LootTable.Builder> map = this.map.remove(entitytype);
                    ResourceLocation resourcelocation = entitytype.getDefaultLootTable();
                    if (map != null) {
                        map.forEach((p_250376_, p_250972_) -> {
                            if (!set.add(p_250376_)) {
                                throw new IllegalStateException(String.format(Locale.ROOT, "Duplicate loottable '%s' for '%s'", p_250376_, p_249003_.key().location()));
                            } else {
                                p_251751_.accept(p_250376_, p_250972_);
                            }
                        });
                    }
                } else {
                    Map<ResourceLocation, LootTable.Builder> map1 = this.map.remove(entitytype);
                    if (map1 != null) {
                        throw new IllegalStateException(String.format(Locale.ROOT, "Weird loottables '%s' for '%s', not a LivingEntity so should not have loot", map1.keySet().stream().map(ResourceLocation::toString).collect(Collectors.joining(",")), p_249003_.key().location()));
                    }
                }

            });
        }

        @Override
        protected Stream<EntityType<?>> getKnownEntityTypes() {
            return ForgeRegistries.ENTITY_TYPES.getValues().stream().filter(block -> ForgeRegistries.ENTITY_TYPES.getKey(block).getNamespace().equals(TorchesBecomeSunlight.MOD_ID)).toList().stream();
        }
    }
}
