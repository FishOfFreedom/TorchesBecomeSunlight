package com.freefish.torchesbecomesunlight.client.render.Item;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.item.weapon.PhantomGrasp;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class PhantomGraspRenderer extends GeoItemRenderer<PhantomGrasp> {
    public PhantomGraspRenderer() {
        super(new PhantomGraspModel());
    }

    public static class PhantomGraspModel extends GeoModel<PhantomGrasp> {
        private static final ResourceLocation MODEL = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/phantom_grasp.geo.json");
        private static final ResourceLocation TEXTURE = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/item/phantom_grasp.png");

        @Override
        public ResourceLocation getModelResource(PhantomGrasp animatable) {
            return MODEL;
        }

        @Override
        public ResourceLocation getTextureResource(PhantomGrasp animatable) {
            return TEXTURE;
        }

        @Override
        public ResourceLocation getAnimationResource(PhantomGrasp animatable) {
            return null;
        }
    }
}
