package com.freefish.torchesbecomesunlight.client.event;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.enviroment.DemonRenderInfo;
import com.freefish.torchesbecomesunlight.client.render.gui.CustomBossBar;
import com.freefish.torchesbecomesunlight.client.render.gui.NeedDialogue;
import com.freefish.torchesbecomesunlight.client.render.util.IceRenderer;
import com.freefish.torchesbecomesunlight.client.render.enviroment.SkyRenderer;
import com.freefish.torchesbecomesunlight.client.shader.ShaderRegistry;
import com.freefish.torchesbecomesunlight.server.capability.frozen.FrozenCapabilityProvider;
import com.freefish.torchesbecomesunlight.server.capability.story.PlayerStoryStoneProvider;
import com.freefish.torchesbecomesunlight.server.entity.dialogueentity.DialogueEntity;
import com.freefish.torchesbecomesunlight.server.entity.help.EntityCameraShake;
import com.freefish.torchesbecomesunlight.server.story.dialogue.Dialogue;
import com.freefish.torchesbecomesunlight.server.story.dialogue.DialogueTrigger;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import com.freefish.torchesbecomesunlight.server.util.storage.ClientStorage;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Mod.EventBusSubscriber(modid = TorchesBecomeSunlight.MOD_ID, value = Dist.CLIENT)
public enum ForgeClientEventL {
    INSTANCE;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void fogRender(ViewportEvent.RenderFog event) {
        //todo
        if (event.isCanceled()) {
            return;
        }
        int demonRadio = ClientStorage.INSTANCE.demonRadio;
        if(ClientStorage.INSTANCE.isBossActive()) {
            float nearness = 0.5f;
            float primordialBossAmount = 0.5f;
            boolean flag = Math.abs(nearness) - 1.0F < 0.01F;
            if (primordialBossAmount > 0.0F) {
                flag = true;
            }
            if (flag) {
                event.setCanceled(true);
                event.setNearPlaneDistance(1);
                event.setFarPlaneDistance(64);
            }
        }
        if(demonRadio>30) {
            event.setCanceled(true);
            event.setNearPlaneDistance(16-(demonRadio-30)/9.0f);
            event.setFarPlaneDistance(16);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void fogColor(ViewportEvent.ComputeFogColor event) {
        if(ClientStorage.INSTANCE.isBossActive()) {
            event.setRed(0.6f);
            event.setGreen(0.6f);
            event.setBlue(0.6f);
        }
        int demonRadio = ClientStorage.INSTANCE.demonRadio;
        if(demonRadio>0){
            event.setRed(0);
            event.setGreen(0);
            event.setBlue(0);
        }
    }

    //SubscribeEvent
    //ublic void onRegisterShader(RegisterShadersEvent event) throws IOException {
    //   event.registerShader(new ShaderInstance(event.getResourceProvider(), new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"rendertype_starlight_portal"), DefaultVertexFormat.POSITION_COLOR_TEX), ShaderRegistry::setRenderTypeStarlightPortal);
    //

    public static void registerShaders(final RegisterShadersEvent e) {
        try {
            e.registerShader(new ShaderInstance(e.getResourceProvider(), new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"rendertype_starlight_portal"), DefaultVertexFormat.BLOCK), ShaderRegistry::setRenderTypeStarlightPortal);
            e.registerShader(new ShaderInstance(e.getResourceProvider(), new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"rendertype_demon"), DefaultVertexFormat.POSITION), ShaderRegistry::setRenderTypeDemon);
            e.registerShader(new ShaderInstance(e.getResourceProvider(), new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"rendertype_hole"), DefaultVertexFormat.BLOCK), ShaderRegistry::setRenderTypeHole);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderBossBar(CustomizeGuiOverlayEvent.BossEventProgress event){
        ResourceLocation bossRegistryName = TorchesBecomeSunlight.bossBarRegistryNames.getOrDefault(event.getBossEvent().getId(), null);
        if (bossRegistryName == null) return;
        CustomBossBar customBossBar = CustomBossBar.customBossBars.getOrDefault(bossRegistryName, null);
        if (customBossBar == null) return;

        event.setCanceled(true);
        customBossBar.renderBossBar(event);
    }

    @SubscribeEvent
    public void onPostRenderLiving(RenderLivingEvent.Post event) {
        LivingEntity entity = event.getEntity();
        entity.getCapability(FrozenCapabilityProvider.FROZEN_CAPABILITY).ifPresent(frozenCapability -> {
            if(frozenCapability.isFrozen){
                IceRenderer.render(event.getEntity(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight(), frozenCapability.frozenTicks);
            }
        });
    }

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if(player == null || !minecraft.isWindowActive())
            return;

        player.getCapability(PlayerStoryStoneProvider.PLAYER_STORY_STONE_CAPABILITY).ifPresent(playerStoryStone -> {
            List<DialogueEntity> entities = player.level().getEntitiesOfClass(DialogueEntity.class,player.getBoundingBox().inflate(5));
            DialogueEntity dialogueEntity = MathUtils.getClosestEntity(player,entities);
            if(dialogueEntity != null) {
                Dialogue dialogue = dialogueEntity.getDialogue();
                if(dialogue != null) {
                    renderChatCenter(event,dialogueEntity);
                    if(dialogue.getOptions() != null && dialogueEntity.getDialogueScale()>=1)
                        renderOptionsCenter(event,dialogueEntity);
                }
            }
            if(playerStoryStone.getDialogueTime()>=40)
                NeedDialogue.renderGui(event);
        });
    }

    @SubscribeEvent
    public void onSetupCamera(ViewportEvent.ComputeCameraAngles event) {
        Player player = Minecraft.getInstance().player;
        float delta = Minecraft.getInstance().getFrameTime();
        float ticksExistedDelta = player.tickCount + delta;
        if (player != null) {
            float shakeAmplitude = 0;
            for (EntityCameraShake cameraShake : player.level().getEntitiesOfClass(EntityCameraShake.class, player.getBoundingBox().inflate(20, 20, 20))) {
                if (cameraShake.distanceTo(player) < cameraShake.getRadius()) {
                    shakeAmplitude += cameraShake.getShakeAmount(player, delta);
                }
            }
            if (shakeAmplitude > 1.0f) shakeAmplitude = 1.0f;
            event.setPitch((float) (event.getPitch() + shakeAmplitude * Math.cos(ticksExistedDelta * 3 + 2) * 25));
            event.setYaw((float) (event.getYaw() + shakeAmplitude * Math.cos(ticksExistedDelta * 5 + 1) * 25));
            event.setRoll((float) (event.getRoll() + shakeAmplitude * Math.cos(ticksExistedDelta * 4) * 25));
        }
    }

    private static void renderChatCenter(RenderGuiOverlayEvent.Pre event,DialogueEntity dialogueEntity){
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        String message = cuttingString(dialogueEntity.getDialogue().getMessage(),MathUtils.fade(dialogueEntity.getDialogueScale()));
        Entity chatEntity = dialogueEntity.getChatEntities()[dialogueEntity.getDialogue().getSpeakerNumber()];
        String name = (chatEntity.hasCustomName()
                ? ChatFormatting.ITALIC + chatEntity.getCustomName().getString()
                : chatEntity.getDisplayName().getString())+": ";

        int fontHeight = font.lineHeight;
        int fontWidth = font.width(message);
        int width = minecraft.getWindow().getGuiScaledWidth() / 4;
        int height = minecraft.getWindow().getGuiScaledHeight() / 5 * 4;

        int recentlyOptions;
        if(dialogueEntity.getDialogue().getOptions() !=null) recentlyOptions=dialogueEntity.getDialogue().getOptions().size();
        else recentlyOptions = 0;
        int min = Math.min(recentlyOptions,dialogueEntity.getOldOptions());
        int max = Math.max(recentlyOptions,dialogueEntity.getOldOptions());

        int len;
        if((max - min)!=0) {
            double i =(dialogueEntity.getFloatScale() - min *10.0) / ((max - min) * 10);
            len = (int)((min + MathUtils.fade(i)* (max - min))*fontHeight);
        }
        else
            len = dialogueEntity.getOldOptions()*fontHeight;
        event.getGuiGraphics().fill(width, height - len, width + fontWidth + font.width(name), height + fontHeight - len, 0x08000000);
        event.getGuiGraphics().drawString(font, name + message, width, height - len, 0xFFFFFF);

    }

    private static void renderOptionsCenter(RenderGuiOverlayEvent.Pre event,DialogueEntity dialogueEntity){
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        int fontHeight = font.lineHeight;
        List<String> messages = dialogueEntity.getDialogue().getOptions().stream().map(DialogueTrigger::getContent).toList();
        int number = dialogueEntity.getNumber();

        int len = messages.size();

        for(int i=0;i<len;i++){
            int fontWidth = font.width(messages.get(i));
            int width = minecraft.getWindow().getGuiScaledWidth() / 4;
            int height = minecraft.getWindow().getGuiScaledHeight() / 5 * 4 - i * fontHeight;
            event.getGuiGraphics().fill(width,height,width+fontWidth,height+fontHeight,0x08000000);
            if(number == i)
                event.getGuiGraphics().drawString(font, messages.get(i), width, height, 0x0000FF);
            else
                event.getGuiGraphics().drawString(font, messages.get(i), width, height, 0xFFFFFF);
        }

    }

    private static String cuttingString(String input , double fraction){
        if(fraction > 1) fraction = 1;
        int cutIndex = Math.round((int)(input.length() * fraction));
        return input.substring(0, cutIndex);
    }
}
