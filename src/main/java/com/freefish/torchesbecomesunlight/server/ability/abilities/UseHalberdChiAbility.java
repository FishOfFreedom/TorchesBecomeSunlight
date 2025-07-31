package com.freefish.torchesbecomesunlight.server.ability.abilities;

import com.freefish.torchesbecomesunlight.server.ability.AbilitySection;
import com.freefish.torchesbecomesunlight.server.ability.AbilityType;
import com.freefish.torchesbecomesunlight.server.ability.PlayerAbility;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.List;

public class UseHalberdChiAbility extends PlayerAbility {

    public UseHalberdChiAbility(AbilityType<Player, UseHalberdChiAbility> abilityType, Player user) {
        super(abilityType, user, new AbilitySection[] {
                new AbilitySection.AbilitySectionDuration(AbilitySection.AbilitySectionType.STARTUP, 18),
                new AbilitySection.AbilitySectionInstant(AbilitySection.AbilitySectionType.ACTIVE),
                new AbilitySection.AbilitySectionDuration(AbilitySection.AbilitySectionType.RECOVERY, 17)
        });
    }

    private static final RawAnimation USE_HALBERD = RawAnimation.begin().thenPlay("use_halberd");

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
        if (getTicksInUse() == 16||getTicksInUse() == 12) {
            Player player = getUser();
            if(!getLevel().isClientSide){
                doRangeAttack(player,6,20,20);
            }
            player.resetAttackStrengthTicker();
        }
    }

    public void doRangeAttack(Player player,double range, double arc,float damage){
        List<LivingEntity> entitiesHit = player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(range+5, 3, range+5), e -> e != player && player.distanceTo(e) <= range + e.getBbWidth() / 2f && e.getY() <= player.getY() + 3);
        for (LivingEntity entityHit : entitiesHit) {
            float entityHitAngle = (float) ((Math.atan2(entityHit.getZ() - player.getZ(), entityHit.getX() - player.getX()) * (180 / Math.PI) - 90) % 360);
            float entityAttackingAngle = player.getYRot() % 360;
            if (entityHitAngle < 0) {
                entityHitAngle += 360;
            }
            if (entityAttackingAngle < 0) {
                entityAttackingAngle += 360;
            }
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            float entityHitDistance = (float) Math.sqrt((entityHit.getZ() - player.getZ()) * (entityHit.getZ() - player.getZ()) + (entityHit.getX() - player.getX()) * (entityHit.getX() - player.getX())) - entityHit.getBbWidth() / 2f;
            if (entityHitDistance <= range && (entityRelativeAngle <= arc / 2 && entityRelativeAngle >= -arc / 2) || (entityRelativeAngle >= 360 - arc / 2 || entityRelativeAngle <= -360 + arc / 2)) {
                entityHit.hurt(player.damageSources().playerAttack(player), damage);
                entityHit.invulnerableTime=1;
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
