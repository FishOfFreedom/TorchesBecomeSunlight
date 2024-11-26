package com.freefish.torchesbecomesunlight.client.render.projectile;

import com.freefish.torchesbecomesunlight.client.render.layer.TuftGlowLayer;
import com.freefish.torchesbecomesunlight.client.render.model.BlackTuftModel;
import com.freefish.torchesbecomesunlight.client.render.model.IceTuftModel;
import com.freefish.torchesbecomesunlight.server.entity.effect.BlackTuft;
import com.freefish.torchesbecomesunlight.server.entity.effect.IceTuft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BlackTuftRenderer extends GeoEntityRenderer<BlackTuft> {
    public BlackTuftRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BlackTuftModel());
    }
}
