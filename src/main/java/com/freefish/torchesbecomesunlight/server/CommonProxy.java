package com.freefish.torchesbecomesunlight.server;

import com.freefish.torchesbecomesunlight.server.config.ConfigHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;

public class CommonProxy {
    public void init(final IEventBus bus, final ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, ConfigHandler.COMMON_CONFIG);
    }
}