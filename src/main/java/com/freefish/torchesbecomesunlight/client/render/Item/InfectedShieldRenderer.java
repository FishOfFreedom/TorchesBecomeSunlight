package com.freefish.torchesbecomesunlight.client.render.Item;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.model.PatriotModel;
import com.freefish.torchesbecomesunlight.client.render.model.tools.MathUtils;
import com.freefish.torchesbecomesunlight.server.item.weapon.InfectedShield;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

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

        if(transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND){
            boolean isActive = stack.hasTag() && stack.getTag().contains("tbsisActive")&& stack.getTag().getBoolean("tbsisActive");
            if (isActive) {
                poseStack.pushPose();
                poseStack.last().pose().translate(0,0,0.45f);

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
                poseStack.last().pose().translate(0.5f,-0.5f,0.2f);

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

            //RenderType armorRenderType = RenderType.armorCutoutNoCull(PatriotModel.TEXTURE);
            //BakedGeoModel model = this.getGeoModel().getBakedModel(this.getGeoModel().getModelResource(animatable));
            //Optional<GeoBone> leftArm = model.getBone("bone");
            //if(leftArm.isPresent()){
            //    poseStack.pushPose();
            //    poseStack.last().pose().translate(-0.65f,0.5f,0);
            //    GeoBone geoBone = leftArm.get();
            //    renderRecursively(poseStack,animatable,geoBone,armorRenderType,bufferSource,bufferSource.getBuffer(armorRenderType),false,0
            //            ,packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1,1);
            //    poseStack.popPose();
            //}

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
