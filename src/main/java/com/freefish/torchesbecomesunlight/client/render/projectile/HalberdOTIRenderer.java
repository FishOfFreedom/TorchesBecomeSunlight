package com.freefish.torchesbecomesunlight.client.render.projectile;

import com.freefish.torchesbecomesunlight.client.render.model.HalberdOTIModel;
import com.freefish.torchesbecomesunlight.server.entity.projectile.HalberdOTIEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HalberdOTIRenderer extends GeoEntityRenderer<HalberdOTIEntity> {
    public HalberdOTIRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HalberdOTIModel());
    }
}
