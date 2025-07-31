package com.freefish.torchesbecomesunlight;

import com.freefish.rosmontislib.gui.factory.UIFactory;
import com.freefish.torchesbecomesunlight.client.ClientProxy;
import com.freefish.torchesbecomesunlight.client.event.ForgeClientEventL;
import com.freefish.torchesbecomesunlight.client.render.gui.TBSDrawerHelper;
import com.freefish.torchesbecomesunlight.client.render.gui.burdenbeast.BurdenbeastContainerGUIFactory;
import com.freefish.torchesbecomesunlight.client.render.gui.partnercommand.PartnerCommandMenuFactory;
import com.freefish.torchesbecomesunlight.client.render.layer.ClientLayerRegistry;
import com.freefish.torchesbecomesunlight.client.render.model.tools.MowzieModelFactory;
import com.freefish.torchesbecomesunlight.client.shader.TBSShaderHandle;
import com.freefish.torchesbecomesunlight.compat.ModDetectionCache;
import com.freefish.torchesbecomesunlight.compat.oculus.ForgeOculusHandle;
import com.freefish.torchesbecomesunlight.compat.rosmontis.TBSMaterialHandle;
import com.freefish.torchesbecomesunlight.server.CommonProxy;
import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.command.customargument.PartnerTypeArgument;
import com.freefish.torchesbecomesunlight.server.config.ConfigHandler;
import com.freefish.torchesbecomesunlight.server.entity.ai.attribute.AttributeRegistry;
import com.freefish.torchesbecomesunlight.server.event.EventListener;
import com.freefish.torchesbecomesunlight.server.event.ServerNetwork;
import com.freefish.torchesbecomesunlight.server.init.*;
import com.freefish.torchesbecomesunlight.server.init.generator.BiomeModifiersHandler;
import com.freefish.torchesbecomesunlight.server.init.generator.advancement.criterion.TriggerHandler;
import com.freefish.torchesbecomesunlight.server.init.group.ModGroup;
import com.freefish.torchesbecomesunlight.server.init.recipe.RecipesHandle;
import com.freefish.torchesbecomesunlight.server.init.village.MemoryModuleTypeHandle;
import com.freefish.torchesbecomesunlight.server.init.village.SensorTypeHandle;
import com.freefish.torchesbecomesunlight.server.partner.PartnerHandler;
import com.freefish.torchesbecomesunlight.server.world.levelEntity.TBSLevelEntityHandle;
import com.freefish.torchesbecomesunlight.server.world.structure.StructureHandle;
import com.freefish.torchesbecomesunlight.server.world.structure.processor.ProcessorHandler;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.core.animation.EasingType;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Mod(TorchesBecomeSunlight.MOD_ID)
public class TorchesBecomeSunlight
{
    public static final String MOD_ID = "torchesbecomesunlight";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Map<UUID, Integer> bossBarRegistryNames = new HashMap<>();
    public static CommonProxy PROXY;

    public static SimpleChannel NETWORK;
    public static final Random random = new Random();

    public static final RecipeBookType RECIPE_TYPE_COOKING = RecipeBookType.create("STEW");

    public TorchesBecomeSunlight()
    {
        GeckoLibUtil.addCustomBakedModelFactory(MOD_ID, new MowzieModelFactory());
        GeckoLibUtil.addCustomEasingType("custom_step",value -> EasingType.easeIn((d)->1));
        GeckoLib.initialize();

        PROXY = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        ItemHandle.ITEMS.register(bus);
        BlockHandle.BLOCKS.register(bus);
        BlockEntityHandle.BLOCK_ENTITY.register(bus);
        CommandArgumentHandle.ARGUMENT_TYPES.register(bus);
        LootHandle.GLOBAL_LOOT.register(bus);
        SoundHandle.SOUNDS.register(bus);
        EntityHandle.ENTITY_TYPE.register(bus);
        AttributeRegistry.ATTRIBUTES.register(bus);
        ParticleHandler.REG.register(bus);
        EffectHandle.init(bus);
        BiomeModifiersHandler.REG.register(bus);
        ModGroup.CREATIVE_MODE_TAB.register(bus);
        StructureHandle.DEFERRED_REGISTRY_STRUCTURE.register(bus);
        MemoryModuleTypeHandle.MEMORY_MODULES.register(bus);
        SensorTypeHandle.SENSOR_TYPES.register(bus);
        RecipesHandle.register(bus);
        MenuHandle.register(bus);

        PROXY.init(bus);
        bus.addListener(this::commonSetup);
        bus.addListener(this::clientSetup);
        bus.addListener(this::onModConfigEvent);
        bus.addListener(this::complete);
        bus.addListener(CapabilityHandle::registerCapabilities);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new EventListener());
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, CapabilityHandle::attachEntityCapability);
    }

    @SubscribeEvent
    public void onModConfigEvent(final ModConfigEvent event) {
        final ModConfig config = event.getConfig();
        if (config.getSpec() == ConfigHandler.COMMON_CONFIG) {
            ConfigHandler.COMMON.TOOLs.HALBERD.attackDamageValue = ConfigHandler.COMMON.TOOLs.HALBERD.attackDamage.get().floatValue();
        };
    }

    private void complete(FMLLoadCompleteEvent event) {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ItemHandle.initializeAttributes();
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        if (ModDetectionCache.hasSteveModelMod()) {
        }

        TBSLevelEntityHandle.init();
        TriggerHandler.init();
        UIFactory.register(PartnerCommandMenuFactory.INSTANCE);
        UIFactory.register(BurdenbeastContainerGUIFactory.INSTANCE);
        ArgumentTypeInfos.registerByClass(PartnerTypeArgument.class, CommandArgumentHandle.PARTNER_TYPE.get());
        SpawnHandler.registerSpawnPlacementTypes();
        event.enqueueWork(()->{
            PartnerHandler.init();
            ServerNetwork.initNetwork();
            ProcessorHandler.registerStructureProcessors();
        });
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientLayerRegistry::onAddLayers);
        event.enqueueWork(()->{
            IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
            bus.addListener(TBSShaderHandle::registerShaders);
            TBSShaderHandle.init();
            TBSDrawerHelper.init();
            TBSMaterialHandle.init();
            MinecraftForge.EVENT_BUS.register(ForgeClientEventL.INSTANCE);
        });
    }

    public static <MSG> void sendMSGToAll(MSG message) {
        for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            NETWORK.sendTo(message, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public static <MSG> void sendToLevel(MSG msg, ServerLevel serverLevel) {
        for(ServerPlayer player:serverLevel.players()){
            NETWORK.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public static boolean isUsingShaderPack() {
        return ForgeOculusHandle.INSTANCE!=null && ForgeOculusHandle.INSTANCE.underShaderPack();
    }
}
