package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.entity.effect.BlackTuft;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class BlackTuftModel extends GeoModel<BlackTuft> {
    @Override
    public ResourceLocation getModelResource(BlackTuft animatable) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/black_spear.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BlackTuft animatable) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/projectile/black_spear.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BlackTuft animatable) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "animations/black_spear.animation.json");
    }

    @Override
    public void setCustomAnimations(BlackTuft animatable, long instanceId, AnimationState<BlackTuft> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        CoreGeoBone body = this.getAnimationProcessor().getBone("main");
        body.setRotX((animatable.getXRot()) * (float)(Math.PI) / 180.0f);
        body.setRotY(animatable.getYRot() * (float)(Math.PI) / 180.0f);
    }
}