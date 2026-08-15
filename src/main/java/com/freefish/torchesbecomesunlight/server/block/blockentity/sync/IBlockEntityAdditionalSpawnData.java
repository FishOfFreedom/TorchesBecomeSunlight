package com.freefish.torchesbecomesunlight.server.block.blockentity.sync;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.FriendlyByteBuf;

public interface IBlockEntityAdditionalSpawnData
{
    void writeSpawnData(RegistryFriendlyByteBuf buffer);

    void readSpawnData(RegistryFriendlyByteBuf additionalData);
}