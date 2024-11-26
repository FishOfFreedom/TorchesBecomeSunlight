package com.freefish.torchesbecomesunlight.client.render.entity;

import com.freefish.torchesbecomesunlight.client.render.model.ShieldGuardModel;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.shield.ShieldGuard;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ShieldGuardRenderer extends GeoEntityRenderer<ShieldGuard> {
    public ShieldGuardRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ShieldGuardModel());
    }

    @Override
    public void render(ShieldGuard entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.scale(0.75f,0.75f,0.75f);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
