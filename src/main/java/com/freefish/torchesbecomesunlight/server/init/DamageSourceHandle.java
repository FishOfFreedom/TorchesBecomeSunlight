package com.freefish.torchesbecomesunlight.server.init;

import com.freefish.torchesbecomesunlight.server.init.generator.CustomResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public class DamageSourceHandle {
    public static DamageSource SnowMonsterFrozen(Entity snowMonster) {
        return new DamageSource(snowMonster.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).
                getHolderOrThrow(CustomResourceKey.FROST_ATTACK), snowMonster);
    }

    public static DamageSource demonAttack(Entity snowMonster) {
        return new DamageSource(snowMonster.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).
                getHolderOrThrow(CustomResourceKey.DEMON_ATTACK), snowMonster);
    }

    public static DamageSource noTriggerAttack(Entity player) {
        return new DamageSource(player.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).
                getHolderOrThrow(CustomResourceKey.NO_TRIGGER_ATTACK), player);
    }

    public static DamageSource noTriggerNoArmorAttack(Entity player) {
        return new DamageSource(player.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).
                getHolderOrThrow(CustomResourceKey.NO_TRIGGER_WITHOUT_ARMOR_ATTACK), player);
    }
}
