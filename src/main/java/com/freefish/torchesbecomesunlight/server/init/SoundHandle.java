package com.freefish.torchesbecomesunlight.server.init;

import net.neoforged.fml.common.EventBusSubscriber;
import net.minecraft.core.registries.BuiltInRegistries;
import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

@EventBusSubscriber(modid = TorchesBecomeSunlight.MOD_ID)
public class SoundHandle {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, TorchesBecomeSunlight.MOD_ID);

    // Rosmontis
    public static final DeferredHolder<SoundEvent, SoundEvent> ROS_START = create("ros.start");
    public static final DeferredHolder<SoundEvent, SoundEvent> ROS_BREAK_STONE = create("ros.break");
    public static final DeferredHolder<SoundEvent, SoundEvent> ROS_END = create("ros.end");
    public static final DeferredHolder<SoundEvent, SoundEvent> ROS_SKILL_1 = create("ros.skill_1");
    public static final DeferredHolder<SoundEvent, SoundEvent> ROS_SKILL_2 = create("ros.skill_2");
    public static final DeferredHolder<SoundEvent, SoundEvent> ROS_SKILL_3 = create("ros.skill_3");

    // GunKnight
    public static final DeferredHolder<SoundEvent, SoundEvent> GLOWING = create("gunknight.glowing");
    public static final DeferredHolder<SoundEvent, SoundEvent> MACHINE_GUN = create("gunknight.machinegun");
    public static final DeferredHolder<SoundEvent, SoundEvent> SHOT_GUN = create("gunknight.shotgun");
    public static final DeferredHolder<SoundEvent, SoundEvent> ARTILLERY = create("gunknight.artillery");
    public static final DeferredHolder<SoundEvent, SoundEvent> BEN = create("gunknight.ben");

    public static final DeferredHolder<SoundEvent, SoundEvent> BigLight = create("gunknight.big_light");
    public static final DeferredHolder<SoundEvent, SoundEvent> HolyLight = create("gunknight.holy_light");
    public static final DeferredHolder<SoundEvent, SoundEvent> ShotLight = create("gunknight.shot_light");
    public static final DeferredHolder<SoundEvent, SoundEvent> CycleWind = create("gunknight.cycle_wind");

    // SnowNova
    public static final DeferredHolder<SoundEvent, SoundEvent> ICE_CRYSTAL = create("snownova.ice_crystal");
    public static final DeferredHolder<SoundEvent, SoundEvent> ICE_WHIRLWIND = create("snownova.ice_whirlwind");
    public static final DeferredHolder<SoundEvent, SoundEvent> ICE_WIND = create("snownova.ice_wind");
    public static final DeferredHolder<SoundEvent, SoundEvent> ICE_DASH = create("snownova.ice_dash");
    public static final DeferredHolder<SoundEvent, SoundEvent> ICE_GROUND = create("snownova.ice_ground");

    // Patriot
    public static final DeferredHolder<SoundEvent, SoundEvent> GIANT_STEP = create("giant_step");
    public static final DeferredHolder<SoundEvent, SoundEvent> AXE_SWEPT = create("patriot.axe_swept");
    public static final DeferredHolder<SoundEvent, SoundEvent> AXE_HIT = create("patriot.axe_hit");
    public static final DeferredHolder<SoundEvent, SoundEvent> HIT = create("patriot.hit");
    public static final DeferredHolder<SoundEvent, SoundEvent> GROUND = create("patriot.ground");

    //Pursuer
    public static final DeferredHolder<SoundEvent, SoundEvent> BREATH = create("pursuer.breath");
    public static final DeferredHolder<SoundEvent, SoundEvent> BIG_BOOM = create("pursuer.bigbomb");
    public static final DeferredHolder<SoundEvent, SoundEvent> SWORD = create("pursuer.sword");
    public static final DeferredHolder<SoundEvent, SoundEvent> SHOOT = create("pursuer.shoot");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPACE = create("pursuer.space");

    // Music
    public static final DeferredHolder<SoundEvent, SoundEvent> SNOWNOVA_LULLABYE = create("music.lullabye");
    public static final DeferredHolder<SoundEvent, SoundEvent> SNOWNOVA_PERMAFROST = create("music.permafrost");
    public static final DeferredHolder<SoundEvent, SoundEvent> PATRIOT_UNYIELDING = create("music.unyielding");
    public static final DeferredHolder<SoundEvent, SoundEvent> PURSUER_1 = create("music.pursuer_1");
    public static final DeferredHolder<SoundEvent, SoundEvent> GUN_KNIGHT_MUSIC_INTRO = create("music.gun_knight_intro");
    public static final DeferredHolder<SoundEvent, SoundEvent> GUN_KNIGHT_MUSIC_LOOP = create("music.gun_knight_loop");
    public static final DeferredHolder<SoundEvent, SoundEvent> PURSUER_2 = create("music.pursuer_2");
    public static final DeferredHolder<SoundEvent, SoundEvent> ROSMONTIS_INTRO = create("music.rosmontis_intro");
    public static final DeferredHolder<SoundEvent, SoundEvent> ROSMONTIS_LOOP = create("music.rosmontis_loop");

    private static DeferredHolder<SoundEvent, SoundEvent> create(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, name)));
    }
}
