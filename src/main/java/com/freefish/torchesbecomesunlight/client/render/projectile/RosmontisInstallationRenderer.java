package com.freefish.torchesbecomesunlight.client.render.projectile;

import com.freefish.torchesbecomesunlight.client.render.model.RosmontisInstallationModel;
import com.freefish.torchesbecomesunlight.server.entity.rhodesIsland.rosmontis.RosmontisInstallation;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RosmontisInstallationRenderer extends GeoEntityRenderer<RosmontisInstallation> {
    public RosmontisInstallationRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new RosmontisInstallationModel());
    }

    @Override
    public void render(RosmontisInstallation entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        Vector3f motionVec = entity.motion;
        Vector3f motionOVec = entity.motionO;
        if(motionVec!=null&&motionOVec!=null){
            double motionX = Mth.lerp(partialTick,motionOVec.x,motionVec.x);
            double motionZ = Mth.lerp(partialTick,motionOVec.z,motionVec.z);
            float lateralSpeed = (float) (motionX);
            float forwardSpeed = (float) (motionZ);
            float maxLateralSpeed = 0.5f;
            float maxForwardSpeed = 0.5f;
            float maxTiltZ = 15.0f;
            float maxTiltX = 15.0f;
            float tiltZ = Mth.clamp((lateralSpeed / maxLateralSpeed) * maxTiltZ, -maxTiltZ, maxTiltZ);
            float tiltX = Mth.clamp((-forwardSpeed / maxForwardSpeed) * maxTiltX, -maxTiltX, maxTiltX);
            poseStack.last().pose().rotate(Axis.XP.rotationDegrees(tiltX));
            poseStack.last().pose().rotate(Axis.ZP.rotationDegrees(tiltZ));
            poseStack.last().pose().rotate(Axis.YP.rotationDegrees(-entity.getYRot()));
        }

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

    @Override
    protected float getDeathMaxRotation(RosmontisInstallation animatable) {
        return 0;
    }

    @Override
    public int getPackedOverlay(RosmontisInstallation animatable, float u) {
        return OverlayTexture.NO_OVERLAY;
    }

    @Override
    public RenderType getRenderType(RosmontisInstallation animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, RosmontisInstallation animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if(bone.getName().equals("aControl")){
            RenderSystem.enableBlend();
            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, bone.getPosZ());
            RenderSystem.disableBlend();
        }
        else {
            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }
}
