package com.freefish.torchesbecomesunlight.server.entity.villager.villager;

import com.freefish.torchesbecomesunlight.server.entity.villager.UrsusVillager;
import com.freefish.torchesbecomesunlight.server.entity.villager.villager.behavior.SetWalkTargetMemory;
import com.freefish.torchesbecomesunlight.server.entity.villager.villager.behavior.WalkToHomeBehavior;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;

import java.util.Optional;

public class UrsusVillagerGoalPackages {
    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super UrsusVillager>>> getCorePackage() {
        return ImmutableList.of(
                Pair.of(0, new Swim(0.8F)),
                Pair.of(0, InteractWithDoor.create()),
                Pair.of(0, new LookAtTargetSink(45, 90)),
                Pair.of(1, new MoveToTargetSink(800,1000)),
                Pair.of(10, AcquirePoi.create((p_217497_) -> {
                    return p_217497_.is(PoiTypes.MEETING);}, MemoryModuleType.MEETING_POINT, true, Optional.of((byte)14)))
        );
    }

   public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super UrsusVillager>>> getIdlePackage(float pSpeedModifier) {
      return ImmutableList.of(
              Pair.of(5, new TestBehavior(100)),
              Pair.of(2, new RunOne<>(ImmutableList.of(
                      Pair.of(VillageBoundRandomStroll.create(pSpeedModifier), 1), Pair.of(SetWalkTargetFromLookTarget.create(pSpeedModifier, 2), 1),
                      Pair.of(new DoNothing(30, 60), 1))))
      );
   }

   public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super UrsusVillager>>> getRestPackage(float pSpeedModifier) {
      return ImmutableList.of(
              Pair.of(1, SetWalkTargetMemory.create(MemoryModuleType.HOME, pSpeedModifier, 1, 150)),
              Pair.of(1, SetWalkTargetMemory.create(MemoryModuleType.HOME, pSpeedModifier, 1, 150)),
              Pair.of(2, new RunOne<>(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT), ImmutableList.of(
                      Pair.of(InsideBrownianWalk.create(pSpeedModifier), 4),
                      Pair.of(new DoNothing(20, 40), 2)
              ))));
   }

   public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> getRestPackage(VillagerProfession pProfession, float pSpeedModifier) {
      return ImmutableList.of(
              Pair.of(3, new SleepInBed()),
              Pair.of(5, new RunOne<>(ImmutableMap.of(MemoryModuleType.HOME, MemoryStatus.VALUE_ABSENT), ImmutableList.of(
                      Pair.of(SetClosestHomeAsWalkTarget.create(pSpeedModifier), 1),
                      Pair.of(InsideBrownianWalk.create(pSpeedModifier), 4),
                      Pair.of(GoToClosestVillage.create(pSpeedModifier, 4), 2),
                      Pair.of(new DoNothing(20, 40), 2)))
              ),
              Pair.of(99, UpdateActivityFromSchedule.create()));
   }

    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> getIdlePackage(VillagerProfession pProfession, float pSpeedModifier) {
        return ImmutableList.of(
                Pair.of(2, new RunOne<>(ImmutableList.of(
                        Pair.of(InteractWith.of(EntityType.VILLAGER, 8, MemoryModuleType.INTERACTION_TARGET, pSpeedModifier, 2), 2),
                        Pair.of(InteractWith.of(EntityType.VILLAGER, 8, AgeableMob::canBreed, AgeableMob::canBreed, MemoryModuleType.BREED_TARGET, pSpeedModifier, 2), 1),
                        Pair.of(InteractWith.of(EntityType.CAT, 8, MemoryModuleType.INTERACTION_TARGET, pSpeedModifier, 2), 1),
                        Pair.of(VillageBoundRandomStroll.create(pSpeedModifier), 1), Pair.of(SetWalkTargetFromLookTarget.create(pSpeedModifier, 2), 1),
                        Pair.of(new JumpOnBed(pSpeedModifier), 1), Pair.of(new DoNothing(30, 60), 1)))),
                Pair.of(3, new GiveGiftToHero(100)),
                Pair.of(3, SetLookAndInteract.create(EntityType.PLAYER, 4)),
                Pair.of(3, new ShowTradesToPlayer(400, 1600)),
                Pair.of(3, new GateBehavior<>(ImmutableMap.of(), ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET), GateBehavior.OrderPolicy.ORDERED, GateBehavior.RunningPolicy.RUN_ONE, ImmutableList.of(
                        Pair.of(new TradeWithVillager(), 1)))),
                Pair.of(3, new GateBehavior<>(ImmutableMap.of(), ImmutableSet.of(MemoryModuleType.BREED_TARGET), GateBehavior.OrderPolicy.ORDERED, GateBehavior.RunningPolicy.RUN_ONE, ImmutableList.of(
                        Pair.of(new VillagerMakeLove(), 1)))),
                Pair.of(99, UpdateActivityFromSchedule.create()));
    }
}