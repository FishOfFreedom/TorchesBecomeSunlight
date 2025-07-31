package com.freefish.torchesbecomesunlight.server.entity.effect;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

public abstract class EntityMultiBlock extends Entity implements IEntityAdditionalSpawnData {
    private BlockState[] blockStates = new BlockState[]{};
    private BlockPos[] blockPoss = new BlockPos[]{};
    @Getter
    private int offset = 1;
    @Getter
    @Setter
    private int duration = 100;

    private static final EntityDataAccessor<Integer> TICKS_EXISTED = SynchedEntityData.defineId(EntityMultiBlock.class, EntityDataSerializers.INT);

    public EntityMultiBlock(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        if (getDeltaMovement().x() > 0 || getDeltaMovement().z() > 0)
            setYRot((float) ((180f/Math.PI) * Math.atan2(getDeltaMovement().x(), getDeltaMovement().z())));
        setXRot(getXRot() + random.nextFloat() * 360);
        setDuration(300);
    }

    public void setMultiBlock(BlockState[] blockStates,BlockPos[] blockPoss,int offset){
        if(blockStates.length!=blockPoss.length){
        }else {
            this.blockStates = blockStates;
            this.blockPoss = blockPoss;
            this.offset = offset;
        }
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
    }

    @Override
    public void tick() {
        super.tick();
        if(!level().isClientSide){
            if (tickCount > getDuration()) discard();
        }
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    protected void defineSynchedData() {
        getEntityData().define(TICKS_EXISTED, 0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        ListTag list = compound.getList("blocks", 10);
        blockStates = new BlockState[list.size()];
        blockPoss = new BlockPos[list.size()];

        for(int i = 0 ;i<list.size();i++){
            CompoundTag blocksTemp = (CompoundTag) list.get(i);
            blockStates[i] = NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK),blocksTemp.getCompound("block"));
            blockPoss[i] = new BlockPos(blocksTemp.getInt("blockx"),blocksTemp.getInt("blocky"),blocksTemp.getInt("blockz"));
        }

        tickCount = compound.getInt("ticksExisted");
        duration = compound.getInt("duration");
        offset = compound.getInt("offset");
        refreshDimensions();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        ListTag blocks = new ListTag();
        for(int i = 0;i<blockStates.length;i++){
            CompoundTag blocksTemp = new CompoundTag();
            blocksTemp.put("block",NbtUtils.writeBlockState(blockStates[i]));
            blocksTemp.putInt("blockx",blockPoss[i].getX());
            blocksTemp.putInt("blocky",blockPoss[i].getY());
            blocksTemp.putInt("blockz",blockPoss[i].getZ());

            blocks.add(blocksTemp);
        }
        compound.put("blocks",blocks);

        compound.putInt("ticksExisted", tickCount);
        compound.putInt("duration", duration);
        compound.putInt("offset", offset);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf friendlyByteBuf) {
        CompoundTag blocksTag = new CompoundTag();
        ListTag blocks = new ListTag();
        for(int i = 0;i<blockStates.length;i++){
            CompoundTag blocksTemp = new CompoundTag();
            blocksTemp.put("block",NbtUtils.writeBlockState(blockStates[i]));
            blocksTemp.putInt("blockx",blockPoss[i].getX());
            blocksTemp.putInt("blocky",blockPoss[i].getY());
            blocksTemp.putInt("blockz",blockPoss[i].getZ());
            blocks.add(blocksTemp);
        }
        blocksTag.put("blockstag",blocks);
        friendlyByteBuf.writeNbt(blocksTag);

        friendlyByteBuf.writeFloat(getYRot());
        friendlyByteBuf.writeFloat(getXRot());
        friendlyByteBuf.writeVarInt(offset);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf friendlyByteBuf) {
        CompoundTag blocksTag = friendlyByteBuf.readNbt();
        ListTag list = blocksTag.getList("blockstag", 10);

        blockStates = new BlockState[list.size()];
        blockPoss = new BlockPos[list.size()];

        for(int i = 0;i<list.size();i++){
            CompoundTag blocksTemp = (CompoundTag) list.get(i);
            blockStates[i] = NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK),blocksTemp.getCompound("block"));
            blockPoss[i] = new BlockPos(blocksTemp.getInt("blockx"),blocksTemp.getInt("blocky"),blocksTemp.getInt("blockz"));
        }
        setYRot(friendlyByteBuf.readFloat());
        setXRot(friendlyByteBuf.readFloat());
        offset = friendlyByteBuf.readVarInt();
        refreshDimensions();
    }

    public BlockState[] getBlockStates() {
        return blockStates;
    }

    public BlockPos[] getBlockPoss() {
        return blockPoss;
    }

    public int getTicksExisted() {
        return getEntityData().get(TICKS_EXISTED);
    }

    public void setTicksExisted(int ticksExisted) {
        getEntityData().set(TICKS_EXISTED, ticksExisted);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        return EntityDimensions.scalable(offset,offset);
    }
}