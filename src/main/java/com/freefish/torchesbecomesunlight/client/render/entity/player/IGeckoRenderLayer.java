package com.freefish.torchesbecomesunlight.client.render.entity.player;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import software.bernie.geckolib.cache.object.GeoBone;

public interface IGeckoRenderLayer {
    default void renderRecursively(GeoBone bone, PoseStack matrixStack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, int color) {
        float red = net.minecraft.util.FastColor.ARGB32.red(color) / 255.0F;
        float green = net.minecraft.util.FastColor.ARGB32.green(color) / 255.0F;
        float blue = net.minecraft.util.FastColor.ARGB32.blue(color) / 255.0F;
        float alpha = net.minecraft.util.FastColor.ARGB32.alpha(color) / 255.0F;

    }
}
