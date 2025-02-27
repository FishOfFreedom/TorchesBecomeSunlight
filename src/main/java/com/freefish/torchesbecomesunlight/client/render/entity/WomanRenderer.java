package com.freefish.torchesbecomesunlight.client.render.entity;

import com.freefish.torchesbecomesunlight.client.render.layer.VillagerHeadLayer;
import com.freefish.torchesbecomesunlight.client.render.model.WomanModel;
import com.freefish.torchesbecomesunlight.server.entity.villager.FemaleVillager;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WomanRenderer extends GeoEntityRenderer<FemaleVillager> {
    public WomanRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new WomanModel());
        addRenderLayer(new VillagerHeadLayer<>(this));
    }
}
