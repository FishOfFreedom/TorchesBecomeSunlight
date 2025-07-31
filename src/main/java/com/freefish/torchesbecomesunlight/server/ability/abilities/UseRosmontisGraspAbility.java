package com.freefish.torchesbecomesunlight.server.ability.abilities;

import com.freefish.torchesbecomesunlight.server.ability.AbilitySection;
import com.freefish.torchesbecomesunlight.server.ability.AbilityType;
import com.freefish.torchesbecomesunlight.server.ability.PlayerAbility;
import com.freefish.torchesbecomesunlight.server.entity.rhodesIsland.rosmontis.RosmontisBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class UseRosmontisGraspAbility extends PlayerAbility {

    public UseRosmontisGraspAbility(AbilityType<Player, UseRosmontisGraspAbility> abilityType, Player user) {
        super(abilityType, user, new AbilitySection[] {
                new AbilitySection.AbilitySectionDuration(AbilitySection.AbilitySectionType.STARTUP, 20),
                new AbilitySection.AbilitySectionInstant(AbilitySection.AbilitySectionType.ACTIVE),
                new AbilitySection.AbilitySectionDuration(AbilitySection.AbilitySectionType.RECOVERY, 20)
        });
    }

    List<ItemEntity> itemEntities;

    @Override
    public void start() {
        super.start();
        Player player = getUser();
        Level level = getLevel();
        ItemStack mainHandItem = player.getMainHandItem();
        heldItemMainHandVisualOverride = mainHandItem;

        if (!player.level().isClientSide()) {
        }
        else {
        }

        int tbsisActive = mainHandItem.getOrCreateTag().getInt("tbsisActive");

        BlockHitResult hitResult = (BlockHitResult) player.pick(20.0D, 0.0F, false);
        BlockPos targetPos = hitResult.getBlockPos();
        if(tbsisActive<2){
            AABB attractionArea = new AABB(targetPos)
                    .inflate(3)
                    .move(0, 0.5, 0);

            List<ItemEntity> items = level.getEntitiesOfClass(
                    ItemEntity.class,
                    attractionArea
            );
            itemEntities = items;
        }else {
            spawnRosStone(hitResult.getLocation(),tbsisActive);
        }
    }

    @Override
    public void tickUsing() {
        super.tickUsing();
        Player player = getUser();
        Level level = getLevel();

        if (itemEntities != null) {
            for (ItemEntity item : itemEntities) {
                attractItemToPlayer(item,player);
            }
        }
    }

    private void attractItemToPlayer(ItemEntity item, Player player) {
        Vec3 playerPos = player.position();
        Vec3 itemPos = item.position();
        Vec3 direction = playerPos.subtract(itemPos);

        if (direction.lengthSqr() > 0) {
            Vec3 motion = direction.normalize().scale(0.8f);

            Vec3 currentMotion = item.getDeltaMovement();
            item.setDeltaMovement(
                    motion.x,
                    motion.y/2,
                    motion.z
            );

            item.setPickUpDelay(0);

            if(getLevel().isClientSide){
                if(getLevel().random.nextBoolean()){
                    player.level().addParticle(
                            ParticleTypes.END_ROD,
                            itemPos.x,
                            itemPos.y + 0.5,
                            itemPos.z,
                            (player.getRandom().nextDouble() - 0.5) * 0.1,
                            0.1,
                            (player.getRandom().nextDouble() - 0.5) * 0.1
                    );
                }
            }
        }
    }

    @Override
    public void end() {
        super.end();
    }

    public void spawnRosStone(Vec3 pos,int scale){
        Level level = getLevel();
        if(level.isClientSide) return;

        pos = pos.add(0,-scale-0.1,0);

        RosmontisBlock block = new RosmontisBlock(getUser(),level);
        if(scale==1){
            BlockPos blockPos = new BlockPos((int) pos.x, (int) pos.y, (int) pos.z);
            BlockPos[] blockPoss = new BlockPos[]{new BlockPos(0,0,0)};
            BlockState blockState = level.getBlockState(blockPos);
            if(!blockState.isAir()) {
                BlockState[] blockStates = new BlockState[]{blockState};
                block.setMultiBlock(blockStates,blockPoss,1);
            }else {
                BlockState[] blockStates = new BlockState[]{Blocks.DIRT.defaultBlockState()};
                block.setMultiBlock(blockStates,blockPoss,1);
            }
        }
        else if(scale==2){
            BlockPos[] blockPoss = new BlockPos[8];
            BlockState[] blockStates = new BlockState[8];
            for(int i = 0;i<2;i++){
                for(int i1 = 0;i1<2;i1++){
                    for(int i2 = 0;i2<2;i2++){
                        blockPoss[i*4+i1*2+i2] = new BlockPos((int) pos.x+i, (int) pos.y+i1, (int) pos.z+i2);
                    }
                }
            }

            BlockPos[] blockPoss1 = new BlockPos[8];
            for(int i = 0;i<2;i++){
                for(int i1 = 0;i1<2;i1++){
                    for(int i2 = 0;i2<2;i2++){
                        blockPoss1[i*4+i1*2+i2] = new BlockPos(i, i1, i2);
                    }
                }
            }

            for(int i =0;i<blockPoss.length;i++){
                BlockState blockState = level.getBlockState(blockPoss[i]);
                if(!blockState.isAir()){
                    blockStates[i] = blockState;
                }else {
                    blockStates[i] = Blocks.DIRT.defaultBlockState();
                }
            }
            block.setMultiBlock(blockStates,blockPoss1,2);
        }
        else if(scale==3){
            BlockPos[] blockPoss = new BlockPos[27];
            BlockState[] blockStates = new BlockState[27];
            for(int i = 0;i<3;i++){
                for(int i1 = 0;i1<3;i1++){
                    for(int i2 = 0;i2<3;i2++){
                        blockPoss[i*9+i1*3+i2] = new BlockPos((int) pos.x+i, (int) pos.y+i1, (int) pos.z+i2);
                    }
                }
            }

            BlockPos[] blockPoss1 = new BlockPos[27];
            for(int i = 0;i<3;i++){
                for(int i1 = 0;i1<3;i1++){
                    for(int i2 = 0;i2<3;i2++){
                        blockPoss1[i*9+i1*3+i2] = new BlockPos(i, i1, i2);
                    }
                }
            }

            for(int i =0;i<blockPoss.length;i++){
                BlockState blockState = level.getBlockState(blockPoss[i]);
                if(!blockState.isAir()){
                    blockStates[i] = blockState;
                }else {
                    blockStates[i] = Blocks.DIRT.defaultBlockState();
                }
            }
            block.setMultiBlock(blockStates,blockPoss1,3);
        }

        block.setPos(pos);
        level.addFreshEntity(block);
    }

    @Override
    public boolean preventsAttacking() {
        return false;
    }

    @Override
    public boolean isAnimating() {
        return false;
    }
}
