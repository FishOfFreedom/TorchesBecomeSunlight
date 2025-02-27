package com.freefish.torchesbecomesunlight.server.entity.villager.villager;

import com.freefish.torchesbecomesunlight.server.entity.villager.UrsusVillager;
import com.freefish.torchesbecomesunlight.server.entity.villager.villager.behavior.SetWalkFromPosBehavior;
import com.freefish.torchesbecomesunlight.server.entity.villager.villager.behavior.WalkToHomeBehavior;
import com.freefish.torchesbecomesunlight.server.entity.villager.villager.behavior.WalkUntilPosBehavior;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;

import java.util.Optional;

public class UrsusVillagerGoalPackages {
    //public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> getCorePackage(float pSpeedModifier) {
    //    return ImmutableList.of(Pair.of(0, new Swim(0.8F)), Pair.of(0, InteractWithDoor.create()), Pair.of(0, new LookAtTargetSink(45, 90)), Pair.of(0, new VillagerPanicTrigger()), Pair.of(0, WakeUp.create()), Pair.of(0, ReactToBell.create()), Pair.of(0, SetRaidStatus.create()), Pair.of(0, ValidateNearbyPoi.create(pProfession.heldJobSite(), MemoryModuleType.JOB_SITE)), Pair.of(0, ValidateNearbyPoi.create(pProfession.acquirableJobSite(), MemoryModuleType.POTENTIAL_JOB_SITE)), Pair.of(1, new MoveToTargetSink()), Pair.of(2, PoiCompetitorScan.create()), Pair.of(3, new LookAndFollowTradingPlayerSink(pSpeedModifier)), Pair.of(5, GoToWantedItem.create(pSpeedModifier, false, 4)), Pair.of(6, AcquirePoi.create(pProfession.acquirableJobSite(), MemoryModuleType.JOB_SITE, MemoryModuleType.POTENTIAL_JOB_SITE, true, Optional.empty())), Pair.of(7, new GoToPotentialJobSite(pSpeedModifier)), Pair.of(8, YieldJobSite.create(pSpeedModifier)), Pair.of(10, AcquirePoi.create((p_217499_) -> {
    //        return p_217499_.is(PoiTypes.HOME);
    //    }, MemoryModuleType.HOME, false, Optional.of((byte)14))), Pair.of(10, AcquirePoi.create((p_217497_) -> {
    //        return p_217497_.is(PoiTypes.MEETING);
    //    }, MemoryModuleType.MEETING_POINT, true, Optional.of((byte)14))), Pair.of(10, AssignProfessionFromJobSite.create()), Pair.of(10, ResetProfession.create()));
    //}

   public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super UrsusVillager>>> getIdlePackage(float pSpeedModifier) {
      return ImmutableList.of(
              //Pair.of(1,new RunOne<>(ImmutableList.of(
              //        Pair.of( new TestBehavior(100),2),
              //        Pair.of(new WalkToHomeBehavior(100),1)))
              //),
              //Pair.of(1, OneBehavior.create(1,1)),
              Pair.of(5, new TestBehavior(100)),
              Pair.of(4, new WalkToHomeBehavior(100)));
   }

   public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super UrsusVillager>>> getRestPackage(float pSpeedModifier) {
      return ImmutableList.of(
              Pair.of(1, SetWalkFromPosBehavior.create(MemoryModuleType.HOME,0.5f)),
              Pair.of(99, new WalkUntilPosBehavior(MemoryModuleType.HOME,10000)),
              Pair.of(4, new WalkToHomeBehavior(100)));
   }

   public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> getRestPackage(VillagerProfession pProfession, float pSpeedModifier) {
      return ImmutableList.of(
              Pair.of(2, SetWalkTargetFromBlockMemory.create(MemoryModuleType.HOME, pSpeedModifier, 1, 150, 1200)),
              Pair.of(3, ValidateNearbyPoi.create((p_217495_) -> {
                 return p_217495_.is(PoiTypes.HOME);
                 }, MemoryModuleType.HOME)),
              Pair.of(3, new SleepInBed()),
              Pair.of(5, new RunOne<>(ImmutableMap.of(MemoryModuleType.HOME, MemoryStatus.VALUE_ABSENT), ImmutableList.of(
                      Pair.of(SetClosestHomeAsWalkTarget.create(pSpeedModifier), 1),
                      Pair.of(InsideBrownianWalk.create(pSpeedModifier), 4),
                      Pair.of(GoToClosestVillage.create(pSpeedModifier, 4), 2),
                      Pair.of(new DoNothing(20, 40), 2)))
              ),
              Pair.of(99, UpdateActivityFromSchedule.create()));
   }
}