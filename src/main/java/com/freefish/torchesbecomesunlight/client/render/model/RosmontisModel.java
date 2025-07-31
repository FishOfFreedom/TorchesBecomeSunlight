package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.entity.rhodesIsland.rosmontis.Rosmontis;
import com.freefish.torchesbecomesunlight.server.util.animation.IAnimatedEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class RosmontisModel extends GeoModel<Rosmontis> {
    private static final ResourceLocation MODEL =  new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/rosmontis.geo.json");
    public static final ResourceLocation TEXTURE =  new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/entity/rosmontis.png");
    private static final ResourceLocation ANIMATION =  new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "animations/rosmontis.animation.json");


    @Override
    public ResourceLocation getModelResource(Rosmontis object) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(Rosmontis object) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(Rosmontis animatable) {
        return ANIMATION;
    }

    @Override
    public void setCustomAnimations(Rosmontis animatable, long instanceId, AnimationState<Rosmontis> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        CoreGeoBone head = this.getAnimationProcessor().getBone("Head");
        EntityModelData extraData = (EntityModelData) animationState.getExtraData().get(DataTickets.ENTITY_MODEL_DATA);
        if(animatable.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            head.setRotX(extraData.headPitch() * 0.017453292F);
            head.setRotY(extraData.netHeadYaw() * 0.017453292F);
        }
    }
}
