package com.freefish.torchesbecomesunlight.client.render.gui;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.config.ConfigHandler;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.BossEvent;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CustomBossBar {
    public static Map<Integer, CustomBossBar> customBossBars = new HashMap<>();
    static {
        customBossBars.put(0, new CustomBossBar(
                new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/gui/boss_bar/snownova_bar_base.png"),
                new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/gui/boss_bar/snownova_bar_overlay.png"),
                10, 32, 13, -5, -22, 256, 64, 36, ChatFormatting.WHITE,183.0F, ConfigHandler.COMMON.MOBS.FROSTNOVA.customBossBarConfig.isOpenCustombossbar));

        customBossBars.put(1, new CustomBossBar(
                new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/gui/boss_bar/snownova_bar_base.png"),
                new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/gui/boss_bar/pursuer_bar_overlay.png"),
                10, 32, 17, -5, -22, 256, 64, 32, ChatFormatting.WHITE,183.0F,ConfigHandler.COMMON.MOBS.PURSUER.customBossBarConfig.isOpenCustombossbar));

        customBossBars.put(2, new CustomBossBar(
                new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/gui/boss_bar/snownova_bar_base.png"),
                new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/gui/boss_bar/patriot_bar_overlay.png"),
                10, 32, 13, -5, -22, 256, 64, 32, ChatFormatting.WHITE,183.0F,ConfigHandler.COMMON.MOBS.PATRIOT.customBossBarConfig.isOpenCustombossbar));

        customBossBars.put(3, new CustomBossBar(
                new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/gui/boss_bar/snownova_bar_base_2.png"),
                new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/gui/boss_bar/snownova_bar_overlay_2.png"),
                10, 32, -6, -5, -22, 256, 64, 10, ChatFormatting.WHITE,73.0F,ConfigHandler.COMMON.MOBS.FROSTNOVA.customBossBarConfig.isOpenCustombossbar));

        customBossBars.put(4, new CustomBossBar(
                new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/gui/boss_bar/snownova_bar_base.png"),
                new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/gui/boss_bar/gun_patriot_bar_overlay.png"),
                10, 32, 13, -5, -22, 256, 64, 32, ChatFormatting.WHITE,183.0F,ConfigHandler.COMMON.MOBS.GUN_KNIGHT.customBossBarConfig.isOpenCustombossbar));
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
    private final float trueWidth;

    private final int verticalIncrement;

    private final ChatFormatting textColor;

    private final Supplier<Boolean> configOpenCustom;

    public CustomBossBar(ResourceLocation baseTexture, ResourceLocation overlayTexture, int baseHeight, int baseTextureHeight, int baseOffsetY, int overlayOffsetX, int overlayOffsetY, int overlayWidth, int overlayHeight, int verticalIncrement, ChatFormatting textColor,float trueWidth,Supplier<Boolean> supplier) {
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
        this.trueWidth = trueWidth;
        this.textColor = textColor;
        this.configOpenCustom = supplier;
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

    public Supplier<Boolean> getConfigOpenCustom() {
        return configOpenCustom;
    }


    public void renderBossBar(CustomizeGuiOverlayEvent.BossEventProgress event,int type) {
        GuiGraphics guiGraphics = event.getGuiGraphics();
        int y = event.getY();
        int i = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int j = y - 9;
        Minecraft.getInstance().getProfiler().push("customBossBarBase");

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, getBaseTexture());

        int offX = type==3 ? 55:0;

        drawBar(guiGraphics, event.getX() + 1+offX, y + getBaseOffsetY(), event.getBossEvent());
        Component component = event.getBossEvent().getName().copy().withStyle(getTextColor());
        Minecraft.getInstance().getProfiler().pop();

        int l = Minecraft.getInstance().font.width(component);
        int i1 = i / 2 - l / 2;
        int j1 = j;
        if(type!=3)
            guiGraphics.drawString(Minecraft.getInstance().font, component, i1, j1, 16777215);

        if (hasOverlay()) {
            Minecraft.getInstance().getProfiler().push("customBossBarOverlay");
            RenderSystem.setShaderTexture(0, getOverlayTexture());
            event.getGuiGraphics().blit(getOverlayTexture(), event.getX() + 1 + getOverlayOffsetX()+offX, y + getOverlayOffsetY() + getBaseOffsetY(), 0, 0, getOverlayWidth(), getOverlayHeight(), getOverlayWidth(), getOverlayHeight());
            Minecraft.getInstance().getProfiler().pop();
        }

        event.setIncrement(getVerticalIncrement());
    }

    private void drawBar(GuiGraphics guiGraphics, int x, int y, BossEvent event) {
        guiGraphics.blit(getBaseTexture(), x, y, 0, 0, (int)trueWidth-1, getBaseHeight(), 256, getBaseTextureHeight());
        int i = (int)(event.getProgress() * trueWidth);
        if (i > 0) {
            guiGraphics.blit(getBaseTexture(), x, y, 0, getBaseHeight(), i, getBaseHeight(), 256, getBaseTextureHeight());
        }
    }
}
