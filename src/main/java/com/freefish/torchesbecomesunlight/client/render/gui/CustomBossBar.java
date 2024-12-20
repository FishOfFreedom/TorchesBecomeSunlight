package com.freefish.torchesbecomesunlight.client.render.gui;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.BossEvent;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class CustomBossBar {
    public static Map<ResourceLocation, CustomBossBar> customBossBars = new HashMap<>();
    static {
        customBossBars.put(ForgeRegistries.ENTITY_TYPES.getKey(EntityHandle.FROST_NOVA.get()), new CustomBossBar(
                new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/gui/boss_bar/snownova_bar_base.png"),
                new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/gui/boss_bar/snownova_bar_overlay.png"),
                10, 32, 13, -5, -22, 256, 64, 32, ChatFormatting.WHITE));

        customBossBars.put(ForgeRegistries.ENTITY_TYPES.getKey(EntityHandle.PURSUER.get()), new CustomBossBar(
                new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/gui/boss_bar/snownova_bar_base.png"),
                new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/gui/boss_bar/pursuer_bar_overlay.png"),
                10, 32, 13, -1, -23, 256, 64,  32, ChatFormatting.BLACK));
    }

    private final ResourceLocation baseTexture;
    private final ResourceLocation overlayTexture;
    private final boolean hasOverlay;

    private final int baseHeight;
    private final int baseTextureHeight;
    private final int baseOffsetY;
    private final int overlayOffsetX;
    private final int overlayOffsetY;
    private final int overlayWidth;
    private final int overlayHeight;

    private final int verticalIncrement;

    private final ChatFormatting textColor;

    public CustomBossBar(ResourceLocation baseTexture, ResourceLocation overlayTexture, int baseHeight, int baseTextureHeight, int baseOffsetY, int overlayOffsetX, int overlayOffsetY, int overlayWidth, int overlayHeight, int verticalIncrement, ChatFormatting textColor) {
        this.baseTexture = baseTexture;
        this.overlayTexture = overlayTexture;
        this.hasOverlay = overlayTexture != null;
        this.baseHeight = baseHeight;
        this.baseTextureHeight = baseTextureHeight;
        this.baseOffsetY = baseOffsetY;
        this.overlayOffsetX = overlayOffsetX;
        this.overlayOffsetY = overlayOffsetY;
        this.overlayWidth = overlayWidth;
        this.overlayHeight = overlayHeight;
        this.verticalIncrement = verticalIncrement;
        this.textColor = textColor;
    }

    public ResourceLocation getBaseTexture() {
        return baseTexture;
    }

    public ResourceLocation getOverlayTexture() {
        return overlayTexture;
    }

    public boolean hasOverlay() {
        return hasOverlay;
    }

    public int getBaseHeight() {
        return baseHeight;
    }

    public int getBaseTextureHeight() {
        return baseTextureHeight;
    }

    public int getBaseOffsetY() {
        return baseOffsetY;
    }

    public int getOverlayOffsetX() {
        return overlayOffsetX;
    }

    public int getOverlayOffsetY() {
        return overlayOffsetY;
    }

    public int getOverlayWidth() {
        return overlayWidth;
    }

    public int getOverlayHeight() {
        return overlayHeight;
    }

    public int getVerticalIncrement() {
        return verticalIncrement;
    }

    public ChatFormatting getTextColor() {
        return textColor;
    }

    public void renderBossBar(CustomizeGuiOverlayEvent.BossEventProgress event) {
        GuiGraphics guiGraphics = event.getGuiGraphics();
        int y = event.getY();
        int i = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int j = y - 9;
        Minecraft.getInstance().getProfiler().push("customBossBarBase");

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, getBaseTexture());
        drawBar(guiGraphics, event.getX() + 1, y + getBaseOffsetY(), event.getBossEvent());
        Component component = event.getBossEvent().getName().copy().withStyle(getTextColor());
        Minecraft.getInstance().getProfiler().pop();

        int l = Minecraft.getInstance().font.width(component);
        int i1 = i / 2 - l / 2;
        int j1 = j;
        guiGraphics.drawString(Minecraft.getInstance().font, component, i1, j1, 16777215);

        if (hasOverlay()) {
            Minecraft.getInstance().getProfiler().push("customBossBarOverlay");
            RenderSystem.setShaderTexture(0, getOverlayTexture());
            event.getGuiGraphics().blit(getOverlayTexture(), event.getX() + 1 + getOverlayOffsetX(), y + getOverlayOffsetY() + getBaseOffsetY(), 0, 0, getOverlayWidth(), getOverlayHeight(), getOverlayWidth(), getOverlayHeight());
            Minecraft.getInstance().getProfiler().pop();
        }

        event.setIncrement(getVerticalIncrement());
    }

    private void drawBar(GuiGraphics guiGraphics, int x, int y, BossEvent event) {
        guiGraphics.blit(getBaseTexture(), x, y, 0, 0, 182, getBaseHeight(), 256, getBaseTextureHeight());
        int i = (int)(event.getProgress() * 183.0F);
        if (i > 0) {
            guiGraphics.blit(getBaseTexture(), x, y, 0, getBaseHeight(), i, getBaseHeight(), 256, getBaseTextureHeight());
        }
    }
}
