package com.freefish.torchesbecomesunlight.server.entity.effect;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class IceTuft extends Entity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int number;

    private static final EntityDataAccessor<Integer> CLASS = SynchedEntityData.defineId(IceTuft.class, EntityDataSerializers.INT);

    public IceTuft(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        number = random.nextInt(3);
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
        if(tickCount>=100) kill();
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(CLASS,0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {

    }

    public void setNumber(int number){
        this.entityData.set(CLASS,number);
    }

    public int getNumber(){
        return this.entityData.get(CLASS);
    }
}
