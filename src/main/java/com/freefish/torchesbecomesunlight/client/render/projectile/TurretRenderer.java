package com.freefish.torchesbecomesunlight.client.render.projectile;

import com.freefish.torchesbecomesunlight.client.render.model.TurretModel;
import com.freefish.torchesbecomesunlight.server.entity.dlc.Turret;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TurretRenderer extends GeoEntityRenderer<Turret> {
    public TurretRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TurretModel());
    }

    @Override
    protected float getDeathMaxRotation(Turret animatable) {
        return 0;
    }

    @Override
    public int getPackedOverlay(Turret animatable, float u) {
        return OverlayTexture.NO_OVERLAY;
    }

    protected void applyRotations(Turret animatable, PoseStack poseStack, float ageInTicks, float rotationYaw,
                                  float partialTick) {
    }
}
