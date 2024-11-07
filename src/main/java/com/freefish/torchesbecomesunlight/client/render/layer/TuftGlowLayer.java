package com.freefish.torchesbecomesunlight.client.render.layer;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.entity.effect.IceTuft;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

@OnlyIn(Dist.CLIENT)
public class TuftGlowLayer extends GeoRenderLayer<IceTuft> {

    private static ResourceLocation ice_glow = new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/projectile/ice_tuftglow.png");
    private static ResourceLocation demon_glow = new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/projectile/black_spearglow.png");

    public TuftGlowLayer(GeoRenderer<IceTuft> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, IceTuft animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        super.render(poseStack, animatable, bakedModel, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
        RenderType glowRenderType;
        if(animatable.getTypeNumber()==0)
            glowRenderType = RenderType.eyes(ice_glow);
        else
            glowRenderType = RenderType.eyes(demon_glow);
        float glow = 0f;

        if(animatable.tickCount>40) glow = (animatable.tickCount-40)/50f;
        getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, glowRenderType, bufferSource.getBuffer(glowRenderType), partialTick, packedLight, OverlayTexture.NO_OVERLAY, glow, glow, glow, glow);
    }
}
