package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.entity.projectile.IceCrystal;
import com.freefish.torchesbecomesunlight.server.entity.projectile.LightingBoom;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class LightBoomModel extends GeoModel<LightingBoom> {
    private static final ResourceLocation MODEL = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/ice_crystal.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/projectile/ice_crystal.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "animations/ice_crystal.animation.json");
    @Override
    public ResourceLocation getModelResource(LightingBoom animatable) {
            return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(LightingBoom animatable) {
            return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(LightingBoom animatable) {
        return ANIMATION;
    }

    @Override
    public void setCustomAnimations(LightingBoom animatable, long instanceId, AnimationState<LightingBoom> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        CoreGeoBone body = this.getAnimationProcessor().getBone("main");
        body.setRotX((animatable.getXRot() - 90) * (float)(Math.PI) / 180.0f);
        body.setRotY(animatable.getYRot() * (float)(Math.PI) / 180.0f);
    }
}
