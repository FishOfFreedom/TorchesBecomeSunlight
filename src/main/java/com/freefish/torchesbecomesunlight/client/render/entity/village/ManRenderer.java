package com.freefish.torchesbecomesunlight.client.render.entity.village;

import com.freefish.torchesbecomesunlight.client.render.model.village.ManModel;
import com.freefish.torchesbecomesunlight.server.entity.villager.MaleVillager;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class ManRenderer extends UrsusVillagerRenderer<MaleVillager> {

    public ManRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ManModel());
    }

}
