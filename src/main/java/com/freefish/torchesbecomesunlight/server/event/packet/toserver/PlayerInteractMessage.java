package com.freefish.torchesbecomesunlight.server.event.packet.toserver;

import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.PlayerCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class PlayerInteractMessage {
    private int entityID;
    private CompoundTag compoundTag;

    public PlayerInteractMessage(){
    }

    public PlayerInteractMessage(int entityID,CompoundTag compoundTag) {
        this.entityID = entityID;
        this.compoundTag = compoundTag;
    }

    public static void serialize(final PlayerInteractMessage message, final FriendlyByteBuf buf) {
        buf.writeVarInt(message.entityID);
        buf.writeNbt(message.compoundTag);
    }

    public static PlayerInteractMessage deserialize(final FriendlyByteBuf buf) {
        final PlayerInteractMessage message = new PlayerInteractMessage();
        message.entityID = buf.readVarInt();
        message.compoundTag = buf.readNbt();
        return message;
    }
    public static class Handler implements BiConsumer<PlayerInteractMessage, Supplier<NetworkEvent.Context>> {
        @Override
        public void accept(PlayerInteractMessage massage, Supplier<NetworkEvent.Context> contextSupplier) {
            final NetworkEvent.Context context = contextSupplier.get();
            final ServerPlayer player2 = context.getSender();
            context.enqueueWork(() -> {
                if(player2!=null){
                    Entity entity1 = player2.level().getEntity(massage.entityID);
                    boolean interact = massage.compoundTag.contains("interact");
                    if(interact){
                        CompoundTag interactTag = massage.compoundTag.getCompound("interact");
                        if (entity1 instanceof Player player) {
                            PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(player, CapabilityHandle.PLAYER_CAPABILITY);
                            if (capability != null) {
                                capability.onPlayerEmptyInteract(player,true,interactTag.getBoolean("isMainHand"));
                            }
                        }
                    }
                    boolean interactoff = massage.compoundTag.contains("interactoff");
                    if(interactoff){
                        CompoundTag interactTag = massage.compoundTag.getCompound("interactoff");
                        if (entity1 instanceof Player player) {
                            PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(player, CapabilityHandle.PLAYER_CAPABILITY);
                            if (capability != null) {
                                capability.onPlayerEmptyInteract(player,false,interactTag.getBoolean("isMainHand"));
                            }
                        }
                    }
                }
            });
            context.setPacketHandled(true);
        }
    }
}
