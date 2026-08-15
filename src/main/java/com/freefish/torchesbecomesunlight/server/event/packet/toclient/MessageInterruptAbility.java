package com.freefish.torchesbecomesunlight.server.event.packet.toclient;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import com.freefish.torchesbecomesunlight.server.ability.Ability;
import com.freefish.torchesbecomesunlight.server.ability.AbilityType;
import com.freefish.torchesbecomesunlight.server.capability.AbilityCapability;
import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
public class MessageInterruptAbility implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageInterruptAbility> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "message_interrupt_ability"));

    public static final StreamCodec<FriendlyByteBuf, MessageInterruptAbility> STREAM_CODEC =
            StreamCodec.of((buf, msg) -> serialize(msg, buf), MessageInterruptAbility::deserialize);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

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

        public static void handle(final MessageInterruptAbility message, final IPayloadContext context) {
            
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
        }
}
