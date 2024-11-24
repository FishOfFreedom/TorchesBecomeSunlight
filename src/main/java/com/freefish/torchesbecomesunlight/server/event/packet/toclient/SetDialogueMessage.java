package com.freefish.torchesbecomesunlight.server.event.packet.toclient;

import com.freefish.torchesbecomesunlight.server.entity.effect.dialogueentity.DialogueEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class SetDialogueMessage {
    private int dialogueEntity;
    private String dialogue;

    public SetDialogueMessage(){
    }

    public SetDialogueMessage(String dialogue, int dialogueEntity) {
        this.dialogueEntity = dialogueEntity;
        this.dialogue = dialogue;
    }

    public static void serialize(final SetDialogueMessage message, final FriendlyByteBuf buf) {
        buf.writeVarInt(message.dialogueEntity);
        buf.writeUtf(message.dialogue,50);
    }

    public static SetDialogueMessage deserialize(final FriendlyByteBuf buf) {
        final SetDialogueMessage message = new SetDialogueMessage();
        message.dialogueEntity = buf.readVarInt();
        message.dialogue = buf.readUtf();
        return message;
    }

    public static class Handler implements BiConsumer<SetDialogueMessage, Supplier<NetworkEvent.Context>> {
        @Override
        public void accept(SetDialogueMessage StartDialogueMessage, Supplier<NetworkEvent.Context> contextSupplier) {
            final NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> {
                Entity entity = Minecraft.getInstance().level.getEntity(StartDialogueMessage.dialogueEntity);
                if(entity instanceof DialogueEntity dialogueEntity&&dialogueEntity.getDialogue()!=null){
                    dialogueEntity.getDialogue().setMessage(StartDialogueMessage.dialogue);
                }
            });
            context.setPacketHandled(true);
        }
    }
}
