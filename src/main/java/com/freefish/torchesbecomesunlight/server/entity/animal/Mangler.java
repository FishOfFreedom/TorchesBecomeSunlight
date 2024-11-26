package com.freefish.torchesbecomesunlight.server.entity.animal;

import com.freefish.torchesbecomesunlight.server.entity.AnimatedEntity;
import com.freefish.torchesbecomesunlight.server.entity.ai.FFBodyRotationControl;
import com.freefish.torchesbecomesunlight.server.entity.ai.FFPathNavigateGround;
import com.freefish.torchesbecomesunlight.server.entity.ai.entity.FollowLeaderGoal;
import com.freefish.torchesbecomesunlight.server.entity.ai.entity.ManglerAttackAI;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationAct;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class Mangler extends AnimatedEntity {
    public static final AnimationAct<Mangler> ATTACK = new AnimationAct<Mangler>("attack",12) {
        @Override
        public void tickUpdate(Mangler entity) {
            int tick = entity.getAnimationTick();
            LivingEntity target = entity.getTarget();
            float damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            entity.locateEntity();

            if (target != null) {
                entity.getLookControl().setLookAt(target);
                if (target.distanceTo(entity) <= 1.6 + target.getBbWidth() / 2) {
                    if (tick == 4) {
                        target.hurt(entity.damageSources().mobAttack(entity), damage);
                    }
                }
            }
        }
    };
    public static final AnimationAct<Mangler> ATTACK2 = new AnimationAct<Mangler>("attack2",12) {
        @Override
        public void tickUpdate(Mangler entity) {
            int tick = entity.getAnimationTick();
            LivingEntity target = entity.getTarget();
            float damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            entity.locateEntity();

            if (target != null) {
                entity.getLookControl().setLookAt(target);
                if (target.distanceTo(entity) <= 1.6 + target.getBbWidth() / 2) {
                    if (tick == 4) {
                        target.hurt(entity.damageSources().mobAttack(entity), damage);
                    }
                }
            }
        }
    };

    private static final EntityDataAccessor<Boolean> IS_LEADER = SynchedEntityData.defineId(Mangler.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<UUID>> LEADER = SynchedEntityData.defineId(Mangler.class, EntityDataSerializers.OPTIONAL_UUID);
    private Mangler leader;

    public Mangler(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    public Mangler(EntityType<? extends PathfinderMob> entityType, Level level,boolean isLead) {
        super(entityType, level);
        setIsLeader(isLead);
    }

    @Override
    public AnimationAct getDeathAnimation() {
        return null;
    }


    private static final AnimationAct[] ANIMATIONS = new AnimationAct[]{NO_ANIMATION,ATTACK,ATTACK2};
    @Override
    public AnimationAct[] getAnimations() {
        return ANIMATIONS;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2,new ManglerAttackAI(this));

        this.goalSelector.addGoal(3,new FollowLeaderGoal(this,0.27f,16,4));

        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this , 0.27));

        this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers(Mangler.class));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar event) {
        super.registerControllers(event);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AnimatedEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.ATTACK_DAMAGE, 10.0f)
                .add(Attributes.ARMOR, 8.0D)
                .add(Attributes.FOLLOW_RANGE, 48);
    }

    @Override
    protected BodyRotationControl createBodyControl() {
        return new FFBodyRotationControl(this);
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        return new FFPathNavigateGround(this,level());
    }

    @Override
    protected <T extends GeoEntity> void basicAnimation(AnimationState<T> event) {
        if(tickCount==1)
            event.getController().transitionLength(1);
        if(event.isMoving())
            event.setAnimation(RawAnimation.begin().thenLoop("walk2"));
        else
            event.setAnimation(RawAnimation.begin().thenLoop("idle"));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingData, @Nullable CompoundTag compound) {
        return super.finalizeSpawn(world, difficulty, reason, livingData, compound);
    }

    public void spawnHerd(){
        setIsLeader(true);
        int size = random.nextInt(2) + 2;
        float theta = (2 * (float) Math.PI / size);

        for (int i = 0; i <= size; i++) {
            Mangler tribeHunter = new Mangler(EntityHandle.MANGLER.get(), this.level(), false);
            tribeHunter.setPos(getX() + 0.1 * Mth.cos(theta * i), getY(), getZ() + 0.1 * Mth.sin(theta * i));
            tribeHunter.setLeader(Optional.of(getUUID()));
            if(!level().isClientSide)
                level().addFreshEntity(tribeHunter);
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_LEADER,false);
        this.entityData.define(LEADER,Optional.empty());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        setIsLeader(pCompound.getBoolean("is_leader"));
        setLeader(Optional.of(pCompound.getUUID("is_leader")));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("is_leader",isLeader());
        pCompound.putUUID("is_leader",getLeader().get());
    }

    public boolean isLeader(){
        return this.entityData.get(IS_LEADER);
    }

    public void setIsLeader(boolean isLeader){
        this.entityData.set(IS_LEADER,isLeader);
    }

    public Optional<UUID> getLeader(){
        return this.entityData.get(LEADER);
    }

    public Mangler leader(){
        if(leader!=null)
            return leader;
        else if(!getLeader().isEmpty()&&!level().isClientSide){
            leader = (Mangler) ((ServerLevel)level()).getEntity(getLeader().get());
            return leader;
        }else {
            return null;
        }
    }

    public void setLeader(Optional<UUID> isLeader){
        this.entityData.set(LEADER,isLeader);
    }

}
