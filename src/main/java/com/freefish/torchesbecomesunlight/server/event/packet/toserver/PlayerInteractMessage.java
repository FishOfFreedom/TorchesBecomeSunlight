package com.freefish.torchesbecomesunlight.server.event.packet.toserver;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.PlayerCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
public class PlayerInteractMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PlayerInteractMessage> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "player_interact_message"));

    public static final StreamCodec<FriendlyByteBuf, PlayerInteractMessage> STREAM_CODEC =
            StreamCodec.of((buf, msg) -> serialize(msg, buf), PlayerInteractMessage::deserialize);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

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
        public static void handle(final PlayerInteractMessage massage, final IPayloadContext context) {
            
            final ServerPlayer player2 = ((ServerPlayer) context.player());
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
        }
}
