package com.freefish.torchesbecomesunlight.client.render.entity;

import com.freefish.torchesbecomesunlight.client.render.model.tools.MathUtils;
import com.freefish.torchesbecomesunlight.server.entity.effect.EntityMultiBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public class RenderMultiBlock <T extends EntityMultiBlock> extends EntityRenderer<T> {
    public RenderMultiBlock(EntityRendererProvider.Context mgr) {
        super(mgr);
    }

    @Override
    public void render(T entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        matrixStackIn.pushPose();
        matrixStackIn.translate(0, 0.5f*entityIn.getOffset()-0.5f, 0);

        matrixStackIn.mulPose(MathUtils.quatFromRotationXYZ(0, -Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()), 0, true));
        matrixStackIn.mulPose(MathUtils.quatFromRotationXYZ(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot()), 0, 0, true));

        float offset = (entityIn.getOffset() - 1)/2f;
        matrixStackIn.translate(-0.5f, -0.5f, -0.5f);

        BlockPos[] blockPoss = entityIn.getBlockPoss();
        BlockState[] blockStates = entityIn.getBlockStates();
        for(int i = 0 ;i < blockPoss.length;i++){
            matrixStackIn.pushPose();
            matrixStackIn.last().pose().translate(blockPoss[i].getX()-offset,blockPoss[i].getY()-offset,blockPoss[i].getZ()-offset);
            dispatcher.renderSingleBlock(blockStates[i], matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY);
            matrixStackIn.popPose();
        }
        matrixStackIn.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return null;
    }
}
