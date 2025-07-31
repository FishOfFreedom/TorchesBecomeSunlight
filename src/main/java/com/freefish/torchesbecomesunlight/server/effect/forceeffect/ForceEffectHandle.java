package com.freefish.torchesbecomesunlight.server.effect.forceeffect;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.FrozenCapability;
import com.freefish.torchesbecomesunlight.server.entity.dlc.GunKnightPatriot;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster.FrostNova;
import com.freefish.torchesbecomesunlight.server.entity.ursus.Pursuer;
import com.freefish.torchesbecomesunlight.server.event.packet.toclient.SynCapabilityMessage;
import com.freefish.torchesbecomesunlight.server.init.ItemHandle;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;

public abstract class ForceEffectHandle {
    public static final BiMap<ResourceLocation,ForceEffectType<?>> RESOURCE_LOCATION_FORCE_EFFECT = HashBiMap.create();

    public static <T extends ForceEffect> ForceEffectType<T> register(ResourceLocation resourceLocation,ForceEffectType<T> forceEffectType){
        RESOURCE_LOCATION_FORCE_EFFECT.put(resourceLocation,forceEffectType);
        return forceEffectType;
    }

    public static ForceEffectType<FrozenForceEffect> FROZEN_FORCE_EFFECT;
    public static ForceEffectType<SlowMoveForceEffect> SLOW_MOVE_FORCE_EFFECT;
    public static ForceEffectType<LightingForceEffect> LIGHTING_FORCE_EFFECT;

    public static void init(){
        FROZEN_FORCE_EFFECT = register(new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"frozen"),new ForceEffectType<>(FrozenForceEffect::new));
        SLOW_MOVE_FORCE_EFFECT = register(new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"slow_move"),new ForceEffectType<>(SlowMoveForceEffect::new));
        LIGHTING_FORCE_EFFECT = register(new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"lighting"),new ForceEffectType<>(LightingForceEffect::new));
    }

    public static void addForceEffect(LivingEntity living,ForceEffectInstance ... forceEffectInstances){
        if(living instanceof GunKnightPatriot||living instanceof Pursuer) return;
        if(living instanceof Player p&&forceEffectInstances.length!=0&&forceEffectInstances[0].getSingleEffect().getType()==ForceEffectHandle.FROZEN_FORCE_EFFECT){
            for (ItemStack item : p.getInventory().items) {
                if(item.is(ItemHandle.BLESSING_OF_SAMI.get())){
                    return;
                }
            }
        }
        if(living instanceof FrostNova p&&forceEffectInstances.length!=0&&forceEffectInstances[0].getSingleEffect().getType()==ForceEffectHandle.FROZEN_FORCE_EFFECT){
            return;
        }

        FrozenCapability.IFrozenCapability capability = CapabilityHandle.getCapability(living, CapabilityHandle.FROZEN_CAPABILITY);
        if(capability!=null){
            capability.addForceEffect(living,forceEffectInstances);

            if(!living.level().isClientSide){
                CompoundTag all = new CompoundTag();
                ListTag listTag = new ListTag();

                for(ForceEffectInstance forceEffectInstance:forceEffectInstances){
                    CompoundTag compoundTag = forceEffectInstance.serializeNBT();
                    listTag.add(compoundTag);
                }
                all.put("forceeffct",listTag);

                TorchesBecomeSunlight.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> living), new SynCapabilityMessage(living,all));
            }
        }
    }

    public static boolean hasForceEffect(LivingEntity living,ForceEffectType<?> type){
        FrozenCapability.IFrozenCapability capability = CapabilityHandle.getCapability(living, CapabilityHandle.FROZEN_CAPABILITY);
        if(capability!=null){
            return capability.hasForceEffect(type);
        } else {
            return false;
        }
    }

    public static ForceEffectInstance getForceEffect(LivingEntity living,ForceEffectType<?> type){
        FrozenCapability.IFrozenCapability capability = CapabilityHandle.getCapability(living, CapabilityHandle.FROZEN_CAPABILITY);
        if(capability!=null){
            return capability.getForceEffect(type);
        }else {
            return null;
        }
    }

    public static ResourceLocation getKey(ForceEffectType<?> forceEffectType){
        return RESOURCE_LOCATION_FORCE_EFFECT.inverse().get(forceEffectType);
    }

    public static ForceEffectType<?> getValue(ResourceLocation resourceLocation){
        return RESOURCE_LOCATION_FORCE_EFFECT.get(resourceLocation);
    }
}
