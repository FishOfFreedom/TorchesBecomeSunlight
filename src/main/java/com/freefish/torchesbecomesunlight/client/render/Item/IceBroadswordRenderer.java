package com.freefish.torchesbecomesunlight.client.render.Item;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.model.YetiIcecleaverModel;
import com.freefish.torchesbecomesunlight.server.item.weapon.IceBroadsword;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class IceBroadswordRenderer extends GeoItemRenderer<IceBroadsword> {
    public IceBroadswordRenderer() {
        super(new IceBroadswordModel());
    }

    public static class IceBroadswordModel extends GeoModel<IceBroadsword> {
        private static final ResourceLocation MODEL = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/ice_broadsword.geo.json");

        @Override
        public ResourceLocation getModelResource(IceBroadsword animatable) {
            return MODEL;
        }

        @Override
        public ResourceLocation getTextureResource(IceBroadsword animatable) {
            return YetiIcecleaverModel.TEXTURE;
        }

        @Override
        public ResourceLocation getAnimationResource(IceBroadsword animatable) {
            return null;
        }
    }
}
