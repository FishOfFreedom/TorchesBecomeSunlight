package com.freefish.torchesbecomesunlight.server.event;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.capability.frozen.FrozenCapability;
import com.freefish.torchesbecomesunlight.server.capability.frozen.FrozenCapabilityProvider;
import com.freefish.torchesbecomesunlight.server.capability.story.PlayerStoryStoneProvider;
import com.freefish.torchesbecomesunlight.server.command.GetStoryStateCommand;
import com.freefish.torchesbecomesunlight.server.command.SetStoryStateCommand;
import com.freefish.torchesbecomesunlight.server.entity.dialogueentity.DialogueEntity;
import com.freefish.torchesbecomesunlight.server.event.packet.toserver.DialogueTriggerMessage;
import com.freefish.torchesbecomesunlight.server.story.ProcessManage;
import com.freefish.torchesbecomesunlight.server.story.dialogue.DialogueTrigger;
import com.freefish.torchesbecomesunlight.server.entity.dialogueentity.quest.QuestBase;
import com.freefish.torchesbecomesunlight.server.entity.dialogueentity.quest.TalkWithEntity;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = TorchesBecomeSunlight.MOD_ID)
public class EventListener {
    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event){
        GetStoryStateCommand.register(event.getDispatcher());
        SetStoryStateCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void registerCapability(RegisterCapabilitiesEvent event){
        event.register(PlayerStoryStoneProvider.class);
        event.register(FrozenCapability.class);
    }

    @SubscribeEvent
    public static void playerClone(PlayerEvent.Clone event) {
        Player original = event.getOriginal();
        original.revive();
        original.getCapability(PlayerStoryStoneProvider.PLAYER_STORY_STONE_CAPABILITY).ifPresent(old ->{
            event.getEntity().getCapability(PlayerStoryStoneProvider.PLAYER_STORY_STONE_CAPABILITY).ifPresent(storystate ->{
                storystate.setStoryState(old.getStoryState());
            });
        });
    }

    @SubscribeEvent
    public static void playerTick(LivingEvent.LivingTickEvent event){
        if(event.getEntity() instanceof Player player){
            Level level = player.level();
            QuestBase questBase = ProcessManage.INSTANCE.getCurrentTask(player);
            if(questBase instanceof TalkWithEntity talkWithEntity) {
                List<LivingEntity> entitiesOfClass = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().deflate(4), talkWithEntity::isTalkEntity);
                for(LivingEntity entity:entitiesOfClass){
                    if(entity.distanceTo(player)<=3){
                        if(level.isClientSide)
                            level.addParticle(ParticleTypes.SMOKE,entity.getX(),entity.getY()+1,entity.getZ(),0,0,0);
                    }
                }
            }
        }
        LivingEntity livingEntity = event.getEntity();
        livingEntity.getCapability(FrozenCapabilityProvider.FROZEN_CAPABILITY).ifPresent(frozenCapability -> {
            if(frozenCapability.isFrozen){
                frozenCapability.tickFrozen(livingEntity);
            }
        });
    }

    @SubscribeEvent
    public static void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
        if (event.getHand() == InteractionHand.OFF_HAND  && event.getTarget() != null) {
            Entity target = event.getTarget();
            Player player = event.getEntity();
            QuestBase questBase = ProcessManage.INSTANCE.getCurrentTask(player);
            if(questBase instanceof TalkWithEntity talkWithEntity) {
                if(talkWithEntity.isTalkEntity(target)){
                    ProcessManage.INSTANCE.nextTask(player);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        if (event.isCanceled()) return;

        double scrollAmount = event.getScrollDelta();
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if(player == null) return;

        Level level = player.level();

        List<DialogueEntity> entitiesOfClass = level.getEntitiesOfClass(DialogueEntity.class, player.getBoundingBox().inflate(5));
        DialogueEntity dialogueEntity = MathUtils.getClosestEntity(player, entitiesOfClass);
        if(dialogueEntity != null && dialogueEntity.getDialogue() != null && dialogueEntity.getDialogue().getOptions()!=null) {
            int len = dialogueEntity.getDialogue().getOptions().size();
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
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onMiddleClick(InputEvent.MouseButton event) {
        if (event.isCanceled()) return;

        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if(player == null) return;

        Level level = player.level();

        List<DialogueEntity> entitiesOfClass = level.getEntitiesOfClass(DialogueEntity.class, player.getBoundingBox().inflate(5));
        DialogueEntity dialogueEntity = MathUtils.getClosestEntity(player, entitiesOfClass);
        if(dialogueEntity != null && dialogueEntity.getDialogue() != null && dialogueEntity.getDialogue().getOptions()!=null){
            if (event.getButton() == 2 && event.getAction() == 1) {
                int number = dialogueEntity.getNumber();
                List<DialogueTrigger> options = dialogueEntity.getDialogue().getOptions();
                DialogueTrigger dialogueTrigger = options.get(number);
                if(dialogueTrigger.getHasTrigger()){
                    Mob entity = (Mob)dialogueEntity.getChatEntities()[dialogueTrigger.getNumber()];
                    dialogueTrigger.trigger(entity);
                    ServerNetwork.toServerMessage(new DialogueTriggerMessage(entity.getId(),dialogueEntity.getDialogue(),number));
                    dialogueEntity.setDialogue(null);
                }
                else {
                    if (dialogueEntity.getDialogue().getNextDialogue() != null) {
                        dialogueEntity.setOldOptions(dialogueEntity.getDialogue().getOptions().size());
                        dialogueEntity.startSpeak(dialogueEntity.getDialogue().getNextDialogue(), 100);
                    } else
                        dialogueEntity.setDialogue(null);
                }
                dialogueEntity.setNumber(0);
                event.setCanceled(true);
            }
        }
    }


}
