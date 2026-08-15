package com.freefish.torchesbecomesunlight.server.init;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import com.freefish.torchesbecomesunlight.client.util.particle.ParticleCloud;
import com.freefish.torchesbecomesunlight.client.util.particle.util.AdvancedParticleData;
import com.freefish.torchesbecomesunlight.client.util.particle.util.RibbonParticleData;
import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.particle.*;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ParticleHandler {

    public static final DeferredRegister<ParticleType<?>> REG = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, TorchesBecomeSunlight.MOD_ID);

    public static final DeferredHolder<ParticleType<?>, ParticleType<AdvancedParticleData>> RING2 = register("ring");
    public static final DeferredHolder<ParticleType<?>, ParticleType<AdvancedParticleData>> RING_BIG = register("ring_big");
    public static final DeferredHolder<ParticleType<?>, ParticleType<AdvancedParticleData>> PIXEL = register("pixel");
    public static final DeferredHolder<ParticleType<?>, ParticleType<AdvancedParticleData>> PIXEL_GLOW = register("pixel_glow");
    public static final DeferredHolder<ParticleType<?>, ParticleType<AdvancedParticleData>> SUN = register("sun");
    public static final DeferredHolder<ParticleType<?>, ParticleType<AdvancedParticleData>> BURST_MESSY = register("burst_messy");
    public static final DeferredHolder<ParticleType<?>, ParticleType<AdvancedParticleData>> ARROW_HEAD = register("arrow_head");
    public static final DeferredHolder<ParticleType<?>, ParticleType<RibbonParticleData>> RIBBON_FLAT = registerRibbon("ribbon_flat");
    public static final DeferredHolder<ParticleType<?>, ParticleType<RibbonParticleData>> RIBBON_GLOW = registerRibbon("ribbon_glow");
    public static final DeferredHolder<ParticleType<?>, ParticleType<AdvancedParticleData>> ARMOR = register("armor");

    public static final DeferredHolder<ParticleType<?>, ParticleType<AdvancedParticleData>> ICEBOMB_1 = register("icebomb_1");
    public static final DeferredHolder<ParticleType<?>, ParticleType<AdvancedParticleData>> ICE_WHIRLWIND = register("ice_whirlwind");
    public static final DeferredHolder<ParticleType<?>, ParticleType<AdvancedParticleData>> SAN = register("san");


    public static final DeferredHolder<ParticleType<?>, ParticleType<BlackSpearParticle.BlackSpearData>> BLACK_SPEAR = REG.register("black_spear", () -> new ParticleType<BlackSpearParticle.BlackSpearData>(false) {
        @Override
        public MapCodec<BlackSpearParticle.BlackSpearData> codec() {
            return BlackSpearParticle.BlackSpearData.CODEC(this);
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, BlackSpearParticle.BlackSpearData> streamCodec() {
            return BlackSpearParticle.BlackSpearData.STREAM_CODEC(this);
        }
    });

    public static final DeferredHolder<ParticleType<?>, ParticleType<DemonParticle.DemonData>> DEMON = REG.register("demon", () -> new ParticleType<DemonParticle.DemonData>(false) {
        @Override
        public MapCodec<DemonParticle.DemonData> codec() {
            return DemonParticle.DemonData.CODEC(this);
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, DemonParticle.DemonData> streamCodec() {
            return DemonParticle.DemonData.STREAM_CODEC(this);
        }
    });

    public static final DeferredHolder<ParticleType<?>, ParticleType<DemonHoleParticle.DemonHoleData>> DEMONHOLE = REG.register("demon_hole", () -> new ParticleType<DemonHoleParticle.DemonHoleData>(false) {
        @Override
        public MapCodec<DemonHoleParticle.DemonHoleData> codec() {
            return DemonHoleParticle.DemonHoleData.CODEC(this);
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, DemonHoleParticle.DemonHoleData> streamCodec() {
            return DemonHoleParticle.DemonHoleData.STREAM_CODEC(this);
        }
    });

    public static final DeferredHolder<ParticleType<?>, ParticleType<WindParticle.WindData>> WIND = REG.register("wind", () -> new ParticleType<WindParticle.WindData>(false) {
        @Override
        public MapCodec<WindParticle.WindData> codec() {
            return WindParticle.WindData.CODEC(this);
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, WindParticle.WindData> streamCodec() {
            return WindParticle.WindData.STREAM_CODEC(this);
        }
    });

    public static final DeferredHolder<ParticleType<?>, ParticleType<BladeParticle.BladeData>> BLADE = REG.register("blade", () -> new ParticleType<BladeParticle.BladeData>(false) {
        @Override
        public MapCodec<BladeParticle.BladeData> codec() {
            return BladeParticle.BladeData.CODEC(this);
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, BladeParticle.BladeData> streamCodec() {
            return BladeParticle.BladeData.STREAM_CODEC(this);
        }
    });

    public static final DeferredHolder<ParticleType<?>, ParticleType<BlackFlatParticle.BlackFlatData>> BLACK_FLAT = REG.register("black_flat", () -> new ParticleType<BlackFlatParticle.BlackFlatData>(false) {
        @Override
        public MapCodec<BlackFlatParticle.BlackFlatData> codec() {
            return BlackFlatParticle.BlackFlatData.CODEC(this);
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, BlackFlatParticle.BlackFlatData> streamCodec() {
            return BlackFlatParticle.BlackFlatData.STREAM_CODEC(this);
        }
    });

    public static final DeferredHolder<ParticleType<?>, ParticleType<ParticleCloud.CloudData>> CLOUD = REG.register("cloud_soft", () -> new ParticleType<ParticleCloud.CloudData>(false) {
        @Override
        public MapCodec<ParticleCloud.CloudData> codec() {
            return ParticleCloud.CloudData.CODEC(this);
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, ParticleCloud.CloudData> streamCodec() {
            return ParticleCloud.CloudData.STREAM_CODEC(this);
        }
    });

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> TESLA_BULB_LIGHTNING = REG.register("tesla_bulb_lightning", () -> new SimpleParticleType(false));

    private static DeferredHolder<ParticleType<?>, SimpleParticleType> register(String key, boolean alwaysShow) {
        return REG.register(key, () -> new SimpleParticleType(alwaysShow));
    }

    private static DeferredHolder<ParticleType<?>, ParticleType<AdvancedParticleData>> register(String key) {
        return REG.register(key, () -> new ParticleType<AdvancedParticleData>(false) {
            @Override
            public MapCodec<AdvancedParticleData> codec() {
                return AdvancedParticleData.CODEC(this);
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, AdvancedParticleData> streamCodec() {
                return AdvancedParticleData.STREAM_CODEC(this);
            }
        });
    }

    private static DeferredHolder<ParticleType<?>, ParticleType<RibbonParticleData>> registerRibbon(String key) {
        return REG.register(key, () -> new ParticleType<RibbonParticleData>(false) {
            @Override
            public MapCodec<RibbonParticleData> codec() {
                return RibbonParticleData.CODEC_RIBBON(this);
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, RibbonParticleData> streamCodec() {
                return RibbonParticleData.STREAM_CODEC_RIBBON(this);
            }
        });
    }
}
