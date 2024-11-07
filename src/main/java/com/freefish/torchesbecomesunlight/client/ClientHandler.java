package com.freefish.torchesbecomesunlight.client;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

public class ClientHandler {
    public static final MultiBufferSource.BufferSource DELAYED_BUFFER_SOURCE = new DelayedMultiBufferSource(new BufferBuilder(RenderType.TRANSIENT_BUFFER_SIZE));
}
