package com.freefish.torchesbecomesunlight.server.ability.abilities;

import com.freefish.torchesbecomesunlight.server.ability.AbilitySection;
import com.freefish.torchesbecomesunlight.server.ability.AbilityType;
import com.freefish.torchesbecomesunlight.server.ability.PlayerAbility;
import com.freefish.torchesbecomesunlight.server.entity.effect.StompEntity;
import com.freefish.torchesbecomesunlight.server.init.SoundHandle;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.List;

public class UseShieldAbility extends PlayerAbility {

    public UseShieldAbility(AbilityType<Player, UseShieldAbility> abilityType, Player user) {
        super(abilityType, user, new AbilitySection[] {
                new AbilitySection.AbilitySectionDuration(AbilitySection.AbilitySectionType.STARTUP, 10),
                new AbilitySection.AbilitySectionInstant(AbilitySection.AbilitySectionType.ACTIVE),
                new AbilitySection.AbilitySectionDuration(AbilitySection.AbilitySectionType.RECOVERY, 10)
        });
    }

    private static final RawAnimation USE_HALBERD = RawAnimation.begin().thenPlay("use_shield");

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
        if (getTicksInUse() == 10) {
            Player player = getUser();
            ItemStack itemInHand = player.getItemInHand(InteractionHand.MAIN_HAND);
            if(!getLevel().isClientSide){
                StompEntity stompEntity = new StompEntity(getLevel(),8,player,3);
                stompEntity.setPos(player.position().add(new Vec3(0, 0, 4.5).yRot((float) (-player.yBodyRot / 180 * Math.PI))));
                player.level().addFreshEntity(stompEntity);
                player.playSound(SoundHandle.GROUND.get(), 1.3F, 1.0F / (player.getRandom().nextFloat() * 0.4F + 0.8F));

                List<Monster> monsters = getLevel().getEntitiesOfClass(Monster.class,player.getBoundingBox().inflate(10), entity->
                        entity.distanceTo(player) <10 );
                for(Monster monster:monsters){
                    monster.setTarget(player);
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
