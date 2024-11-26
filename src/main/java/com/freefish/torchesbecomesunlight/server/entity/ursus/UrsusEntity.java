package com.freefish.torchesbecomesunlight.server.entity.ursus;

import com.freefish.torchesbecomesunlight.server.util.animation.AnimationAct;
import com.freefish.torchesbecomesunlight.server.entity.AnimatedEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class UrsusEntity extends AnimatedEntity {
    public UrsusEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public AnimationAct getDeathAnimation() {
        return null;
    }
}
