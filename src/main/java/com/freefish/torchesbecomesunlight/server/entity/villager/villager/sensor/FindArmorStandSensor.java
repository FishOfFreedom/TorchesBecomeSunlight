package com.freefish.torchesbecomesunlight.server.entity.villager.villager.sensor;

import com.freefish.torchesbecomesunlight.server.init.village.MemoryModuleTypeHandle;
import com.freefish.torchesbecomesunlight.server.util.FFEntityUtils;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.schedule.Activity;

import java.util.Set;
public class FindArmorStandSensor extends Sensor<LivingEntity> {
    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleTypeHandle.ARMOR_STAND_POS.get());
    }

    @Override
    protected void doTick(ServerLevel level, LivingEntity entity) {
        ArmorStand globalPos = getGlobalPos(level, entity);
        if(globalPos!=null&&entity.getBrain().getActiveNonCoreActivity().orElse(Activity.IDLE)!= Activity.FIGHT) {
            entity.getBrain().setMemory(MemoryModuleTypeHandle.ARMOR_STAND_POS.get(), GlobalPos.of(globalPos.level().dimension(),globalPos.getOnPos()));
        }
    }

    private ArmorStand getGlobalPos (ServerLevel level , LivingEntity living){
        ArmorStand closestEntity = FFEntityUtils.getClosestEntity(living, level.getEntitiesOfClass(ArmorStand.class, living.getBoundingBox().inflate(4)));
        return closestEntity;
    }
}