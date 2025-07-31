package com.freefish.torchesbecomesunlight.server.event.packet.toclient;

import com.freefish.torchesbecomesunlight.server.story.data.DialogueEntry;
import com.freefish.torchesbecomesunlight.server.story.data.Option;
import com.freefish.torchesbecomesunlight.server.story.data.generatext.Generatext;
import com.freefish.torchesbecomesunlight.server.story.dialogueentity.DialogueEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class StartDialogueMessage {
    private int id;
    private String dialogueid;

    private String currentText;

    private Option[] currentOption;

    public StartDialogueMessage() {
    }

    public StartDialogueMessage(DialogueEntity dialogueEntity) {
        this.id = dialogueEntity.getId();
        this.dialogueid = dialogueEntity.getDialogue().getId();
        this.currentOption = dialogueEntity.getCurrentOptions();
        //todo 开局无效 是否无视第一次传包
        DialogueEntry dialogue = dialogueEntity.getDialogue();
        if(dialogue.hasGeneratext()){
            Generatext generatext = dialogue.getGeneratext();
            this.currentText = Component.translatable(dialogue.getText(),generatext.generaText(dialogueEntity)).getString();
        }
        else
            this.currentText = Component.translatable(dialogue.getText()).getString();
    }

    public static void serialize(final StartDialogueMessage message, final FriendlyByteBuf buf) {
        buf.writeVarInt(message.id);
        buf.writeUtf(message.dialogueid);
        buf.writeUtf(message.currentText);
        CompoundTag options = new CompoundTag();

        for(int i = 0;i<message.currentOption.length;i++){
            options.putString(String.valueOf(i),message.currentOption[i].getText());
        }
        buf.writeNbt(options);
    }

    public static StartDialogueMessage deserialize(final FriendlyByteBuf buf) {
        final StartDialogueMessage message = new StartDialogueMessage();
        message.id = buf.readVarInt();
        message.dialogueid = buf.readUtf();
        message.currentText = buf.readUtf();

        CompoundTag options = buf.readNbt();
        Option[] cOption = new Option[options.size()];
        int i = 0;
        for(String key:options.getAllKeys()){
            Option option = new Option();
            option.setText(options.getString(key));
            cOption[i] = option;
            i++;
        }
        message.currentOption = cOption;

        return message;
    }

    public static class Handler implements BiConsumer<StartDialogueMessage, Supplier<NetworkEvent.Context>> {
        @Override
        public void accept(final StartDialogueMessage message, final Supplier<NetworkEvent.Context> contextSupplier) {
            final NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> {
                Entity entity =Minecraft.getInstance().level.getEntity(message.id);
                if (entity instanceof DialogueEntity dialogue) {
                    String currentID = message.dialogueid;
                    DialogueEntry dialogueEntry = dialogue.getDialogueEntry(currentID);
                    if(dialogueEntry!=null){
                        dialogue.setFloatScale(0);
                        dialogue.startSpeak(dialogueEntry,dialogueEntry.getDialoguetime());

                        dialogue.currentText = message.currentText;

                        dialogue.setOldOptions(dialogue.getOptions());
                        dialogue.resetFloatScale();

                        dialogue.setCurrentOptions(message.currentOption);
                        dialogue.setOptions(dialogue.hasOptions()?dialogue.getCurrentOptions().length:0);

                        if(!dialogue.startDialogueFirst){
                            dialogue.setOldOptions(dialogue.getOptions());
                            dialogue.startDialogueFirst = true;
                        }
                    }
                }
            });
            context.setPacketHandled(true);
        }
    }
}
