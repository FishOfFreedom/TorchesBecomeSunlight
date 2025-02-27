package com.freefish.torchesbecomesunlight.server.event.packet.toserver;


import com.freefish.torchesbecomesunlight.server.entity.effect.dialogueentity.DialogueEntity;
import com.freefish.torchesbecomesunlight.server.story.dialogue.DialogueTrigger;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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
                        if(dialogueEntity != null && dialogueEntity.getDialogue() != null && dialogueEntity.getDialogue().getOptions()!=null){
                            int number = dialogueEntity.getNumber();
                            List<DialogueTrigger> options = dialogueEntity.getDialogue().getOptions();
                            DialogueTrigger dialogueTrigger = options.get(number);

                            if(dialogueTrigger.getHasTrigger()){
                                LivingEntity entity = (LivingEntity)dialogueEntity.getChatEntities()[dialogueTrigger.getNumber()];
                                dialogueTrigger.trigger(entity);
                            }

                            if(dialogueTrigger.getNextDialogue()!=null){
                                Entity chatEntity = dialogueEntity.getChatEntities()[dialogueTrigger.getNumber()];
                                dialogueTrigger.chooseDialogue(chatEntity);
                                dialogueEntity.startSpeakInServer(dialogueTrigger.getNextDialogue(), dialogueTrigger.getNextDialogue().getDialogueTime());
                            }
                            //else if (dialogueEntity.getDialogue().getNextDialogue() != null) {
                            //    dialogueEntity.startSpeakInServer(dialogueEntity.getDialogue().getNextDialogue(), dialogueEntity.getDialogue().getNextDialogue().getDialogueTime());
                            //}
                            else {
                                dialogueEntity.startSpeak(null, 100);
                            }
                            //dialogueEntity.setNumber(0);
                        }

                    }
                }
            });
            context.setPacketHandled(true);
        }
    }
}
