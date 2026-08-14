package com.freefish.torchesbecomesunlight.server.event.packet.toclient;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.UUID;
public class MessageUpdateBossBar implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageUpdateBossBar> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "message_update_boss_bar"));

    public static final StreamCodec<FriendlyByteBuf, MessageUpdateBossBar> STREAM_CODEC =
            StreamCodec.of((buf, msg) -> serialize(msg, buf), MessageUpdateBossBar::deserialize);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private UUID bossID;
    private int renderType;

    public MessageUpdateBossBar() {

    }

    public MessageUpdateBossBar(UUID bossID, int entity) {
        this.bossID = bossID;
        this.renderType = entity;
    }

    public static void serialize(final MessageUpdateBossBar message, final FriendlyByteBuf buf) {
        buf.writeUUID(message.bossID);
        buf.writeInt(message.renderType);
    }

    public static MessageUpdateBossBar deserialize(final FriendlyByteBuf buf) {
        return new MessageUpdateBossBar(buf.readUUID(), buf.readInt());
    }

        public static void handle(final MessageUpdateBossBar message, final IPayloadContext context) {
            
            context.enqueueWork(() -> {
                if (message.renderType == -1) {
                    TorchesBecomeSunlight.bossBarRegistryNames.remove(message.bossID);
                }
                else {
                    TorchesBecomeSunlight.bossBarRegistryNames.put(message.bossID, message.renderType);
                }
            });
        }
}
