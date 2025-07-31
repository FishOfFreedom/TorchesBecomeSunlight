package com.freefish.torchesbecomesunlight.client.keybindings;

import com.freefish.rosmontislib.gui.modular.ModularUIGuiContainer;
import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.gui.partnercommand.PartnerCommandMenuFactory;
import com.freefish.torchesbecomesunlight.client.render.gui.partnercommand.PartnerGuiContainer;
import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.PlayerCapability;
import com.freefish.torchesbecomesunlight.server.event.ServerNetwork;
import com.freefish.torchesbecomesunlight.server.event.packet.toserver.MiddelClickMessage;
import com.freefish.torchesbecomesunlight.server.story.dialogueentity.DialogueEntity;
import com.freefish.torchesbecomesunlight.server.util.FFEntityUtils;
import com.freefish.torchesbecomesunlight.server.util.storage.ClientStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = TorchesBecomeSunlight.MOD_ID)
public class KeyHandler {
    private static final Minecraft MINECRAFT = Minecraft.getInstance();

    public static void checkKeysPressed(int key,int action) {
        checkCasterKeys(key,action);
    }

    public static void checkCasterKeys(int key,int action) {
        if (key == -1)
            return;
        Player player = MINECRAFT.player;

        if (key == ModKeyBindings.OPEN_BOOK.getKey().getValue()) {
            ClientStorage.INSTANCE.isSkip = action == 1;
        } else if (key == ModKeyBindings.OPEN_PARTNER_COMMAND_MENU.getKey().getValue()) {
            var minecraft = Minecraft.getInstance();
            var entityPlayer = minecraft.player;
            PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(player, CapabilityHandle.PLAYER_CAPABILITY);
            if(minecraft.screen==null&&capability!=null&&capability.getPartnerManager().getCurrentPartner()!=null){
                var uiTemplate = PartnerCommandMenuFactory.INSTANCE.createUI(entityPlayer);
                uiTemplate.initWidgets();
                ModularUIGuiContainer ModularUIGuiContainer = new PartnerGuiContainer(uiTemplate, entityPlayer.containerMenu.containerId);
                minecraft.setScreen(ModularUIGuiContainer);
                entityPlayer.containerMenu = ModularUIGuiContainer.getMenu();
            }
        } else if (key == ModKeyBindings.SELECT_DIALOGUE.getKey().getValue()) {
            if(action==1){
                if(player == null) return;

                Level level = player.level();
                List<DialogueEntity> entities = level.getEntitiesOfClass(DialogueEntity.class,player.getBoundingBox().inflate(9));
                DialogueEntity dialogueEntity = FFEntityUtils.getClosestEntity(player,entities);
                if(dialogueEntity != null && dialogueEntity.getDialogue() != null && dialogueEntity.hasOptions()){
                    ServerNetwork.toServerMessage(new MiddelClickMessage(player.getId()));
                    dialogueEntity.setNumber(0);
                }
            }
        }
    }

    @SubscribeEvent
    public static void mouseEvent(final InputEvent.MouseButton.Post event) {
        if (MINECRAFT.player == null||event.getAction()==2)
            return;
        if (MINECRAFT.screen == null )
            checkKeysPressed(event.getButton(), event.getAction());
    }

    @SubscribeEvent
    public static void keyEvent(final InputEvent.Key event) {
        if (MINECRAFT.player == null||event.getAction()==2)
            return;
        if (MINECRAFT.screen == null )
            checkKeysPressed(event.getKey(), event.getAction());
    }
}
