package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.entity.animal.Burdenbeast;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class BurdenbeastModel extends GeoModel<Burdenbeast> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/entity/burdenbeast.png");
    private static final ResourceLocation MODEL = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/burdenbeast.geo.json");
    private static final ResourceLocation ANIMATION = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "animations/burdenbeask.animation.json");

    @Override
    public void setCustomAnimations(Burdenbeast animatable, long instanceId, AnimationState<Burdenbeast> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        CoreGeoBone head = this.getAnimationProcessor().getBone("head");
        CoreGeoBone armor = this.getAnimationProcessor().getBone("armor");
        armor.setHidden(!animatable.isSaddled());
        EntityModelData extraData = (EntityModelData) animationState.getExtraData().get(DataTickets.ENTITY_MODEL_DATA);
        float pitch = extraData.headPitch() * 0.017453292F;
        float yaw = extraData.netHeadYaw() * 0.017453292F;
        head.setRotX(pitch/2);
        head.setRotY(yaw/2);
    }

    @Override
    public ResourceLocation getModelResource(Burdenbeast animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(Burdenbeast animatable) {
            return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(Burdenbeast animatable) {
        return ANIMATION;
    }
}