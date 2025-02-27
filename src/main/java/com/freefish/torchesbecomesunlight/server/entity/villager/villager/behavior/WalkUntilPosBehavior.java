package com.freefish.torchesbecomesunlight.server.entity.villager.villager.behavior;

import com.freefish.torchesbecomesunlight.server.entity.villager.UrsusVillager;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class WalkUntilPosBehavior extends Behavior<UrsusVillager> {
   private final MemoryModuleType<GlobalPos> globalPosMemory;

   BlockPos blockPos;

   public WalkUntilPosBehavior(MemoryModuleType<GlobalPos> globalPosMemory, int pDuration) {
      super(ImmutableMap.of(globalPosMemory, MemoryStatus.VALUE_PRESENT), pDuration);
      this.globalPosMemory = globalPosMemory;
   }

   @Override
   protected boolean checkExtraStartConditions(ServerLevel pLevel, UrsusVillager pOwner) {
      return true;
   }

   @Override
   protected boolean canStillUse(ServerLevel pLevel, UrsusVillager pEntity, long pGameTime) {
      if(blockPos==null)
         return false;
      else  {
         float dist = (float) pEntity.position().distanceTo(new Vec3(blockPos.getX(),blockPos.getY(),blockPos.getZ()));
         return dist>2;
      }
   }

   @Override
   protected void tick(ServerLevel pLevel, UrsusVillager pOwner, long pGameTime) {
      Optional<WalkTarget> memory = pOwner.getBrain().getMemory(MemoryModuleType.WALK_TARGET);
      memory.ifPresent(walkTarget -> {
         PositionTracker target = walkTarget.getTarget();
         float speedModifier = walkTarget.getSpeedModifier();
         pOwner.getNavigation().moveTo(target.currentPosition().x,target.currentPosition().y,target.currentPosition().z,speedModifier);
      });
   }

   @Override
   protected void start(ServerLevel pLevel, UrsusVillager pEntity, long pGameTime) {
      super.start(pLevel, pEntity, pGameTime);
      Optional<GlobalPos> memory = pEntity.getBrain().getMemory(globalPosMemory);
      memory.ifPresent(globalPos -> blockPos = globalPos.pos());
   }

   @Override
   protected void stop(ServerLevel pLevel, UrsusVillager pEntity, long pGameTime) {
      super.stop(pLevel, pEntity, pGameTime);
      pEntity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
   }
}