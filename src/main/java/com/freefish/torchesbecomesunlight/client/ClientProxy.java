package com.freefish.torchesbecomesunlight.client;

import com.freefish.torchesbecomesunlight.client.render.gui.hud.HudWidget;
import com.freefish.torchesbecomesunlight.server.CommonProxy;
import com.freefish.torchesbecomesunlight.server.config.ConfigHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;

public class ClientProxy extends CommonProxy {

    @Override
    public void init(final IEventBus bus, final ModContainer container) {
        super.init(bus, container);
        HudWidget.initHud();
        container.registerConfig(ModConfig.Type.CLIENT, ConfigHandler.CLIENT_CONFIG);
    }
}
