package com.freefish.torchesbecomesunlight.client;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

public class DelayedMultiBufferSource extends MultiBufferSource.BufferSource {
    public DelayedMultiBufferSource(BufferBuilder builder) {
        super(builder, new Object2ObjectLinkedOpenHashMap<>());
    }

    @Override
    public VertexConsumer getBuffer(RenderType renderType) {
        if (!fixedBuffers.containsKey(renderType)) {
            fixedBuffers.put(renderType, new BufferBuilder(renderType.bufferSize()));
        }
        return super.getBuffer(renderType);
    }

    @Override
    public void endBatch() {
        for (RenderType renderType : fixedBuffers.keySet()) {
            endBatch(renderType);
            fixedBuffers.get(renderType).end();
        }
        fixedBuffers.clear();
    }
}
