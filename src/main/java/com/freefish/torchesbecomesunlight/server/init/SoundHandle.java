package com.freefish.torchesbecomesunlight.server.init;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = TorchesBecomeSunlight.MOD_ID)
public class SoundHandle {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, TorchesBecomeSunlight.MOD_ID);

    // Rosmontis
    public static final RegistryObject<SoundEvent> ROS_START = create("ros.start");
    public static final RegistryObject<SoundEvent> ROS_BREAK_STONE = create("ros.break");
    public static final RegistryObject<SoundEvent> ROS_END = create("ros.end");
    public static final RegistryObject<SoundEvent> ROS_SKILL_1 = create("ros.skill_1");
    public static final RegistryObject<SoundEvent> ROS_SKILL_2 = create("ros.skill_2");
    public static final RegistryObject<SoundEvent> ROS_SKILL_3 = create("ros.skill_3");

    // GunKnight
    public static final RegistryObject<SoundEvent> GLOWING = create("gunknight.glowing");
    public static final RegistryObject<SoundEvent> MACHINE_GUN = create("gunknight.machinegun");
    public static final RegistryObject<SoundEvent> SHOT_GUN = create("gunknight.shotgun");
    public static final RegistryObject<SoundEvent> ARTILLERY = create("gunknight.artillery");
    public static final RegistryObject<SoundEvent> BEN = create("gunknight.ben");

    public static final RegistryObject<SoundEvent> BigLight = create("gunknight.big_light");
    public static final RegistryObject<SoundEvent> HolyLight = create("gunknight.holy_light");
    public static final RegistryObject<SoundEvent> ShotLight = create("gunknight.shot_light");
    public static final RegistryObject<SoundEvent> CycleWind = create("gunknight.cycle_wind");

    // SnowNova
    public static final RegistryObject<SoundEvent> ICE_CRYSTAL = create("snownova.ice_crystal");
    public static final RegistryObject<SoundEvent> ICE_WHIRLWIND = create("snownova.ice_whirlwind");
    public static final RegistryObject<SoundEvent> ICE_WIND = create("snownova.ice_wind");
    public static final RegistryObject<SoundEvent> ICE_DASH = create("snownova.ice_dash");
    public static final RegistryObject<SoundEvent> ICE_GROUND = create("snownova.ice_ground");

    // Patriot
    public static final RegistryObject<SoundEvent> GIANT_STEP = create("giant_step");
    public static final RegistryObject<SoundEvent> AXE_SWEPT = create("patriot.axe_swept");
    public static final RegistryObject<SoundEvent> AXE_HIT = create("patriot.axe_hit");
    public static final RegistryObject<SoundEvent> HIT = create("patriot.hit");
    public static final RegistryObject<SoundEvent> GROUND = create("patriot.ground");

    //Pursuer
    public static final RegistryObject<SoundEvent> BREATH = create("pursuer.breath");
    public static final RegistryObject<SoundEvent> BIG_BOOM = create("pursuer.bigbomb");
    public static final RegistryObject<SoundEvent> SWORD = create("pursuer.sword");
    public static final RegistryObject<SoundEvent> SHOOT = create("pursuer.shoot");
    public static final RegistryObject<SoundEvent> SPACE = create("pursuer.space");

    // Music
    public static final RegistryObject<SoundEvent> SNOWNOVA_LULLABYE = create("music.lullabye");
    public static final RegistryObject<SoundEvent> SNOWNOVA_PERMAFROST = create("music.permafrost");
    public static final RegistryObject<SoundEvent> PATRIOT_UNYIELDING = create("music.unyielding");
    public static final RegistryObject<SoundEvent> PURSUER_1 = create("music.pursuer_1");
    public static final RegistryObject<SoundEvent> GUN_KNIGHT_MUSIC_INTRO = create("music.gun_knight_intro");
    public static final RegistryObject<SoundEvent> GUN_KNIGHT_MUSIC_LOOP = create("music.gun_knight_loop");
    public static final RegistryObject<SoundEvent> PURSUER_2 = create("music.pursuer_2");
    public static final RegistryObject<SoundEvent> ROSMONTIS_INTRO = create("music.rosmontis_intro");
    public static final RegistryObject<SoundEvent> ROSMONTIS_LOOP = create("music.rosmontis_loop");

    private static RegistryObject<SoundEvent> create(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, name)));
    }
}
