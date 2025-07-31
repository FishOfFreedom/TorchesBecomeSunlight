package com.freefish.torchesbecomesunlight.server.ability.abilities;

import com.freefish.torchesbecomesunlight.client.util.particle.ParticleCloud;
import com.freefish.torchesbecomesunlight.server.ability.AbilitySection;
import com.freefish.torchesbecomesunlight.server.ability.AbilityType;
import com.freefish.torchesbecomesunlight.server.ability.PlayerAbility;
import com.freefish.torchesbecomesunlight.server.init.ParticleHandler;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animation.RawAnimation;

public class UseMacheteAbility extends PlayerAbility {

    public UseMacheteAbility(AbilityType<Player, UseMacheteAbility> abilityType, Player user) {
        super(abilityType, user, new AbilitySection[] {
                new AbilitySection.AbilitySectionDuration(AbilitySection.AbilitySectionType.STARTUP, 80),
                new AbilitySection.AbilitySectionInstant(AbilitySection.AbilitySectionType.ACTIVE),
                new AbilitySection.AbilitySectionDuration(AbilitySection.AbilitySectionType.RECOVERY, 20)
        });
    }

    private static final RawAnimation USE_HALBERD = RawAnimation.begin().thenPlay("use_machete");

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
        if (getTicksInUse() == 11) {
            Player player = getUser();
            if(getLevel().isClientSide){
                int radio = 4;

                for (int i = 0; i <= radio; i++) {
                    int len = (int) ((i+1)*6.28f);
                    for (int j = 0; j < len; j++) {
                        if(player.getRandom().nextBoolean()) {
                            Vec3 move = new Vec3(0, 0, 0.5 + i).yRot((float) 6.28 * j / len +player.getRandom().nextFloat() * 0.2f - 0.1f);
                            Vec3 vec3 = player.position().add(move);
                            getLevel().addParticle(new ParticleCloud.CloudData(ParticleHandler.CLOUD.get(), 0f, 0f, 0f, (float) (10d + player.getRandom().nextDouble() * 10d), 20, ParticleCloud.EnumCloudBehavior.SHRINK, 1f), vec3.x, vec3.y + 0.2, vec3.z, 0, 0.01, 0);
                        }
                    }
                }
            }
        }
        if(!getLevel().isClientSide&&getTicksInUse() >=10){
            Player player = getUser();
            if(getTicksInUse()%5==0){
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 10, 1));
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
