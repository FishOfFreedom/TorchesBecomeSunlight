package com.freefish.torchesbecomesunlight.client.render.entity;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.layer.DialogueIconLayer;
import com.freefish.torchesbecomesunlight.client.render.layer.PatriotGlowLayer;
import com.freefish.torchesbecomesunlight.client.render.model.PatriotModel;
import com.freefish.torchesbecomesunlight.client.render.util.FFRenderTypes;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.shield.Patriot;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

public class PatriotRenderer extends GeoEntityRenderer<Patriot> {
    private static final ResourceLocation TRAIL_TEXTURE = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/particle/trail.png");

    public PatriotRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PatriotModel());
        addRenderLayer(new DialogueIconLayer(this));
        addRenderLayer(new PatriotGlowLayer(this));
    }

    @Override
    public void render(Patriot entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {

        if(entity.hasTrail()){
            double x = Mth.lerp(partialTick, entity.xOld, entity.getX());
            double y = Mth.lerp(partialTick, entity.yOld, entity.getY());
            double z = Mth.lerp(partialTick, entity.zOld, entity.getZ());
            poseStack.pushPose();
            poseStack.translate(-x, -y, -z);
            renderTrail(entity, partialTick, poseStack, bufferSource, 1F, packedLight);
            poseStack.popPose();
        }

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

    private void renderTrail(Patriot entityIn, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, float trailA, int packedLightIn) {
        int samples = 0;
        int sampleSize = 7;
        Vec3[] drawFrom = entityIn.getTrailPosition(0, partialTicks);
        VertexConsumer vertexconsumer = bufferIn.getBuffer(FFRenderTypes.getGlowingEffect(TRAIL_TEXTURE));
        while (samples < sampleSize) {
            Vec3[] sample = entityIn.getTrailPosition(samples + 2, partialTicks);
            float u1 = samples / (float) sampleSize;
            float u2 = u1 + 1 / (float) sampleSize;

            Vec3[] draw1 = drawFrom;
            Vec3[] draw2 = sample;

            PoseStack.Pose posestack$pose = poseStack.last();
            Matrix4f matrix4f = posestack$pose.pose();
            Matrix3f matrix3f = posestack$pose.normal();
            vertexconsumer.vertex(matrix4f, (float) draw1[0].x, (float) draw1[0].y, (float) draw1[0].z).color(1f, 1f, 1f, trailA).uv(u1, 1F).overlayCoords(NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, (float) draw2[0].x, (float) draw2[0].y, (float) draw2[0].z).color(1f, 1f, 1f, trailA).uv(u2, 1F).overlayCoords(NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, (float) draw2[1].x, (float) draw2[1].y, (float) draw2[1].z).color(1f, 1f, 1f, trailA).uv(u2, 0).overlayCoords(NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, (float) draw1[1].x, (float) draw1[1].y, (float) draw1[1].z).color(1f, 1f, 1f, trailA).uv(u1, 0).overlayCoords(NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            samples++;
            drawFrom = sample;
        }
    }

    @Override
    public void renderRecursively(PoseStack poseStack, Patriot animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if(bone.getName().matches("shieldFX.*")){
            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, bone.getPosX());
        }
        else
            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
