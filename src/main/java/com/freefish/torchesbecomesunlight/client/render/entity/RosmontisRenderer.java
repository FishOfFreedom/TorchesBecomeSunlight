package com.freefish.torchesbecomesunlight.client.render.entity;

import com.freefish.torchesbecomesunlight.client.render.layer.DialogueIconLayer;
import com.freefish.torchesbecomesunlight.client.render.model.RosmontisModel;
import com.freefish.torchesbecomesunlight.server.entity.rhodesIsland.rosmontis.Rosmontis;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.util.Color;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RosmontisRenderer extends GeoEntityRenderer<Rosmontis> {
    public RosmontisRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new RosmontisModel());
        addRenderLayer(new DialogueIconLayer<>(this));
    }

    @Override
    protected float getDeathMaxRotation(Rosmontis animatable) {
        return 0;
    }

    @Override
    public int getPackedOverlay(Rosmontis animatable, float u) {
        return OverlayTexture.pack(OverlayTexture.u(u),
                OverlayTexture.v(animatable.hurtTime > 0));
    }

    @Override
    public Color getRenderColor(Rosmontis animatable, float partialTick, int packedLight) {
        if(animatable.isStartBlack()){
            float startBlack = animatable.getStartBlack(partialTick)/20;
            return Color.ofRGBA(startBlack,startBlack,startBlack,startBlack);
        }
        return super.getRenderColor(animatable, partialTick, packedLight);
    }

    @Override
    public RenderType getRenderType(Rosmontis animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        if(animatable.isStartBlack()){
            return RenderType.entityTranslucent(RosmontisModel.TEXTURE);
        }
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }

    @Override
    public void render(Rosmontis entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn) {
        stack.pushPose();
        stack.scale(0.63f, 0.63f, 0.63f);
        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
        stack.popPose();
    }

    @Override
    public void renderRecursively(PoseStack poseStack, Rosmontis animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
        float red = net.minecraft.util.FastColor.ARGB32.red(color) / 255.0F;
        float green = net.minecraft.util.FastColor.ARGB32.green(color) / 255.0F;
        float blue = net.minecraft.util.FastColor.ARGB32.blue(color) / 255.0F;
        float alpha = net.minecraft.util.FastColor.ARGB32.alpha(color) / 255.0F;
        if(bone.getName().equals("Rosmontis_weapon")){
            RenderSystem.enableBlend();
            super.renderRecursively(poseStack, animatable, bone, RenderType.entityTranslucent(RosmontisModel.TEXTURE), bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, net.minecraft.util.FastColor.ARGB32.colorFromFloat(bone.getPosZ(), red, green, blue));
            RenderSystem.disableBlend();
        }
        else
            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, net.minecraft.util.FastColor.ARGB32.colorFromFloat(alpha, red, green, blue));
    }
}
