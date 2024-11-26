package com.freefish.torchesbecomesunlight.server.event.packet.toserver;

import com.freefish.torchesbecomesunlight.server.entity.effect.dialogueentity.DialogueEntity;
import com.freefish.torchesbecomesunlight.server.story.dialogue.Dialogue;
import com.freefish.torchesbecomesunlight.server.story.dialogue.DialogueStore;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class SpawnDialogueEntity {
    private Entity[] entities;
    private int entityID;
    private Dialogue dialogue;

    public SpawnDialogueEntity(){
    }

    public SpawnDialogueEntity(int entityID , Dialogue dialogue,Entity ... entities) {
        this.entityID = entityID;
        this.dialogue = dialogue;
        this.entities = entities;

    }

    public static void serialize(final SpawnDialogueEntity message, final FriendlyByteBuf buf) {
        buf.writeVarInt(message.entityID);
        buf.writeVarInt(message.dialogue.getIndex());
        buf.writeVarInt(message.entities.length);
        for(Entity entity: message.entities){
            buf.writeVarInt(entity.getId());
        }
    }

    public static SpawnDialogueEntity deserialize(final FriendlyByteBuf buf) {
        final SpawnDialogueEntity message = new SpawnDialogueEntity();
        message.entityID = buf.readVarInt();
        message.dialogue = DialogueStore.dialogueList.get(buf.readVarInt());
        int entityAmount = buf.readVarInt();
        Entity[] entities1 = new Entity[entityAmount];
        for(int i =0;i<entityAmount;i++){
            entities1[i] = Minecraft.getInstance().level.getEntity(buf.readVarInt());
        }
        message.entities = entities1;
        return message;
    }
    public static class Handler implements BiConsumer<SpawnDialogueEntity, Supplier<NetworkEvent.Context>> {
        @Override
        public void accept(SpawnDialogueEntity SpawnDialogueEntity, Supplier<NetworkEvent.Context> contextSupplier) {
            final NetworkEvent.Context context = contextSupplier.get();
            final ServerPlayer player = context.getSender();
            context.enqueueWork(() -> {
                if(player!=null){
                    DialogueEntity dialogueEntity = new DialogueEntity(player,player.level(),SpawnDialogueEntity.dialogue,SpawnDialogueEntity.entities);
                    dialogueEntity.setPos(player.position());
                    player.level().addFreshEntity(dialogueEntity);
                }
            });
            context.setPacketHandled(true);
        }
    }
}
