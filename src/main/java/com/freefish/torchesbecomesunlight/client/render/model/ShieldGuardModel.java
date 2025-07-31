package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.shield.ShieldGuard;
import com.freefish.torchesbecomesunlight.server.util.animation.IAnimatedEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class ShieldGuardModel extends GeoModel<ShieldGuard> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/entity/shield_guard.png");


    @Override
    public void setCustomAnimations(ShieldGuard animatable, long instanceId, AnimationState<ShieldGuard> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        CoreGeoBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) animationState.getExtraData().get(DataTickets.ENTITY_MODEL_DATA);
        if(animatable.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            head.setRotX(extraData.headPitch() * 0.017453292F);
            head.setRotY(extraData.netHeadYaw() * 0.017453292F);
        }
    }

    @Override
    public ResourceLocation getModelResource(ShieldGuard animatable) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/shield_guard.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ShieldGuard animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(ShieldGuard animatable) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "animations/shield_guard.animation.json");
    }
}
