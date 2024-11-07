package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.entity.effect.BlackHoleEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BlackHoleModel extends GeoModel<BlackHoleEntity> {
    @Override
    public ResourceLocation getModelResource(BlackHoleEntity animatable) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/blackhole.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BlackHoleEntity animatable) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/projectile/blackhole.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BlackHoleEntity animatable) {
        return null;
    }
}