package com.freefish.torchesbecomesunlight.client.render.entity.village;

import com.freefish.torchesbecomesunlight.client.render.layer.VillagerArmorLayer;
import com.freefish.torchesbecomesunlight.client.render.layer.VillagerHeadLayer;
import com.freefish.torchesbecomesunlight.client.render.layer.VillagerItemLayer;
import com.freefish.torchesbecomesunlight.client.render.model.ManModel;
import com.freefish.torchesbecomesunlight.client.render.model.player.ModelBipedAnimated;
import com.freefish.torchesbecomesunlight.server.entity.villager.MaleVillager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ManRenderer extends GeoEntityRenderer<MaleVillager> {

    public ManRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ManModel());
        addRenderLayer(new VillagerHeadLayer<>(this));
        addRenderLayer(new VillagerItemLayer<>(this));
        addRenderLayer(new VillagerArmorLayer<>(this,new ModelBipedAnimated<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),new ModelBipedAnimated<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR))));
    }

    @Override
    public void renderRecursively(PoseStack poseStack, MaleVillager animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
