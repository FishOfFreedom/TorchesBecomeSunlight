package com.freefish.torchesbecomesunlight.server.partner;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.PlayerCapability;
import com.freefish.torchesbecomesunlight.server.event.packet.toclient.SynCapabilityMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

public class PartnerUtil {
    public static Partner<?> getPartner(Mob mob){
        return ((IMobPartner) mob).getPartner();
    }

    public static void removePartner(Mob mob,Player player){
        Partner<?> partner = ((IMobPartner) mob).getPartner();

        CompoundTag all = new CompoundTag();
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putInt("partnerid",mob.getId());
        all.put("removepartnerdata",compoundTag);

        TorchesBecomeSunlight.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SynCapabilityMessage(player,all));
        if(partner!=null) partner.remove();
        ((IMobPartner) mob).setPartner(null);
    }

    public static void removePartner(Partner<?> partner,Player player){
        Mob mob = partner.getPartnerMob();

        CompoundTag all = new CompoundTag();
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putInt("partnerid",mob.getId());
        all.put("removepartnerdata",compoundTag);

        TorchesBecomeSunlight.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SynCapabilityMessage(player,all));
        if(partner!=null) partner.remove();
        ((IMobPartner) mob).setPartner(null);
    }

    public static void setPartner(Mob mob,Partner<?> partner){
        ((IMobPartner) mob).setPartner(partner);
    }

    public static void startPartner(Player player, Mob mob, PartnerType<?> partnerType){
        PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(player, CapabilityHandle.PLAYER_CAPABILITY);
        if(capability!=null){
            Partner partner = partnerType.create();
            partner.init(player,mob);
            capability.addPartner(partner);
            ((IMobPartner) mob).setPartner(partner);
            if(!player.level().isClientSide){
                CompoundTag all = new CompoundTag();
                CompoundTag compoundTag = new CompoundTag();
                compoundTag.putInt("playerid",player.getId());
                compoundTag.putInt("partnerid",mob.getId());
                compoundTag.putString("partnertype",PartnerUtil.getKey(partnerType).toString());
                all.put("partnerdata",compoundTag);

                TorchesBecomeSunlight.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SynCapabilityMessage(player,all));
            }
        }
    }

    public static PartnerType<?> getValue(ResourceLocation resourceLocation){
        return PartnerHandler.PARTNER_TYPES.get(resourceLocation);
    }

    public static ResourceLocation getKey(PartnerType<?> partnerType){
        return PartnerHandler.PARTNER_TYPES.inverse().get(partnerType);
    }
}
