package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.model.village.ManModel;
import com.freefish.torchesbecomesunlight.server.entity.ursus.PatrolCaptain;
import com.freefish.torchesbecomesunlight.server.util.animation.IAnimatedEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class PatrolCaptainModel extends GeoModel<PatrolCaptain> {
    @Override
    public void setCustomAnimations(PatrolCaptain animatable, long instanceId, AnimationState<PatrolCaptain> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        CoreGeoBone head = this.getAnimationProcessor().getBone("AllHead");
        EntityModelData extraData = (EntityModelData) animationState.getExtraData().get(DataTickets.ENTITY_MODEL_DATA);
        if(animatable.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            float pitch = extraData.headPitch() * 0.017453292F;
            float yaw = extraData.netHeadYaw() * 0.017453292F;
            head.setRotX(pitch);
            head.setRotY(yaw);
        }
    }

    @Override
    public ResourceLocation getModelResource(PatrolCaptain animatable) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/patrol_captain.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PatrolCaptain animatable) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/entity/patrol_captain.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PatrolCaptain animatable) {
        return ManModel.ANIMATION;
    }
}
