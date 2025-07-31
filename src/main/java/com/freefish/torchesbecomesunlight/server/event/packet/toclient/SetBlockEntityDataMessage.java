package com.freefish.torchesbecomesunlight.server.event.packet.toclient;

import com.freefish.torchesbecomesunlight.server.block.blockentity.AutoSynTagBlockEntity;
import com.freefish.torchesbecomesunlight.server.block.blockentity.sync.SynchedBlockEntityData;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class SetBlockEntityDataMessage {
    private List<SynchedBlockEntityData.DataValue<?>> pValues;
    private BlockPos blockPos;

    public SetBlockEntityDataMessage() {

    }

    public SetBlockEntityDataMessage(List<SynchedBlockEntityData.DataValue<?>> pValues,BlockPos blockPos) {
        this.pValues = pValues;
        this.blockPos = blockPos;
    }

    public static void serialize(final SetBlockEntityDataMessage message, final FriendlyByteBuf buf) {
        buf.writeNbt(NbtUtils.writeBlockPos(message.blockPos));
        for(SynchedBlockEntityData.DataValue<?> datavalue : message.pValues) {
            datavalue.write(buf);
        }
        buf.writeByte(255);
    }

    public static SetBlockEntityDataMessage deserialize(final FriendlyByteBuf buf) {
        final SetBlockEntityDataMessage message = new SetBlockEntityDataMessage();
        message.blockPos = NbtUtils.readBlockPos(buf.readNbt());
        List<SynchedBlockEntityData.DataValue<?>> list = new ArrayList<>();
        int i;
        while((i = buf.readUnsignedByte()) != 255) {
            list.add(SynchedBlockEntityData.DataValue.read(buf, i));
        }
        message.pValues = list;
        return message;
    }

    public static class Handler implements BiConsumer<SetBlockEntityDataMessage, Supplier<NetworkEvent.Context>> {
        @Override
        public void accept(final SetBlockEntityDataMessage message, final Supplier<NetworkEvent.Context> contextSupplier) {
            final NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> {
                BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(message.blockPos);
                if(blockEntity instanceof AutoSynTagBlockEntity autoSynTagBlockEntity){
                    autoSynTagBlockEntity.blockEntityData.assignValues(message.pValues);
                }
            });
            context.setPacketHandled(true);
        }
    }
}
