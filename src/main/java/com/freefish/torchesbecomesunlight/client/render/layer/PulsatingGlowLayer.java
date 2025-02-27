package com.freefish.torchesbecomesunlight.client.render.layer;

import com.freefish.torchesbecomesunlight.client.render.util.FFRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
public class PulsatingGlowLayer<T extends LivingEntity & GeoEntity> extends GeoRenderLayer<T> {

    public ResourceLocation textureLocation;

    public float pulseSpeed;
    public float pulseAmount;
    public float minimumPulseAmount;
    public Predicate<T> predicate;

    public PulsatingGlowLayer(GeoRenderer<T> entityRendererIn, ResourceLocation textureLocation, float pulseSpeed, float pulseAmount, float minimumPulseAmount, Predicate<T> pFilter) {
        super(entityRendererIn);
        this.textureLocation = textureLocation;
        this.pulseSpeed = pulseSpeed;
        this.pulseAmount = pulseAmount;
        this.minimumPulseAmount = minimumPulseAmount;
        this.predicate = pFilter;
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        super.render(poseStack, animatable, bakedModel, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
        if(predicate.test(animatable))
        {
            RenderType glowRenderType = FFRenderTypes.eyes(textureLocation);
            float glow = Math.max(minimumPulseAmount, Mth.cos(partialTick * pulseSpeed) * pulseAmount);
            getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, glowRenderType, bufferSource.getBuffer(glowRenderType), partialTick, packedLight, OverlayTexture.NO_OVERLAY, glow, glow, glow, glow);
        }
    }
}