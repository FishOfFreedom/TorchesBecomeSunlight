package com.freefish.torchesbecomesunlight.server.entity.villager.villager;

import com.freefish.torchesbecomesunlight.server.entity.villager.UrsusVillager;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

import java.util.Optional;

public class TestBehavior extends Behavior<UrsusVillager> {
    public TestBehavior(int pDuration) {
        super(ImmutableMap.of(MemoryModuleType.HOME, MemoryStatus.REGISTERED), pDuration);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel pLevel, UrsusVillager pOwner) {
        return true;
    }

    @Override
    protected boolean canStillUse(ServerLevel pLevel, UrsusVillager pEntity, long pGameTime) {
        return true;
    }

    @Override
    protected void tick(ServerLevel pLevel, UrsusVillager pOwner, long pGameTime) {
        Optional<GlobalPos> memory = pOwner.getBrain().getMemory(MemoryModuleType.HOME);
    }
}
