package com.freefish.torchesbecomesunlight.server.event.packet.toclient;

import com.freefish.torchesbecomesunlight.server.util.animation.IAnimatedEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class AnimationActMessage {
    private int entityID;
    private int index;

    public AnimationActMessage() {

    }

    public AnimationActMessage(int entityID, int index) {
        this.entityID = entityID;
        this.index = index;
    }

    public static void serialize(final AnimationActMessage message, final FriendlyByteBuf buf) {
        buf.writeVarInt(message.entityID);
        buf.writeVarInt(message.index);
    }

    public static AnimationActMessage deserialize(final FriendlyByteBuf buf) {
        final AnimationActMessage message = new AnimationActMessage();
        message.entityID = buf.readVarInt();
        message.index = buf.readVarInt();
        return message;
    }

    public static class Handler implements BiConsumer<AnimationActMessage, Supplier<NetworkEvent.Context>> {
        @Override
        public void accept(final AnimationActMessage message, final Supplier<NetworkEvent.Context> contextSupplier) {
            final NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> {
                IAnimatedEntity entity = (IAnimatedEntity) Minecraft.getInstance().level.getEntity(message.entityID);
                if (entity != null) {
                    if (message.index == -1) {
                        entity.setAnimation(IAnimatedEntity.NO_ANIMATION);
                    } else {
                        entity.setAnimation(entity.getAnimations()[message.index]);
                    }
                    entity.setAnimationTick(0);
                }
            });
            context.setPacketHandled(true);
        }
    }
}
