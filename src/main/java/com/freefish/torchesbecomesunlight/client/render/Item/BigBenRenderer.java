package com.freefish.torchesbecomesunlight.client.render.Item;

import com.freefish.torchesbecomesunlight.client.render.model.BigBenItemModel;
import com.freefish.torchesbecomesunlight.server.item.geoItem.BigBenItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class BigBenRenderer extends GeoItemRenderer<BigBenItem> {
    public BigBenRenderer() {
        super(new BigBenItemModel());
    }
}
