package com.freefish.torchesbecomesunlight.client.render.projectile;

import com.freefish.torchesbecomesunlight.client.render.model.RosmontisLivingInstallationModel;
import com.freefish.torchesbecomesunlight.server.entity.rhodesIsland.rosmontis.RosmontisLivingInstallation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RosmontisLivingInstallationRenderer extends GeoEntityRenderer<RosmontisLivingInstallation> {
    public RosmontisLivingInstallationRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new RosmontisLivingInstallationModel());
    }

    @Override
    protected float getDeathMaxRotation(RosmontisLivingInstallation animatable) {
        return 0;
    }

    @Override
    public void actuallyRender(PoseStack poseStack, RosmontisLivingInstallation animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        float time = Math.min(1,(animatable.tickCount+partialTick)/20);
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, time);
    }

    @Override
    public int getPackedOverlay(RosmontisLivingInstallation animatable, float u) {
        return OverlayTexture.NO_OVERLAY;
    }

    @Override
    public RenderType getRenderType(RosmontisLivingInstallation animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}
