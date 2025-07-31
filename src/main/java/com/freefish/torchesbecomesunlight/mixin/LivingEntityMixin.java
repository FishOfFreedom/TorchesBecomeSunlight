package com.freefish.torchesbecomesunlight.mixin;

import com.freefish.torchesbecomesunlight.server.item.food.TBSFood;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "eat"
            ,at = @At("HEAD"), cancellable = true)
    public void eatTBSFood(Level pLevel, ItemStack pFood,CallbackInfoReturnable<ItemStack> cir) {
        LivingEntity living = (LivingEntity) (Object) this;

        if(pFood.getItem() instanceof TBSFood tbsFood){
            pLevel.playSound((Player)null, living.getX(), living.getY(), living.getZ(), living.getEatingSound(pFood), SoundSource.NEUTRAL, 1.0F, 1.0F + (pLevel.random.nextFloat() - pLevel.random.nextFloat()) * 0.4F);
            pFood.hurtAndBreak(1,living,(living1 -> {}));
            living.gameEvent(GameEvent.EAT);
            cir.setReturnValue(pFood);
            cir.cancel();
        }
    }
}