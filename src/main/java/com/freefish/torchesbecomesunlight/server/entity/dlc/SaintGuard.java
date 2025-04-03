package com.freefish.torchesbecomesunlight.server.entity.dlc;

import com.freefish.torchesbecomesunlight.server.entity.AnimatedEntity;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationAct;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class SaintGuard extends AnimatedEntity {
    public SaintGuard(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public AnimationAct getDeathAnimation() {
        return null;
    }
}
