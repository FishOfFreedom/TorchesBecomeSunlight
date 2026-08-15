package com.freefish.torchesbecomesunlight.client.render.layer;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.DialogueCapability;
import com.freefish.torchesbecomesunlight.server.entity.AnimatedEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class DialogueIconLayer<T extends AnimatedEntity> extends GeoRenderLayer<T> {
    private static final ResourceLocation[] DIALOGUE = new ResourceLocation[]{
            ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "textures/particle/dialogue/dialogueicon1.png"),
            ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "textures/particle/dialogue/dialogueicon2.png"),
            ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "textures/particle/dialogue/dialogueicon3.png"),
            ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "textures/particle/dialogue/dialogueicon4.png"),
            ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "textures/particle/dialogue/dialogueicon5.png"),
            ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "textures/particle/dialogue/dialogueicon6.png"),
            ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "textures/particle/dialogue/dialogueicon7.png")
    };

    public DialogueIconLayer(GeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        super.render(poseStack, animatable, bakedModel, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
        poseStack.pushPose();
        poseStack.translate(0, animatable.getBbHeight()+2, 0);
        PoseStack.Pose matrixstack$entry = poseStack.last();
        Matrix4f matrix4f2 = matrixstack$entry.pose();
        Vector4f vector4f = matrix4f2.transform(new Vector4f(0,0,0,1f));
        PoseStack poseStack1 = new PoseStack();
        poseStack1.translate(vector4f.x,vector4f.y,vector4f.z);
        Matrix4f matrix4f = poseStack1.last().pose();
        PoseStack.Pose matrix3f = matrixstack$entry;

        DialogueCapability.IDialogueCapability data = CapabilityHandle.getCapability(animatable, CapabilityHandle.DIALOGUE_CAPABILITY);
        if(data!=null){
            int dialogueTime = data.getDialogueNeedTime();
            float alpha = 1;
            VertexConsumer ivertexbuilder;
            if(dialogueTime>40){
                int time = (animatable.tickCount)%7;
                ivertexbuilder = bufferSource.getBuffer(RenderType.entityTranslucent(DIALOGUE[time],true));
            }
            else {
                ivertexbuilder = bufferSource.getBuffer(RenderType.entityTranslucent(DIALOGUE[0],true));
                alpha = dialogueTime /40f;
            }
            if(alpha>0.05)
                drawSun(matrix4f, matrix3f, ivertexbuilder,alpha);
        }

        bufferSource.getBuffer(renderType);
        poseStack.popPose();
    }

    private void drawSun(Matrix4f matrix4f, PoseStack.Pose matrix3f, VertexConsumer builder, float alpha) {
        float sunRadius = 0.3f;
        RenderSystem.enableBlend();
        this.drawVertex(matrix4f, matrix3f, builder, -sunRadius, -sunRadius, 0, 0, 0,alpha);
        this.drawVertex(matrix4f, matrix3f, builder, -sunRadius, sunRadius,  0, 0, 1,alpha);
        this.drawVertex(matrix4f, matrix3f, builder, sunRadius, sunRadius,   0, 1, 1,alpha);
        this.drawVertex(matrix4f, matrix3f, builder, sunRadius, -sunRadius,  0, 1, 0,alpha);
        RenderSystem.disableBlend();
    }

    public void drawVertex(Matrix4f matrix, PoseStack.Pose normals, VertexConsumer vertexBuilder, float offsetX, float offsetY, float offsetZ, float textureX, float textureY,float alpha) {
        vertexBuilder.addVertex(matrix, (float) (offsetX), (float) (offsetY), (float) (offsetZ)).setColor(1f, 1f, 1f, alpha).setUv(textureX, textureY).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(normals, 1.0F, 1.0F, 1.0F);
    }
}
