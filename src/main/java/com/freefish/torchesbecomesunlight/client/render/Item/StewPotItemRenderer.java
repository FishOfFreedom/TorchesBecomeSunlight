package com.freefish.torchesbecomesunlight.client.render.Item;

import com.freefish.torchesbecomesunlight.client.render.model.StewPotItemModel;
import com.freefish.torchesbecomesunlight.server.item.geoItem.StewPotItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class StewPotItemRenderer extends GeoItemRenderer<StewPotItem> {
    public StewPotItemRenderer() {
        super(new StewPotItemModel());
    }
}
