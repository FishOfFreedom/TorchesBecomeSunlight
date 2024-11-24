package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.entity.projectile.IceBlade;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class IceBladeModel extends GeoModel<IceBlade> {
    @Override
    public ResourceLocation getModelResource(IceBlade animatable) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/ice_blade.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(IceBlade animatable) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/projectile/ice_blade.png");
    }

    @Override
    public ResourceLocation getAnimationResource(IceBlade animatable) {
        return null;
    }

    @Override
    public void setCustomAnimations(IceBlade animatable, long instanceId, AnimationState<IceBlade> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        CoreGeoBone body = this.getAnimationProcessor().getBone("main");
        body.setRotX((animatable.getXRot() - 90) * (float)(Math.PI) / 180.0f);
        body.setRotY(animatable.getYRot() * (float)(Math.PI) / 180.0f);
    }
}
