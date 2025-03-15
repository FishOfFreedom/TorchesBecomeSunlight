package com.freefish.torchesbecomesunlight.server.entity.villager.villager.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.scores.Team;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class FindNearestAttackableTargetBehavior<T extends LivingEntity> extends Behavior<Mob> {
    private int unseenTicks;
    @Nullable
    protected LivingEntity targetMob;
    protected int unseenMemoryTicks = 60;
    protected final Class<T> targetType;
    protected final int randomInterval;
    @Nullable
    protected LivingEntity target;
    protected TargetingConditions targetConditions;

    public FindNearestAttackableTargetBehavior(Class<T> pTargetType, Mob pOwner, Predicate<LivingEntity> pTargetPredicate) {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET,MemoryStatus.VALUE_ABSENT));
        this.targetType = pTargetType;
        this.randomInterval = reducedTickDelay(10);
        this.targetConditions = TargetingConditions.forCombat().range(this.getFollowDistance(pOwner)).selector(pTargetPredicate);
    }

    public FindNearestAttackableTargetBehavior(Class<T> pTargetType, Mob pOwner) {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET,MemoryStatus.VALUE_ABSENT));
        this.targetType = pTargetType;
        this.randomInterval = reducedTickDelay(10);
        this.targetConditions = TargetingConditions.forCombat().range(this.getFollowDistance(pOwner)).selector((living)->true);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel pLevel, Mob pOwner) {
        if (this.randomInterval > 0 && pOwner.getRandom().nextInt(this.randomInterval) != 0) {
            return false;
        } else {
            this.findTarget(pOwner);
            return this.target != null;
        }
    }

    protected AABB getTargetSearchArea(double pTargetDistance, Mob pOwner) {
        return pOwner.getBoundingBox().inflate(pTargetDistance, 4.0D, pTargetDistance);
    }

    protected void findTarget( Mob pOwner) {
        if (this.targetType != Player.class && this.targetType != ServerPlayer.class) {
            this.target = pOwner.level().getNearestEntity(pOwner.level().getEntitiesOfClass(this.targetType, this.getTargetSearchArea(this.getFollowDistance(pOwner),pOwner), (p_148152_) -> {
                return true;
            }), this.targetConditions, pOwner, pOwner.getX(), pOwner.getEyeY(), pOwner.getZ());
        } else {
            this.target = pOwner.level().getNearestPlayer(this.targetConditions, pOwner, pOwner.getX(), pOwner.getEyeY(), pOwner.getZ());
        }

    }

    @Override
    protected boolean canStillUse(ServerLevel pLevel, Mob pOwner, long pGameTime) {
        LivingEntity livingentity = pOwner.getTarget();
        if (livingentity == null) {
            livingentity = this.targetMob;
        }

        if (livingentity == null) {
            return false;
        } else if (!pOwner.canAttack(livingentity)) {
            return false;
        } else {
            Team team = pOwner.getTeam();
            Team team1 = livingentity.getTeam();
            if (team != null && team1 == team) {
                return false;
            } else {
                double d0 = this.getFollowDistance(pOwner);
                if (pOwner.distanceToSqr(livingentity) > d0 * d0) {
                    return false;
                } else {
                    if (pOwner.getSensing().hasLineOfSight(livingentity)) {
                        this.unseenTicks = 0;
                    } else if (++this.unseenTicks > reducedTickDelay(this.unseenMemoryTicks)) {
                        return false;
                    }
                    pOwner.setTarget(livingentity);
                    pOwner.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET,livingentity);
                    pOwner.getBrain().setActiveActivityIfPossible(Activity.FIGHT);
                    return true;
                }
            }
        }
    }

    protected double getFollowDistance(Mob pOwner) {
        return pOwner.getAttributeValue(Attributes.FOLLOW_RANGE);
    }

    @Override
    protected void start(ServerLevel pLevel, Mob pOwner, long pGameTime) {
        super.start(pLevel, pOwner, pGameTime);

        pOwner.setTarget(this.target);
        pOwner.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET,this.target);
        pOwner.getBrain().setActiveActivityIfPossible(Activity.FIGHT);
        this.unseenTicks = 0;
    }

    @Override
    protected void stop(ServerLevel pLevel, Mob pOwner, long pGameTime) {
        super.stop(pLevel, pOwner, pGameTime);
        this.targetMob = null;
    }

    protected static int reducedTickDelay(int pReduction) {
        return Mth.positiveCeilDiv(pReduction, 2);
    }
}
