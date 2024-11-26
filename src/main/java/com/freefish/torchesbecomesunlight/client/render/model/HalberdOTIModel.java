package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.entity.projectile.HalberdOTIEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class HalberdOTIModel extends GeoModel<HalberdOTIEntity> {
    @Override
    public void setCustomAnimations(HalberdOTIEntity animatable, long instanceId, AnimationState<HalberdOTIEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        CoreGeoBone body = this.getAnimationProcessor().getBone("halber");
        body.setRotX((animatable.getXRot() - 90) * (float)(Math.PI) / 180.0f);
        body.setRotY(animatable.getYRot() * (float)(Math.PI) / 180.0f);
    }

    @Override
    public ResourceLocation getModelResource(HalberdOTIEntity animatable) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/halberd_of_the_infected.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HalberdOTIEntity animatable) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/projectile/halberd_of_the_infected.png");
    }

    @Override
    public ResourceLocation getAnimationResource(HalberdOTIEntity animatable) {
        return null;
    }
}
