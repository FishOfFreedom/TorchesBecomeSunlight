package com.freefish.torchesbecomesunlight.client.render.entity;

import com.freefish.torchesbecomesunlight.client.render.layer.DialogueIconLayer;
import com.freefish.torchesbecomesunlight.client.render.model.PatriotModel;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.shield.Patriot;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PatriotRenderer extends GeoEntityRenderer<Patriot> {
    public PatriotRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PatriotModel());
        addRenderLayer(new DialogueIconLayer(this));
    }

    @Override
    protected float getDeathMaxRotation(Patriot animatable) {
        return 0;
    }

    @Override
    public int getPackedOverlay(Patriot animatable, float u) {
        return OverlayTexture.pack(OverlayTexture.u(u),
                OverlayTexture.v(animatable.hurtTime > 0));
    }
}
