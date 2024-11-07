package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.entity.projectile.BlackSpear;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class BlackSpearModel extends GeoModel<BlackSpear> {
    @Override
    public ResourceLocation getModelResource(BlackSpear animatable) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/black_spear.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BlackSpear animatable) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/projectile/black_spear.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BlackSpear animatable) {
        return null;
    }

    @Override
    public void setCustomAnimations(BlackSpear animatable, long instanceId, AnimationState<BlackSpear> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        CoreGeoBone body = this.getAnimationProcessor().getBone("main");
        body.setRotX((animatable.getXRot() - 90) * (float)(Math.PI) / 180.0f);
        body.setRotY(animatable.getYRot() * (float)(Math.PI) / 180.0f);
    }
}