package com.freefish.torchesbecomesunlight.server.event.packet.toserver;

import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.PlayerCapability;
import com.freefish.torchesbecomesunlight.server.partner.Partner;
import com.freefish.torchesbecomesunlight.server.partner.PartnerPlayerManager;
import com.freefish.torchesbecomesunlight.server.partner.command.PartnerCommandBasic;
import com.freefish.torchesbecomesunlight.server.partner.command.triggertype.TriggerBasic;
import com.freefish.torchesbecomesunlight.server.partner.command.triggertype.TriggerBasicType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class PartnerCommandTriggerMessage {
    private int entityID;
    private int commandIndex;
    private CompoundTag compoundTag;

    public PartnerCommandTriggerMessage(){
    }

    public PartnerCommandTriggerMessage(int entityID,int commandIndex, CompoundTag compoundTag) {
        this.entityID = entityID;
        this.commandIndex = commandIndex;
        this.compoundTag = compoundTag;
    }

    public static void serialize(final PartnerCommandTriggerMessage message, final FriendlyByteBuf buf) {
        buf.writeVarInt(message.entityID);
        buf.writeVarInt(message.commandIndex);
        buf.writeNbt(message.compoundTag);
    }

    public static PartnerCommandTriggerMessage deserialize(final FriendlyByteBuf buf) {
        final PartnerCommandTriggerMessage message = new PartnerCommandTriggerMessage();
        message.entityID = buf.readVarInt();
        message.commandIndex = buf.readVarInt();
        message.compoundTag = buf.readNbt();
        return message;
    }
    public static class Handler implements BiConsumer<PartnerCommandTriggerMessage, Supplier<NetworkEvent.Context>> {
        @Override
        public void accept(PartnerCommandTriggerMessage massage, Supplier<NetworkEvent.Context> contextSupplier) {
            final NetworkEvent.Context context = contextSupplier.get();
            final ServerPlayer player2 = context.getSender();
            context.enqueueWork(() -> {
                if(player2!=null){
                    Entity entity1 = player2.level().getEntity(massage.entityID);
                    if(entity1 instanceof Player player){
                        PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(player, CapabilityHandle.PLAYER_CAPABILITY);
                        if(capability!=null){
                            PartnerPlayerManager partnerManager = capability.getPartnerManager();
                            Partner<?> currentPartner = partnerManager.getCurrentPartner();
                            if(currentPartner!=null){
                                PartnerCommandBasic partnerCommandBasic = currentPartner.getSkillManager().getPartnerCommandBasics().get(massage.commandIndex);
                                TriggerBasicType triggerType = partnerCommandBasic.getTriggerType();
                                TriggerBasic triggerBasic = triggerType.create();
                                triggerBasic.deserializeNBT(massage.compoundTag);
                                partnerCommandBasic.triggerCommand(triggerBasic);
                            }
                        }
                    }
                }
            });
            context.setPacketHandled(true);
        }
    }
}
