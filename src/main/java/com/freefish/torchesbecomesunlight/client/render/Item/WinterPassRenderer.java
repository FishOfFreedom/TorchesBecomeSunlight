package com.freefish.torchesbecomesunlight.client.render.Item;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.model.SnowNovaModel;
import com.freefish.torchesbecomesunlight.server.item.weapon.WinterPass;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class WinterPassRenderer extends GeoItemRenderer<WinterPass> {
    public WinterPassRenderer() {
        super(new WinterPassModel());
    }

    public static class WinterPassModel extends GeoModel<WinterPass> {
        private static final ResourceLocation MODEL = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/winter_pass.geo.json");

        @Override
        public ResourceLocation getModelResource(WinterPass animatable) {
            return MODEL;
        }

        @Override
        public ResourceLocation getTextureResource(WinterPass animatable) {
            return SnowNovaModel.TEXTURE;
        }

        @Override
        public ResourceLocation getAnimationResource(WinterPass animatable) {
            return null;
        }
    }
}
