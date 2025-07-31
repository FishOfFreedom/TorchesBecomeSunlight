package com.freefish.torchesbecomesunlight.client.keybindings;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = TorchesBecomeSunlight.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModKeyBindings {
    public static final String CATEGORY = "key.torchesbecomesunlight.general";

    public static final KeyMapping OPEN_BOOK = new KeyMapping("key.torchesbecomesunlight.skip_dialogue", GLFW.GLFW_KEY_K, CATEGORY);
    public static final KeyMapping SELECT_DIALOGUE = new KeyMapping("key.torchesbecomesunlight.select_dialogue", InputConstants.Type.MOUSE, 2, CATEGORY);
    public static final KeyMapping OPEN_PARTNER_COMMAND_MENU = new KeyMapping("key.torchesbecomesunlight.open_partner_command_menu", GLFW.GLFW_KEY_V, CATEGORY);

    @SubscribeEvent
    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_BOOK);
        event.register(SELECT_DIALOGUE);
        event.register(OPEN_PARTNER_COMMAND_MENU);
    }
}
