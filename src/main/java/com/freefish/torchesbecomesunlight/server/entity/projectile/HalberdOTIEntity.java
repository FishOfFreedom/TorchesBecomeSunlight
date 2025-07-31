package com.freefish.torchesbecomesunlight.server.entity.projectile;

import com.freefish.torchesbecomesunlight.client.util.particle.ParticleCloud;
import com.freefish.torchesbecomesunlight.client.util.particle.util.AdvancedParticleBase;
import com.freefish.torchesbecomesunlight.client.util.particle.util.ParticleComponent;
import com.freefish.torchesbecomesunlight.server.entity.effect.EntityCameraShake;
import com.freefish.torchesbecomesunlight.server.entity.effect.EntityFallingBlock;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.GuerrillasEntity;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.shield.Patriot;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.init.ParticleHandler;
import com.freefish.torchesbecomesunlight.server.util.FFEntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class HalberdOTIEntity extends AbstractArrow implements GeoEntity , IEntityAdditionalSpawnData {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private ItemStack tridentItem = new ItemStack(Items.TRIDENT);
    private boolean dealtDamage;
    public HashSet<LivingEntity> livingEntities = new HashSet<LivingEntity>();
    private boolean isFirstOnGround;
    private boolean isLocate;
    private int halberdUseTime = 20;

    public HalberdOTIEntity(EntityType<? extends HalberdOTIEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public HalberdOTIEntity(Level pLevel, LivingEntity pShooter, ItemStack pStack,boolean isLocate) {
        super(EntityHandle.HALBERD_OTI_ENTITY.get(), pShooter, pLevel);
        this.tridentItem = pStack.copy();
        this.isLocate = isLocate;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    public void tick() {
        Iterator<LivingEntity> iterator = livingEntities.iterator();
        while (iterator.hasNext()){
            LivingEntity livingEntity = iterator.next();
            Vec3 move = position().add(0,1,0).subtract(livingEntity.position());
            livingEntity.setDeltaMovement(move);
            if(move.length()>4||!livingEntity.isAlive()){
                iterator.remove();
            }
        }

        if (this.inGroundTime > 4) {
            this.dealtDamage = true;
        }

        if(inGround && !isFirstOnGround){
            groundFX();
            isFirstOnGround = true;
        }
        if(this.tickCount > 200) {
            List<Entity> ridingEntitys = getPassengers();
            for(Entity entity1:ridingEntitys)
                entity1.stopRiding();
            if(getOwner() instanceof Patriot)
                this.kill();
        }

        if(!inGround&&halberdUseTime>30){
            spawnRing();
        }

        super.tick();
    }

    private void groundFX(){
        Vec3 vector3d = position();
        EntityCameraShake.cameraShake(level(), vector3d, 25, 0.1f, 0, 20);
        Vec3 move = new Vec3(getDeltaMovement().x,0,getDeltaMovement().z);

        if(level().isClientSide) {
            AdvancedParticleBase.spawnParticle(level(), ParticleHandler.RING_BIG.get(), getX(), getY() + 0.5, getZ(), 0, 0.01, 0, false, 0, org.joml.Math.toRadians(-90), 0, 0, 50F, 1, 1, 1, 1, 1, 12, true, false, new ParticleComponent[]{
                    new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, ParticleComponent.KeyTrack.startAndEnd(0f, 80f), false),
                    new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, ParticleComponent.KeyTrack.startAndEnd(1f, 0.5f), false)
            });

            playSound(SoundEvents.GENERIC_EXPLODE, 3, 1F + random.nextFloat() * 0.1F);
            for(int j = 0;j<6;j++) {
                for (int i = 0; i < 6; i++) {
                    if(random.nextBoolean()) {
                        Vec3 vec = new Vec3(0, 0, 2+random.nextFloat()*2).xRot(3.14f * j / 6).yRot(3.14f * i / 6);
                        level().addParticle(ParticleTypes.EXPLOSION, vec.x + getX(), vec.y + getY(), vec.z + getZ(), 1.0D, 0.1D, 0.0D);
                    }
                }
            }
            for(int i=0;i<20;i++){
                for(int j=0;j<10;j++){
                    Vec3 vec3 = new Vec3(0, 0, random.nextFloat()+j/10f).xRot((float) ((0.1+j/25f+random.nextFloat()*0.1) * org.joml.Math.PI)).yRot((float) (random.nextFloat()*0.5+(i/10f) * org.joml.Math.PI));
                    level().addParticle(new ParticleCloud.CloudData(ParticleHandler.CLOUD.get(), 1,1,1, (float) (10d + random.nextDouble() * 15d), 60-j*3, ParticleCloud.EnumCloudBehavior.SHRINK, 1f), getX(), getY(0.5) , getZ(), vec3.x, vec3.y, vec3.z);
                }
            }
        }

        Entity shooter = getOwner();
        if(shooter instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) shooter;
            float damage = (float) living.getAttribute(Attributes.ATTACK_DAMAGE).getValue()/2;
            List<LivingEntity> livingEntities = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(6));
            for (LivingEntity livingEntity : livingEntities) {
                if (livingEntity.distanceToSqr(this) > 25 || !livingEntity.onGround())
                    continue;
                if(livingEntity instanceof GuerrillasEntity)
                    continue;

                float d = 1.0f - (float) distanceToSqr(livingEntity)/25.0f;
                Vec3 vector3d1 = livingEntity.position().subtract(this.position()).normalize();
                livingEntity.setDeltaMovement(vector3d1.scale(d).add(0,0.5,0));
                livingEntity.hurt(damageSources().mobAttack((LivingEntity) shooter), damage*d);
            }
        }

        for (int i1 = 1; i1 <= 6; i1++) {
            double spread = Math.PI * 2;
            int arcLen = Mth.ceil(i1 * spread * 2);
            for (int i = 0; i < arcLen; i++) {
                double theta = (i / (arcLen - 1.0) - 0.5) * spread;
                double vx = Math.cos(theta);
                double vz = Math.sin(theta);
                double px = vector3d.x + vx * i1;
                double pz = vector3d.z + vz * i1;
                float factor = 1 - i1 / (float) 5;
                int hitY = (int)(vector3d.y-0.5);
                if (random.nextBoolean()) {
                    int hitX = Mth.floor(px);
                    int hitZ = Mth.floor(pz);

                    float dist = (float) move.dot(new Vec3(hitX-getX(),0,hitZ-getZ()));

                    BlockPos pos = new BlockPos(hitX, hitY, hitZ);
                    BlockPos abovePos = new BlockPos(pos).above();
                    BlockState block = level().getBlockState(pos);
                    BlockState blockAbove = level().getBlockState(abovePos);
                    if (!block.isAir() && block.isRedstoneConductor(level(), pos) && !block.hasBlockEntity() && !blockAbove.blocksMotion()) {
                        EntityFallingBlock fallingBlock = new EntityFallingBlock(EntityHandle.FALLING_BLOCK.get(), level(), block, (float) (0.4 + factor * 0.2));
                        fallingBlock.setPos(hitX + 0.5, hitY + 1, hitZ + 0.5);
                        level().addFreshEntity(fallingBlock);
                    }
                }
            }
        }
    }

    public void setHalberdUseTime(int halberdUseTime) {
        halberdUseTime = Mth.clamp(halberdUseTime,20,120);
        this.halberdUseTime = halberdUseTime;
    }


    protected ItemStack getPickupItem() {
        return this.tridentItem.copy();
    }

    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        float f = 20.0F;

        if(getOwner()instanceof Patriot)
            f = (float) ((Patriot)getOwner()).getAttributeValue(Attributes.ATTACK_DAMAGE)*1.2f;
        else if(getOwner()instanceof Player) {
            f *= Mth.lerp((halberdUseTime-20.0f)/100,1.5f,5f);
        }

        if (entity instanceof Player player) {
            FFEntityUtils.disableShield(player,200);
        }
        Entity entity1 = this.getOwner();
        DamageSource damagesource = damageSources().mobAttack((LivingEntity) entity1);
        this.dealtDamage = true;
        SoundEvent soundevent = SoundEvents.TRIDENT_HIT;

        if (entity instanceof LivingEntity) {
            LivingEntity livingentity1 = (LivingEntity)entity;
            if(!livingEntities.contains(livingentity1)) {
                livingentity1.setDeltaMovement(this.getDeltaMovement().normalize().scale(0.5));
                entity.hurt(damagesource, f);
                if(isLocate){
                    livingEntities.add(livingentity1);
                }
            }
        }

        this.playSound(soundevent, 1f, 1.0F);
    }

    public boolean isChanneling() {
        return EnchantmentHelper.hasChanneling(this.tridentItem);
    }

    protected boolean tryPickup(Player pPlayer) {
        return super.tryPickup(pPlayer) || this.isNoPhysics() && this.ownedBy(pPlayer) && pPlayer.getInventory().add(this.getPickupItem());
    }

    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.TRIDENT_HIT_GROUND;
    }

    public void playerTouch(Player pEntity) {
        if (this.ownedBy(pEntity) || this.getOwner() == null) {
            super.playerTouch(pEntity);
        }

    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("Trident", 10)) {
            this.tridentItem = ItemStack.of(pCompound.getCompound("Trident"));
        }
        this.dealtDamage = pCompound.getBoolean("DealtDamage");
        this.isLocate = pCompound.getBoolean("islocate");
        this.halberdUseTime = pCompound.getInt("halberdUseTime");
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.put("Trident", this.tridentItem.save(new CompoundTag()));
        pCompound.putBoolean("DealtDamage", this.dealtDamage);
        pCompound.putBoolean("islocate", this.isLocate);
        pCompound.putInt("halberdUseTime", this.halberdUseTime);
    }

    protected float getWaterInertia() {
        return 0.99F;
    }

    public boolean shouldRender(double pX, double pY, double pZ) {
        return true;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar event) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public void spawnRing(){
        if(level().isClientSide&&tickCount%3==0){
            //todo 粒子会消失
            AdvancedParticleBase.spawnParticle(level(), ParticleHandler.RING_BIG.get(), getX(), getY(), getZ(), 0, 0, 0, false, Math.toRadians(getYRot()), -Math.toRadians(getXRot()), 0, 0, 4F, 1, 1, 1, 0.75, 1, 15, true, false, new ParticleComponent[]{
                    new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, new ParticleComponent.KeyTrack(new float[]{0.2f,1,0},new float[]{0,0.5f,1}), false),
                    new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, new ParticleComponent.KeyTrack(new float[]{0,60,60},new float[]{0,0.5f,1}), false)
            });
        }
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeVarInt(halberdUseTime);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        halberdUseTime = additionalData.readVarInt();
    }
}
