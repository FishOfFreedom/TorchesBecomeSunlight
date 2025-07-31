package com.freefish.torchesbecomesunlight.server.entity.rhodesIsland;

import com.freefish.torchesbecomesunlight.server.entity.AnimatedEntity;
import com.freefish.torchesbecomesunlight.server.entity.rhodesIsland.ai.RhodesIslandRandomStrollGoal;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationAct;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class RhodesIslandEntity extends AnimatedEntity {
    @Getter
    private BlockPos spawnPos;

    public RhodesIslandEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public AnimationAct getDeathAnimation() {
        return null;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(8, new RhodesIslandRandomStrollGoal<>(this , 0.31));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        if(spawnPos!=null){
            pCompound.put("spawnPos", NbtUtils.writeBlockPos(spawnPos));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if(pCompound.contains("spawnPos")){
            spawnPos = NbtUtils.readBlockPos(pCompound.getCompound("spawnPos"));
        }
    }

    @Override
    protected float getJumpPower() {
        BlockPos below = blockPosition().below();
        BlockState blockState = level().getBlockState(below);
        if(blockState.is(Blocks.GRAY_CONCRETE)||blockState.is(Blocks.SMOOTH_QUARTZ_SLAB)){
            return 0;
        }
        return super.getJumpPower();
    }

    public void init(BlockPos spawnPos){
        this.spawnPos = spawnPos;
    }

    @Override
    public boolean isAlliedTo(Entity pEntity) {
        if(pEntity instanceof RhodesIslandEntity gun){
            return true;
        }
        return super.isAlliedTo(pEntity);
    }

    @Override
    public void setTarget(@Nullable LivingEntity pTarget) {
        if(pTarget!=null&&isAlliedTo(pTarget)) return;

        super.setTarget(pTarget);
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        Entity entity = source.getEntity();
        if(entity!=null&&isAlliedTo(entity)) return false;

        return super.hurt(source, damage);
    }

}
