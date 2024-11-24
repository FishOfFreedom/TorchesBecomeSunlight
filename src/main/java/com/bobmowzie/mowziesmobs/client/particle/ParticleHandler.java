package com.bobmowzie.mowziesmobs.client.particle;

import com.bobmowzie.mowziesmobs.client.particle.util.AdvancedParticleBase;
import com.bobmowzie.mowziesmobs.client.particle.util.AdvancedParticleData;
import com.bobmowzie.mowziesmobs.client.particle.util.RibbonParticleData;
import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.particle.*;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**Be from https://github.com/BobMowzie/MowziesMobs/blob/master/src/main/java/com/bobmowzie/mowziesmobs/client/particle/ParticleHandler.java
 * @author bobmowzie
 */
@Mod.EventBusSubscriber(modid = TorchesBecomeSunlight.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ParticleHandler {

    public static final DeferredRegister<ParticleType<?>> REG = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, TorchesBecomeSunlight.MOD_ID);

    public static final RegistryObject<ParticleType<AdvancedParticleData>> RING2 = register("ring", AdvancedParticleData.DESERIALIZER);
    public static final RegistryObject<ParticleType<AdvancedParticleData>> RING_BIG = register("ring_big", AdvancedParticleData.DESERIALIZER);
    public static final RegistryObject<ParticleType<AdvancedParticleData>> PIXEL = register("pixel", AdvancedParticleData.DESERIALIZER);
    public static final RegistryObject<ParticleType<AdvancedParticleData>> ORB2 = register("orb", AdvancedParticleData.DESERIALIZER);
    public static final RegistryObject<ParticleType<AdvancedParticleData>> EYE = register("eye", AdvancedParticleData.DESERIALIZER);
    public static final RegistryObject<ParticleType<AdvancedParticleData>> BUBBLE = register("bubble", AdvancedParticleData.DESERIALIZER);
    public static final RegistryObject<ParticleType<AdvancedParticleData>> SUN = register("sun", AdvancedParticleData.DESERIALIZER);
    public static final RegistryObject<ParticleType<AdvancedParticleData>> SUN_NOVA = register("sun_nova", AdvancedParticleData.DESERIALIZER);
    public static final RegistryObject<ParticleType<AdvancedParticleData>> FLARE = register("flare", AdvancedParticleData.DESERIALIZER);
    public static final RegistryObject<ParticleType<AdvancedParticleData>> FLARE_RADIAL = register("flare_radial", AdvancedParticleData.DESERIALIZER);
    public static final RegistryObject<ParticleType<AdvancedParticleData>> BURST_IN = register("ring1", AdvancedParticleData.DESERIALIZER);
    public static final RegistryObject<ParticleType<AdvancedParticleData>> BURST_MESSY = register("burst_messy", AdvancedParticleData.DESERIALIZER);
    public static final RegistryObject<ParticleType<AdvancedParticleData>> RING_SPARKS = register("sparks_ring", AdvancedParticleData.DESERIALIZER);
    public static final RegistryObject<ParticleType<AdvancedParticleData>> BURST_OUT = register("ring2", AdvancedParticleData.DESERIALIZER);
    public static final RegistryObject<ParticleType<AdvancedParticleData>> GLOW = register("glow", AdvancedParticleData.DESERIALIZER);
    public static final RegistryObject<ParticleType<AdvancedParticleData>> ARROW_HEAD = register("arrow_head", AdvancedParticleData.DESERIALIZER);
    public static final RegistryObject<ParticleType<RibbonParticleData>> RIBBON_FLAT = registerRibbon("ribbon_flat", RibbonParticleData.DESERIALIZER);
    public static final RegistryObject<ParticleType<RibbonParticleData>> RIBBON_STREAKS = registerRibbon("ribbon_streaks", RibbonParticleData.DESERIALIZER);
    public static final RegistryObject<ParticleType<RibbonParticleData>> RIBBON_GLOW = registerRibbon("ribbon_glow", RibbonParticleData.DESERIALIZER);
    public static final RegistryObject<ParticleType<RibbonParticleData>> RIBBON_SQUIGGLE = registerRibbon("ribbon_squiggle", RibbonParticleData.DESERIALIZER);

    public static final RegistryObject<ParticleType<AdvancedParticleData>> ICEBOMB_1 = register("icebomb_1", AdvancedParticleData.DESERIALIZER);
    public static final RegistryObject<ParticleType<AdvancedParticleData>> ICE_WHIRLWIND = register("ice_whirlwind", AdvancedParticleData.DESERIALIZER);
    public static final RegistryObject<ParticleType<AdvancedParticleData>> SAN = register("san", AdvancedParticleData.DESERIALIZER);

    public static final RegistryObject<ParticleType<ParticleWindigoCrack.WindigoCrackData>> WINDIGO_CRACK = REG.register("windigo_crack", () -> new ParticleType<ParticleWindigoCrack.WindigoCrackData>(false, ParticleWindigoCrack.WindigoCrackData.DESERIALIZER) {
        @Override
        public Codec<ParticleWindigoCrack.WindigoCrackData> codec() {
            return ParticleWindigoCrack.WindigoCrackData.CODEC(WINDIGO_CRACK.get());
        }
    });

    public static final RegistryObject<ParticleType<BlackSpearParticle.BlackSpearData>> BLACK_SPEAR = REG.register("black_spear", () -> new ParticleType<BlackSpearParticle.BlackSpearData>(false, BlackSpearParticle.BlackSpearData.DESERIALIZER) {
        @Override
        public Codec<BlackSpearParticle.BlackSpearData> codec() {
            return BlackSpearParticle.BlackSpearData.CODEC(BLACK_SPEAR.get());
        }
    });

    public static final RegistryObject<ParticleType<DemonParticle.DemonData>> DEMON = REG.register("demon", () -> new ParticleType<DemonParticle.DemonData>(false, DemonParticle.DemonData.DESERIALIZER) {
        @Override
        public Codec<DemonParticle.DemonData> codec() {
            return DemonParticle.DemonData.CODEC(DEMON.get());
        }
    });

    public static final RegistryObject<ParticleType<DemonHoleParticle.DemonHoleData>> DEMONHOLE = REG.register("demonhole", () -> new ParticleType<DemonHoleParticle.DemonHoleData>(false, DemonHoleParticle.DemonHoleData.DESERIALIZER) {
        @Override
        public Codec<DemonHoleParticle.DemonHoleData> codec() {
            return DemonHoleParticle.DemonHoleData.CODEC(DEMONHOLE.get());
        }
    });

    public static final RegistryObject<ParticleType<BladeParticle.BladeData>> BLADE = REG.register("blade", () -> new ParticleType<BladeParticle.BladeData>(false, BladeParticle.BladeData.DESERIALIZER) {
        @Override
        public Codec<BladeParticle.BladeData> codec() {
            return BladeParticle.BladeData.CODEC(BLADE.get());
        }
    });

    public static final RegistryObject<ParticleType<BlackFlatParticle.BlackFlatData>> BLACK_FLAT = REG.register("black_flat", () -> new ParticleType<BlackFlatParticle.BlackFlatData>(false, BlackFlatParticle.BlackFlatData.DESERIALIZER) {
        @Override
        public Codec<BlackFlatParticle.BlackFlatData> codec() {
            return BlackFlatParticle.BlackFlatData.CODEC(BLACK_FLAT.get());
        }
    });

    public static final RegistryObject<ParticleType<BlackHoleParticle.BlackHoleData>> BLACKHOLE = REG.register("blackhole", () -> new ParticleType<BlackHoleParticle.BlackHoleData>(false, BlackHoleParticle.BlackHoleData.DESERIALIZER) {
        @Override
        public Codec<BlackHoleParticle.BlackHoleData> codec() {
            return BlackHoleParticle.BlackHoleData.CODEC(BLACKHOLE.get());
        }
    });

    public static final RegistryObject<ParticleType<ParticleCloud.CloudData>> CLOUD = REG.register("cloud_soft", () -> new ParticleType<ParticleCloud.CloudData>(false, ParticleCloud.CloudData.DESERIALIZER) {
        @Override
        public Codec<ParticleCloud.CloudData> codec() {
            return ParticleCloud.CloudData.CODEC(CLOUD.get());
        }
    });

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ParticleHandler.CLOUD.get(), ParticleCloud.CloudFactory::new);

        event.registerSpriteSet(ParticleHandler.RING2.get(), AdvancedParticleBase.Factory::new);
        event.registerSpriteSet(ParticleHandler.RING_BIG.get(), AdvancedParticleBase.Factory::new);
        event.registerSpriteSet(ParticleHandler.PIXEL.get(), AdvancedParticleBase.Factory::new);
        event.registerSpriteSet(ParticleHandler.ORB2.get(), AdvancedParticleBase.Factory::new);
        event.registerSpriteSet(ParticleHandler.EYE.get(), AdvancedParticleBase.Factory::new);
        event.registerSpriteSet(ParticleHandler.BUBBLE.get(), AdvancedParticleBase.Factory::new);
        event.registerSpriteSet(ParticleHandler.SUN.get(), AdvancedParticleBase.Factory::new);
        event.registerSpriteSet(ParticleHandler.SUN_NOVA.get(), AdvancedParticleBase.Factory::new);
        event.registerSpriteSet(ParticleHandler.FLARE.get(), AdvancedParticleBase.Factory::new);
        event.registerSpriteSet(ParticleHandler.FLARE_RADIAL.get(), AdvancedParticleBase.Factory::new);
        event.registerSpriteSet(ParticleHandler.BURST_IN.get(), AdvancedParticleBase.Factory::new);
        event.registerSpriteSet(ParticleHandler.BURST_MESSY.get(), AdvancedParticleBase.Factory::new);
        event.registerSpriteSet(ParticleHandler.RING_SPARKS.get(), AdvancedParticleBase.Factory::new);
        event.registerSpriteSet(ParticleHandler.BURST_OUT.get(), AdvancedParticleBase.Factory::new);
        event.registerSpriteSet(ParticleHandler.GLOW.get(), AdvancedParticleBase.Factory::new);
        event.registerSpriteSet(ParticleHandler.ARROW_HEAD.get(), AdvancedParticleBase.Factory::new);

        event.registerSpriteSet(ParticleHandler.ICEBOMB_1.get(), AdvancedParticleBase.Factory::new);
        event.registerSpriteSet(ParticleHandler.ICE_WHIRLWIND.get(), AdvancedParticleBase.Factory::new);
        event.registerSpriteSet(ParticleHandler.SAN.get(), AdvancedParticleBase.Factory::new);

        event.registerSpriteSet(ParticleHandler.RIBBON_FLAT.get(), ParticleRibbon.Factory::new);
        event.registerSpriteSet(ParticleHandler.RIBBON_STREAKS.get(), ParticleRibbon.Factory::new);
        event.registerSpriteSet(ParticleHandler.RIBBON_GLOW.get(), ParticleRibbon.Factory::new);
        event.registerSpriteSet(ParticleHandler.RIBBON_SQUIGGLE.get(), ParticleRibbon.Factory::new);

        event.registerSpriteSet(ParticleHandler.WINDIGO_CRACK.get(), ParticleWindigoCrack.WindigoCrackFactory::new);
        event.registerSpriteSet(ParticleHandler.BLACK_SPEAR.get(), BlackSpearParticle.Factory::new);
        event.registerSpriteSet(ParticleHandler.DEMON.get(), DemonParticle.Factory::new);
        event.registerSpriteSet(ParticleHandler.DEMONHOLE.get(), DemonHoleParticle.Factory::new);
        event.registerSpriteSet(ParticleHandler.BLADE.get(), BladeParticle.Factory::new);
        event.registerSpriteSet(ParticleHandler.BLACKHOLE.get(), BlackHoleParticle.Factory::new);
        event.registerSpriteSet(ParticleHandler.BLACK_FLAT.get(), BlackFlatParticle.Factory::new);
    }

    private static RegistryObject<SimpleParticleType> register(String key, boolean alwaysShow) {
        return REG.register(key, () -> new SimpleParticleType(alwaysShow));
    }

    private static RegistryObject<ParticleType<AdvancedParticleData>> register(String key, ParticleOptions.Deserializer<AdvancedParticleData> deserializer) {
        return REG.register(key, () -> new ParticleType<AdvancedParticleData>(false, deserializer) {
            public Codec<AdvancedParticleData> codec() {
                return AdvancedParticleData.CODEC(this);
            }
        });
    }

    private static RegistryObject<ParticleType<RibbonParticleData>> registerRibbon(String key, ParticleOptions.Deserializer<RibbonParticleData> deserializer) {
        return REG.register(key, () -> new ParticleType<RibbonParticleData>(false, deserializer) {
            public Codec<RibbonParticleData> codec() {
                return RibbonParticleData.CODEC_RIBBON(this);
            }
        });
    }
}
