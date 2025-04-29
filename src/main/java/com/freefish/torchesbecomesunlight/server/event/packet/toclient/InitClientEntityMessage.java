package com.freefish.torchesbecomesunlight.server.event.packet.toclient;

import com.freefish.torchesbecomesunlight.server.entity.ITwoStateEntity;
import com.freefish.torchesbecomesunlight.server.util.animation.IAnimatedEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class InitClientEntityMessage {
    private int entityID;
    private InitDataType index;
    private Entity entity;

    //TwoState
    private ITwoStateEntity.State isTwoState;

    public InitClientEntityMessage() {
    }

    public InitClientEntityMessage(Entity entity, InitDataType index) {
        this.entityID = entity.getId();
        this.index = index;
        this.entity = entity;
    }

    public static void serialize(final InitClientEntityMessage message, final FriendlyByteBuf buf) {
        buf.writeVarInt(message.entityID);
        InitDataType indexType = message.index;
        buf.writeUtf(indexType.toString());

        if(indexType == InitDataType.ISTWOSTATE){
            ITwoStateEntity twoStateEntity = (ITwoStateEntity) message.entity;
            buf.writeUtf(twoStateEntity.getSpawnState().toString());
        }
    }

    public static InitClientEntityMessage deserialize(final FriendlyByteBuf buf) {
        final InitClientEntityMessage message = new InitClientEntityMessage();
        message.entityID = buf.readVarInt();
        message.index = InitDataType.valueOf(buf.readUtf());

        if(message.index == InitDataType.ISTWOSTATE){
            message.isTwoState = ITwoStateEntity.State.valueOf(buf.readUtf());
        }

        return message;
    }

    public static class Handler implements BiConsumer<InitClientEntityMessage, Supplier<NetworkEvent.Context>> {
        @Override
        public void accept(final InitClientEntityMessage message, final Supplier<NetworkEvent.Context> contextSupplier) {
            final NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> {
                Entity entity =Minecraft.getInstance().level.getEntity(message.entityID);
                if (entity != null) {
                    if(message.index == InitDataType.ISTWOSTATE){
                        ITwoStateEntity twoStateEntity = (ITwoStateEntity) entity;
                        twoStateEntity.transSpawnState(message.isTwoState);
                        twoStateEntity.setSpawnState(message.isTwoState);
                    }
                }
            });
            context.setPacketHandled(true);
        }
    }

    public enum InitDataType{
        ISTWOSTATE
    }
}
