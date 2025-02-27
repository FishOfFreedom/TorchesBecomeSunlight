package com.freefish.torchesbecomesunlight.client.keybindings;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
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

    @SubscribeEvent
    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_BOOK);
    }
}
