package com.freefish.torchesbecomesunlight.server.init;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.capability.AbilityCapability;
import com.freefish.torchesbecomesunlight.server.capability.DialogueCapability;
import com.freefish.torchesbecomesunlight.server.capability.FrozenCapability;
import com.freefish.torchesbecomesunlight.server.capability.PlayerCapability;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * NeoForge 1.21.1 移除了 Forge 的 Capability 体系（{@code LazyOptional} /
 * {@code ICapabilitySerializable} / {@code AttachCapabilitiesEvent} 全部不复存在）。
 * 本 Mod 的这四份数据都是"挂在实体上的持久化状态"，对应物是 Data Attachments，
 * 而不是 NeoForge 新的 {@code EntityCapability}（后者面向物品栏/流体/能量这类能力查询）。
 * <p>
 * 挂载条件（玩家才有 ABILITY/PLAYER 等）由 {@link com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle}
 * 统一把关，以保持与原 {@code attachEntityCapability} 相同的语义。
 * <p>
 * 玩家死亡后的数据延续仍由 EventListener 里的 {@code PlayerEvent.Clone} 处理，
 * 未改用 {@code copyOnDeath()}，以免悄悄改变原有行为。
 */
public class TBSAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, TorchesBecomeSunlight.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<AbilityCapability.IAbilityCapability>> ABILITY =
            ATTACHMENT_TYPES.register("ability", () ->
                    AttachmentType.<CompoundTag, AbilityCapability.IAbilityCapability>serializable(
                            AbilityCapability.AbilityCapabilityImp::new).build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerCapability.IPlayerCapability>> PLAYER =
            ATTACHMENT_TYPES.register("player", () ->
                    AttachmentType.<CompoundTag, PlayerCapability.IPlayerCapability>serializable(
                            PlayerCapability.PlayerCapabilityImp::new).build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<FrozenCapability.IFrozenCapability>> FROZEN =
            ATTACHMENT_TYPES.register("frozen", () ->
                    AttachmentType.<CompoundTag, FrozenCapability.IFrozenCapability>serializable(
                            FrozenCapability.FrozenCapabilityImp::new).build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<DialogueCapability.IDialogueCapability>> DIALOGUE =
            ATTACHMENT_TYPES.register("dialogue", () ->
                    AttachmentType.<CompoundTag, DialogueCapability.IDialogueCapability>serializable(
                            DialogueCapability.DialogueCapabilityImp::new).build());
}
