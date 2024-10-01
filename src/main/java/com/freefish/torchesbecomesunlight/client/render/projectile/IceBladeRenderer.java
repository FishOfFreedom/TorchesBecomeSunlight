package com.freefish.torchesbecomesunlight.client.render.projectile;

import com.freefish.torchesbecomesunlight.client.render.model.IceBladeModel;
import com.freefish.torchesbecomesunlight.client.render.util.FFRenderTypes;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster.SnowNova;
import com.freefish.torchesbecomesunlight.server.entity.projectile.IceBlade;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class IceBladeRenderer extends GeoEntityRenderer<IceBlade> {
    public IceBladeRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new IceBladeModel());
    }

    @Override
    public void render(IceBlade entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn) {
        stack.pushPose(); // 保存当前矩阵状态
        stack.scale(2f, 2f, 2f);
        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
        stack.popPose();
    }

    @Override
    public RenderType getRenderType(IceBlade animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return FFRenderTypes.getGlowingEffect(texture);
    }
}