package com.freefish.torchesbecomesunlight.server.block.furniture;

import com.freefish.torchesbecomesunlight.server.block.furniture.furniturenum.LineRL;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CargoRack extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<LineRL> LINE_RL = EnumProperty.create("line", LineRL.class);

    public static final VoxelShape SHAPE = Shapes.or(
            Block.box(0, 0, 0, 16, 1, 16),
            Block.box(0, 1, 0, 1, 16, 1),
            Block.box(0, 1, 15, 1, 16, 16),
            Block.box(15, 1, 0, 16, 16, 1),
            Block.box(15, 1, 15, 16, 16, 16));

    public CargoRack(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING,Direction.NORTH).setValue(LINE_RL, LineRL.LINE));
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING,context.getHorizontalDirection()).setValue(LINE_RL,LineRL.LINE);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public BlockState updateShape(BlockState selfState, Direction direction, BlockState neighbourState, LevelAccessor level, BlockPos pos, BlockPos neighboutPos) {
        boolean hen = selfState.getValue(FACING) == Direction.NORTH||selfState.getValue(FACING) == Direction.SOUTH;
        BlockState blockState = level.getBlockState(pos.relative(direction.getOpposite()));
        if(hen){
            if(direction == Direction.NORTH||direction == Direction.SOUTH){
                boolean isNORTH = direction == Direction.NORTH;
                selfState = selfState.setValue(LINE_RL,setLineState(selfState,neighbourState,blockState,isNORTH));
            }
        }else {
            if(direction == Direction.EAST||direction == Direction.WEST){
                boolean isNORTH = direction == Direction.EAST;
                selfState = selfState.setValue(LINE_RL,setLineState(selfState,neighbourState,blockState,isNORTH));
            }
        }
        return selfState;
    }

    private static LineRL setLineState(BlockState self,BlockState blockState,BlockState OpblockState,boolean isLeft) {
        if(isLeft){
            if (isLightTube(self, blockState)){
                if(isLightTube(self,OpblockState)){
                    return LineRL.LINE_RL;
                }else {
                    return LineRL.LINE_L;
                }
            }else {
                if(isLightTube(self,OpblockState)){
                    return LineRL.LINE_R;
                }
            }
        }else {
            if (isLightTube(self, blockState)){
                if(isLightTube(self,OpblockState)){
                    return LineRL.LINE_RL;
                }else {
                    return LineRL.LINE_R;
                }
            }else {
                if(isLightTube(self,OpblockState)){
                    return LineRL.LINE_L;
                }
            }
        }

        return LineRL.LINE;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING).add(LINE_RL);
    }

    public static boolean isLightTube(BlockState blockState,BlockState blockState1) {
        if(!(blockState1.getBlock() instanceof CargoRack)){
            return false;
        }
        if(blockState.getValue(FACING) == Direction.NORTH||blockState.getValue(FACING) == Direction.SOUTH){
            if(blockState1.getValue(FACING) == Direction.EAST||blockState1.getValue(FACING) == Direction.WEST){
                return false;
            }
        }
        if(blockState.getValue(FACING) == Direction.EAST||blockState.getValue(FACING) == Direction.WEST){
            if(blockState1.getValue(FACING) == Direction.NORTH||blockState1.getValue(FACING) == Direction.SOUTH){
                return false;
            }
        }
        return true;
    }
}
