package com.freefish.torchesbecomesunlight.client.render.entity;

import com.freefish.torchesbecomesunlight.client.render.layer.VillagerHeadLayer;
import com.freefish.torchesbecomesunlight.client.render.model.ManModel;
import com.freefish.torchesbecomesunlight.server.entity.villager.MaleVillager;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ManRenderer extends GeoEntityRenderer<MaleVillager> {
    public ManRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ManModel());
        addRenderLayer(new VillagerHeadLayer<>(this));
    }
}
