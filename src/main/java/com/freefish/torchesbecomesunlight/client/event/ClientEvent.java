package com.freefish.torchesbecomesunlight.client.event;


import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.particle.*;
import com.freefish.torchesbecomesunlight.client.render.entity.*;
import com.freefish.torchesbecomesunlight.client.render.entity.village.ManRenderer;
import com.freefish.torchesbecomesunlight.client.render.entity.village.WomanRenderer;
import com.freefish.torchesbecomesunlight.client.render.enviroment.SkyRenderer;
import com.freefish.torchesbecomesunlight.client.render.gui.recipebook.RecipeCategories;
import com.freefish.torchesbecomesunlight.server.init.MenuHandle;
import com.freefish.torchesbecomesunlight.client.render.gui.screen.StewPotScreen;
import com.freefish.torchesbecomesunlight.client.render.projectile.*;
import com.freefish.torchesbecomesunlight.client.util.particle.ParticleCloud;
import com.freefish.torchesbecomesunlight.client.util.particle.ParticleRibbon;
import com.freefish.torchesbecomesunlight.client.util.particle.util.AdvancedParticleBase;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.init.ParticleHandler;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.event.RegisterRecipeBookCategoriesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TorchesBecomeSunlight.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvent {
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(EntityHandle.FROST_NOVA.get(), SnowNovaRenderer::new);
        event.registerEntityRenderer(EntityHandle.SHIELD_GUARD.get(), ShieldGuardRenderer::new);
        event.registerEntityRenderer(EntityHandle.SAINT_GUARD.get(), SaintGuardRenderer::new);
        event.registerEntityRenderer(EntityHandle.MANGLER.get(), ManglerRenderer::new);
        event.registerEntityRenderer(EntityHandle.MALE.get(), ManRenderer::new);
        event.registerEntityRenderer(EntityHandle.FEMALE.get(), WomanRenderer::new);
        event.registerEntityRenderer(EntityHandle.DIALOGUE.get(), DialogueEntityRenderer::new);
        event.registerEntityRenderer(EntityHandle.ICE_CRYSTAL.get(), IceCrystalRenderer::new);
        event.registerEntityRenderer(EntityHandle.LIGHT_BOOM.get(), LightBoomRenderer::new);
        event.registerEntityRenderer(EntityHandle.LIGHT_HALBERD.get(), HalberdOTI2Renderer::new);
        event.registerEntityRenderer(EntityHandle.BIG_ICE_CRYSTAL.get(), BigIceCrystalRenderer::new);
        event.registerEntityRenderer(EntityHandle.CAMERA_SHAKE.get(), VoidRenderer::new);
        event.registerEntityRenderer(EntityHandle.ICE_TUFT.get(), IceTuftRenderer::new);
        event.registerEntityRenderer(EntityHandle.ICE_BLADE.get(), IceBladeRenderer::new);
        event.registerEntityRenderer(EntityHandle.FALLING_BLOCK.get(), RenderFallingBlock::new);
        event.registerEntityRenderer(EntityHandle.HALBERD_OTI_ENTITY.get(), HalberdOTIRenderer::new);
        event.registerEntityRenderer(EntityHandle.PATRIOT.get(), PatriotRenderer::new);
        event.registerEntityRenderer(EntityHandle.GUN_KNIGHT_PATRIOT.get(), GunKnightPatriotRenderer::new);
        event.registerEntityRenderer(EntityHandle.STOMP_ENTITY.get(), VoidRenderer::new);
        event.registerEntityRenderer(EntityHandle.PURSUER.get(), PursuerRenderer::new);
        event.registerEntityRenderer(EntityHandle.BLACK_SPEAR.get(), BlackSpearRenderer::new);
        event.registerEntityRenderer(EntityHandle.BULLET.get(), BulletRenderer::new);
        event.registerEntityRenderer(EntityHandle.PEE.get(), VoidRenderer::new);
        event.registerEntityRenderer(EntityHandle.SACRED_REALM.get(), SacredRealmEntityRenderer::new);
        event.registerEntityRenderer(EntityHandle.BLACKHE.get(), BlackHoleRenderer::new);
        event.registerEntityRenderer(EntityHandle.BLACK_TUFT.get(), BlackTuftRenderer::new);
        event.registerEntityRenderer(EntityHandle.TURRET.get(), TurretRenderer::new);
        event.registerEntityRenderer(EntityHandle.FX_ENTITY.get(), VoidRenderer::new);

        MenuScreens.register(MenuHandle.STEW_POT_MENU.get(), StewPotScreen::new);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ParticleHandler.CLOUD.get(), ParticleCloud.CloudFactory::new);

        event.registerSpriteSet(ParticleHandler.RING2.get(), AdvancedParticleBase.Factory::new);
        event.registerSpriteSet(ParticleHandler.RING_BIG.get(), AdvancedParticleBase.Factory::new);
        event.registerSpriteSet(ParticleHandler.PIXEL.get(), AdvancedParticleBase.Factory::new);
        event.registerSpriteSet(ParticleHandler.PIXEL_GLOW.get(), AdvancedParticleBase.Factory::new);
        event.registerSpriteSet(ParticleHandler.SUN.get(), AdvancedParticleBase.Factory::new);
        event.registerSpriteSet(ParticleHandler.BURST_MESSY.get(), AdvancedParticleBase.Factory::new);
        event.registerSpriteSet(ParticleHandler.ARROW_HEAD.get(), AdvancedParticleBase.Factory::new);
        event.registerSpriteSet(ParticleHandler.ARMOR.get(), AdvancedParticleBase.Factory::new);

        event.registerSpriteSet(ParticleHandler.ICEBOMB_1.get(), AdvancedParticleBase.Factory::new);
        event.registerSpriteSet(ParticleHandler.ICE_WHIRLWIND.get(), AdvancedParticleBase.Factory::new);
        event.registerSpriteSet(ParticleHandler.SAN.get(), AdvancedParticleBase.Factory::new);

        event.registerSpriteSet(ParticleHandler.RIBBON_FLAT.get(), ParticleRibbon.Factory::new);
        event.registerSpriteSet(ParticleHandler.RIBBON_GLOW.get(), ParticleRibbon.Factory::new);

        event.registerSpriteSet(ParticleHandler.BLACK_SPEAR.get(), BlackSpearParticle.Factory::new);
        event.registerSpriteSet(ParticleHandler.DEMON.get(), DemonParticle.Factory::new);
        event.registerSpriteSet(ParticleHandler.DEMONHOLE.get(), DemonHoleParticle.Factory::new);
        event.registerSpriteSet(ParticleHandler.BLADE.get(), BladeParticle.Factory::new);
        event.registerSpriteSet(ParticleHandler.BLACK_FLAT.get(), BlackFlatParticle.Factory::new);
        event.registerSpriteSet(ParticleHandler.WIND.get(), WindParticle.Factory::new);
        event.registerSpecial(ParticleHandler.TESLA_BULB_LIGHTNING.get(),new TeslaBulbLightningParticle.Factory());
    }

    @SubscribeEvent
    public static void onRegisterRecipeBookCategories(RegisterRecipeBookCategoriesEvent event) {
        RecipeCategories.init(event);
    }

    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BulletRenderer.DecalModel.LAYER_LOCATION, BulletRenderer.DecalModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerDimEffects(RegisterDimensionSpecialEffectsEvent event) {
        new SkyRenderer();
    }
}
