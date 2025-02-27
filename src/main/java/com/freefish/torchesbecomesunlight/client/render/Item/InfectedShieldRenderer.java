package com.freefish.torchesbecomesunlight.client.render.Item;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.model.PatriotModel;
import com.freefish.torchesbecomesunlight.server.item.weapon.InfectedHalberd;
import com.freefish.torchesbecomesunlight.server.item.weapon.InfectedShield;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.Optional;

public class InfectedShieldRenderer extends GeoItemRenderer<InfectedShield> {
    public InfectedShieldRenderer() {
        super(new InfectedShieldModel());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack,
                             MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        this.animatable = (InfectedShield) stack.getItem();
        this.currentItemStack = stack;
        this.renderPerspective = transformType;

        if (transformType == ItemDisplayContext.GUI) {
            //renderInGui(transformType, poseStack, bufferSource, packedLight, packedOverlay);

            RenderType armorRenderType = RenderType.armorCutoutNoCull(PatriotModel.TEXTURE);
            BakedGeoModel model = this.getGeoModel().getBakedModel(this.getGeoModel().getModelResource(animatable));
            Optional<GeoBone> leftArm = model.getBone("bone");
            if(leftArm.isPresent()){
                poseStack.pushPose();
                poseStack.last().pose().translate(-0.65f,0.5f,0);
                GeoBone geoBone = leftArm.get();
                renderRecursively(poseStack,animatable,geoBone,armorRenderType,bufferSource,bufferSource.getBuffer(armorRenderType),false,0
                        ,packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1,1);
                poseStack.popPose();
            }

        }
        else {
            RenderType renderType = getRenderType(this.animatable, getTextureLocation(this.animatable), bufferSource, Minecraft.getInstance().getFrameTime());
            VertexConsumer buffer = ItemRenderer.getFoilBufferDirect(bufferSource, renderType, false, this.currentItemStack != null && this.currentItemStack.hasFoil());

            defaultRender(poseStack, this.animatable, bufferSource, renderType, buffer,
                    0, Minecraft.getInstance().getFrameTime(), packedLight);
        }
    }

    public static class InfectedShieldModel extends GeoModel<InfectedShield> {
        private static final ResourceLocation MODEL = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/shield.geo.json");
        private static final ResourceLocation ANIMATION = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "animations/shield.animation.json");

        @Override
        public ResourceLocation getModelResource(InfectedShield animatable) {
            return MODEL;
        }

        @Override
        public ResourceLocation getTextureResource(InfectedShield animatable) {
            return PatriotModel.TEXTURE;
        }

        @Override
        public ResourceLocation getAnimationResource(InfectedShield animatable) {
            return ANIMATION;
        }
    }
}
