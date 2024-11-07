package com.freefish.torchesbecomesunlight.server.item.help;

import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import com.freefish.torchesbecomesunlight.server.entity.AnimatedEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AnimationItem extends Item {
    AnimatedEntity animatedEntity;
    int number;
    public AnimationItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand pUsedHand) {
        if (!level.isClientSide&&animatedEntity!=null) {
            if(player.isShiftKeyDown()) {
                int length = animatedEntity.getAnimations().length;
                number+=1;
                if(number>=length) number = 0;
                player.sendSystemMessage(Component.translatable(String.valueOf(number)));
            }
            else {
                AnimationActHandler.INSTANCE.sendAnimationMessage(animatedEntity,animatedEntity.getAnimations()[number]);
            }
        }
        return super.use(level, player, pUsedHand);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (entity instanceof AnimatedEntity) {
            if (!entity.level().isClientSide) {
                animatedEntity = (AnimatedEntity) entity;
                number = 0;
            }
            return true;
        }
        return false;
    }
}
