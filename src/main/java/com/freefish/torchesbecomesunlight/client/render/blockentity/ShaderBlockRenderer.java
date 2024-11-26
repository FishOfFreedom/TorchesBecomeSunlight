package com.freefish.torchesbecomesunlight.client.render.blockentity;

import com.freefish.torchesbecomesunlight.client.ClientHandler;
import com.freefish.torchesbecomesunlight.client.render.util.FFRenderTypes;
import com.freefish.torchesbecomesunlight.server.block.blockentity.ShaderBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class ShaderBlockRenderer implements BlockEntityRenderer<ShaderBlockEntity> {

    public ShaderBlockRenderer(BlockEntityRendererProvider.Context pContext) {
    }

    @Override
    public void render(ShaderBlockEntity pBlockEntity, float pPartialTick, PoseStack stack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        VertexConsumer vertexConsumer = ClientHandler.DELAYED_BUFFER_SOURCE.getBuffer(FFRenderTypes.PORTAL);
        PoseStack.Pose pose = stack.last();
        Matrix4f pose1 = pose.pose();
        Matrix3f pose2 = pose.normal();
        float radius = 1;

        vertexConsumer.vertex(pose1, -radius, -radius, 0.5f).color(1, 1, 1, 1).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(pose2, 0.0F, 1.0F, 0.0F);
        vertexConsumer.vertex(pose1, -radius, 1 + radius, 0.5f).color(1, 1, 1, 1).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(pose2, 0.0F, 1.0F, 0.0F);
        vertexConsumer.vertex(pose1, 1 + radius, 1 + radius, 0.5f).color(1, 1, 1, 1).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(pose2, 0.0F, 1.0F, 0.0F);
        vertexConsumer.vertex(pose1, 1 + radius, -radius, 0.5f).color(1, 1, 1, 1).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(pose2, 0.0F, 1.0F, 0.0F);
    }
}
