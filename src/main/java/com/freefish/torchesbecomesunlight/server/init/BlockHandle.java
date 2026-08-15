package com.freefish.torchesbecomesunlight.server.init;

import net.minecraft.core.registries.BuiltInRegistries;
import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.block.*;
import com.freefish.torchesbecomesunlight.server.block.furniture.DirectionalBlock;
import com.freefish.torchesbecomesunlight.server.block.furniture.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

public class BlockHandle {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(BuiltInRegistries.BLOCK, TorchesBecomeSunlight.MOD_ID);

    public static final DeferredHolder<Block, Block> ORE_AND_ICE = registryBlockNoItem("ore_and_ice",() -> new WoolCarpetBlock(DyeColor.WHITE, BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).strength(0.1F).sound(SoundType.GLASS).friction(0.98F)),false);

    public static final DeferredHolder<Block, Block> STEW_POT = BLOCKS.register("stew_pot",
            () -> new StewPotBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion()));

    public static final DeferredHolder<Block, Block> CUTTING_BOARD = BLOCKS.register("cutting_board",
            () -> new CuttingBoardBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion()));

    public static final DeferredHolder<Block, Block> OVEN = BLOCKS.register("oven",
            () -> new OvenBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion()));

    public static final DeferredHolder<Block, Block> BIG_BEN = BLOCKS.register("big_ben",
            () -> new BigBenBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));

    public static final DeferredHolder<Block, Block> STARGATE_FLAME = registryBlock("stargate_flame", GlowBlock::new,false);

    public static final DeferredHolder<Block, Block> STARGATE_CONTROLER = registryBlock("stargate_controler", StargateControlerBlock::new,true);

    public static final DeferredHolder<Block, Block> ELEVATOR = registryBlock("elevator",
            () -> new ElevatorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion().noCollission()),true);

    public static final DeferredHolder<Block, Block> ELEVATOR_DOOR = registryBlock("elevator_door",
            () -> new ElevatorDoorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion().noCollission()),true);

    public static final DeferredHolder<Block, Block> ELEVATOR_CONTROLLER = registryBlock("elevator_controller",
            () -> new ElevatorControlBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.REDSTONE_WIRE).noOcclusion()),true);

    public static final DeferredHolder<Block, Block> PATH_SIGN = registryBlock("path_sign",
            () -> new DirectionalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.REDSTONE_WIRE).noOcclusion()),false);

    public static final DeferredHolder<Block, Block> PATH_SIGN_2 = registryBlock("path_sign_2",
            () -> new DirectionalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.REDSTONE_WIRE).noOcclusion()),false);

    public static final DeferredHolder<Block, Block> ROUND_STOOL = registryBlock("round_stool",
            () -> new RoundStool(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD).noOcclusion()),false);

    public static final DeferredHolder<Block, Block> THERMAL_KETTLE = registryBlock("thermal_kettle",
            () -> new ThermalKettle(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion()),false);

    public static final DeferredHolder<Block, Block> CARTON = registryBlock("carton",
            () -> new Carton(BlockBehaviour.Properties.ofFullCopy(Blocks.HAY_BLOCK).noOcclusion()),false);

    public static final DeferredHolder<Block, Block> BIG_CARTON = registryBlock("big_carton",
            () -> new BigCarton(BlockBehaviour.Properties.ofFullCopy(Blocks.HAY_BLOCK).noOcclusion()),false);

    public static final DeferredHolder<Block, Block> LIGHT_TUBE = registryBlock("light_tube",
            () -> new LightTube(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).noOcclusion().lightLevel(state -> 15)),false);

    public static final DeferredHolder<Block, Block> CARGO_RACK = registryBlock("cargo_rack",
            () -> new CargoRack(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion()),false);

    //vertical_slab
    public static final DeferredHolder<Block, VerticalSlabBlock> GRAY_CONCRETE_VERTICAL_SLAB = registryVerticalSlab("gray_concrete_vertical_slab",Blocks.GRAY_CONCRETE);
    public static final DeferredHolder<Block, VerticalSlabBlock> WHITE_CONCRETE_VERTICAL_SLAB = registryVerticalSlab("white_concrete_vertical_slab",Blocks.WHITE_CONCRETE);
    public static final DeferredHolder<Block, VerticalSlabBlock> YELLOW_CONCRETE_VERTICAL_SLAB = registryVerticalSlab("yellow_concrete_vertical_slab",Blocks.YELLOW_CONCRETE);
    public static final DeferredHolder<Block, VerticalSlabBlock> LIGHT_GRAY_CONCRETE_VERTICAL_SLAB = registryVerticalSlab("light_gray_concrete_vertical_slab",Blocks.LIGHT_GRAY_CONCRETE);
    //slab
    public static final DeferredHolder<Block, SlabBlock> GRAY_CONCRETE_SLAB = registerSlab("gray_concrete_slab",Blocks.GRAY_CONCRETE);
    public static final DeferredHolder<Block, SlabBlock> WHITE_CONCRETE_SLAB = registerSlab("white_concrete_slab",Blocks.WHITE_CONCRETE);
    public static final DeferredHolder<Block, SlabBlock> YELLOW_CONCRETE_SLAB = registerSlab("yellow_concrete_slab",Blocks.YELLOW_CONCRETE);
    public static final DeferredHolder<Block, SlabBlock> LIGHT_GRAY_CONCRETE_SLAB = registerSlab("light_gray_concrete_slab",Blocks.LIGHT_GRAY_CONCRETE);
    //stairs
    public static final DeferredHolder<Block, StairBlock> GRAY_CONCRETE_STAIRS = registerStairs("gray_concrete_stairs",Blocks.GRAY_CONCRETE);
    public static final DeferredHolder<Block, StairBlock> WHITE_CONCRETE_STAIRS = registerStairs("white_concrete_stairs",Blocks.WHITE_CONCRETE);
    public static final DeferredHolder<Block, StairBlock> YELLOW_CONCRETE_STAIRS = registerStairs("yellow_concrete_stairs",Blocks.YELLOW_CONCRETE);
    public static final DeferredHolder<Block, StairBlock> LIGHT_GRAY_CONCRETE_STAIRS = registerStairs("light_gray_concrete_stairs",Blocks.LIGHT_GRAY_CONCRETE);

    public static void registerBlockFurniture(final String name, final Block t){
        registerStairs(name,t);
        registerSlab(name,t);
        registryVerticalSlab(name,t);
    }
    public static DeferredHolder<Block, StairBlock> registerStairs(final String name, final Block t){
        return registryBlock(name, () -> new StairBlock(() -> t.defaultBlockState(), Block.Properties.ofFullCopy(t)),false);
    }
    public static DeferredHolder<Block, SlabBlock> registerSlab(final String name, final Block t){
        return registryBlock(name, () -> new SlabBlock(Block.Properties.ofFullCopy(t)),false);
    }
    private static DeferredHolder<Block, VerticalSlabBlock> registryVerticalSlab(String name, Block blockState) {
        return registryBlock(name,
                () -> new VerticalSlabBlock(() -> blockState.defaultBlockState(),BlockBehaviour.Properties.ofFullCopy(blockState).noOcclusion()),false);
    }

    private static <T extends Block> DeferredHolder<Block, T> registryBlock(String name, Supplier<T> block, boolean isEntity) {
        DeferredHolder<Block, T> register = BLOCKS.register(name, block);
        registerBlockItem(name, register, new Item.Properties(), isEntity);
        return register;
    }

    private static <T extends Block> DeferredHolder<Block, T> registryBlockNoItem(String name, Supplier<T> block, boolean isEntity) {
        DeferredHolder<Block, T> register = BLOCKS.register(name, block);
        return register;
    }

    private static <T extends Block> DeferredHolder<Block, T> registryBlock(String name, Supplier<T> block, boolean isEntity, Item.Properties properties) {
        DeferredHolder<Block, T> register = BLOCKS.register(name, block);
        registerBlockItem(name, register, properties, isEntity);
        return register;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredHolder<Block, T> block, Item.Properties properties, boolean isEntityBlock) {
        ItemHandle.ITEMS.register(name, () -> new BlockItem(block.get(), properties));
    }
}
