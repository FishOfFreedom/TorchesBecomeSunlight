package com.freefish.torchesbecomesunlight.server.event.packet.toclient;

import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.FrozenCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class SynCapabilityMessage {
    private int dialogueEntityID;

    private int frozeTime;
    private int lightingTime;

    public SynCapabilityMessage(){
    }

    public SynCapabilityMessage(LivingEntity dialogueEntity,int frozeTime,int lightingTime) {
        this.dialogueEntityID = dialogueEntity.getId();
        this.frozeTime = frozeTime;
        this.lightingTime = lightingTime;
    }

    public static void serialize(final SynCapabilityMessage message, final FriendlyByteBuf buf) {
        buf.writeVarInt(message.dialogueEntityID);
        buf.writeVarInt(message.frozeTime);
        buf.writeVarInt(message.lightingTime);
    }

    public static SynCapabilityMessage deserialize(final FriendlyByteBuf buf) {
        final SynCapabilityMessage message = new SynCapabilityMessage();

        message.dialogueEntityID = buf.readVarInt();
        message.frozeTime = buf.readVarInt();
        message.lightingTime = buf.readVarInt();

        return message;
    }

    public static class Handler implements BiConsumer<SynCapabilityMessage, Supplier<NetworkEvent.Context>> {
        @Override
        public void accept(SynCapabilityMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
            final NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> {
                Entity entity = Minecraft.getInstance().level.getEntity(message.dialogueEntityID);
                if(entity instanceof LivingEntity livingEntity){
                    FrozenCapability.IFrozenCapability data = CapabilityHandle.getCapability(livingEntity, CapabilityHandle.FROZEN_CAPABILITY);
                    if(data!=null){
                        data.setFrozen(message.frozeTime);
                        data.setIsFrozen(true);
                        data.setLighting(message.lightingTime);
                        data.setIsLighting(true);

                        if(data.getFrozenTick() == 0){
                            data.clearFrozen(livingEntity);
                        }
                        if(data.getLightingTick() == 0){
                            data.clearLighting(livingEntity);
                        }
                    }
                }
            });
            context.setPacketHandled(true);
        }
    }
}
