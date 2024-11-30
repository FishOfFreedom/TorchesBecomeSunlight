package com.freefish.torchesbecomesunlight.client.event;


import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.blockentity.GemPolishingBlockEntityRenderer;
import com.freefish.torchesbecomesunlight.client.render.blockentity.ShaderBlockRenderer;
import com.freefish.torchesbecomesunlight.client.render.entity.*;
import com.freefish.torchesbecomesunlight.client.render.enviroment.SkyRenderer;
import com.freefish.torchesbecomesunlight.client.render.gui.screen.GemPolishingStationScreen;
import com.freefish.torchesbecomesunlight.client.render.gui.screen.ModMenuTypes;
import com.freefish.torchesbecomesunlight.client.render.gui.screen.PotScreen;
import com.freefish.torchesbecomesunlight.client.render.projectile.*;
import com.freefish.torchesbecomesunlight.server.init.BlockEntityHandle;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Locale;

@Mod.EventBusSubscriber(modid = TorchesBecomeSunlight.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvent {
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(EntityHandle.FROST_NOVA.get(), SnowNovaRenderer::new);
        event.registerEntityRenderer(EntityHandle.SHIELD_GUARD.get(), ShieldGuardRenderer::new);
        event.registerEntityRenderer(EntityHandle.MANGLER.get(), ManglerRenderer::new);
        event.registerEntityRenderer(EntityHandle.MAN.get(), ManRenderer::new);
        event.registerEntityRenderer(EntityHandle.DIALOGUE.get(), VoidRenderer::new);
        event.registerEntityRenderer(EntityHandle.SPEED_ENTITY.get(), VoidRenderer::new);
        event.registerEntityRenderer(EntityHandle.ICE_CRYSTAL.get(), IceCrystalRenderer::new);
        event.registerEntityRenderer(EntityHandle.BIG_ICE_CRYSTAL.get(), BigIceCrystalRenderer::new);
        event.registerEntityRenderer(EntityHandle.CAMERA_SHAKE.get(), VoidRenderer::new);
        event.registerEntityRenderer(EntityHandle.ICE_TUFT.get(), IceTuftRenderer::new);
        event.registerEntityRenderer(EntityHandle.ICE_WALL_ENTITY.get(), VoidRenderer::new);
        event.registerEntityRenderer(EntityHandle.ICE_BLADE.get(), IceBladeRenderer::new);
        event.registerEntityRenderer(EntityHandle.FALLING_BLOCK.get(), RenderFallingBlock::new);
        event.registerEntityRenderer(EntityHandle.HALBERD_OTI_ENTITY.get(), HalberdOTIRenderer::new);
        event.registerEntityRenderer(EntityHandle.PATRIOT.get(), PatriotRenderer::new);
        event.registerEntityRenderer(EntityHandle.STOMP_ENTITY.get(), VoidRenderer::new);
        event.registerEntityRenderer(EntityHandle.PURSUER.get(), PursuerRenderer::new);
        event.registerEntityRenderer(EntityHandle.BLACK_SPEAR.get(), BlackSpearRenderer::new);
        event.registerEntityRenderer(EntityHandle.PEE.get(), VoidRenderer::new);
        event.registerEntityRenderer(EntityHandle.BLACKHE.get(), BlackHoleRenderer::new);
        event.registerEntityRenderer(EntityHandle.BLACK_TUFT.get(), BlackTuftRenderer::new);

        event.registerBlockEntityRenderer(BlockEntityHandle.SHADER.get(), ShaderBlockRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityHandle.GEM_POLISHING_BE.get(), GemPolishingBlockEntityRenderer::new);

        MenuScreens.register(ModMenuTypes.GEM_POLISHING_MENU.get(), GemPolishingStationScreen::new);
        MenuScreens.register(ModMenuTypes.POT_MENU.get(), PotScreen::new);
    }

    @SubscribeEvent
    public static void registerDimEffects(RegisterDimensionSpecialEffectsEvent event) {
        new SkyRenderer();
        //event.register(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "name".toLowerCase(Locale.ROOT)), new DemonRenderInfo(128.0F, false, DimensionSpecialEffects.SkyType.NONE, false, false));
    }
}
