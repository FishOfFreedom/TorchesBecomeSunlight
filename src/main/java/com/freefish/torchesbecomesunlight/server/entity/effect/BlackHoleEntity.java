package com.freefish.torchesbecomesunlight.server.entity.effect;

import com.freefish.torchesbecomesunlight.server.init.ParticleHandler;
import com.freefish.torchesbecomesunlight.client.util.particle.util.AdvancedParticleBase;
import com.freefish.torchesbecomesunlight.client.util.particle.util.ParticleComponent;
import com.freefish.torchesbecomesunlight.client.util.particle.util.RibbonComponent;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class BlackHoleEntity extends Entity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    @Nullable
    private UUID ownerUUID;
    @Nullable
    private Entity owner;

    public BlackHoleEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public BlackHoleEntity(Level pLevel, LivingEntity livingEntity) {
        super(EntityHandle.BLACKHE.get(), pLevel);
        setOwner(livingEntity);
    }

    @Override
    public void tick() {
        super.tick();
        if(level().isClientSide){
            if(tickCount==1){
                Vec3 vec3 = position();
                //level().addParticle(new BlackHoleParticle.BlackHoleData(75,3.5f),vec3.x,vec3.y,vec3.z,0,0,0);
            }
        }
        if(tickCount<=75) {
            linkEntity();
            addWhirlFX();
        }
        else {
            if(!level().isClientSide)
                kill();
        }
    }

    private void linkEntity(){
        List<LivingEntity> list = level().getEntitiesOfClass(LivingEntity.class,getBoundingBox().inflate(13),livingEntity ->
                livingEntity != owner&&livingEntity.distanceTo(this)<11);
        for(LivingEntity livingEntity:list){
            if(livingEntity instanceof Player player&&player.isCreative()) {
                    continue;
            }
            float s = 0;
            if(livingEntity instanceof Player player) {
                s = 0.26f;
            }

            float scale = 1;
            double length = livingEntity.distanceTo(this);
            if(length>3.0){
                scale = (float) (1 - (length - 3) / 8);
            }
            Vec3 move = new Vec3(getX()-livingEntity.getX(),0,getZ()-livingEntity.getZ()).normalize().scale((0.3-s)*scale);
            livingEntity.setDeltaMovement(getDeltaMovement().add(move));
        }
    }

    @Override
    protected void defineSynchedData() {
    }

    protected void addAdditionalSaveData(CompoundTag pCompound) {
        if (this.ownerUUID != null) {
            pCompound.putUUID("Owner", this.ownerUUID);
        }
    }

    protected void readAdditionalSaveData(CompoundTag pCompound) {
        if (pCompound.hasUUID("Owner")) {
            this.ownerUUID = pCompound.getUUID("Owner");
            this.owner = null;
        }
    }

    public void setOwner(@Nullable Entity pOwner) {
        if (pOwner != null) {
            this.ownerUUID = pOwner.getUUID();
            this.owner = pOwner;
        }

    }

    @Nullable
    public Entity getOwner() {
        if (this.owner != null && !this.owner.isRemoved()) {
            return this.owner;
        } else if (this.ownerUUID != null && this.level() instanceof ServerLevel) {
            this.owner = ((ServerLevel)this.level()).getEntity(this.ownerUUID);
            return this.owner;
        } else {
            return null;
        }
    }

    private void addWhirlFX(){
        if(tickCount==1)
            EntityCameraShake.cameraShake(level(), position(), 30, 0.1f, 40, 30);
        if (level().isClientSide&&tickCount%5==0) {
            float scale = 8.2f;
            ParticleComponent.KeyTrack keyTrack = new ParticleComponent.KeyTrack(new float[]{0, 0.2f, 0.2f, 0}, new float[]{0, 0.3f, 0.7f, 1});
            for (int i = 0; i < 2; i++) {
                float phaseOffset = random.nextFloat();
                AdvancedParticleBase.spawnParticle(level(), ParticleHandler.PIXEL.get(), getX(), getY(), getZ(), 0, 0, 0, false, 0, 0, 0, 0, 8F, 0, 0, 0, 1, 1, 30+random.nextInt(11), true, true, new ParticleComponent[]{
                        new ParticleComponent.Orbit(new Vec3[]{position().add(0, getBbHeight() / 2, 0)}, ParticleComponent.KeyTrack.startAndEnd(0 + phaseOffset, 1.6f + phaseOffset), new ParticleComponent.KeyTrack(
                                new float[]{1f * scale,0.998f * scale, 0.974f * scale, 0.87f * scale, 0.53f * scale, 0 },
                                new float[]{ 0, 0.15f,0.3f, 0.45f,0.6f,0.75f }
                        ), ParticleComponent.KeyTrack.startAndEnd(random.nextFloat() * 2 - 1, random.nextFloat() * 2 - 1), new ParticleComponent.Constant(1.55f), new ParticleComponent.Constant(0), false),
                        new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, keyTrack, false),
                        new RibbonComponent(ParticleHandler.RIBBON_FLAT.get(), 10, 0, 0, 0, 0.2F, 0,0,0, 1, true, true, new ParticleComponent[]{
                                new RibbonComponent.PropertyOverLength(RibbonComponent.PropertyOverLength.EnumRibbonProperty.SCALE, ParticleComponent.KeyTrack.startAndEnd(1, 0)),
                                new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, keyTrack, false)
                        }),
                        new ParticleComponent.FaceMotion()
                });
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<BlackHoleEntity>(this, "Controller", 2, this::predicate));
    }

    private PlayState predicate(AnimationState<BlackHoleEntity> event) {
        event.getController().setAnimation(RawAnimation.begin().thenLoop("a"));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
