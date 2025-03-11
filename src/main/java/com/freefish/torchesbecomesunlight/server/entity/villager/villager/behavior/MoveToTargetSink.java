package com.freefish.torchesbecomesunlight.server.entity.villager.villager.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class MoveToTargetSink extends Behavior<Mob> {
   private int remainingCooldown;

   public MoveToTargetSink() {
      this(150, 250);
   }

   public MoveToTargetSink(int pMinDuration, int pMaxDuration) {
      super(ImmutableMap.of(MemoryModuleType.PATH, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_PRESENT), pMinDuration, pMaxDuration);
   }

   protected boolean checkExtraStartConditions(ServerLevel pLevel, Mob pOwner) {
      if (this.remainingCooldown > 0) {
         --this.remainingCooldown;
         return false;
      } else {
         Brain<?> brain = pOwner.getBrain();
         WalkTarget walktarget = brain.getMemory(MemoryModuleType.WALK_TARGET).get();
         if (!this.reachedTarget(pOwner,walktarget)) {
            return true;
         } else {
            brain.eraseMemory(MemoryModuleType.WALK_TARGET);
            return false;
         }
      }
   }

   protected boolean canStillUse(ServerLevel pLevel, Mob pEntity, long pGameTime) {
      Optional<WalkTarget> optional = pEntity.getBrain().getMemory(MemoryModuleType.WALK_TARGET);
      return optional.isPresent() && !this.reachedTarget(pEntity, optional.get());
   }

   protected void stop(ServerLevel pLevel, Mob pEntity, long pGameTime) {
      if (pEntity.getBrain().hasMemoryValue(MemoryModuleType.WALK_TARGET) && !this.reachedTarget(pEntity, pEntity.getBrain().getMemory(MemoryModuleType.WALK_TARGET).get()) && pEntity.getNavigation().isStuck()) {
         this.remainingCooldown = pLevel.getRandom().nextInt(40);
      }

      pEntity.getNavigation().stop();
      pEntity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
      pEntity.getBrain().eraseMemory(MemoryModuleType.PATH);
   }

   protected void start(ServerLevel pLevel, Mob pEntity, long pGameTime) {
      Path path = pEntity.getNavigation().getPath();


      pEntity.getBrain().setMemory(MemoryModuleType.PATH, path);
      WalkTarget memory = pEntity.getBrain().getMemory(MemoryModuleType.WALK_TARGET).orElse((WalkTarget) null);
      if(memory != null) {
         Vec3 vec3 = memory.getTarget().currentPosition();
         pEntity.getNavigation().moveTo(pEntity.getNavigation().createPath(vec3.x, vec3.y, vec3.z,0), memory.getSpeedModifier());
      }
   }

   protected void tick(ServerLevel pLevel, Mob pOwner, long pGameTime) {
      Path path = pOwner.getNavigation().getPath();
      if(path!=null){
         Vec3 memory = pOwner.getBrain().getMemory(MemoryModuleType.WALK_TARGET).orElse((WalkTarget) null).getTarget().currentPosition();



         BlockPos blockPos = path.getEndNode().asBlockPos();
         pLevel.sendParticles(ParticleTypes.EXPLOSION,blockPos.getX(),blockPos.getY(),blockPos.getZ(),1,0,0,0,0);
         pLevel.sendParticles(ParticleTypes.EXPLOSION,memory.x,memory.y,memory.z,1,0,0,0,0);
      }

      Brain<?> brain = pOwner.getBrain();
      this.start(pLevel, pOwner, pGameTime);
   }

   private boolean reachedTarget(Mob pMob, WalkTarget pTarget) {
      return pTarget.getTarget().currentBlockPosition().distManhattan(pMob.blockPosition()) <= pTarget.getCloseEnoughDist();
   }
}