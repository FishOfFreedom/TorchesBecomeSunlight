package com.freefish.torchesbecomesunlight.server.event.packet.toclient;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import com.freefish.torchesbecomesunlight.server.util.animation.IAnimatedEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
public class AnimationActMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<AnimationActMessage> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "animation_act_message"));

    public static final StreamCodec<FriendlyByteBuf, AnimationActMessage> STREAM_CODEC =
            StreamCodec.of((buf, msg) -> serialize(msg, buf), AnimationActMessage::deserialize);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

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

        public static void handle(final AnimationActMessage message, final IPayloadContext context) {
            
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
        }
}
