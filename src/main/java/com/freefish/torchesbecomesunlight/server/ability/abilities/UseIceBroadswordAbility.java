package com.freefish.torchesbecomesunlight.server.ability.abilities;

import com.freefish.torchesbecomesunlight.server.ability.AbilitySection;
import com.freefish.torchesbecomesunlight.server.ability.AbilityType;
import com.freefish.torchesbecomesunlight.server.ability.PlayerAbility;
import com.freefish.torchesbecomesunlight.server.effect.forceeffect.ForceEffectHandle;
import com.freefish.torchesbecomesunlight.server.effect.forceeffect.ForceEffectInstance;
import com.freefish.torchesbecomesunlight.server.init.SoundHandle;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.List;

public class UseIceBroadswordAbility extends PlayerAbility {

    public UseIceBroadswordAbility(AbilityType<Player, UseIceBroadswordAbility> abilityType, Player user) {
        super(abilityType, user, new AbilitySection[] {
                new AbilitySection.AbilitySectionDuration(AbilitySection.AbilitySectionType.STARTUP, 15),
                new AbilitySection.AbilitySectionInstant(AbilitySection.AbilitySectionType.ACTIVE),
                new AbilitySection.AbilitySectionDuration(AbilitySection.AbilitySectionType.RECOVERY, 15)
        });
    }

    private static final RawAnimation USE_HALBERD = RawAnimation.begin().thenPlay("use_ice_broadsword");

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
        int tick = getTicksInUse();
        if(!getLevel().isClientSide){
            float damage = 15;

            if(tick == 15)
                getUser().playSound(SoundHandle.AXE_SWEPT.get(), 1.0F, 1.0F / (getUser().getRandom().nextFloat() * 0.4F + 0.8F));
            else if (tick == 16) {
                this.doRangeAttack(getUser(),4,90,damage);
            }
        }

        if(tick == 14){
            this.dashForward(4,0);
        }
    }

    public void dashForward(float maxLen,float yawOffset){
        float jumpLen;
        jumpLen = maxLen / 4;

        Vec3 direction = new Vec3(0, Math.sqrt(jumpLen)*0.1, jumpLen).yRot((float) (yawOffset-getUser().getYRot() / 180 * org.joml.Math.PI));
        getUser().setDeltaMovement(direction);
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
                ForceEffectInstance forceEffect = ForceEffectHandle.getForceEffect(entityHit, ForceEffectHandle.FROZEN_FORCE_EFFECT);
                float d1;

                if(forceEffect!=null&&forceEffect.getLevel()>=2){
                    d1 = damage*3;
                    forceEffect.discard(entityHit);
                }else {
                    d1 = damage;
                }

                entityHit.hurt(player.damageSources().playerAttack(player), d1);
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
