package com.freefish.torchesbecomesunlight.server.entity.projectile;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class LightingBoom extends NoGravityProjectileEntity{
    public LightingBoom(EntityType<? extends NoGravityProjectileEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {

    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {

    }
}
