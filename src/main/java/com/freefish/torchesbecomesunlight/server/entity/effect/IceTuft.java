package com.freefish.torchesbecomesunlight.server.entity.effect;

import com.bobmowzie.mowziesmobs.client.particle.ParticleHandler;
import com.bobmowzie.mowziesmobs.client.particle.util.AdvancedParticleBase;
import com.bobmowzie.mowziesmobs.client.particle.util.ParticleComponent;
import com.freefish.torchesbecomesunlight.server.entity.ursus.Pursuer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.joml.Math;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class IceTuft extends EffectEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int number;

    private static final EntityDataAccessor<Integer> CLASS = SynchedEntityData.defineId(IceTuft.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TYPE = SynchedEntityData.defineId(IceTuft.class, EntityDataSerializers.INT);

    public IceTuft(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        number = random.nextInt(3);
    }

    public IceTuft(EntityType<?> pEntityType, Level pLevel,LivingEntity owner) {
        this(pEntityType, pLevel);
        caster = owner;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<IceTuft>(this, "Controller", 2, this::predicate));
    }

    private PlayState predicate(AnimationState<IceTuft> event) {
        if(number==0)
            event.getController().setAnimation(RawAnimation.begin().thenLoop("1"));
        else if(number==1)
            event.getController().setAnimation(RawAnimation.begin().thenLoop("2"));
        else if(number==2)
            event.getController().setAnimation(RawAnimation.begin().thenLoop("3"));
        return PlayState.CONTINUE;
    }

    @Override
    public void tick() {
        super.tick();
        if(level().isClientSide&&tickCount==79){
            sparkTuft();
        }
        if(!level().isClientSide&&tickCount>=80) {
            bomb();
            discard();
        }
    }

    private void bomb(){
        if(caster instanceof Pursuer){
            float damage =(float) caster.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            List<LivingEntity> nearByLivingEntities = getNearByLivingEntities(3);
            for(LivingEntity hit:nearByLivingEntities){
                if(hit == caster) continue;
                hit.hurt(caster.damageSources().mobAttack(caster),damage);
            }
        }
    }

    private void sparkTuft(){
        AdvancedParticleBase.spawnParticle(level(), ParticleHandler.RING_BIG.get(), getX(), getY() + 0.5, getZ(), 0, 0.01, 0, false, 0, Math.toRadians(-90), 0, 0, 50F, 1, 1, 1, 1, 1, 4, true, false, new ParticleComponent[]{
                new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, ParticleComponent.KeyTrack.startAndEnd(0f, 90f), false),
                new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, ParticleComponent.KeyTrack.startAndEnd(1f, 0.5f), false)
        });
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(CLASS,0);
        this.entityData.define(TYPE,0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        this.entityData.set(TYPE,pCompound.getInt("type"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putInt("type",this.entityData.get(TYPE));
    }

    public void setNumber(int number){
        this.entityData.set(CLASS,number);
    }

    public int getNumber(){
        return this.entityData.get(CLASS);
    }

    public void setTypeNumber(int number){
        this.entityData.set(TYPE,number);
    }

    public int getTypeNumber(){
        return this.entityData.get(TYPE);
    }
}
