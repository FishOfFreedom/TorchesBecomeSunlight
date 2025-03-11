package com.freefish.torchesbecomesunlight.server.entity.villager.villager.behavior;

import com.freefish.torchesbecomesunlight.server.entity.villager.UrsusVillager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

public class SetWalkTargetMemory {
   public static OneShot<UrsusVillager> create(MemoryModuleType<GlobalPos> pBlockTargetMemory, float pSpeedModifier, int pCloseEnoughDist, int pTooFarDistance) {
      return BehaviorBuilder.create((p_258717_) -> {
         return p_258717_.group(p_258717_.absent(MemoryModuleType.WALK_TARGET), p_258717_.present(pBlockTargetMemory)).apply(p_258717_, ( walkTargetMemoryAccessor, globalPosMemoryAccessor) -> {
            return (p_275056_, p_275057_, p_275058_) -> {
               GlobalPos globalpos = p_258717_.get(globalPosMemoryAccessor);
               if (globalpos.dimension() == p_275056_.dimension()) {
                  if (globalpos.pos().distManhattan(p_275057_.blockPosition()) > pTooFarDistance) {
                     Vec3 vec3 = null;
                     int i = 0;
                     int j = 1000;

                     while(vec3 == null || BlockPos.containing(vec3).distManhattan(p_275057_.blockPosition()) > pTooFarDistance) {
                        vec3 = DefaultRandomPos.getPosTowards(p_275057_, 15, 7, Vec3.atBottomCenterOf(globalpos.pos()), (double)((float)Math.PI / 2F));
                        ++i;
                        if (i == 1000) {
                           globalPosMemoryAccessor.erase();
                           return true;
                        }
                     }

                     walkTargetMemoryAccessor.set(new WalkTarget(vec3, pSpeedModifier, pCloseEnoughDist));
                  } else if (globalpos.pos().distManhattan(p_275057_.blockPosition()) > pCloseEnoughDist) {
                     walkTargetMemoryAccessor.set(new WalkTarget(globalpos.pos(), pSpeedModifier, pCloseEnoughDist));
                  }
               } else {
                  globalPosMemoryAccessor.erase();
               }
               return true;
            };
         });
      });
   }
}