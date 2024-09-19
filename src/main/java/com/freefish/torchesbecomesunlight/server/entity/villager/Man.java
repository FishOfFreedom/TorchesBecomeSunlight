package com.freefish.torchesbecomesunlight.server.entity.villager;

import com.freefish.torchesbecomesunlight.server.capability.story.PlayerStoryStone;
import com.freefish.torchesbecomesunlight.server.capability.story.PlayerStoryStoneProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Man extends PathfinderMob implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public static final RawAnimation WALK_ = RawAnimation.begin().then("walk", Animation.LoopType.LOOP);
    public static final RawAnimation IDLE_ = RawAnimation.begin().then("idle", Animation.LoopType.LOOP);
    public static final RawAnimation TALK_ = RawAnimation.begin().then("talk", Animation.LoopType.LOOP);

    private static final EntityDataAccessor<Boolean> IS_TALKING = SynchedEntityData.defineId(Man.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> NUMBER = SynchedEntityData.defineId(Man.class, EntityDataSerializers.INT);

    int frame;

    @Override
    public void tick() {
        super.tick();
        frame++;
    }

    public Man(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<Man>(this, "Controller", 3, this::predicate));
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        return super.hurt(pSource, pAmount);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new RandomStrollGoal(this , 1F));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        setNumber(random.nextInt(3));
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    private PlayState predicate(AnimationState<Man> event) {
        if(getIsTalking())
            event.setAnimation(TALK_);
        else {
            if(event.isMoving())
                event.setAnimation(WALK_);
            else event.setAnimation(IDLE_);
        }
        return PlayState.CONTINUE;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25f);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_TALKING,false);
        this.entityData.define(NUMBER,0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("istalking",getIsTalking());
        pCompound.putInt("number",getNumber());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        setIsTalking(pCompound.getBoolean("istalking"));
        setNumber(pCompound.getInt("number"));
    }

    public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
        return false;
    }

    public boolean getIsTalking(){
        return this.entityData.get(IS_TALKING);
    }

    public void setIsTalking(boolean isTalking){
        this.entityData.set(IS_TALKING,isTalking);
    }

    public int getNumber(){
        return this.entityData.get(NUMBER);
    }

    public void setNumber(int number){
        this.entityData.set(NUMBER,number);
    }

}
