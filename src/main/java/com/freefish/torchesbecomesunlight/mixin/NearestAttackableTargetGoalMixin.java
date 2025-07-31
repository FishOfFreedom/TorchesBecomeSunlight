package com.freefish.torchesbecomesunlight.mixin;

import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.PlayerCapability;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.Tags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.function.Predicate;

@Mixin(NearestAttackableTargetGoal.class)
public abstract class NearestAttackableTargetGoalMixin<T extends LivingEntity> extends TargetGoal {
    @Shadow
    protected TargetingConditions targetConditions;

    public NearestAttackableTargetGoalMixin(Mob pMob, boolean pMustSee) {
        super(pMob, pMustSee);
    }
    @Inject(
            method = "<init>(Lnet/minecraft/world/entity/Mob;Ljava/lang/Class;IZZLjava/util/function/Predicate;)V",
            at = @At("RETURN"))
    private void onConstructed(Mob mob, Class<T> targetType, int randomInterval,
                               boolean mustSee, boolean mustReach,
                               @Nullable Predicate<LivingEntity> targetPredicate,
                               CallbackInfo ci) {
        Predicate<LivingEntity> newPredicate = entity -> {
            if (entity instanceof Player player && shouldExcludePlayer(mob,player)) {
                return false;
            }
            return targetPredicate == null || targetPredicate.test(entity);
        };
        this.targetConditions = TargetingConditions.forCombat().range(this.getFollowDistance()).selector(newPredicate);
    }

    private boolean shouldExcludePlayer(Mob mob, Player player) {
        if(!mob.getType().is(Tags.EntityTypes.BOSSES)) {
            PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(player, CapabilityHandle.PLAYER_CAPABILITY);
            if (capability != null) {
                if (capability.isSankta()) {
                    return true;
                }
            }
        }
        return false;
    }
}
