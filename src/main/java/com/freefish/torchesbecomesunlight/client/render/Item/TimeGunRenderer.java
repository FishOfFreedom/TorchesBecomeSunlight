package com.freefish.torchesbecomesunlight.client.render.Item;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.item.weapon.TimeGun;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class TimeGunRenderer extends GeoItemRenderer<TimeGun> {
    public TimeGunRenderer() {
        super(new TimeGunModel());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }

    public static class TimeGunModel extends GeoModel<TimeGun> {
        private static final ResourceLocation MODEL = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/timegun.geo.json");
        private static final ResourceLocation TEXTURE = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/item/timeandlight.png");

        @Override
        public ResourceLocation getModelResource(TimeGun animatable) {
            return MODEL;
        }

        @Override
        public ResourceLocation getTextureResource(TimeGun animatable) {
            return TEXTURE;
        }

        @Override
        public ResourceLocation getAnimationResource(TimeGun animatable) {
            return null;
        }
    }
}
