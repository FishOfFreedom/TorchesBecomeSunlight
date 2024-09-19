package com.freefish.torchesbecomesunlight.server.sound;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = TorchesBecomeSunlight.MOD_ID)
public class SoundRegistry {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, TorchesBecomeSunlight.MOD_ID);

    // SnowNova
    public static final RegistryObject<SoundEvent> ICE_CRYSTAL = create("snownova.ice_crystal");
    public static final RegistryObject<SoundEvent> ICE_WHIRLWIND = create("snownova.ice_whirlwind");

    // Music
    public static final RegistryObject<SoundEvent> SNOWNOVA_LULLABYE = create("music.lullabye");
    public static final RegistryObject<SoundEvent> SNOWNOVA_PERMAFROST = create("music.permafrost");

    private static RegistryObject<SoundEvent> create(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, name)));
    }
}
