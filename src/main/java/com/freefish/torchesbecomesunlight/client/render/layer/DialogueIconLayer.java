package com.freefish.torchesbecomesunlight.client.render.layer;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.capability.story.PlayerStoryStoneProvider;
import com.freefish.torchesbecomesunlight.server.entity.AnimatedEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class DialogueIconLayer<T extends AnimatedEntity> extends GeoRenderLayer<T> {
    private static final ResourceLocation[] DIALOGUE = new ResourceLocation[]{
            new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/particle/dialogue/dialogueicon1.png"),
            new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/particle/dialogue/dialogueicon2.png"),
            new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/particle/dialogue/dialogueicon3.png"),
            new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/particle/dialogue/dialogueicon4.png"),
            new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/particle/dialogue/dialogueicon5.png"),
            new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/particle/dialogue/dialogueicon6.png"),
            new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/particle/dialogue/dialogueicon7.png")
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
        Matrix3f matrix3f = matrixstack$entry.normal();
        Player player = Minecraft.getInstance().player;
        if(player!=null){
            player.getCapability(PlayerStoryStoneProvider.PLAYER_STORY_STONE_CAPABILITY).ifPresent(data->{
                int dialogueTime = data.getDialogueTime();
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
                    drawSun(matrix4f, matrix3f, ivertexbuilder,alpha);
            });
        }

        bufferSource.getBuffer(renderType);
        poseStack.popPose();
    }

    private void drawSun(Matrix4f matrix4f, Matrix3f matrix3f, VertexConsumer builder, float alpha) {
        float sunRadius = 0.3f;
        this.drawVertex(matrix4f, matrix3f, builder, -sunRadius, -sunRadius, 0, 0, 0,alpha);
        this.drawVertex(matrix4f, matrix3f, builder, -sunRadius, sunRadius,  0, 0, 1,alpha);
        this.drawVertex(matrix4f, matrix3f, builder, sunRadius, sunRadius,   0, 1, 1,alpha);
        this.drawVertex(matrix4f, matrix3f, builder, sunRadius, -sunRadius,  0, 1, 0,alpha);
    }

    public void drawVertex(Matrix4f matrix, Matrix3f normals, VertexConsumer vertexBuilder, float offsetX, float offsetY, float offsetZ, float textureX, float textureY,float alpha) {
        vertexBuilder.vertex(matrix, offsetX, offsetY, offsetZ).color(1f, 1f, 1f, alpha).uv(textureX, textureY).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normals, 1.0F, 1.0F, 1.0F).endVertex();
    }
}
