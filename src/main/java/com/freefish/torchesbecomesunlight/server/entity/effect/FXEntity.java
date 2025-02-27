package com.freefish.torchesbecomesunlight.server.entity.effect;

import com.freefish.torchesbecomesunlight.server.entity.dlc.GunKnightPatriot;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.init.SoundHandle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class FXEntity extends Entity  {
    //private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final EntityDataAccessor<Integer> PREDICATE = SynchedEntityData.defineId(FXEntity.class, EntityDataSerializers.INT);
    private int predicateCa = -1;
    private LivingEntity living;

    public FXEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void tick() {
        if(getPredicate()==0){
            int bombRange = tickCount*2;
            if(level().isClientSide){
                int len = (int) ((bombRange + 1) * 2 * 3.14f);
                for (int i = 0; i < len; i+=2) {
                    if(random.nextBoolean()){
                        Vec3 vec3 = new Vec3(0, 0, bombRange + 1).yRot(6.28f * i / len).add(position());
                        level().addParticle(ParticleTypes.EXPLOSION, vec3.x, vec3.y, vec3.z, 0, 0, 0);
                    }
                }
            }
            else {
                if(tickCount==1){
                    playSound(SoundHandle.BIG_BOOM.get(), 2.5F, 1f);
                }
                if (getLiving() instanceof GunKnightPatriot gunKnightPatriot) {
                    float damage = (float) gunKnightPatriot.getAttributeValue(Attributes.ATTACK_DAMAGE);
                    List<LivingEntity> list = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(bombRange + 1), livingEntity ->
                            !(livingEntity instanceof GunKnightPatriot) && livingEntity.distanceTo(gunKnightPatriot) < (bombRange + 1));
                    for (LivingEntity livingEntity : list) {
                        if (bombRange <= 4) livingEntity.invulnerableTime = 0;
                        livingEntity.hurt(damageSources().mobAttack(gunKnightPatriot), damage * (21 - bombRange));
                    }
                }
                int len = (int) ((bombRange-2) * 1.5 * 3.14f);
                for (int i = 0; i < len; i++) {
                    Vec3 vec3 = new Vec3(0, -1.5, bombRange).yRot(6.28f * i / len).add(position());
                    if (random.nextBoolean()) {
                        BlockPos pos = new BlockPos((int)vec3.x, (int)vec3.y, (int)vec3.z);
                        BlockPos abovePos = new BlockPos(pos).above();
                        BlockState block = level().getBlockState(pos);
                        BlockState blockAbove = level().getBlockState(abovePos);
                        if (!block.isAir() && block.isRedstoneConductor(level(), pos) && !block.hasBlockEntity() && !blockAbove.blocksMotion()) {
                            EntityFallingBlock fallingBlock = new EntityFallingBlock(EntityHandle.FALLING_BLOCK.get(), level(), block, 0.3f);
                            fallingBlock.setPos(vec3.x + 0.5, vec3.y + 1, vec3.z + 0.5);
                            level().addFreshEntity(fallingBlock);
                        }
                    }
                }
            }

            if (tickCount > 10)
                kill();
        }
        super.tick();
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(PREDICATE,-1);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        setPredicate(pCompound.getInt("predicate"));
    }

    @Override
    public boolean shouldRender(double pX, double pY, double pZ) {
        return true;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putInt("predicate",getPredicate());
    }

    public void setPredicate(int predicate){
        this.entityData.set(PREDICATE,predicate);
    }

    public LivingEntity getLiving() {
        return living;
    }

    public void setLiving(LivingEntity living) {
        this.living = living;
    }


    public int getPredicate(){
        if(predicateCa!=-1)
            return predicateCa;
        else {
            predicateCa = this.entityData.get(PREDICATE);
            return predicateCa;
        }
    }

    public static void SpawnFXEntity(Level level, int type, Vec3 pos){
        FXEntity fxEntity = new FXEntity(EntityHandle.FX_ENTITY.get(), level);
        fxEntity.setPredicate(type);
        fxEntity.setPos(pos);
        level.addFreshEntity(fxEntity);
    }

    public static void SpawnFXEntity(Level level, int type, Vec3 pos, LivingEntity living){
        FXEntity fxEntity = new FXEntity(EntityHandle.FX_ENTITY.get(), level);
        fxEntity.setPredicate(type);
        fxEntity.setPos(pos);
        fxEntity.setLiving(living);
        level.addFreshEntity(fxEntity);
    }
}
