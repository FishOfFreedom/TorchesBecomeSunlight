package com.freefish.torchesbecomesunlight.server.entity.villager.villager.behavior;

import com.freefish.torchesbecomesunlight.server.entity.villager.UrsusVillager;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class SetWalkFromPosBehavior {
   public static OneShot<UrsusVillager> create(MemoryModuleType<GlobalPos> globalPosMemory,float speed) {
      return BehaviorBuilder.create((behavior) -> {
         return behavior.group(behavior.registered(MemoryModuleType.WALK_TARGET),behavior.present(globalPosMemory)).apply(behavior, (walkTarget,globalPos) -> {
            return (level, villager, gameTime) -> {
               Optional<GlobalPos> memory = villager.getBrain().getMemory(globalPosMemory);
               if(memory.isPresent()){
                  System.out.println(1);
                  GlobalPos pos = memory.get();
                  walkTarget.set(new WalkTarget(pos.pos(),speed,100));
                  return true;
               }
               else
                  return false;
            };
         });
      });
   }
}