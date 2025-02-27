package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.entity.villager.MaleVillager;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class ManModel extends GeoModel<MaleVillager> {
    private static final ResourceLocation MODEL = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/male_villager.geo.json");
    public static final ResourceLocation TEXTURE_1 = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/entity/villager/male_1.png");
    public static final ResourceLocation TEXTURE_2 = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/entity/villager/male_2.png");
    public static final ResourceLocation TEXTURE_3 = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/entity/villager/male_3.png");
    public static final ResourceLocation TEXTUREHEAD_1 = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/entity/villager/malehead_1.png");
    public static final ResourceLocation TEXTUREHEAD_2 = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/entity/villager/malehead_2.png");
    public static final ResourceLocation TEXTUREHEAD_3 = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/entity/villager/malehead_3.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "animations/male_villager.animation.json");

    @Override
    public ResourceLocation getModelResource(MaleVillager animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(MaleVillager animatable) {
        int body = animatable.getBody();
        if(body==0)
            return TEXTURE_1;
        else if(body==1)
            return TEXTURE_2;
        else
            return TEXTURE_3;
    }

    @Override
    public ResourceLocation getAnimationResource(MaleVillager animatable) {
        return ANIMATION;
    }

    @Override
    public void setCustomAnimations(MaleVillager animatable, long instanceId, AnimationState<MaleVillager> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        CoreGeoBone head = this.getAnimationProcessor().getBone("Head");
        EntityModelData extraData = (EntityModelData) animationState.getExtraData().get(DataTickets.ENTITY_MODEL_DATA);

        float headPitch = extraData.headPitch();
        float headYaw = extraData.netHeadYaw();


        head.setRotX(headPitch * 0.017453292F);
        head.setRotY(headYaw * 0.017453292F);

        CoreGeoBone rightEye = this.getAnimationProcessor().getBone("RightEyelidBase");
        CoreGeoBone leftEye = this.getAnimationProcessor().getBone("LeftEyelidBase");

        rightEye.setPosX(headYaw * 0.017453292F);
        leftEye.setPosX(headYaw * 0.017453292F);
    }

    @Override
    public void applyMolangQueries(MaleVillager animatable, double animTime) {
        super.applyMolangQueries(animatable, animTime);
    }
}
