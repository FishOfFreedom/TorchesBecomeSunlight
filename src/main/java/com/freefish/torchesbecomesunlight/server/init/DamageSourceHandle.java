package com.freefish.torchesbecomesunlight.server.init;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public class DamageSourceHandle {
    public static DamageSource SnowMonsterFrozen(Entity snowMonster) {
        return new DamageSource(snowMonster.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).
                getHolderOrThrow(registerResourceKey(Registries.DAMAGE_TYPE, "snow_monster_frozen")), snowMonster);
    }

    private static <T> ResourceKey<T> registerResourceKey(ResourceKey<Registry<T>> registry, String key) {
        return ResourceKey.create(registry, new ResourceLocation(TorchesBecomeSunlight.MOD_ID, key));
    }
}
