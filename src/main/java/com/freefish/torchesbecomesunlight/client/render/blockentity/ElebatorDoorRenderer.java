package com.freefish.torchesbecomesunlight.client.render.blockentity;

import com.freefish.torchesbecomesunlight.client.render.model.ElevatorDoorModel;
import com.freefish.torchesbecomesunlight.server.block.blockentity.ElevatorDoorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ElebatorDoorRenderer extends GeoBlockRenderer<ElevatorDoorBlockEntity> {
    public ElebatorDoorRenderer() {
        super(new ElevatorDoorModel());
    }

    @Override
    public boolean shouldRenderOffScreen(ElevatorDoorBlockEntity pBlockEntity) {
        return true;
    }

    @Override
    public void actuallyRender(PoseStack poseStack, ElevatorDoorBlockEntity animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
