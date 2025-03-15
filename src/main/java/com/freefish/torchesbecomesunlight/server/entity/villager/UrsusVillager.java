package com.freefish.torchesbecomesunlight.server.entity.villager;

import com.freefish.torchesbecomesunlight.server.entity.AnimatedEntity;
import com.freefish.torchesbecomesunlight.server.entity.ai.FFBodyRotationControl;
import com.freefish.torchesbecomesunlight.server.entity.ai.FFPathNavigateGround;
import com.freefish.torchesbecomesunlight.server.entity.villager.villager.UrsusVillagerGoalPackages;
import com.freefish.torchesbecomesunlight.server.entity.effect.dialogueentity.IDialogue;
import com.freefish.torchesbecomesunlight.server.init.village.MemoryModuleTypeHandle;
import com.freefish.torchesbecomesunlight.server.init.village.SensorTypeHandle;
import com.freefish.torchesbecomesunlight.server.story.dialogue.Dialogue;
import com.freefish.torchesbecomesunlight.server.story.dialogue.DialogueStore;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationAct;
import com.freefish.torchesbecomesunlight.server.world.gen.biome.ModBiomes;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.*;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;

public abstract class UrsusVillager extends AnimatedEntity implements IDialogue {
    private static final EntityDataAccessor<Integer> BODY = SynchedEntityData.defineId(UrsusVillager.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> HEAD = SynchedEntityData.defineId(UrsusVillager.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_ARMOR = SynchedEntityData.defineId(UrsusVillager.class, EntityDataSerializers.BOOLEAN);
    private int body_type = -1;
    private int head_type = -1;

    private static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.HOME,MemoryModuleType.WALK_TARGET,MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,MemoryModuleType.PATH,
            MemoryModuleType.LOOK_TARGET,MemoryModuleType.ATTACK_TARGET,MemoryModuleType.NEAREST_LIVING_ENTITIES,MemoryModuleType.DOORS_TO_CLOSE,MemoryModuleType.MEETING_POINT,
            MemoryModuleTypeHandle.ARMOR_STAND_POS.get(),MemoryModuleTypeHandle.BEHAVIOR_RUN_ONE.get());
    private static final ImmutableList<SensorType<? extends Sensor<? super UrsusVillager>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorTypeHandle.FIND_ARMOR_STAND_SENSOR.get());
    public static final Map<MemoryModuleType<GlobalPos>, BiPredicate<UrsusVillager, Holder<PoiType>>> POI_MEMORIES = ImmutableMap.of(MemoryModuleType.HOME, (ursusVillager, poiType) -> poiType.is(PoiTypes.HOME));

    public static final RawAnimation WALK_ = RawAnimation.begin().then("walk", Animation.LoopType.LOOP);
    public static final RawAnimation IDLE_ = RawAnimation.begin().then("idle", Animation.LoopType.LOOP);
    private LivingEntity dialogueLivingEntity;

    private Activity activity = null;

    public static final AnimationAct<UrsusVillager> ATTACK = new AnimationAct<UrsusVillager>("attack_4",30) {
        @Override
        public void tickUpdate(UrsusVillager entity) {
            int tick = entity.getAnimationTick();
            LivingEntity target = entity.getTarget();
            float damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            entity.locateEntity();

            if (target != null) {
                entity.getLookControl().setLookAt(target);
                if (target.distanceTo(entity) <= 1.6 + target.getBbWidth() / 2) {
                    if (tick == 4) {
                        //target.hurt(entity.damageSources().mobAttack(entity), damage);
                        ItemStack mainHandItem = target.getMainHandItem();
                        Item item = mainHandItem.getItem();
                        item.hurtEnemy(mainHandItem,target,entity);
                        //target.hurt(entity.damageSources().mobAttack(entity), damage);
                    }
                }
            }
        }
    };

    public static final AnimationAct<UrsusVillager> ATTACK1 = new AnimationAct<UrsusVillager>("attack_3",26) {
        @Override
        public void tickUpdate(UrsusVillager entity) {
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
    public static final AnimationAct<UrsusVillager> ARMOR_UP = new AnimationAct<UrsusVillager>("armor_up",25) {
        @Override
        public void tickUpdate(UrsusVillager entity) {
            int tick = entity.getAnimationTick();
            entity.locateEntity();
            if(tick==1){
                entity.setIsArmor(true);
            }
        }
    };
    public static final AnimationAct<UrsusVillager> ARMOR_DOWN = new AnimationAct<UrsusVillager>("armor_down",25) {
        @Override
        public void tickUpdate(UrsusVillager entity) {
            int tick = entity.getAnimationTick();
            entity.locateEntity();
            if(tick==1){
                entity.setIsArmor(false);
            }
        }
    };

    private static final AnimationAct[] ANIMATIONACTS = new AnimationAct[]{NO_ANIMATION,ATTACK,ATTACK1,ARMOR_UP,ARMOR_DOWN};

    @Override
    public AnimationAct[] getAnimations() {
        return ANIMATIONACTS;
    }

    public UrsusVillager(EntityType<? extends AnimatedEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
        this.getNavigation().setCanFloat(true);

        if(!level().isClientSide){
            setHead(getRandom().nextInt(3));
            setBody(getRandom().nextInt(3));
            this.activity = getBrain().getActiveNonCoreActivity().orElse(Activity.IDLE);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<UrsusVillager>(this, "Controller", 3, this::predicate));
    }

    @Override
    protected void registerGoals() {
        //super.registerGoals();
        //this.goalSelector.addGoal(7, new FFLookAtPlayerGoal<>(this, Player.class, 8.0F));
        //this.goalSelector.addGoal(6, new FFRandomLookAroundGoal<>(this));
        //this.goalSelector.addGoal(8, new FFWaterAvoidingRandomStrollGoal(this , 0.31));
    }

    @Override
    public void tick() {
        super.tick();

        if(level().isClientSide){

        }
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        getBrain().setMemory(MemoryModuleType.HOME,GlobalPos.of(level().dimension(),blockPosition()));
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    public Brain<UrsusVillager> getBrain() {
        return (Brain<UrsusVillager>) super.getBrain();
    }

    protected Brain.Provider<UrsusVillager> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    protected Brain<?> makeBrain(Dynamic<?> pDynamic) {
        Brain<UrsusVillager> brain = this.brainProvider().makeBrain(pDynamic);
        this.registerBrainGoals(brain);
        return brain;
    }

    public void refreshBrain(ServerLevel pServerLevel) {
        Brain<UrsusVillager> brain = this.getBrain();
        brain.stopAll(pServerLevel, this);
        this.brain = brain.copyWithoutBehaviors();
        this.registerBrainGoals(this.getBrain());
    }

    private void registerBrainGoals(Brain<UrsusVillager> pVillagerBrain) {
        pVillagerBrain.setSchedule(Schedule.VILLAGER_DEFAULT);

        pVillagerBrain.addActivity(Activity.CORE,UrsusVillagerGoalPackages.getCorePackage(this));

        pVillagerBrain.addActivity(Activity.IDLE, UrsusVillagerGoalPackages.getIdlePackage(this,.24F));
        pVillagerBrain.addActivity(Activity.REST, UrsusVillagerGoalPackages.getRestPackage(0.24F));
        pVillagerBrain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT,5, UrsusVillagerGoalPackages.getFightPackage(this,.24F), MemoryModuleType.ATTACK_TARGET);

        pVillagerBrain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        pVillagerBrain.setDefaultActivity(Activity.IDLE);
        pVillagerBrain.setActiveActivityIfPossible(Activity.IDLE);
        if(pVillagerBrain.getActiveNonCoreActivity().isEmpty())
            pVillagerBrain.updateActivityFromSchedule(this.level().getDayTime(), this.level().getGameTime());
    }

    @Override
    protected void customServerAiStep() {
        Brain<UrsusVillager> brain1 = this.getBrain();
        this.level().getProfiler().push("ursusVillagerBrain");
        brain1.tick((ServerLevel)this.level(), this);
        this.level().getProfiler().pop();
        Optional<Activity> activeNonCoreActivity = brain1.getActiveNonCoreActivity();
        if(activeNonCoreActivity.isPresent()){
            Activity activity1 = activeNonCoreActivity.get();
            if(activity1 != this.activity){
                this.activityChange(activity1);
            }
        }

        super.customServerAiStep();
    }

    @Override
    @NotNull
    protected BodyRotationControl createBodyControl() {
        return new FFBodyRotationControl(this);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new FFPathNavigateGround(this, level);
    }

    @Override
    protected <T extends GeoEntity> void basicAnimation(AnimationState<T> event) {
        if(event.isMoving())
            event.setAnimation(WALK_);
        else
            event.setAnimation(IDLE_);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.ATTACK_DAMAGE, 4f)
                .add(Attributes.ARMOR, 2f)
                .add(Attributes.MOVEMENT_SPEED, 1f);
    }

    public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
        return false;
    }

    @Override
    public Dialogue getDialogue() {
        return DialogueStore.pursuer_d_1;
    }

    @Override
    public LivingEntity getDialogueEntity() {
        return dialogueLivingEntity;
    }

    @Override
    public void setDialogueEntity(LivingEntity dialogueEntity) {
        dialogueLivingEntity = dialogueEntity;
    }

    @Override
    public boolean getHasDialogue() {
        return getDialogue()!=null;
    }

    @Override
    public int getArmorValue() {
        return isArmor()?super.getArmorValue():2;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BODY,0);
        this.entityData.define(HEAD,0);
        this.entityData.define(IS_ARMOR,false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("body",getBody());
        pCompound.putInt("head",getHead());
        pCompound.putBoolean("isarmor",isArmor());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        setBody(pCompound.getInt("body"));
        setHead(pCompound.getInt("head"));
        setIsArmor(pCompound.getBoolean("isarmor"));
    }

    public void setBody(int body){
        this.body_type = body;
        this.entityData.set(BODY,body);
    }

    public int getBody(){
        if(this.body_type!=-1){
            return this.body_type;
        }else {
            this.body_type = this.entityData.get(BODY);
            return body_type;
        }
    }

    public void setHead(int head){
        this.head_type = head;
        this.entityData.set(HEAD,head);
    }

    public int getHead(){
        if(this.head_type!=-1){
            return this.head_type;
        }else {
            this.head_type = this.entityData.get(HEAD);
            return head_type;
        }
    }

    public void activityChange(Activity activity) {
        Brain<UrsusVillager> brain1 = getBrain();
        //if(activity==Activity.REST){
        //    Optional<GlobalPos> memory = brain1.getMemory(MemoryModuleType.HOME);
        //    GlobalPos pos = memory.orElse((GlobalPos) null);
        //    if(pos!=null){
        //        brain1.setMemory(MemoryModuleType.WALK_TARGET,new WalkTarget(pos.pos(),0.24F,1));
        //    }
        //}
        getBrain().setMemory(MemoryModuleTypeHandle.BEHAVIOR_RUN_ONE.get(),true);
        this.activity = activity;
    }

    public void setIsArmor(boolean isArmor){
        this.entityData.set(IS_ARMOR,isArmor);
    }

    public boolean isArmor(){
        return this.entityData.get(IS_ARMOR);
    }
}
