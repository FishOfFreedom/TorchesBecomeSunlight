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

    // Music
    public static final RegistryObject<SoundEvent> SNOWNOVA_LULLABYE = create("music.lullabye");
    public static final RegistryObject<SoundEvent> SNOWNOVA_PERMAFROST = create("music.permafrost");
    public static final RegistryObject<SoundEvent> PATRIOT_UNYIELDING = create("music.unyielding");

    private static RegistryObject<SoundEvent> create(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, name)));
    }
}
