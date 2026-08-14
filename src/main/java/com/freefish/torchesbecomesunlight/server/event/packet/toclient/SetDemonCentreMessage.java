package com.freefish.torchesbecomesunlight.server.event.packet.toclient;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import com.freefish.torchesbecomesunlight.server.entity.ursus.Pursuer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
public class SetDemonCentreMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SetDemonCentreMessage> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "set_demon_centre_message"));

    public static final StreamCodec<FriendlyByteBuf, SetDemonCentreMessage> STREAM_CODEC =
            StreamCodec.of((buf, msg) -> serialize(msg, buf), SetDemonCentreMessage::deserialize);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private int entityID;
    private Vec3 centre;

    public SetDemonCentreMessage() {

    }

    public SetDemonCentreMessage(int entityID, Vec3 centre) {
        this.entityID = entityID;
        this.centre = centre;
    }

    public static void serialize(final SetDemonCentreMessage message, final FriendlyByteBuf buf) {
        buf.writeVarInt(message.entityID);
        buf.writeFloat((float)message.centre.x);
        buf.writeFloat((float)message.centre.y);
        buf.writeFloat((float)message.centre.z);
    }

    public static SetDemonCentreMessage deserialize(final FriendlyByteBuf buf) {
        final SetDemonCentreMessage message = new SetDemonCentreMessage();
        message.entityID = buf.readVarInt();
        message.centre = new Vec3(buf.readFloat(),buf.readFloat(),buf.readFloat());
        return message;
    }

        public static void handle(final SetDemonCentreMessage message, final IPayloadContext context) {
            
            context.enqueueWork(() -> {
                Pursuer entity = (Pursuer) Minecraft.getInstance().level.getEntity(message.entityID);
                if (entity != null) {
                    entity.DemonCentre=message.centre;
                }
            });
        }
}
