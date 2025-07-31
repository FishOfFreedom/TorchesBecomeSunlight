package com.freefish.torchesbecomesunlight.client.render.blockentity;

import com.freefish.torchesbecomesunlight.client.render.model.BigBenModel;
import com.freefish.torchesbecomesunlight.server.block.blockentity.BigBenBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class BigBenRenderer extends GeoBlockRenderer<BigBenBlockEntity> {
    public BigBenRenderer() {
        super(new BigBenModel());
    }

    @Override
    public void actuallyRender(PoseStack poseStack, BigBenBlockEntity animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, 0xF000F0, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public boolean shouldRenderOffScreen(BigBenBlockEntity pBlockEntity) {
        return true;
    }
}
