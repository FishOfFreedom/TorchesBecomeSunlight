package com.freefish.torchesbecomesunlight.client.render.Item;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.model.ShieldGuardModel;
import com.freefish.torchesbecomesunlight.client.render.model.tools.MathUtils;
import com.freefish.torchesbecomesunlight.server.item.weapon.GuardShield;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class GuardShieldRenderer extends GeoItemRenderer<GuardShield> {
    public GuardShieldRenderer() {
        super(new GuardShieldModel());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack,
                             MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        this.animatable = (GuardShield) stack.getItem();
        this.currentItemStack = stack;
        this.renderPerspective = transformType;

        if(transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND){
            boolean isActive = stack.hasTag() && stack.getTag().contains("tbsisActive")&& stack.getTag().getBoolean("tbsisActive");
            if (isActive) {
                poseStack.pushPose();
                poseStack.last().pose().translate(0,0,0.3f);

                poseStack.last().pose().translate(0.3f,-0.3f,-0.6f);

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
                poseStack.last().pose().translate(0.5f,-0.5f,0.5f);

                poseStack.last().pose().translate(0,-0.3f,0.72f);

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

    public static class GuardShieldModel extends GeoModel<GuardShield> {
        private static final ResourceLocation MODEL = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/guard_shield.geo.json");

        @Override
        public ResourceLocation getModelResource(GuardShield animatable) {
            return MODEL;
        }

        @Override
        public ResourceLocation getTextureResource(GuardShield animatable) {
            return ShieldGuardModel.TEXTURE;
        }

        @Override
        public ResourceLocation getAnimationResource(GuardShield animatable) {
            return null;
        }
    }
}
