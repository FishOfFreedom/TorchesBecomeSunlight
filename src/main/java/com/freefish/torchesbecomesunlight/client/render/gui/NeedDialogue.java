package com.freefish.torchesbecomesunlight.client.render.gui;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;

public class NeedDialogue {
    private static ResourceLocation RING = new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/gui/fx/skill_outline.png");
    private static ResourceLocation RINGR = new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/gui/fx/skill_radial_mask.png");

    public static void renderGui(RenderGuiOverlayEvent.Pre event) {
        GuiGraphics guiGraphics = event.getGuiGraphics();
        //PoseStack pose = guiGraphics.pose();
        //RenderSystem.setShaderTexture(0, RING);
        //Matrix4f matrix4f = pose.last().pose();
        //RenderSystem.setShader(ShaderRegistry::getRenderTypeDemon);
        //BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        //bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        //bufferbuilder.vertex(matrix4f, (float)0, (float)0, 0).uv(0, 0).endVertex();
        //bufferbuilder.vertex(matrix4f, (float)0, (float)100, 0).uv(0, 1).endVertex();
        //bufferbuilder.vertex(matrix4f, (float)100, (float)100, 0).uv(1, 1).endVertex();
        //bufferbuilder.vertex(matrix4f, (float)100, (float)0, 0).uv(1, 0).endVertex();
        //BufferUploader.drawWithShader(bufferbuilder.end());
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        guiGraphics.setColor(1,1,1,1);
        guiGraphics.blit(RING, 10, 10, 0, 0, 128, 128, 128, 128);
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }
}
