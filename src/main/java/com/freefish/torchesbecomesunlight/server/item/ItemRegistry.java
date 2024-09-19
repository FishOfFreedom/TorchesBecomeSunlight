package com.freefish.torchesbecomesunlight.server.item;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.item.help.TestItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, TorchesBecomeSunlight.MOD_ID);

    public static final RegistryObject<Item> TEST_ITEM = ITEMS.register("test_item",
            () -> new TestItem(new Item.Properties()));

    public static final RegistryObject<Item> SPEED_ITEM = ITEMS.register("speed_item",
            () -> new TestItem(new Item.Properties()));
}
