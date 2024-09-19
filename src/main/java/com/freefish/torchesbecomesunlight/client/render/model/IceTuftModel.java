package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.entity.effect.IceTuft;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class IceTuftModel extends GeoModel<IceTuft> {
    @Override
    public ResourceLocation getModelResource(IceTuft animatable) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/ice_tuft.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(IceTuft animatable) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/projectile/ice_tuft.png");
    }

    @Override
    public ResourceLocation getAnimationResource(IceTuft animatable) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "animations/ice_tuft.animation.json");
    }
}
