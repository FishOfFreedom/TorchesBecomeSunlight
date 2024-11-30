package com.freefish.torchesbecomesunlight.client.event;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.gui.CustomBossBar;
import com.freefish.torchesbecomesunlight.client.render.gui.NeedDialogue;
import com.freefish.torchesbecomesunlight.client.render.util.IceRenderer;
import com.freefish.torchesbecomesunlight.client.shader.ShaderRegistry;
import com.freefish.torchesbecomesunlight.server.capability.frozen.FrozenCapabilityProvider;
import com.freefish.torchesbecomesunlight.server.capability.story.PlayerStoryStoneProvider;
import com.freefish.torchesbecomesunlight.server.config.ConfigHandler;
import com.freefish.torchesbecomesunlight.server.entity.effect.dialogueentity.DialogueEntity;
import com.freefish.torchesbecomesunlight.server.entity.effect.EntityCameraShake;
import com.freefish.torchesbecomesunlight.server.story.dialogue.Dialogue;
import com.freefish.torchesbecomesunlight.server.story.dialogue.DialogueTrigger;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import com.freefish.torchesbecomesunlight.server.util.storage.ClientStorage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

import java.awt.*;
import java.io.IOException;
import java.util.List;

@Mod.EventBusSubscriber(modid = TorchesBecomeSunlight.MOD_ID, value = Dist.CLIENT)
public enum ForgeClientEventL {
    INSTANCE;
    private static final ResourceLocation DO = new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/gui/dialogue_op.png");

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void fogRender(ViewportEvent.RenderFog event) {
        //todo
        if (event.isCanceled()) {
            return;
        }
        float demonRadio = ClientStorage.INSTANCE.getDemon((float) event.getPartialTick());
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

        if(demonRadio>=60&&ConfigHandler.CLIENT.demonRender.get()) {
            event.setNearPlaneDistance(Mth.lerp(Math.min(1,(demonRadio-60)/60f),event.getNearPlaneDistance(),8));
            event.setFarPlaneDistance(Mth.lerp(Math.min(1,(demonRadio-60)/60f),event.getFarPlaneDistance(),16));
            event.setCanceled(true);
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
        if(demonRadio>61&&ConfigHandler.CLIENT.demonRender.get()){
            float demon = ClientStorage.INSTANCE.getDemon((float) event.getPartialTick());
            float min = Math.min(1, (demon - 62) / 60f);
            event.setRed(  Mth.lerp(min,event.getRed(),0));
            event.setGreen(Mth.lerp(min,event.getGreen(),0));
            event.setBlue( Mth.lerp(min,event.getBlue(),0));
        }
    }

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
            int skipRadio = ClientStorage.INSTANCE.getSkip(1);

            List<DialogueEntity> entities = player.level().getEntitiesOfClass(DialogueEntity.class,player.getBoundingBox().inflate(5));
            DialogueEntity dialogueEntity = MathUtils.getClosestEntity(player,entities);
            if(dialogueEntity != null) {
                int wight = minecraft.getWindow().getGuiScaledWidth();
                Font font = minecraft.font;
                String s = Component.translatable("torchesbecomesunlight.skip").getString();
                int wightFont = font.width(s);
                int heighFont = font.lineHeight;

                event.getGuiGraphics().setColor(1,1,1,1);
                event.getGuiGraphics().fill(wight-wightFont, 0 ,wight-wightFont+ (int)(MathUtils.easeOutCubic((skipRadio/41f))*wightFont), heighFont , 0x08000000);
                event.getGuiGraphics().drawString(font,s,wight-wightFont,0, 0xFFFFFF);

                Dialogue dialogue = dialogueEntity.getDialogue();
                if(dialogue != null) {
                    renderChatCenter(event,dialogueEntity);
                    if(dialogue.getOptions() != null && !dialogue.getOptions().isEmpty() && dialogueEntity.getFloatScale()==20)
                        renderOptionsCenter(event,dialogueEntity);
                }
            }
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
        GuiGraphics guiGraphics = event.getGuiGraphics();
        float partialTick = event.getPartialTick();
        //i18
        String message = cuttingString(dialogueEntity.getDialogue().getMessage(),MathUtils.easeOutCubic(Math.min(1,2f*dialogueEntity.getDialogueScale())));
        Entity chatEntity = dialogueEntity.getChatEntities()[dialogueEntity.getDialogue().getSpeakerNumber()];
        String name = (chatEntity.hasCustomName()
                ? ChatFormatting.ITALIC + chatEntity.getCustomName().getString()
                : chatEntity.getDisplayName().getString());

        int fontHeight = font.lineHeight;
        int fontWidth = font.width(message);
        int width = minecraft.getWindow().getGuiScaledWidth() / 4;
        int height = minecraft.getWindow().getGuiScaledHeight() / 5 * 4;
//in 1s
        float len;
        if(dialogueEntity.getFloatScale()==20)
            len = dialogueEntity.getOptions()*fontHeight;
        else
            len = Mth.lerp(MathUtils.easeOutCubic((float) ((dialogueEntity.getFloatScale()+ partialTick)/ 20.0)) ,dialogueEntity.getOldOptions(),dialogueEntity.getOptions())*fontHeight;
        guiGraphics.pose().pushPose();
        Matrix4f pose = guiGraphics.pose().last().pose();
        pose.translate(0,- len,0);
        guiGraphics.fill(width, height , width + fontWidth, height + fontHeight , 0x08000000);
        guiGraphics.drawString(font,  message, width, height , 0xFFFFFF);

        guiGraphics.drawString(font, "["+name+"]", width, height -fontHeight, 0xFFFFFF);
        guiGraphics.pose().popPose();
    }

    private static void renderOptionsCenter(RenderGuiOverlayEvent.Pre event,DialogueEntity dialogueEntity){
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        int fontHeight = font.lineHeight;
        GuiGraphics guiGraphics = event.getGuiGraphics();

        List<String> messages = dialogueEntity.getDialogue().getOptions().stream().map(DialogueTrigger::getContent).toList();
        int number = dialogueEntity.getNumber();
        float scale1 = Math.min(1,(dialogueEntity.getSpeakTickCount()-20)/20f);
        int len = messages.size();

        int width = minecraft.getWindow().getGuiScaledWidth() / 4;

        float floatOp = dialogueEntity.getFloatOp(event.getPartialTick());
        int height1 = minecraft.getWindow().getGuiScaledHeight() / 5 * 4;

        guiGraphics.pose().pushPose();
        Matrix4f pose = guiGraphics.pose().last().pose();
        pose.translate(0,-floatOp*fontHeight,0);
        RenderSystem.enableBlend();
        guiGraphics.setColor(1,1,1,0.8f);
        guiGraphics.blit(DO,width,height1,0,0,fontHeight*4,fontHeight,fontHeight*4,fontHeight);
        RenderSystem.disableBlend();
        guiGraphics.pose().popPose();
        guiGraphics.setColor(1,1,1,1);

        for(int i=0;i<len;i++){
            int height = minecraft.getWindow().getGuiScaledHeight() / 5 * 4 - i * fontHeight;
             scale1 = scale1*(len*0.2f+1);
            String s = cuttingString(dialogueEntity.getDialogue().getOptions().get(i).getContent(),MathUtils.easeOutCubic(Math.min(1,2f*scale1)));
            if(number == i)
                guiGraphics.drawString(font, s, width, height, 0xFFFF00);
            else
                guiGraphics.drawString(font, s, width, height, 0xFFFFFF);
        }

    }

    private static String cuttingString(String input , double fraction){
        if(fraction > 1) fraction = 1;
        int cutIndex = Math.round((int)(input.length() * fraction));
        return input.substring(0, cutIndex);
    }
}
