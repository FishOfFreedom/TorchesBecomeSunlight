package com.freefish.torchesbecomesunlight.server.entity.villager.villager.behavior;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;

public class UpdateActivityWhileNoAct {
   public static BehaviorControl<LivingEntity> create() {
      return BehaviorBuilder.create((p_259429_) -> {
         return p_259429_.point((level, living, time) -> {
            living.getBrain().updateActivityFromSchedule(level.getDayTime(), level.getGameTime());
            return true;
         });
      });
   }
}