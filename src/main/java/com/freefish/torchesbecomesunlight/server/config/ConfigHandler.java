package com.freefish.torchesbecomesunlight.server.config;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static net.minecraftforge.common.ForgeConfigSpec.*;

@Mod.EventBusSubscriber(modid = TorchesBecomeSunlight.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ConfigHandler {
    private ConfigHandler() {}

    public static final Common COMMON;
    public static final Client CLIENT;

    private static final String LANG_PREFIX = "config." + TorchesBecomeSunlight.MOD_ID + ".";

    private static final Builder COMMON_BUILDER = new Builder();
    private static final Builder CLIENT_BUILDER = new Builder();

    public static ForgeConfigSpec COMMON_CONFIG;
    public static ForgeConfigSpec CLIENT_CONFIG;

    private static final Predicate<Object> STRING_PREDICATE = s -> s instanceof String;

    static {
        COMMON = new Common(COMMON_BUILDER);
        CLIENT = new Client(CLIENT_BUILDER);

        COMMON_CONFIG = COMMON_BUILDER.build();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    public static class Common {
        private Common(final Builder builder) {
            builder.push("server");

            MOBS = new Mobs(builder);
            GLOBALSETTING = new GlobalSettingConfig(builder);
            this.spawnDemon = builder.translation(LANG_PREFIX + "spawnDemon")
                    .define("spawnDemon", false);
            builder.pop();
        }

        public final Mobs MOBS;

        public final GlobalSettingConfig GLOBALSETTING;

        public final BooleanValue spawnDemon;
    }

    public static class Client {
        private Client(final Builder builder) {
            builder.push("client");
            this.demonRender = builder.comment("close demonRender, which may look bad with certain shaders.")
                    .translation(LANG_PREFIX + "demonRender")
                    .define("demonRender", false);
            builder.pop();
        }

        public final BooleanValue demonRender;
    }

    public static class CombatConfig {
        CombatConfig(final Builder builder, float healthMultiplier, float attackMultiplier) {
            builder.push("attribute_config");
            this.healthMultiplier = builder.comment("Regulate mob health by this value")
                    .translation(LANG_PREFIX + "health_multiplier")
                    .defineInRange("Health multiplier", healthMultiplier, 0d, Double.MAX_VALUE);
            this.attackMultiplier = builder.comment("Regulate mob attack damage by this value")
                    .translation(LANG_PREFIX + "attack_multiplier")
                    .defineInRange("Attack multiplier", attackMultiplier, 0d, Double.MAX_VALUE);
            builder.pop();
        }

        public final DoubleValue healthMultiplier;

        public final DoubleValue attackMultiplier;
    }

    public static class CustomBossBarConfig {
        CustomBossBarConfig(final Builder builder) {
            builder.push("bossbar");
            this.isOpenCustombossbar = builder.comment("Enable custom bossbar")
                    .translation(LANG_PREFIX + "bossbar")
                    .define("Enable or not",true);
            builder.pop();
        }

        public final BooleanValue isOpenCustombossbar;
    }

    public static class SpawnConfig {
        SpawnConfig(final Builder builder, int spawnRate) {
            builder.comment("Regulate mob spawning");
            builder.push("spawn_config");
            this.spawnRate = builder.comment("Regulate spawn rate, 0 to disable spawning")
                    .translation(LANG_PREFIX + "spawn_rate")
                    .defineInRange("spawn_rate", spawnRate, 0, Integer.MAX_VALUE);
            builder.pop();
        }

        public final IntValue spawnRate;
    }

    public static class Mobs {
        Mobs(final Builder builder) {
            builder.push("mobs");
            GUN_KNIGHT = new GunKnight(builder);
            PATRIOT = new Patriot(builder);
            FROSTNOVA = new FrostNova(builder);
            PURSUER = new Pursuer(builder);
            builder.pop();
        }

        public final GunKnight GUN_KNIGHT;

        public final Patriot PATRIOT;

        public final FrostNova FROSTNOVA;

        public final Pursuer PURSUER;
    }

    public static class GunKnight {
        GunKnight(final Builder builder) {
            builder.push("gun_knight");
            combatConfig = new CombatConfig(builder, 1, 1);
            customBossBarConfig = new CustomBossBarConfig(builder);
            generationConfig = new GenerationConfig(builder,
                    50, 100
            );

            isFrontalAttack = builder.comment("If 'true' disable frontal attack").define("nullify a frontal attack", true);

            damageConfig = new GeneralDamageCap(builder,0.1);
            spawnConfig = new SpawnConfig(builder,1);
            builder.pop();
        }

        public final CombatConfig combatConfig;
        public final CustomBossBarConfig customBossBarConfig;
        public final GenerationConfig generationConfig;
        public final SpawnConfig spawnConfig;
        public final GeneralDamageCap damageConfig;
        public final BooleanValue isFrontalAttack;
    }

    public static class FrostNova {
        FrostNova(final Builder builder) {
            builder.push("frost_nova");
            combatConfig = new CombatConfig(builder, 1, 1);
            customBossBarConfig = new CustomBossBarConfig(builder);
            damageConfig = new GeneralDamageCap(builder,0.1);
            spawnConfig = new SpawnConfig(builder,1);
            builder.pop();
        }

        public final CombatConfig combatConfig;
        public final CustomBossBarConfig customBossBarConfig;

        public final SpawnConfig spawnConfig;
        public final GeneralDamageCap damageConfig;
    }

    public static class Patriot {
        Patriot(final Builder builder) {
            builder.push("patriot");
            combatConfig = new CombatConfig(builder, 1, 1);
            customBossBarConfig = new CustomBossBarConfig(builder);
            isFrontalAttack = builder.comment("If 'true' disable frontal attack").define("nullify a frontal attack", true);

            damageConfig = new GeneralDamageCap(builder,0.1);
            spawnConfig = new SpawnConfig(builder,1);
            builder.pop();
        }

        public final CombatConfig combatConfig;
        public final CustomBossBarConfig customBossBarConfig;

        public final SpawnConfig spawnConfig;
        public final GeneralDamageCap damageConfig;

        public final BooleanValue isFrontalAttack;
    }

    public static class Pursuer {
        Pursuer(final Builder builder) {
            builder.push("pursuer");
            combatConfig = new CombatConfig(builder, 1, 1);
            customBossBarConfig = new CustomBossBarConfig(builder);
            damageConfig = new GeneralDamageCap(builder,0.1);
            spawnConfig = new SpawnConfig(builder,1);

            builder.pop();
        }

        public final CombatConfig combatConfig;
        public final CustomBossBarConfig customBossBarConfig;

        public final SpawnConfig spawnConfig;

        public final GeneralDamageCap damageConfig;
    }

    public static class GenerationConfig {
        GenerationConfig(final Builder builder, float heightMin, float heightMax) {
            builder.comment("Regulate spawning structure");
            builder.push("structure_generation_config");
            this.heightMax = builder.comment("Maximum height for generation placement. -65 to ignore")
                    .translation(LANG_PREFIX + "height_max")
                    .defineInRange("height_max", heightMax, -65, 256);
            this.heightMin = builder.comment("Minimum height for generation placement. -65 to ignore")
                    .translation(LANG_PREFIX + "height_min")
                    .defineInRange("height_min", heightMin, -65, 256);
            builder.pop();
        }

        public final DoubleValue heightMin;

        public final DoubleValue heightMax;
    }

    public static class GeneralDamageCap {
        public GeneralDamageCap(final Builder builder,double damageCapPercentage) {
            this.damageCap = builder.comment("Set damage limit percentage (based on max health)")
                    .defineInRange("damage limit percentage", damageCapPercentage, 0.01D, 1D);
        }

        public final DoubleValue damageCap;
    }

    public static class GlobalSettingConfig {
        public GlobalSettingConfig(final Builder builder) {
            builder.push("global_setting");
            this.damageCap = builder.comment("Whether living can talk(experiment)")
                    .define("Enable living can talk", false);

            this.ursusvillage = new URSUSVILLAGE(builder);

            builder.pop();
        }

        public final BooleanValue damageCap;

        public final URSUSVILLAGE ursusvillage;
    }

    public static class StructureConfig {
        StructureConfig(final ForgeConfigSpec.Builder builder,boolean canGenerate, List<String> avoidStructures) {
            builder.comment("Controls for spawning structure/mob with world generation");
            builder.push("generation_config");
            this.canGenerate = builder.comment("false causes disable generate")
                    .translation(LANG_PREFIX + "generation_distance")
                    .define("avoid_structures", canGenerate);
            builder.pop();
        }
        public final BooleanValue canGenerate;
    }

    public static class URSUSVILLAGE {
        URSUSVILLAGE(final Builder builder) {
            builder.push("ursus_village");
            structureConfig = new StructureConfig(builder, false, new ArrayList<>());
            builder.pop();
        }

        public final StructureConfig structureConfig;
    }
}
