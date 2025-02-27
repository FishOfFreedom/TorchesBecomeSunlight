package com.freefish.torchesbecomesunlight.client.render.projectile;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.util.FFRenderTypes;
import com.freefish.torchesbecomesunlight.server.entity.projectile.Bullet;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.awt.*;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

public class BulletRenderer extends EntityRenderer<Bullet> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/entity/decal.png");
    public static final ResourceLocation TRAIL_TEXTURE = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/particle/trail_1.png");
    private static final ResourceLocation SUN = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/particle/sun.png");
    protected EntityModel<Bullet> model;

    public BulletRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = new DecalModel(context.bakeLayer(DecalModel.LAYER_LOCATION));
    }

    @Override
    public boolean shouldRender(Bullet pLivingEntity, Frustum pCamera, double pCamX, double pCamY, double pCamZ) {
        return true;
    }

    @Override
    public void render(Bullet pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        int i = pEntity.getType1();

        float baseScale = 0 ;
        if(i==0||i==4){
            if(pEntity.isHoly()){
                draw(pMatrixStack, pBuffer, 0.45f);
            }
        }else if(i==1||i==3){
            baseScale = 0.7f;
            if(pEntity.isHoly()) {
                draw(pMatrixStack, pBuffer, 1+(1 + (float) Math.sin(pEntity.tickCount + pPartialTicks))/4);
            }
        }else if(i==2){
            baseScale = 4f;
            draw(pMatrixStack, pBuffer, (1+(1 + (float) Math.sin((pEntity.tickCount + pPartialTicks)*2))/4)*2);
        }

        if (pEntity.hasTrail()) {
            double x = Mth.lerp(pPartialTicks, pEntity.xOld, pEntity.getX());
            double y = Mth.lerp(pPartialTicks, pEntity.yOld, pEntity.getY());
            double z = Mth.lerp(pPartialTicks, pEntity.zOld, pEntity.getZ());
            pMatrixStack.pushPose();
            pMatrixStack.translate(-x, -y, -z);
            if(pEntity.getType1()!=2&&pEntity.getType1()!=3){
                renderTrail(pEntity, pPartialTicks, pMatrixStack, pBuffer, pPackedLight, 0.1f + baseScale);
            }else {
                renderTrail2(pEntity, pPartialTicks, pMatrixStack, pBuffer, pPackedLight, 0.1f + baseScale);
            }
            pMatrixStack.popPose();
        }


        this.model.setupAnim(pEntity, 0, 1.6f, 0.0F, (float) Math.toRadians(pEntity.getYRot()), (float) Math.toRadians(pEntity.getXRot()));
        VertexConsumer vertexconsumer1 = pBuffer.getBuffer(model.renderType(TEXTURE));
        pMatrixStack.pushPose();
        pMatrixStack.last().pose().scale(0.7f+baseScale);
        this.model.renderToBuffer(pMatrixStack, vertexconsumer1, 15728880, NO_OVERLAY, 1, 0.8f, 0, 1F);
        pMatrixStack.popPose();

        pMatrixStack.pushPose();
        pMatrixStack.last().pose().scale(1f+baseScale);
        VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.eyes(TEXTURE));
        this.model.renderToBuffer(pMatrixStack, vertexconsumer, 15728880, NO_OVERLAY, 1f,1f,1F, 1F);
        pMatrixStack.popPose();

        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    private void renderTrail(Bullet entityIn, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn,float trailHeight) {
        int samples = 0;

        int sampleSize = 3;
        Vec3 topAngleVec = new Vec3(0, trailHeight, 0);
        Vec3 bottomAngleVec = new Vec3(0, -trailHeight, 0);
        Vec3 drawFrom = entityIn.getTrailPosition(0, partialTicks);
        VertexConsumer vertexconsumer = bufferIn.getBuffer(FFRenderTypes.getGlowingEffect((TRAIL_TEXTURE)));
        while (samples < sampleSize) {
            Vec3 sample = entityIn.getTrailPosition(samples + 1, partialTicks);
            float u1 = samples / (float) sampleSize;
            float u2 = u1 + 1 / (float) sampleSize;

            Vec3 draw1 = drawFrom;
            Vec3 draw2 = sample;

            PoseStack.Pose posestack$pose = poseStack.last();
            Matrix4f matrix4f = posestack$pose.pose();
            Matrix3f matrix3f = posestack$pose.normal();
            vertexconsumer.vertex(matrix4f, (float) draw1.x + (float) bottomAngleVec.x, (float) draw1.y + (float) bottomAngleVec.y, (float) draw1.z + (float) bottomAngleVec.z).color(1,1,1, 0.8f).uv(u1, 1F).overlayCoords(NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, (float) draw2.x + (float) bottomAngleVec.x, (float) draw2.y + (float) bottomAngleVec.y, (float) draw2.z + (float) bottomAngleVec.z).color(1,1,1, 0.8f).uv(u2, 1F).overlayCoords(NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, (float) draw2.x + (float) topAngleVec.x, (float) draw2.y + (float) topAngleVec.y, (float) draw2.z + (float) topAngleVec.z)         .color(1,1,1, 0.8f).uv(u2, 0).overlayCoords(NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, (float) draw1.x + (float) topAngleVec.x, (float) draw1.y + (float) topAngleVec.y, (float) draw1.z + (float) topAngleVec.z)         .color(1,1,1, 0.8f).uv(u1, 0).overlayCoords(NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            samples++;
            drawFrom = sample;
        }
    }

    private void renderTrail2(Bullet entityIn, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn,float trailHeight) {
        int samples = 0;

        int sampleSize = 4;
        Vec3 topAngleVec =    new Vec3(trailHeight , 0, 0);
        Vec3 bottomAngleVec = new Vec3(-trailHeight, 0, 0);
        Vec3 topAngleVec1 =    new Vec3(0, 0, trailHeight );
        Vec3 bottomAngleVec1 = new Vec3(0, 0, -trailHeight);
        Vec3 drawFrom = entityIn.getTrailPosition(0, partialTicks);
        VertexConsumer vertexconsumer = bufferIn.getBuffer(FFRenderTypes.getGlowingEffect((TRAIL_TEXTURE)));
        while (samples < sampleSize) {
            Vec3 sample = entityIn.getTrailPosition(samples + 1, partialTicks);
            float u1 = samples / (float) sampleSize;
            float u2 = u1 + 1 / (float) sampleSize;

            Vec3 draw1 = drawFrom;
            Vec3 draw2 = sample;

            PoseStack.Pose posestack$pose = poseStack.last();
            Matrix4f matrix4f = posestack$pose.pose();
            Matrix3f matrix3f = posestack$pose.normal();
            vertexconsumer.vertex(matrix4f, (float) draw1.x + (float) bottomAngleVec.x, (float) draw1.y + (float) bottomAngleVec.y, (float) draw1.z + (float) bottomAngleVec.z).color(1,1,1, 0.8f).uv(u1, 1F).overlayCoords(NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, (float) draw2.x + (float) bottomAngleVec.x, (float) draw2.y + (float) bottomAngleVec.y, (float) draw2.z + (float) bottomAngleVec.z).color(1,1,1, 0.8f).uv(u2, 1F).overlayCoords(NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, (float) draw2.x + (float) topAngleVec.x, (float) draw2.y + (float) topAngleVec.y, (float) draw2.z + (float) topAngleVec.z)         .color(1,1,1, 0.8f).uv(u2, 0).overlayCoords(NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, (float) draw1.x + (float) topAngleVec.x, (float) draw1.y + (float) topAngleVec.y, (float) draw1.z + (float) topAngleVec.z)         .color(1,1,1, 0.8f).uv(u1, 0).overlayCoords(NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();

            vertexconsumer.vertex(matrix4f, (float) draw1.x + (float) bottomAngleVec1.x, (float) draw1.y + (float) bottomAngleVec1.y, (float) draw1.z + (float) bottomAngleVec1.z).color(1,1,1, 0.8f).uv(u1, 1F).overlayCoords(NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, (float) draw2.x + (float) bottomAngleVec1.x, (float) draw2.y + (float) bottomAngleVec1.y, (float) draw2.z + (float) bottomAngleVec1.z).color(1,1,1, 0.8f).uv(u2, 1F).overlayCoords(NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, (float) draw2.x + (float) topAngleVec1.x, (float) draw2.y + (float) topAngleVec1.y, (float) draw2.z + (float) topAngleVec1.z)         .color(1,1,1, 0.8f).uv(u2, 0).overlayCoords(NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, (float) draw1.x + (float) topAngleVec1.x, (float) draw1.y + (float) topAngleVec1.y, (float) draw1.z + (float) topAngleVec1.z)         .color(1,1,1, 0.8f).uv(u1, 0).overlayCoords(NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            samples++;
            drawFrom = sample;
        }
    }

    private void draw(PoseStack poseStack,MultiBufferSource bufferSource,float time){
        poseStack.pushPose();
        PoseStack.Pose matrixstack$entry = poseStack.last();
        Matrix4f matrix4f2 = matrixstack$entry.pose();
        Vector4f vector4f = matrix4f2.transform(new Vector4f(0,0,0,1f));
        PoseStack poseStack1 = new PoseStack();
        poseStack1.translate(vector4f.x,vector4f.y,vector4f.z);
        Matrix4f matrix4f = poseStack1.last().pose();
        Matrix3f matrix3f = matrixstack$entry.normal();

        VertexConsumer ivertexbuilder = bufferSource.getBuffer(FFRenderTypes.getGlowingEffect(SUN));
        drawSun(matrix4f, matrix3f, ivertexbuilder,time);

        poseStack.popPose();
    }

    private void drawSun(Matrix4f matrix4f, Matrix3f matrix3f, VertexConsumer builder, float alpha) {
        RenderSystem.enableBlend();
        this.drawVertex(matrix4f, matrix3f, builder, -alpha, -alpha, 0, 0, 0,1);
        this.drawVertex(matrix4f, matrix3f, builder, -alpha,  alpha,  0, 0, 1,1);
        this.drawVertex(matrix4f, matrix3f, builder, alpha,   alpha,   0, 1, 1,1);
        this.drawVertex(matrix4f, matrix3f, builder, alpha,  -alpha,  0, 1, 0,1);
        RenderSystem.disableBlend();
    }

    public void drawVertex(Matrix4f matrix, Matrix3f normals, VertexConsumer vertexBuilder, float offsetX, float offsetY, float offsetZ, float textureX, float textureY,float alpha) {
        vertexBuilder.vertex(matrix, offsetX, offsetY, offsetZ).color(1f, 1f, 1f, alpha).uv(textureX, textureY).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normals, 1.0F, 1.0F, 1.0F).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(Bullet corpse) {
        return TEXTURE;
    }



    public static class DecalModel extends EntityModel<Bullet> {
        public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "decal"), "main");
        private final ModelPart bb_main;

        public DecalModel(ModelPart root) {
            this.bb_main = root.getChild("bb_main");
        }

        public static LayerDefinition createBodyLayer() {
            MeshDefinition meshdefinition = new MeshDefinition();
            PartDefinition partdefinition = meshdefinition.getRoot();

            PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-2)), PartPose.offset(0.0F, 0.0F, 0.0F));

            return LayerDefinition.create(meshdefinition, 64, 64);
        }

        @Override
        public void setupAnim(Bullet entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
            bb_main.yRot = netHeadYaw;
            bb_main.xRot = -headPitch;
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
            bb_main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }
}
