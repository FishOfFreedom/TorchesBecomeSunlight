package com.freefish.torchesbecomesunlight.server.event.packet.toserver;


import com.freefish.torchesbecomesunlight.server.story.dialogue.Dialogue;
import com.freefish.torchesbecomesunlight.server.story.dialogue.DialogueStore;
import com.freefish.torchesbecomesunlight.server.story.dialogue.DialogueTrigger;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class DialogueTriggerMessage {
    private int entityID;
    private Dialogue dialogue;
    private int optionNumber;

    public DialogueTriggerMessage(){

    }

    public DialogueTriggerMessage(int entityID , Dialogue dialogue, int optionNumber) {
        this.entityID = entityID;
        this.dialogue = dialogue;
        this.optionNumber = optionNumber;
    }

    public static void serialize(final DialogueTriggerMessage message, final FriendlyByteBuf buf) {
        buf.writeVarInt(message.entityID);
        buf.writeVarInt(message.dialogue.getIndex());
        buf.writeVarInt(message.optionNumber);
    }

    public static DialogueTriggerMessage deserialize(final FriendlyByteBuf buf) {
        final DialogueTriggerMessage message = new DialogueTriggerMessage();
        message.entityID = buf.readVarInt();
        message.dialogue = DialogueStore.dialogueList.get(buf.readVarInt());
        message.optionNumber = buf.readVarInt();
        return message;
    }
    public static class Handler implements BiConsumer<DialogueTriggerMessage, Supplier<NetworkEvent.Context>> {
        @Override
        public void accept(DialogueTriggerMessage DialogueTriggerMessage, Supplier<NetworkEvent.Context> contextSupplier) {
            final NetworkEvent.Context context = contextSupplier.get();
            final ServerPlayer player = context.getSender();
            context.enqueueWork(() -> {
                if(player!=null){
                    Entity entity = player.level().getEntity(DialogueTriggerMessage.entityID);
                    if (entity instanceof Mob mob) {
                        Dialogue dialogue = DialogueTriggerMessage.dialogue;
                        if(dialogue.getOptions()!=null){
                            List<DialogueTrigger> options = dialogue.getOptions();
                            DialogueTrigger dialogueTrigger = options.get(DialogueTriggerMessage.optionNumber);
                            dialogueTrigger.trigger(mob);
                        }
                    }
                }
            });
            context.setPacketHandled(true);
        }
    }
}
