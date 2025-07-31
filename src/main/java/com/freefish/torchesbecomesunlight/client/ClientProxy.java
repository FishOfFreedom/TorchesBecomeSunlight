package com.freefish.torchesbecomesunlight.client;

import com.freefish.torchesbecomesunlight.client.render.gui.hud.HudWidget;
import com.freefish.torchesbecomesunlight.server.CommonProxy;
import com.freefish.torchesbecomesunlight.server.config.ConfigHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ClientProxy extends CommonProxy {

    @Override
    public void init(final IEventBus bus) {
        super.init(bus);
        HudWidget.initHud();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigHandler.CLIENT_CONFIG);
    }
}
