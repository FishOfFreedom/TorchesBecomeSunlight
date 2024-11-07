package com.freefish.torchesbecomesunlight;

import com.bobmowzie.mowziesmobs.client.particle.ParticleHandler;
import com.freefish.torchesbecomesunlight.client.event.ForgeClientEventL;
import com.freefish.torchesbecomesunlight.server.init.*;
import com.freefish.torchesbecomesunlight.server.capability.frozen.FrozenCapabilityProvider;
import com.freefish.torchesbecomesunlight.server.capability.story.PlayerStoryStoneProvider;
import com.freefish.torchesbecomesunlight.server.entity.ai.attribute.AttributeRegistry;
import com.freefish.torchesbecomesunlight.server.event.ServerNetwork;
import com.freefish.torchesbecomesunlight.server.init.group.ModGroup;
import com.freefish.torchesbecomesunlight.server.init.SoundHandle;
import com.freefish.torchesbecomesunlight.server.world.structure.STStructures;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod(TorchesBecomeSunlight.MOD_ID)
public class TorchesBecomeSunlight
{
    public static final String MOD_ID = "torchesbecomesunlight";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Map<UUID, ResourceLocation> bossBarRegistryNames = new HashMap<>();

    public static SimpleChannel NETWORK;

    public TorchesBecomeSunlight()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        ItemHandle.ITEMS.register(bus);
        BlockHandle.BLOCKS.register(bus);
        BlockEntityHandle.BLOCK_ENTITY.register(bus);
        SoundHandle.SOUNDS.register(bus);
        EntityHandle.ENTITY_TYPE.register(bus);
        AttributeRegistry.ATTRIBUTES.register(bus);
        ParticleHandler.REG.register(bus);
        EffectHandle.POTIONS.register(bus);
        STStructures.DEFERRED_REGISTRY_STRUCTURE.register(bus);
        ModGroup.CREATIVE_MODE_TAB.register(bus);

        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class,this::attachCapability);

        bus.addListener(this::commonSetup);
        bus.addListener(this::clientSetup);
        MinecraftForge.EVENT_BUS.register(this);
        bus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(()->{
            ServerNetwork.initNetwork();
            MinecraftForge.EVENT_BUS.register(ForgeClientEventL.INSTANCE);
        });
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(()->{
            IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
            bus.addListener(ForgeClientEventL::registerShaders);
        });
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {

    }
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }

    public void attachCapability(AttachCapabilitiesEvent<Entity> event){
        if(event.getObject() instanceof Player player){
            if(!player.getCapability(PlayerStoryStoneProvider.PLAYER_STORY_STONE_CAPABILITY).isPresent()){
                event.addCapability(new ResourceLocation(MOD_ID,"storystone"),new PlayerStoryStoneProvider());
            }
        }
        if(event.getObject() instanceof LivingEntity livingEntity){
            if(!livingEntity.getCapability(FrozenCapabilityProvider.FROZEN_CAPABILITY).isPresent()){
                event.addCapability(new ResourceLocation(MOD_ID,"frozen"),new FrozenCapabilityProvider());
            }
        }
    }

    public static <MSG> void sendMSGToAll(MSG message) {
        for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            NETWORK.sendTo(message, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }
}
