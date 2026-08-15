package com.freefish.torchesbecomesunlight.server.event.packet.toserver;


import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import com.freefish.torchesbecomesunlight.server.story.data.Dialogue;
import com.freefish.torchesbecomesunlight.server.story.data.DialogueEntry;
import com.freefish.torchesbecomesunlight.server.story.dialogueentity.DialogueEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
public class SkipDialogueMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SkipDialogueMessage> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "skip_dialogue_message"));

    public static final StreamCodec<FriendlyByteBuf, SkipDialogueMessage> STREAM_CODEC =
            StreamCodec.of((buf, msg) -> serialize(msg, buf), SkipDialogueMessage::deserialize);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

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
        public static void handle(final SkipDialogueMessage DialogueTriggerMessage, final IPayloadContext context) {
            
            final ServerPlayer player = ((ServerPlayer) context.player());
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
        }
}
