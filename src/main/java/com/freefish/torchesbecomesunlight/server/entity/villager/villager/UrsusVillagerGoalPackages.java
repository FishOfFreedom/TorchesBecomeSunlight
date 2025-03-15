package com.freefish.torchesbecomesunlight.server.entity.villager.villager;

import com.freefish.torchesbecomesunlight.server.entity.villager.UrsusVillager;
import com.freefish.torchesbecomesunlight.server.entity.villager.villager.behavior.FindNearestAttackableTargetBehavior;
import com.freefish.torchesbecomesunlight.server.entity.villager.villager.behavior.SetWalkTargetMemory;
import com.freefish.torchesbecomesunlight.server.entity.villager.villager.behavior.VillagerAttackAI;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.monster.Zombie;

import java.util.Optional;

public class UrsusVillagerGoalPackages {
    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super UrsusVillager>>> getCorePackage(Mob pOwner) {
        return ImmutableList.of(
                Pair.of(0, new Swim(0.8F)),
                Pair.of(0, InteractWithDoor.create()),
                Pair.of(0, new LookAtTargetSink(45, 90)),
                Pair.of(1, new MoveToTargetSink(800,1000)),
                Pair.of(10, AcquirePoi.create((p_217497_) -> {
                    return p_217497_.is(PoiTypes.MEETING);}, MemoryModuleType.MEETING_POINT, true, Optional.of((byte)14)))
        );
    }

   public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super UrsusVillager>>> getIdlePackage(Mob pOwner,float pSpeedModifier) {
      return ImmutableList.of(
              //Pair.of(1, new FindNearestAttackableTargetBehavior<>(Zombie.class,pOwner)),
              Pair.of(2, new RunOne<>(ImmutableList.of(
                      Pair.of(VillageBoundRandomStroll.create(pSpeedModifier), 1),
                      Pair.of(SetWalkTargetFromLookTarget.create(pSpeedModifier, 2), 1),
                      Pair.of(new DoNothing(30, 60), 1)))),
              Pair.of(99, UpdateActivityFromSchedule.create())
      );
   }

   public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super UrsusVillager>>> getRestPackage(float pSpeedModifier) {
      return ImmutableList.of(
              Pair.of(1, SetWalkTargetMemory.create(MemoryModuleType.HOME, pSpeedModifier, 1, 150)),
              Pair.of(2, new RunOne<>(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT), ImmutableList.of(
                      Pair.of(InsideBrownianWalk.create(pSpeedModifier), 4),
                      Pair.of(new DoNothing(20, 40), 2)))),
              Pair.of(99, UpdateActivityFromSchedule.create())
      );
   }

    public static ImmutableList<? extends BehaviorControl<? super UrsusVillager>> getFightPackage(Mob pOwner,float pSpeedModifier) {
        return ImmutableList.of(
                new FindNearestAttackableTargetBehavior<>(Zombie.class,pOwner),
                new RunOne<>(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET,MemoryStatus.REGISTERED),ImmutableList.of(
                        Pair.of(new VillagerAttackAI(),2),
                        Pair.of(new VillagerAttackAI(),1)
                ))
        );
    }
}