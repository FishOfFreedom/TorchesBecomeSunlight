package com.freefish.torchesbecomesunlight.client.render.blockentity;

import com.freefish.torchesbecomesunlight.client.render.model.ElevatorModel;
import com.freefish.torchesbecomesunlight.client.render.model.tools.MathUtils;
import com.freefish.torchesbecomesunlight.server.block.blockentity.ElevatorBlockEntity;
import com.freefish.torchesbecomesunlight.server.block.blockentity.HookPlayerClick;
import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.PlayerCapability;
import com.freefish.torchesbecomesunlight.server.util.FFEntityUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ElebatorRenderer extends GeoBlockRenderer<ElevatorBlockEntity> {
    public ElebatorRenderer() {
        super(new ElevatorModel());
    }

    @Override
    public boolean shouldRenderOffScreen(ElevatorBlockEntity pBlockEntity) {
        return true;
    }

    @Override
    public void actuallyRender(PoseStack poseStack, ElevatorBlockEntity animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        LocalPlayer player = Minecraft.getInstance().player;

        poseStack.pushPose();
        float upCurrentHeight = animatable.getUpCurrentHeight(partialTick);
        poseStack.last().pose().translate(0, upCurrentHeight,0);
        //if(animatable.isUp){
        //    List<LivingEntity> players1 = animatable.players;
        //    for(LivingEntity living : players1){
        //        living.setPos(living.getX(),animatable.getBlockPos().getY()+upCurrentHeight+0.01f,living.getZ());
        //    }
        //}
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        poseStack.popPose();

        PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(player, CapabilityHandle.PLAYER_CAPABILITY);
        if(capability!=null){
            HookPlayerClick hookPlayer = capability.getHookPlayer();
            if(hookPlayer!=null){
                String message = "互动";
                Vec3 center = animatable.getBlockPos().getCenter();
                Vec3 headRotVec = FFEntityUtils.getHeadRotVec(player, new Vec3(0, 0, 1)).subtract(player.getX(), player.getY(), player.getZ());
                Vec3 subtract = center.subtract(player.getEyePosition());
                double dot = subtract.dot(headRotVec);
                if (dot > 0) {
                    //Vec3 eye = player.getEyePosition().subtract(animatable.getBlockPos().getX(), animatable.getBlockPos().getY(), animatable.getBlockPos().getZ());
                    Vec3 normalize = subtract.normalize();
                    normalize = normalize.scale(dot / subtract.length());
                    Vec3 subtract1 = normalize.subtract(headRotVec);
                    //VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.lines());

                    //vertexconsumer.vertex(poseStack.last().pose(), (float) (eye.x + headRotVec.x), (float) (eye.y + headRotVec.y), (float) (eye.z + headRotVec.z)).color(0, 0, 255, 255).normal(poseStack.last().normal(), 0, 0, 0).endVertex();
                    //vertexconsumer.vertex(poseStack.last().pose(), (float) (eye.x + normalize.x), (float) (eye.y + normalize.y), (float) (eye.z + normalize.z)).color(0, 0, 255, 255).normal(poseStack.last().normal(), 0, 0, 0).endVertex();
                    //vertexconsumer.vertex(poseStack.last().pose(), (float) (eye.x), (float) (eye.y), (float) (eye.z)).color(0, 0, 255, 255).normal(poseStack.last().normal(), 0, 0, 0).endVertex();
                    //vertexconsumer.vertex(poseStack.last().pose(), (float) (0.5), (float) (0.5), (float) (0.5)).color(255, 0, 0, 255).normal(poseStack.last().normal(), 0, 0, 0).endVertex();

                    if (subtract1.length() < 0.1) {
                        message = "选中";
                        hookPlayer.isLocalPlayerLooked = true;
                    } else {
                        hookPlayer.isLocalPlayerLooked = false;
                    }
                }
                drawString(animatable, message, poseStack, bufferSource, partialTick, packedLight);
            }
        }
    }

    private void drawString(ElevatorBlockEntity animatable,String message, PoseStack poseStack, MultiBufferSource bufferSource,float par, int packedLightIn) {
        poseStack.pushPose();
        Font font = Minecraft.getInstance().font;
        int len = font.width(message);
        poseStack.last().pose().translate(0.5f,1,0.5f);
        Matrix4f matrix4f = poseStack.last().pose();
        //poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        poseStack.mulPose(MathUtils.quatFromRotationXYZ(0,3.14f,0,false));
        poseStack.scale(-0.025F, -0.025F, 0.025F);
        float startX = ((float) -len) / 2;
        font.drawInBatch(message, startX, 0, 0XFFFFFF, false, matrix4f, bufferSource, Font.DisplayMode.NORMAL, 0, packedLightIn);
        poseStack.popPose();
    }
}
