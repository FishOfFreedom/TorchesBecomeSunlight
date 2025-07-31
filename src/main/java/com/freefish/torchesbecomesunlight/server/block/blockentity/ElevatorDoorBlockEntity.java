package com.freefish.torchesbecomesunlight.server.block.blockentity;

import com.freefish.torchesbecomesunlight.server.block.ElevatorDoorBlock;
import com.freefish.torchesbecomesunlight.server.block.furniture.furniturenum.Door;
import com.freefish.torchesbecomesunlight.server.init.BlockEntityHandle;
import com.freefish.torchesbecomesunlight.server.init.BlockHandle;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ElevatorDoorBlockEntity extends AutoSynTagBlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public int tickcount;

    public ElevatorDoorBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityHandle.ELEVATOR_DOOR.get(), pPos, pBlockState);
    }

    public void inter(Level level, BlockPos pos, BlockState state,boolean open){
        Door value = state.getValue(ElevatorDoorBlock.DOOR_ENUM_PROPERTY);
        if(open){
            switch (value) {
                case CLOSE_L ->
                        level.setBlock(pos, state.setValue(ElevatorDoorBlock.DOOR_ENUM_PROPERTY, Door.OPEN_L), 3);
                case CLOSE_R ->
                        level.setBlock(pos, state.setValue(ElevatorDoorBlock.DOOR_ENUM_PROPERTY, Door.OPEN_R), 3);
            }
        }
        else  {
            switch (value) {
                case OPEN_L ->
                        level.setBlock(pos, state.setValue(ElevatorDoorBlock.DOOR_ENUM_PROPERTY, Door.CLOSE_L), 3);
                case OPEN_R ->
                        level.setBlock(pos, state.setValue(ElevatorDoorBlock.DOOR_ENUM_PROPERTY, Door.CLOSE_R), 3);
            }
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ElevatorDoorBlockEntity cookingPot) {
        cookingPot.tickcount++;

        if(cookingPot.tickcount%20==0&&!level.isClientSide){
            Door value = state.getValue(ElevatorDoorBlock.DOOR_ENUM_PROPERTY);
            if(value==Door.OPEN_R||value==Door.CLOSE_R){
                BlockState blockState = level.getBlockState(pos.offset(-3, 0, -4));
                if (blockState.is(BlockHandle.ELEVATOR.get())) {
                    cookingPot.inter(level, pos, state, true);
                } else {
                    cookingPot.inter(level, pos, state, false);
                }
            }else {
                BlockState blockState = level.getBlockState(pos.offset(0, 0, -4));
                if (blockState.is(BlockHandle.ELEVATOR.get())) {
                    cookingPot.inter(level, pos, state, true);
                } else {
                    cookingPot.inter(level, pos, state, false);
                }
            }
        }

        //if(!level.isClientSide&&cookingPot.tickcount%40==0){
        //    Door value = state.getValue(ElevatorDoorBlock.DOOR_ENUM_PROPERTY);
        //    switch (value){
        //        case OPEN_L ->
        //                level.setBlock(pos,state.setValue(ElevatorDoorBlock.DOOR_ENUM_PROPERTY,Door.CLOSE_L),3);
        //        case CLOSE_L ->
        //                level.setBlock(pos,state.setValue(ElevatorDoorBlock.DOOR_ENUM_PROPERTY,Door.OPEN_L),3);
        //        case OPEN_R ->
        //                level.setBlock(pos,state.setValue(ElevatorDoorBlock.DOOR_ENUM_PROPERTY,Door.CLOSE_R),3);
        //        case CLOSE_R ->
        //                level.setBlock(pos,state.setValue(ElevatorDoorBlock.DOOR_ENUM_PROPERTY,Door.OPEN_R),3);
        //    }
        //}

        if(level instanceof ServerLevel serverLevel){
            onServerTick(serverLevel,pos,cookingPot);
        }
    }
    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private void markUpdated() {
        this.setChanged();
        this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putInt("update",0);
        return compoundTag;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
    }

    @Override
    protected void defineSynchedData() {
    }

    private final AnimationController<ElevatorDoorBlockEntity> animationController = new AnimationController<ElevatorDoorBlockEntity>(this, "Controller", 5, this::predicate);

    private PlayState predicate(AnimationState<ElevatorDoorBlockEntity> elevatorBlockEntityAnimationState) {
        BlockState blockState = getBlockState();
        Door value = blockState.getValue(ElevatorDoorBlock.DOOR_ENUM_PROPERTY);

        switch (value){
            case OPEN_L ->
                    elevatorBlockEntityAnimationState.setAnimation(RawAnimation.begin().thenPlayAndHold("open_l"));
            case CLOSE_L ->
                    elevatorBlockEntityAnimationState.setAnimation(RawAnimation.begin().thenPlayAndHold("close_l"));
            case OPEN_R ->
                    elevatorBlockEntityAnimationState.setAnimation(RawAnimation.begin().thenPlayAndHold("open_r"));
            case CLOSE_R ->
                    elevatorBlockEntityAnimationState.setAnimation(RawAnimation.begin().thenPlayAndHold("close_r"));
        }

        return PlayState.CONTINUE;
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(animationController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
