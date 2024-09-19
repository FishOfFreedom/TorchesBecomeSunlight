package com.freefish.torchesbecomesunlight.server.particle;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Function;

@Mod.EventBusSubscriber(modid = TorchesBecomeSunlight.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ParticleHandler {
    public static final DeferredRegister<ParticleType<?>> REG = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, TorchesBecomeSunlight.MOD_ID);
}
