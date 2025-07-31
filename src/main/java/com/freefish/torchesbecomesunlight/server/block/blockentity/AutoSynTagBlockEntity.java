package com.freefish.torchesbecomesunlight.server.block.blockentity;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.block.blockentity.sync.SynchedBlockEntityData;
import com.freefish.torchesbecomesunlight.server.event.packet.toclient.SetBlockEntityDataMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class AutoSynTagBlockEntity extends BlockEntity{
    public SynchedBlockEntityData blockEntityData;

    public AutoSynTagBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        this.blockEntityData = new SynchedBlockEntityData(this);
        defineSynchedData();
    }

    public <T> void onSyncedDataUpdated(EntityDataAccessor<T> pKey) {
    }

    public <T> void onSyncedDataUpdated(List<SynchedBlockEntityData.DataValue<?>> pEntries) {
    }

    protected void defineSynchedData(){

    }

    public static void onServerTick(ServerLevel serverLevel, BlockPos pos,AutoSynTagBlockEntity cookingPot){
        SynchedBlockEntityData blockEntityData = cookingPot.blockEntityData;
        if(blockEntityData.isDirty()){
            List<SynchedBlockEntityData.DataValue<?>> list = blockEntityData.packDirty();
            if (list != null) {
                TorchesBecomeSunlight.sendToLevel(new SetBlockEntityDataMessage(list,pos),serverLevel);
            }
        }
    }
}
