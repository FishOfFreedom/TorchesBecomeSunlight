package com.freefish.torchesbecomesunlight.server.event.packet.toclient;

import com.freefish.torchesbecomesunlight.server.ability.Ability;
import com.freefish.torchesbecomesunlight.server.ability.AbilityType;
import com.freefish.torchesbecomesunlight.server.capability.AbilityCapability;
import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class MessageInterruptAbility {
    private int entityID;
    private int index;

    public MessageInterruptAbility() {

    }

    public MessageInterruptAbility(int entityID, int index) {
        this.entityID = entityID;
        this.index = index;
    }

    public static void serialize(final MessageInterruptAbility message, final FriendlyByteBuf buf) {
        buf.writeVarInt(message.entityID);
        buf.writeVarInt(message.index);
    }

    public static MessageInterruptAbility deserialize(final FriendlyByteBuf buf) {
        final MessageInterruptAbility message = new MessageInterruptAbility();
        message.entityID = buf.readVarInt();
        message.index = buf.readVarInt();
        return message;
    }

    public static class Handler implements BiConsumer<MessageInterruptAbility, Supplier<NetworkEvent.Context>> {
        @Override
        public void accept(final MessageInterruptAbility message, final Supplier<NetworkEvent.Context> contextSupplier) {
            final NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> {
                LivingEntity entity = (LivingEntity) Minecraft.getInstance().level.getEntity(message.entityID);
                if (entity != null) {
                    AbilityCapability.IAbilityCapability abilityCapability = CapabilityHandle.getCapability(entity, CapabilityHandle.ABILITY_CAPABILITY);
                    if (abilityCapability != null) {
                        AbilityType<?, ?> abilityType = abilityCapability.getAbilityTypesOnEntity(entity)[message.index];
                        Ability instance = abilityCapability.getAbilityMap().get(abilityType);
                        if (instance.isUsing()) instance.interrupt();
                    }
                }
            });
            context.setPacketHandled(true);
        }
    }
}
