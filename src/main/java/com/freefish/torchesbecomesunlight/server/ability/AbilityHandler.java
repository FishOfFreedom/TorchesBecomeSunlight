package com.freefish.torchesbecomesunlight.server.ability;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.ability.abilities.*;
import com.freefish.torchesbecomesunlight.server.capability.AbilityCapability;
import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.event.packet.toclient.MessageInterruptAbility;
import com.freefish.torchesbecomesunlight.server.event.packet.toclient.MessageUseAbility;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;

public enum AbilityHandler {
    INSTANCE;

    public static final AbilityType<Player, UseHalberdAbility> USE_HALBERD_ABILITY = new AbilityType<>("use_halberd_ability", UseHalberdAbility::new);
    public static final AbilityType<Player, UseHalberdChiAbility> USE_HALBERD_CHI_ABILITY = new AbilityType<>("use_halberd_chi_ability", UseHalberdChiAbility::new);
    public static final AbilityType<Player, UseMacheteAbility> USE_MACHETE_ABILITY = new AbilityType<>("use_machete_ability", UseMacheteAbility::new);
    public static final AbilityType<Player, UseMachete1Ability> USE_MACHETE1_ABILITY = new AbilityType<>("use_machete1_ability", UseMachete1Ability::new);
    public static final AbilityType<Player, UseShieldAbility> USE_SHIELD_ABILITY_ABILITY = new AbilityType<>("use_shield_ability", UseShieldAbility::new);
    public static final AbilityType<Player, UseWinterPassAbility> USE_PASS_ABILITY = new AbilityType<>("use_pass_ability", UseWinterPassAbility::new);
    public static final AbilityType<Player, UseWinterScratchAbility> USE_SCRATCH_ABILITY = new AbilityType<>("use_scratch_ability", UseWinterScratchAbility::new);
    public static final AbilityType<Player, UseGunAbility> USE_GUN_ABILITY = new AbilityType<>("use_gun_ability", UseGunAbility::new);
    public static final AbilityType<Player, UseSHalberdLightAbility> USE_SHALBERD_LIGHT_ABILITY = new AbilityType<>("use_slight_ability", UseSHalberdLightAbility::new);
    public static final AbilityType<Player, UseSHalberdWindAbility> USE_SHALBERD_WIND_ABILITY = new AbilityType<>("use_swind_ability", UseSHalberdWindAbility::new);
    public static final AbilityType<Player, UseSHalberdWindLightAbility> USE_SHALBERD_LIGHTWIND_ABILITY = new AbilityType<>("use_slightwind_ability", UseSHalberdWindLightAbility::new);
    public static final AbilityType<Player, UseRosmontisEmbraceAbility> USE_ROSMONTIS_EMBRACE_ABILITY = new AbilityType<>("use_embrace_ability", UseRosmontisEmbraceAbility::new);
    public static final AbilityType<Player, UseRosmontisGraspAbility> USE_ROSMONTIS_GRASP_ABILITY = new AbilityType<>("use_grasp_ability", UseRosmontisGraspAbility::new);
    public static final AbilityType<Player, UseSanktaRingAbility> USE_SANKTA_RING_ABILITY = new AbilityType<>("use_sanktaring_ability", UseSanktaRingAbility::new);
    public static final AbilityType<Player, UseIceBroadswordAbility> USE_ICE_BROADSWORD_ABILITY = new AbilityType<>("use_ice_broadsword_ability", UseIceBroadswordAbility::new);

    public static final AbilityType<Player, ? extends PlayerAbility>[] PLAYER_ABILITIES = new AbilityType[] {
            USE_HALBERD_ABILITY,USE_HALBERD_CHI_ABILITY,USE_MACHETE_ABILITY,USE_SHIELD_ABILITY_ABILITY,USE_MACHETE1_ABILITY,
    USE_PASS_ABILITY,USE_SCRATCH_ABILITY,USE_GUN_ABILITY,USE_SHALBERD_WIND_ABILITY,USE_SHALBERD_LIGHT_ABILITY,USE_SHALBERD_LIGHTWIND_ABILITY,
            USE_ROSMONTIS_EMBRACE_ABILITY,USE_ROSMONTIS_GRASP_ABILITY,USE_SANKTA_RING_ABILITY,USE_ICE_BROADSWORD_ABILITY};

    @Nullable
    public AbilityCapability.IAbilityCapability getAbilityCapability(LivingEntity entity) {
        return CapabilityHandle.getCapability(entity, CapabilityHandle.ABILITY_CAPABILITY);
    }

    @Nullable
    public Ability getAbility(LivingEntity entity, AbilityType<?, ?> abilityType) {
        AbilityCapability.IAbilityCapability abilityCapability = getAbilityCapability(entity);
        if (abilityCapability != null) {
            return abilityCapability.getAbilityMap().get(abilityType);
        }
        return null;
    }

    public <T extends LivingEntity> void sendAbilityMessage(T entity, AbilityType<?, ?> abilityType) {
        if (entity.level().isClientSide) {
            return;
        }
        AbilityCapability.IAbilityCapability abilityCapability = getAbilityCapability(entity);
        if (abilityCapability != null) {
            Ability instance = abilityCapability.getAbilityMap().get(abilityType);
            if (instance != null && instance.canUse()) {
                abilityCapability.activateAbility(entity, abilityType);
                TorchesBecomeSunlight.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new MessageUseAbility(entity.getId(), ArrayUtils.indexOf(abilityCapability.getAbilityTypesOnEntity(entity), abilityType)));
            }
        }
    }

    public <T extends LivingEntity> void sendInterruptAbilityMessage(T entity, AbilityType<?, ?> abilityType) {
        if (entity.level().isClientSide) {
            return;
        }
        AbilityCapability.IAbilityCapability abilityCapability = getAbilityCapability(entity);
        if (abilityCapability != null) {
            Ability instance = abilityCapability.getAbilityMap().get(abilityType);
            if (instance.isUsing()) {
                instance.interrupt();
                TorchesBecomeSunlight.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new MessageInterruptAbility(entity.getId(), ArrayUtils.indexOf(abilityCapability.getAbilityTypesOnEntity(entity), abilityType)));
            }
        }
    }
}