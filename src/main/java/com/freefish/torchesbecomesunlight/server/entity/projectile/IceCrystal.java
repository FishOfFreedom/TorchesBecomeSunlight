package com.freefish.torchesbecomesunlight.server.entity.projectile;

import com.bobmowzie.mowziesmobs.client.particle.ParticleHandler;
import com.bobmowzie.mowziesmobs.client.particle.util.AdvancedParticleBase;
import com.bobmowzie.mowziesmobs.client.particle.util.ParticleComponent;
import com.bobmowzie.mowziesmobs.client.particle.util.RibbonComponent;
import com.freefish.torchesbecomesunlight.server.capability.frozen.FrozenCapabilityProvider;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster.FrostNova;
import com.freefish.torchesbecomesunlight.server.init.DamageSourceHandle;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.entity.effect.IceTuft;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.GuerrillasEntity;
import com.freefish.torchesbecomesunlight.server.entity.effect.EntityCameraShake;
import com.freefish.torchesbecomesunlight.server.init.SoundHandle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
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
import java.util.List;

public class IceCrystal extends Projectile implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final int MAX_ACTIVE = 400;

    public IceCrystal(EntityType<? extends IceCrystal> entityType, Level level) {
        super(entityType, level);
    }

    public IceCrystal(Level level, LivingEntity caster) {
        super(EntityHandle.ICE_CRYSTAL.get(), level);
        setOwner(caster);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public void tick() {
        super.tick();
        Entity entity = this.getOwner();
        if (this.level().isClientSide || (entity == null || !entity.isRemoved()) && this.level().hasChunkAt(this.blockPosition())) {
            this.move(MoverType.SELF, this.getDeltaMovement());

            HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hitresult.getType() != HitResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult)) {
                this.onHit(hitresult);
            }

            this.checkInsideBlocks();

            if (this.tickCount >= MAX_ACTIVE) {
                this.discard();
            }
        } else {
            this.discard();
        }
        spawnRing();
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        this.bomb(hitResult.getEntity());
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        this.bomb(null);
    }

    private void bomb(@Nullable Entity entity){
        if(entity instanceof LivingEntity livingEntity){
            boolean flag = true;
            if(entity instanceof Player player){
                ItemStack pPlayerItemStack = player.getUseItem();
                if(!pPlayerItemStack.isEmpty() && pPlayerItemStack.is(Items.SHIELD))
                    flag = false;
            }
            if (flag) {
                livingEntity.getCapability(FrozenCapabilityProvider.FROZEN_CAPABILITY).ifPresent(data -> {
                    data.setFrozen(livingEntity, 150);
                });
            }
        }
        Entity caster = getOwner();

        if(caster instanceof FrostNova snowNova) {
            int is1;
            if(snowNova.getState()==1) is1 = 1;
            else is1 = 0;
            List<LivingEntity> livingEntities = level().getEntitiesOfClass(LivingEntity.class,getBoundingBox().inflate(6),hit ->
                    !(hit instanceof GuerrillasEntity)&&hit.distanceTo(this)<2+is1+hit.getBbWidth()/2);
            for(LivingEntity livingEntity:livingEntities){
                    double damage = snowNova.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
                    livingEntity.hurt(DamageSourceHandle.SnowMonsterFrozen(snowNova),(float) damage);
            }
        }
        playSound(SoundHandle.ICE_CRYSTAL.get(), 1.0F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
        EntityCameraShake.cameraShake(this.level(), this.position(), 16F, 0.025F, 5, 15);
        spawnIceParticle();
        this.discard();
    }

    private void spawnIceParticle(){
        Vec3 vec3 = position();

        int count1=0;
        for(int i = 0;i<3;i++){
            BlockPos blockpos2 = new BlockPos((int)vec3.x,(int)(vec3.y-i),(int)vec3.z);
            BlockState blockState = level().getBlockState(blockpos2);
            if(blockState.isAir()) count1++;
        }
        if(count1 == 3) return;
        int count2=0;
        for(int i = 0;i<3;i++){
            BlockPos blockpos2 = new BlockPos((int)vec3.x,(int)(vec3.y-i),(int)vec3.z);
            BlockState blockState = level().getBlockState(blockpos2);
            if(blockState.isAir())
                count2++;
            else
                break;
        }
        Vec3 finalVec = new Vec3(getX(), Math.ceil(vec3.y - count2 - 1), getZ());
        if(getOwner() instanceof FrostNova snowNova&&snowNova.getState()==1) {
            int time = 3 + random.nextInt(3);
            for (int i = 0; i < time; i++) {
                IceTuft iceTuft = new IceTuft(EntityHandle.ICE_TUFT.get(), level());
                iceTuft.setYRot(2 * (float) Math.PI * random.nextFloat());
                iceTuft.setPos(finalVec.add(3 - random.nextFloat() * 6, 0, 3 - random.nextFloat() * 6));
                if (!level().isClientSide)
                    level().addFreshEntity(iceTuft);
            }
        }
        if(level().isClientSide){
            for(int i = 0;i<4;i++) {

                Vec3 vec31 =finalVec.add(3-random.nextFloat()*6,0,3-random.nextFloat()*6);
                level().addParticle(ParticleTypes.EXPLOSION,vec31.x,vec31.y,vec31.z,0,0,0);
            }
        }
    }

    @Override
    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    @Override
    public boolean isInWater() {
        return false;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (this.level().isClientSide) {
            return false;
        } else {
            this.markHurt();
            Entity entity = source.getEntity();
            if (entity != null) {

                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<IceCrystal>(this, "Controller", 1, this::predicate));
    }

    private PlayState predicate(AnimationState<IceCrystal> event) {
        event.getController().setAnimation(RawAnimation.begin().thenLoop("normal"));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public void spawnRing(){
        if(level().isClientSide){
            AdvancedParticleBase.spawnParticle(level(), ParticleHandler.RING_BIG.get(), getX(), getY(), getZ(), 0, 0, 0, false, Math.toRadians(getYRot()), -Math.toRadians(getXRot()), 0, 0, 4F, 1, 1, 1, 0.75, 1, 15, true, false, new ParticleComponent[]{
                    new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, ParticleComponent.KeyTrack.startAndEnd(1f, 0f), false),
                    new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, ParticleComponent.KeyTrack.startAndEnd(1f, 10f), false)
            });
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
