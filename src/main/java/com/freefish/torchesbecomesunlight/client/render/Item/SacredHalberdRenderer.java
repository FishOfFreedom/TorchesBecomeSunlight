package com.freefish.torchesbecomesunlight.client.render.Item;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.model.GunKnightPatriotModel;
import com.freefish.torchesbecomesunlight.server.item.weapon.SacredHalberd;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class SacredHalberdRenderer extends GeoItemRenderer<SacredHalberd> {
    public SacredHalberdRenderer() {
        super(new SacredHalberdModel());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        //if(transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND||transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND){
        //    boolean isActive = stack.hasTag() && stack.getTag().contains("tbsisActive")&& stack.getTag().getBoolean("tbsisActive");
        //    if (isActive) {
        //        poseStack.pushPose();
        //        poseStack.scale(1, -1, 1);
        //        super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
        //        poseStack.popPose();
        //    }else {
        //        super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
        //    }
        //}
        //else
            super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }

    public static class SacredHalberdModel extends GeoModel<SacredHalberd> {
        private static final ResourceLocation MODEL = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/sacred_halberd.geo.json");

        @Override
        public ResourceLocation getModelResource(SacredHalberd animatable) {
            return MODEL;
        }

        @Override
        public ResourceLocation getTextureResource(SacredHalberd animatable) {
            return GunKnightPatriotModel.TEXTURE;
        }

        @Override
        public ResourceLocation getAnimationResource(SacredHalberd animatable) {
            return null;
        }
    }
}
