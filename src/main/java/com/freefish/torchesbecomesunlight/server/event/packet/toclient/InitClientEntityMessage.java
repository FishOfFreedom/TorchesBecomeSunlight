package com.freefish.torchesbecomesunlight.server.event.packet.toclient;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import com.freefish.torchesbecomesunlight.server.entity.ITwoStateEntity;
import com.freefish.torchesbecomesunlight.server.util.animation.IAnimatedEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
public class InitClientEntityMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<InitClientEntityMessage> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "init_client_entity_message"));

    public static final StreamCodec<FriendlyByteBuf, InitClientEntityMessage> STREAM_CODEC =
            StreamCodec.of((buf, msg) -> serialize(msg, buf), InitClientEntityMessage::deserialize);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

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

        public static void handle(final InitClientEntityMessage message, final IPayloadContext context) {
            
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
        }

    public enum InitDataType{
        ISTWOSTATE
    }
}
