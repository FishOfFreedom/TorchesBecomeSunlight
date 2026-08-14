package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.entity.demon.Crazelyseon;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class CrazelyseonModel extends GeoModel<Crazelyseon> {

    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "geo/crazelyseon.geo.json");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "textures/entity/crazelyseon/crazelyseon.png");
    //private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "animations/crazelyseon.animation.json");
    private static final ResourceLocation ANIMATION = null;

    @Override
    public ResourceLocation getModelResource(Crazelyseon animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(Crazelyseon animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(Crazelyseon animatable) {
        return ANIMATION;
    }

    @Override
    public void setCustomAnimations(Crazelyseon animatable, long instanceId, AnimationState<Crazelyseon> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        GeoBone head = this.getAnimationProcessor().getBone("fix");
        head.setHidden(true);
    }
}
