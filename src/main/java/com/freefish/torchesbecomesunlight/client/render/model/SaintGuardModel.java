package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.entity.dlc.SaintGuard;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SaintGuardModel extends GeoModel<SaintGuard> {
    private static final ResourceLocation MODEL =  new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/saint_guard.geo.json");
    public static final ResourceLocation TEXTURE =  new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/entity/saint_guard.png");

    @Override
    public ResourceLocation getModelResource(SaintGuard object) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(SaintGuard object) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(SaintGuard animatable) {
        return null;
    }
}
