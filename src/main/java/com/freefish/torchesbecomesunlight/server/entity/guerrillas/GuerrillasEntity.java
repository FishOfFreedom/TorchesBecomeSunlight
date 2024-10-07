package com.freefish.torchesbecomesunlight.server.entity.guerrillas;

import com.freefish.torchesbecomesunlight.server.entity.AnimatedEntity;
import com.freefish.torchesbecomesunlight.server.entity.ai.attribute.AttributeRegistry;
import com.freefish.torchesbecomesunlight.server.entity.FreeFishEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public abstract class GuerrillasEntity extends AnimatedEntity {
    //private static final EntityDataAccessor<Float> ARMORDURABILITY = SynchedEntityData.defineId(GuerrillasEntity.class, EntityDataSerializers.FLOAT);

    public GuerrillasEntity(EntityType<? extends GuerrillasEntity> entityType, Level level) {
        super(entityType, level);
        //this.setArmorDurability(this.getMaxArmorDurability());
    }
/*
    @Override
    public void tick() {
        super.tick();
        if(getTarget() instanceof GuerrillasEntity)
            setTarget(null);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        float armorDurability = this.entityData.get(ARMORDURABILITY);
        if (armorDurability > 0) {
            if (amount <= 0) return false;
            amount = this.getDamageAfterArmorAbsorb(source, amount);
            amount = this.getDamageAfterMagicAbsorb(source, amount);
            float f2 = Math.max(amount - this.getAbsorptionAmount(), 0.0F);
            this.setAbsorptionAmount(this.getAbsorptionAmount() - (amount - f2));
            float f = amount - f2;
            if (f > 0.0F && f < 3.4028235E37F && source.getDirectEntity() instanceof ServerPlayer) {
                ((ServerPlayer)source.getDirectEntity()).awardStat(Stats.DAMAGE_DEALT_ABSORBED, Math.round(f * 10.0F));
            }
            //if (f2 != 0.0F) {
            //    this.getCombatTracker().trackDamage(source, armorDurability, f2);
            //    this.getCombatTracker().trackDamage(source, armorDurability, f2);
            //    setArmorDurability(armorDurability - f2);
            //    this.setAbsorptionAmount(this.getAbsorptionAmount() - f2);
            //}
            return super.hurt(source, 0);
        }
        return super.hurt(source, amount);
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ARMORDURABILITY, 0f);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("ArmorDurability", this.getArmorDurability());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setArmorDurability(compound.getFloat("ArmorDurability"));
    }

    public void setArmorDurability(float armorDurability) {
        this.entityData.set(ARMORDURABILITY, Mth.clamp(armorDurability, 0.0F, this.getMaxArmorDurability()));
    }

    public float getArmorDurability() {
        return this.entityData.get(ARMORDURABILITY);
    }

    public final float getMaxArmorDurability() {
        return (float)this.getAttributeValue(AttributeRegistry.ARMOR_DURABILITY.get());
    }

 */

    @Override
    public void tick() {
        super.tick();
        if(getTarget() instanceof GuerrillasEntity) setTarget(null);
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        Entity entity = source.getDirectEntity();
        if(entity instanceof GuerrillasEntity)
            return false;
        else
            return super.hurt(source, damage);
    }

    @Nullable
    public Vec3 findVillage(){
        if(!level().isClientSide) {
            ServerLevel serverlevel = (ServerLevel) this.level();
            BlockPos blockpos = this.blockPosition();
            SectionPos sectionpos = SectionPos.of(blockpos);
            SectionPos sectionpos1 = BehaviorUtils.findSectionClosestToVillage(serverlevel, sectionpos, 20);
            return sectionpos1 != sectionpos ? Vec3.atBottomCenterOf(sectionpos1.center()): null;
        }
        return null;
    }
}
