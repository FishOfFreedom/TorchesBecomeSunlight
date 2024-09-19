package com.freefish.torchesbecomesunlight.server.effect;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EffectRegistry {
    public static final DeferredRegister<MobEffect> POTIONS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, TorchesBecomeSunlight.MOD_ID);

    public static final RegistryObject<Freeze> FREEZE =
            POTIONS.<Freeze>register("freeze", Freeze::new);
}
