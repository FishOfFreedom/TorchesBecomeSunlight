package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.util.animation.IAnimatedEntity;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.shield.Patriot;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class PatriotModel extends GeoModel<Patriot> {
    @Override
    public void setCustomAnimations(Patriot animatable, long instanceId, AnimationState<Patriot> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        CoreGeoBone swordLocate1 = this.getAnimationProcessor().getBone("halberd_lock");
        CoreGeoBone swordLocate2 = this.getAnimationProcessor().getBone("halberd_lock2");
        Vec3 vec31 = MathUtils.getWorldPosFromModel(animatable, animatable.yBodyRot, (GeoBone) swordLocate1);
        Vec3 vec32 = MathUtils.getWorldPosFromModel(animatable, animatable.yBodyRot, (GeoBone) swordLocate2);
        animatable.updateTrail(vec31, vec32);
        
        CoreGeoBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) animationState.getExtraData().get(DataTickets.ENTITY_MODEL_DATA);
        if(animatable.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            head.setRotX(extraData.headPitch() * 0.017453292F);
            head.setRotY(extraData.netHeadYaw() * 0.017453292F);
        }

    }

    private static final ResourceLocation MODEL = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/patriot.geo.json");
    public static final ResourceLocation TEXTURE = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/entity/patriot/patriot.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "animations/patriot.animation.json");

    @Override
    public ResourceLocation getModelResource(Patriot animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(Patriot animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(Patriot animatable) {
        return ANIMATION;
    }
}
