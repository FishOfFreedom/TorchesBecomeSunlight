package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.server.entity.AnimatedEntity;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.EasingType;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class RotMainGeoModel<T extends AnimatedEntity> extends GeoModel<T> {
    @Override
    public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        EntityModelData extraData = (EntityModelData) animationState.getExtraData().get(DataTickets.ENTITY_MODEL_DATA);

        CoreGeoBone head = getNeckBone();
        float headPitch= Mth.clamp(extraData.headPitch(),-30,30) * 0.017453292F;
        head.setRotX(headPitch);
        head.setRotY(extraData.netHeadYaw() * 0.017453292F);
    }

    public CoreGeoBone getMainBone(){
        return this.getAnimationProcessor().getBone("main");
    }

    public CoreGeoBone getNeckBone(){
        return this.getAnimationProcessor().getBone("neck");
    }
}
