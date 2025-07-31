package com.freefish.torchesbecomesunlight.server.init;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.effect.*;
import com.freefish.torchesbecomesunlight.server.effect.forceeffect.ForceEffectHandle;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
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

    public static final RegistryObject<FullOfEnergyEffect> FULL_OF_ENERGY =
            POTIONS.<FullOfEnergyEffect>register("full_of_energy", () -> new FullOfEnergyEffect(MobEffectCategory.BENEFICIAL, 5882118));

    public static void init(IEventBus bug){
        POTIONS.register(bug);
        ForceEffectHandle.init();
    }
}
