package com.freefish.torchesbecomesunlight.server.event.packet.toclient;

import com.freefish.torchesbecomesunlight.server.entity.effect.dialogueentity.DialogueEntity;
import com.freefish.torchesbecomesunlight.server.story.dialogue.Dialogue;
import com.freefish.torchesbecomesunlight.server.story.dialogue.DialogueStore;
import com.freefish.torchesbecomesunlight.server.story.dialogue.DialogueTrigger;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class StartDialogueMessage {
    private Entity[] entities;
    private int dialogueEntityID;
    private DialogueEntity dialogueEntity;
    private Dialogue dialogue;

    public StartDialogueMessage(){
    }

    public StartDialogueMessage(Dialogue dialogue,DialogueEntity dialogueEntity,Entity ... entities) {
        this.dialogueEntityID = dialogueEntity.getId();
        this.dialogueEntity = dialogueEntity;
        this.entities = entities;
        this.dialogue = dialogue;
    }

    public static void serialize(final StartDialogueMessage message, final FriendlyByteBuf buf) {

        buf.writeUtf(message.dialogue.getMessage());
        buf.writeVarInt(message.dialogue.getSpeakerNumber());

        List<DialogueTrigger> options = message.dialogue.getOptions();
        int number;
        if(options==null){
            number = 0;
        }else {
            number = options.size();
        }

        List<String> temp = new ArrayList<>();
        int tem = 0;
        for(int i= 0;i<number;i++){
            DialogueTrigger dialogueTrigger = options.get(i);
            if(message.dialogueEntity.getChatEntities()[dialogueTrigger.getNumber()]instanceof LivingEntity livingEntity&& dialogueTrigger.isNoSend(livingEntity)){
                tem += 1;
            }
            else
                temp.add(options.get(i).getContent());
        }

        buf.writeVarInt(number-tem);
        for(String s:temp){
            buf.writeUtf(s);
        }

        buf.writeVarInt(message.dialogue.getDialogueTime());

        buf.writeVarInt(message.dialogueEntityID);
        buf.writeVarInt(message.entities.length);
        for(Entity entity: message.entities){
            buf.writeVarInt(entity.getId());
        }
    }

    public static StartDialogueMessage deserialize(final FriendlyByteBuf buf) {
        final StartDialogueMessage message = new StartDialogueMessage();
        String s1 = buf.readUtf();
        int number = buf.readVarInt();

        int size = buf.readVarInt();
        List<DialogueTrigger> options = new ArrayList<>();
        for(int i = 0;i<size;i++){
            options.add(new DialogueTrigger(buf.readUtf(),0,null));
        }
        int dialogueTime = buf.readVarInt();

        message.dialogue = new Dialogue(s1,options,null,number,dialogueTime);

        message.dialogueEntityID = buf.readVarInt();
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
                Entity entity = Minecraft.getInstance().level.getEntity(StartDialogueMessage.dialogueEntityID);
                if(entity instanceof DialogueEntity dialogueEntity){
                    if(StartDialogueMessage.entities.length!=0)
                        dialogueEntity.setChatEntities(StartDialogueMessage.entities);
                    dialogueEntity.setFloatScale(0);
                    dialogueEntity.startSpeak(StartDialogueMessage.dialogue,StartDialogueMessage.dialogue.getDialogueTime());
                    dialogueEntity.setOldOptions(dialogueEntity.getOptions());
                    dialogueEntity.resetFloatScale();
                    dialogueEntity.setOptions(StartDialogueMessage.dialogue.getOptions()==null?0:StartDialogueMessage.dialogue.getOptions().size());
                }
            });
            context.setPacketHandled(true);
        }
    }
}
