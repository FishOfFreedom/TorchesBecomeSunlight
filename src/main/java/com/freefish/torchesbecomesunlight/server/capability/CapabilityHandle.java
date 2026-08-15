package com.freefish.torchesbecomesunlight.server.capability;

import com.freefish.torchesbecomesunlight.server.init.TBSAttachments;
import com.freefish.torchesbecomesunlight.server.story.IDialogueEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Forge 的 Capability 体系在 NeoForge 1.21.1 已不存在，底层改由
 * {@link TBSAttachments} 的 Data Attachments 承载。
 * <p>
 * 这里刻意保留 {@code getCapability(entity, XXX_CAPABILITY)} 这一调用形式，
 * 使 60 余处调用点无需改动。
 */
public class CapabilityHandle {
    public static final Supplier<AttachmentType<AbilityCapability.IAbilityCapability>> ABILITY_CAPABILITY = TBSAttachments.ABILITY;
    public static final Supplier<AttachmentType<PlayerCapability.IPlayerCapability>> PLAYER_CAPABILITY = TBSAttachments.PLAYER;
    public static final Supplier<AttachmentType<FrozenCapability.IFrozenCapability>> FROZEN_CAPABILITY = TBSAttachments.FROZEN;
    public static final Supplier<AttachmentType<DialogueCapability.IDialogueCapability>> DIALOGUE_CAPABILITY = TBSAttachments.DIALOGUE;

    /**
     * 取实体上的附加数据。
     * <p>
     * attachment 的 {@code getData} 在数据不存在时会直接按默认值创建，因此这里补上
     * 原 {@code attachEntityCapability} 的挂载条件，避免给不该有该数据的实体凭空建出一份：
     * ABILITY / PLAYER 仅玩家，FROZEN 仅 LivingEntity，DIALOGUE 仅 IDialogueEntity。
     */
    @Nullable
    public static <T> T getCapability(Entity entity, Supplier<AttachmentType<T>> capability) {
        if (entity == null || entity.isRemoved()) return null;
        if (!canHave(entity, capability)) return null;
        return entity.getData(capability);
    }

    private static boolean canHave(Entity entity, Supplier<?> capability) {
        if (capability == ABILITY_CAPABILITY || capability == PLAYER_CAPABILITY) {
            return entity instanceof Player;
        }
        if (capability == FROZEN_CAPABILITY) {
            return entity instanceof LivingEntity;
        }
        if (capability == DIALOGUE_CAPABILITY) {
            return entity instanceof IDialogueEntity;
        }
        return true;
    }
}
