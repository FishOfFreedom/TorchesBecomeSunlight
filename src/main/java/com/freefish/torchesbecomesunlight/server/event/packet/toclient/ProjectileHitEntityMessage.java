package com.freefish.torchesbecomesunlight.server.event.packet.toclient;

import com.freefish.torchesbecomesunlight.server.entity.projectile.NoGravityProjectileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ProjectileHitEntityMessage {
    private int entityID;
    private int hitEntityID;

    public ProjectileHitEntityMessage() {

    }

    public ProjectileHitEntityMessage(int projectileID, int hitEntityID) {
        this.entityID = entityID;
        this.hitEntityID = hitEntityID;
    }

    public ProjectileHitEntityMessage(NoGravityProjectileEntity noGravityProjectileEntity, int hitEntityID) {
        this.entityID = noGravityProjectileEntity.getId();
        this.hitEntityID = hitEntityID;
    }

    public static void serialize(final ProjectileHitEntityMessage message, final FriendlyByteBuf buf) {
        buf.writeVarInt(message.entityID);
        buf.writeVarInt(message.hitEntityID);
    }

    public static ProjectileHitEntityMessage deserialize(final FriendlyByteBuf buf) {
        final ProjectileHitEntityMessage message = new ProjectileHitEntityMessage();
        message.entityID = buf.readVarInt();
        message.hitEntityID = buf.readVarInt();
        return message;
    }

    public static class Handler implements BiConsumer<ProjectileHitEntityMessage, Supplier<NetworkEvent.Context>> {
        @Override
        public void accept(final ProjectileHitEntityMessage message, final Supplier<NetworkEvent.Context> contextSupplier) {
            final NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> {
                NoGravityProjectileEntity entity =(NoGravityProjectileEntity) Minecraft.getInstance().level.getEntity(message.entityID);
                Entity target = Minecraft.getInstance().level.getEntity(message.hitEntityID);
                if (entity != null) {
                    entity.hitEntity(target);
                }
            });
            context.setPacketHandled(true);
        }
    }
}
