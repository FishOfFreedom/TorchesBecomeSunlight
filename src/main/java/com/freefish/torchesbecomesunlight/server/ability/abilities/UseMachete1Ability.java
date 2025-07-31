package com.freefish.torchesbecomesunlight.server.ability.abilities;

import com.freefish.torchesbecomesunlight.server.ability.AbilitySection;
import com.freefish.torchesbecomesunlight.server.ability.AbilityType;
import com.freefish.torchesbecomesunlight.server.ability.PlayerAbility;
import com.freefish.torchesbecomesunlight.server.config.ConfigHandler;
import com.freefish.torchesbecomesunlight.server.entity.effect.PlayerSkillHelpEntity;
import com.freefish.torchesbecomesunlight.server.entity.ursus.Pursuer;
import com.freefish.torchesbecomesunlight.server.util.FFEntityUtils;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.List;

public class UseMachete1Ability extends PlayerAbility {

    public UseMachete1Ability(AbilityType<Player, UseMachete1Ability> abilityType, Player user) {
        super(abilityType, user, new AbilitySection[] {
                new AbilitySection.AbilitySectionDuration(AbilitySection.AbilitySectionType.STARTUP, 20),
                new AbilitySection.AbilitySectionInstant(AbilitySection.AbilitySectionType.ACTIVE),
                new AbilitySection.AbilitySectionDuration(AbilitySection.AbilitySectionType.RECOVERY, 21)
        });
    }

    private static final RawAnimation USE_HALBERD = RawAnimation.begin().thenPlay("use_machete_1");
    private Vec3 revec = Vec3.ZERO;

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
            if(!getLevel().isClientSide){
                revec = player.position().add(0,0.5,0);
                Vec3 bodyRotVec = FFEntityUtils.getBodyRotVec(player, new Vec3(0, 0, 8));
                player.teleportTo(bodyRotVec.x,bodyRotVec.y,bodyRotVec.z);
            }
        }
        if (getTicksInUse() == 12) {
            Player player = getUser();
            if(!getLevel().isClientSide){
                PlayerSkillHelpEntity entity = new PlayerSkillHelpEntity(getLevel(), player, 1, revec);
                entity.setPos(player.position().add(0,0.5,0));
                getLevel().addFreshEntity(entity);

                Vec3 line = revec.subtract(player.position()).normalize();
                List<LivingEntity> list = player.level().getEntitiesOfClass(LivingEntity.class,player.getBoundingBox().inflate(22), spentity -> !(spentity instanceof Pursuer));
                for (LivingEntity livingEntity : list) {
                    if (livingEntity instanceof Player player1 && player1.isCreative()) continue;
                    if(livingEntity == player) continue;

                    Vec3 subtract = livingEntity.position().subtract(player.position());
                    double dot = line.dot(subtract);
                    if (dot > 0) {
                        Vec3 line1 = line.scale(dot);
                        float len = (float) line1.subtract(subtract).length();
                        if (len < 2) {
                            livingEntity.setDeltaMovement(0, 0, 0);
                            livingEntity.setPos(livingEntity.xo, livingEntity.yo, livingEntity.zo);
                            livingEntity.hurt(player.damageSources().playerAttack(player), ConfigHandler.COMMON.TOOLs.MACHETE.attackDamageValue*2);
                            livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,10,10));
                        }
                    }
                }
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
