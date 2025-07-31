package com.freefish.torchesbecomesunlight.server.ability.abilities;

import com.freefish.torchesbecomesunlight.server.ability.AbilitySection;
import com.freefish.torchesbecomesunlight.server.ability.AbilityType;
import com.freefish.torchesbecomesunlight.server.ability.PlayerAbility;
import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.PlayerCapability;
import com.freefish.torchesbecomesunlight.server.entity.projectile.HalberdOTIEntity;
import com.freefish.torchesbecomesunlight.server.init.ItemHandle;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.core.animation.RawAnimation;

public class UseHalberdAbility extends PlayerAbility {

    public UseHalberdAbility(AbilityType<Player, UseHalberdAbility> abilityType, Player user) {
        super(abilityType, user, new AbilitySection[] {
                new AbilitySection.AbilitySectionDuration(AbilitySection.AbilitySectionType.STARTUP, 22),
                new AbilitySection.AbilitySectionInstant(AbilitySection.AbilitySectionType.ACTIVE),
                new AbilitySection.AbilitySectionDuration(AbilitySection.AbilitySectionType.RECOVERY, 22)
        });
    }

    private static final RawAnimation USE_HALBERD = RawAnimation.begin().thenPlay("throw_halberd");

    @Override
    public void start() {
        super.start();
        heldItemMainHandVisualOverride = getUser().getMainHandItem();
        if (!getUser().level().isClientSide()) {
        }
        else {
            playAnimation(USE_HALBERD);
        }
    }

    @Override
    public void tickUsing() {
        super.tickUsing();
        if (getTicksInUse() == 23) {
            Player player = getUser();
            ItemStack itemInHand = player.getItemInHand(InteractionHand.MAIN_HAND);
            PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(player, CapabilityHandle.PLAYER_CAPABILITY);
            if(itemInHand.is(ItemHandle.INFECTED_HALBERD.get())&&capability!=null){
                itemInHand.getOrCreateTag().putBoolean("tbsisActive", false);
                if(!getLevel().isClientSide){
                    int halberdUseTime = capability.getHalberdUseTime();
                    HalberdOTIEntity throwntrident = new HalberdOTIEntity(player.level(), player, itemInHand,halberdUseTime>=120);
                    throwntrident.setHalberdUseTime(halberdUseTime);
                    throwntrident.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 6F, 1.0F);
                    if (player.getAbilities().instabuild) {
                        throwntrident.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                    }
                    getLevel().addFreshEntity(throwntrident);
                    getLevel().playSound((Player) null, throwntrident, SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);
                    if (!player.getAbilities().instabuild) {
                        player.getInventory().removeItem(itemInHand);
                    }
                }
            }
            player.resetAttackStrengthTicker();
        }
    }

    @Override
    public void end() {
        super.end();
    }

    @Override
    public boolean preventsAttacking() {
        return false;
    }
}
