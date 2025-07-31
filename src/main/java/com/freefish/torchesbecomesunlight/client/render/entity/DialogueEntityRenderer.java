package com.freefish.torchesbecomesunlight.client.render.entity;


import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.story.data.DialogueEntry;
import com.freefish.torchesbecomesunlight.server.story.dialogueentity.DialogueEntity;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class DialogueEntityRenderer extends EntityRenderer<DialogueEntity> {
    private static final ResourceLocation DIALOGUE =new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/particle/ribbon_flat.png");
    public DialogueEntityRenderer(EntityRendererProvider.Context mgr) {
        super(mgr);
    }

    @Override
    public ResourceLocation getTextureLocation(DialogueEntity entity) {
        return null;
    }

    @Override
    public void render(DialogueEntity animatable, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLightIn) {
        poseStack.pushPose();
        drawString(animatable,poseStack, bufferSource,partialTicks, packedLightIn);
        poseStack.popPose();

        super.render(animatable, entityYaw, partialTicks, poseStack, bufferSource, packedLightIn);
    }

    private void drawString(DialogueEntity entity, PoseStack poseStack, MultiBufferSource bufferSource,float par, int packedLightIn) {
        Player player = Minecraft.getInstance().player;
        if(entity.hasChatEntities()) {
            DialogueEntry dialogue = entity.getDialogue();
            if(dialogue!=null&&player!=null) {
                Font font = this.getFont();
                String oMessage = entity.currentText;
                int len = font.width(oMessage);

                String  message = cuttingString(oMessage, MathUtils.easeOutCubic(Math.min(1,2f*entity.getDialogueScale())));
                Entity chatEntity = entity.getChatEntities(dialogue.getSpeaker());
                if(chatEntity!=null){
                    poseStack.translate(chatEntity.getX()-entity.getX(), chatEntity.getY()+1.5+(1-1.5/chatEntity.getBbHeight())-entity.getY(), chatEntity.getZ()-entity.getZ());

                    Vec3 playerVec = player.getPosition(par).subtract(chatEntity.getPosition(par));
                    float len1 = (float) playerVec.length();
                    Vec3 finalVec = playerVec.normalize().scale(Math.max(0, len1 - 4));
                    poseStack.translate(finalVec.x, finalVec.y, finalVec.z);
                    Matrix4f matrix4f = poseStack.last().pose();
                    poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());

                    poseStack.scale(-0.025F, -0.025F, 0.025F);

                    float startX = ((float) -len) / 2;
                    int width = font.width(message);
                    int height = font.lineHeight;
                    font.drawInBatch(message, startX, 0, 0XFFFFFF, false, matrix4f, bufferSource, Font.DisplayMode.NORMAL, 0, packedLightIn);
                    RenderSystem.enableBlend();
                    VertexConsumer ivertexbuilder = bufferSource.getBuffer(RenderType.entityTranslucent(DIALOGUE, true));
                    ivertexbuilder.vertex(matrix4f, startX, -1, 0.01f).color(0, 0, 0, 0.3f).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(poseStack.last().normal(), 1.0F, 1.0F, 1.0F).endVertex();
                    ivertexbuilder.vertex(matrix4f, startX, height, 0.01f).color(0, 0, 0, 0.3f).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(poseStack.last().normal(), 1.0F, 1.0F, 1.0F).endVertex();
                    ivertexbuilder.vertex(matrix4f, startX + width, height, 0.01f).color(0, 0, 0, 0.3f).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(poseStack.last().normal(), 1.0F, 1.0F, 1.0F).endVertex();
                    ivertexbuilder.vertex(matrix4f, startX + width, -1, 0.01f).color(0, 0, 0, 0.3f).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(poseStack.last().normal(), 1.0F, 1.0F, 1.0F).endVertex();
                    RenderSystem.disableBlend();
                }
            }
        }
    }

    private static String cuttingString(String input , double fraction){
        if(fraction > 1) fraction = 1;
        int cutIndex = Math.round((int)(input.length() * fraction));
        return input.substring(0, cutIndex);
    }
}
