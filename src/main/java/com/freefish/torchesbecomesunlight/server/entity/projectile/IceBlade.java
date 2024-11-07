package com.freefish.torchesbecomesunlight.server.entity.projectile;

import com.bobmowzie.mowziesmobs.client.particle.ParticleHandler;
import com.bobmowzie.mowziesmobs.client.particle.util.AdvancedParticleBase;
import com.bobmowzie.mowziesmobs.client.particle.util.ParticleComponent;
import com.freefish.torchesbecomesunlight.server.capability.frozen.FrozenCapabilityProvider;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster.SnowNova;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

public class IceBlade extends Projectile implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final int MAX_ACTIVE = 35;

    public IceBlade(EntityType<? extends IceBlade> entityType, Level level) {
        super(entityType, level);
    }

    public IceBlade(Level level, LivingEntity caster) {
        super(EntityHandle.ICE_BLADE.get(), level);
        setOwner(caster);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public void tick() {
        super.tick();
        Entity entity = this.getOwner();
        if (this.level().isClientSide || (entity == null || !entity.isRemoved()) && this.level().hasChunkAt(this.blockPosition())) {
            this.move(MoverType.SELF, this.getDeltaMovement());
            HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hitresult.getType() != HitResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult)) {
                this.onHit(hitresult);
            }
            this.checkInsideBlocks();
            if (this.tickCount >= MAX_ACTIVE) {
                this.discard();
            }
        } else {
            this.discard();
        }
        spawnRing();
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        hit(hitResult.getEntity());
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        hit(null);
        kill();
    }

    private void hit(@Nullable Entity entity){
        if(entity instanceof LivingEntity livingEntity){
            Entity owner = getOwner();
            if(owner instanceof SnowNova snowNova){
                livingEntity.getCapability(FrozenCapabilityProvider.FROZEN_CAPABILITY).ifPresent(data ->{
                    float damage = (float) snowNova.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * 1.5f;
                    if (data.isFrozen) {
                        data.clearFrozen(livingEntity);
                        damage *= 2;
                    }
                    livingEntity.hurt(damageSources().mobAttack(snowNova), damage);
                });
            }
        }
    }

    @Override
    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    @Override
    public boolean isInWater() {
        return false;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (this.level().isClientSide) {
            return false;
        } else {
            this.markHurt();
            Entity entity = source.getEntity();
            if (entity != null) {

                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public void spawnRing(){
        if(level().isClientSide){
            if(tickCount%2==0){
                int i1 = 2 + random.nextInt(2);
                for(int i = 0 ;i<i1;i++) {
                    Vec3 vec3 = getDeltaMovement().scale(-1).yRot(0.15f - random.nextFloat() * 0.3f);
                    AdvancedParticleBase.spawnParticle(level(), ParticleHandler.SAN.get(), getX(), getY(), getZ(), vec3.x, 0, vec3.z, true, 0, 0, 0, 0, 1.5F, 1, 1, 1, 0.75, 1, 40+random.nextInt(21), true, false, new ParticleComponent[]{
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, ParticleComponent.KeyTrack.startAndEnd(1f, 0f), false)
                    });
                }
            }
        }
    }
}
