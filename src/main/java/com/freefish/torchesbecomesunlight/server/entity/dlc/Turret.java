package com.freefish.torchesbecomesunlight.server.entity.dlc;


import com.freefish.torchesbecomesunlight.client.util.particle.ParticleCloud;
import com.freefish.torchesbecomesunlight.server.init.ParticleHandler;
import com.freefish.torchesbecomesunlight.client.util.particle.util.AdvancedParticleBase;
import com.freefish.torchesbecomesunlight.client.util.particle.util.ParticleComponent;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.GuerrillasEntity;
import com.freefish.torchesbecomesunlight.server.entity.projectile.Bullet;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.projectile.Projectile;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Turret extends Mob implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Nullable
    private UUID ownerUUID;
    @Nullable
    private Entity owner;

    public Turret(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        if(!level().isClientSide){
            this.entityData.set(PREDICATE, 0);
        }
    }

    public Turret(EntityType<? extends Mob> pEntityType,Entity owner, Level pLevel) {
        super(pEntityType, pLevel);
        if(!level().isClientSide){
            setOwner(owner);
            this.entityData.set(PREDICATE, 0);
        }
    }

    private static final EntityDataAccessor<Integer> PREDICATE = SynchedEntityData.defineId(Turret.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_FIRE = SynchedEntityData.defineId(Turret.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_DIE = SynchedEntityData.defineId(Turret.class, EntityDataSerializers.BOOLEAN);

    private final AnimationController<Turret> animationController1 = new AnimationController<Turret>(this, "HandController", 5, this::basicHandAnimation);

    private PlayState basicHandAnimation(AnimationState<Turret> event) {
        if(!this.entityData.get(IS_DIE)){
            if (onGroundTime > 25) {
                if (getIsFire()) {
                    event.setAnimation(RawAnimation.begin().thenLoop("loop"));
                } else {
                    event.setAnimation(RawAnimation.begin().thenPlayAndHold("end"));

                }
            } else {
                if (onGroundTime == 0) {
                    event.setAnimation(RawAnimation.begin().thenPlayAndHold("appear2"));
                } else {
                    event.setAnimation(RawAnimation.begin().thenPlayAndHold("appear"));
                }
            }
        }
        else {
            event.setAnimation(RawAnimation.begin().thenPlayAndHold("abd"));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(animationController1);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Mob.class, true,livingEntity -> livingEntity instanceof Mob mob&&mob.getTarget() instanceof GunKnightPatriot));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Zombie.class, true));
        this.targetSelector.addGoal(1, new OwnerProtectGoal(this, 32));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return GuerrillasEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 50.0D)
                .add(Attributes.ATTACK_DAMAGE, 20.0f)
                .add(Attributes.ARMOR, 10.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE,1f);
    }

    private int onGroundTime = 0;

    @Override
    public void tick() {
        super.tick();
        if(tickCount==1){
            AdvancedParticleBase.spawnParticle(level(), ParticleHandler.RING_BIG.get(), getX(), getY() + 0.5, getZ(), 0, 0.01, 0, false, 0, org.joml.Math.toRadians(-90), 0, 0, 50F, 1, 1, 1, 1, 1, 8, true, false, new ParticleComponent[]{
                    new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, ParticleComponent.KeyTrack.easeInCubic(0f, 120f), false),
                    new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, ParticleComponent.KeyTrack.easeInCubic(1f, 0.3f), false)
            });
        }
        if(onGroundTime==0){
            if(level().isClientSide){
                level().addParticle(new ParticleCloud.CloudData(ParticleHandler.CLOUD.get(), 0.8f, 0.8f, 1f, (float) (20d + random.nextDouble() * 20d), 60, ParticleCloud.EnumCloudBehavior.SHRINK, 1f), getX()+1-random.nextFloat()*2, getY()+1-random.nextFloat()*2, getZ()+1-random.nextFloat()*2, 0, 0.5, 0);
            }
        }

        if(onGround()&&level().isClientSide){
            if(onGroundTime<25) {
                onGroundTime++;
                if(onGroundTime==1&&level().isClientSide){
                    for (int i = 0; i < 3; i++) {
                        float ran = random.nextFloat();
                        for (int j = 0; j < 8; j++) {
                            Vec3 vec3 = position();
                            Vec3 move = new Vec3(0, 0.1,  0.8+i / 10f).yRot((float) org.joml.Math.PI * 2 * j / 8 + ran);
                            level().addParticle(new ParticleCloud.CloudData(ParticleHandler.CLOUD.get(), 0.8f, 0.8f, 1f, (float) (10d + random.nextDouble() * 10d), 40, ParticleCloud.EnumCloudBehavior.SHRINK, 1f), vec3.x, vec3.y-1.5, vec3.z, move.x, move.y, move.z);
                        }
                    }
                }
            }

            int armorTick = tickCount%25;
            if(armorTick<=9&&armorTick%3==0){
                float dist = 5 - armorTick/3f;
                float height = (float) Math.sqrt(25 - dist*dist);
                int Max = (int)(dist*2*3.14);

                for (int j = 0; j < Max+1; j++) {
                    Vec3 offset = new Vec3(0, height,  dist).yRot(6.28f * j / Max);
                    float y = (float) (Mth.atan2(offset.x, offset.z));
                    float p = (float) (Mth.atan2(offset.y, offset.horizontalDistance()));
                    ParticleComponent.KeyTrack keyTrack2 = new ParticleComponent.KeyTrack(new float[]{0, 1, 0}, new float[]{0, 0.5f, 1});
                    AdvancedParticleBase.spawnParticle(this.level(), ParticleHandler.ARMOR.get(), getX() + offset.x, getY()+offset.y, getZ() + offset.z, 0, 0, 0, false, y, -p, 0, 0, 4F, 1, 0.8, 0, 0.6, 1, 10, true, false, new ParticleComponent[]{
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, keyTrack2, false)
                    });
                }
            }
        }
        if(getDeltaMovement().y>-2.9&&onGroundTime==0){
            this.move(MoverType.SELF, this.getDeltaMovement());
            setDeltaMovement(getDeltaMovement().add(0, -0.5, 0));
        }

        if(!level().isClientSide){
            if(tickCount>710) {
                kill();
            }

            if(isAlive()){
                Entity ownerC = getOwner();
                LivingEntity target = null;
                if (ownerC instanceof Mob mob && mob.getTarget()!=null)
                    target = mob.getTarget();
                else
                    target = getTarget();

                if (tickCount % 10 == 0) {
                    setIsFire(target != null && distanceTo(target) < 16);
                }

                if (target != null && distanceTo(target) < 24 && getPredicate() == 0) {
                    if(tickCount % 5 == 0){
                        Vec3 vec3 = new Vec3(0, 0, 1.2).xRot((float) Math.toRadians(-getXRot())).yRot((float) (-getYRot() / 180 * Math.PI));

                        Bullet abstractarrow = new Bullet(level(), this, 0);
                        abstractarrow.setPos(position().add(0, 1.7, 0));

                        abstractarrow.shoot(vec3.x, vec3.y, vec3.z, 2.5F, 1);
                        this.level().addFreshEntity(abstractarrow);
                    }

                    lookAt(target, 3f, 3f);
                }

                //if (tickCount % 5 == 0 && target != null && getPredicate() == 1) {
                //    for (int i = 0; i < 6; i++) {
                //        Vec3 vec3 = new Vec3(0, 0, 0.5).yRot(i / 6f * 6.28f);
                //        Bullet abstractarrow = new Bullet(level(), 0);
                //        abstractarrow.setPos(vec3.add(getX(), getY() + 1.625, getZ()));
                //        abstractarrow.shoot(vec3.x, vec3.y, vec3.z, 2F, 1);
                //        this.level().addFreshEntity(abstractarrow);
                //    }
                //}
            }

            List<Projectile> list = level().getEntitiesOfClass(Projectile.class,getBoundingBox().inflate(7), entity ->
                    entity.distanceTo(this)<6.5);

            for(Projectile arrow:list){
                if(arrow instanceof Bullet bullet&&bullet.isHoly()) continue;
                if(arrow.getDeltaMovement().dot(position().add(0,1.7,0).subtract(arrow.position()))<=0) continue;
                //TorchesBecomeSunlight.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this), new sendAdvancedParticlePacket(getId(),arrow.position()));
                arrow.discard();
            }
        }
    }

    @Override
    public void die(DamageSource pDamageSource) {
        if(!level().isClientSide)
            this.entityData.set(IS_DIE,true);
        super.die(pDamageSource);
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if(level().isClientSide){
            level().addParticle(ParticleTypes.SMOKE, getRandomX(1), getY() + 1.1D, getRandomZ(1), 0.0D, 0.0D, 0.0D);
        }
        if (this.tickCount >= 710 && !this.level().isClientSide() && !this.isRemoved()) {
            this.level().broadcastEntityEvent(this, (byte)60);
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    @Override
    protected @org.jetbrains.annotations.Nullable SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.IRON_GOLEM_HURT;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.hasUUID("Owner")) {
            this.ownerUUID = pCompound.getUUID("Owner");
            this.owner = null;
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        if (this.ownerUUID != null) {
            pCompound.putUUID("Owner", this.ownerUUID);
        }
    }

    public void setOwner(Entity owner) {
        this.owner = owner;
        ownerUUID = owner.getUUID();
    }

    public Entity getOwner() {
        if(level().isClientSide())
            return null;
        else if(owner==null){
            owner =  ((ServerLevel) level()).getEntity(ownerUUID);
            return owner;
        }
        else {
            return owner;
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PREDICATE,0);
        this.entityData.define(IS_FIRE,false);
        this.entityData.define(IS_DIE,false);
    }

    public int getPredicate(){
        return this.entityData.get(PREDICATE);
    }

    public boolean getIsFire(){
        return this.entityData.get(IS_FIRE);
    }

    public void setIsFire(boolean isFire){
        this.entityData.set(IS_FIRE,isFire);
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false;
    }

    public static void SpawnTurret(Level level, Vec3 pos, Entity owner){
        Turret turret = new Turret(EntityHandle.TURRET.get(), owner, level);
        turret.setPos(pos);
        level.addFreshEntity(turret);
    }

    public static class OwnerProtectGoal extends Goal {
        private final Turret venerable;
        private final float searchRange;
        private final int randomCooling;
        private final List<Mob> mandatoryTarget = new ArrayList<>();

        public OwnerProtectGoal(Turret venerable, float searchRange) {
            this.venerable = venerable;
            this.searchRange = searchRange;
            this.randomCooling = 10 + this.venerable.getRandom().nextInt(10) + 1;
        }

        @Override
        public boolean canUse() {
            if (this.venerable.isAlive() && this.venerable.getOwner() != null) {
                Mob owner = (Mob) this.venerable.getOwner();
                LivingEntity target = owner.getTarget();
                if (!owner.isAlive() || target == null) return false;
                if (this.venerable.tickCount % randomCooling == 0) return false;
                this.mandatoryTarget.addAll(this.venerable.level().getEntitiesOfClass(Mob.class, this.venerable.getBoundingBox().inflate(searchRange))
                        .stream().filter(mob -> mob != owner  && mob.getTarget() == owner)
                        .limit(Mth.floor(searchRange * 2))
                        .toList());
            }
            return !mandatoryTarget.isEmpty();
        }

        @Override
        public void start() {
            try {
                this.mandatoryTarget.stream().filter(Mob::isAlive).forEach(mob -> {
                            mob.setTarget(this.venerable);
                            if (mob.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET)) {
                                mob.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, this.venerable);
                            }
                        }
                );
            } finally {
                this.mandatoryTarget.clear();
            }
        }
    }
}
