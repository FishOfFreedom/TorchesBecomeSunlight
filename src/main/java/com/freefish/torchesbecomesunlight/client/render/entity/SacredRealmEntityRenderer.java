package com.freefish.torchesbecomesunlight.client.render.entity;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.util.FFRenderTypes;
import com.freefish.torchesbecomesunlight.server.entity.effect.SacredRealmEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Math;
import org.joml.Matrix3f;
import org.joml.Vector3f;

public class SacredRealmEntityRenderer extends EntityRenderer<SacredRealmEntity> {
    private static final ResourceLocation[] TEXTURE = new ResourceLocation[]{
            new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/particle/fire/fire1.png"),
            new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/particle/fire/fire2.png"),
            new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/particle/fire/fire3.png"),
            new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/particle/fire/fire4.png"),
            new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/particle/fire/fire5.png"),
            new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/particle/fire/fire6.png"),
            new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/particle/fire/fire7.png"),
    };
    public SacredRealmEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(SacredRealmEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        poseStack.pushPose();
        PoseStack.Pose matrixstack$entry = poseStack.last();

        VertexConsumer portalStatic = bufferSource.getBuffer(FFRenderTypes.getGlowingEffect(TEXTURE[entity.tickCount%7]));
        PoseStack posestack = new PoseStack();
        PoseStack.Pose posestack$pose = posestack.last();
        Matrix3f matrix3f = posestack$pose.normal();

        float scale =Math.min(1,(partialTick+entity.tickCount)/10);
        float len1 = 1.5f*scale;
        float len2 = 2.5f*scale;
        renderFire(-len1,-len2,len1,-len2,portalStatic,matrixstack$entry,matrix3f,scale);
        renderFire(len1,-len2,len1,-len1,portalStatic,matrixstack$entry,matrix3f,scale);
        renderFire(len1,-len1,len2,-len1,portalStatic,matrixstack$entry,matrix3f,scale);
        renderFire(len2,-len1,len2,len1,portalStatic,matrixstack$entry,matrix3f,scale);
        renderFire(len2,len1,len1,len1,portalStatic,matrixstack$entry,matrix3f,scale);
        renderFire(len1,len1,len1,len2,portalStatic,matrixstack$entry,matrix3f,scale);
        renderFire(len1,len2,-len1,len2,portalStatic,matrixstack$entry,matrix3f,scale);
        renderFire(-len1,len2,-len1,len1,portalStatic,matrixstack$entry,matrix3f,scale);
        renderFire(-len1,len1,-len2,len1,portalStatic,matrixstack$entry,matrix3f,scale);
        renderFire(-len2,len1,-len2,-len1,portalStatic,matrixstack$entry,matrix3f,scale);
        renderFire(-len2,-len1,-len1,-len1,portalStatic,matrixstack$entry,matrix3f,scale);
        renderFire(-len1,-len1,-len1,-len2,portalStatic,matrixstack$entry,matrix3f,scale);

        poseStack.popPose();
    }

    private void renderFire(float x1,float z1,float x2 , float z2,VertexConsumer portalStatic,PoseStack.Pose matrixstack$entry,Matrix3f matrix3f,float alpha){
        float f8 = 1;
        float f5 = 0;
        float f6 = 1;
        int j = 240;

        float offX = x2  - x1;
        float offZ = z2  - z1;
        float len = Math.sqrt(offX*offX+offZ*offZ);
        int count = 0;
        float v = offX / len;
        float v1 = offZ / len;

        while (len>0){
            float finalLen = Math.min(1f,len);

            portalStatic.vertex(matrixstack$entry.pose(), x1+ v *(count), 0, z1+ v1 *(count)).color(1f, 0.8f, 0f, alpha).uv(f8, f6).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
            portalStatic.vertex(matrixstack$entry.pose(), x1+ v *(count), 1, z1+ v1 *(count)).color(1f, 0.8f, 0f, alpha).uv(f8, f5).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
            portalStatic.vertex(matrixstack$entry.pose(), x1+ v *(finalLen+count), 1, z1+ v1 *(finalLen+count)).color(1f, 0.8f, 0f, alpha).uv(1-finalLen, f5).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
            portalStatic.vertex(matrixstack$entry.pose(), x1+ v *(finalLen+count), 0, z1+ v1 *(finalLen+count)).color(1f, 0.8f, 0f, alpha).uv(1-finalLen, f6).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();

            count+=1;
            len-=1;
        }
    }

    @Override
    public ResourceLocation getTextureLocation(SacredRealmEntity sacredRealmEntity) {
        return null;
    }
}
