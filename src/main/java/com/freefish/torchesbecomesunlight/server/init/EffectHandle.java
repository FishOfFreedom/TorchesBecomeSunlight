package com.freefish.torchesbecomesunlight.server.init;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.effect.*;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EffectHandle {
    public static final DeferredRegister<MobEffect> POTIONS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, TorchesBecomeSunlight.MOD_ID);

    public static final RegistryObject<Freeze> FREEZE =
            POTIONS.<Freeze>register("freeze", Freeze::new);
    public static final RegistryObject<DeepFear> DEEP_FEAR =
            POTIONS.<DeepFear>register("deep_fear", DeepFear::new);
    public static final RegistryObject<Windigo> WINDIGO =
            POTIONS.<Windigo>register("windigo", Windigo::new);
    public static final RegistryObject<SongOFGuerrilla> SONG_OF_GUERRILLA =
            POTIONS.<SongOFGuerrilla>register("song_of_guerrilla", SongOFGuerrilla::new);
    public static final RegistryObject<Collapsal> COLLAPSAL =
            POTIONS.<Collapsal>register("collapsal", Collapsal::new);
}
