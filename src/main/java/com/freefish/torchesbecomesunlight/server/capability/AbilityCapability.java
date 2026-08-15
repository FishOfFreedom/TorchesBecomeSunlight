package com.freefish.torchesbecomesunlight.server.capability;

import net.minecraft.core.HolderLookup;
import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.entity.player.GeckoPlayer;
import com.freefish.torchesbecomesunlight.client.render.model.tools.geckolib.MowzieGeoModel;
import com.freefish.torchesbecomesunlight.server.ability.Ability;
import com.freefish.torchesbecomesunlight.server.ability.AbilityHandler;
import com.freefish.torchesbecomesunlight.server.ability.AbilityType;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.INBTSerializable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;

import javax.annotation.Nonnull;
import java.util.*;

public class AbilityCapability {
    public static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "ability_cap");

    public interface IAbilityCapability extends INBTSerializable<CompoundTag> {

        void activateAbility(LivingEntity entity, AbilityType<?, ?> ability);

        void instanceAbilities(LivingEntity entity);

        void tick(LivingEntity entity);

        AbilityType<?, ?>[] getAbilityTypesOnEntity(LivingEntity entity);

        Map<AbilityType<?, ?>, Ability> getAbilityMap();

        Ability getAbilityFromType(AbilityType<?, ?> abilityType);

        Collection<Ability> getAbilities();

        Ability getActiveAbility();

        void setActiveAbility(Ability activeAbility);

        <E extends GeoEntity> PlayState animationPredicate(AnimationState<E> e, GeckoPlayer.Perspective perspective);

        void codeAnimations(MowzieGeoModel<? extends GeoEntity> model, float partialTick);
    }

    public static class AbilityCapabilityImp implements IAbilityCapability {
        SortedMap<AbilityType<?, ?>, Ability> abilityInstances = new TreeMap<>();
        Ability activeAbility = null;
        Map<String, Tag> nbtMap = new HashMap<>();

        @Override
        public void instanceAbilities(LivingEntity entity) {
            setActiveAbility(null);
            for (AbilityType<? extends LivingEntity, ?> abilityType : getAbilityTypesOnEntity(entity)) {
                Ability ability = abilityType.makeInstance(entity);
                abilityInstances.put(abilityType, ability);
                if (nbtMap.containsKey(abilityType.getName())) ability.readNBT(nbtMap.get(abilityType.getName()));
            }
        }

        @Override
        public void activateAbility(LivingEntity entity, AbilityType<?, ?> abilityType) {
            Ability ability = abilityInstances.get(abilityType);
            if (ability != null) {
                boolean tryResult = ability.tryAbility();
                if (tryResult) ability.start();
            }
            else System.out.println("Ability " + abilityType.toString() + " does not exist on mob " + entity.getClass().getSimpleName());
        }

        @Override
        public void tick(LivingEntity entity) {
            for (Ability ability : abilityInstances.values()) {
                ability.tick();
            }
        }

        @Override
        public AbilityType<?, ?>[] getAbilityTypesOnEntity(LivingEntity entity) {
            if (entity instanceof Player) {
                return AbilityHandler.PLAYER_ABILITIES;
            }
            return new AbilityType[0];
        }

        @Override
        public Map<AbilityType<?, ?>, Ability> getAbilityMap() {
            return abilityInstances;
        }

        @Override
        public Ability getAbilityFromType(AbilityType<?, ?> abilityType) {
            return abilityInstances.get(abilityType);
        }

        @Override
        public Ability getActiveAbility() {
            return activeAbility;
        }

        @Override
        public void setActiveAbility(Ability activeAbility) {
            if (getActiveAbility() != null && getActiveAbility().isUsing()) getActiveAbility().interrupt();
            this.activeAbility = activeAbility;
        }

        @Override
        public Collection<Ability> getAbilities() {
            return abilityInstances.values();
        }

        @Override
        public <E extends GeoEntity> PlayState animationPredicate(AnimationState<E> e, GeckoPlayer.Perspective perspective) {
            return getActiveAbility().animationPredicate(e, perspective);
        }

        public void codeAnimations(MowzieGeoModel<? extends GeoEntity> model, float partialTick) {
            getActiveAbility().codeAnimations(model, partialTick);
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider registries) {
            CompoundTag compound = new CompoundTag();
            for (Map.Entry<AbilityType<?, ?>, Ability> abilityEntry : getAbilityMap().entrySet()) {
                CompoundTag nbt = abilityEntry.getValue().writeNBT();
                if (!nbt.isEmpty()) {
                    compound.put(abilityEntry.getKey().getName(), nbt);
                }
            }
            return compound;
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider registries, CompoundTag nbt) {
            CompoundTag compound = (CompoundTag) nbt;
            Set<String> keys = compound.getAllKeys();
            for (String abilityName : keys) {
                nbtMap.put(abilityName, compound.get(abilityName));
            }
        }
    }

    
}

