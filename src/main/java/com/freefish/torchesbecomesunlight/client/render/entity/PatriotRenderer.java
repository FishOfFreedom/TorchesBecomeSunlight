package com.freefish.torchesbecomesunlight.client.render.entity;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.layer.DialogueIconLayer;
import com.freefish.torchesbecomesunlight.client.render.layer.PatriotLocateLayer;
import com.freefish.torchesbecomesunlight.client.render.layer.PulsatingGlowLayer;
import com.freefish.torchesbecomesunlight.client.render.model.PatriotModel;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.shield.Patriot;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PatriotRenderer extends GeoEntityRenderer<Patriot> {
    public static ResourceLocation GLOW = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/entity/patriot/halberd.png");

    public PatriotRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PatriotModel());
        addRenderLayer(new DialogueIconLayer(this));
        addRenderLayer(new PatriotLocateLayer(this));
        addRenderLayer(new PulsatingGlowLayer<Patriot>(this, GLOW, 0.1F, 1.0F, 0.25F,Patriot::changeHalberd));
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
