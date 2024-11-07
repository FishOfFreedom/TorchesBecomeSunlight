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

    public static final RegistryObject<Item> SPEED_ITEM = ITEMS.register("speed_item",
            () -> new TestItem(new Item.Properties()));

    public static final RegistryObject<Item> Animation_ITEM = ITEMS.register("animation_item",
            () -> new AnimationItem(new Item.Properties()));

    public static final RegistryObject<SpawnEggItem> PATRIOT_EGG =
            ITEMS.register("patriot_egg", () ->
                    new ForgeSpawnEggItem(EntityHandle.PATRIOT, 4996656, 986895, new Item.Properties()));

    public static final RegistryObject<SpawnEggItem> SNOW_NOVA_EGG =
            ITEMS.register("snow_nova_egg", () ->
                    new ForgeSpawnEggItem(EntityHandle.SNOWNOVA, 4996656, 986895, new Item.Properties()));
}
