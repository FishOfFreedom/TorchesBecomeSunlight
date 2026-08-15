package com.freefish.torchesbecomesunlight.server.event.packet.toserver;


import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import com.freefish.torchesbecomesunlight.server.story.data.DialogueEntry;
import com.freefish.torchesbecomesunlight.server.story.data.Option;
import com.freefish.torchesbecomesunlight.server.story.dialogueentity.DialogueEntity;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import java.util.List;
public class MiddelClickMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MiddelClickMessage> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "middel_click_message"));

    public static final StreamCodec<FriendlyByteBuf, MiddelClickMessage> STREAM_CODEC =
            StreamCodec.of((buf, msg) -> serialize(msg, buf), MiddelClickMessage::deserialize);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

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
        public static void handle(final MiddelClickMessage SpawnDialogueEntity, final IPayloadContext context) {
            
            final ServerPlayer player2 = ((ServerPlayer) context.player());
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
        }
}
