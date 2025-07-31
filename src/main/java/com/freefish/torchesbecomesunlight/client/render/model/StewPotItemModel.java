package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.server.item.geoItem.StewPotItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class StewPotItemModel extends GeoModel<StewPotItem> {
    @Override
    public ResourceLocation getModelResource(StewPotItem animatable) {
        return StewPotModel.MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(StewPotItem animatable) {
        return StewPotModel.TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(StewPotItem animatable) {
        return null;
    }
}
