package com.freefish.torchesbecomesunlight.server.entity.animal;

import com.freefish.torchesbecomesunlight.server.entity.AnimatedEntity;
import com.freefish.torchesbecomesunlight.server.entity.ai.FFBodyRotationControl;
import com.freefish.torchesbecomesunlight.server.entity.ai.FFPathNavigateGround;
import com.freefish.torchesbecomesunlight.server.entity.ai.entity.FollowLeaderGoal;
import com.freefish.torchesbecomesunlight.server.entity.ai.entity.ManglerAttackAI;
import com.freefish.torchesbecomesunlight.server.entity.ai.entity.ManglerHaQiAttackAI;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationAct;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
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
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
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
                if (target.distanceTo(entity) <= 2 + target.getBbWidth() / 2) {
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
                if (target.distanceTo(entity) <= 2 + target.getBbWidth() / 2) {
                    if (tick == 4) {
                        target.hurt(entity.damageSources().mobAttack(entity), damage);
                    }
                }
            }
        }
    };
    public static final AnimationAct<Mangler> ATTACKWALK = new AnimationAct<Mangler>("attackwalk",80) {
        @Override
        public void tickUpdate(Mangler entity) {
            int tick = entity.getAnimationTick();
            LivingEntity target = entity.getTarget();
            float damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            entity.locateEntity();

            if (target != null) {
                entity.getLookControl().setLookAt(target);
                if (target.distanceTo(entity) <= 2 + target.getBbWidth() / 2) {
                    if (tick == 4) {
                        target.hurt(entity.damageSources().mobAttack(entity), damage);
                    }
                }
            }
        }
    };
    public static final AnimationAct<Mangler> DASHATTACK = new AnimationAct<Mangler>("dashattack",25) {
        @Override
        public void tickUpdate(Mangler entity) {
            int tick = entity.getAnimationTick();
            LivingEntity target = entity.getTarget();
            float damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();

            if (target != null) {
                entity.getLookControl().setLookAt(target);
                if(tick==7){
                    entity.dashForward(8,0);
                    entity.setDeltaMovement(entity.getDeltaMovement().add(0,0.3,0));
                }
                if (target.distanceTo(entity) <= 2 + target.getBbWidth() / 2) {
                    if (tick == 10) {
                        target.hurt(entity.damageSources().mobAttack(entity), damage*2);
                        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,60,1));
                    }
                }
            }
        }

        @Override
        public void stop(Mangler entity) {
            entity.setAngryLevel(2);
            super.stop(entity);
        }
    };
    public static final AnimationAct<Mangler> IDLE_1 = new AnimationAct<Mangler>("idle_1",80) {
        @Override
        public void tickUpdate(Mangler entity) {
            int tick = entity.getAnimationTick();
            LivingEntity target = entity.getTarget();
            float damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            entity.locateEntity();

            if (target != null) {
                entity.getLookControl().setLookAt(target);
                if (target.distanceTo(entity) <= 2 + target.getBbWidth() / 2) {
                    if (tick == 4) {
                        target.hurt(entity.damageSources().mobAttack(entity), damage);
                    }
                }
            }
        }
    };

    private static final EntityDataAccessor<Boolean> IS_LEADER = SynchedEntityData.defineId(Mangler.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<UUID>> LEADER = SynchedEntityData.defineId(Mangler.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Boolean> RUN = SynchedEntityData.defineId(Burdenbeast.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ANGRY_LEVEL = SynchedEntityData.defineId(Burdenbeast.class, EntityDataSerializers.INT);

    private Mangler leader;
    public boolean isAttacked;
    private List<Mangler> followers = new ArrayList<>();
    private AttributeModifier attributeModifier = new AttributeModifier(UUID.fromString("f2ccfb2b-cb0b-4a7d-9c64-9fa53f7687a2"), "Health config multiplier", 0.8, AttributeModifier.Operation.ADDITION);

    public Mangler(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        super.tick();
        if(!level().isClientSide&&(tickCount%10)==0){
            if(isLeader()){
                for(Mangler mangler:followers){
                    LivingEntity target = mangler.getTarget();
                    if(target==null||!target.isAlive()){
                        mangler.setTarget(getTarget());
                    }
                }
            }else {
                Mangler leader1 = leader();
                if(leader1!=null&&leader1.isAlive()){
                    LivingEntity target = leader1.getTarget();
                    if(target==null||!target.isAlive()){
                        LivingEntity target1 = getTarget();
                        if(target1!=null&&target1.isAlive()){
                            leader1.setTarget(target1);
                        }
                    }
                }
            }
            if(getTarget()==null||!getTarget().isAlive()){
                setRun(false);
            }
        }
    }

    @Override
    public AnimationAct getDeathAnimation() {
        return null;
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        if(leader()==null){
            isAttacked = false;
        }
        return super.hurt(source, damage);
    }

    private static final AnimationAct[] ANIMATIONS = new AnimationAct[]{NO_ANIMATION,ATTACK,ATTACK2,ATTACKWALK,DASHATTACK,IDLE_1};
    @Override
    public AnimationAct[] getAnimations() {
        return ANIMATIONS;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1,new ManglerHaQiAttackAI(this));
        this.goalSelector.addGoal(2,new ManglerAttackAI(this));

        this.goalSelector.addGoal(3,new FollowLeaderGoal(this,0.27f,8,4));

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
        return AnimatedEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.ATTACK_DAMAGE, 8.0f)
                .add(Attributes.ARMOR, 6.0D)
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
        int angryLevel = isAngryLevel();
        if(event.isMoving()) {
            if(getRun()){
                event.setAnimation(RawAnimation.begin().thenLoop("run"));
            }
            else {
                if(angryLevel==1)
                    event.setAnimation(RawAnimation.begin().thenLoop("attackWalk"));
                else
                    event.setAnimation(RawAnimation.begin().thenLoop("walk2"));
            }
        }
        else {
            if(angryLevel==1)
                event.setAnimation(RawAnimation.begin().thenLoop("attackWalkidle"));
            else
                event.setAnimation(RawAnimation.begin().thenLoop("idle"));
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingData, @Nullable CompoundTag compound) {
        if(reason == MobSpawnType.NATURAL){
            spawnHerd();
        }
        return super.finalizeSpawn(world, difficulty, reason, livingData, compound);
    }

    public void spawnHerd(){
        setIsLeader(true);

        if(!level().isClientSide){
            AttributeInstance maxHealthAttr = getAttribute(Attributes.MAX_HEALTH);
            if (maxHealthAttr != null) {
                double difference = maxHealthAttr.getBaseValue();
                maxHealthAttr.addTransientModifier(new AttributeModifier("Health config multiplier", difference, AttributeModifier.Operation.ADDITION));
                this.setHealth(this.getMaxHealth());
            }

            AttributeInstance attackDamageAttr = getAttribute(Attributes.ATTACK_DAMAGE);
            if (attackDamageAttr != null) {
                double difference = attackDamageAttr.getBaseValue() * 0.5f;
                attackDamageAttr.addTransientModifier(new AttributeModifier("Attack config multiplier", difference, AttributeModifier.Operation.ADDITION));
            }
        }

        int size = random.nextInt(2) + 2;
        float theta = (2 * (float) Math.PI / size);

        for (int i = 0; i <= size; i++) {
            Mangler tribeHunter = new Mangler(EntityHandle.MANGLER.get(), this.level());
            tribeHunter.setPos(getX() + 0.1 * Mth.cos(theta * i), getY(), getZ() + 0.1 * Mth.sin(theta * i));
            tribeHunter.setLeader(this);
            followers.add(tribeHunter);
            if(!level().isClientSide)
                level().addFreshEntity(tribeHunter);
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_LEADER,false);
        this.entityData.define(LEADER,Optional.empty());
        this.entityData.define(RUN,false);
        this.entityData.define(ANGRY_LEVEL,0);
    }

    @Override
    public float getStepHeight() {
        boolean run1 = getRun();
        if(run1){
            return 1.2f;
        }
        return super.getStepHeight();
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        setIsLeader(pCompound.getBoolean("is_leader"));

        if(isLeader()){
            AttributeInstance maxHealthAttr = getAttribute(Attributes.MAX_HEALTH);
            if (maxHealthAttr != null) {
                double difference = maxHealthAttr.getBaseValue();
                maxHealthAttr.addTransientModifier(new AttributeModifier("Health config multiplier", difference, AttributeModifier.Operation.ADDITION));
                this.setHealth(this.getMaxHealth());
            }

            AttributeInstance attackDamageAttr = getAttribute(Attributes.ATTACK_DAMAGE);
            if (attackDamageAttr != null) {
                double difference = attackDamageAttr.getBaseValue() * 0.5f;
                attackDamageAttr.addTransientModifier(new AttributeModifier("Attack config multiplier", difference, AttributeModifier.Operation.ADDITION));
            }
        }

        if(pCompound.hasUUID("is_leader")){
            setLeader(Optional.of(pCompound.getUUID("is_leader")));
        }
        setAngryLevel(pCompound.getInt("angry"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("is_leader",isLeader());
        getLeader().ifPresent((uuid1 -> pCompound.putUUID("is_leader", uuid1)));
        pCompound.putInt("angry",isAngryLevel());
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling();
    }

    public int isAngryLevel(){
        return this.entityData.get(ANGRY_LEVEL);
    }

    public void setAngryLevel(int isLeader){
        this.entityData.set(ANGRY_LEVEL,isLeader);
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
            if(!leader.isAlive()){
                return null;
            }
            else
                return leader;
        else if(!getLeader().isEmpty()&&!level().isClientSide){
            leader = (Mangler) ((ServerLevel)level()).getEntity(getLeader().get());
            if(leader!=null&&!leader.followers.contains(this)){
                leader.followers.add(this);
            }
            return leader;
        }else {
            return null;
        }
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

    public void setLeader(Optional<UUID> isLeader){
        this.entityData.set(LEADER,isLeader);
    }

    public void setLeader(Mangler mangler){
        this.entityData.set(LEADER,Optional.of(mangler.getUUID()));
        this.leader = mangler;
    }

}
