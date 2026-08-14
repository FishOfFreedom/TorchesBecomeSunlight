package com.freefish.torchesbecomesunlight.server.event.packet.toserver;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import com.freefish.torchesbecomesunlight.server.story.dialogueentity.DialogueEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
public class SynNumberEntity implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SynNumberEntity> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "syn_number_entity"));

    public static final StreamCodec<FriendlyByteBuf, SynNumberEntity> STREAM_CODEC =
            StreamCodec.of((buf, msg) -> serialize(msg, buf), SynNumberEntity::deserialize);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private int entityID;
    private int dialogue;

    public SynNumberEntity(){
    }

    public SynNumberEntity(int entityID , int dialogue) {
        this.entityID = entityID;
        this.dialogue = dialogue;
    }

    public static void serialize(final SynNumberEntity message, final FriendlyByteBuf buf) {
        buf.writeVarInt(message.entityID);
        buf.writeVarInt(message.dialogue);
    }

    public static SynNumberEntity deserialize(final FriendlyByteBuf buf) {
        final SynNumberEntity message = new SynNumberEntity();
        message.entityID = buf.readVarInt();
        message.dialogue = buf.readVarInt();
        return message;
    }
        public static void handle(final SynNumberEntity data, final IPayloadContext context) {
            
            final ServerPlayer player = ((ServerPlayer) context.player());
            context.enqueueWork(() -> {
                Entity entity = player.level().getEntity(data.entityID);
                if(entity instanceof DialogueEntity dialogueEntity){
                    dialogueEntity.setNumber(data.dialogue);
                }
            });
        }
}
