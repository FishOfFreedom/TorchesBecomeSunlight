package com.freefish.torchesbecomesunlight.client.render.entity;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.layer.DialogueIconLayer;
import com.freefish.torchesbecomesunlight.client.render.model.SnowNovaModel;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster.SnowNova;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix4f;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SnowNovaRenderer extends GeoEntityRenderer<SnowNova> {
    public SnowNovaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SnowNovaModel());
        //addRenderLayer(new DialogueIconLayer(this));
    }

    @Override
    protected float getDeathMaxRotation(SnowNova animatable) {
        return 0;
    }

    @Override
    public int getPackedOverlay(SnowNova animatable, float u) {
        return OverlayTexture.pack(OverlayTexture.u(u),
                OverlayTexture.v(animatable.hurtTime > 0));
    }

    @Override
    public void render(SnowNova entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn) {
        stack.pushPose(); // 保存当前矩阵状态
        stack.scale(0.75f, 0.75f, 0.75f);
        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
        stack.popPose();
    }
}
