package com.freefish.torchesbecomesunlight.client.render.Item;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraft.resources.ResourceLocation;
import software.bernie.example.item.WolfArmorItem;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public final class WinterScratchRenderer extends GeoArmorRenderer<WolfArmorItem> {
    public WinterScratchRenderer() {
        super(new DefaultedItemGeoModel<>(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "armor/winter_scratch")));
    }
}
