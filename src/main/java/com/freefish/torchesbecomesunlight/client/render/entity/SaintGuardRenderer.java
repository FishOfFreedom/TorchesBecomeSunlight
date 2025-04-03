package com.freefish.torchesbecomesunlight.client.render.entity;

import com.freefish.torchesbecomesunlight.client.render.layer.DialogueIconLayer;
import com.freefish.torchesbecomesunlight.client.render.model.SaintGuardModel;
import com.freefish.torchesbecomesunlight.server.entity.dlc.SaintGuard;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SaintGuardRenderer extends GeoEntityRenderer<SaintGuard> {
    public SaintGuardRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SaintGuardModel());
        addRenderLayer(new DialogueIconLayer<>(this));
    }

    @Override
    protected float getDeathMaxRotation(SaintGuard animatable) {
        return 0;
    }

    @Override
    public int getPackedOverlay(SaintGuard animatable, float u) {
        return OverlayTexture.pack(OverlayTexture.u(u),
                OverlayTexture.v(animatable.hurtTime > 0));
    }
}
