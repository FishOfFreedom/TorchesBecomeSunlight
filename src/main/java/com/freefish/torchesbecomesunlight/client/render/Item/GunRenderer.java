package com.freefish.torchesbecomesunlight.client.render.Item;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.model.GunKnightPatriotModel;
import com.freefish.torchesbecomesunlight.server.item.weapon.Gun;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class GunRenderer extends GeoItemRenderer<Gun> {
    public GunRenderer() {
        super(new GunModel());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }

    public static class GunModel extends GeoModel<Gun> {
        private static final ResourceLocation MODEL = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/gun.geo.json");
        private static final ResourceLocation ANIMATION = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "animations/gun.animation.json");

        @Override
        public ResourceLocation getModelResource(Gun animatable) {
            return MODEL;
        }

        @Override
        public ResourceLocation getTextureResource(Gun animatable) {
            return GunKnightPatriotModel.TEXTURE;
        }

        @Override
        public ResourceLocation getAnimationResource(Gun animatable) {
            return ANIMATION;
        }
    }
}
