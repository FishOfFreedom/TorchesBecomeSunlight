package com.freefish.torchesbecomesunlight.server.block.blockentity;

import com.freefish.torchesbecomesunlight.server.init.BlockEntityHandle;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ElevatorControllerBlockEntity extends BlockEntity {
    public ElevatorControllerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityHandle.ELEVATOR_CONTROLLER.get(), pPos, pBlockState);
    }
}
