package com.freefish.torchesbecomesunlight.server.block.blockentity.sync;

import net.minecraft.network.FriendlyByteBuf;

public interface IBlockEntityAdditionalSpawnData
{
    void writeSpawnData(FriendlyByteBuf buffer);

    void readSpawnData(FriendlyByteBuf additionalData);
}