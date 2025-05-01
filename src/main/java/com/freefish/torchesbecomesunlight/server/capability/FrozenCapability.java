package com.freefish.torchesbecomesunlight.server.capability;

import com.freefish.rosmontislib.client.particle.advance.base.particle.RLParticle;
import com.freefish.rosmontislib.client.particle.advance.data.EmissionSetting;
import com.freefish.rosmontislib.client.particle.advance.data.material.MaterialHandle;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction3;
import com.freefish.rosmontislib.client.particle.advance.data.number.RandomConstant;
import com.freefish.rosmontislib.client.particle.advance.data.number.color.Gradient;
import com.freefish.rosmontislib.client.particle.advance.data.shape.Sphere;
import com.freefish.rosmontislib.client.particle.advance.effect.EntityEffect;
import com.freefish.rosmontislib.client.utils.GradientColor;
import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.event.ServerNetwork;
import com.freefish.torchesbecomesunlight.server.event.packet.toclient.SynCapabilityMessage;
import com.google.common.collect.Maps;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.UUID;

public class FrozenCapability {
    public static ResourceLocation ID = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "frozen_cap");

    public interface IFrozenCapability extends INBTSerializable<CompoundTag> {
        //dishAttribute
        void joinWorld(EntityJoinLevelEvent event);
        void removeAttributeModifiers(LivingEntity pLivingEntity, AttributeMap pAttributeMap, int pAmplifier);
        void addAttributeModifiers(LivingEntity pLivingEntity, AttributeMap pAttributeMap, int pAmplifier);
        void onEffectUpdated(LivingEntity pEntity,float act,float move,float armor,float health);
        boolean canDeleteDish();
        void setCanDeleteDish(boolean canDeleteDish);
        //Frozen
        void tickFrozen(final LivingEntity entity);
        void setFrozen(final LivingEntity target, int duration);
        void setFrozen(int duration);
        void setIsFrozen(boolean isFrozen);
        boolean getFrozen();
        int getFrozenTick();
        void clearFrozen(final LivingEntity entity);
        //Lighting
        void tickLighting(final LivingEntity entity);
        void setLighting(final LivingEntity target, int duration);
        void setLighting(int duration);
        void setIsLighting(boolean isLighting);
        boolean getLighting();
        int getLightingTick();
        void clearLighting(final LivingEntity entity);
    }

    public static class FrozenCapabilityImp implements IFrozenCapability {
        public int frozenTicks;
        public boolean isFrozen;
        public int lightingTicks;
        public boolean isLighting;
        public boolean canDeleteDish = true;

        public float[] attributes = new float[4];
        private final Map<Attribute, AttributeModifier> attributeModifiers = Maps.newHashMap();

        public void joinWorld(EntityJoinLevelEvent event){
            addAttributeModifier(Attributes.ATTACK_DAMAGE, "6ba8181f-d9b5-4ba1-8ea9-08661c3672b9",attributes[0], AttributeModifier.Operation.MULTIPLY_TOTAL);
            addAttributeModifier(Attributes.MOVEMENT_SPEED,"1a2f907e-b53b-430d-9318-e6b20024457e",attributes[1], AttributeModifier.Operation.MULTIPLY_TOTAL);
            addAttributeModifier(Attributes.ARMOR,         "68e93bcf-0534-4780-b0e0-0ace7ebe8236",attributes[2], AttributeModifier.Operation.MULTIPLY_TOTAL);
            addAttributeModifier(Attributes.MAX_HEALTH,    "37d95b34-6f62-4793-8b75-0fdf7b8637c1",attributes[3], AttributeModifier.Operation.MULTIPLY_TOTAL);

            LivingEntity entity = (LivingEntity) event.getEntity();
            addAttributeModifiers(entity,entity.getAttributes(),1);
        }

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

        public void tickLighting(final LivingEntity entity) {
            if (!isLighting) {
                return;
            }


            //if (entity.isOnFire()) {
            //    clearFrozen(entity);
            //    entity.clearFire();
            //    return;
            //}

            if (!entity.isAlive()) {
                clearLighting(entity);
                return;
            }

            if (lightingTicks > 0) {
                lightingTicks--;
            } else {
                clearLighting(entity);
            }

            if (isLighting && !(entity instanceof Player player && player.isCreative())) {
                if(entity.level().isClientSide&&entity.tickCount%20==0){
                    RLParticle rlParticle2 = new RLParticle();
                    rlParticle2.config.setDuration(20);
                    rlParticle2.config.setStartLifetime(NumberFunction.constant(8));
                    rlParticle2.config.setStartSpeed(NumberFunction.constant(2));
                    rlParticle2.config.setStartColor(new Gradient(new GradientColor(0XFFDFEF86)));
                    rlParticle2.config.getEmission().setEmissionRate(NumberFunction.constant(0.2));
                    rlParticle2.config.getMaterial().setMaterial(MaterialHandle.VOID);
                    Sphere circle2 = new Sphere();circle2.setRadius(0.5f);
                    rlParticle2.config.getShape().setShape(circle2);
                    rlParticle2.config.getShape().setPosition(new NumberFunction3(NumberFunction.constant(0),new RandomConstant(2,0,true),NumberFunction.constant(0)));
                    rlParticle2.config.getNoise().open();
                    rlParticle2.config.getNoise().setPosition(new NumberFunction3(1.5));

                    rlParticle2.config.getVelocityOverLifetime().open();
                    rlParticle2.config.getVelocityOverLifetime().setLinear(new NumberFunction3(0,6,0));

                    rlParticle2.config.trails.open();
                    rlParticle2.config.trails.config.getMaterial().setMaterial(MaterialHandle.CIRCLE);
                    rlParticle2.config.trails.config.getRenderer().setBloomEffect(true);

                    EntityEffect effect = new EntityEffect(entity.level(),entity);
                    float scale = entity.getBbHeight()/4;
                    rlParticle2.updateScale(new Vector3f(scale));
                    rlParticle2.emmit(effect);
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
            ServerNetwork.toClientMessage(target,new SynCapabilityMessage(target,frozenTicks,0));
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
                ServerNetwork.toClientMessage(entity,new SynCapabilityMessage(entity,frozenTicks,0));
        }

        public void setLighting(final LivingEntity target, int duration) {
            if(target.level().isClientSide) return;

            lightingTicks = duration;
            isLighting = true;
            ServerNetwork.toClientMessage(target,new SynCapabilityMessage(target, lightingTicks,1));
        }

        public void setIsLighting(boolean isLighting){
            this.isLighting = isLighting;
        }

        public void setLighting(int LightingTicks){
            this.lightingTicks = LightingTicks;
        }

        public int getLightingTick(){
            return this.lightingTicks;
        };

        public boolean getLighting(){
            return isLighting;
        }

        public void clearLighting(final LivingEntity entity) {
            isLighting = false;
            lightingTicks = 0;
            if(!entity.level().isClientSide)
                ServerNetwork.toClientMessage(entity,new SynCapabilityMessage(entity, lightingTicks,1));
        }



        public void addAttributeModifier(Attribute pAttribute, String pUuid, double pAmount, AttributeModifier.Operation pOperation) {
            AttributeModifier attributemodifier = new AttributeModifier(UUID.fromString(pUuid), "dishattributemodifier", pAmount, pOperation);
            this.attributeModifiers.put(pAttribute, attributemodifier);
        }

        public void removeAttributeModifiers(LivingEntity pLivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
            for(Map.Entry<Attribute, AttributeModifier> entry : this.attributeModifiers.entrySet()) {
                AttributeInstance attributeinstance = pAttributeMap.getInstance(entry.getKey());
                if (attributeinstance != null) {
                    attributeinstance.removeModifier(entry.getValue());
                }
            }
        }

        public void addAttributeModifiers(LivingEntity pLivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
            for(Map.Entry<Attribute, AttributeModifier> entry : this.attributeModifiers.entrySet()) {
                AttributeInstance attributeinstance = pAttributeMap.getInstance(entry.getKey());
                if (attributeinstance != null) {
                    AttributeModifier attributemodifier = entry.getValue();
                    attributeinstance.removeModifier(attributemodifier);
                    attributeinstance.addPermanentModifier(new AttributeModifier(attributemodifier.getId(), "dishattributemodifier" + " " + pAmplifier, this.getAttributeModifierValue(pAmplifier, attributemodifier), attributemodifier.getOperation()));
                }
            }
        }

        public double getAttributeModifierValue(int pAmplifier, AttributeModifier pModifier) {
            return pModifier.getAmount() * (double)(pAmplifier + 1);
        }

        public void onEffectUpdated(LivingEntity pEntity,float act,float move,float armor,float health) {
            this.removeAttributeModifiers(pEntity, pEntity.getAttributes(), 1);
            attributeModifiers.clear();

            attributes[0] = act;attributes[1] = move;attributes[2] = armor;attributes[3] = health;

            addAttributeModifier(Attributes.ATTACK_DAMAGE, "6ba8181f-d9b5-4ba1-8ea9-08661c3672b9",act, AttributeModifier.Operation.MULTIPLY_TOTAL);
            addAttributeModifier(Attributes.MOVEMENT_SPEED,"1a2f907e-b53b-430d-9318-e6b20024457e",move, AttributeModifier.Operation.MULTIPLY_TOTAL);
            addAttributeModifier(Attributes.ARMOR,         "68e93bcf-0534-4780-b0e0-0ace7ebe8236",armor, AttributeModifier.Operation.MULTIPLY_TOTAL);
            addAttributeModifier(Attributes.MAX_HEALTH,    "37d95b34-6f62-4793-8b75-0fdf7b8637c1",health, AttributeModifier.Operation.MULTIPLY_TOTAL);
            this.addAttributeModifiers(pEntity, pEntity.getAttributes(), 1);
        }

        @Override
        public boolean canDeleteDish() {
            return canDeleteDish;
        }

        @Override
        public void setCanDeleteDish(boolean canDeleteDish) {
            this.canDeleteDish = canDeleteDish;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag frozenData = new CompoundTag();
            frozenData.putInt("frozenTicks", frozenTicks);
            frozenData.putBoolean("isfrozen", isFrozen);
            frozenData.putInt("lightingTicks", lightingTicks);
            frozenData.putBoolean("islighting", isLighting);

            frozenData.putFloat("act",   attributes[0]);
            frozenData.putFloat("move",  attributes[1]);
            frozenData.putFloat("armor", attributes[2]);
            frozenData.putFloat("health",attributes[3]);
            return frozenData;
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            frozenTicks = tag.getInt("frozenTicks");
            isFrozen = tag.getBoolean("isfrozen");
            lightingTicks = tag.getInt("lightingTicks");
            isLighting = tag.getBoolean("islighting");

            attributes[0] = tag.getFloat("act");
            attributes[1] = tag.getFloat("move");
            attributes[2] = tag.getFloat("armor");
            attributes[3] = tag.getFloat("health");
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
