package com.freefish.torchesbecomesunlight.client.keybindings;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.util.storage.ClientStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
