package com.freefish.torchesbecomesunlight.client.render.projectile;

import com.freefish.torchesbecomesunlight.client.render.model.BlackSpearModel;
import com.freefish.torchesbecomesunlight.server.entity.projectile.BlackSpear;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BlackSpearRenderer extends GeoEntityRenderer<BlackSpear> {
    public BlackSpearRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BlackSpearModel());
    }
}

