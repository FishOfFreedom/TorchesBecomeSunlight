package com.freefish.torchesbecomesunlight.server.init;

import net.minecraft.core.registries.BuiltInRegistries;
import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.effect.*;
import com.freefish.torchesbecomesunlight.server.effect.forceeffect.ForceEffectHandle;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class EffectHandle {
    public static final DeferredRegister<MobEffect> POTIONS =
            DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, TorchesBecomeSunlight.MOD_ID);

    public static final DeferredHolder<MobEffect, Freeze> FREEZE =
            POTIONS.<Freeze>register("freeze", Freeze::new);
    public static final DeferredHolder<MobEffect, DeepFear> DEEP_FEAR =
            POTIONS.<DeepFear>register("deep_fear", DeepFear::new);
    public static final DeferredHolder<MobEffect, Windigo> WINDIGO =
            POTIONS.<Windigo>register("windigo", Windigo::new);
    public static final DeferredHolder<MobEffect, SongOFGuerrilla> SONG_OF_GUERRILLA =
            POTIONS.<SongOFGuerrilla>register("song_of_guerrilla", SongOFGuerrilla::new);
    public static final DeferredHolder<MobEffect, Collapsal> COLLAPSAL =
            POTIONS.<Collapsal>register("collapsal", Collapsal::new);

    public static final DeferredHolder<MobEffect, FullOfEnergyEffect> FULL_OF_ENERGY =
            POTIONS.<FullOfEnergyEffect>register("full_of_energy", () -> new FullOfEnergyEffect(MobEffectCategory.BENEFICIAL, 5882118));

    public static void init(IEventBus bug){
        POTIONS.register(bug);
        ForceEffectHandle.init();
    }
}
