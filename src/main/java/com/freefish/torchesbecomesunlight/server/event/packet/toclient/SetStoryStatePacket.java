package com.freefish.torchesbecomesunlight.server.event.packet.toclient;

import com.freefish.torchesbecomesunlight.server.capability.story.PlayerStoryStoneProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class SetStoryStatePacket {
    private int entityID;
    private int value;

    public SetStoryStatePacket(){

    }

    public SetStoryStatePacket(int entityID , int value) {
        this.entityID = entityID;
        this.value = value;
    }

    public static void serialize(final SetStoryStatePacket message, final FriendlyByteBuf buf) {
        buf.writeVarInt(message.entityID);
        buf.writeVarInt(message.value);
    }

    public static SetStoryStatePacket deserialize(final FriendlyByteBuf buf) {
        final SetStoryStatePacket message = new SetStoryStatePacket();
        message.entityID = buf.readVarInt();
        message.value = buf.readVarInt();
        return message;
    }
    public static class Handler implements BiConsumer<SetStoryStatePacket, Supplier<NetworkEvent.Context>>{
        @Override
        public void accept(SetStoryStatePacket setStoryStatePacket, Supplier<NetworkEvent.Context> contextSupplier) {
            final NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> {
                Entity entity = Minecraft.getInstance().level.getEntity(setStoryStatePacket.entityID);
                if (entity instanceof Player player) {
                    entity.getCapability(PlayerStoryStoneProvider.PLAYER_STORY_STONE_CAPABILITY).ifPresent(storyStone -> {
                        storyStone.setStoryState(setStoryStatePacket.value);
                    });
                }
            });
            context.setPacketHandled(true);
        }
    }
}
