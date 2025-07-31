package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.server.entity.rhodesIsland.rosmontis.RosmontisLivingInstallation;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RosmontisLivingInstallationModel extends GeoModel<RosmontisLivingInstallation> {
    @Override
    public ResourceLocation getModelResource(RosmontisLivingInstallation object) {
        return RosmontisInstallationModel.MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(RosmontisLivingInstallation object) {
        return RosmontisInstallationModel.TEXTURE2;
    }

    @Override
    public ResourceLocation getAnimationResource(RosmontisLivingInstallation animatable) {
        return RosmontisInstallationModel.ANIMATION;
    }
}
