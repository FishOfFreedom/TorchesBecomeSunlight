package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.entity.projectile.LightingHalberd;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class HalberdOTI2Model extends GeoModel<LightingHalberd> {
    @Override
    public void setCustomAnimations(LightingHalberd animatable, long instanceId, AnimationState<LightingHalberd> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        CoreGeoBone body = this.getAnimationProcessor().getBone("balber");
        body.setRotX((animatable.getXRot() - 90) * (float)(Math.PI) / 180.0f);
        body.setRotY( animatable.getYRot() * (float)(Math.PI) / 180.0f);
    }

    @Override
    public ResourceLocation getModelResource(LightingHalberd animatable) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/halberd_of_the_infected2.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LightingHalberd animatable) {
        return GunKnightPatriotModel.TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(LightingHalberd animatable) {
        return null;
    }
}
