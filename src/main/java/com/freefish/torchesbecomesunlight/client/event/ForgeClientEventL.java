package com.freefish.torchesbecomesunlight.client.event;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.entity.player.GeckoFirstPersonRenderer;
import com.freefish.torchesbecomesunlight.client.render.entity.player.GeckoPlayer;
import com.freefish.torchesbecomesunlight.client.render.entity.player.GeckoRenderPlayer;
import com.freefish.torchesbecomesunlight.client.render.gui.CustomBossBar;
import com.freefish.torchesbecomesunlight.client.render.gui.recipebook.RecipeCategories;
import com.freefish.torchesbecomesunlight.client.render.layer.DialogueIconLayer;
import com.freefish.torchesbecomesunlight.client.render.model.player.ModelGeckoPlayerFirstPerson;
import com.freefish.torchesbecomesunlight.client.render.model.player.ModelGeckoPlayerThirdPerson;
import com.freefish.torchesbecomesunlight.client.render.util.IceRenderer;
import com.freefish.torchesbecomesunlight.server.ability.AbilityHandler;
import com.freefish.torchesbecomesunlight.server.capability.AbilityCapability;
import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.FrozenCapability;
import com.freefish.torchesbecomesunlight.server.capability.PlayerCapability;
import com.freefish.torchesbecomesunlight.server.config.ConfigHandler;
import com.freefish.torchesbecomesunlight.server.entity.IDialogueEntity;
import com.freefish.torchesbecomesunlight.server.entity.effect.dialogueentity.DialogueEntity;
import com.freefish.torchesbecomesunlight.server.entity.effect.EntityCameraShake;
import com.freefish.torchesbecomesunlight.server.event.ServerNetwork;
import com.freefish.torchesbecomesunlight.server.event.packet.toserver.MiddelClickMessage;
import com.freefish.torchesbecomesunlight.server.story.dialogue.Dialogue;
import com.freefish.torchesbecomesunlight.server.story.dialogue.DialogueTrigger;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import com.freefish.torchesbecomesunlight.server.util.storage.ClientStorage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import org.joml.Matrix4f;
import software.bernie.geckolib.event.GeoRenderEvent;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public enum ForgeClientEventL {
    INSTANCE;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onHandRender(RenderHandEvent event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        boolean shouldAnimate = false;
        AbilityCapability.IAbilityCapability abilityCapability = AbilityHandler.INSTANCE.getAbilityCapability(player);
        if (abilityCapability != null) shouldAnimate = abilityCapability.getActiveAbility() != null;
        if (shouldAnimate) {
            PlayerCapability.IPlayerCapability playerCapability = CapabilityHandle.getCapability(player, CapabilityHandle.PLAYER_CAPABILITY);
            if (playerCapability != null) {
                GeckoPlayer.GeckoPlayerFirstPerson geckoPlayer = GeckoFirstPersonRenderer.GECKO_PLAYER_FIRST_PERSON;
                if (geckoPlayer != null) {
                    ModelGeckoPlayerFirstPerson geckoFirstPersonModel = (ModelGeckoPlayerFirstPerson) geckoPlayer.getModel();
                    GeckoFirstPersonRenderer firstPersonRenderer = (GeckoFirstPersonRenderer) geckoPlayer.getPlayerRenderer();

                    if (geckoFirstPersonModel != null && firstPersonRenderer != null) {
                        if (!geckoFirstPersonModel.isUsingSmallArms() && ((AbstractClientPlayer) player).getModelName().equals("slim")) {
                            firstPersonRenderer.setSmallArms();
                        }
                        event.setCanceled(true);

                        if (event.isCanceled()) {
                            float delta = event.getPartialTick();
                            float f1 = Mth.lerp(delta, player.xRotO, player.getXRot());
                            firstPersonRenderer.renderItemInFirstPerson((AbstractClientPlayer) player, f1, delta, event.getHand(), event.getSwingProgress(), event.getItemStack(), event.getEquipProgress(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight(), geckoPlayer);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void renderLivingEvent(RenderLivingEvent.Pre<? extends LivingEntity, ? extends EntityModel<? extends LivingEntity>> event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (player == null) return;
            float delta = event.getPartialTick();
            AbilityCapability.IAbilityCapability abilityCapability = AbilityHandler.INSTANCE.getAbilityCapability(player);
//        shouldAnimate = (player.ticksExisted / 20) % 2 == 0;
            if (abilityCapability != null && abilityCapability.getActiveAbility() != null) {
                PlayerCapability.IPlayerCapability playerCapability = CapabilityHandle.getCapability(event.getEntity(), CapabilityHandle.PLAYER_CAPABILITY);
                if (playerCapability != null) {
                    GeckoPlayer.GeckoPlayerThirdPerson geckoPlayer = playerCapability.getGeckoPlayer();
                    if (geckoPlayer != null) {
                        ModelGeckoPlayerThirdPerson geckoPlayerModel = (ModelGeckoPlayerThirdPerson) geckoPlayer.getModel();
                        GeckoRenderPlayer animatedPlayerRenderer = (GeckoRenderPlayer) geckoPlayer.getPlayerRenderer();

                        if (geckoPlayerModel != null && animatedPlayerRenderer != null) {
                            event.setCanceled(true);

                            if (event.isCanceled()) {
                                animatedPlayerRenderer.render((AbstractClientPlayer) event.getEntity(), event.getEntity().getYRot(), delta, event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight(), geckoPlayer);
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START || event.player == null) {
            return;
        }
        Player player = event.player;
        PlayerCapability.IPlayerCapability playerCapability = CapabilityHandle.getCapability(player, CapabilityHandle.PLAYER_CAPABILITY);
        if (playerCapability != null && event.side == LogicalSide.CLIENT) {
            GeckoPlayer geckoPlayer = playerCapability.getGeckoPlayer();
            if (geckoPlayer != null) geckoPlayer.tick();
            if (player == Minecraft.getInstance().player) GeckoFirstPersonRenderer.GECKO_PLAYER_FIRST_PERSON.tick();
        }
    }

    @SubscribeEvent
    public void onGeoArmorRender(GeoRenderEvent.Armor.Pre geoRenderEvent){
        LocalPlayer player = Minecraft.getInstance().player;
        if(player!=null){
            PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(player, CapabilityHandle.PLAYER_CAPABILITY);
            AbilityCapability.IAbilityCapability capability1 = CapabilityHandle.getCapability(player, CapabilityHandle.ABILITY_CAPABILITY);
            if(capability!=null&&capability1!=null&&capability1.getActiveAbility()!=null){
                GeckoPlayer.GeckoPlayerThirdPerson geckoPlayer = capability.getGeckoPlayer();
                if(geckoPlayer!=null){
                    if(geckoPlayer.getModel() instanceof ModelGeckoPlayerThirdPerson model){
                        MathUtils.copyAnimation(model,geoRenderEvent.getRenderer());
                    }
                }
            }
        }
    }

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

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderBossBar(CustomizeGuiOverlayEvent.BossEventProgress event){
        int bossRegistryName = TorchesBecomeSunlight.bossBarRegistryNames.getOrDefault(event.getBossEvent().getId(), -1);
        if (bossRegistryName == -1) return;
        CustomBossBar customBossBar = CustomBossBar.customBossBars.getOrDefault(bossRegistryName, null);

        if(bossRegistryName == 3&& !customBossBar.getConfigOpenCustom().get()) event.setCanceled(true);

        if (customBossBar == null || !customBossBar.getConfigOpenCustom().get()) return;

        event.setCanceled(true);
        customBossBar.renderBossBar(event,bossRegistryName);
    }

    @SubscribeEvent
    public void onPostRenderLiving(RenderLivingEvent.Post event) {
        LivingEntity entity = event.getEntity();
        FrozenCapability.IFrozenCapability data = CapabilityHandle.getCapability(entity, CapabilityHandle.FROZEN_CAPABILITY);
        if(data!=null&&data.getFrozen()){
            IceRenderer.render(event.getEntity(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight(), data.getFrozenTick());
        }
    }

    @SubscribeEvent
    public void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        if (event.isCanceled()) return;

        double scrollAmount = event.getScrollDelta();
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if(player == null) return;

        Level level = player.level();

        List<DialogueEntity> entitiesOfClass = level.getEntitiesOfClass(DialogueEntity.class, player.getBoundingBox().inflate(5));
        DialogueEntity dialogueEntity = MathUtils.getClosestEntity(player, entitiesOfClass);
        if(dialogueEntity != null && dialogueEntity.getDialogue() != null && dialogueEntity.getDialogue().getOptions()!=null&& !dialogueEntity.getDialogue().getOptions().isEmpty()) {
            int len = dialogueEntity.getOptions();
            int number = dialogueEntity.getNumber();
            if (scrollAmount > 0) {
                if (number >= len - 1)
                    dialogueEntity.setNumber(0);
                else
                    dialogueEntity.setNumber(dialogueEntity.getNumber() + 1);
            } else if (scrollAmount < 0) {
                if (number <= 0)
                    dialogueEntity.setNumber(len - 1);
                else
                    dialogueEntity.setNumber(dialogueEntity.getNumber() - 1);
            }
            dialogueEntity.resetFloat();
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onMiddleClick(InputEvent.MouseButton event) {
        if (event.isCanceled()) return;
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if(player == null) return;

        Level level = player.level();
        List<DialogueEntity> entities = level.getEntitiesOfClass(DialogueEntity.class,player.getBoundingBox().inflate(9));
        DialogueEntity dialogueEntity = null;
        for (DialogueEntity dialogueEntity1 :entities){
            if(dialogueEntity1.getChatEntities()!=null&&dialogueEntity1.getChatEntities().length!=0&&dialogueEntity1.getChatEntities()[0]==player){
                dialogueEntity = dialogueEntity1;
            }
        }
        if(dialogueEntity != null && dialogueEntity.getDialogue() != null && dialogueEntity.getDialogue().getOptions()!=null){
            if (event.getButton() == 2 && event.getAction() == 1) {
                ServerNetwork.toServerMessage(new MiddelClickMessage(player.getId()));
                dialogueEntity.setNumber(0);
                event.setCanceled(true);
            }
        }
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
}
