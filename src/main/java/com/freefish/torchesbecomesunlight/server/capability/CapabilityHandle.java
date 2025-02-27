package com.freefish.torchesbecomesunlight.server.capability;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import javax.annotation.Nullable;

public class CapabilityHandle {
    public static final Capability<AbilityCapability.IAbilityCapability> ABILITY_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<PlayerCapability.IPlayerCapability> PLAYER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<FrozenCapability.IFrozenCapability> FROZEN_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(AbilityCapability.IAbilityCapability.class);
        event.register(PlayerCapability.IPlayerCapability.class);
        event.register(FrozenCapability.IFrozenCapability.class);
    }

    public static void attachEntityCapability(AttachCapabilitiesEvent<Entity> e) {
        if (e.getObject() instanceof LivingEntity) {
            if (e.getObject() instanceof Player) {
                e.addCapability(AbilityCapability.ID, new AbilityCapability.Provider());
                e.addCapability(PlayerCapability.ID, new PlayerCapability.Provider());
            }
            e.addCapability(FrozenCapability.ID, new FrozenCapability.Provider());
        }
    }

    @Nullable
    public static <T> T getCapability(Entity entity, Capability<T> capability) {
        if (entity == null) return null;
        if (entity.isRemoved()) return null;
        return entity.getCapability(capability).isPresent() ? entity.getCapability(capability).orElseThrow(() -> new IllegalArgumentException("Lazy optional must not be empty")) : null;
    }
}
