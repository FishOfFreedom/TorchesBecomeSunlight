package com.freefish.torchesbecomesunlight.client.render.projectile;

import com.freefish.torchesbecomesunlight.client.render.model.IceCrystalModel;
import com.freefish.torchesbecomesunlight.client.render.model.LightBoomModel;
import com.freefish.torchesbecomesunlight.client.render.util.FFRenderTypes;
import com.freefish.torchesbecomesunlight.server.entity.projectile.IceCrystal;
import com.freefish.torchesbecomesunlight.server.entity.projectile.LightingBoom;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

public class LightBoomRenderer extends GeoEntityRenderer<LightingBoom> {
    public LightBoomRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new LightBoomModel());
    }
}
