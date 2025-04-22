package com.freefish.torchesbecomesunlight.client.render.projectile;

import com.freefish.torchesbecomesunlight.client.render.model.HalberdOTI2Model;
import com.freefish.torchesbecomesunlight.client.render.model.HalberdOTIModel;
import com.freefish.torchesbecomesunlight.server.entity.projectile.HalberdOTIEntity;
import com.freefish.torchesbecomesunlight.server.entity.projectile.LightingHalberd;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HalberdOTI2Renderer extends GeoEntityRenderer<LightingHalberd> {
    public HalberdOTI2Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HalberdOTI2Model());
    }
}
