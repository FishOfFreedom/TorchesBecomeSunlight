package com.freefish.torchesbecomesunlight.server.event.packet.toserver;


import com.freefish.torchesbecomesunlight.server.story.data.DialogueEntry;
import com.freefish.torchesbecomesunlight.server.story.data.Option;
import com.freefish.torchesbecomesunlight.server.story.dialogueentity.DialogueEntity;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class MiddelClickMessage {
    private int entityID;
    public MiddelClickMessage(){
    }

    public MiddelClickMessage(int entityID) {
        this.entityID = entityID;
    }

    public static void serialize(final MiddelClickMessage message, final FriendlyByteBuf buf) {
        buf.writeVarInt(message.entityID);
    }

    public static MiddelClickMessage deserialize(final FriendlyByteBuf buf) {
        final MiddelClickMessage message = new MiddelClickMessage();
        message.entityID = buf.readVarInt();
        return message;
    }
    public static class Handler implements BiConsumer<MiddelClickMessage, Supplier<NetworkEvent.Context>> {
        @Override
        public void accept(MiddelClickMessage SpawnDialogueEntity, Supplier<NetworkEvent.Context> contextSupplier) {
            final NetworkEvent.Context context = contextSupplier.get();
            final ServerPlayer player2 = context.getSender();
            context.enqueueWork(() -> {
                if(player2!=null){
                    Entity entity1 = player2.level().getEntity(SpawnDialogueEntity.entityID);
                    if(entity1 instanceof Player player){
                        Level level = player.level();
                        List<DialogueEntity> entitiesOfClass = level.getEntitiesOfClass(DialogueEntity.class, player.getBoundingBox().inflate(5));
                        DialogueEntity dialogueEntity = MathUtils.getClosestEntity(player, entitiesOfClass);
                        if(dialogueEntity != null && dialogueEntity.getDialogue() != null && dialogueEntity.getCurrentOptions()!=null){
                            int number = dialogueEntity.getNumber();
                            Option[] options = dialogueEntity.getCurrentOptions();
                            Option dialogueTrigger = options[number];

                            if(dialogueTrigger.HasTrigger()){
                                dialogueTrigger.trigger(dialogueEntity);
                            }

                            String chooseNextId = dialogueTrigger.getChooseNextId(dialogueEntity);
                            DialogueEntry dialogueEntry = dialogueEntity.getDialogueEntry(chooseNextId.isEmpty()?dialogueTrigger.getNextid():chooseNextId);
                            if(dialogueEntry!=null){
                                dialogueEntity.startSpeak(dialogueEntry, dialogueEntry.getDialoguetime());
                                dialogueEntry.trigger(dialogueEntity);
                            }
                            else {
                                dialogueEntity.startSpeak(null, 100);
                            }
                            dialogueEntity.setNumber(0);
                        }
                    }
                }
            });
            context.setPacketHandled(true);
        }
    }
}
