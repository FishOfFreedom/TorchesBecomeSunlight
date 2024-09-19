package com.freefish.torchesbecomesunlight.client.render.entity;

import com.freefish.torchesbecomesunlight.client.render.model.SnowNova1Model;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster.SnowNova1;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SnowNova1Renderer extends GeoEntityRenderer<SnowNova1> {
    public SnowNova1Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SnowNova1Model());
    }

    @Override
    public void render(SnowNova1 entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn) {
        stack.pushPose(); // 保存当前矩阵状态
        stack.scale(0.75f, 0.75f, 0.75f);
        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
        stack.popPose();
    }
}
