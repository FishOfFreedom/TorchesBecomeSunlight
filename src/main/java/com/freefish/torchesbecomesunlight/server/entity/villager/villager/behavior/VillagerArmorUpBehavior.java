package com.freefish.torchesbecomesunlight.server.entity.villager.villager.behavior;

import com.freefish.torchesbecomesunlight.server.entity.villager.UrsusVillager;
import com.freefish.torchesbecomesunlight.server.init.village.MemoryModuleTypeHandle;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.VillagerGoalPackages;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

import java.util.Map;

public class VillagerArmorUpBehavior extends Behavior<UrsusVillager> {
    public VillagerArmorUpBehavior() {
        super(ImmutableMap.of(MemoryModuleTypeHandle.ARMOR_STAND_POS.get(),MemoryStatus.VALUE_PRESENT));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel pLevel, UrsusVillager pOwner) {
        return !pOwner.isArmor();
    }

    @Override
    protected boolean canStillUse(ServerLevel pLevel, UrsusVillager pEntity, long pGameTime) {
        return checkExtraStartConditions(pLevel,pEntity);
    }

    @Override
    protected void tick(ServerLevel pLevel, UrsusVillager pOwner, long pGameTime) {
    }
}
