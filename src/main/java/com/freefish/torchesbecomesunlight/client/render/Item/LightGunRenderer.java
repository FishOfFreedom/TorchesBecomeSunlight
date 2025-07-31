package com.freefish.torchesbecomesunlight.client.render.Item;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.item.weapon.LightGun;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class LightGunRenderer extends GeoItemRenderer<LightGun> {
    public LightGunRenderer() {
        super(new LightGunModel());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }

    public static class LightGunModel extends GeoModel<LightGun> {
        private static final ResourceLocation MODEL = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/lightgun.geo.json");
        private static final ResourceLocation TEXTURE = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/item/timeandlight.png");

        @Override
        public ResourceLocation getModelResource(LightGun animatable) {
            return MODEL;
        }

        @Override
        public ResourceLocation getTextureResource(LightGun animatable) {
            return TEXTURE;
        }

        @Override
        public ResourceLocation getAnimationResource(LightGun animatable) {
            return null;
        }
    }
}
