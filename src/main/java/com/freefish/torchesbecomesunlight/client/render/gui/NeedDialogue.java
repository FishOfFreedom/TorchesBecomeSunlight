package com.freefish.torchesbecomesunlight.client.render.gui;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;

public class NeedDialogue {
    private static ResourceLocation RING = new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/gui/fx/skill_outline.png");

    public static void renderGui(RenderGuiOverlayEvent.Pre event) {
        GuiGraphics guiGraphics = event.getGuiGraphics();
        RenderSystem.disableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        //RenderSystem.setShaderTexture(0, RING);
        drawGui(guiGraphics);
        RenderSystem.enableDepthTest();
    }

    private static void drawGui(GuiGraphics guiGraphics) {
        guiGraphics.blit(RING, 10, 10, 0, 0, 128, 128, 128, 128);
    }
}
