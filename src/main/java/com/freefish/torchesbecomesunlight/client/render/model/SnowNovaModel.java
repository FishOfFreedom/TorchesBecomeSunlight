package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.util.animation.IAnimatedEntity;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster.FrostNova;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class SnowNovaModel extends GeoModel<FrostNova> {
    @Override
    public ResourceLocation getModelResource(FrostNova object) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/snownova.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(FrostNova object) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/entity/snownova.png");
    }

    @Override
    public ResourceLocation getAnimationResource(FrostNova animatable) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "animations/snownova.animation.json");
    }

    @Override
    public void setCustomAnimations(FrostNova animatable, long instanceId, AnimationState<FrostNova> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        CoreGeoBone head = this.getAnimationProcessor().getBone("Head");
        EntityModelData extraData = (EntityModelData) animationState.getExtraData().get(DataTickets.ENTITY_MODEL_DATA);
        if(animatable.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            head.setRotX(extraData.headPitch() * 0.017453292F);
            head.setRotY(extraData.netHeadYaw() * 0.017453292F);
        }
    }
}
