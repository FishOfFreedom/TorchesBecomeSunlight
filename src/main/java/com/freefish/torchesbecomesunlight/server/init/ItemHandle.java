package com.freefish.torchesbecomesunlight.server.init;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.item.help.AnimationItem;
import com.freefish.torchesbecomesunlight.server.item.help.TestItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemHandle {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, TorchesBecomeSunlight.MOD_ID);

    public static final RegistryObject<Item> TEST_ITEM = ITEMS.register("test_item",
            () -> new TestItem(new Item.Properties()));

    public static final RegistryObject<Item> Animation_ITEM = ITEMS.register("animation_item",
            () -> new AnimationItem(new Item.Properties()));



    public static final RegistryObject<SpawnEggItem> PATRIOT_EGG =
            ITEMS.register("patriot_egg", () ->
                    new ForgeSpawnEggItem(EntityHandle.PATRIOT, 0X6d5f20, 0Xf30f0f, new Item.Properties()));

    public static final RegistryObject<SpawnEggItem> SNOW_NOVA_EGG =
            ITEMS.register("frost_nova_egg", () ->
                    new ForgeSpawnEggItem(EntityHandle.FROST_NOVA, 0X70f3e8, 0Xf30f0f, new Item.Properties()));

    public static final RegistryObject<SpawnEggItem> PURSUER_EGG =
            ITEMS.register("pursuer_egg", () ->
                    new ForgeSpawnEggItem(EntityHandle.PURSUER, 0X727272, 0X000000, new Item.Properties()));

    public static final RegistryObject<SpawnEggItem> SHIELD_GUARD_EGG =
            ITEMS.register("shield_guard_egg", () ->
                    new ForgeSpawnEggItem(EntityHandle.SHIELD_GUARD, 0X052bf3, 0Xf30f0f, new Item.Properties()));

    public static final RegistryObject<SpawnEggItem> MANGLER_EGG =
            ITEMS.register("mangler_egg", () ->
                    new ForgeSpawnEggItem(EntityHandle.MANGLER, 0X5f5309, 0X727272, new Item.Properties()));
}
