package com.freefish.torchesbecomesunlight.server.event.packet.toclient;

import com.freefish.torchesbecomesunlight.server.entity.effect.dialogueentity.DialogueEntity;
import com.freefish.torchesbecomesunlight.server.story.dialogue.Dialogue;
import com.freefish.torchesbecomesunlight.server.story.dialogue.DialogueStore;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class StartDialogueMessage {
    private Entity[] entities;
    private int dialogueEntity;
    private Dialogue dialogue;

    public StartDialogueMessage(){
    }

    public StartDialogueMessage(Dialogue dialogue,int dialogueEntity,Entity ... entities) {
        this.dialogueEntity = dialogueEntity;
        this.entities = entities;
        this.dialogue = dialogue;
    }

    public static void serialize(final StartDialogueMessage message, final FriendlyByteBuf buf) {
        buf.writeVarInt(message.dialogue.getIndex());
        buf.writeVarInt(message.dialogueEntity);
        buf.writeVarInt(message.entities.length);
        for(Entity entity: message.entities){
            buf.writeVarInt(entity.getId());
        }
    }

    public static StartDialogueMessage deserialize(final FriendlyByteBuf buf) {
        final StartDialogueMessage message = new StartDialogueMessage();
        message.dialogue = DialogueStore.dialogueList.get(buf.readVarInt());
        message.dialogueEntity = buf.readVarInt();
        int entityAmount = buf.readVarInt();
        Entity[] entities1 = new Entity[entityAmount];
        for(int i =0;i<entityAmount;i++){
            entities1[i] = Minecraft.getInstance().level.getEntity(buf.readVarInt());
        }
        message.entities = entities1;
        return message;
    }

    public static class Handler implements BiConsumer<StartDialogueMessage, Supplier<NetworkEvent.Context>> {
        @Override
        public void accept(StartDialogueMessage StartDialogueMessage, Supplier<NetworkEvent.Context> contextSupplier) {
            final NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> {
                Entity entity = Minecraft.getInstance().level.getEntity(StartDialogueMessage.dialogueEntity);
                if(entity instanceof DialogueEntity dialogueEntity){
                    dialogueEntity.setChatEntities(StartDialogueMessage.entities);
                    dialogueEntity.startSpeak(StartDialogueMessage.dialogue,100);
                }
            });
            context.setPacketHandled(true);
        }
    }
}
