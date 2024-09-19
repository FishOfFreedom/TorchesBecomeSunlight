package com.freefish.torchesbecomesunlight.client.render.projectile;

import com.freefish.torchesbecomesunlight.client.render.model.IceCrystalModel;
import com.freefish.torchesbecomesunlight.server.entity.projectile.IceCrystal;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class IceCrystalRenderer extends GeoEntityRenderer<IceCrystal> {
    public IceCrystalRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new IceCrystalModel());
    }
}
