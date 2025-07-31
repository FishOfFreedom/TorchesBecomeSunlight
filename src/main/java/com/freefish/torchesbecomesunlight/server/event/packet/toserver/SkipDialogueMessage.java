package com.freefish.torchesbecomesunlight.server.event.packet.toserver;


import com.freefish.torchesbecomesunlight.server.story.data.Dialogue;
import com.freefish.torchesbecomesunlight.server.story.data.DialogueEntry;
import com.freefish.torchesbecomesunlight.server.story.dialogueentity.DialogueEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class SkipDialogueMessage {
    private int entityID;

    public SkipDialogueMessage(){

    }

    public SkipDialogueMessage(int entityID) {
        this.entityID = entityID;
    }

    public static void serialize(final SkipDialogueMessage message, final FriendlyByteBuf buf) {
        buf.writeVarInt(message.entityID);
    }

    public static SkipDialogueMessage deserialize(final FriendlyByteBuf buf) {
        final SkipDialogueMessage message = new SkipDialogueMessage();
        message.entityID = buf.readVarInt();
        return message;
    }
    public static class Handler implements BiConsumer<SkipDialogueMessage, Supplier<NetworkEvent.Context>> {
        @Override
        public void accept(SkipDialogueMessage DialogueTriggerMessage, Supplier<NetworkEvent.Context> contextSupplier) {
            final NetworkEvent.Context context = contextSupplier.get();
            final ServerPlayer player = context.getSender();
            context.enqueueWork(() -> {
                if(player!=null){
                    Entity entity = player.level().getEntity(DialogueTriggerMessage.entityID);
                    if (entity instanceof DialogueEntity dialogueEntity) {
                        Dialogue dialogue = dialogueEntity.getAllDialogue();
                        DialogueEntry dialogueEntry = dialogueEntity.getDialogueEntry(dialogue.getSkipid());
                        if(dialogueEntry!=null) {

                            dialogueEntity.startSpeak(dialogueEntry,dialogueEntry.getDialoguetime());
                            dialogueEntry.trigger(dialogueEntity);
                        }
                    }
                }
            });
            context.setPacketHandled(true);
        }
    }
}
