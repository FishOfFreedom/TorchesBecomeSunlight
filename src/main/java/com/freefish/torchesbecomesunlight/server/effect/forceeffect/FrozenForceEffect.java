package com.freefish.torchesbecomesunlight.server.effect.forceeffect;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;

public class FrozenForceEffect extends ForceEffect{
    @Override
    public void addEffect(LivingEntity living, int level) {
        living.playSound(SoundEvents.GLASS_PLACE, 1, 1);
        ForceEffectInstance forceEffectInstance = getForceEffectInstance();
        if(level<=2&&level!=0){
            forceEffectInstance.setLevel(forceEffectInstance.getLevel()+1);
        }
    }

    @Override
    public void removeEffect(LivingEntity living) {
        if(getForceEffectInstance().getLevel()>=2){
            if(living.level().isClientSide){
                for (int i = 0; i < 15; i++) {
                    living.level().addParticle(
                            new BlockParticleOption(ParticleTypes.BLOCK,
                                    Blocks.ICE.defaultBlockState()),
                            living.getX() + ((living.getRandom().nextDouble() - 0.5D) * living.getBbWidth()),
                            living.getY() + ((living.getRandom().nextDouble()) * living.getBbHeight()),
                            living.getZ() + ((living.getRandom().nextDouble() - 0.5D) * living.getBbWidth()),
                            0, 0, 0);
                }
            }
            living.playSound(SoundEvents.GLASS_BREAK, 3, 1);
        }
    }

    @Override
    public void tick(LivingEntity entity, int level) {
        if (entity.isOnFire()) {
            entity.clearFire();
            isRemoved = true;
        }

        if (!(entity instanceof Player player && player.isCreative())) {
            if(!entity.level().isClientSide){
                if(entity.tickCount%10==0){
                    entity.setTicksFrozen(30);
                }

                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,20,1));
            }
            if(level>1){
                entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.15F, 1, 0.15F));
                if (!(entity instanceof EnderDragon) && !entity.onGround()) {
                    entity.setDeltaMovement(entity.getDeltaMovement().add(0, -0.3, 0));
                }
            }
        }
    }

    @Override
    public ForceEffectType<?> getType() {
        return ForceEffectHandle.FROZEN_FORCE_EFFECT;
    }
}
