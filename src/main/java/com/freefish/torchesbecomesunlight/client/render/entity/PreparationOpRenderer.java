package com.freefish.torchesbecomesunlight.client.render.entity;

import com.freefish.torchesbecomesunlight.client.render.layer.DialogueIconLayer;
import com.freefish.torchesbecomesunlight.client.render.model.PreparationOpModel;
import com.freefish.torchesbecomesunlight.server.entity.rhodesIsland.PreparationOp;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PreparationOpRenderer extends GeoEntityRenderer<PreparationOp> {
    public PreparationOpRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PreparationOpModel());
        addRenderLayer(new DialogueIconLayer<>(this));
    }
}
