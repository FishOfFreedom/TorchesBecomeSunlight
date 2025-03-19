package com.freefish.torchesbecomesunlight.server.init;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.item.armor.DeerHelmetItem;
import com.freefish.torchesbecomesunlight.server.item.armor.WinterScratchItem;
import com.freefish.torchesbecomesunlight.server.item.food.CustomFoodProperties;
import com.freefish.torchesbecomesunlight.server.item.food.TBSFood;
import com.freefish.torchesbecomesunlight.server.item.help.AnimationItem;
import com.freefish.torchesbecomesunlight.server.item.weapon.*;
import com.freefish.torchesbecomesunlight.server.item.help.TestItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.Foods;
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

    public static final RegistryObject<Item> URSUS_MACHETE = ITEMS.register("ursus_machete",
            () ->new Machete(Tiers.NETHERITE, 15, -2.0F, (new Item.Properties()).fireResistant()));

    public static final RegistryObject<Item> GUN = ITEMS.register("gun",
            () ->new Gun((new Item.Properties()).fireResistant()));

    public static final RegistryObject<Item> INFECTED_HALBERD = ITEMS.register("halberd",
            () ->new InfectedHalberd((new Item.Properties()).fireResistant().durability(1000)));

    public static final RegistryObject<Item> INFECTED_SHIELD = ITEMS.register("shield",
            () ->new InfectedShield((new Item.Properties()).fireResistant()));

    public static final RegistryObject<Item> WINTER_PASS = ITEMS.register("winter_pass",
            () ->new WinterPass((new Item.Properties()).fireResistant()));

    public static final RegistryObject<DeerHelmetItem> DEER_HELMET = ITEMS.register("deer_helmet",
            () -> new DeerHelmetItem(ArmorMaterials.DIAMOND, ArmorItem.Type.HELMET, new Item.Properties()));

    public static final RegistryObject<WinterScratchItem> WINTER_SCRATCH = ITEMS.register("winter_scratch",
            () -> new WinterScratchItem(ArmorMaterials.DIAMOND, ArmorItem.Type.CHESTPLATE, new Item.Properties()));

    public static final RegistryObject<Item> TITLE =
            ITEMS.register("title", () ->
                    new Item(new Item.Properties()));

    public static final RegistryObject<Item> BLESSING_OF_SAMI =
            ITEMS.register("blessing_of_sami", () ->
                    new Item(new Item.Properties()){
                        @Override
                        public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
                            super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);pTooltipComponents.add(Component.translatable("text.torchesbecomesunlight.blessing_of_sami"));
                        }
                    });

    public static final RegistryObject<Item> THORN_RING =
            ITEMS.register("thorn_ring", () ->
                    new Item(new Item.Properties()));

    public static final RegistryObject<Item> BRONZE_SKIN =
            ITEMS.register("bronze_skin", () ->
                    new Item(new Item.Properties()));

    public static final RegistryObject<Item> FROSTED_TALISMAN =
            ITEMS.register("frosted_talisman", () ->
                    new Item(new Item.Properties()));

    public static final RegistryObject<Item> TALISMAN =
            ITEMS.register("talisman", () ->
                    new Item(new Item.Properties()));

    public static final RegistryObject<Item> THIGH_MEAT =
            ITEMS.register("thigh_meat", () ->
                    new Item(new Item.Properties().food(CustomFoodProperties.THIGH_MEAT)));

    public static final RegistryObject<Item> COOKED_THIGH_MEAT =
            ITEMS.register("cooked_thigh_meat", () ->
                    new TBSFood(new Item.Properties()));

    public static final RegistryObject<Item> MACHETE =
            ITEMS.register("machete", () ->
                    new Item(new Item.Properties()));

    public static final RegistryObject<SpawnEggItem> PATRIOT_EGG =
            ITEMS.register("patriot_egg", () ->
                    new ForgeSpawnEggItem(EntityHandle.PATRIOT, 0X6d5f20, 0Xf30f0f, new Item.Properties()));

    public static final RegistryObject<SpawnEggItem> GUN_PATRIOT_EGG =
            ITEMS.register("gun_patriot_egg", () ->
                    new ForgeSpawnEggItem(EntityHandle.GUN_KNIGHT_PATRIOT, 0X6d5f20, 0Xf30f0f, new Item.Properties()));

    public static final RegistryObject<SpawnEggItem> SNOW_NOVA_EGG =
            ITEMS.register("frost_nova_egg", () ->
                    new ForgeSpawnEggItem(EntityHandle.FROST_NOVA, 0X70f3e8, 0Xf30f0f, new Item.Properties()));

    public static final RegistryObject<SpawnEggItem> PURSUER_EGG =
            ITEMS.register("pursuer_egg", () ->
                    new ForgeSpawnEggItem(EntityHandle.PURSUER, 0X727272, 0X000000, new Item.Properties()));

    public static final RegistryObject<SpawnEggItem> SHIELD_GUARD_EGG =
            ITEMS.register("shield_guard_egg", () ->
                    new ForgeSpawnEggItem(EntityHandle.SHIELD_GUARD, 0X052bf3, 0Xf30f0f, new Item.Properties()));

    public static final RegistryObject<SpawnEggItem> MELA_VILLAGER_EGG =
            ITEMS.register("male_villager_egg", () ->
                    new ForgeSpawnEggItem(EntityHandle.MALE, 0X052bf3, 0Xf30f0f, new Item.Properties()));

    public static final RegistryObject<SpawnEggItem> FEMALE_VILLAGER_EGG =
            ITEMS.register("female_villager_egg", () ->
                    new ForgeSpawnEggItem(EntityHandle.FEMALE, 0X052bf3, 0Xf30f0f, new Item.Properties()));

    public static final RegistryObject<SpawnEggItem> MANGLER_EGG =
            ITEMS.register("mangler_egg", () ->
                    new ForgeSpawnEggItem(EntityHandle.MANGLER, 0X5f5309, 0X727272, new Item.Properties()));
}
