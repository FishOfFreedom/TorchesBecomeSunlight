package com.freefish.torchesbecomesunlight.client.render.projectile;

import com.freefish.torchesbecomesunlight.client.render.model.BigIceCrystalModel;
import com.freefish.torchesbecomesunlight.server.entity.projectile.BigIceCrystal;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BigIceCrystalRenderer extends GeoEntityRenderer<BigIceCrystal> {
    public BigIceCrystalRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BigIceCrystalModel());
    }
}
