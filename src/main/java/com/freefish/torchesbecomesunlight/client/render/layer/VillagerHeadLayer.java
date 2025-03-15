package com.freefish.torchesbecomesunlight.client.render.layer;

import com.freefish.torchesbecomesunlight.client.render.model.village.ManModel;
import com.freefish.torchesbecomesunlight.client.render.model.village.WomanModel;
import com.freefish.torchesbecomesunlight.client.render.util.FFRenderTypes;
import com.freefish.torchesbecomesunlight.server.entity.villager.MaleVillager;
import com.freefish.torchesbecomesunlight.server.entity.villager.UrsusVillager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

@OnlyIn(Dist.CLIENT)
public class VillagerHeadLayer<T extends UrsusVillager> extends GeoRenderLayer<T> {
    GeoRenderer geoRenderer;

    public VillagerHeadLayer(GeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
        geoRenderer = entityRendererIn;
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        ResourceLocation headTexture;
        int head = animatable.getHead();
        if(animatable instanceof MaleVillager){
            if(head==0)
                headTexture = ManModel.TEXTUREHEAD_1;
            else if(head==1)
                headTexture = ManModel.TEXTUREHEAD_2;
            else
                headTexture = ManModel.TEXTUREHEAD_3;
        }else {
            if(head==0)
                headTexture = WomanModel.TEXTUREHEAD_1;
            else if(head==1)
                headTexture = WomanModel.TEXTUREHEAD_2;
            else
                headTexture = WomanModel.TEXTUREHEAD_3;
        }

        RenderType glowRenderType = FFRenderTypes.armorCutoutNoCull(headTexture);
        getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, glowRenderType, bufferSource.getBuffer(glowRenderType), partialTick, packedLight, packedOverlay, 1, 1, 1, 1);
        super.render(poseStack, animatable, bakedModel, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
    }
}