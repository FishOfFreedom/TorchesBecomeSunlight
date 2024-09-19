package com.freefish.torchesbecomesunlight.client.render.entity;

import com.freefish.torchesbecomesunlight.client.render.model.ManModel;
import com.freefish.torchesbecomesunlight.server.entity.villager.Man;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ManRenderer extends GeoEntityRenderer<Man> {
    public ManRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ManModel());
    }
}
