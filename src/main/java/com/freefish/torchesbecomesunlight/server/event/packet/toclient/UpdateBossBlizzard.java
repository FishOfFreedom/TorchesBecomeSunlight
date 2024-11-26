package com.freefish.torchesbecomesunlight.server.event.packet.toclient;

import com.freefish.torchesbecomesunlight.server.util.storage.ClientStorage;
import com.freefish.torchesbecomesunlight.server.util.storage.TBSWorldData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class UpdateBossBlizzard {
    public int entityId;
    public boolean blizzard;

    public UpdateBossBlizzard(int entityId, boolean blizzard) {
        this.entityId = entityId;
        this.blizzard = blizzard;
    }


    public static UpdateBossBlizzard deserialize(FriendlyByteBuf buf) {
        return new UpdateBossBlizzard(buf.readInt(), buf.readBoolean());
    }

    public static void serialize(UpdateBossBlizzard message, FriendlyByteBuf buf) {
        buf.writeInt(message.entityId);
        buf.writeBoolean(message.blizzard);
    }

    public static class Handler implements BiConsumer<UpdateBossBlizzard, Supplier<NetworkEvent.Context>> {
        @Override
        public void accept(UpdateBossBlizzard UpdateBossBlizzard, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            Player playerSided = context.get().getSender();
            if (context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                playerSided = Minecraft.getInstance().player;
            }
            if(playerSided != null){
                ClientStorage.INSTANCE.setBossActive(UpdateBossBlizzard.blizzard);
                //TBSWorldData worldData = TBSWorldData.get(playerSided.level());
                //if (worldData != null) {
                //    worldData.trackPrimordialBoss(UpdateBossBlizzard.entityId, UpdateBossBlizzard.blizzard);
                //}
            }
        }
    }
}
