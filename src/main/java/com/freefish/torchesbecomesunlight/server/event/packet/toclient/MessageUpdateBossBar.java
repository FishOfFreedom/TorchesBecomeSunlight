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
    private boolean remove;
    private ResourceLocation registryName;

    public MessageUpdateBossBar() {

    }

    // Set entity to null to remove boss from the map
    public MessageUpdateBossBar(UUID bossID, LivingEntity entity) {
        this.bossID = bossID;
        if (entity != null) {
            this.registryName = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
            this.remove = false;
        }
        else {
            this.registryName = null;
            this.remove = true;
        }
    }

    public static void serialize(final MessageUpdateBossBar message, final FriendlyByteBuf buf) {
        buf.writeUUID(message.bossID);
        buf.writeBoolean(message.remove);
        if (!message.remove && message.registryName != null) buf.writeResourceLocation(message.registryName);
    }

    public static MessageUpdateBossBar deserialize(final FriendlyByteBuf buf) {
        final MessageUpdateBossBar message = new MessageUpdateBossBar();
        message.bossID = buf.readUUID();
        message.remove = buf.readBoolean();
        if (!message.remove) message.registryName = buf.readResourceLocation();
        return message;
    }

    public static class Handler implements BiConsumer<MessageUpdateBossBar, Supplier<NetworkEvent.Context>> {
        @Override
        public void accept(final MessageUpdateBossBar message, final Supplier<NetworkEvent.Context> contextSupplier) {
            final NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> {
                if (message.registryName == null) {
                    TorchesBecomeSunlight.bossBarRegistryNames.remove(message.bossID);
                }
                else {
                    TorchesBecomeSunlight.bossBarRegistryNames.put(message.bossID, message.registryName);
                }
            });
            context.setPacketHandled(true);
        }
    }
}
