package com.freefish.torchesbecomesunlight.server.block.furniture;

import com.freefish.torchesbecomesunlight.server.block.furniture.furniturenum.EightDirection;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.Nullable;

public class EightDirectionBlock extends Block {
    public static final EnumProperty<EightDirection> EIGHT_DIRECTION = EnumProperty.create("eight_direction", EightDirection.class);

    public EightDirectionBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState().setValue(EIGHT_DIRECTION, EightDirection.NORTH));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(EIGHT_DIRECTION);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        float rotation = pContext.getRotation();
        if(rotation<0) rotation+=360;
        EightDirection eightDirection = EightDirection.NORTH;
        if(rotation>22.5&&rotation<=67.5){
            eightDirection = EightDirection.NORTH_EAST;
        } else if(rotation>67.5&&rotation<=112.5){
            eightDirection = EightDirection.EAST;
        } else if(rotation>112.5&&rotation<=157.5){
            eightDirection = EightDirection.EAST_SOUTH;
        } else if(rotation>157.5&&rotation<=202.5){
            eightDirection = EightDirection.SOUTH;
        } else if(rotation>202.5&&rotation<=247.5){
            eightDirection = EightDirection.SOUTH_WAST;
        } else if(rotation>247.5&&rotation<=292.5){
            eightDirection = EightDirection.WEST;
        } else if(rotation>247.5&&rotation<=337.5){
            eightDirection = EightDirection.WEST_NORTH;
        }

        return defaultBlockState().setValue(EIGHT_DIRECTION, eightDirection);
    }
}
