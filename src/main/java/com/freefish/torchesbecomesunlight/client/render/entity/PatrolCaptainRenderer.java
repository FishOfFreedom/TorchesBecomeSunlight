package com.freefish.torchesbecomesunlight.client.render.entity;

import com.freefish.torchesbecomesunlight.client.render.layer.ItemInHandLayer;
import com.freefish.torchesbecomesunlight.client.render.model.PatrolCaptainModel;
import com.freefish.torchesbecomesunlight.server.entity.ursus.PatrolCaptain;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PatrolCaptainRenderer extends GeoEntityRenderer<PatrolCaptain> {
    public PatrolCaptainRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PatrolCaptainModel());
        addRenderLayer(new ItemInHandLayer(this));
    }
}
