package com.freefish.torchesbecomesunlight.server.entity.effect;

import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class AnimationBlock extends EntityMultiBlock {
    public AnimationBlock(EntityType<AnimationBlock> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        noPhysics = true;
    }

    public AnimationBlock(Level worldIn, Vec3 pos,float yRot,float xRot) {
        this(EntityHandle.ANIMATION_BLOCK.get(), worldIn);
        setXRot(xRot);
        setYRot(yRot);
        setPos(pos);
        setDuration(90+random.nextInt(6));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
    }

    @Override
    public void tick() {
        move(MoverType.SELF,getDeltaMovement());

        super.tick();

        if(tickCount<=10){
            setDeltaMovement(new Vec3(0, MathUtils.easeInQuint((10-tickCount)/10f),0));
        }else if(tickCount > getDuration()-10&&tickCount <= getDuration()) {
            setDeltaMovement(new Vec3(0, MathUtils.easeInQuint(-(10-(tickCount-(getDuration()-10)))/10f),0));
        }
    }

    public static void spawnRing(Vec3 center,int radio,Level level){
        for(int i=1;i<=radio;i++){
            int len = (int)(i*3.14f*2f);
            for(int i1=1;i1<=len;i1++){
                float v = (float) i1 / len * 6.28f +(level.random.nextFloat()*0.7f-0.35f);
                Vec3 offset = new Vec3(0,0,i).yRot(v);
                Vec3 add = center.add(offset);
                AnimationBlock animationBlock = new AnimationBlock(level, add,-v/3.14f*180f+(level.random.nextInt(60)-30), Mth.lerpInt(i/(float)radio,-10,-30));

                BlockPos blockPos = new BlockPos((int) add.x, (int)( add.y-0.5f), (int) add.z);
                BlockState blockState = level.getBlockState(blockPos);
                if(blockState.isAir()){
                    animationBlock.setMultiBlock(new BlockState[]{Blocks.DIRT.defaultBlockState()},new BlockPos[]{new BlockPos(0,0,0)},1);
                }else {
                    animationBlock.setMultiBlock(new BlockState[]{blockState},new BlockPos[]{new BlockPos(0,0,0)},1);
                }
                level.addFreshEntity(animationBlock);
            }
        }
    }
}
