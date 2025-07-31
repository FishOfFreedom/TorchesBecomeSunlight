package com.freefish.torchesbecomesunlight.server.story.task;

import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.PlayerCapability;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Supplier;

public class KillEntityTaskType extends TaskType<KillEntityTask>{

    public KillEntityTaskType(Supplier<KillEntityTask> supplier) {
        super(supplier);
    }

    public static void handleKillEntityTask(Player player, LivingEntity killedEntity){
        Level level = player.level();
        if(level instanceof ServerLevel){
            PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(player, CapabilityHandle.PLAYER_CAPABILITY);
            if (capability != null) {
                List<KillEntityTask> taskByType = capability.getTaskByType(TaskHandle.KILL_ENTITY_TASK);
                ResourceLocation key = level.registryAccess().registryOrThrow(Registries.ENTITY_TYPE).getKey(killedEntity.getType());
                if(key!=null) {
                    for (KillEntityTask task : taskByType) {
                        task.killEntity(key.toString());
                    }
                }
            }
        }
    }
}
