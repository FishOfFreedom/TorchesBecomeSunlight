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
    public void actuallyRender(PoseStack poseStack, ElevatorDoorBlockEntity animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
        float red = net.minecraft.util.FastColor.ARGB32.red(color) / 255.0F;
        float green = net.minecraft.util.FastColor.ARGB32.green(color) / 255.0F;
        float blue = net.minecraft.util.FastColor.ARGB32.blue(color) / 255.0F;
        float alpha = net.minecraft.util.FastColor.ARGB32.alpha(color) / 255.0F;
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, net.minecraft.util.FastColor.ARGB32.colorFromFloat(alpha, red, green, blue));
    }
}
