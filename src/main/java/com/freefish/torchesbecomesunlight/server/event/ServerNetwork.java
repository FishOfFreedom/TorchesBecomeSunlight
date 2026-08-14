package com.freefish.torchesbecomesunlight.server.event;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.event.packet.toclient.*;
import com.freefish.torchesbecomesunlight.server.event.packet.toserver.*;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * NeoForge 的封包按 {@link CustomPacketPayload.Type} 里的 ResourceLocation 寻址，
 * 不再像 Forge 的 SimpleChannel 那样依赖自增 id，因此这里的注册顺序不再是通信协议的一部分。
 * 注意：MessageUseAbility / AnimationActMessage 传的仍是数组下标，
 * 那两个数组（AbilityHandler.PLAYER_ABILITIES、entity.getAnimations()）"只能追加"的约束依然成立。
 */
@EventBusSubscriber(modid = TorchesBecomeSunlight.MOD_ID)
public class ServerNetwork {
    private static final String VERSION = "1";

    @SubscribeEvent
    public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(VERSION);

        // -> 客户端
        registrar.playToClient(ActRangeSignMessage.TYPE, ActRangeSignMessage.STREAM_CODEC, ActRangeSignMessage::handle);
        registrar.playToClient(AnimationActMessage.TYPE, AnimationActMessage.STREAM_CODEC, AnimationActMessage::handle);
        registrar.playToClient(StartDialogueMessage.TYPE, StartDialogueMessage.STREAM_CODEC, StartDialogueMessage::handle);
        registrar.playToClient(MessageUpdateBossBar.TYPE, MessageUpdateBossBar.STREAM_CODEC, MessageUpdateBossBar::handle);
        registrar.playToClient(SetDemonCentreMessage.TYPE, SetDemonCentreMessage.STREAM_CODEC, SetDemonCentreMessage::handle);
        registrar.playToClient(MessageUseAbility.TYPE, MessageUseAbility.STREAM_CODEC, MessageUseAbility::handle);
        registrar.playToClient(MessageInterruptAbility.TYPE, MessageInterruptAbility.STREAM_CODEC, MessageInterruptAbility::handle);
        registrar.playToClient(SynCapabilityMessage.TYPE, SynCapabilityMessage.STREAM_CODEC, SynCapabilityMessage::handle);
        registrar.playToClient(InitClientEntityMessage.TYPE, InitClientEntityMessage.STREAM_CODEC, InitClientEntityMessage::handle);
        registrar.playToClient(ProjectileHitEntityMessage.TYPE, ProjectileHitEntityMessage.STREAM_CODEC, ProjectileHitEntityMessage::handle);
        registrar.playToClient(SetBlockEntityDataMessage.TYPE, SetBlockEntityDataMessage.STREAM_CODEC, SetBlockEntityDataMessage::handle);

        // -> 服务端
        registrar.playToServer(SkipDialogueMessage.TYPE, SkipDialogueMessage.STREAM_CODEC, SkipDialogueMessage::handle);
        registrar.playToServer(PlayerInteractMessage.TYPE, PlayerInteractMessage.STREAM_CODEC, PlayerInteractMessage::handle);
        registrar.playToServer(MiddelClickMessage.TYPE, MiddelClickMessage.STREAM_CODEC, MiddelClickMessage::handle);
        registrar.playToServer(SynNumberEntity.TYPE, SynNumberEntity.STREAM_CODEC, SynNumberEntity::handle);
        registrar.playToServer(PartnerCommandTriggerMessage.TYPE, PartnerCommandTriggerMessage.STREAM_CODEC, PartnerCommandTriggerMessage::handle);
    }

    public static void toClientMessage(Entity entity, CustomPacketPayload message) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, message);
    }

    public static void toServerMessage(CustomPacketPayload message) {
        PacketDistributor.sendToServer(message);
    }

    public static void toPlayer(ServerPlayer player, CustomPacketPayload message) {
        PacketDistributor.sendToPlayer(player, message);
    }

    public static void toLevel(ServerLevel level, CustomPacketPayload message) {
        PacketDistributor.sendToPlayersInDimension(level, message);
    }

    public static void toAll(CustomPacketPayload message) {
        PacketDistributor.sendToAllPlayers(message);
    }
}
