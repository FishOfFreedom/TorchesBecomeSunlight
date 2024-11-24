package com.freefish.torchesbecomesunlight.server.entity.projectile;

import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.entity.effect.EntityFallingBlock;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.GuerrillasEntity;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.shield.Patriot;
import com.freefish.torchesbecomesunlight.server.entity.effect.EntityCameraShake;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraftforge.network.NetworkHooks;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashSet;
import java.util.List;

public class HalberdOTIEntity extends AbstractArrow implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private ItemStack tridentItem = new ItemStack(Items.TRIDENT);
    private boolean dealtDamage;
    public HashSet<LivingEntity> livingEntities = new HashSet<LivingEntity>();
    private boolean isFirstOnGround;

    public HalberdOTIEntity(EntityType<? extends HalberdOTIEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public HalberdOTIEntity(Level pLevel, LivingEntity pShooter, ItemStack pStack) {
        super(EntityHandle.HALBERD_OTI_ENTITY.get(), pShooter, pLevel);
        this.tridentItem = pStack.copy();
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    public void tick() {
        for(LivingEntity livingEntity : livingEntities){
            Vec3 move = position().add(0,1,0).subtract(livingEntity.position());
            if(move.length()>3){
                livingEntities.remove(livingEntity);
                continue;
            }
            livingEntity.setDeltaMovement(move);
        }

        if (this.inGroundTime > 4) {
            this.dealtDamage = true;
        }

        if(inGround && !isFirstOnGround){
            Vec3 vector3d = position();
            EntityCameraShake.cameraShake(level(), vector3d, 25, 0.1f, 0, 20);
            if(level().isClientSide) {
                playSound(SoundEvents.GENERIC_EXPLODE, 3, 1F + random.nextFloat() * 0.1F);
                for(int j = 0;j<8;j++) {
                    for (int i = 0; i < 36-j*4; i++) {
                        Vec3 bomb = vector3d.add(new Vec3(4-j/2.0, j*0.5, 0).yRot((float) Math.PI / (18-j*2) * i+random.nextFloat()*0.1f));
                        level().addParticle(ParticleTypes.EXPLOSION, bomb.x, bomb.y, bomb.z, 1.0D, 0.1D, 0.0D);
                    }
                }
            }
            Entity shooter = getOwner();
            if(shooter instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) shooter;
                float damage = (float) living.getAttribute(Attributes.ATTACK_DAMAGE).getValue()/2;
                List<LivingEntity> livingEntities = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(10));
                for (LivingEntity livingEntity : livingEntities) {
                    if (livingEntity.distanceToSqr(this) > 25 || Math.abs(livingEntity.getY() - getY()) > 3)
                        continue;
                    if(livingEntity instanceof GuerrillasEntity)
                        continue;
                    float d = 1.0f - (float) distanceToSqr(livingEntity)/25.0f;
                    Vec3 vector3d1 = livingEntity.position().subtract(this.position()).normalize();
                    livingEntity.setDeltaMovement(vector3d1.scale(d).add(0,0.5,0));
                    livingEntity.hurt(damageSources().trident(this,shooter), damage*d);
                }
            }
            //for(int i = 0;i<3;i++){
            //    BlockPos blockPos = new BlockPos((int)vector3d.x,(int)vector3d.y,(int)vector3d.z);
            //    BlockState blockState = level().getBlockState(blockPos);
            //    if(blockState.isAir()||blockState.isCollisionShapeFullBlock(level(),blockPos))
            //        break;
            //    else
            //        vector3d = vector3d.add(0,1,0);
            //}
            for (int i1 = 1; i1 <= 10; i1++) {
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
            isFirstOnGround = true;
        }
        if(this.tickCount > 200) {
            List<Entity> ridingEntitys = getPassengers();
            for(Entity entity1:ridingEntitys)
                entity1.stopRiding();
            if(getOwner() instanceof Patriot)
                this.kill();
        }

        super.tick();
    }

    protected ItemStack getPickupItem() {
        return this.tridentItem.copy();
    }

    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        float f = 20.0F;
        if(getOwner()instanceof Patriot)
            f = (float) ((Patriot)getOwner()).getAttributeValue(Attributes.ATTACK_DAMAGE)*1.2f;
        if (entity instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity)entity;
            if(entity instanceof Player player){
                ItemStack pPlayerItemStack = player.getUseItem();
                if (!pPlayerItemStack.isEmpty() && pPlayerItemStack.is(Items.SHIELD)) {
                    player.getCooldowns().addCooldown(Items.SHIELD, 100);
                    this.level().broadcastEntityEvent(player, (byte)30);
                }
            }
        }
        Entity entity1 = this.getOwner();
        DamageSource damagesource = damageSources().mobAttack((LivingEntity) entity1);
        this.dealtDamage = true;
        SoundEvent soundevent = SoundEvents.TRIDENT_HIT;

        if (entity instanceof LivingEntity) {
            LivingEntity livingentity1 = (LivingEntity)entity;
            if(!livingEntities.contains(livingentity1)) {
                livingentity1.setDeltaMovement(this.getDeltaMovement());
                entity.hurt(damagesource, f);
                livingEntities.add(livingentity1);
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
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.put("Trident", this.tridentItem.save(new CompoundTag()));
        pCompound.putBoolean("DealtDamage", this.dealtDamage);
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
}
