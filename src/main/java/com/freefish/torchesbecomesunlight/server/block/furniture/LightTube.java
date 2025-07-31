package com.freefish.torchesbecomesunlight.server.block.furniture;

import com.freefish.torchesbecomesunlight.server.block.furniture.furniturenum.Corner;
import com.freefish.torchesbecomesunlight.server.block.furniture.furniturenum.LineRL;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LightTube extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    public static final EnumProperty<Corner> CORNER = EnumProperty.create("corner", Corner.class);
    public static final EnumProperty<LineRL> LINE_RL = EnumProperty.create("line", LineRL.class);
    protected static final VoxelShape[] TOP_SHAPES = makeShapes();

    private static VoxelShape[] makeShapes() {
        return new VoxelShape[]{
                Block.box(0,14,0,16,16,16),
                Block.box(0,0,0,16,2,16),
                Block.box(0,0,14,16,16,16),
                Block.box(0,0,0,16,16,2),
                Block.box(14,0,0,16,16,16),
                Block.box(0,0,0,2,16,16),
        };
    }

    public LightTube(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING,Direction.DOWN).setValue(CORNER, Corner.NORTH).setValue(LINE_RL, LineRL.LINE));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return TOP_SHAPES[pState.getValue(FACING).ordinal()];
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(CORNER).add(FACING).add(LINE_RL);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clickedFace = context.getClickedFace();
        Vec3 hitVec = context.getClickLocation();
        BlockPos clickedPos = context.getClickedPos();

        double xHit = hitVec.x - clickedPos.getX();
        double yHit = hitVec.y - clickedPos.getY();
        double zHit = hitVec.z - clickedPos.getZ();

        Direction.Axis axis = clickedFace.getAxis();
        double u, v;

        if (axis == Direction.Axis.X) {
            u = zHit;
            v = yHit;
        } else if (axis == Direction.Axis.Y) {
            u = xHit;
            v = zHit;
        } else {
            u = xHit;
            v = yHit;
        }
        Corner corner = getCorner(u, v);
        return defaultBlockState().setValue(CORNER, corner).setValue(FACING,clickedFace);
    }

    private Corner getCorner(double u, double v) {
        u-=0.5;
        v-=0.5;
        double r =Math.toDegrees(Math.atan2(v,u))+180;
        if (r<135&&r>45) {
            return Corner.NORTH;
        } else if (r<225&&r>=135) {
            return Corner.EAST;
        } else if (r<315&&r>=225) {
            return Corner.SOUTH;
        }
        return Corner.WEST;
    }

    @Override
    public BlockState updateShape(BlockState selfState, Direction direction, BlockState neighbourState, LevelAccessor level, BlockPos pos, BlockPos neighboutPos) {
        boolean hen = selfState.getValue(CORNER).isHen();
        Direction value = selfState.getValue(FACING);
        if(value.getAxis() == Direction.Axis.Y){
            BlockState blockState = level.getBlockState(pos.relative(direction.getOpposite()));
            if(hen){
                if(direction == Direction.NORTH||direction == Direction.SOUTH){
                    boolean isNORTH = direction == Direction.NORTH;
                    if(value == Direction.DOWN) isNORTH = !isNORTH;
                    selfState = selfState.setValue(LINE_RL,setLineState(selfState,neighbourState,blockState,isNORTH));
                }
            }else {
                if(direction == Direction.EAST||direction == Direction.WEST){
                    boolean isNORTH = direction == Direction.EAST;
                    selfState = selfState.setValue(LINE_RL,setLineState(selfState,neighbourState,blockState,isNORTH));
                }
            }
        } else if (value.getAxis() == Direction.Axis.X) {
            BlockState blockState = level.getBlockState(pos.relative(direction.getOpposite()));
            if(!hen){
                if(direction == Direction.NORTH||direction == Direction.SOUTH){
                    boolean isNORTH = (direction == Direction.NORTH);
                    if(value == Direction.EAST) isNORTH = !isNORTH;
                    selfState = selfState.setValue(LINE_RL,setLineState(selfState,neighbourState,blockState,isNORTH));
                }
            }else {
                if(direction == Direction.UP||direction == Direction.DOWN){
                    boolean isNORTH = direction != Direction.UP;
                    selfState = selfState.setValue(LINE_RL,setLineState(selfState,neighbourState,blockState,isNORTH));
                }
            }
        } else if (value.getAxis() == Direction.Axis.Z) {
            BlockState blockState = level.getBlockState(pos.relative(direction.getOpposite()));
            if(!hen){
                if(direction == Direction.EAST||direction == Direction.WEST){
                    boolean isNORTH = direction == Direction.EAST;
                    selfState = selfState.setValue(LINE_RL,setLineState(selfState,neighbourState,blockState,isNORTH));
                }
            }else {
                if(direction == Direction.UP||direction == Direction.DOWN){
                    boolean isNORTH = direction != Direction.UP;
                    if(value == Direction.SOUTH) isNORTH = !isNORTH;
                    selfState = selfState.setValue(LINE_RL,setLineState(selfState,neighbourState,blockState,isNORTH));
                }
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

    public static boolean isLightTube(BlockState blockState,BlockState blockState1) {
        return blockState1.getBlock() instanceof LightTube
                &&blockState.getValue(FACING)==blockState1.getValue(FACING)
                &&blockState.getValue(CORNER).isHen()==blockState1.getValue(CORNER).isHen();
    }
}
