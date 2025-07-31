package com.freefish.torchesbecomesunlight.server.block;

import com.freefish.torchesbecomesunlight.server.block.blockentity.StewPotBlockEntity;
import com.freefish.torchesbecomesunlight.server.block.furniture.furniturenum.StewPotEnum;
import com.freefish.torchesbecomesunlight.server.init.BlockEntityHandle;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class StewPotBlock extends BaseEntityBlock {
    public static final EnumProperty<StewPotEnum> STEW_POT_ENUM_PROPERTY = EnumProperty.create("stew_pot", StewPotEnum.class);
    public static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 18, 16);

    public StewPotBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState().setValue(STEW_POT_ENUM_PROPERTY, StewPotEnum.Firewood_1));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(STEW_POT_ENUM_PROPERTY);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof StewPotBlockEntity) {
                //((CrockPotBlockEntity) blockEntity).drops();
            }
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            StewPotEnum value = pState.getValue(STEW_POT_ENUM_PROPERTY);
            ItemStack mainHandItem = pPlayer.getMainHandItem();

            boolean flad = false;
            if (mainHandItem.getItem() instanceof BlockItem blockItem) {
                Block block = blockItem.getBlock();
                if(block.defaultBlockState().is(BlockTags.LOGS)){
                    flad = true;
                }
            }

            if(value!=StewPotEnum.Firewood_4&&flad){
                if(value==StewPotEnum.Firewood_1) {
                    pLevel.setBlock(pPos,pState.setValue(STEW_POT_ENUM_PROPERTY,StewPotEnum.Firewood_2),3);
                }else if(value==StewPotEnum.Firewood_2) {
                    pLevel.setBlock(pPos,pState.setValue(STEW_POT_ENUM_PROPERTY,StewPotEnum.Firewood_3),3);
                }else if(value==StewPotEnum.Firewood_3) {
                    pLevel.setBlock(pPos,pState.setValue(STEW_POT_ENUM_PROPERTY,StewPotEnum.Firewood_4),3);
                }else
                mainHandItem.shrink(1);
            }
            else {
                BlockEntity entity = pLevel.getBlockEntity(pPos);
                if (entity instanceof StewPotBlockEntity) {
                    NetworkHooks.openScreen(((ServerPlayer) pPlayer), (StewPotBlockEntity) entity, pPos);
                } else {
                    throw new IllegalStateException("Our Container provider is missing!");
                }
            }
        }

        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new StewPotBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if(pLevel.isClientSide()) {
            return createTickerHelper(pBlockEntityType, BlockEntityHandle.STEW_POT.get(),
                    StewPotBlockEntity::animationTick);
        }
        return createTickerHelper(pBlockEntityType, BlockEntityHandle.STEW_POT.get(),
                StewPotBlockEntity::cookingTick);
    }
}
