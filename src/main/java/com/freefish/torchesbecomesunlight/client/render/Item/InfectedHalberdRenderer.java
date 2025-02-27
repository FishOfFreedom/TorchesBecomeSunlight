package com.freefish.torchesbecomesunlight.client.render.Item;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.model.GunKnightPatriotModel;
import com.freefish.torchesbecomesunlight.client.render.model.PatriotModel;
import com.freefish.torchesbecomesunlight.server.item.weapon.Gun;
import com.freefish.torchesbecomesunlight.server.item.weapon.InfectedHalberd;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class InfectedHalberdRenderer extends GeoItemRenderer<InfectedHalberd> {
    public InfectedHalberdRenderer() {
        super(new InfectedHalberdModel());
    }

    public static class InfectedHalberdModel extends GeoModel<InfectedHalberd> {
        private static final ResourceLocation MODEL = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/halberd.geo.json");

        @Override
        public ResourceLocation getModelResource(InfectedHalberd animatable) {
            return MODEL;
        }

        @Override
        public ResourceLocation getTextureResource(InfectedHalberd animatable) {
            return PatriotModel.TEXTURE;
        }

        @Override
        public ResourceLocation getAnimationResource(InfectedHalberd animatable) {
            return null;
        }
    }
}
