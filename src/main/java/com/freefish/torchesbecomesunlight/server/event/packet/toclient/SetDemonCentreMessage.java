package com.freefish.torchesbecomesunlight.server.event.packet.toclient;

import com.freefish.torchesbecomesunlight.server.entity.ursus.Pursuer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class SetDemonCentreMessage {
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

    public static class Handler implements BiConsumer<SetDemonCentreMessage, Supplier<NetworkEvent.Context>> {
        @Override
        public void accept(final SetDemonCentreMessage message, final Supplier<NetworkEvent.Context> contextSupplier) {
            final NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> {
                Pursuer entity = (Pursuer) Minecraft.getInstance().level.getEntity(message.entityID);
                if (entity != null) {
                    entity.DemonCentre=message.centre;
                }
            });
            context.setPacketHandled(true);
        }
    }
}
