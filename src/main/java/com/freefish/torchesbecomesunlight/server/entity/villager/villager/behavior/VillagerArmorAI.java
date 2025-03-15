package com.freefish.torchesbecomesunlight.server.entity.villager.villager.behavior;

import com.freefish.torchesbecomesunlight.server.entity.villager.UrsusVillager;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

import static com.freefish.torchesbecomesunlight.server.util.animation.IAnimatedEntity.NO_ANIMATION;

public class VillagerArmorAI extends Behavior<UrsusVillager> {
    public VillagerArmorAI() {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel pLevel, UrsusVillager pOwner) {
        LivingEntity livingEntity = pOwner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
        return livingEntity!=null&&livingEntity.isAlive();
    }

    @Override
    protected boolean canStillUse(ServerLevel pLevel, UrsusVillager pEntity, long pGameTime) {
        return checkExtraStartConditions(pLevel,pEntity);
    }

    @Override
    protected void start(ServerLevel pLevel, UrsusVillager pEntity, long pGameTime) {
        super.start(pLevel, pEntity, pGameTime);
        pEntity.setAggressive(true);
    }


    @Override
    protected void stop(ServerLevel pLevel, UrsusVillager pEntity, long pGameTime) {
        super.stop(pLevel, pEntity, pGameTime);
        pEntity.setAggressive(false);
        pEntity.getNavigation().stop();
    }

    @Override
    public void tick(ServerLevel pLevel, UrsusVillager pOwner, long pGameTime) {
        LivingEntity target = pOwner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
        if (target == null) return;
        RandomSource random = pOwner.getRandom();

        if(!(pOwner.getAnimation() == NO_ANIMATION)) return;
        walk(pOwner,target);

        double dist = pOwner.distanceTo(target);

        if (target.getY() - pOwner.getY() >= -1 && target.getY() - pOwner.getY() <= 2) {
            if (dist < 1D  && Math.abs(MathUtils.wrapDegrees(pOwner.getAngleBetweenEntities(target, pOwner) - pOwner.yBodyRot)) < 35.0D) {
                double rand = random.nextDouble();
                if (rand >= 0.6)
                    AnimationActHandler.INSTANCE.sendAnimationMessage(pOwner, UrsusVillager.ATTACK);
                else
                    AnimationActHandler.INSTANCE.sendAnimationMessage(pOwner, UrsusVillager.ATTACK);
            }
        }
    }

    private void walk(UrsusVillager pOwner,LivingEntity target){
        if(target!=null) {
            moveMode(pOwner,target);
        }
    }

    private boolean moveMode(UrsusVillager pOwner,LivingEntity target){
        return pOwner.getNavigation().moveTo(target, 0.24);
    }
}
