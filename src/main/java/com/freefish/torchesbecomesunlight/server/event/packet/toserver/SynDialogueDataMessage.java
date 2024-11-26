package com.freefish.torchesbecomesunlight.server.event.packet.toserver;

import com.freefish.torchesbecomesunlight.server.capability.story.PlayerStoryStone;
import com.freefish.torchesbecomesunlight.server.capability.story.PlayerStoryStoneProvider;
import com.freefish.torchesbecomesunlight.server.entity.effect.dialogueentity.DialogueEntity;
import com.freefish.torchesbecomesunlight.server.story.dialogue.Dialogue;
import com.freefish.torchesbecomesunlight.server.story.dialogue.DialogueStore;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class SynDialogueDataMessage {
    private int entityID;
    private boolean isCanDialogue;

    public SynDialogueDataMessage(){
    }

    public SynDialogueDataMessage(int entityID , PlayerStoryStone data) {
        this.entityID = entityID;
        this.isCanDialogue = data.isCanDialogue();
    }

    public static void serialize(final SynDialogueDataMessage message, final FriendlyByteBuf buf) {
        buf.writeVarInt(message.entityID);
        buf.writeBoolean(message.isCanDialogue);
    }

    public static SynDialogueDataMessage deserialize(final FriendlyByteBuf buf) {
        final SynDialogueDataMessage message = new SynDialogueDataMessage();
        message.entityID = buf.readVarInt();
        message.isCanDialogue = buf.readBoolean();
        return message;
    }

    public static class Handler implements BiConsumer<SynDialogueDataMessage, Supplier<NetworkEvent.Context>> {
        @Override
        public void accept(SynDialogueDataMessage SpawnDialogueEntity, Supplier<NetworkEvent.Context> contextSupplier) {
            final NetworkEvent.Context context = contextSupplier.get();
            final ServerPlayer player = context.getSender();
            context.enqueueWork(() -> {
                if(player!=null){
                    Entity entity = player.level().getEntity(SpawnDialogueEntity.entityID);
                    if(entity instanceof Player player1){
                        player1.getCapability(PlayerStoryStoneProvider.PLAYER_STORY_STONE_CAPABILITY).ifPresent(data->{
                            data.setCanDialogue(SpawnDialogueEntity.isCanDialogue);
                        });
                    }
                }
            });
            context.setPacketHandled(true);
        }
    }
}
