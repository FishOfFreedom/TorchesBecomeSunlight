package com.freefish.torchesbecomesunlight.client.render.entity;

import com.freefish.torchesbecomesunlight.client.render.layer.DialogueIconLayer;
import com.freefish.torchesbecomesunlight.client.render.layer.PulsatingGlowLayer;
import com.freefish.torchesbecomesunlight.client.render.model.GunKnightPatriotModel;
import com.freefish.torchesbecomesunlight.server.entity.dlc.GunKnightPatriot;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GunKnightPatriotRenderer extends GeoEntityRenderer<GunKnightPatriot> {
    public GunKnightPatriotRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GunKnightPatriotModel());
        addRenderLayer(new DialogueIconLayer(this));
        addRenderLayer(new PulsatingGlowLayer<GunKnightPatriot>(this, GunKnightPatriotModel.TEXTURE_GLOW, 0.1F, 1.0F, 0.25F, gunKnightPatriot -> true));
    }

    @Override
    public void render(GunKnightPatriot entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    protected float getDeathMaxRotation(GunKnightPatriot animatable) {
        return 0;
    }

    @Override
    public int getPackedOverlay(GunKnightPatriot animatable, float u) {
        return OverlayTexture.pack(OverlayTexture.u(u),
                OverlayTexture.v(animatable.hurtTime > 0));
    }

    //@Override
    //public void renderRecursively(PoseStack poseStack, GunKnightPatriot animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
        float red = net.minecraft.util.FastColor.ARGB32.red(color) / 255.0F;
        float green = net.minecraft.util.FastColor.ARGB32.green(color) / 255.0F;
        float blue = net.minecraft.util.FastColor.ARGB32.blue(color) / 255.0F;
        float alpha = net.minecraft.util.FastColor.ARGB32.alpha(color) / 255.0F;
    //    if(bone.getName().matches("Wing.*")){
    //        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, net.minecraft.util.FastColor.ARGB32.colorFromFloat(0.6f+ 0.4f*bone.getPosZ(), red, green, blue));
    //    }
    //    else
    //        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, net.minecraft.util.FastColor.ARGB32.colorFromFloat(alpha, red, green, blue));
    //}
}
