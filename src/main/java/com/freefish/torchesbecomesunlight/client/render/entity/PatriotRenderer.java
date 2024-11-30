package com.freefish.torchesbecomesunlight.client.render.entity;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.layer.DialogueIconLayer;
import com.freefish.torchesbecomesunlight.client.render.layer.PulsatingGlowLayer;
import com.freefish.torchesbecomesunlight.client.render.model.PatriotModel;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.shield.Patriot;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PatriotRenderer extends GeoEntityRenderer<Patriot> {
    public static ResourceLocation GLOW = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/entity/patriot/halberd.png");

    public PatriotRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PatriotModel());
        addRenderLayer(new DialogueIconLayer(this));
        addRenderLayer(new PulsatingGlowLayer<Patriot>(this, GLOW, 0.1F, 1.0F, 0.25F,Patriot::changeHalberd));
    }

    @Override
    public void render(Patriot entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        //todo shader
        //poseStack.pushPose();
        //poseStack.translate(0, animatable.getBbHeight()+4, 0);
        //PoseStack.Pose matrixstack$entry = poseStack.last();
        //Matrix4f matrix4f2 = matrixstack$entry.pose();
        //Vector4f vector4f = matrix4f2.transform(new Vector4f(0,0,0,1f));
        //PoseStack poseStack1 = new PoseStack();
        //poseStack1.translate(vector4f.x,vector4f.y,vector4f.z);
        //Matrix4f matrix4f = poseStack1.last().pose();
        //Matrix3f matrix3f = matrixstack$entry.normal();
        //VertexConsumer ivertexbuilder = bufferSource.getBuffer(FFRenderTypes.HOLE);
        //float alpha = 0;
        //Player player = Minecraft.getInstance().player;
        //if(player!=null){
        //    alpha = Mth.clamp(2 - animatable.distanceTo(player)/8,0,1);
        //}
        //drawSun(matrix4f, matrix3f, ivertexbuilder,alpha);
        //poseStack.popPose();
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

    @Override
    protected float getDeathMaxRotation(Patriot animatable) {
        return 0;
    }

    @Override
    public int getPackedOverlay(Patriot animatable, float u) {
        return OverlayTexture.pack(OverlayTexture.u(u),
                OverlayTexture.v(animatable.hurtTime > 0));
    }
}
