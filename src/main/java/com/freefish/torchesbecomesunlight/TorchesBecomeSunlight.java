package com.freefish.torchesbecomesunlight;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.loading.FMLEnvironment;
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
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.animation.EasingType;
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

    public static final Random random = new Random();

    /**
     * RecipeBookType 是 IExtensibleEnum，新常量在 META-INF/enumextensions.json 中声明。
     * 走 EnumProxy 懒取，避免类初始化早于枚举扩展。
     */
    public static final net.neoforged.fml.common.asm.enumextension.EnumProxy<RecipeBookType> RECIPE_TYPE_COOKING_PROXY =
            new net.neoforged.fml.common.asm.enumextension.EnumProxy<>(RecipeBookType.class);

    public static RecipeBookType recipeTypeCooking() {
        return RECIPE_TYPE_COOKING_PROXY.getValue();
    }

    // NeoForge 1.21.1：mod 总线由构造函数注入，不再有 FMLJavaModLoadingContext
    public TorchesBecomeSunlight(IEventBus bus, ModContainer container)
    {
        GeckoLibUtil.addCustomBakedModelFactory(MOD_ID, new MowzieModelFactory());
        GeckoLibUtil.addCustomEasingType("custom_step",value -> EasingType.easeIn((d)->1));
        // DistExecutor 在 1.21.1 已被移除，改用 FMLEnvironment.dist 判断
        PROXY = FMLEnvironment.dist.isClient() ? new ClientProxy() : new CommonProxy();

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
        TBSAttachments.ATTACHMENT_TYPES.register(bus);
        TriggerHandler.register(bus);
        ProcessorHandler.register(bus);

        PROXY.init(bus, container);
        bus.addListener(this::commonSetup);
        bus.addListener(this::clientSetup);
        bus.addListener(this::onModConfigEvent);
        bus.addListener(this::complete);

        if (FMLEnvironment.dist.isClient()) {
            bus.addListener(ClientLayerRegistry::onAddLayers);
            bus.addListener(TBSShaderHandle::registerShaders);
        }

        NeoForge.EVENT_BUS.register(new EventListener());
        // NeoForge 已移除 AttachCapabilitiesEvent，capability 改为在 RegisterCapabilitiesEvent 中按实体类型注册
    }

    public void onModConfigEvent(final ModConfigEvent event) {
        final ModConfig config = event.getConfig();
        if (config.getSpec() == ConfigHandler.COMMON_CONFIG) {
            ConfigHandler.COMMON.TOOLs.HALBERD.attackDamageValue = ConfigHandler.COMMON.TOOLs.HALBERD.attackDamage.get().floatValue();
        };
    }

    private void complete(FMLLoadCompleteEvent event) {
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
            // 封包注册已改为 ServerNetwork 上的 RegisterPayloadHandlersEvent，无需在此初始化
        });
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        // ClientLayerRegistry / TBSShaderHandle 的监听已在构造函数中挂到注入的 mod 总线上
        event.enqueueWork(()->{
            TBSShaderHandle.init();
            TBSDrawerHelper.init();
            TBSMaterialHandle.init();
            NeoForge.EVENT_BUS.register(ForgeClientEventL.INSTANCE);
        });
    }

    public static void sendMSGToAll(CustomPacketPayload message) {
        PacketDistributor.sendToAllPlayers(message);
    }

    public static void sendToLevel(CustomPacketPayload msg, ServerLevel serverLevel) {
        PacketDistributor.sendToPlayersInDimension(serverLevel, msg);
    }

    /**
     * 移植目标环境使用 Sodium，不含 Oculus/Iris，compat/oculus 已在移植时剥离。
     * 若之后要接 Iris，在这里改判即可。
     */
    public static boolean isUsingShaderPack() {
        return false;
    }
}
