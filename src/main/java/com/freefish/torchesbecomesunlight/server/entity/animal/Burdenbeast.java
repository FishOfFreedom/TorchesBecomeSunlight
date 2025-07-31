package com.freefish.torchesbecomesunlight.server.entity.animal;

import com.freefish.torchesbecomesunlight.server.entity.AnimatedEntity;
import com.freefish.torchesbecomesunlight.server.entity.ai.FFBodyRotationControl;
import com.freefish.torchesbecomesunlight.server.entity.ai.FFPathNavigateGround;
import com.freefish.torchesbecomesunlight.server.entity.ai.entity.BurdenbeastAttackAI;
import com.freefish.torchesbecomesunlight.server.entity.effect.StompEntity;
import com.freefish.torchesbecomesunlight.server.init.SoundHandle;
import com.freefish.torchesbecomesunlight.server.util.FFEntityUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.HitResult;
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

public class Burdenbeast extends AbstractChestedHorse implements GeoEntity{
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final EntityDataAccessor<CompoundTag> ANIMATION = SynchedEntityData.defineId(Burdenbeast.class, EntityDataSerializers.COMPOUND_TAG);
    private static final EntityDataAccessor<Boolean> RUN = SynchedEntityData.defineId(Burdenbeast.class, EntityDataSerializers.BOOLEAN);

    private AttributeModifier attributeModifier = new AttributeModifier(UUID.fromString("f2ccfb2b-cb0b-4a7d-9c64-9fa53f7687a2"), "Health config multiplier", 0.8, AttributeModifier.Operation.ADDITION);
    public int animationType;
    private int animationTime;

    public Burdenbeast(EntityType<? extends AbstractChestedHorse> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        //this.goalSelector.addGoal(1, new PanicGoal(this, 1.2D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D, AbstractHorse.class));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.0D));
        if (this.canPerformRearing()) {
            this.goalSelector.addGoal(9, new RandomStandGoal(this));
        }

        this.addBehaviourGoals();
        this.goalSelector.addGoal(1,new BurdenbeastAttackAI(this));

        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this , 0.27));

        this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers(Burdenbeast.class));
    }

    @Override
    public void tick() {
        super.tick();
        if(animationType==1){
            int tick = 35 - animationTime;
            if(!level().isClientSide){
                locateEntity();
                if(tick==24){
                    float damage = (float) getAttributeValue(Attributes.ATTACK_DAMAGE);

                    List<LivingEntity> entitiesOfClass = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(6), (e) ->
                            e.distanceTo(this) < 6);

                    for(LivingEntity living:entitiesOfClass){
                        if(living == this) continue;
                        living.hurt(damageSources().mobAttack(this),damage);
                    }

                    StompEntity stompEntity = new StompEntity(this.level(),8,this,3);
                    stompEntity.setPos(this.position().add(new Vec3(0, -0.5, 2).yRot((float) (-this.yBodyRot / 180 * Math.PI))));
                    this.level().addFreshEntity(stompEntity);
                    this.playSound(SoundHandle.GROUND.get(), 1.3F, 1.0F / (this.random.nextFloat() * 0.4F + 0.8F));

                }

                if(tick==34) setRun(false);
            }
        }else if(animationType==2){
            int tick = 31 - animationTime;
            if(!level().isClientSide){
                LivingEntity target = getTarget();
                if(tick<10&&target!=null){
                    locateEntity();
                    getLookControl().setLookAt(target);
                }
                if(tick==14){
                    doRangeKnockBack(6,60,1);
                    Vec3 vec3 = new Vec3(0, 0.1, 1).yRot((float) (-this.yBodyRot / 180 * Math.PI));
                    setDeltaMovement(getDeltaMovement().add(vec3));
                }
                if(tick==30) setRun(false);
            }
        }else if(animationType==3){
            int tick = 20 - animationTime;
            if(!level().isClientSide){
                locateEntity();
                if(tick==19) setAnimation(4,100);
            }
        }else if(animationType==4){
            int tick = 100 - animationTime;
            if(!level().isClientSide){
                locateEntity();
                if(tick==99){
                    setAnimation(5,30);
                }
            }
        }
        if(!level().isClientSide){
            boolean run1 = getRun();
            if(run1){
                Vec3 vec32 = this.position();
                Vec3 vec33 = FFEntityUtils.getBodyRotVec(this,new Vec3(0,1.5,3));
                HitResult hitresult = this.level().clip(new ClipContext(vec32, vec33, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
                if (hitresult.getType() != HitResult.Type.MISS) {
                    setRun(false);
                    setAnimation(3,20);
                }
            }
        }

        if(animationType!=0&&animationTime>0){
            animationTime--;
            if(animationTime==0){
                animationType = 0;
            }
        }

    }

    @Override
    public int getHeadRotSpeed() {
        if(getRun()){
            return 1;
        }
        return super.getHeadRotSpeed();
    }

    @Override
    public int getMaxHeadXRot() {
        return super.getMaxHeadXRot();
    }

    @Override
    protected BodyRotationControl createBodyControl() {
        return new FFBodyRotationControl(this);
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        return new FFPathNavigateGround(this,level());
    }

    public void doRangeKnockBack(double range, double arc,float knockback){
        float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        List<LivingEntity> entitiesHit = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(range+5, 3, range+5), e -> e != this && distanceTo(e) <= range + e.getBbWidth() / 2f && e.getY() <= getY() + 3);
        for (LivingEntity entityHit : entitiesHit) {
            float entityHitAngle = (float) ((Math.atan2(entityHit.getZ() - getZ(), entityHit.getX() - getX()) * (180 / Math.PI) - 90) % 360);
            float entityAttackingAngle = getYRot() % 360;
            if (entityHitAngle < 0) {
                entityHitAngle += 360;
            }
            if (entityAttackingAngle < 0) {
                entityAttackingAngle += 360;
            }
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            float entityHitDistance = (float) Math.sqrt((entityHit.getZ() - getZ()) * (entityHit.getZ() - getZ()) + (entityHit.getX() - getX()) * (entityHit.getX() - getX())) - entityHit.getBbWidth() / 2f;
            if (entityHitDistance <= range && (entityRelativeAngle <= arc / 2 && entityRelativeAngle >= -arc / 2) || (entityRelativeAngle >= 360 - arc / 2 || entityRelativeAngle <= -360 + arc / 2)) {
                Vec3 direction = new Vec3(0, knockback*0.1, knockback).yRot((float) ((-getYRot()) / 180 * org.joml.Math.PI));
                entityHit.setDeltaMovement(entityHit.getDeltaMovement().x + direction.x, entityHit.getDeltaMovement().y+ direction.y, entityHit.getDeltaMovement().z + direction.z);
                entityHit.hurt(damageSources().mobAttack(this),damage);
                entityHit.move(MoverType.SELF,entityHit.getDeltaMovement());
            }
        }
    }

    private void locateEntity(){
        if(onGround()){
            setDeltaMovement(0,0,0);
        }else {
            Vec3 deltaMovement = getDeltaMovement();
            setDeltaMovement(0,deltaMovement.y,0);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar event) {
        event.add(animationController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public float getStepHeight() {
        if(getRun()){
            return 1.6f;
        }
        return super.getStepHeight();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AnimatedEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 160.0D)
                .add(Attributes.ATTACK_DAMAGE, 20.0f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1)
                .add(Attributes.ARMOR, 8.0D)
                .add(Attributes.JUMP_STRENGTH)
                .add(Attributes.FOLLOW_RANGE, 48);
    }

    @Override
    protected void randomizeAttributes(RandomSource pRandom) {

    }

    private final AnimationController<Burdenbeast> animationController = new AnimationController<Burdenbeast>(this, "Controller", 5, this::predicate);

    protected  <T extends GeoEntity> PlayState predicate(AnimationState<T> animationState) {
        basicAnimation(animationState);
        return PlayState.CONTINUE;
    }

    protected <T extends GeoEntity> void basicAnimation(AnimationState<T> event) {
        if(animationType==0){
            if (event.isMoving()) {
                if(!getRun()){
                    event.setAnimation(RawAnimation.begin().thenLoop("walk"));
                }else {
                    event.setAnimation(RawAnimation.begin().thenLoop("run"));
                }
            }
            else
                event.setAnimation(RawAnimation.begin().thenLoop("idle"));
        }else {
            switch (animationType){
                case 1 -> event.setAnimation(RawAnimation.begin().thenLoop("stone"));
                case 2 -> event.setAnimation(RawAnimation.begin().thenLoop("attack_1"));
                case 3 -> event.setAnimation(RawAnimation.begin().thenLoop("weak"));
                case 4 -> event.setAnimation(RawAnimation.begin().thenLoop("weak_idle"));
                case 5 -> event.setAnimation(RawAnimation.begin().thenLoop("weak_end"));
            }
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingData, @Nullable CompoundTag compound) {
        return super.finalizeSpawn(world, difficulty, reason, livingData, compound);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIMATION,new CompoundTag());
        this.entityData.define(RUN,false);
    }

    public void setRun(boolean run){
        if(run == getRun()) return;
        if(run){
            AttributeInstance maxHealthAttr = getAttribute(Attributes.MOVEMENT_SPEED);
            if (maxHealthAttr != null) {
                maxHealthAttr.addPermanentModifier(attributeModifier);
            }
        }else {
            AttributeInstance maxHealthAttr = getAttribute(Attributes.MOVEMENT_SPEED);
            if (maxHealthAttr != null) {
                maxHealthAttr.removeModifier(attributeModifier);
            }
        }
        this.entityData.set(RUN,run);
    }

    public boolean getRun(){
        return this.entityData.get(RUN);
    }

    public void setAnimation(int type,int time ){
        if(animationType==-1) return;

        animationType = type;animationTime = time;
        CompoundTag a = new CompoundTag();
        a.putInt("type",type);
        a.putInt("time",time);
        this.entityData.set(ANIMATION,a,true);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        if(pKey.equals(ANIMATION)){
            CompoundTag compoundTag = this.entityData.get(ANIMATION);
            int type = compoundTag.getInt("type");
            int time = compoundTag.getInt("time");
            animationType = type;animationTime = time;
        }
        super.onSyncedDataUpdated(pKey);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.entityData.set(RUN,pCompound.getBoolean("run"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("run",getRun());
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.DONKEY_AMBIENT;
    }

    protected SoundEvent getAngrySound() {
        return SoundEvents.DONKEY_ANGRY;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.DONKEY_DEATH;
    }

    @Nullable
    protected SoundEvent getEatingSound() {
        return SoundEvents.DONKEY_EAT;
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.DONKEY_HURT;
    }

    @Nullable
    public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        EntityType<? extends AbstractHorse> entitytype = pOtherParent instanceof Horse ? EntityType.MULE : EntityType.DONKEY;
        AbstractHorse abstracthorse = entitytype.create(pLevel);
        if (abstracthorse != null) {
            this.setOffspringAttributes(pOtherParent, abstracthorse);
        }

        return abstracthorse;
    }

    //@Override
    //public int getInventoryColumns() {
    //    return 20;
    //}
}
