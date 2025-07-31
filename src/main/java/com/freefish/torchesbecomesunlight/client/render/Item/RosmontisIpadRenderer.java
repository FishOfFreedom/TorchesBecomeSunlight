package com.freefish.torchesbecomesunlight.client.render.Item;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.model.RosmontisModel;
import com.freefish.torchesbecomesunlight.server.item.weapon.RosmontisIpad;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class RosmontisIpadRenderer extends GeoItemRenderer<RosmontisIpad> {
    public RosmontisIpadRenderer() {
        super(new RosmontisIpadModel());
    }


    public static class RosmontisIpadModel extends GeoModel<RosmontisIpad> {
        private static final ResourceLocation MODEL = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/rosmontisipad.geo.json");

        @Override
        public ResourceLocation getModelResource(RosmontisIpad animatable) {
            return MODEL;
        }

        @Override
        public ResourceLocation getTextureResource(RosmontisIpad animatable) {
            return RosmontisModel.TEXTURE;
        }

        @Override
        public ResourceLocation getAnimationResource(RosmontisIpad animatable) {
            return null;
        }
    }
}
