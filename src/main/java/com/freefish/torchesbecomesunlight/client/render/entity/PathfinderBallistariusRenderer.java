package com.freefish.torchesbecomesunlight.client.render.entity;

import com.freefish.torchesbecomesunlight.client.render.layer.DialogueIconLayer;
import com.freefish.torchesbecomesunlight.client.render.model.PathfinderBallistariusModel;
import com.freefish.torchesbecomesunlight.server.entity.dlc.PathfinderBallistarius;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PathfinderBallistariusRenderer extends GeoEntityRenderer<PathfinderBallistarius> {
    public PathfinderBallistariusRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PathfinderBallistariusModel());
        addRenderLayer(new DialogueIconLayer<>(this));
    }

    @Override
    public void render(PathfinderBallistarius entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(0.75f,0.75f,0.75f);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }
}
