package com.freefish.torchesbecomesunlight.server.entity.projectile;

import com.freefish.rosmontislib.client.particle.advance.base.particle.RLParticle;
import com.freefish.rosmontislib.client.particle.advance.data.EmissionSetting;
import com.freefish.rosmontislib.client.particle.advance.data.RendererSetting;
import com.freefish.rosmontislib.client.particle.advance.data.material.MaterialHandle;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction3;
import com.freefish.rosmontislib.client.particle.advance.data.number.RandomConstant;
import com.freefish.rosmontislib.client.particle.advance.data.number.color.Gradient;
import com.freefish.rosmontislib.client.particle.advance.data.number.curve.Line;
import com.freefish.rosmontislib.client.particle.advance.data.shape.Circle;
import com.freefish.rosmontislib.client.particle.advance.data.shape.Dot;
import com.freefish.rosmontislib.client.particle.advance.effect.BlockEffect;
import com.freefish.rosmontislib.client.particle.advance.effect.EntityEffect;
import com.freefish.rosmontislib.client.utils.GradientColor;
import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.event.packet.toclient.MessageUseAbility;
import com.freefish.torchesbecomesunlight.server.event.packet.toclient.ProjectileHitEntityMessage;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import org.apache.commons.lang3.ArrayUtils;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

public abstract class NoGravityProjectileEntity extends Projectile implements GeoEntity, IEntityAdditionalSpawnData {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final EntityDataAccessor<Byte> ID_FLAGS = SynchedEntityData.defineId(NoGravityProjectileEntity.class, EntityDataSerializers.BYTE);

    @Nullable
    private BlockState lastState;
    protected boolean inGround;
    protected int inGroundTime;
    public int shakeTime;
    private int life;
    private int knockback;
    private SoundEvent soundEvent = this.getDefaultHitGroundSoundEvent();

    private final IntOpenHashSet ignoredEntities = new IntOpenHashSet();

    protected NoGravityProjectileEntity(EntityType<? extends NoGravityProjectileEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    protected NoGravityProjectileEntity(EntityType<? extends NoGravityProjectileEntity> pEntityType, double pX, double pY, double pZ, Level pLevel) {
        this(pEntityType, pLevel);
        this.setPos(pX, pY, pZ);
    }

    protected NoGravityProjectileEntity(EntityType<? extends NoGravityProjectileEntity> pEntityType, LivingEntity pShooter, Level pLevel) {
        this(pEntityType, pShooter.getX(), pShooter.getEyeY() - (double)0.1F, pShooter.getZ(), pLevel);
        this.setOwner(pShooter);
    }

    public void setSoundEvent(SoundEvent pSoundEvent) {
        this.soundEvent = pSoundEvent;
    }

    public boolean shouldRenderAtSqrDistance(double pDistance) {
        double d0 = this.getBoundingBox().getSize() * 10.0D;
        if (Double.isNaN(d0)) {
            d0 = 1.0D;
        }

        d0 *= 64.0D * getViewScale();
        return pDistance < d0 * d0;
    }

    protected void defineSynchedData() {
        this.entityData.define(ID_FLAGS, (byte)0);
    }

    public void shoot(double pX, double pY, double pZ, float pVelocity, float pInaccuracy) {
        super.shoot(pX, pY, pZ, pVelocity, pInaccuracy);
        this.life = 0;
    }

    @Override
    public void lerpTo(double pX, double pY, double pZ, float pYaw, float pPitch, int pPosRotationIncrements, boolean pTeleport) {
        this.setPos(pX, pY, pZ);
        this.setRot(pYaw, pPitch);
    }

    @Override
    public void lerpMotion(double pX, double pY, double pZ) {
        //super.lerpMotion(pX, pY, pZ);
        this.life = 0;
    }

    boolean isHitBlockSecond;

    @Override
    public void tick() {
        super.tick();
        boolean flag = this.isNoPhysics();
        Vec3 vec3 = this.getDeltaMovement();
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            double d0 = vec3.horizontalDistance();
            this.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * (double)(180F / (float)Math.PI)));
            this.setXRot((float)(Mth.atan2(vec3.y, d0) * (double)(180F / (float)Math.PI)));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }

        BlockPos blockpos = this.blockPosition();
        BlockState blockstate = this.level().getBlockState(blockpos);
        if (!blockstate.isAir() && !flag) {
            VoxelShape voxelshape = blockstate.getCollisionShape(this.level(), blockpos);
            if (!voxelshape.isEmpty()) {
                Vec3 vec31 = this.position();

                for(AABB aabb : voxelshape.toAabbs()) {
                    if (aabb.move(blockpos).contains(vec31)) {
                        this.inGround = true;
                        break;
                    }
                }
            }
        }

        if (this.shakeTime > 0) {
            --this.shakeTime;
        }

        if (this.isInWaterOrRain() || blockstate.is(Blocks.POWDER_SNOW) || this.isInFluidType((fluidType, height) -> this.canFluidExtinguish(fluidType))) {
            this.clearFire();
        }

        if (!this.level().isClientSide) {
            this.tickDespawn();
        }

        if (this.inGround) {
            if (this.lastState != blockstate && this.shouldFall()) {
                this.startFalling();
            }
            ++this.inGroundTime;
        } else {
            this.inGroundTime = 0;
            Vec3 vec32 = this.position();
            Vec3 vec33 = vec32.add(vec3);
            HitResult hitresult = this.level().clip(new ClipContext(vec32, vec33, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if (hitresult.getType() != HitResult.Type.MISS) {
                vec33 = hitresult.getLocation();
            }

            while(!this.isRemoved()) {
                EntityHitResult entityhitresult = this.findHitEntity(vec32, vec33);
                if (entityhitresult != null) {
                    hitresult = entityhitresult;
                }

                if (hitresult != null && hitresult.getType() == HitResult.Type.ENTITY) {
                    Entity entity = ((EntityHitResult)hitresult).getEntity();
                    Entity entity1 = this.getOwner();
                    if (entity instanceof Player && entity1 instanceof Player && !((Player)entity1).canHarmPlayer((Player)entity)) {
                        hitresult = null;
                        entityhitresult = null;
                    }
                }

                if (hitresult != null && hitresult.getType() != HitResult.Type.MISS) {
                    this.onHit(hitresult);
                    this.hasImpulse = true;
                    break;
                }

                if (entityhitresult == null) {
                    break;
                }

                hitresult = null;
            }

            HitResult hitblockresult = this.level().clip(new ClipContext(this.position(), this.position().add(vec3), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if(hitblockresult.getType()!= HitResult.Type.MISS){
                onHit(hitblockresult);
            }
            isHitBlockSecond = false;

            if (this.isRemoved())
                return;

            vec3 = changeDeltaMovement(this.getDeltaMovement());
            double d5 = vec3.x;
            double d6 = vec3.y;
            double d1 = vec3.z;

            double d7 = this.getX() + d5;
            double d2 = this.getY() + d6;
            double d3 = this.getZ() + d1;
            double d4 = vec3.horizontalDistance();
            if (flag) {
                this.setYRot((float)(Mth.atan2(-d5, -d1) * (double)(180F / (float)Math.PI)));
            } else {
                this.setYRot((float)(Mth.atan2(d5, d1) * (double)(180F / (float)Math.PI)));
            }

            this.setXRot((float)(Mth.atan2(d6, d4) * (double)(180F / (float)Math.PI)));
            this.setXRot(lerpRotation(this.xRotO, this.getXRot()));
            this.setYRot(lerpRotation(this.yRotO, this.getYRot()));
            float f = 1F;
            if (this.isInWater()) {
                for(int j = 0; j < 4; ++j) {
                    this.level().addParticle(ParticleTypes.BUBBLE, d7 - d5 * 0.25D, d2 - d6 * 0.25D, d3 - d1 * 0.25D, d5, d6, d1);
                }
                f = this.getWaterInertia();
            }

            this.setDeltaMovement(vec3.scale(f));
            if (!this.isNoGravity() && !flag) {
                Vec3 vec34 = this.getDeltaMovement();
                this.setDeltaMovement(vec34.x, vec34.y - (double)0.05F, vec34.z);
            }

            this.setPos(d7, d2, d3);
            this.checkInsideBlocks();
        }
    }

    public Vec3 changeDeltaMovement(Vec3 vec3){
        return vec3;
    }

    private boolean shouldFall() {
        return this.inGround && this.level().noCollision((new AABB(this.position(), this.position())).inflate(0.06D));
    }

    private void startFalling() {
        this.inGround = false;
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.multiply((double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F)));
        this.life = 0;
    }

    @Override
    public void move(MoverType pType, Vec3 pPos) {
        super.move(pType, pPos);
        if (pType != MoverType.SELF && this.shouldFall()) {
            this.startFalling();
        }
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        Vec3 deltaMovement = getDeltaMovement();
        buffer.writeDouble(deltaMovement.x);
        buffer.writeDouble(deltaMovement.y);
        buffer.writeDouble(deltaMovement.z);
        buffer.writeFloat(getXRot());
        buffer.writeFloat(getYRot());
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        Vec3 deltaMovement = new Vec3(additionalData.readDouble(),additionalData.readDouble(),additionalData.readDouble());
        setDeltaMovement(deltaMovement);
        setXRot(additionalData.readFloat());
        setYRot(additionalData.readFloat());
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    protected void tickDespawn() {
        ++this.life;
        if (this.life >= getTickDespawn()) {
            this.discard();
        }
    }

    public int getTickDespawn(){
        return 1200;
    }

    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        Entity entity = pResult.getEntity();
        Entity owner = getOwner();
        if(owner instanceof LivingEntity livingOwner) livingOwner.getAttributeValue(Attributes.ATTACK_DAMAGE);
        int i = Mth.ceil((getOwner() !=null&&getOwner() instanceof LivingEntity?(((LivingEntity) getOwner()).getAttributeValue(Attributes.ATTACK_DAMAGE)):1));

        Entity entity1 = this.getOwner();
        DamageSource damagesource;

        damagesource = this.damageSources().mobAttack( (LivingEntity) entity1);

        int k = entity.getRemainingFireTicks();
        if (this.isOnFire()) {
            entity.setSecondsOnFire(5);
        }

        if (entity.hurt(damagesource, (float)i)) {
            if (entity instanceof LivingEntity) {
                LivingEntity livingentity = (LivingEntity)entity;

                if (this.knockback > 0) {
                    double d0 = Math.max(0.0D, 1.0D - livingentity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                    Vec3 vec3 = this.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).normalize().scale((double)this.knockback * 0.6D * d0);
                    if (vec3.lengthSqr() > 0.0D) {
                        livingentity.push(vec3.x, 0.1D, vec3.z);
                    }
                }

                if (!this.level().isClientSide && entity1 instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects(livingentity, entity1);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity)entity1, livingentity);
                }

                this.doPostHurtEffects(livingentity);
            }

            this.playSound(this.soundEvent, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        } else {
            entity.setRemainingFireTicks(k);
        }
        hitEntity(entity);
        if(!level().isClientSide){
            TorchesBecomeSunlight.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this), new ProjectileHitEntityMessage(this, entity.getId()));
        }

        if(isHitEntityDiscard())
            this.discard();
    }

    public void hitEntity(Entity target){

    }

    protected void onHitBlock(BlockHitResult pResult) {
        if(isHitBlockSecond) return;

        this.lastState = this.level().getBlockState(pResult.getBlockPos());
        super.onHitBlock(pResult);
        Vec3 vec3 = pResult.getLocation().subtract(this.getX(), this.getY(), this.getZ());
        this.setDeltaMovement(vec3);
        Vec3 vec31 = vec3.normalize().scale((double)0.05F);
        this.setPosRaw(this.getX() - vec31.x, this.getY() - vec31.y, this.getZ() - vec31.z);
        this.playSound(this.getHitGroundSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        this.inGround = true;
        this.shakeTime = 7;
        this.setSoundEvent(SoundEvents.ARROW_HIT);
        isHitBlockSecond = true;
    }

    /**
     * The sound made when an entity is hit by this projectile
     */
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.ARROW_HIT;
    }

    protected final SoundEvent getHitGroundSoundEvent() {
        return this.soundEvent;
    }

    protected void doPostHurtEffects(LivingEntity pTarget) {
    }

    @Nullable
    protected EntityHitResult findHitEntity(Vec3 pStartVec, Vec3 pEndVec) {
        return ProjectileUtil.getEntityHitResult(this.level(), this, pStartVec, pEndVec, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), this::canHitEntity);
    }

    protected boolean canHitEntity(Entity p_36743_) {
        if(getOwner() != null && getOwner() == p_36743_) return false;
        return super.canHitEntity(p_36743_) && !this.ignoredEntities.contains(p_36743_.getId());
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putShort("life", (short)this.life);
        if (this.lastState != null) {
            pCompound.put("inBlockState", NbtUtils.writeBlockState(this.lastState));
        }

        pCompound.putByte("shake", (byte)this.shakeTime);
        pCompound.putBoolean("inGround", this.inGround);
        pCompound.putString("SoundEvent", BuiltInRegistries.SOUND_EVENT.getKey(this.soundEvent).toString());
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.life = pCompound.getShort("life");
        if (pCompound.contains("inBlockState", 10)) {
            this.lastState = NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), pCompound.getCompound("inBlockState"));
        }

        this.shakeTime = pCompound.getByte("shake") & 255;
        this.inGround = pCompound.getBoolean("inGround");

        if (pCompound.contains("SoundEvent", 8)) {
            this.soundEvent = BuiltInRegistries.SOUND_EVENT.getOptional(new ResourceLocation(pCompound.getString("SoundEvent"))).orElse(this.getDefaultHitGroundSoundEvent());
        }
    }

    public void setOwner(@Nullable Entity pEntity) {
        super.setOwner(pEntity);
    }

    public void playerTouch(Player pEntity) {
        if (!this.level().isClientSide && (this.inGround || this.isNoPhysics()) && this.shakeTime <= 0) {
        }
    }

    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    public void setKnockback(int pKnockback) {
        this.knockback = pKnockback;
    }

    public int getKnockback() {
        return this.knockback;
    }

    public boolean isAttackable() {
        return false;
    }

    protected float getEyeHeight(Pose pPose, EntityDimensions pSize) {
        return 0.13F;
    }

    private void setFlag(int pId, boolean pValue) {
        byte b0 = this.entityData.get(ID_FLAGS);
        if (pValue) {
            this.entityData.set(ID_FLAGS, (byte)(b0 | pId));
        } else {
            this.entityData.set(ID_FLAGS, (byte)(b0 & ~pId));
        }
    }

    protected float getWaterInertia() {
        return 0.6F;
    }

    public void setNoPhysics(boolean pNoPhysics) {
        this.noPhysics = pNoPhysics;
        this.setFlag(2, pNoPhysics);
    }

    public boolean isNoPhysics() {
        if (!this.level().isClientSide) {
            return this.noPhysics;
        } else {
            return (this.entityData.get(ID_FLAGS) & 2) != 0;
        }
    }

    public boolean isHitEntityDiscard(){
        return false;
    }
}
