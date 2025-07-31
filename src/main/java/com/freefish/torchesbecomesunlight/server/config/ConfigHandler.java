package com.freefish.torchesbecomesunlight.server.config;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

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
            TOOLs = new TOOLS(builder);
            GLOBALSETTING = new GlobalSettingConfig(builder);
            this.spawnDemon = builder.translation(LANG_PREFIX + "spawnDemon")
                    .define("spawnDemon", false);
            builder.pop();
        }

        public final Mobs MOBS;

        public final TOOLS TOOLs;

        public final GlobalSettingConfig GLOBALSETTING;

        public final BooleanValue spawnDemon;
    }

    public static class Client {
        private Client(final Builder builder) {
            builder.push("client");
            this.demonRender = builder.comment("demonRender")
                    .define("true enables the demonRender", false);

            this.playerAnimationF = builder.comment("choose whether to enable first player-customized animations")
                    .define("true enables the first animation to be played", true);

            this.playerAnimationT = builder.comment("choose whether to enable Third player-customized animations")
                    .define("true enables the third animation to be played", true);

            builder.pop();
        }

        public final BooleanValue demonRender;
        public final BooleanValue playerAnimationT;
        public final BooleanValue playerAnimationF;
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

    public static class NeutralProtectionConfig {
        NeutralProtectionConfig(final Builder builder, float distance) {
            builder.push("neutral_protection_config");
            this.healthMultiplier = builder.comment("Can take unsourced damage without an attack target")
                    .define("can damage", false);
            this.attackMultiplier = builder.comment("How far of sourced damage can be ignored when there is no target to attack")
                    .defineInRange("can damage distance", distance, 0d, Double.MAX_VALUE);
            builder.pop();
        }

        public final BooleanValue healthMultiplier;
        public final DoubleValue attackMultiplier;
    }

    public static class PartnerConfig {
        PartnerConfig(final Builder builder, float healthMultiplier, float attackMultiplier) {
            builder.push("partner_attribute_config");
            this.healthMultiplier = builder.comment("Regulate partner mob health by this value")
                    .translation(LANG_PREFIX + "health_multiplier")
                    .defineInRange("Health multiplier", healthMultiplier, 0d, Double.MAX_VALUE);
            this.attackMultiplier = builder.comment("Regulate partner mob attack damage by this value")
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
            PATROL_CAPTAIN = new PatrolCaptain(builder);
            MANGLER = new Mangler(builder);
            BURDENBEAST = new Burdenbeast(builder);
            FROSTNOVA = new FrostNova(builder);
            ROSMONTIS = new Rosmontis(builder);
            PURSUER = new Pursuer(builder);
            builder.pop();
        }

        public final GunKnight GUN_KNIGHT;
        public final PatrolCaptain PATROL_CAPTAIN;
        public final Mangler MANGLER;
        public final Burdenbeast BURDENBEAST;

        public final Patriot PATRIOT;

        public final FrostNova FROSTNOVA;

        public final Rosmontis ROSMONTIS;

        public final Pursuer PURSUER;
    }

    public static class TOOLS {
        TOOLS(final Builder builder) {
            builder.push("tools");
            HALBERD = new Halberd(builder);
            SHIELD = new Shield(builder);
            SCRATCH = new Scratch(builder);
            WINTER_PASS = new WinterPass(builder);

            ICEBROADSWORD = new ICEBroadSword(builder);
            SANKTA_RING = new SanktaRing(builder);

            PHANTOM_GRASP = new PhantomGrasp(builder);
            SACRED_HALBERD = new SacredHalberd(builder);
            SACRED_GUN = new SacredGun(builder);
            MACHETE = new Machete(builder);
            builder.pop();
        }

        public final Halberd HALBERD;
        public final SanktaRing SANKTA_RING;

        public final ICEBroadSword ICEBROADSWORD;

        public final Scratch SCRATCH;

        public final Shield SHIELD;
        public final PhantomGrasp PHANTOM_GRASP;

        public final SacredHalberd SACRED_HALBERD;

        public final SacredGun SACRED_GUN;

        public final Machete MACHETE;

        public final WinterPass WINTER_PASS;

    }

    public static class PatrolCaptain {
        PatrolCaptain(final Builder builder) {
            builder.push("patrol_captain");
            spawnConfig = new SpawnConfig(builder,2);
            builder.pop();
        }

        public final SpawnConfig spawnConfig;
    }

    public static class Mangler {
        Mangler(final Builder builder) {
            builder.push("mangler");
            spawnConfig = new SpawnConfig(builder,2);
            builder.pop();
        }

        public final SpawnConfig spawnConfig;
    }

    public static class Burdenbeast {
        Burdenbeast(final Builder builder) {
            builder.push("burdenbeast");
            spawnConfig = new SpawnConfig(builder,2);
            builder.pop();
        }

        public final SpawnConfig spawnConfig;
    }

    public static class GunKnight {
        GunKnight(final Builder builder) {
            builder.push("gun_knight");
            combatConfig = new CombatConfig(builder, 1, 1);
            neutralProtectionConfig = new NeutralProtectionConfig(builder, 16);
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
        public final NeutralProtectionConfig neutralProtectionConfig;
        public final GenerationConfig generationConfig;
        public final SpawnConfig spawnConfig;
        public final GeneralDamageCap damageConfig;
        public final BooleanValue isFrontalAttack;
    }

    public static class FrostNova {
        FrostNova(final Builder builder) {
            builder.push("frost_nova");
            combatConfig = new CombatConfig(builder, 1, 1);
            partnerConfig = new PartnerConfig(builder, 1, 1);
            neutralProtectionConfig = new NeutralProtectionConfig(builder, 16);
            customBossBarConfig = new CustomBossBarConfig(builder);
            damageConfig = new GeneralDamageCap(builder,0.1);
            spawnConfig = new SpawnConfig(builder,1);
            builder.pop();
        }

        public final CombatConfig combatConfig;
        public final PartnerConfig partnerConfig;
        public final NeutralProtectionConfig neutralProtectionConfig;
        public final CustomBossBarConfig customBossBarConfig;

        public final SpawnConfig spawnConfig;
        public final GeneralDamageCap damageConfig;
    }

    public static class Rosmontis {
        Rosmontis(final Builder builder) {
            builder.push("rosmontis");
            combatConfig = new CombatConfig(builder, 1, 1);
            partnerConfig = new PartnerConfig(builder, 1, 1);
            neutralProtectionConfig = new NeutralProtectionConfig(builder, 16);
            customBossBarConfig = new CustomBossBarConfig(builder);
            damageConfig = new GeneralDamageCap(builder,0.1);
            spawnConfig = new SpawnConfig(builder,1);
            builder.pop();
        }

        public final CombatConfig combatConfig;
        public final PartnerConfig partnerConfig;
        public final CustomBossBarConfig customBossBarConfig;
        public final NeutralProtectionConfig neutralProtectionConfig;

        public final SpawnConfig spawnConfig;
        public final GeneralDamageCap damageConfig;
    }

    public static class Patriot {
        Patriot(final Builder builder) {
            builder.push("patriot");
            combatConfig = new CombatConfig(builder, 1, 1);
            partnerConfig = new PartnerConfig(builder, 1, 1);
            customBossBarConfig = new CustomBossBarConfig(builder);
            neutralProtectionConfig = new NeutralProtectionConfig(builder, 16);
            isFrontalAttack = builder.comment("If 'true' disable frontal attack").define("nullify a frontal attack", true);

            damageConfig = new GeneralDamageCap(builder,0.1);
            spawnConfig = new SpawnConfig(builder,1);
            builder.pop();
        }

        public final CombatConfig combatConfig;
        public final PartnerConfig partnerConfig;
        public final CustomBossBarConfig customBossBarConfig;
        public final NeutralProtectionConfig neutralProtectionConfig;

        public final SpawnConfig spawnConfig;
        public final GeneralDamageCap damageConfig;

        public final BooleanValue isFrontalAttack;
    }

    public static class Halberd {
        Halberd(final Builder builder) {
            builder.push("halberd");
            attackDamage = builder.comment("tool attack damage")
                            .defineInRange("attack_damage",20,0d,Double.MAX_VALUE);

            skillAmount1 = builder.comment("skill 1 numerical requirements")
                    .defineInRange("requirements_1",10,0,100);

            skillAmount2 = builder.comment("skill 2 numerical requirements")
                    .defineInRange("requirements_2",50,0,100);

            builder.pop();
        }

        public final IntValue skillAmount1;
        public final IntValue skillAmount2;
        public final DoubleValue attackDamage;
        public float attackDamageValue = 20;
    }

    public static class ICEBroadSword {
        ICEBroadSword(final Builder builder) {
            builder.push("ice_broadsword");
            attackDamage = builder.comment("tool attack damage")
                    .defineInRange("attack_damage",10,0d,Double.MAX_VALUE);

            skillAmount1 = builder.comment("skill 1 numerical requirements")
                    .defineInRange("requirements_1",35,0,100);

            builder.pop();
        }

        public final IntValue skillAmount1;
        public final DoubleValue attackDamage;
        public float attackDamageValue = 10;
    }

    public static class SanktaRing {
        SanktaRing(final Builder builder) {
            builder.push("sankta_ring");

            skillAmount1 = builder.comment("skill 1 numerical requirements")
                    .defineInRange("requirements_1",5,0,100);

            builder.pop();
        }

        public final IntValue skillAmount1;
    }

    public static class Scratch {
        Scratch(final Builder builder) {
            builder.push("scratch");

            skillAmount1 = builder.comment("skill 1 numerical requirements")
                    .defineInRange("requirements_1",60,0,100);

            builder.pop();
        }

        public final IntValue skillAmount1;
    }

    public static class Shield {
        Shield(final Builder builder) {
            builder.push("shield");

            skillAmount1 = builder.comment("skill 1 numerical requirements")
                    .defineInRange("requirements_1",10,0,100);

            builder.pop();
        }

        public final IntValue skillAmount1;
    }

    public static class PhantomGrasp {
        PhantomGrasp(final Builder builder) {
            builder.push("phantom_grasp");
            attackDamage = builder.comment("tool attack damage")
                    .defineInRange("attack_damage",4,0d,Double.MAX_VALUE);

            skillAmount1 = builder.comment("skill1 numerical requirements")
                    .defineInRange("requirements1",20,0,100);

            builder.pop();
        }

        public final IntValue skillAmount1;
        public final DoubleValue attackDamage;
        public float attackDamageValue = 2;
    }

    public static class SacredHalberd {
        SacredHalberd(final Builder builder) {
            builder.push("sacred_halberd");
            attackDamage = builder.comment("tool attack damage")
                    .defineInRange("attack_damage",24,0d,Double.MAX_VALUE);

            skillAmount1 = builder.comment("skill light numerical requirements")
                    .defineInRange("light_requirements",10,0,100);

            skillAmount2 = builder.comment("skill wind numerical requirements")
                    .defineInRange("wind_requirements",70,0,100);
            builder.pop();
        }

        public final IntValue skillAmount1;
        public final IntValue skillAmount2;
        public final DoubleValue attackDamage;
        public float attackDamageValue = 24;
    }

    public static class SacredGun {
        SacredGun(final Builder builder) {
            builder.push("sacred_gun");
            attackDamage = builder.comment("tool attack damage")
                    .defineInRange("attack_damage",10,0d,Double.MAX_VALUE);

            skillAmount1 = builder.comment("skill numerical requirements")
                    .defineInRange("requirements",60,0,100);

            builder.pop();
        }

        public final IntValue skillAmount1;
        public final DoubleValue attackDamage;
    }

    public static class Machete {
        Machete(final Builder builder) {
            builder.push("machete");
            attackDamage = builder.comment("tool attack damage")
                    .defineInRange("attack_damage",14,0d,Double.MAX_VALUE);

            skillAmount = builder.comment("skill numerical requirements")
                    .defineInRange("requirements",45,0,100);
            builder.pop();
        }

        public final DoubleValue attackDamage;
        public final IntValue skillAmount;
        public float attackDamageValue = 14;
    }

    public static class WinterPass {
        WinterPass(final Builder builder) {
            builder.push("winter_pass");
            attackDamage = builder.comment("tool attack damage")
                    .defineInRange("attack_damage",6,0d,Double.MAX_VALUE);

            skillAmount1 = builder.comment("skill1 numerical requirements")
                    .defineInRange("requirements1",50,0,100);
            builder.pop();
        }

        public final IntValue skillAmount1;
        public final DoubleValue attackDamage;
        public float attackDamageValue = 6;
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
            this.canDialogue = builder.comment("Whether living can talk")
                    .define("Enable living can talk", true);


            this.canDialogueAttack = builder.comment("Whether attack living when dialogue")
                    .define("Enable attack living when dialogue", false);

            this.healthBarIsNearShow = builder.comment("Whether boss show healthBar")
                    .define("Enable bossBar can show", false);

            this.ursusVillage = new GenerationConfig(builder,20,150);

            this.sanktaStatue = new GenerationConfig(builder,20,150);

            this.rhodeTrainingGround = new GenerationConfig(builder,20,150);

            builder.pop();
        }

        public final BooleanValue canDialogue;
        public final BooleanValue canDialogueAttack;

        public final BooleanValue healthBarIsNearShow;

        public final GenerationConfig ursusVillage;

        public final GenerationConfig sanktaStatue;

        public final GenerationConfig rhodeTrainingGround;
    }
}
