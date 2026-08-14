package com.freefish.torchesbecomesunlight.server;

import com.freefish.torchesbecomesunlight.server.config.ConfigHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;

public class CommonProxy {
    public void init(final IEventBus bus) {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.COMMON_CONFIG);
    }
}