package com.freefish.torchesbecomesunlight.client.render.entity;

import com.freefish.torchesbecomesunlight.client.render.model.ManglerModel;
import com.freefish.torchesbecomesunlight.server.entity.animal.Mangler;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ManglerRenderer extends GeoEntityRenderer<Mangler> {
    public ManglerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ManglerModel());
    }

    @Override
    public void render(Mangler entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.scale(0.75f,0.75f,0.75f);
        if(entity.isLeader()){
            poseStack.scale(1.2f,1.2f,1.2f);
        }
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
