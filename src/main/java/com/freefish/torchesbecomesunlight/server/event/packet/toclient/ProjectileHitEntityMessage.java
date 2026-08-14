package com.freefish.torchesbecomesunlight.server.event.packet.toclient;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import com.freefish.torchesbecomesunlight.server.entity.projectile.NoGravityProjectileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
public class ProjectileHitEntityMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ProjectileHitEntityMessage> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "projectile_hit_entity_message"));

    public static final StreamCodec<FriendlyByteBuf, ProjectileHitEntityMessage> STREAM_CODEC =
            StreamCodec.of((buf, msg) -> serialize(msg, buf), ProjectileHitEntityMessage::deserialize);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

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

        public static void handle(final ProjectileHitEntityMessage message, final IPayloadContext context) {
            
            context.enqueueWork(() -> {
                NoGravityProjectileEntity entity =(NoGravityProjectileEntity) Minecraft.getInstance().level.getEntity(message.entityID);
                Entity target = Minecraft.getInstance().level.getEntity(message.hitEntityID);
                if (entity != null) {
                    entity.hitEntity(target);
                }
            });
        }
}
