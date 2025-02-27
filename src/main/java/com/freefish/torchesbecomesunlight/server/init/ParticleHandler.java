package com.freefish.torchesbecomesunlight.server.init;

import com.freefish.torchesbecomesunlight.client.util.particle.ParticleCloud;
import com.freefish.torchesbecomesunlight.client.util.particle.util.AdvancedParticleData;
import com.freefish.torchesbecomesunlight.client.util.particle.util.RibbonParticleData;
import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.particle.*;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ParticleHandler {

    public static final DeferredRegister<ParticleType<?>> REG = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, TorchesBecomeSunlight.MOD_ID);

    public static final RegistryObject<ParticleType<AdvancedParticleData>> RING2 = register("ring", AdvancedParticleData.DESERIALIZER);
    public static final RegistryObject<ParticleType<AdvancedParticleData>> RING_BIG = register("ring_big", AdvancedParticleData.DESERIALIZER);
    public static final RegistryObject<ParticleType<AdvancedParticleData>> PIXEL = register("pixel", AdvancedParticleData.DESERIALIZER);
    public static final RegistryObject<ParticleType<AdvancedParticleData>> PIXEL_GLOW = register("pixel_glow", AdvancedParticleData.DESERIALIZER);
    public static final RegistryObject<ParticleType<AdvancedParticleData>> SUN = register("sun", AdvancedParticleData.DESERIALIZER);
    public static final RegistryObject<ParticleType<AdvancedParticleData>> BURST_MESSY = register("burst_messy", AdvancedParticleData.DESERIALIZER);
    public static final RegistryObject<ParticleType<AdvancedParticleData>> ARROW_HEAD = register("arrow_head", AdvancedParticleData.DESERIALIZER);
    public static final RegistryObject<ParticleType<RibbonParticleData>> RIBBON_FLAT = registerRibbon("ribbon_flat", RibbonParticleData.DESERIALIZER);
    public static final RegistryObject<ParticleType<RibbonParticleData>> RIBBON_GLOW = registerRibbon("ribbon_glow", RibbonParticleData.DESERIALIZER);
    public static final RegistryObject<ParticleType<AdvancedParticleData>> ARMOR = register("armor", AdvancedParticleData.DESERIALIZER);

    public static final RegistryObject<ParticleType<AdvancedParticleData>> ICEBOMB_1 = register("icebomb_1", AdvancedParticleData.DESERIALIZER);
    public static final RegistryObject<ParticleType<AdvancedParticleData>> ICE_WHIRLWIND = register("ice_whirlwind", AdvancedParticleData.DESERIALIZER);
    public static final RegistryObject<ParticleType<AdvancedParticleData>> SAN = register("san", AdvancedParticleData.DESERIALIZER);


    public static final RegistryObject<ParticleType<BlackSpearParticle.BlackSpearData>> BLACK_SPEAR = REG.register("black_spear", () -> new ParticleType<BlackSpearParticle.BlackSpearData>(false, BlackSpearParticle.BlackSpearData.DESERIALIZER) {
        @Override
        public Codec<BlackSpearParticle.BlackSpearData> codec() {
            return BlackSpearParticle.BlackSpearData.CODEC(BLACK_SPEAR.get());
        }
    });

    public static final RegistryObject<ParticleType<CycleWindParticle.CycleWindData>> CYCLE_WIND = REG.register("cycle_wind", () -> new ParticleType<CycleWindParticle.CycleWindData>(false, CycleWindParticle.CycleWindData.DESERIALIZER) {
        @Override
        public Codec<CycleWindParticle.CycleWindData> codec() {
            return CycleWindParticle.CycleWindData.CODEC(CYCLE_WIND.get());
        }
    });

    public static final RegistryObject<ParticleType<DemonParticle.DemonData>> DEMON = REG.register("demon", () -> new ParticleType<DemonParticle.DemonData>(false, DemonParticle.DemonData.DESERIALIZER) {
        @Override
        public Codec<DemonParticle.DemonData> codec() {
            return DemonParticle.DemonData.CODEC(DEMON.get());
        }
    });

    public static final RegistryObject<ParticleType<DemonHoleParticle.DemonHoleData>> DEMONHOLE = REG.register("demon_hole", () -> new ParticleType<DemonHoleParticle.DemonHoleData>(false, DemonHoleParticle.DemonHoleData.DESERIALIZER) {
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

    public static final RegistryObject<ParticleType<ParticleCloud.CloudData>> CLOUD = REG.register("cloud_soft", () -> new ParticleType<ParticleCloud.CloudData>(false, ParticleCloud.CloudData.DESERIALIZER) {
        @Override
        public Codec<ParticleCloud.CloudData> codec() {
            return ParticleCloud.CloudData.CODEC(CLOUD.get());
        }
    });

    public static final RegistryObject<SimpleParticleType> TESLA_BULB_LIGHTNING = REG.register("tesla_bulb_lightning", () -> new SimpleParticleType(false));

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
