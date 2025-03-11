package com.freefish.torchesbecomesunlight.server.capability;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.event.ServerNetwork;
import com.freefish.torchesbecomesunlight.server.event.packet.toclient.SynCapabilityMessage;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

public class FrozenCapability {
    public static ResourceLocation ID = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "frozen_cap");

    public interface IFrozenCapability extends INBTSerializable<CompoundTag> {
        void tickFrozen(final LivingEntity entity);
        void setFrozen(final LivingEntity target, int duration);
        void setFrozen(int duration);
        void setIsFrozen(boolean isFrozen);
        boolean getFrozen();
        int getFrozenTick();
        void clearFrozen(final LivingEntity entity);
    }

    public static class FrozenCapabilityImp implements IFrozenCapability {
        public int frozenTicks;
        public boolean isFrozen;

        public void tickFrozen(final LivingEntity entity) {
            if (!isFrozen) {
                return;
            }

            if (entity.isOnFire()) {
                clearFrozen(entity);
                entity.clearFire();
                return;
            }

            if (!entity.isAlive()) {
                clearFrozen(entity);
                return;
            }

            if (frozenTicks > 0) {
                frozenTicks--;
            } else {
                clearFrozen(entity);
            }

            if (isFrozen && !(entity instanceof Player player && player.isCreative())) {
                entity.setTicksFrozen(150);
                entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.15F, 1, 0.15F));

                if (!(entity instanceof EnderDragon) && !entity.onGround()) {
                    entity.setDeltaMovement(entity.getDeltaMovement().add(0, -0.3, 0));
                }
            }
        }

        public void setFrozen(final LivingEntity target, int duration) {
            if(target.level().isClientSide) return;

            if (!isFrozen) {
                target.playSound(SoundEvents.GLASS_PLACE, 1, 1);
            }
            frozenTicks = duration;
            isFrozen = true;
            ServerNetwork.toClientMessage(target,new SynCapabilityMessage(target,frozenTicks));
        }

        public void setIsFrozen(boolean isFrozen){
            this.isFrozen = isFrozen;
        }

        public void setFrozen(int frozenTicks){
            this.frozenTicks = frozenTicks;
        }

        public int getFrozenTick(){
            return this.frozenTicks;
        };

        public boolean getFrozen(){
            return isFrozen;
        }

        public void clearFrozen(final LivingEntity entity) {
            for (int i = 0; i < 15; i++) {
                entity.level().addParticle(
                        new BlockParticleOption(ParticleTypes.BLOCK,
                                Blocks.ICE.defaultBlockState()),
                        entity.getX() + ((entity.getRandom().nextDouble() - 0.5D) * entity.getBbWidth()),
                        entity.getY() + ((entity.getRandom().nextDouble()) * entity.getBbHeight()),
                        entity.getZ() + ((entity.getRandom().nextDouble() - 0.5D) * entity.getBbWidth()),
                        0, 0, 0);
            }
            entity.playSound(SoundEvents.GLASS_BREAK, 3, 1);
            isFrozen = false;
            frozenTicks = 0;
            if(!entity.level().isClientSide)
                ServerNetwork.toClientMessage(entity,new SynCapabilityMessage(entity,frozenTicks));
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag frozenData = new CompoundTag();
            frozenData.putInt("frozenTicks", frozenTicks);
            frozenData.putBoolean("isfrozen", isFrozen);
            return frozenData;
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            frozenTicks = tag.getInt("frozenTicks");
            isFrozen = tag.getBoolean("isfrozen");
        }

    }

    public static class Provider implements ICapabilityProvider, ICapabilitySerializable<CompoundTag>
    {
        private final LazyOptional<IFrozenCapability> instance = LazyOptional.of(FrozenCapabilityImp::new);

        @Override
        public CompoundTag serializeNBT() {
            return instance.orElseThrow(NullPointerException::new).serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            instance.orElseThrow(NullPointerException::new).deserializeNBT(nbt);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
            return CapabilityHandle.FROZEN_CAPABILITY.orEmpty(cap, instance.cast());
        }
    }
}
