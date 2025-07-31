package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.server.item.geoItem.BigBenItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BigBenItemModel extends GeoModel<BigBenItem> {
    @Override
    public ResourceLocation getModelResource(BigBenItem animatable) {
        return BigBenModel.MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(BigBenItem animatable) {
        return BigBenModel.TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(BigBenItem animatable) {
        return null;
    }
}
