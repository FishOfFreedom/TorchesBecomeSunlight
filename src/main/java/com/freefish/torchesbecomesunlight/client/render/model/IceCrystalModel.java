package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.entity.projectile.IceCrystal;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

import javax.annotation.Nullable;

public class IceCrystalModel extends GeoModel<IceCrystal> {
    @Override
    public ResourceLocation getModelResource(IceCrystal animatable) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/ice_crystal.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(IceCrystal animatable) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/projectile/ice_crystal.png");
    }

    @Override
    public ResourceLocation getAnimationResource(IceCrystal animatable) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "animations/ice_crystal.animation.json");
    }

    @Override
    public void setCustomAnimations(IceCrystal animatable, long instanceId, AnimationState<IceCrystal> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        CoreGeoBone body = this.getAnimationProcessor().getBone("main");
        body.setRotX((animatable.getXRot() - 90) * (float)(Math.PI) / 180.0f);
        body.setRotY(animatable.getYRot() * (float)(Math.PI) / 180.0f);
    }
}
