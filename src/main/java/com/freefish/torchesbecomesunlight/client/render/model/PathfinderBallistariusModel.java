package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.entity.dlc.PathfinderBallistarius;
import com.freefish.torchesbecomesunlight.server.util.animation.IAnimatedEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class PathfinderBallistariusModel extends GeoModel<PathfinderBallistarius> {
    private static final ResourceLocation MODEL =  new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/pathfinder_ballistarius.geo.json");
    public static final ResourceLocation TEXTURE = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/entity/pathfinder_ballistarius.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "animations/pathfinder_ballistarius.animation.json");

    @Override
    public void setCustomAnimations(PathfinderBallistarius animatable, long instanceId, AnimationState<PathfinderBallistarius> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        CoreGeoBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) animationState.getExtraData().get(DataTickets.ENTITY_MODEL_DATA);
        if(animatable.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            head.setRotX(extraData.headPitch() * 0.017453292F);
            head.setRotY(extraData.netHeadYaw() * 0.017453292F);
        }
    }

    @Override
    public ResourceLocation getModelResource(PathfinderBallistarius animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(PathfinderBallistarius animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(PathfinderBallistarius animatable) {
        return ANIMATION;
    }
}
