package com.freefish.torchesbecomesunlight.client.event;


import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.entity.*;
import com.freefish.torchesbecomesunlight.client.render.projectile.*;
import com.freefish.torchesbecomesunlight.server.entity.EntityRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TorchesBecomeSunlight.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvent {
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(EntityRegistry.SNOWNOVA.get(), SnowNovaRenderer::new);
        event.registerEntityRenderer(EntityRegistry.SNOWNOVA1.get(), SnowNova1Renderer::new);
        event.registerEntityRenderer(EntityRegistry.MAN.get(), ManRenderer::new);
        event.registerEntityRenderer(EntityRegistry.DIALOGUE.get(), VoidRenderer::new);
        event.registerEntityRenderer(EntityRegistry.SPEED_ENTITY.get(), VoidRenderer::new);
        event.registerEntityRenderer(EntityRegistry.ICE_CRYSTAL.get(), IceCrystalRenderer::new);
        event.registerEntityRenderer(EntityRegistry.BIG_ICE_CRYSTAL.get(), BigIceCrystalRenderer::new);
        event.registerEntityRenderer(EntityRegistry.CAMERA_SHAKE.get(), VoidRenderer::new);
        event.registerEntityRenderer(EntityRegistry.ICE_TUFT.get(), IceTuftRenderer::new);
        event.registerEntityRenderer(EntityRegistry.ICE_WALL_ENTITY.get(), VoidRenderer::new);
        event.registerEntityRenderer(EntityRegistry.ICE_BLADE.get(), IceBladeRenderer::new);
        event.registerEntityRenderer(EntityRegistry.FALLING_BLOCK.get(), RenderFallingBlock::new);
        event.registerEntityRenderer(EntityRegistry.HALBERD_OTI_ENTITY.get(), HalberdOTIRenderer::new);
        event.registerEntityRenderer(EntityRegistry.PATRIOT.get(), PatriotRenderer::new);
    }
}
