package com.freefish.torchesbecomesunlight.client.render.Item;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.model.PreparationOpModel;
import com.freefish.torchesbecomesunlight.client.render.model.tools.MathUtils;
import com.freefish.torchesbecomesunlight.server.item.weapon.RhodesShield;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class RhodesShieldRenderer extends GeoItemRenderer<RhodesShield> {
    public RhodesShieldRenderer() {
        super(new RhodesShieldModel());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack,
                             MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        this.animatable = (RhodesShield) stack.getItem();
        this.currentItemStack = stack;
        this.renderPerspective = transformType;

        if(transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND){
            boolean isActive = stack.hasTag() && stack.getTag().contains("tbsisActive")&& stack.getTag().getBoolean("tbsisActive");
            if (isActive) {
                poseStack.pushPose();
                poseStack.last().pose().translate(0,0,0.3f);

                poseStack.last().pose().translate(-0.25f,-0.15f,0.05f);

                poseStack.mulPose(MathUtils.quatFromRotationXYZ(0,45,0,true));
                poseStack.mulPose(MathUtils.quatFromRotationXYZ(0,0,25,true));
                poseStack.mulPose(MathUtils.quatFromRotationXYZ(20,0,0,true));
                super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
                poseStack.popPose();
            }else {
                super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
            }
        }else if(transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND){
            boolean isActive = stack.hasTag() && stack.getTag().contains("tbsisActive")&& stack.getTag().getBoolean("tbsisActive");
            if (isActive) {
                poseStack.pushPose();
                poseStack.last().pose().translate(0.5f,-0.5f,0.3f);
                poseStack.last().pose().translate(0.25f,0,-0.4f);
                poseStack.mulPose(MathUtils.quatFromRotationXYZ(0,-45,0,true));
                poseStack.mulPose(MathUtils.quatFromRotationXYZ(0,0,25,true));
                poseStack.mulPose(MathUtils.quatFromRotationXYZ(-20,0,0,true));
                super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
                poseStack.popPose();
            }else {
                super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
            }
        }
        else super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);

    }

    public static class RhodesShieldModel extends GeoModel<RhodesShield> {
        private static final ResourceLocation MODEL = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/rhodeshield.geo.json");

        @Override
        public ResourceLocation getModelResource(RhodesShield animatable) {
            return MODEL;
        }

        @Override
        public ResourceLocation getTextureResource(RhodesShield animatable) {
            return PreparationOpModel.TEXTURE;
        }

        @Override
        public ResourceLocation getAnimationResource(RhodesShield animatable) {
            return null;
        }
    }
}
