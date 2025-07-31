package com.freefish.torchesbecomesunlight.server.init;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.item.DemonEye;
import com.freefish.torchesbecomesunlight.server.item.SanktaRing;
import com.freefish.torchesbecomesunlight.server.item.armor.DeerHelmetItem;
import com.freefish.torchesbecomesunlight.server.item.armor.RosmontisEmbraceItem;
import com.freefish.torchesbecomesunlight.server.item.armor.WinterScratchItem;
import com.freefish.torchesbecomesunlight.server.item.armor.patrolcaptain.PatrolCaptainArmorItem;
import com.freefish.torchesbecomesunlight.server.item.armor.patrolcaptain.PreparationOpArmorItem;
import com.freefish.torchesbecomesunlight.server.item.food.CustomFoodProperties;
import com.freefish.torchesbecomesunlight.server.item.food.TBSFood;
import com.freefish.torchesbecomesunlight.server.item.geoItem.BigBenItem;
import com.freefish.torchesbecomesunlight.server.item.geoItem.StewPotItem;
import com.freefish.torchesbecomesunlight.server.item.help.*;
import com.freefish.torchesbecomesunlight.server.item.weapon.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemHandle {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, TorchesBecomeSunlight.MOD_ID);

    public static final RegistryObject<Item> TEST_ITEM = ITEMS.register("test_item",
            () -> new TestItem(new Item.Properties()));

    public static final RegistryObject<Item> Animation_ITEM = ITEMS.register("animation_item",
            () -> new AnimationItem(new Item.Properties()));

    public static final RegistryObject<Machete> URSUS_MACHETE = ITEMS.register("ursus_machete",
            () ->new Machete((new Item.Properties()).fireResistant()));

    public static final RegistryObject<SacredHalberd> SACRED_HALBERD = ITEMS.register("sacred_halberd",
            () ->new SacredHalberd((new Item.Properties()).fireResistant()));

    public static final RegistryObject<Item> GUN = ITEMS.register("gun",
            () ->new Gun((new Item.Properties()).fireResistant().stacksTo(1)));

    public static final RegistryObject<InfectedHalberd> INFECTED_HALBERD = ITEMS.register("halberd",
            () ->new InfectedHalberd(new Item.Properties().fireResistant()));

    public static final RegistryObject<Item> INFECTED_SHIELD = ITEMS.register("shield",
            () ->new InfectedShield((new Item.Properties()).fireResistant().stacksTo(1)));

    public static final RegistryObject<DeerHelmetItem> DEER_HELMET = ITEMS.register("deer_helmet",
            () -> new DeerHelmetItem(ArmorMaterials.DIAMOND, ArmorItem.Type.HELMET, new Item.Properties()));

    public static final RegistryObject<PatrolCaptainArmorItem> PATROL_CAPTAIN_HELMET = ITEMS.register("patrol_captain_helmet",
            () -> new PatrolCaptainArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.HELMET, new Item.Properties()));

    public static final RegistryObject<PatrolCaptainArmorItem> PATROL_CAPTAIN_LEGGINGS = ITEMS.register("patrol_captain_leggings",
            () -> new PatrolCaptainArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.LEGGINGS, new Item.Properties()));

    public static final RegistryObject<PatrolCaptainArmorItem> PATROL_CAPTAIN_CHESTPLATE = ITEMS.register("patrol_captain_chestplate",
            () -> new PatrolCaptainArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.CHESTPLATE, new Item.Properties()));

    public static final RegistryObject<PatrolCaptainArmorItem> PATROL_CAPTAIN_BOOTS = ITEMS.register("patrol_captain_boots",
            () -> new PatrolCaptainArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.BOOTS, new Item.Properties()));

    public static final RegistryObject<PreparationOpArmorItem> PREPARATION_OP_HELMET = ITEMS.register("preparation_op_helmet",
            () -> new PreparationOpArmorItem(ArmorMaterials.IRON, ArmorItem.Type.HELMET, new Item.Properties()));

    public static final RegistryObject<PreparationOpArmorItem> PREPARATION_OP_LEGGINGS = ITEMS.register("preparation_op_leggings",
            () -> new PreparationOpArmorItem(ArmorMaterials.IRON, ArmorItem.Type.LEGGINGS, new Item.Properties()));

    public static final RegistryObject<PreparationOpArmorItem> PREPARATION_OP_CHESTPLATE = ITEMS.register("preparation_op_chestplate",
            () -> new PreparationOpArmorItem(ArmorMaterials.IRON, ArmorItem.Type.CHESTPLATE, new Item.Properties()));

    public static final RegistryObject<PreparationOpArmorItem> PREPARATION_OP_BOOTS = ITEMS.register("preparation_op_boots",
            () -> new PreparationOpArmorItem(ArmorMaterials.IRON, ArmorItem.Type.BOOTS, new Item.Properties()));

    public static final RegistryObject<WinterPass> WINTER_PASS = ITEMS.register("winter_pass",
            () ->new WinterPass((new Item.Properties()).fireResistant()));

    public static final RegistryObject<WinterScratchItem> WINTER_SCRATCH = ITEMS.register("winter_scratch",
            () -> new WinterScratchItem(ArmorMaterials.DIAMOND, ArmorItem.Type.CHESTPLATE, new Item.Properties()));

    public static final RegistryObject<PhantomGrasp> PHANTOM_GRASP = ITEMS.register("phantom_grasp",
            () ->new PhantomGrasp((new Item.Properties()).fireResistant()));

    public static final RegistryObject<RosmontisEmbraceItem> ROSMONTIS_EMBRACE = ITEMS.register("rosmontis_embrace",
            () -> new RosmontisEmbraceItem(ArmorMaterials.DIAMOND, ArmorItem.Type.CHESTPLATE, new Item.Properties()));

    public static final RegistryObject<Item> INFECTED_GUN = ITEMS.register("pathfinder_gun",
            () ->new PathfinderGun((new Item.Properties()).fireResistant().stacksTo(1)));

    public static final RegistryObject<Item> PATHFINDER_SHIELD = ITEMS.register("pathfinder_shield",
            () ->new PathfinderShield((new Item.Properties()).fireResistant().durability(1024)));

    public static final RegistryObject<Item> GUARD_SHIELD = ITEMS.register("guard_shield",
            () ->new GuardShield((new Item.Properties()).fireResistant().durability(768)));

    public static final RegistryObject<IceBroadsword> ICE_BROADSWORD = ITEMS.register("ice_broadsword",
            () ->new IceBroadsword((new Item.Properties())));

    public static final RegistryObject<Item> LIGHT =
            ITEMS.register("light", () ->
                    new LightGun(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> TIME =
            ITEMS.register("time", () ->
                    new TimeGun(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> MACHETE =
            ITEMS.register("machete", () ->
                    new SwordItem(Tiers.DIAMOND, 3, -2.4F, new Item.Properties()){
                        @Override
                        public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
                            super.appendHoverText(stack, level, tooltip, flagIn);
                            tooltip.add(Component.translatable("text.torchesbecomesunlight.machete1"));
                        }
                    });

    public static final RegistryObject<Item> TITLE =
            ITEMS.register("title", () ->
                    new Item(new Item.Properties()));

    public static final RegistryObject<Item> BLESSING_OF_SAMI =
            ITEMS.register("blessing_of_sami", () ->
                    new Item(new Item.Properties()){
                        @Override
                        public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
                            super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
                            pTooltipComponents.add(Component.translatable("text.torchesbecomesunlight.blessing_of_sami"));
                            pTooltipComponents.add(Component.translatable("text.torchesbecomesunlight.blessing_of_sami_tool"));
                        }
                    });

    public static final RegistryObject<Item> SANKTA_RING =
            ITEMS.register("sankta_ring", () ->
                    new SanktaRing(new Item.Properties().rarity(Rarity.EPIC).stacksTo(1)));

    public static final RegistryObject<Item> DEMON_EYE =
            ITEMS.register("demon_eye", () ->
                    new DemonEye(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> RHODESISLAND_EYE =
            ITEMS.register("rhodes_island_eye", RhodesIslandEye::new);

    public static final RegistryObject<Item> SANKTA_STATUE_EYE =
            ITEMS.register("sankta_statue_eye", SanktaStatueEye::new);

    public static final RegistryObject<Item> RHODES_SHIELD = ITEMS.register("rhodes_shield",
            () ->new RhodesShield((new Item.Properties().durability(512))));

    public static final RegistryObject<Item> RHODES_KNIFE = ITEMS.register("rhodes_knife",
            () ->new RhodesKnife((new Item.Properties())));

    public static final RegistryObject<Item> ROSMONTIS_IPAD =
            ITEMS.register("rosmontis_ipad", () ->
                    new RosmontisIpad(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> THORN_RING =
            ITEMS.register("thorn_ring", () ->
                    new Item(new Item.Properties()){
                        @Override
                        public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
                            super.appendHoverText(stack, level, tooltip, flagIn);
                            tooltip.add(Component.translatable("text.torchesbecomesunlight.thorn_ring"));
                        }
                    });

    public static final RegistryObject<Item> BRONZE_SKIN =
            ITEMS.register("bronze_skin", () ->
                    new Item(new Item.Properties()){
                        @Override
                        public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
                            super.appendHoverText(stack, level, tooltip, flagIn);
                            tooltip.add(Component.translatable("text.torchesbecomesunlight.bronze_skin"));
                        }
                    });

    public static final RegistryObject<Item> FROSTED_TALISMAN =
            ITEMS.register("frosted_talisman", () ->
                    new FrostedTalisman(new Item.Properties()));

    public static final RegistryObject<Item> TALISMAN =
            ITEMS.register("talisman", () ->
                    new Talisman(new Item.Properties()));

    public static final RegistryObject<Item> THIGH_MEAT =
            ITEMS.register("thigh_meat", () ->
                    new Item(new Item.Properties().food(CustomFoodProperties.THIGH_MEAT)){
                        @Override
                        public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
                            super.appendHoverText(stack, level, tooltip, flagIn);
                            tooltip.add(Component.translatable("text.torchesbecomesunlight.thigh_meat"));
                        }
                    });

    public static final RegistryObject<Item> COOKED_THIGH_MEAT =
            ITEMS.register("cooked_thigh_meat", () ->
                    new Item(new Item.Properties().food(CustomFoodProperties.COOKED_THIGH_MEAT)){
                        @Override
                        public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
                            super.appendHoverText(stack, level, tooltip, flagIn);
                            tooltip.add(Component.translatable("text.torchesbecomesunlight.cooked_thigh_meat"));
                        }
                    });

    public static final RegistryObject<Item> BURDENBEAST_MEAT =
            ITEMS.register("burdenbeast_meat", () -> new Item(new Item.Properties().food(CustomFoodProperties.BURDENBEAST_MEAT)){
                @Override
                public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
                    super.appendHoverText(stack, level, tooltip, flagIn);
                    tooltip.add(Component.translatable("text.torchesbecomesunlight.burdenbeast_meat"));
                }
            });

    public static final RegistryObject<Item> COOKED_BURDENBEAST_MEAT =
            ITEMS.register("cooked_burdenbeast_meat", () ->
                    new Item(new Item.Properties().food(CustomFoodProperties.COOKED_BURDENBEAST_MEAT)){
                        @Override
                        public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
                            super.appendHoverText(stack, level, tooltip, flagIn);
                            tooltip.add(Component.translatable("text.torchesbecomesunlight.cooked_burdenbeast_meat"));
                        }
                    });

    //食物
    public static final RegistryObject<Item> BORSCHT =
            ITEMS.register("borscht", () ->
                    new TBSFood(12,0.6f,2){
                        @Override
                        public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> tooltip, TooltipFlag pIsAdvanced) {
                            super.appendHoverText(pStack, pLevel, tooltip, pIsAdvanced);
                            tooltip.add(Component.translatable("text.torchesbecomesunlight.borscht"));
                        }
                    });

    public static final RegistryObject<Item> POLAR_STEW_MEAT =
            ITEMS.register("polar_stew_meat", () ->
                    new TBSFood(18,0.8f,3){
                        @Override
                        public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> tooltip, TooltipFlag pIsAdvanced) {
                            super.appendHoverText(pStack, pLevel, tooltip, pIsAdvanced);
                            tooltip.add(Component.translatable("text.torchesbecomesunlight.polar_stew_meat"));
                        }
                    });

    public static final RegistryObject<Item> BAKED_COD_POTATO =
            ITEMS.register("baked_cod_potato", () ->
                    new TBSFood(18,0.8f,3){
                        @Override
                        public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> tooltip, TooltipFlag pIsAdvanced) {
                            super.appendHoverText(pStack, pLevel, tooltip, pIsAdvanced);
                            tooltip.add(Component.translatable("text.torchesbecomesunlight.baked_cod_potato"));
                        }
                    });

    public static final RegistryObject<Item> OIL_SALAD =
            ITEMS.register("oil_salad", () ->
                    new TBSFood(18,0.8f,3){
                        @Override
                        public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> tooltip, TooltipFlag pIsAdvanced) {
                            super.appendHoverText(pStack, pLevel, tooltip, pIsAdvanced);
                            tooltip.add(Component.translatable("text.torchesbecomesunlight.oil_salad"));
                        }
                    });

    public static final RegistryObject<Item> MASHED_POTATO_STEAK =
            ITEMS.register("mashed_potato_steak", () ->
                    new TBSFood(18,0.8f,3){
                        @Override
                        public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> tooltip, TooltipFlag pIsAdvanced) {
                            super.appendHoverText(pStack, pLevel, tooltip, pIsAdvanced);
                            tooltip.add(Component.translatable("text.torchesbecomesunlight.mashed_potato_steak"));
                        }
                    });

    public static final RegistryObject<Item> APPLE_PIE =
            ITEMS.register("apple_pie", () ->
                    new TBSFood(18,0.8f,3){
                        @Override
                        public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> tooltip, TooltipFlag pIsAdvanced) {
                            super.appendHoverText(pStack, pLevel, tooltip, pIsAdvanced);
                            tooltip.add(Component.translatable("text.torchesbecomesunlight.apple_pie"));
                        }
                    });

    public static final RegistryObject<Item> BEEF_BEET_DUMPLING =
            ITEMS.register("beef_beet_dumpling", () ->
                    new TBSFood(18,0.8f,3){
                        @Override
                        public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> tooltip, TooltipFlag pIsAdvanced) {
                            super.appendHoverText(pStack, pLevel, tooltip, pIsAdvanced);
                            tooltip.add(Component.translatable("text.torchesbecomesunlight.beef_beet_dumpling"));
                        }
                    });

    public static final RegistryObject<Item> URSUS_BREAD =
            ITEMS.register("ursus_bread", () ->
                    new TBSFood(18,0.8f,3){
                        @Override
                        public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> tooltip, TooltipFlag pIsAdvanced) {
                            super.appendHoverText(pStack, pLevel, tooltip, pIsAdvanced);
                            tooltip.add(Component.translatable("text.torchesbecomesunlight.ursus_bread"));
                        }
                    });

    public static final RegistryObject<Item> BURDENBEAST_BURGER =
            ITEMS.register("burdenbeast_burger", () ->
                    new TBSFood(18,0.8f,3){
                        @Override
                        public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> tooltip, TooltipFlag pIsAdvanced) {
                            super.appendHoverText(pStack, pLevel, tooltip, pIsAdvanced);
                            tooltip.add(Component.translatable("text.torchesbecomesunlight.burdenbeast_burger"));
                        }
                    });

    public static final RegistryObject<Item> DOUGH =
            ITEMS.register("dough", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SOUR_CREAM =
            ITEMS.register("sour_cream", () -> new Item(new Item.Properties()));
    //邪魔
    public static final RegistryObject<SpawnEggItem> CRAZELYSEON_EGG =
            ITEMS.register("crazelyseon_egg", () ->
                    new ForgeSpawnEggItem(EntityHandle.CRAZELYSEON, 0X1010e8, 0Xf30f0f, new Item.Properties()));

    //罗德岛
    public static final RegistryObject<SpawnEggItem> ROSMONTIS_EGG =
            ITEMS.register("rosmontis_egg", () ->
                    new ForgeSpawnEggItem(EntityHandle.ROSMONTIS, 0X1010e8, 0Xf30f0f, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> PREPARATION_OP_EGG =
            ITEMS.register("preparation_op_egg", () ->
                    new ForgeSpawnEggItem(EntityHandle.PREPARATION_OP, 0X1010e8, 0Xf30f0f, new Item.Properties()));


    public static final RegistryObject<SpawnEggItem> PATRIOT_EGG =
            ITEMS.register("patriot_egg", () ->
                    new ForgeSpawnEggItem(EntityHandle.PATRIOT, 0X6d5f20, 0Xf30f0f, new Item.Properties()));

    public static final RegistryObject<SpawnEggItem> PATHFINDER_BALL_EGG =
            ITEMS.register("pathfinder_ballistarius_egg", () ->
                    new ForgeSpawnEggItem(EntityHandle.PATHFINDER_BALL, 0X6d5f20, 0Xf30f0f, new Item.Properties()));

    public static final RegistryObject<SpawnEggItem> GUN_PATRIOT_EGG =
            ITEMS.register("gun_patriot_egg", () ->
                    new TwoStateSpawnEggItem(EntityHandle.GUN_KNIGHT_PATRIOT, 0X6d5f20, 0Xf30f0f, new Item.Properties()));

    //public static final RegistryObject<SpawnEggItem> SAINT_GUARD_EGG =
    //        ITEMS.register("saint_guard_egg", () ->
    //                new ForgeSpawnEggItem(EntityHandle.SAINT_GUARD, 0X6d5f20, 0Xf30f0f, new Item.Properties()));

    public static final RegistryObject<SpawnEggItem> SNOW_NOVA_EGG =
            ITEMS.register("frost_nova_egg", () ->
                    new ForgeSpawnEggItem(EntityHandle.FROST_NOVA, 0X70f3e8, 0Xf30f0f, new Item.Properties()));

    public static final RegistryObject<SpawnEggItem> PURSUER_EGG =
            ITEMS.register("pursuer_egg", () ->
                    new ForgeSpawnEggItem(EntityHandle.PURSUER, 0X727272, 0X000000, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> PATROL_CAPTAIN_EGG =
            ITEMS.register("patrol_captain_egg", () ->
                    new ForgeSpawnEggItem(EntityHandle.PATROL_CAPTAIN, 0X727272, 0X000000, new Item.Properties()));

    public static final RegistryObject<SpawnEggItem> SHIELD_GUARD_EGG =
            ITEMS.register("shield_guard_egg", () ->
                    new ForgeSpawnEggItem(EntityHandle.SHIELD_GUARD, 0X052bf3, 0Xf30f0f, new Item.Properties()));

    public static final RegistryObject<SpawnEggItem> YETI_ICE_LEAVER_EGG =
            ITEMS.register("yeti_ice_leaver_egg", () ->
                    new ForgeSpawnEggItem(EntityHandle.YETI_ICE_LEAVER, 0X052bf3, 0Xf30f0f, new Item.Properties()));

    public static final RegistryObject<SpawnEggItem> MELA_VILLAGER_EGG =
            ITEMS.register("male_villager_egg", () ->
                    new ForgeSpawnEggItem(EntityHandle.MALE, 0X052bf3, 0Xf30f0f, new Item.Properties()));

    public static final RegistryObject<SpawnEggItem> FEMALE_VILLAGER_EGG =
            ITEMS.register("female_villager_egg", () ->
                    new ForgeSpawnEggItem(EntityHandle.FEMALE, 0X052bf3, 0Xf30f0f, new Item.Properties()));

    public static final RegistryObject<SpawnEggItem> MANGLER_EGG =
            ITEMS.register("mangler_egg", () ->
                    new ForgeSpawnEggItem(EntityHandle.MANGLER, 0X5f5309, 0X727272, new Item.Properties()));

    public static final RegistryObject<SpawnEggItem> BURDENBEAST_EGG =
            ITEMS.register("burdenbeast_egg", () ->
                    new ForgeSpawnEggItem(EntityHandle.BURDENBEAST, 0X5f5309, 0X727272, new Item.Properties()));

    public static final RegistryObject<BlockItem> STEW_POT = ITEMS.register("stew_pot",
            () -> new StewPotItem(BlockHandle.STEW_POT.get(),
                    new Item.Properties()));

    public static final RegistryObject<BlockItem> CUTTING_BOARD = ITEMS.register("cutting_board",
            () -> new BlockItem(BlockHandle.CUTTING_BOARD.get(),
                    new Item.Properties()));

    public static final RegistryObject<BlockItem> OVEN = ITEMS.register("oven",
            () -> new BlockItem(BlockHandle.OVEN.get(),
                    new Item.Properties()));

    public static final RegistryObject<BlockItem> BIG_BEN = ITEMS.register("big_ben",
            () -> new BigBenItem(BlockHandle.BIG_BEN.get(),
                    new Item.Properties()));

    public static void initializeAttributes() {
        INFECTED_HALBERD.get().refreshAttributesFromConfig();
        URSUS_MACHETE.get().refreshAttributesFromConfig();
        SACRED_HALBERD.get().refreshAttributesFromConfig();
        WINTER_PASS.get().refreshAttributesFromConfig();
        PHANTOM_GRASP.get().refreshAttributesFromConfig();
        ICE_BROADSWORD.get().refreshAttributesFromConfig();
    }
}
