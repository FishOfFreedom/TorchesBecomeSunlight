package com.freefish.torchesbecomesunlight.client.render.entity;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.model.CrazelyseonModel;
import com.freefish.torchesbecomesunlight.client.render.util.FFRenderTypes;
import com.freefish.torchesbecomesunlight.server.entity.demon.Crazelyseon;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CrazelyseonRenderer extends GeoEntityRenderer<Crazelyseon> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/entity/crazelyseon/crazelyseon_eye.png");

    public CrazelyseonRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CrazelyseonModel());
    }

    @Override
    public void render(Crazelyseon entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.last().pose().scale(32f);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();

        if(false){
            Player player = Minecraft.getInstance().player;
            if (player == null) return;

            poseStack.pushPose();
            poseStack.translate(0, 24, 0);
            Vec3 playerVec = player.getEyePosition(partialTick).subtract(entity.getPosition(partialTick).add(0, 24, 0));
            double d6 = playerVec.y;
            double d4 = playerVec.horizontalDistance();

            Quaternionf quatX = MathUtils.quatFromRotationXYZ(0, (float) (Mth.atan2(playerVec.x, playerVec.z)), 0, false);
            Quaternionf quatY = MathUtils.quatFromRotationXYZ(-(float) (Mth.atan2(d6, d4)), 0, 0, false);

            Vec3 finalVec = playerVec.normalize().scale(24);

            poseStack.translate(finalVec.x, finalVec.y, finalVec.z);
            PoseStack.Pose matrixstack$entry = poseStack.last();

            VertexConsumer portalStatic = bufferSource.getBuffer(FFRenderTypes.getGlowingEffect(TEXTURE));
            PoseStack posestack = new PoseStack();
            PoseStack.Pose posestack$pose = posestack.last();
            Matrix3f matrix3f = posestack$pose.normal();

            Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1, -1.0F, 0), new Vector3f(-1, 1.0F, 0), new Vector3f(1, 1.0F, 0), new Vector3f(1, -1.0F, 0)};
            float f4 = 60F;
            for (int i = 0; i < 4; ++i) {
                Vector3f vector3f = avector3f[i];
                vector3f.rotate(quatY);
                vector3f.rotate(quatX);
                vector3f.mul(f4 + (float) Math.sin(entity.tickCount + partialTick * 2) * 0.2f);
            }

            float f7 = 0;
            float f8 = 1;
            float f5 = 0;
            float f6 = 1;
            int j = 240;
            portalStatic.vertex(matrixstack$entry.pose(), (float) avector3f[0].x(), (float) avector3f[0].y(), (float) avector3f[0].z()).color(1f, 1f, 1f, 1f).uv(f8, f6).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
            portalStatic.vertex(matrixstack$entry.pose(), (float) avector3f[1].x(), (float) avector3f[1].y(), (float) avector3f[1].z()).color(1f, 1f, 1f, 1f).uv(f8, f5).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
            portalStatic.vertex(matrixstack$entry.pose(), (float) avector3f[2].x(), (float) avector3f[2].y(), (float) avector3f[2].z()).color(1f, 1f, 1f, 1f).uv(f7, f5).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
            portalStatic.vertex(matrixstack$entry.pose(), (float) avector3f[3].x(), (float) avector3f[3].y(), (float) avector3f[3].z()).color(1f, 1f, 1f, 1f).uv(f7, f6).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();

            poseStack.popPose();
        }

    }

    @Override
    protected float getDeathMaxRotation(Crazelyseon animatable) {
        return 0;
    }

    @Override
    public int getPackedOverlay(Crazelyseon animatable, float u) {
        return OverlayTexture.pack(OverlayTexture.u(u),
                OverlayTexture.v(animatable.hurtTime > 0));
    }
}
