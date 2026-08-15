package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.entity.rhodesIsland.rosmontis.RosmontisInstallation;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class RosmontisInstallationModel extends GeoModel<RosmontisInstallation> {
    public static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "geo/rosmontis_installation.geo.json");
    private static final ResourceLocation TEXTURE1 = ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "textures/entity/rosmontis_installation/rosmontis_installation1.png");
    public static final ResourceLocation TEXTURE2 = ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "textures/entity/rosmontis_installation/rosmontis_installation2.png");
    private static final ResourceLocation TEXTURE3 = ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "textures/entity/rosmontis_installation/rosmontis_installation3.png");
    private static final ResourceLocation TEXTURE4 = ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "textures/entity/rosmontis_installation/rosmontis_installation4.png");
    public static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "animations/rosmontis_installation.animation.json");

    @Override
    public ResourceLocation getModelResource(RosmontisInstallation object) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(RosmontisInstallation object) {
        int installIndex = object.getInstallIndex();
        if(installIndex ==0) return TEXTURE1;
        else if(installIndex == 1) return TEXTURE2;
        else if(installIndex == 2) return TEXTURE3;
        else return TEXTURE4;
    }

    @Override
    public ResourceLocation getAnimationResource(RosmontisInstallation animatable) {
        return ANIMATION;
    }

    @Override
    public void setCustomAnimations(RosmontisInstallation animatable, long instanceId, AnimationState<RosmontisInstallation> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        EntityModelData extraData = (EntityModelData) animationState.getExtraData().get(DataTickets.ENTITY_MODEL_DATA);
    }
}
