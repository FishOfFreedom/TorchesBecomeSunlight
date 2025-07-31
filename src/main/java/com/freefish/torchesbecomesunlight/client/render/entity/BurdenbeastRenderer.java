package com.freefish.torchesbecomesunlight.client.render.entity;

import com.freefish.torchesbecomesunlight.client.render.model.BurdenbeastModel;
import com.freefish.torchesbecomesunlight.server.entity.animal.Burdenbeast;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BurdenbeastRenderer extends GeoEntityRenderer<Burdenbeast> {
    public BurdenbeastRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BurdenbeastModel());
    }

    @Override
    public void render(Burdenbeast entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(1.86f,1.86f,1.86f);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }
}
