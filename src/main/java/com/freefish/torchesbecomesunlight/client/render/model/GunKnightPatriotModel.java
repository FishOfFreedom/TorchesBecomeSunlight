package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.entity.ITwoStateEntity;
import com.freefish.torchesbecomesunlight.server.entity.dlc.GunKnightPatriot;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationAct;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class GunKnightPatriotModel extends GeoModel<GunKnightPatriot> {
    @Override
    public void setCustomAnimations(GunKnightPatriot animatable, long instanceId, AnimationState<GunKnightPatriot> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        CoreGeoBone head = this.getAnimationProcessor().getBone("neck");
        CoreGeoBone right_arm_rot = this.getAnimationProcessor().getBone("right_arm_rot");
        EntityModelData extraData = (EntityModelData) animationState.getExtraData().get(DataTickets.ENTITY_MODEL_DATA);

        stateHideGroup(animatable);

        float headPitch= Mth.clamp(extraData.headPitch(),-30,30) * 0.017453292F;
        if(animatable.getAnimation()!=GunKnightPatriot.STATE_2&&animatable.getSpawnState()!=ITwoStateEntity.State.TWO)
            right_arm_rot.setRotX(headPitch);

        head.setRotX(headPitch);
        head.setRotY(extraData.netHeadYaw() * 0.017453292F);
    }

    private void stateHideGroup(GunKnightPatriot patriot){
        AnimationAct animation = patriot.getAnimation();

        ITwoStateEntity.State spawnState = patriot.getSpawnState();

        CoreGeoBone halber = this.getAnimationProcessor().getBone("halber");
        CoreGeoBone halber2 = this.getAnimationProcessor().getBone("halber2");
        halber2.setHidden(animation !=GunKnightPatriot.STATE_2);
        halber.setHidden(animation !=GunKnightPatriot.STATE_2&&spawnState != ITwoStateEntity.State.TWO);

        CoreGeoBone shield2 = this.getAnimationProcessor().getBone("shield2");
        shield2.setHidden(true);
        CoreGeoBone gun = this.getAnimationProcessor().getBone("gun");
        gun.setHidden(spawnState== ITwoStateEntity.State.TWO);
    }

    private static final ResourceLocation MODEL = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/gun_knight_patriot.geo.json");
    public static final ResourceLocation TEXTURE = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/entity/gun_knight_patriot.png");
    public static final ResourceLocation TEXTURE_GLOW = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/entity/gun_knight_patriot_glow.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "animations/patriot_gun_knight.animation.json");

    @Override
    public ResourceLocation getModelResource(GunKnightPatriot animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(GunKnightPatriot animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(GunKnightPatriot animatable) {
        return ANIMATION;
    }
}
