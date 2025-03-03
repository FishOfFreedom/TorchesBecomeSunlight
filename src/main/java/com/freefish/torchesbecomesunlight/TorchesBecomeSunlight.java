package com.freefish.torchesbecomesunlight;

import com.freefish.torchesbecomesunlight.client.render.layer.ClientLayerRegistry;
import com.freefish.torchesbecomesunlight.client.render.model.tools.MowzieModelFactory;
import com.freefish.torchesbecomesunlight.client.shader.ShaderHandle;
import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.event.EventListener;
import com.freefish.torchesbecomesunlight.server.init.ParticleHandler;
import com.freefish.torchesbecomesunlight.client.event.ForgeClientEventL;
import com.freefish.torchesbecomesunlight.server.init.MenuHandle;
import com.freefish.torchesbecomesunlight.server.config.ConfigHandler;
import com.freefish.torchesbecomesunlight.server.init.*;
import com.freefish.torchesbecomesunlight.server.entity.ai.attribute.AttributeRegistry;
import com.freefish.torchesbecomesunlight.server.event.ServerNetwork;
import com.freefish.torchesbecomesunlight.server.init.generator.BiomeModifiersHandler;
import com.freefish.torchesbecomesunlight.server.init.group.ModGroup;
import com.freefish.torchesbecomesunlight.server.init.SoundHandle;
import com.freefish.torchesbecomesunlight.server.init.recipe.ModRecipes;
import com.freefish.torchesbecomesunlight.server.world.structure.StructureHandle;
import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod(TorchesBecomeSunlight.MOD_ID)
public class TorchesBecomeSunlight
{
    public static final String MOD_ID = "torchesbecomesunlight";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Map<UUID, Integer> bossBarRegistryNames = new HashMap<>();

    public static SimpleChannel NETWORK;

    public TorchesBecomeSunlight()
    {
        GeckoLibUtil.addCustomBakedModelFactory(MOD_ID, new MowzieModelFactory());
        GeckoLib.initialize();
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        ItemHandle.ITEMS.register(bus);
        BlockHandle.BLOCKS.register(bus);
        BlockEntityHandle.BLOCK_ENTITY.register(bus);
        SoundHandle.SOUNDS.register(bus);
        EntityHandle.ENTITY_TYPE.register(bus);
        AttributeRegistry.ATTRIBUTES.register(bus);
        ParticleHandler.REG.register(bus);
        EffectHandle.POTIONS.register(bus);
        BiomeModifiersHandler.REG.register(bus);
        ModGroup.CREATIVE_MODE_TAB.register(bus);
        StructureHandle.DEFERRED_REGISTRY_STRUCTURE.register(bus);
        ModRecipes.register(bus);
        MenuHandle.register(bus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigHandler.CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.COMMON_CONFIG);

        bus.addListener(this::commonSetup);
        bus.addListener(this::clientSetup);
        bus.addListener(CapabilityHandle::registerCapabilities);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new EventListener());
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, CapabilityHandle::attachEntityCapability);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        SpawnHandler.registerSpawnPlacementTypes();
        event.enqueueWork(()->{
            ServerNetwork.initNetwork();
        });
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientLayerRegistry::onAddLayers);
        event.enqueueWork(()->{
            IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
            bus.addListener(ShaderHandle::registerShaders);
            MinecraftForge.EVENT_BUS.register(ForgeClientEventL.INSTANCE);
        });
    }

    public static <MSG> void sendMSGToAll(MSG message) {
        for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            NETWORK.sendTo(message, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }
}
