package com.freefish.torchesbecomesunlight.server.init;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

/**
 * 方块的物品栏是真正意义上的"能力查询"，因此仍走 NeoForge 的 Capability，
 * 而不是 {@link TBSAttachments} 那套持久化状态用的 Data Attachments。
 * <p>
 * 原先各 BlockEntity 里覆写 {@code getCapability(Capability, Direction)} 并返回
 * {@code LazyOptional} 的写法在 NeoForge 已不存在，改为在此处按面集中注册。
 */
@EventBusSubscriber(modid = TorchesBecomeSunlight.MOD_ID)
public class TBSCapabilities {
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityHandle.OVEN.get(),
                (blockEntity, side) -> blockEntity.getItemHandler(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityHandle.CUTTING_BOARD.get(),
                (blockEntity, side) -> blockEntity.getItemHandler(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityHandle.STEW_POT.get(),
                (blockEntity, side) -> blockEntity.getItemHandler(side));
    }
}
