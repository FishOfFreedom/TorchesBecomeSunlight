package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.block.blockentity.ElevatorDoorBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ElevatorDoorModel extends GeoModel<ElevatorDoorBlockEntity> {
    public static final ResourceLocation MODEL = new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"geo/elevator_door.geo.json");
    public static final ResourceLocation TEXTURE = new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/block/elevator.png");
    public static final ResourceLocation ANIMATION = new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"animations/elevator_door.animation.json");
    @Override
    public ResourceLocation getModelResource(ElevatorDoorBlockEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(ElevatorDoorBlockEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(ElevatorDoorBlockEntity animatable) {
        return ANIMATION;
    }
}
