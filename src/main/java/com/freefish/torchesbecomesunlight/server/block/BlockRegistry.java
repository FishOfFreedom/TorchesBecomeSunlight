package com.freefish.torchesbecomesunlight.server.block;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.item.ItemRegistry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class BlockRegistry {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, TorchesBecomeSunlight.MOD_ID);

    public static final RegistryObject<Block> ORE_AND_ICE = registryBlock("ore_and_ice",() -> new WoolCarpetBlock(DyeColor.WHITE, BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).strength(0.1F).sound(SoundType.WOOL).friction(0.98F)),false);

    private static <T extends Block> RegistryObject<T> registryBlock(String name, Supplier<T> block, boolean isEntity) {
        RegistryObject<T> register = BLOCKS.register(name, block);
        registerBlockItem(name, register, new Item.Properties(), isEntity);
        return register;
    }

    private static <T extends Block> RegistryObject<T> registryBlock(String name, Supplier<T> block, boolean isEntity, Item.Properties properties) {
        RegistryObject<T> register = BLOCKS.register(name, block);
        registerBlockItem(name, register, properties, isEntity);
        return register;
    }

    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block, Item.Properties properties, boolean isEntityBlock) {
        ItemRegistry.ITEMS.register(name, () -> new BlockItem(block.get(), properties));
    }
}
