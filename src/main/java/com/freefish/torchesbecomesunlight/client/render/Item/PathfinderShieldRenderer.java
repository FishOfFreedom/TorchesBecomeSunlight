package com.freefish.torchesbecomesunlight.client.render.Item;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.model.PathfinderBallistariusModel;
import com.freefish.torchesbecomesunlight.client.render.model.tools.MathUtils;
import com.freefish.torchesbecomesunlight.server.item.weapon.PathfinderShield;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class PathfinderShieldRenderer extends GeoItemRenderer<PathfinderShield> {
    public PathfinderShieldRenderer() {
        super(new PathfinderShieldModel());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack,
                             MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        this.animatable = (PathfinderShield) stack.getItem();
        this.currentItemStack = stack;
        this.renderPerspective = transformType;

        if(transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND){
            boolean isActive = stack.hasTag() && stack.getTag().contains("tbsisActive")&& stack.getTag().getBoolean("tbsisActive");
            if (isActive) {
                poseStack.pushPose();
                poseStack.last().pose().translate(0,0,0.3f);

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

    public static class PathfinderShieldModel extends GeoModel<PathfinderShield> {
        private static final ResourceLocation MODEL = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/pathfindershield.geo.json");

        @Override
        public ResourceLocation getModelResource(PathfinderShield animatable) {
            return MODEL;
        }

        @Override
        public ResourceLocation getTextureResource(PathfinderShield animatable) {
            return PathfinderBallistariusModel.TEXTURE;
        }

        @Override
        public ResourceLocation getAnimationResource(PathfinderShield animatable) {
            return null;
        }
    }
}
