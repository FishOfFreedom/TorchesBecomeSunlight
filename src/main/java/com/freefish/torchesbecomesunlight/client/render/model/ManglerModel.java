package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.entity.animal.Mangler;
import com.freefish.torchesbecomesunlight.server.util.animation.IAnimatedEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class ManglerModel extends GeoModel<Mangler> {
    @Override
    public void setCustomAnimations(Mangler animatable, long instanceId, AnimationState<Mangler> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        CoreGeoBone head = this.getAnimationProcessor().getBone("head");
        CoreGeoBone neck = this.getAnimationProcessor().getBone("neck");
        EntityModelData extraData = (EntityModelData) animationState.getExtraData().get(DataTickets.ENTITY_MODEL_DATA);
        if(animatable.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            float pitch = extraData.headPitch() * 0.017453292F;
            float yaw = extraData.netHeadYaw() * 0.017453292F;
            head.setRotX(pitch/2);
            head.setRotY(yaw/2);
            neck.setRotX(pitch/3);
            neck.setRotY(yaw/3);
        }
    }

    @Override
    public ResourceLocation getModelResource(Mangler animatable) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/mangler.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Mangler animatable) {
        if(true)
            return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/entity/mangler_0.png");
        else
            return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/entity/mangler_1.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Mangler animatable) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "animations/mangler.animation.json");
    }
}
