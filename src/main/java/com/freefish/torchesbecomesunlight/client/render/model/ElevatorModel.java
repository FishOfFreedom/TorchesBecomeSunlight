package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.block.blockentity.ElevatorBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ElevatorModel extends GeoModel<ElevatorBlockEntity> {
    public static final ResourceLocation MODEL = new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"geo/elevator.geo.json");
    public static final ResourceLocation TEXTURE = new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/block/elevator1.png");
    public static final ResourceLocation ANIMATION = new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"animations/elevator.animation.json");
    @Override
    public ResourceLocation getModelResource(ElevatorBlockEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(ElevatorBlockEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(ElevatorBlockEntity animatable) {
        return ANIMATION;
    }
}
