package com.freefish.torchesbecomesunlight.client.render.projectile;

import com.freefish.torchesbecomesunlight.client.render.layer.PulsatingGlowLayer;
import com.freefish.torchesbecomesunlight.client.render.layer.TuftGlowLayer;
import com.freefish.torchesbecomesunlight.client.render.model.IceTuftModel;
import com.freefish.torchesbecomesunlight.server.entity.effect.IceTuft;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.shield.Patriot;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class IceTuftRenderer extends GeoEntityRenderer<IceTuft> {
    public IceTuftRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new IceTuftModel());
        addRenderLayer(new TuftGlowLayer(this));
    }
}
