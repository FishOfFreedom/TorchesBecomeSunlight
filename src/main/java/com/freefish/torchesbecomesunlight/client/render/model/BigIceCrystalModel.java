package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.entity.projectile.BigIceCrystal;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BigIceCrystalModel extends GeoModel<BigIceCrystal> {
    @Override
    public ResourceLocation getModelResource(BigIceCrystal animatable) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/big_ice_crystal.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BigIceCrystal animatable) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/projectile/big_ice_crystal.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BigIceCrystal animatable) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "animations/big_ice_crystal.animation.json");
    }
}
