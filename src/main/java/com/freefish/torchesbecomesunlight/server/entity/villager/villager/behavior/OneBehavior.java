package com.freefish.torchesbecomesunlight.server.entity.villager.villager.behavior;

import com.freefish.torchesbecomesunlight.server.entity.villager.UrsusVillager;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;

import java.util.function.Function;
import java.util.function.Predicate;

public class OneBehavior {
   public static OneShot<UrsusVillager> create(float pSpeedModifier, int pCloseEnoughDist) {
      return create((living) -> {
         return true;
      }, (living) -> {
         return pSpeedModifier;
      }, pCloseEnoughDist);
   }

   public static OneShot<UrsusVillager> create(Predicate<UrsusVillager> pCanSetWalkTarget, Function<UrsusVillager, Float> pSpeedModifier, int pCloseEnoughDist) {
      return BehaviorBuilder.create((behavior) -> {
         return behavior.group(behavior.absent(MemoryModuleType.HOME)).apply(behavior, (globalPos) -> {
            return (level, villager, gameTime) -> {
               if (!pCanSetWalkTarget.test(villager)) {
                  return false;
               } else {
                  System.out.println(1);
                  globalPos.set(GlobalPos.of(level.dimension(),villager.blockPosition()));
                  return true;
               }
            };
         });
      });
   }
}