package com.freefish.torchesbecomesunlight.server.entity.effect;

import com.freefish.torchesbecomesunlight.server.init.BlockHandle;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ChairEntity extends Entity {
    private BlockPos chairPos;

    public ChairEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    public ChairEntity(Level level, BlockPos chairPos) {
        super(EntityHandle.CHAIR_ENTITY.get(), level);
        this.chairPos = chairPos;
        this.setPos(chairPos.getX() + 0.5, chairPos.getY()+0.15, chairPos.getZ() + 0.5);
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    public void tick() {
        super.tick();
        if(!level().isClientSide&&tickCount>1){
            if(chairPos!=null){
                if (!level().getBlockState(chairPos).is(BlockHandle.ROUND_STOOL.get())) {
                    this.discard();
                    if (!this.getPassengers().isEmpty()) {
                        this.ejectPassengers();
                    }
                }else if(this.getPassengers().isEmpty()){
                    this.discard();
                }
            }else {
                this.discard();
            }
        }
    }

    @Override
    public void move(MoverType pType, Vec3 pPos) {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {

    }
}