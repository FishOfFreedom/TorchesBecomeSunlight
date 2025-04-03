package com.freefish.torchesbecomesunlight.server.event;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.event.packet.toclient.*;
import com.freefish.torchesbecomesunlight.server.event.packet.toserver.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;

import javax.swing.text.html.parser.Entity;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ServerNetwork {
    private static int nextMessageId = 0;

    public static void initNetwork() {
        final String VERSION = "1";
        TorchesBecomeSunlight.NETWORK = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "net"))
                .networkProtocolVersion(() -> VERSION)
                .clientAcceptedVersions(VERSION::equals)
                .serverAcceptedVersions(VERSION::equals)
                .simpleChannel();

        registerMessage(AnimationActMessage.class, AnimationActMessage::serialize, AnimationActMessage::deserialize, new AnimationActMessage.Handler());
        registerMessage(StartDialogueMessage.class, StartDialogueMessage::serialize, StartDialogueMessage::deserialize, new StartDialogueMessage.Handler());
        registerMessage(DialogueTriggerMessage.class, DialogueTriggerMessage::serialize, DialogueTriggerMessage::deserialize, new DialogueTriggerMessage.Handler());
        registerMessage(MessageUpdateBossBar.class, MessageUpdateBossBar::serialize, MessageUpdateBossBar::deserialize, new MessageUpdateBossBar.Handler());
        registerMessage(SetDemonCentreMessage.class, SetDemonCentreMessage::serialize, SetDemonCentreMessage::deserialize, new SetDemonCentreMessage.Handler());
        registerMessage(MessageUseAbility.class, MessageUseAbility::serialize, MessageUseAbility::deserialize, new MessageUseAbility.Handler());
        registerMessage(MessageInterruptAbility.class, MessageInterruptAbility::serialize, MessageInterruptAbility::deserialize, new MessageInterruptAbility.Handler());
        registerMessage(MiddelClickMessage.class, MiddelClickMessage::serialize, MiddelClickMessage::deserialize, new MiddelClickMessage.Handler());
        registerMessage(SynNumberEntity.class, SynNumberEntity::serialize, SynNumberEntity::deserialize, new SynNumberEntity.Handler());
        registerMessage(SynCapabilityMessage.class, SynCapabilityMessage::serialize, SynCapabilityMessage::deserialize, new SynCapabilityMessage.Handler());
        registerMessage(InitClientEntityMessage.class, InitClientEntityMessage::serialize, InitClientEntityMessage::deserialize, new InitClientEntityMessage.Handler());
    }

    private static  <MSG> void registerMessage(final Class<MSG> clazz, final BiConsumer<MSG, FriendlyByteBuf> encoder, final Function<FriendlyByteBuf, MSG> decoder, final BiConsumer<MSG, Supplier<NetworkEvent.Context>> consumer) {
        TorchesBecomeSunlight.NETWORK.messageBuilder(clazz, nextMessageId++)
                .encoder(encoder).decoder(decoder)
                .consumerNetworkThread(consumer)
                .add();
    }

    public static <MSG> void toClientMessage(LivingEntity entity, MSG message){
        TorchesBecomeSunlight.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), message);
    }

    public static <MSG> void toServerMessage(MSG message){
        TorchesBecomeSunlight.NETWORK.sendToServer(message);
    }
}
