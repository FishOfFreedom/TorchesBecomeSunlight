package com.freefish.torchesbecomesunlight.server.entity.villager.villager.behavior;

import com.freefish.torchesbecomesunlight.server.entity.villager.UrsusVillager;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class WalkToHomeBehavior extends Behavior<UrsusVillager> {
   public WalkToHomeBehavior(int pDuration) {
      super(ImmutableMap.of(MemoryModuleType.HOME, MemoryStatus.REGISTERED), pDuration);
   }

   @Override
   protected boolean checkExtraStartConditions(ServerLevel pLevel, UrsusVillager pOwner) {
      return true;
   }

   @Override
   protected boolean canStillUse(ServerLevel pLevel, UrsusVillager pEntity, long pGameTime) {
      return true;
   }

   @Override
   protected void tick(ServerLevel pLevel, UrsusVillager pOwner, long pGameTime) {
   }
}