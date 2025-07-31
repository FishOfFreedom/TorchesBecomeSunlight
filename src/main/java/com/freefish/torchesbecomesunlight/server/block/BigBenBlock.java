package com.freefish.torchesbecomesunlight.server.block;

import com.freefish.torchesbecomesunlight.server.block.blockentity.BigBenBlockEntity;
import com.freefish.torchesbecomesunlight.server.init.BlockEntityHandle;
import com.freefish.torchesbecomesunlight.server.init.ItemHandle;
import com.freefish.torchesbecomesunlight.server.init.SoundHandle;
import com.freefish.torchesbecomesunlight.server.init.generator.advancement.criterion.TriggerHandler;
import com.freefish.torchesbecomesunlight.server.util.FFEntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class BigBenBlock extends BaseEntityBlock {
    public static final VoxelShape SHAPE = Block.box(-16, 0, -16, 16, 32, 16);

    public BigBenBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return super.getShape(pState, pLevel, pPos, pContext);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return super.getCollisionShape(pState, pLevel, pPos, pContext);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new BigBenBlockEntity(pPos,pState);
    }

    @Override
    public void attack(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
        super.attack(pState, pLevel, pPos, pPlayer);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if(!pLevel.isClientSide()){
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if(blockEntity instanceof BigBenBlockEntity entity){
                Inventory inventory = pPlayer.getInventory();
                boolean flad1 = false;
                boolean flad2 = false;
                for(ItemStack itemStack:inventory.items){
                    if(itemStack.is(ItemHandle.LIGHT.get())){
                        flad1 = true;
                    }
                    if(itemStack.is(ItemHandle.TIME.get())){
                        flad2 = true;
                    }
                }

                if(flad1&&flad2){
                    if(pPlayer instanceof ServerPlayer player){
                        TriggerHandler.STRING_ADVANCEMENT_TRIGGER.trigger(player, "gunPatriot_1_time");
                    }
                    float posToPosRot = FFEntityUtils.getPosToPosRot(pPlayer.position(), pPos.getCenter());
                    entity.hitBen(posToPosRot, 640);
                    Vec3 center = pPos.getCenter();
                    pLevel.playSound((Player) null, center.x, center.y, center.z, SoundHandle.BEN.get(), SoundSource.BLOCKS, 5, 1f);
                }
            }
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, BlockEntityHandle.BIG_BEN.get(),
                BigBenBlockEntity::tick);
    }
}
