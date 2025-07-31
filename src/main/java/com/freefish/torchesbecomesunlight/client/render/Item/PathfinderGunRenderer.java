package com.freefish.torchesbecomesunlight.client.render.Item;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.model.PathfinderBallistariusModel;
import com.freefish.torchesbecomesunlight.server.item.weapon.PathfinderGun;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class PathfinderGunRenderer extends GeoItemRenderer<PathfinderGun> {
    public PathfinderGunRenderer() {
        super(new PathfinderGunModel());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }

    public static class PathfinderGunModel extends GeoModel<PathfinderGun> {
        private static final ResourceLocation MODEL = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/pathfindergun.geo.json");
        private static final ResourceLocation ANIMATION = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "animations/pathfindergun.animation.json");

        @Override
        public ResourceLocation getModelResource(PathfinderGun animatable) {
            return MODEL;
        }

        @Override
        public ResourceLocation getTextureResource(PathfinderGun animatable) {
            return PathfinderBallistariusModel.TEXTURE;
        }

        @Override
        public ResourceLocation getAnimationResource(PathfinderGun animatable) {
            return ANIMATION;
        }
    }
}
