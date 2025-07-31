package com.freefish.torchesbecomesunlight.server.ability.abilities;

import com.freefish.torchesbecomesunlight.server.ability.AbilitySection;
import com.freefish.torchesbecomesunlight.server.ability.AbilityType;
import com.freefish.torchesbecomesunlight.server.ability.PlayerAbility;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class UseWinterPassAbility extends PlayerAbility {

    public UseWinterPassAbility(AbilityType<Player, UseWinterPassAbility> abilityType, Player user) {
        super(abilityType, user, new AbilitySection[] {
                new AbilitySection.AbilitySectionDuration(AbilitySection.AbilitySectionType.STARTUP, 600),
                new AbilitySection.AbilitySectionInstant(AbilitySection.AbilitySectionType.ACTIVE),
                new AbilitySection.AbilitySectionDuration(AbilitySection.AbilitySectionType.RECOVERY, 600)
        });
    }

    @Override
    public void start() {
        super.start();
        heldItemMainHandVisualOverride = getUser().getMainHandItem();
        if (!getUser().level().isClientSide()) {
        }
        else {
        }
    }

    @Override
    public void tickUsing() {
        super.tickUsing();

        Player player = getUser();
        Level level = player.level();
        if(level.isClientSide){
            level.addParticle(ParticleTypes.SNOWFLAKE, player.getRandomX(1), player.getY() + 1.1D, player.getRandomZ(1), 0.0D, 0.0D, 0.0D);
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

    @Override
    public boolean canPlayAnimation() {
        return false;
    }
}
