package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.entity.projectile.IceCrystal;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class IceCrystalModel extends GeoModel<IceCrystal> {
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "geo/ice_crystal.geo.json");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "textures/projectile/ice_crystal.png");
    private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "animations/ice_crystal.animation.json");
    private static final ResourceLocation MODEL_1 = ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "geo/big_ice_crystal.geo.json");
    private static final ResourceLocation TEXTURE_1 = ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "textures/projectile/big_ice_crystal.png");
    @Override
    public ResourceLocation getModelResource(IceCrystal animatable) {
        int type1 = animatable.getType1();
        if(type1==0||type1==2)
            return MODEL;
        else
            return MODEL_1;
    }

    @Override
    public ResourceLocation getTextureResource(IceCrystal animatable) {
        int type1 = animatable.getType1();
        if(type1==0||type1==2)
            return TEXTURE;
        else
            return TEXTURE_1;
    }

    @Override
    public ResourceLocation getAnimationResource(IceCrystal animatable) {
        return ANIMATION;
    }

    @Override
    public void setCustomAnimations(IceCrystal animatable, long instanceId, AnimationState<IceCrystal> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        GeoBone body = this.getAnimationProcessor().getBone("main");
        body.setRotX((animatable.getXRot() - 90) * (float)(Math.PI) / 180.0f);
        body.setRotY(animatable.getYRot() * (float)(Math.PI) / 180.0f);
    }
}
