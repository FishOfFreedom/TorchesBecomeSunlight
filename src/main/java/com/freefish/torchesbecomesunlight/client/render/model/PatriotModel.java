package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.animation.IAnimatedEntity;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.shield.Patriot;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class PatriotModel extends GeoModel<Patriot> {
    @Override
    public void setCustomAnimations(Patriot animatable, long instanceId, AnimationState<Patriot> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        CoreGeoBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) animationState.getExtraData().get(DataTickets.ENTITY_MODEL_DATA);
        if(animatable.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            head.setRotX(extraData.headPitch() * 0.017453292F);
            head.setRotY(extraData.netHeadYaw() * 0.017453292F);
        }
    }

    @Override
    public ResourceLocation getModelResource(Patriot animatable) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/patriot.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Patriot animatable) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/entity/patriot/patriot.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Patriot animatable) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "animations/patriot.animation.json");
    }
}
