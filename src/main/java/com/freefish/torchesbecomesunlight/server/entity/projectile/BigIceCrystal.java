package com.freefish.torchesbecomesunlight.server.entity.projectile;

import com.bobmowzie.mowziesmobs.client.particle.ParticleHandler;
import com.bobmowzie.mowziesmobs.client.particle.util.AdvancedParticleBase;
import com.bobmowzie.mowziesmobs.client.particle.util.ParticleComponent;
import com.bobmowzie.mowziesmobs.client.particle.util.RibbonComponent;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster.FrostNova;
import com.freefish.torchesbecomesunlight.server.init.BlockHandle;
import com.freefish.torchesbecomesunlight.server.capability.frozen.FrozenCapabilityProvider;
import com.freefish.torchesbecomesunlight.server.init.DamageSourceHandle;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.GuerrillasEntity;
import com.freefish.torchesbecomesunlight.server.init.SoundHandle;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.List;

public class BigIceCrystal extends AbstractArrow implements GeoEntity {
    private boolean dealtDamage;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final int[][] off = new int[][]{new int[]{3,0},new int[]{-3,0},new int[]{0,3},new int[]{0,-3}};

    public BigIceCrystal(EntityType<? extends BigIceCrystal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public BigIceCrystal(Level pLevel, LivingEntity pShooter) {
        super(EntityHandle.BIG_ICE_CRYSTAL.get(), pShooter, pLevel);;
    }

    public void tick() {
        if (this.inGroundTime > 200) {
            removeIceFloor();
            kill();
        }
        if(!level().isClientSide) {
            if (inGroundTime == 1)
                spawnIceFloor();
        }

        if(tickCount<=10) {
            setDeltaMovement(0, 0.8f*((10-tickCount)/10.0f), 0);
        }
        if(tickCount==11) {
            setDeltaMovement(0, -1.2, 0);
        }
        if(level().isClientSide&&tickCount>=11&&tickCount<=21)
            spawnRing();

        if(inGroundTime>0){
            if(level().isClientSide)addFx();
            hurtEntity();
        }
        if (inGroundTime == 1) {
            List<LivingEntity> livingEntity = level().getEntitiesOfClass(LivingEntity.class,getBoundingBox().inflate(6),entity ->
                    !(entity instanceof GuerrillasEntity) && entity.distanceTo(this)<=3.5+entity.getBbWidth()/2);
            for(LivingEntity livingEntity1:livingEntity) {
                livingEntity1.getCapability(FrozenCapabilityProvider.FROZEN_CAPABILITY).ifPresent(data ->{
                    data.setFrozen(livingEntity1,200);
                });
            }
        }

        super.tick();
    }

    @Nullable
    protected EntityHitResult findHitEntity(Vec3 pStartVec, Vec3 pEndVec) {
        return this.dealtDamage ? null : super.findHitEntity(pStartVec, pEndVec);
    }

    protected void onHitEntity(EntityHitResult pResult) {
        Entity entity = pResult.getEntity();
        float f = 8.0F;

        Entity entity1 = this.getOwner();
        DamageSource damagesource = this.damageSources().trident(this, (Entity)(entity1 == null ? this : entity1));
        this.dealtDamage = true;
        if (entity.hurt(damagesource, f)) {
            if (entity.getType() == EntityType.ENDERMAN) {
                return;
            }

            if (entity instanceof LivingEntity) {
                LivingEntity livingentity1 = (LivingEntity)entity;
                if (entity1 instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects(livingentity1, entity1);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity)entity1, livingentity1);
                }

                this.doPostHurtEffects(livingentity1);
            }
        }
    }

    protected boolean tryPickup(Player pPlayer) {
        return super.tryPickup(pPlayer) || this.isNoPhysics() && this.ownedBy(pPlayer) && pPlayer.getInventory().add(this.getPickupItem());
    }

    @Override
    protected ItemStack getPickupItem() {
        return null;
    }

    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.ARROW_HIT;
    }

    public void playerTouch(Player pEntity) {
        if (this.ownedBy(pEntity) || this.getOwner() == null) {
            super.playerTouch(pEntity);
        }

    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.dealtDamage = pCompound.getBoolean("DealtDamage");
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("DealtDamage", this.dealtDamage);
    }

    protected float getWaterInertia() {
        return 0.99F;
    }

    public boolean shouldRender(double pX, double pY, double pZ) {
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<BigIceCrystal>(this, "Controller", 2, this::predicate));
    }

    private PlayState predicate(AnimationState<BigIceCrystal> event) {
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public void spawnIceFloor(){
        BlockPos blockPos = getOnPos().below(3);
        for(int i = 0;i<5;i++){
            for(int j = 0;j<5;j++){
                BlockPos blockPos1 = new BlockPos(blockPos.offset(i-2,0,j-2));
                int count = -1;
                boolean flag = false;
                for(int i1 = 0;i1<6;i1++){
                    BlockState blockState = level().getBlockState(blockPos1.above(i1));
                    if(blockState.is(BlockHandle.ORE_AND_ICE.get())) flag = true;
                    if(!blockState.isAir()&& !blockState.is(Blocks.SNOW))
                        count = i1;
                }
                if(count==-1||count==5||flag) continue;
                BlockPos blockpos = blockPos1.above(count+1);
                BlockState blockstate2 = BlockHandle.ORE_AND_ICE.get().defaultBlockState();
                level().setBlock(blockpos, blockstate2, 3);
                level().gameEvent(GameEvent.BLOCK_PLACE, blockpos, GameEvent.Context.of(this, blockstate2));
            }
        }
        for(int[] ints:off){
            BlockPos blockPos1 = new BlockPos(blockPos.offset(ints[0],0,ints[1]));
            int count = -1;
            boolean flag = false;
            for(int i1 = 0;i1<6;i1++){
                BlockState blockState = level().getBlockState(blockPos1.above(i1));
                if(blockState.is(BlockHandle.ORE_AND_ICE.get())) flag = true;
                if(!blockState.isAir()&& !blockState.is(Blocks.SNOW))
                    count = i1;
            }
            if(count==-1||count==5||flag) continue;
            BlockPos blockpos = blockPos1.above(count+1);
            BlockState blockstate2 = BlockHandle.ORE_AND_ICE.get().defaultBlockState();
            level().setBlock(blockpos, blockstate2, 3);
            level().gameEvent(GameEvent.BLOCK_PLACE, blockpos, GameEvent.Context.of(this, blockstate2));
        }
        playSound(SoundHandle.ICE_GROUND.get(), 0.8F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
    }

    public void removeIceFloor(){
        BlockPos blockPos = getOnPos().below(3);
        for(int i = 0;i<5;i++){
            for(int j = 0;j<5;j++){
                BlockPos blockPos1 = new BlockPos(blockPos.offset(i-2,0,j-2));
                for(int i1 = 0;i1<6;i1++){
                    BlockPos temp = blockPos1.above(i1);
                    BlockState blockState = level().getBlockState(temp);
                    if(blockState.is(BlockHandle.ORE_AND_ICE.get())){
                        BlockState blockstate2 = Blocks.SNOW.defaultBlockState();
                        level().destroyBlock(temp,false);
                        level().setBlock(temp, blockstate2, 3);
                        level().gameEvent(GameEvent.BLOCK_PLACE, temp, GameEvent.Context.of(this, blockstate2));
                    }
                }
            }
        }
        for(int[] ints:off){
            BlockPos blockPos1 = new BlockPos(blockPos.offset(ints[0],0,ints[1]));
            for(int i1 = 0;i1<6;i1++){
                BlockPos temp = blockPos1.above(i1);
                BlockState blockState = level().getBlockState(temp);
                if(blockState.is(BlockHandle.ORE_AND_ICE.get())){
                    BlockState blockstate2 = Blocks.SNOW.defaultBlockState();
                    level().destroyBlock(temp,false);
                    level().setBlock(temp, blockstate2, 3);
                    level().gameEvent(GameEvent.BLOCK_PLACE, temp, GameEvent.Context.of(this, blockstate2));
                }
            }
        }
    }

    public void addFx(){
        if(level().isClientSide&&tickCount%2==0){
            int time = 1+random.nextInt(2);
            for(int i = 0;i<time;i++) {
                Vec3 vec3 = position().add(new Vec3(0,0.5+random.nextFloat(),3.5*random.nextFloat()).yRot((float)Math.PI*2*random.nextFloat()));
                AdvancedParticleBase.spawnParticle(level(), ParticleHandler.SAN.get(), vec3.x, vec3.y, vec3.z, 0.01-0.02*random.nextFloat(), 0.02, 0.01-0.02*random.nextFloat(), true, 0, 0, 0, 0, 1.5F, 1, 1, 1, 0.75, 1, 40+random.nextInt(21), true, false, new ParticleComponent[]{
                        new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, ParticleComponent.KeyTrack.startAndEnd(1f, 0f), false)
                });
            }
        }
    }

    public void hurtEntity(){
        List<LivingEntity> livingEntity = level().getEntitiesOfClass(LivingEntity.class,getBoundingBox().inflate(6),entity ->
                !(entity instanceof GuerrillasEntity) && entity.distanceTo(this)<=3.5+entity.getBbWidth()/2);
        for(LivingEntity livingEntity1:livingEntity){
            livingEntity1.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,20,2));
            if(tickCount%5==0) {
                Entity caster = getOwner();
                if(caster instanceof FrostNova snowNova){
                    float damage = (float)snowNova.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
                    livingEntity1.hurt(DamageSourceHandle.SnowMonsterFrozen(snowNova),damage/2);
                    Vec3 move = livingEntity1.getDeltaMovement();
                    livingEntity1.setDeltaMovement(move.x*0.8f,move.y,move.z*0.8f);
                }
            }
        }
    }

    public void spawnRing(){
        if(level().isClientSide){
            if((tickCount&3)==0) {
                AdvancedParticleBase.spawnParticle(level(), ParticleHandler.RING_BIG.get(), getX(), getY(), getZ(), 0, 0, 0, false, 0, -Math.toRadians(90), 0, 0, 4F, 1, 1, 1, 0.75, 1, 25, true, false, new ParticleComponent[]{
                        new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, ParticleComponent.KeyTrack.startAndEnd(1f, 0f), false),
                        new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, ParticleComponent.KeyTrack.startAndEnd(5f, 20f), false)
                });
            }
            AdvancedParticleBase.spawnParticle(level(), ParticleHandler.ARROW_HEAD.get(), xo, yo, zo, 0, 0, 0, false, 0, 0, 0, 0, 3.5F, 1, 1, 1, 0.75, 1, 2, true, false, new ParticleComponent[]{
                    new ParticleComponent.Attractor(new Vec3[]{new Vec3(getX(), getY(), getZ())}, 0.5f, 0.2f, ParticleComponent.Attractor.EnumAttractorBehavior.LINEAR),
                    new RibbonComponent(ParticleHandler.RIBBON_FLAT.get(), 10, 0, 0, 0, 0.12F, 1, 1, 1, 0.75, true, true, new ParticleComponent[]{
                            new RibbonComponent.PropertyOverLength(RibbonComponent.PropertyOverLength.EnumRibbonProperty.SCALE, ParticleComponent.KeyTrack.startAndEnd(1, 0))
                    }),
                    new ParticleComponent.FaceMotion(),
                    new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, new ParticleComponent.KeyTrack(new float[]{0, 0, 1}, new float[]{0, 0.05f, 0.06f}), false),
            });
        }
    }
}
