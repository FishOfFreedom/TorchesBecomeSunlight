package com.freefish.torchesbecomesunlight.client.render.entity;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.model.PursuerModel;
import com.freefish.torchesbecomesunlight.client.render.util.FFRenderTypes;
import com.freefish.torchesbecomesunlight.server.entity.ursus.Pursuer;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationAct;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

public class PursuerRenderer extends GeoEntityRenderer<Pursuer> {
    private static final ResourceLocation TRAIL_TEXTURE = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/particle/trail.png");

    public PursuerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PursuerModel());
        //addRenderLayer(new DialogueIconLayer(this));
    }

    @Override
    protected float getDeathMaxRotation(Pursuer animatable) {
        return 0;
    }

    @Override
    public void render(Pursuer entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        int tick = entity.getAnimationTick();
        AnimationAct animation = entity.getAnimation();
        if(((animation==Pursuer.TELE||animation==Pursuer.TELE1)&&tick>=8&&tick<20)||(animation==Pursuer.DEMON&&tick>=20&&tick<60))
            poseStack.scale(0,0.7f,0);
        else
            poseStack.scale(0.7f,0.7f,0.7f);

        if(entity.hasTrail()&&entity.getPredicate()!=0){
            double x = Mth.lerp(partialTick, entity.xOld, entity.getX());
            double y = Mth.lerp(partialTick, entity.yOld, entity.getY());
            double z = Mth.lerp(partialTick, entity.zOld, entity.getZ());
            poseStack.pushPose();
            poseStack.translate(-x, -y, -z);
            renderTrail(entity, partialTick, poseStack, bufferSource, 1F, packedLight);
            poseStack.popPose();
        }

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public int getPackedOverlay(Pursuer animatable, float u) {
        return OverlayTexture.pack(OverlayTexture.u(u),
                OverlayTexture.v(animatable.hurtTime > 0));
    }

    @Override
    public Color getRenderColor(Pursuer animatable, float partialTick, int packedLight) {
        float c = 1;
        AnimationAct animation = animatable.getAnimation();
        if((animation == Pursuer.TELE|animation==Pursuer.TELE1)&&animatable.getAnimationTick()<27){
            c = 1-((animatable.getAnimationTick()+partialTick)/27)*2;
            c = Math.abs(c);
            return Color.ofRGB(c,c,c);
        }else if(animation == Pursuer.DEMON){
            int animationTick = animatable.getAnimationTick();
            if(animationTick<20)
                c = 1 - (animationTick+partialTick)/20;
            else if(animationTick>=60){
                c = (animationTick-60+partialTick)/10;
            }
            c = Math.abs(c);
            return Color.ofRGB(c,c,c);
        }
        return super.getRenderColor(animatable, partialTick, packedLight);
    }

    private void renderTrail(Pursuer entityIn, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, float trailA, int packedLightIn) {
        int samples = 0;
        int sampleSize = 10;
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
            vertexconsumer.vertex(matrix4f, (float) draw1[0].x, (float) draw1[0].y, (float) draw1[0].z).color(0, 0, 0, trailA).uv(u1, 1F).overlayCoords(NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, (float) draw2[0].x, (float) draw2[0].y, (float) draw2[0].z).color(0, 0, 0, trailA).uv(u2, 1F).overlayCoords(NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, (float) draw2[1].x, (float) draw2[1].y, (float) draw2[1].z).color(0, 0, 0, trailA).uv(u2, 0).overlayCoords(NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, (float) draw1[1].x, (float) draw1[1].y, (float) draw1[1].z).color(0, 0, 0, trailA).uv(u1, 0).overlayCoords(NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            samples++;
            drawFrom = sample;
        }
    }
}
