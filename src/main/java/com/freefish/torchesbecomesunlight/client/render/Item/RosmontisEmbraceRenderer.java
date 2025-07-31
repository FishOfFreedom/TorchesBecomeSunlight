package com.freefish.torchesbecomesunlight.client.render.Item;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.item.armor.RosmontisEmbraceItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public final class RosmontisEmbraceRenderer extends GeoArmorRenderer<RosmontisEmbraceItem> {
    public RosmontisEmbraceRenderer() {
        super(new DefaultedItemGeoModel<>(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "armor/rosmontis_embrace")));
    }
}
