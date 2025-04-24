package com.freefish.torchesbecomesunlight.server.item.help;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.entity.ITwoStateEntity;
import com.freefish.torchesbecomesunlight.server.event.packet.toclient.InitClientEntityMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class TwoStateSpawnEggItem extends ForgeSpawnEggItem {
    ITwoStateEntity.State state = ITwoStateEntity.State.NATURE;

    public TwoStateSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> type, int backgroundColor, int highlightColor, Properties props) {
        super(type, backgroundColor, highlightColor, props);
    }

    //block
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();

        if(!pContext.getPlayer().isCrouching()){
            if (!(level instanceof ServerLevel)) {
                return InteractionResult.SUCCESS;
            } else {
                ItemStack itemstack = pContext.getItemInHand();
                BlockPos blockpos = pContext.getClickedPos();
                Direction direction = pContext.getClickedFace();
                BlockState blockstate = level.getBlockState(blockpos);
                if (blockstate.is(Blocks.SPAWNER)) {
                    BlockEntity blockentity = level.getBlockEntity(blockpos);
                    if (blockentity instanceof SpawnerBlockEntity) {
                        SpawnerBlockEntity spawnerblockentity = (SpawnerBlockEntity) blockentity;
                        EntityType<?> entitytype1 = this.getType(itemstack.getTag());
                        spawnerblockentity.setEntityId(entitytype1, level.getRandom());
                        blockentity.setChanged();
                        level.sendBlockUpdated(blockpos, blockstate, blockstate, 3);
                        level.gameEvent(pContext.getPlayer(), GameEvent.BLOCK_CHANGE, blockpos);
                        itemstack.shrink(1);
                        return InteractionResult.CONSUME;
                    }
                }

                BlockPos blockpos1;
                if (blockstate.getCollisionShape(level, blockpos).isEmpty()) {
                    blockpos1 = blockpos;
                } else {
                    blockpos1 = blockpos.relative(direction);
                }

                EntityType<?> entitytype = this.getType(itemstack.getTag());
                Entity spawn = entitytype.spawn((ServerLevel) level, itemstack, pContext.getPlayer(), blockpos1, MobSpawnType.SPAWN_EGG, true, !Objects.equals(blockpos, blockpos1) && direction == Direction.UP);
                if (spawn != null) {
                    if(spawn instanceof ITwoStateEntity twoStateEntity) {
                        twoStateEntity.setSpawnState(state);
                        TorchesBecomeSunlight.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> spawn),new InitClientEntityMessage(spawn,InitClientEntityMessage.InitDataType.ISTWOSTATE));
                    }
                    itemstack.shrink(1);
                    level.gameEvent(pContext.getPlayer(), GameEvent.ENTITY_PLACE, blockpos);
                }

                return InteractionResult.CONSUME;
            }
        }{
            switch (state){
                case NATURE -> state = ITwoStateEntity.State.ONE;
                case ONE -> state = ITwoStateEntity.State.TWO;
                default -> state = ITwoStateEntity.State.NATURE;
            }
            return InteractionResult.SUCCESS;
        }
    }

    @Override
    public EntityType<?> getType(@Nullable CompoundTag tag) {
        return super.getType(tag);
    }

    //air
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        BlockHitResult blockhitresult = getPlayerPOVHitResult(pLevel, pPlayer, ClipContext.Fluid.SOURCE_ONLY);

        if(!pPlayer.isCrouching()){
            if (blockhitresult.getType() != HitResult.Type.BLOCK) {
                return InteractionResultHolder.pass(itemstack);
            } else if (!(pLevel instanceof ServerLevel)) {
                return InteractionResultHolder.success(itemstack);
            } else {
                BlockPos blockpos = blockhitresult.getBlockPos();
                if (!(pLevel.getBlockState(blockpos).getBlock() instanceof LiquidBlock)) {
                    return InteractionResultHolder.pass(itemstack);
                } else if (pLevel.mayInteract(pPlayer, blockpos) && pPlayer.mayUseItemAt(blockpos, blockhitresult.getDirection(), itemstack)) {

                    EntityType<?> entitytype = this.getType(itemstack.getTag());
                    Entity entity = entitytype.spawn((ServerLevel) pLevel, itemstack, pPlayer, blockpos, MobSpawnType.SPAWN_EGG, false, false);
                    if (entity == null) {
                        return InteractionResultHolder.pass(itemstack);
                    } else {
                        if(entity instanceof ITwoStateEntity twoStateEntity)
                            twoStateEntity.setSpawnState(state);
                        if (!pPlayer.getAbilities().instabuild) {
                            itemstack.shrink(1);
                        }
                        pPlayer.awardStat(Stats.ITEM_USED.get(this));
                        pLevel.gameEvent(pPlayer, GameEvent.ENTITY_PLACE, entity.position());
                        return InteractionResultHolder.consume(itemstack);
                    }

                } else {
                    return InteractionResultHolder.fail(itemstack);
                }
            }
        } else {
            switch (state){
                case NATURE -> state = ITwoStateEntity.State.ONE;
                case ONE -> state = ITwoStateEntity.State.TWO;
                default -> state = ITwoStateEntity.State.NATURE;
            }
            return InteractionResultHolder.success(itemstack);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, level, tooltip, flagIn);
        tooltip.add(Component.literal("("+state+")"));
    }
}
