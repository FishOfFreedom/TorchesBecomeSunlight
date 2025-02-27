package com.freefish.torchesbecomesunlight.client.render.Item;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.model.GunKnightPatriotModel;
import com.freefish.torchesbecomesunlight.server.item.weapon.Gun;
import com.freefish.torchesbecomesunlight.server.item.weapon.Machete;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class GunRenderer extends GeoItemRenderer<Gun> {
    public GunRenderer() {
        super(new GunModel());
    }

    public static class GunModel extends GeoModel<Gun> {
        private static final ResourceLocation MODEL = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/gun.geo.json");

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
            return null;
        }
    }
}
