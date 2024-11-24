package com.freefish.torchesbecomesunlight.client.render.projectile;

import com.freefish.torchesbecomesunlight.client.render.model.BlackSpearModel;
import com.freefish.torchesbecomesunlight.server.entity.projectile.BlackSpear;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BlackSpearRenderer extends GeoEntityRenderer<BlackSpear> {
    public BlackSpearRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BlackSpearModel());
    }

    @Override
    public void render(BlackSpear entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if(entity.getType1()==3)
            poseStack.scale(1.5f,1.5f,1.5f);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}

