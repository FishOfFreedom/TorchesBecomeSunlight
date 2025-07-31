package com.freefish.torchesbecomesunlight.server.world.levelEntity;

import com.freefish.rosmontislib.levelentity.InstanceLevelEntityType;
import com.freefish.rosmontislib.levelentity.LevelEntityHandle;
import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraft.resources.ResourceLocation;

public class TBSLevelEntityHandle {
    public static InstanceLevelEntityType<RhodesIslandLevelEntity> RHODES_ISLAND_DATA;

    public static void init(){
        RHODES_ISLAND_DATA = LevelEntityHandle.registerInstance(new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"rhodes_island"),RhodesIslandLevelEntity::new);
    }
}
