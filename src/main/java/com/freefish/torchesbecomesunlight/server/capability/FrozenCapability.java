package com.freefish.torchesbecomesunlight.server.capability;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.effect.forceeffect.ForceEffectHandle;
import com.freefish.torchesbecomesunlight.server.effect.forceeffect.ForceEffectInstance;
import com.freefish.torchesbecomesunlight.server.effect.forceeffect.ForceEffectType;
import com.google.common.collect.BiMap;
import com.google.common.collect.Maps;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class FrozenCapability {
    public static ResourceLocation ID = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "frozen_cap");

    public interface IFrozenCapability extends INBTSerializable<CompoundTag> {
        //dishAttribute
        void joinWorld(EntityJoinLevelEvent event);
        void onEffectUpdated(LivingEntity pEntity,float act,float move,float armor,float health);

        //ForceEffect
        void addForceEffect(LivingEntity living, ForceEffectInstance ... forceEffectInstances);
        boolean hasForceEffect(ForceEffectType<?> forceEffectType);
        ForceEffectInstance getForceEffect(ForceEffectType<?> forceEffectType);
        void tick(LivingEntity living);
    }

    public static class FrozenCapabilityImp implements IFrozenCapability {
        public boolean canDeleteDish = true;

        public float[] attributes = new float[4];
        public boolean isDishEffectOn;
        public UUID[] uuids = new UUID[]{
                UUID.fromString("6ba8181f-d9b5-4ba1-8ea9-08661c3672b9"),
                UUID.fromString("1a2f907e-b53b-430d-9318-e6b20024457e"),
                UUID.fromString("68e93bcf-0534-4780-b0e0-0ace7ebe8236"),
                UUID.fromString("37d95b34-6f62-4793-8b75-0fdf7b8637c1")
        };
        private final Map<Attribute, AttributeModifier> attributeModifiers = Maps.newHashMap();

        private final Map<ResourceLocation, ForceEffectInstance> forceEffects = Maps.newHashMap();

        public void joinWorld(EntityJoinLevelEvent event){
            //addAttributeModifier(Attributes.ATTACK_DAMAGE, "6ba8181f-d9b5-4ba1-8ea9-08661c3672b9",attributes[0], AttributeModifier.Operation.MULTIPLY_TOTAL);
            //addAttributeModifier(Attributes.MOVEMENT_SPEED,"1a2f907e-b53b-430d-9318-e6b20024457e",attributes[1], AttributeModifier.Operation.MULTIPLY_TOTAL);
            //addAttributeModifier(Attributes.ARMOR,         "68e93bcf-0534-4780-b0e0-0ace7ebe8236",attributes[2], AttributeModifier.Operation.MULTIPLY_TOTAL);
            //addAttributeModifier(Attributes.MAX_HEALTH,    "37d95b34-6f62-4793-8b75-0fdf7b8637c1",attributes[3], AttributeModifier.Operation.MULTIPLY_TOTAL);

            LivingEntity entity = (LivingEntity) event.getEntity();
            //addAttributeModifiers(entity,entity.getAttributes(),1);
        }

        @Override
        public void addForceEffect(LivingEntity living, ForceEffectInstance... forceEffectInstances) {
            for(ForceEffectInstance forceEffectInstance:forceEffectInstances){
                BiMap<ForceEffectType<?>, ResourceLocation> inverse = ForceEffectHandle.RESOURCE_LOCATION_FORCE_EFFECT.inverse();
                ResourceLocation resourceLocation = inverse.get(forceEffectInstance.getSingleEffect().getType());

                ForceEffectInstance oForce = forceEffects.get(resourceLocation);

                if(oForce==null){
                    forceEffectInstance.getSingleEffect().addEffect(living,0);
                    forceEffects.put(resourceLocation,forceEffectInstance);
                }else {
                    forceEffectInstance.getSingleEffect().addEffect(living, oForce.getLevel());

                    int oLevel = oForce.getLevel();
                    if(forceEffectInstance.getLevel()>=oLevel){
                        forceEffects.put(resourceLocation, forceEffectInstance);
                    }
                }
            }
        }

        @Override
        public boolean hasForceEffect(ForceEffectType<?> forceEffectType) {
            BiMap<ForceEffectType<?>, ResourceLocation> inverse = ForceEffectHandle.RESOURCE_LOCATION_FORCE_EFFECT.inverse();
            ResourceLocation resourceLocation = inverse.get(forceEffectType);
            return forceEffects.containsKey(resourceLocation);
        }

        @Override
        public ForceEffectInstance getForceEffect(ForceEffectType<?> forceEffectType) {
            BiMap<ForceEffectType<?>, ResourceLocation> inverse = ForceEffectHandle.RESOURCE_LOCATION_FORCE_EFFECT.inverse();
            ResourceLocation resourceLocation = inverse.get(forceEffectType);
            return forceEffects.get(resourceLocation);
        }

        @Override
        public void tick(LivingEntity living) {

            Iterator<Map.Entry<ResourceLocation, ForceEffectInstance>> iterator = forceEffects.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<ResourceLocation, ForceEffectInstance> entry = iterator.next();
                ForceEffectInstance value = entry.getValue();
                if (value.getTime()<=0||value.getSingleEffect().isRemoved) {
                    value.getSingleEffect().removeEffect(living);
                    iterator.remove();
                }else {
                    value.tick(living);
                }
            }
        }

        public void onEffectUpdated(LivingEntity pEntity,float act,float move,float armor,float health) {
            AttributeModifier healthAttribute = new AttributeModifier(uuids[0], "Health", health, AttributeModifier.Operation.ADDITION);
            AttributeModifier attackAttribute = new AttributeModifier(uuids[1], "Attack", act, AttributeModifier.Operation.ADDITION);
            AttributeModifier armorAttribute =  new AttributeModifier(uuids[2], "Armor", armor, AttributeModifier.Operation.ADDITION);
            AttributeModifier speedAttribute =  new AttributeModifier(uuids[3], "Speed", move/4, AttributeModifier.Operation.ADDITION);

            AttributeInstance healthAt = pEntity.getAttribute(Attributes.MAX_HEALTH);
            AttributeInstance actAt = pEntity.getAttribute(Attributes.ATTACK_DAMAGE);
            AttributeInstance armorAt = pEntity.getAttribute(Attributes.ARMOR);
            AttributeInstance speed = pEntity.getAttribute(Attributes.MOVEMENT_SPEED);

            if(act==0&&move==0&&armor==0&&health==0) {
                healthAt.removeModifier(uuids[0]);
                actAt.removeModifier(uuids[1]);
                armorAt.removeModifier(uuids[2]);
                speed.removeModifier(uuids[3]);
                isDishEffectOn = false;
            }else {
                if(isDishEffectOn){
                    healthAt.removeModifier(uuids[0]);
                    actAt.removeModifier(uuids[1]);
                    armorAt.removeModifier(uuids[2]);
                    speed.removeModifier(uuids[3]);

                    healthAt.addPermanentModifier(healthAttribute);
                    actAt.addPermanentModifier(attackAttribute);
                    armorAt.addPermanentModifier(armorAttribute);
                    speed.addPermanentModifier(speedAttribute);
                }else {
                    healthAt.addPermanentModifier(healthAttribute);
                    actAt.addPermanentModifier(attackAttribute);
                    armorAt.addPermanentModifier(armorAttribute);
                    speed.addPermanentModifier(speedAttribute);
                }
                float maxHealth = pEntity.getMaxHealth();
                if(pEntity.getHealth()> maxHealth){
                    pEntity.setHealth(maxHealth);
                }
                isDishEffectOn = true;
            }
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag frozenData = new CompoundTag();

            frozenData.putFloat("act",   attributes[0]);
            frozenData.putFloat("move",  attributes[1]);
            frozenData.putFloat("armor", attributes[2]);
            frozenData.putFloat("health",attributes[3]);
            frozenData.putBoolean("dish",isDishEffectOn);
            return frozenData;
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {

            attributes[0] = tag.getFloat("act");
            attributes[1] = tag.getFloat("move");
            attributes[2] = tag.getFloat("armor");
            attributes[3] = tag.getFloat("health");
            isDishEffectOn = tag.getBoolean("dish");
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
