package com.freefish.torchesbecomesunlight.server.entity.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class LightingBoom extends NoGravityProjectileEntity{
    public LightingBoom(EntityType<? extends NoGravityProjectileEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
}
