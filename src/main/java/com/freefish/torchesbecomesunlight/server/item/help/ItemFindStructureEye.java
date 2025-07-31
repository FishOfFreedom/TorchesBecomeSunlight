package com.freefish.torchesbecomesunlight.server.item.help;

import com.freefish.torchesbecomesunlight.server.entity.EntityEyeOfStructure;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.structure.Structure;

import javax.annotation.Nullable;
import java.util.List;

public abstract class ItemFindStructureEye extends Item {
    private static final int FIND_MAX_HEIGHT = 100;
    private final TagKey<Structure> FIND_STRUCTURE;
    private final float r;
    private final float g;
    private final float b;

    protected ItemFindStructureEye(Properties properties, TagKey<Structure> tagKey, float r, float g, float b) {
        super(properties);
        this.FIND_STRUCTURE = tagKey;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack eyeItem = player.getItemInHand(hand);
        player.startUsingItem(hand);
        if (level instanceof ServerLevel serverlevel) {
            BlockPos blockpos = serverlevel.findNearestMapStructure(FIND_STRUCTURE, player.blockPosition(), FIND_MAX_HEIGHT, false);
            if (blockpos != null) {
                EntityEyeOfStructure eye = new EntityEyeOfStructure(level, player.getX(), player.getY(0.5D), player.getZ());
                eye.setItem(eyeItem);
                eye.signalTo(blockpos);
                eye.setR(r);
                eye.setG(g);
                eye.setB(b);
                level.gameEvent(GameEvent.PROJECTILE_SHOOT, eye.position(), GameEvent.Context.of(player));
                level.addFreshEntity(eye);
                if (player instanceof ServerPlayer) {
                    CriteriaTriggers.USED_ENDER_EYE.trigger((ServerPlayer) player, blockpos);
                }

                level.playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDER_EYE_LAUNCH, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
                level.levelEvent((Player) null, 1003, player.blockPosition(), 0);
                if (!player.getAbilities().instabuild) {
                    eyeItem.shrink(1);
                }

                player.awardStat(Stats.ITEM_USED.get(this));
                player.swing(hand, true);
                return InteractionResultHolder.success(eyeItem);
            }
        }
        return InteractionResultHolder.consume(eyeItem);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, level, tooltip, flagIn);
    }
}
