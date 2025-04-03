package com.freefish.torchesbecomesunlight.server.partner;

import com.freefish.torchesbecomesunlight.server.capability.AbilityCapability;
import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;

public enum PartnerHandler {
    INSTANCE;

    public static final PartnerType<LivingEntity, ? extends Partner>[] PLAYER_ABILITIES = new PartnerType[] {
            };

    //@Nullable
    //public AbilityCapability.IAbilityCapability getPartnerCapability(LivingEntity entity) {
    //    return CapabilityHandle.getCapability(entity, CapabilityHandle.ABILITY_CAPABILITY);
    //}
//
    //@Nullable
    //public Partner getPartner(LivingEntity entity, PartnerType<?, ?> abilityType) {
    //    AbilityCapability.IAbilityCapability abilityCapability = getPartnerCapability(entity);
    //    if (abilityCapability != null) {
    //        return abilityCapability.getPartnerMap().get(abilityType);
    //    }
    //    return null;
    //}
//
    //public <T extends LivingEntity> void sendPartnerMessage(T entity, PartnerType<?, ?> abilityType) {
    //    if (entity.level().isClientSide) {
    //        return;
    //    }
    //    PartnerCapability.IPartnerCapability abilityCapability = getPartnerCapability(entity);
    //    if (abilityCapability != null) {
    //        Partner instance = abilityCapability.getPartnerMap().get(abilityType);
    //        if (instance != null && instance.canUse()) {
    //            abilityCapability.activatePartner(entity, abilityType);
    //            TorchesBecomeSunlight.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new MessageUsePartner(entity.getId(), ArrayUtils.indexOf(abilityCapability.getPartnerTypesOnEntity(entity), abilityType)));
    //        }
    //    }
    //}
//
    //public <T extends LivingEntity> void sendInterruptPartnerMessage(T entity, PartnerType<?, ?> abilityType) {
    //    if (entity.level().isClientSide) {
    //        return;
    //    }
    //    PartnerCapability.IPartnerCapability abilityCapability = getPartnerCapability(entity);
    //    if (abilityCapability != null) {
    //        Partner instance = abilityCapability.getPartnerMap().get(abilityType);
    //        if (instance.isUsing()) {
    //            instance.interrupt();
    //            TorchesBecomeSunlight.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new MessageInterruptPartner(entity.getId(), ArrayUtils.indexOf(abilityCapability.getPartnerTypesOnEntity(entity), abilityType)));
    //        }
    //    }
    //}
}
