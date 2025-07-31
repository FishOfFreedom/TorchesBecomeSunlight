package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.entity.demon.Crazelyseon;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class CrazelyseonModel extends GeoModel<Crazelyseon> {

    private static final ResourceLocation MODEL = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/crazelyseon.geo.json");
    public static final ResourceLocation TEXTURE = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/entity/crazelyseon/crazelyseon.png");
    //private static final ResourceLocation ANIMATION = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "animations/crazelyseon.animation.json");
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

        CoreGeoBone head = this.getAnimationProcessor().getBone("fix");
        head.setHidden(true);
    }
}
