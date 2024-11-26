package com.freefish.torchesbecomesunlight.server.event.packet.toserver;

import com.freefish.torchesbecomesunlight.server.entity.effect.dialogueentity.DialogueEntity;
import com.freefish.torchesbecomesunlight.server.story.dialogue.Dialogue;
import com.freefish.torchesbecomesunlight.server.story.dialogue.DialogueStore;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class SynNumberEntity {
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
    public static class Handler implements BiConsumer<SynNumberEntity, Supplier<NetworkEvent.Context>> {
        @Override
        public void accept(SynNumberEntity data, Supplier<NetworkEvent.Context> contextSupplier) {
            final NetworkEvent.Context context = contextSupplier.get();
            final ServerPlayer player = context.getSender();
            context.enqueueWork(() -> {
                Entity entity = player.level().getEntity(data.entityID);
                if(entity instanceof DialogueEntity dialogueEntity){
                    dialogueEntity.setNumber(data.dialogue);
                }
            });
            context.setPacketHandled(true);
        }
    }
}
