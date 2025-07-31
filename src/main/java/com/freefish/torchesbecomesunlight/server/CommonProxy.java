package com.freefish.torchesbecomesunlight.server;

import com.freefish.torchesbecomesunlight.server.config.ConfigHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class CommonProxy {
    public void init(final IEventBus bus) {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.COMMON_CONFIG);
    }
}