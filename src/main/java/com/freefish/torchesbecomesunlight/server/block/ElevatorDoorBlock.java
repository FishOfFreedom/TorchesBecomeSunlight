package com.freefish.torchesbecomesunlight.server.block;

import com.freefish.torchesbecomesunlight.server.block.blockentity.ElevatorDoorBlockEntity;
import com.freefish.torchesbecomesunlight.server.block.furniture.furniturenum.Door;
import com.freefish.torchesbecomesunlight.server.init.BlockEntityHandle;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ElevatorDoorBlock extends BaseEntityBlock {
    public static final EnumProperty<Door> DOOR_ENUM_PROPERTY = EnumProperty.create("door", Door.class);

    public static final VoxelShape SHAPE_RIGHT = Block.box(0, 0, 0, 32, 32, 2);
    public static final VoxelShape SHAPE_LEFT = Block.box(-16, 0, 0, 16, 32, 2);
    public static final VoxelShape SHAPE_1 = Block.box(0, 0, 0, 16, 32, 2);

    public ElevatorDoorBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState().setValue(DOOR_ENUM_PROPERTY, Door.CLOSE_R));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Door value = pState.getValue(DOOR_ENUM_PROPERTY);
        return switch (value){
            case CLOSE_L -> SHAPE_RIGHT;
            case CLOSE_R -> SHAPE_LEFT;
            default -> SHAPE_1;
        };
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(DOOR_ENUM_PROPERTY);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Door door = (pContext.getPlayer()!=null&&pContext.getPlayer().isShiftKeyDown())?Door.CLOSE_L:Door.CLOSE_R;
        return super.getStateForPlacement(pContext).setValue(DOOR_ENUM_PROPERTY,door);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Door value = pState.getValue(DOOR_ENUM_PROPERTY);
        return switch (value){
            case CLOSE_L -> SHAPE_RIGHT;
            case CLOSE_R -> SHAPE_LEFT;
            default -> SHAPE_1;
        };
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ElevatorDoorBlockEntity(pPos,pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, BlockEntityHandle.ELEVATOR_DOOR.get(),
                ElevatorDoorBlockEntity::tick);
    }
}
