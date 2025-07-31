package com.freefish.torchesbecomesunlight.server.entity;

import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EntityEyeOfStructure extends Entity implements ItemSupplier {
    private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK = SynchedEntityData.defineId(EntityEyeOfStructure.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Float> R = SynchedEntityData.defineId(EntityEyeOfStructure.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> G = SynchedEntityData.defineId(EntityEyeOfStructure.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> B = SynchedEntityData.defineId(EntityEyeOfStructure.class, EntityDataSerializers.FLOAT);
    private double tx;
    private double ty;
    private double tz;
    private int life;

    public EntityEyeOfStructure(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }


    public EntityEyeOfStructure(Level level, double px, double py, double pz) {
        super(EntityHandle.EYE_OF_STRUCTURE.get(), level);
        this.setPos(px, py, pz);
    }

    //public void setColor(Color color) {
    //    setR(color.getRed());
    //    setG(color.getGreen());
    //    setB(color.getBlue());
    //}

    public float getR() {
        return this.getEntityData().get(R);
    }

    public void setR(float r) {
        this.getEntityData().set(R, r);
    }

    public float getG() {
        return this.getEntityData().get(G);
    }

    public void setG(float g) {
        this.getEntityData().set(G, g);
    }

    public float getB() {
        return this.getEntityData().get(B);
    }

    public void setB(float b) {
        this.getEntityData().set(B, b);
    }

    public void setItem(ItemStack itemStack) {
        this.getEntityData().set(DATA_ITEM_STACK, itemStack.copyWithCount(1));
    }

    @Override
    public boolean isCurrentlyGlowing() {
        return this.level().isClientSide && !this.level().getBlockState(this.blockPosition().above()).isAir() || super.isCurrentlyGlowing();//TODO
    }

    private ItemStack getItemRaw() {
        return this.getEntityData().get(DATA_ITEM_STACK);
    }

    @Override
    public ItemStack getItem() {
        ItemStack itemstack = this.getItemRaw();
        return itemstack.isEmpty() ? ItemStack.EMPTY : itemstack;
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_ITEM_STACK, ItemStack.EMPTY);
        this.getEntityData().define(R, 0F);
        this.getEntityData().define(G, 0F);
        this.getEntityData().define(B, 0F);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        double d0 = this.getBoundingBox().getSize() * 4.0D;
        if (Double.isNaN(d0)) {
            d0 = 4.0D;
        }

        d0 *= 64.0D;
        return distance < d0 * d0;
    }

    public void signalTo(BlockPos blockPos) {
        double d0 = (double) blockPos.getX();
        int i = blockPos.getY();
        double d1 = (double) blockPos.getZ();
        double d2 = d0 - this.getX();
        double d3 = d1 - this.getZ();
        double d4 = Math.sqrt(d2 * d2 + d3 * d3);
        if (d4 > 12.0D) {
            this.tx = this.getX() + d2 / d4 * 12.0D;
            this.tz = this.getZ() + d3 / d4 * 12.0D;
            this.ty = this.getY() + 8.0D;
        } else {
            this.tx = d0;
            this.ty = (double) i;
            this.tz = d1;
        }

        this.life = 0;
    }

    @Override
    public void lerpMotion(double px, double py, double pz) {
        this.setDeltaMovement(px, py, pz);
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            double d0 = Math.sqrt(px * px + pz * pz);
            this.setYRot((float) (Mth.atan2(px, pz) * (double) (180F / (float) Math.PI)));
            this.setXRot((float) (Mth.atan2(py, d0) * (double) (180F / (float) Math.PI)));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }

    }

    @Override
    public void tick() {
        super.tick();
        if (getItem().isEmpty()) {
            discard();
        }
        Vec3 vec3 = this.getDeltaMovement();
        double d0 = this.getX() + vec3.x;
        double d1 = this.getY() + vec3.y;
        double d2 = this.getZ() + vec3.z;
        double d3 = vec3.horizontalDistance();
        this.setXRot(lerpRotation(this.xRotO, (float) (Mth.atan2(vec3.y, d3) * (double) (180F / (float) Math.PI))));
        this.setYRot(lerpRotation(this.yRotO, (float) (Mth.atan2(vec3.x, vec3.z) * (double) (180F / (float) Math.PI))));
        if (!this.level().isClientSide) {
            double d4 = this.tx - d0;
            double d5 = this.tz - d2;
            float f = (float) Math.sqrt(d4 * d4 + d5 * d5);
            float f1 = (float) Mth.atan2(d5, d4);
            double d6 = Mth.lerp(0.0025D, d3, (double) f);
            double d7 = vec3.y;
            if (f < 1.0F) {
                d6 *= 0.8D;
                d7 *= 0.8D;
            }

            int j = this.getY() < this.ty ? 1 : -1;
            vec3 = new Vec3(Math.cos((double) f1) * d6, d7 + ((double) j - d7) * (double) 0.015F, Math.sin((double) f1) * d6);
            this.setDeltaMovement(vec3);
        }

        float f2 = 0.25F;
        if (this.isInWater()) {
            for(int i = 0; i < 4; ++i) {
                this.level().addParticle(ParticleTypes.BUBBLE, d0 - vec3.x * 0.25D, d1 - vec3.y * 0.25D, d2 - vec3.z * 0.25D, vec3.x, vec3.y, vec3.z);
            }
        } else {
            this.level().addParticle(ParticleTypes.GLOW, d0 - vec3.x * 0.25D + this.random.nextDouble() * 0.6D - 0.3D, d1 - vec3.y * 0.25D - 0.5D, d2 - vec3.z * 0.25D + this.random.nextDouble() * 0.6D - 0.3D, vec3.x, vec3.y, vec3.z);
        }

        if (!this.level().isClientSide) {
            this.setPos(d0, d1, d2);
            ++this.life;
            if (this.life > 80 && !this.level().isClientSide) {
                this.playSound(SoundEvents.ENDER_EYE_DEATH, 1.0F, 1.0F);
                this.level().broadcastEntityEvent(this, (byte) 5);
                this.discard();
                this.level().addFreshEntity(new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), this.getItem()));
            }
        } else {
            this.setPosRaw(d0, d1, d2);
        }

    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 5) {
            }
        super.handleEntityEvent(id);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        ItemStack itemstack = this.getItemRaw();
        if (!itemstack.isEmpty()) {
            compoundTag.put("Item", itemstack.save(new CompoundTag()));
        }
        compoundTag.putFloat("R", getR());
        compoundTag.putFloat("G", getG());
        compoundTag.putFloat("B", getB());

    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        ItemStack itemstack = ItemStack.of(compoundTag.getCompound("Item"));
        this.setItem(itemstack);
        setR(compoundTag.getFloat("R"));
        setG(compoundTag.getFloat("G"));
        setB(compoundTag.getFloat("B"));
    }

    @Override
    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    protected static float lerpRotation(float f1, float f2) {
        while (f2 - f1 < -180.0F) {
            f1 -= 360.0F;
        }

        while (f2 - f1 >= 180.0F) {
            f1 += 360.0F;
        }

        return Mth.lerp(0.2F, f1, f2);
    }
}
