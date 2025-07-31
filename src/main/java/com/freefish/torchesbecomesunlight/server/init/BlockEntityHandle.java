package com.freefish.torchesbecomesunlight.server.init;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.block.blockentity.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntityHandle {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, TorchesBecomeSunlight.MOD_ID);

    public static final RegistryObject<BlockEntityType<StewPotBlockEntity>> STEW_POT =
            BLOCK_ENTITY.register("stew_pot_tile", () ->
                    BlockEntityType.Builder.of(StewPotBlockEntity::new,
                            BlockHandle.STEW_POT.get()).build(null));

    public static final RegistryObject<BlockEntityType<CuttingBoradBlockEntity>> CUTTING_BOARD =
            BLOCK_ENTITY.register("cutting_board_tile", () ->
                    BlockEntityType.Builder.of(CuttingBoradBlockEntity::new,
                            BlockHandle.CUTTING_BOARD.get()).build(null));

    public static final RegistryObject<BlockEntityType<OvenBlockEntity>> OVEN =
            BLOCK_ENTITY.register("oven_tile", () ->
                    BlockEntityType.Builder.of(OvenBlockEntity::new,
                            BlockHandle.OVEN.get()).build(null));

    public static final RegistryObject<BlockEntityType<ElevatorBlockEntity>> ELEVATOR =
            BLOCK_ENTITY.register("elevator_tile", () ->
                    BlockEntityType.Builder.of(ElevatorBlockEntity::new,
                            BlockHandle.ELEVATOR.get()).build(null));

    public static final RegistryObject<BlockEntityType<ElevatorDoorBlockEntity>> ELEVATOR_DOOR =
            BLOCK_ENTITY.register("elevator_door_tile", () ->
                    BlockEntityType.Builder.of(ElevatorDoorBlockEntity::new,
                            BlockHandle.ELEVATOR_DOOR.get()).build(null));

    public static final RegistryObject<BlockEntityType<ElevatorControllerBlockEntity>> ELEVATOR_CONTROLLER =
            BLOCK_ENTITY.register("elevator_controller_tile", () ->
                    BlockEntityType.Builder.of(ElevatorControllerBlockEntity::new,
                            BlockHandle.ELEVATOR_CONTROLLER.get()).build(null));

    public static final RegistryObject<BlockEntityType<BigBenBlockEntity>> BIG_BEN =
            BLOCK_ENTITY.register("big_ben_tile", () ->
                    BlockEntityType.Builder.of(BigBenBlockEntity::new,
                            BlockHandle.BIG_BEN.get()).build(null));


    public static final RegistryObject<BlockEntityType<StargateControlerBlockEntity>> STARGATE_CONTROLER =
            BLOCK_ENTITY.register("stargate_controler_tile", () ->
                    BlockEntityType.Builder.of(StargateControlerBlockEntity::new,
                            BlockHandle.STARGATE_CONTROLER.get()).build(null));

}
