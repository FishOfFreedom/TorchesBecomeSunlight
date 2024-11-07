package com.freefish.torchesbecomesunlight.server.entity.projectile;


import com.bobmowzie.mowziesmobs.client.particle.ParticleHandler;
import com.bobmowzie.mowziesmobs.client.particle.util.AdvancedParticleBase;
import com.bobmowzie.mowziesmobs.client.particle.util.ParticleComponent;
import com.bobmowzie.mowziesmobs.client.particle.util.RibbonComponent;
import com.freefish.torchesbecomesunlight.client.particle.DemonHoleParticle;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.entity.effect.EffectEntity;
import com.freefish.torchesbecomesunlight.server.entity.effect.IceTuft;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class BlackSpear extends EffectEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final int MAX_ACTIVE = 40;
    private int type = 0;

    public BlackSpear(EntityType<? extends BlackSpear> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }

    public BlackSpear(Level level, LivingEntity caster,int type) {
        this(EntityHandle.BLACK_SPEAR.get(), level);
        this.caster = caster;
        this.type = type;
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        type = compoundTag.getInt("type");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("type",type);
    }

    @Override
    public void tick() {
        super.tick();
        this.move(MoverType.SELF, this.getDeltaMovement());
        if (tickCount <= MAX_ACTIVE) {
            if (tickCount % 2 == 0) {
                double ac = 70D / MAX_ACTIVE;
                if(type==2)ac-=0.3;
                this.setDeltaMovement(this.getDeltaMovement().scale(ac));
            }
        }
        if (this.tickCount > MAX_ACTIVE) {
            this.discard();
        } else {
            this.doHurtTarget();
        }
        if(level().isClientSide&&tickCount==1){
            Vec3 vec3 = position();
            float f1 = (float) Math.toRadians(getXRot());
            float f2 = (float) Math.toRadians(getYRot()-180);
            if(true){
                Vec3 faceVec = new Vec3(0,0,5).xRot(f1).yRot(3.14f+f2).add(position());
                AdvancedParticleBase.spawnParticle(level(), ParticleHandler.PIXEL.get(), getX(), getY(), getZ(), 0, 0, 0, false, 0, 0, 0, 0, 3.5F, 1, 1, 1, 0.75, 1, 2, true, false, new ParticleComponent[]{
                        new ParticleComponent.Attractor(new Vec3[]{faceVec}, 0.5f, 0.2f, ParticleComponent.Attractor.EnumAttractorBehavior.LINEAR),
                        new RibbonComponent(ParticleHandler.RIBBON_FLAT.get(), 10, 0, 0, 0, 0.12F, 1, 1, 1, 0.75, true, true, new ParticleComponent[]{
                                new RibbonComponent.PropertyOverLength(RibbonComponent.PropertyOverLength.EnumRibbonProperty.SCALE, ParticleComponent.KeyTrack.startAndEnd(1, 0))
                        }),
                        new ParticleComponent.FaceMotion(),
                        new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, new ParticleComponent.KeyTrack(new float[]{0, 0, 1}, new float[]{0, 0.05f, 0.06f}), false),
                });
            }
            level().addParticle(new DemonHoleParticle.DemonHoleData(30,3,(float) f2,f1),vec3.x,vec3.y,vec3.z,0,0,0);
        }

        BlockState blockState = getBlockStateOn();
        if (!blockState.isAir()) {
            spawnIceParticle();
            discard();
        }

    }

    private void doHurtTarget() {
        if (!this.level().isClientSide) {
            List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.2));
            boolean flad = false;
            for (LivingEntity target : entities) {
                if (target == caster) continue;
                float damage = (float) target.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
                flad = target.hurt(this.damageSources().indirectMagic(this, caster), damage);
            }
            if(flad){
                discard();
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

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    private void spawnIceParticle(){
        Vec3 vec3 = position();
        if(!level().isClientSide&&type==1) {


            int time = 3 + random.nextInt(3);
            for (int i = 0; i < time; i++) {
                IceTuft iceTuft = new IceTuft(EntityHandle.ICE_TUFT.get(), level(),caster);
                iceTuft.setYRot(2 * (float) Math.PI * random.nextFloat());
                iceTuft.setPos(MathUtils.getFirstBlockAbove(level(),vec3.add(3 - random.nextFloat() * 6, 0, 3 - random.nextFloat() * 6),4).add(0,-1,0));
                iceTuft.setTypeNumber(1);
                level().addFreshEntity(iceTuft);
            }
        }
        if(level().isClientSide){
            for(int i = 0;i<4;i++) {
                Vec3 vec31 =vec3.add(3-random.nextFloat()*6,1+random.nextFloat(),3-random.nextFloat()*6);
                level().addParticle(ParticleTypes.EXPLOSION,vec31.x,vec31.y,vec31.z,0,0,0);
            }
        }
    }
}
