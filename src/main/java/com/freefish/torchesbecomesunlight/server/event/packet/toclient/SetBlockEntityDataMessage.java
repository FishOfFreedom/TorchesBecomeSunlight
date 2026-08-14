package com.freefish.torchesbecomesunlight.server.event.packet.toclient;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import com.freefish.torchesbecomesunlight.server.block.blockentity.AutoSynTagBlockEntity;
import com.freefish.torchesbecomesunlight.server.block.blockentity.sync.SynchedBlockEntityData;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import java.util.ArrayList;
import java.util.List;
public class SetBlockEntityDataMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SetBlockEntityDataMessage> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "set_block_entity_data_message"));

    public static final StreamCodec<FriendlyByteBuf, SetBlockEntityDataMessage> STREAM_CODEC =
            StreamCodec.of((buf, msg) -> serialize(msg, buf), SetBlockEntityDataMessage::deserialize);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

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

        public static void handle(final SetBlockEntityDataMessage message, final IPayloadContext context) {
            
            context.enqueueWork(() -> {
                BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(message.blockPos);
                if(blockEntity instanceof AutoSynTagBlockEntity autoSynTagBlockEntity){
                    autoSynTagBlockEntity.blockEntityData.assignValues(message.pValues);
                }
            });
        }
}
