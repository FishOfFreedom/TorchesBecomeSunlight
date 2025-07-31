package com.freefish.torchesbecomesunlight.client.render.Item;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.model.PreparationOpModel;
import com.freefish.torchesbecomesunlight.server.item.weapon.RhodesKnife;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class RhodesKnifeRenderer extends GeoItemRenderer<RhodesKnife> {
    public RhodesKnifeRenderer() {
        super(new RhodesKnifeModel());
    }

    public static class RhodesKnifeModel extends GeoModel<RhodesKnife> {
        private static final ResourceLocation MODEL = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/rhodeknife.geo.json");

        @Override
        public ResourceLocation getModelResource(RhodesKnife animatable) {
            return MODEL;
        }

        @Override
        public ResourceLocation getTextureResource(RhodesKnife animatable) {
            return PreparationOpModel.TEXTURE;
        }

        @Override
        public ResourceLocation getAnimationResource(RhodesKnife animatable) {
            return null;
        }
    }
}
