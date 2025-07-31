package com.freefish.torchesbecomesunlight.server.partner.ai;

import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

public class HurtByPartnerTargetGoal extends TargetGoal {
   private static final TargetingConditions HURT_BY_TARGETING = TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting();
   private boolean alertSameType;
   /** Store the previous revengeTimer value */
   private int timestamp;
   private Player player;

   @Nullable
   private Class<?>[] toIgnoreAlert;

   public HurtByPartnerTargetGoal(PathfinderMob pMob, Player player) {
      super(pMob, true);
      this.player = player;
      this.setFlags(EnumSet.of(Goal.Flag.TARGET));
   }

   /**
    * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
    * method as well.
    */
   public boolean canUse() {
      int i = this.mob.getLastHurtByMobTimestamp();
      LivingEntity livingentity = this.mob.getLastHurtByMob();


      int i1 = this.player.getLastHurtByMobTimestamp();
      LivingEntity livingentity1 = this.player.getLastHurtByMob();
      if ((i != this.timestamp && livingentity != null)||(i1 != this.timestamp && livingentity1 != null)) {
         if(livingentity==null){
            livingentity = livingentity1;
         }

         if (livingentity.getType() == EntityType.PLAYER && this.mob.level().getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
            return false;
         } else {
            return this.canAttack(livingentity, HURT_BY_TARGETING);
         }
      } else {
         return false;
      }
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void start() {
      LivingEntity lastHurtByMob = this.mob.getLastHurtByMob();
      if(lastHurtByMob==null) lastHurtByMob = player.getLastHurtByMob();

      this.mob.setTarget(lastHurtByMob);

      this.targetMob = this.mob.getTarget();
      this.timestamp = this.mob.getLastHurtByMobTimestamp();
      this.unseenMemoryTicks = 300;
      if (this.alertSameType) {
         this.alertOthers();
      }

      super.start();
   }

   protected void alertOthers() {
      double d0 = this.getFollowDistance();
      AABB aabb = AABB.unitCubeFromLowerCorner(this.mob.position()).inflate(d0, 10.0D, d0);
      List<? extends Mob> list = this.mob.level().getEntitiesOfClass(this.mob.getClass(), aabb, EntitySelector.NO_SPECTATORS);
      Iterator iterator = list.iterator();

      while(true) {
         Mob mob;
         while(true) {
            if (!iterator.hasNext()) {
               return;
            }

            mob = (Mob)iterator.next();
            if (this.mob != mob && mob.getTarget() == null && (!(this.mob instanceof TamableAnimal) || ((TamableAnimal)this.mob).getOwner() == ((TamableAnimal)mob).getOwner()) && !mob.isAlliedTo(this.mob.getLastHurtByMob())) {
               if (this.toIgnoreAlert == null) {
                  break;
               }

               boolean flag = false;

               for(Class<?> oclass : this.toIgnoreAlert) {
                  if (mob.getClass() == oclass) {
                     flag = true;
                     break;
                  }
               }

               if (!flag) {
                  break;
               }
            }
         }
      }
   }
}