package com.freefish.torchesbecomesunlight.server.event;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.mixin.MixinStructureTemplatePool;
import com.freefish.torchesbecomesunlight.server.ability.Ability;
import com.freefish.torchesbecomesunlight.server.ability.AbilityHandler;
import com.freefish.torchesbecomesunlight.server.ability.PlayerAbility;
import com.freefish.torchesbecomesunlight.server.capability.AbilityCapability;
import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.FrozenCapability;
import com.freefish.torchesbecomesunlight.server.capability.PlayerCapability;
import com.freefish.torchesbecomesunlight.server.effect.Collapsal;
import com.freefish.torchesbecomesunlight.server.entity.effect.dialogueentity.DialogueEntity;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.shield.Patriot;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster.FrostNova;
import com.freefish.torchesbecomesunlight.server.entity.ursus.Pursuer;
import com.freefish.torchesbecomesunlight.server.event.packet.toserver.DialogueTriggerMessage;
import com.freefish.torchesbecomesunlight.server.event.packet.toserver.MiddelClickMessage;
import com.freefish.torchesbecomesunlight.server.init.generator.CustomResourceKey;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import com.freefish.torchesbecomesunlight.server.util.storage.ClientStorage;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class EventListener {
    @SubscribeEvent(priority = EventPriority.LOW)
    public void spawnEntityLimit(MobSpawnEvent.FinalizeSpawn event){
        Mob entity = event.getEntity();
        if(event.getSpawnType()== MobSpawnType.NATURAL) {
            ServerLevel level = (ServerLevel) event.getLevel();
            if (entity instanceof FrostNova) {
                level.getEntities().getAll().forEach(entity1 -> {
                    if (entity1 instanceof FrostNova) {
                        event.setSpawnCancelled(true);
                    }
                });
            } else if (entity instanceof Patriot) {
                level.getEntities().getAll().forEach(entity1 -> {
                    if (entity1 instanceof Patriot) {
                        event.setSpawnCancelled(true);
                    }
                });
            } else if (entity instanceof Pursuer) {
                level.getEntities().getAll().forEach(entity1 -> {
                    if (entity1 instanceof Pursuer) {
                        event.setSpawnCancelled(true);
                    }
                });
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onEntityHurt(LivingHurtEvent event) {
        if (!event.isCanceled()) {
            LivingEntity entity = event.getEntity();

            if(event.getSource().is(CustomResourceKey.DEMON_ATTACK)){
                entity.invulnerableTime=0;
            }
        }
    }

    @SubscribeEvent
    public void onEffectAdded(MobEffectEvent.Added event) {
        LivingEntity entity = event.getEntity();
        MobEffect effect = event.getEffectInstance().getEffect();
        if(effect instanceof Collapsal && entity instanceof Player player){
            Iterable<ItemStack> allSlots = player.getAllSlots();
            for(ItemStack itemStack:allSlots){
                if(!itemStack.isEmpty()){
                    player.getCooldowns().addCooldown(itemStack.getItem(),100);
                }
            }
        }
    }

    @SubscribeEvent
    public void registerCommand(RegisterCommandsEvent event){
    }

    @SubscribeEvent
    public void playerClone(PlayerEvent.Clone event) {
        Player original = event.getOriginal();
        original.revive();
    }

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event){
        if (event.phase == TickEvent.Phase.START || event.player == null) {
            return;
        }
        Player player = event.player;
        Level level = player.level();

        if(level.isClientSide()){
            //todo RenderDemon
            ClientStorage.INSTANCE.update();

            int skip = ClientStorage.INSTANCE.skipRadio;
            if(ClientStorage.INSTANCE.isSkip&&skip<41){
                if(skip==40){
                    DialogueEntity dialogueEntity = MathUtils.getClosestEntity(player,level.getEntitiesOfClass(DialogueEntity.class,player.getBoundingBox().inflate(5)));
                    if(dialogueEntity!=null) {
                        ServerNetwork.toServerMessage(new DialogueTriggerMessage(dialogueEntity.getId()));
                        ClientStorage.INSTANCE.skipRadio = 0;
                    }
                }
                ClientStorage.INSTANCE.skipRadio+=1;
            }
            else if(!ClientStorage.INSTANCE.isSkip&&skip>0){
                ClientStorage.INSTANCE.skipRadio-=1;
            }

            if(MathUtils.isInDemon(player)){
                if(ClientStorage.INSTANCE.demonRadio<160)ClientStorage.INSTANCE.demonRadio+=1;
            }
            else
                if(ClientStorage.INSTANCE.demonRadio>0)ClientStorage.INSTANCE.demonRadio-=1;
        }

        PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(player, CapabilityHandle.PLAYER_CAPABILITY);
        if(capability!=null) capability.tick(event);
    }

    @SubscribeEvent
    public void livingTick(LivingEvent.LivingTickEvent event){
        LivingEntity livingEntity = event.getEntity();
        FrozenCapability.IFrozenCapability frozen = CapabilityHandle.getCapability(livingEntity, CapabilityHandle.FROZEN_CAPABILITY);
        if(frozen!=null){
            if(frozen.getFrozen()) frozen.tickFrozen(livingEntity);
        }

        if (event.getEntity() != null) {
            LivingEntity entity = (LivingEntity) event.getEntity();
            AbilityCapability.IAbilityCapability abilityCapability = CapabilityHandle.getCapability(entity, CapabilityHandle.ABILITY_CAPABILITY);
            if (abilityCapability != null) {
                abilityCapability.tick(entity);
                Ability activeAbility = abilityCapability.getActiveAbility();
                if(activeAbility != null&& entity instanceof Player player){
                    if (activeAbility instanceof PlayerAbility playerAbility) {
                        if(playerAbility.heldItemMainHandOverride()==null){
                            AbilityHandler.INSTANCE.sendInterruptAbilityMessage(player, activeAbility.getAbilityType());
                        }
                        else {
                            if(!player.getMainHandItem().equals(playerAbility.heldItemMainHandOverride(),false))
                                AbilityHandler.INSTANCE.sendInterruptAbilityMessage(player, activeAbility.getAbilityType());
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onJoinWorld(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Player) {
            AbilityCapability.IAbilityCapability abilityCapability = AbilityHandler.INSTANCE.getAbilityCapability((LivingEntity) event.getEntity());
            if (abilityCapability != null) abilityCapability.instanceAbilities((LivingEntity) event.getEntity());
        }

        if (event.getEntity() instanceof Player) {
            PlayerCapability.IPlayerCapability playerCapability = CapabilityHandle.getCapability((Player) event.getEntity(), CapabilityHandle.PLAYER_CAPABILITY);
            if (playerCapability != null) playerCapability.addedToWorld(event);
        }
    }

    @SubscribeEvent
    public void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
        if (event.getHand() == InteractionHand.OFF_HAND  && event.getTarget() != null) {
            Entity target = event.getTarget();
            Player player = event.getEntity();
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
}
