package com.freefish.torchesbecomesunlight.server.block.blockentity;

import com.freefish.torchesbecomesunlight.server.init.BlockEntityHandle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ShaderBlockEntity extends BlockEntity {
    protected ShaderBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public ShaderBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityHandle.SHADER.get(), blockPos, blockState);
    }

    public boolean shouldRenderFace(Direction pFace) {
        return pFace.getAxis() == Direction.Axis.Y;
    }
}
