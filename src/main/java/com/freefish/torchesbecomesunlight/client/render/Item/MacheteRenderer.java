package com.freefish.torchesbecomesunlight.client.render.Item;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.model.PursuerModel;
import com.freefish.torchesbecomesunlight.server.item.weapon.Machete;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class MacheteRenderer extends GeoItemRenderer<Machete> {
    public MacheteRenderer() {
        super(new MacheteModel());
    }

    public static class MacheteModel extends GeoModel<Machete> {
        private static final ResourceLocation MODEL = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/machete.geo.json");

        @Override
        public ResourceLocation getModelResource(Machete animatable) {
            return MODEL;
        }

        @Override
        public ResourceLocation getTextureResource(Machete animatable) {
            return PursuerModel.TEXTURE;
        }

        @Override
        public ResourceLocation getAnimationResource(Machete animatable) {
            return null;
        }
    }
}
