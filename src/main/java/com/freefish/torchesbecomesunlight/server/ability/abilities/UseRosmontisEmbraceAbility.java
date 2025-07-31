package com.freefish.torchesbecomesunlight.server.ability.abilities;

import com.freefish.torchesbecomesunlight.client.render.entity.player.GeckoPlayer;
import com.freefish.torchesbecomesunlight.server.ability.AbilitySection;
import com.freefish.torchesbecomesunlight.server.ability.AbilityType;
import com.freefish.torchesbecomesunlight.server.ability.PlayerAbility;
import com.freefish.torchesbecomesunlight.server.entity.projectile.IceCrystal;
import com.freefish.torchesbecomesunlight.server.util.FFEntityUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animation.RawAnimation;

public class UseRosmontisEmbraceAbility extends PlayerAbility {

    public UseRosmontisEmbraceAbility(AbilityType<Player, UseRosmontisEmbraceAbility> abilityType, Player user) {
        super(abilityType, user, new AbilitySection[] {
                new AbilitySection.AbilitySectionDuration(AbilitySection.AbilitySectionType.STARTUP, 20),
                new AbilitySection.AbilitySectionInstant(AbilitySection.AbilitySectionType.ACTIVE),
                new AbilitySection.AbilitySectionDuration(AbilitySection.AbilitySectionType.RECOVERY, 20)
        });
    }

    private static final RawAnimation USE_HALBERD = RawAnimation.begin().thenPlay("use_lullaby");

    @Override
    public void start() {
        super.start();
        heldItemMainHandVisualOverride = getUser().getMainHandItem();
        if (!getUser().level().isClientSide()) {
        }
        else {
            playAnimation(USE_HALBERD, GeckoPlayer.Perspective.THIRD_PERSON);
        }
    }

    @Override
    public void tickUsing() {
        super.tickUsing();
        Player player = getUser();
        Level level = getLevel();
        if(!level.isClientSide){
            if (getTicksInUse() == 5) {
                for(int i = 0;i<5;i++){
                    RandomSource random = player.getRandom();
                    IceCrystal.spawnWaitCrystal(player.level(), new Vec3(4 - random.nextInt(9), 2 + random.nextInt(5), 4 - random.nextInt(9)).add(player.position()), player, FFEntityUtils.getHeadRotVec(player, new Vec3(0, 0, 10)));
                }
            }
            if(getTicksInUse()%5==0&&getTicksInUse()<=35){
                RandomSource random = player.getRandom();
                IceCrystal.spawnWaitCrystal(player.level(),new Vec3(4-random.nextInt(9),2+random.nextInt(5),4-random.nextInt(9)).add(player.position()),player, FFEntityUtils.getHeadRotVec(player,new Vec3(0,0,10)));
            }
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
