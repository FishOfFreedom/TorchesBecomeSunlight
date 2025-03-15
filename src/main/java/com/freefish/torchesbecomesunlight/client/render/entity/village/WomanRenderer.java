package com.freefish.torchesbecomesunlight.client.render.entity.village;

import com.freefish.torchesbecomesunlight.client.render.model.village.WomanModel;
import com.freefish.torchesbecomesunlight.server.entity.villager.FemaleVillager;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class WomanRenderer extends UrsusVillagerRenderer<FemaleVillager> {
    public WomanRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new WomanModel());
    }
}
