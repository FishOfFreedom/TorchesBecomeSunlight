package com.freefish.torchesbecomesunlight.server.entity.effect;

import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class IceWallEntity extends Entity {
    private float iceWallYaw;
    private BlockPos blockPos2;
    private int[] block = new int[]{2,-2};

    public IceWallEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public IceWallEntity(Level pLevel,float yaw,BlockPos blockPos) {
        super(EntityHandle.ICE_WALL_ENTITY.get(), pLevel);
        iceWallYaw = yaw;
        this.blockPos2 = blockPos;
    }

    @Override
    public void tick() {
        super.tick();
        if(!level().isClientSide) {
            if (tickCount >= 80) {
                destroyIceWall();
                kill();
            }
            if(tickCount==1){
                spawnIceWall();
            }
        }
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {

    }

    private void spawnIceWall(){
        BlockPos blockPos;
        if(blockPos2==null)
            blockPos = getOnPos();
        else
            blockPos = blockPos2;
        Vec3 center = new Vec3(blockPos.getX() + 0.5,blockPos.getY() + 1.5,blockPos.getZ() + 0.5);

        for(int i = 0;i<5;i++){
            for(int j = 0;j<2;j++){
                for(int n = 0;n<4;n++){
                    Vec3 wall = new Vec3(i-2,n-1,2+j).yRot((float) (-iceWallYaw / 180 * Math.PI)).add(center);
                    BlockPos blockPos1 = new BlockPos((int)wall.x,(int)wall.y,(int)wall.z);
                    BlockState wallState = level().getBlockState(blockPos1);
                    if(wallState.isAir()||wallState.is(Blocks.SNOW)){
                        if(n==3||i==0||i==4){
                            if(random.nextFloat()<0.5f) continue;
                        }
                        level().setBlock(blockPos1, Blocks.ICE.defaultBlockState(), 3);
                    }
                }
            }
        }
        for(int i :block){
            for(int i1 = 0;i1<2;i1++) {
                Vec3 wall = new Vec3(i, 0, i1).yRot((float) (-iceWallYaw / 180 * Math.PI)).add(center);
                BlockPos blockPos1 = new BlockPos((int) wall.x, (int) wall.y, (int) wall.z);
                BlockState wallState = level().getBlockState(blockPos1);
                if (wallState.isAir() || wallState.is(Blocks.SNOW))
                    level().setBlock(blockPos1, Blocks.ICE.defaultBlockState(), 3);
            }
        }
    }

    private void destroyIceWall(){
        BlockPos blockPos;
        if(blockPos2==null)
            blockPos = getOnPos();
        else
            blockPos = blockPos2;
        Vec3 center = new Vec3(blockPos.getX() + 0.5,blockPos.getY() + 1.5,blockPos.getZ() + 0.5);

        for(int i = 0;i<5;i++){
            for(int j = 0;j<2;j++){
                for(int n = 0;n<4;n++){
                    Vec3 wall = new Vec3(i-2,n-1,2+j).yRot((float) (-iceWallYaw / 180 * Math.PI)).add(center);
                    BlockPos blockPos1 = new BlockPos((int)wall.x,(int)wall.y,(int)wall.z);
                    BlockState wallState = level().getBlockState(blockPos1);
                    if(wallState.is(Blocks.ICE)){
                        level().destroyBlock(blockPos1,false);
                    }
                }
            }
        }
        for(int i1 = 0;i1<2;i1++) {
            for (int i : block) {
                Vec3 wall = new Vec3(i, 0, i1).yRot((float) (-iceWallYaw / 180 * Math.PI)).add(center);
                BlockPos blockPos1 = new BlockPos((int) wall.x, (int) wall.y, (int) wall.z);
                BlockState wallState = level().getBlockState(blockPos1);
                if (wallState.is(Blocks.ICE)) {
                    level().destroyBlock(blockPos1, false);
                }
            }
        }
    }

}
