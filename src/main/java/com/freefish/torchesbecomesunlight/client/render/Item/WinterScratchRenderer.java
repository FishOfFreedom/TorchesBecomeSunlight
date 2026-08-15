package com.freefish.torchesbecomesunlight.client.render.Item;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.AnimalArmorItem;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public final class WinterScratchRenderer extends GeoArmorRenderer<AnimalArmorItem> {
    public WinterScratchRenderer() {
        super(new DefaultedItemGeoModel<>(ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "armor/winter_scratch")));
    }
}
