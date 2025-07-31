package com.freefish.torchesbecomesunlight.server.entity.dlc;

import com.freefish.torchesbecomesunlight.server.block.blockentity.BigBenBlockEntity;
import com.freefish.torchesbecomesunlight.server.entity.AnimatedEntity;
import com.freefish.torchesbecomesunlight.server.entity.dlc.ai.GunKnightRandomStrollGoal;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public abstract class GunKnightEntity extends AnimatedEntity implements IEntityAdditionalSpawnData {
    @Getter
    private BlockPos controlBen;

    private BigBenBlockEntity tile;
    @Getter
    private BlockPos spawnPos;

    public GunKnightEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(8, new GunKnightRandomStrollGoal<>(this , 0.33));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        if(controlBen!=null){
            pCompound.put("controlBen", NbtUtils.writeBlockPos(controlBen));
        }
        if(spawnPos!=null){
            pCompound.put("spawnPos", NbtUtils.writeBlockPos(spawnPos));
        }
    }
    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        CompoundTag tag = new CompoundTag();
        BlockPos spawnPos = getSpawnPos();
        BlockPos controlBen1 = getControlBen();
        if(spawnPos!=null&&controlBen1!=null){
            tag.put("spawn", NbtUtils.writeBlockPos(spawnPos));
            tag.put("controlBen1", NbtUtils.writeBlockPos(controlBen1));
        }
        buffer.writeNbt(tag);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        CompoundTag compoundTag = additionalData.readNbt();
        if(compoundTag.contains("spawn")){
            init(NbtUtils.readBlockPos(compoundTag.getCompound("controlBen1")),NbtUtils.readBlockPos(compoundTag.getCompound("spawn")));
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public BigBenBlockEntity getTile(){
        if(tile==null&&controlBen!=null){
            BlockEntity blockEntity = level().getBlockEntity(controlBen);
            if(blockEntity instanceof BigBenBlockEntity big){
                tile = big;
                return tile;
            }
            return null;
        }else {
            return tile;
        }
    }

    @Override
    public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
        return false;
    }

    public void init(BlockPos controlBen, BlockPos spawnPos){
        this.controlBen = controlBen;
        this.spawnPos = spawnPos;
    }

    @Override
    public boolean isAlliedTo(Entity pEntity) {
        if(pEntity instanceof GunKnightEntity gun){
            if(gun.getTile()==getTile()){
                return true;
            }
        }
        return super.isAlliedTo(pEntity);
    }

    @Override
    public void setTarget(@Nullable LivingEntity pTarget) {
        if(pTarget!=null&&isAlliedTo(pTarget)) return;

        super.setTarget(pTarget);
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        Entity entity = source.getEntity();
        if(entity!=null&&isAlliedTo(entity)) return false;

        return super.hurt(source, damage);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if(pCompound.contains("controlBen")){
            controlBen = NbtUtils.readBlockPos(pCompound.getCompound("controlBen"));
        }
        if(pCompound.contains("spawnPos")){
            spawnPos = NbtUtils.readBlockPos(pCompound.getCompound("spawnPos"));
        }
    }
}
