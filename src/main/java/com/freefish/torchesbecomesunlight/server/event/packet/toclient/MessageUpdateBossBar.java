package com.freefish.torchesbecomesunlight.server.event.packet.toclient;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class MessageUpdateBossBar {
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

    public static class Handler implements BiConsumer<MessageUpdateBossBar, Supplier<NetworkEvent.Context>> {
        @Override
        public void accept(final MessageUpdateBossBar message, final Supplier<NetworkEvent.Context> contextSupplier) {
            final NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> {
                if (message.renderType == -1) {
                    TorchesBecomeSunlight.bossBarRegistryNames.remove(message.bossID);
                }
                else {
                    TorchesBecomeSunlight.bossBarRegistryNames.put(message.bossID, message.renderType);
                }
            });
            context.setPacketHandled(true);
        }
    }
}
