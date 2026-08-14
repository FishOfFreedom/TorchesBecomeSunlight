package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.block.blockentity.BigBenBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class BigBenModel extends GeoModel<BigBenBlockEntity> {
    public static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID,"geo/big_ben.geo.json");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID,"textures/block/big_ben.png");
    public static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID,"animations/big_ben.animation.json");

    @Override
    public ResourceLocation getModelResource(BigBenBlockEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(BigBenBlockEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(BigBenBlockEntity animatable) {
        return ANIMATION;
    }

    @Override
    public void setCustomAnimations(BigBenBlockEntity animatable, long instanceId, AnimationState<BigBenBlockEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        GeoBone body = this.getAnimationProcessor().getBone("rot");
        body.setRotY(-animatable.face * (float)(Math.PI) / 180.0f);
        GeoBone hidr1 = this.getAnimationProcessor().getBone("hidr1");
        hidr1.setRotY(animatable.face * (float)(Math.PI) / 180.0f);
        GeoBone hidr2 = this.getAnimationProcessor().getBone("hidr2");
        hidr2.setRotY(animatable.face * (float)(Math.PI) / 180.0f);
        GeoBone hidr3 = this.getAnimationProcessor().getBone("hidr3");
        hidr3.setRotY(animatable.face * (float)(Math.PI) / 180.0f);
    }
}
