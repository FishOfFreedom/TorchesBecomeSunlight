package com.freefish.torchesbecomesunlight.server.entity.projectile;


import com.bobmowzie.mowziesmobs.client.particle.ParticleHandler;
import com.bobmowzie.mowziesmobs.client.particle.util.AdvancedParticleBase;
import com.bobmowzie.mowziesmobs.client.particle.util.ParticleComponent;
import com.bobmowzie.mowziesmobs.client.particle.util.RibbonComponent;
import com.freefish.torchesbecomesunlight.client.particle.BlackFlatParticle;
import com.freefish.torchesbecomesunlight.client.particle.DemonHoleParticle;
import com.freefish.torchesbecomesunlight.server.entity.AnimatedEntity;
import com.freefish.torchesbecomesunlight.server.entity.effect.PursuerEffectEntity;
import com.freefish.torchesbecomesunlight.server.entity.ursus.Pursuer;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.entity.effect.EffectEntity;
import com.freefish.torchesbecomesunlight.server.entity.effect.IceTuft;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class BlackSpear extends EffectEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final int MAX_ACTIVE = 200;
    private boolean isHit = false;
    private Vec3 reVec3;

    private static final EntityDataAccessor<Integer> TYPE = SynchedEntityData.defineId(BlackSpear.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ETIME = SynchedEntityData.defineId(BlackSpear.class, EntityDataSerializers.INT);

    public BlackSpear(EntityType<? extends BlackSpear> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }

    public BlackSpear(Level level, LivingEntity caster,int type) {
        this(EntityHandle.BLACK_SPEAR.get(), level);
        this.caster = caster;
        setType(type);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(TYPE,0);
        this.entityData.define(ETIME,0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        setType(compoundTag.getInt("type"));
        setEtime(compoundTag.getInt("etime"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("type",getType1());
        compoundTag.putInt("etime",getEtime());
    }

    @Override
    public void tick() {
        super.tick();
        this.move(MoverType.SELF, this.getDeltaMovement());
        if (tickCount <= MAX_ACTIVE) {
            if (tickCount % 2 == 0&&!isHit) {
                double ac = 1.8;
                int type1 = getType1();
                if(type1==2)ac-=0.32;
                else if(type1==3) ac -= 0.4;
                this.setDeltaMovement(this.getDeltaMovement().scale(ac));
            }
        }
        if (this.tickCount > MAX_ACTIVE|this.tickCount > getEtime()+30) {
            this.discard();
        } else {
            this.doHurtTarget();
        }
        if(level().isClientSide&&tickCount==1){
            Vec3 vec3 = position();
            float f1 = (float) Math.toRadians(getXRot());
            float f2 = (float) Math.toRadians(getYRot()-180);
            int eTime = 2;
            Vec3 faceVec = new Vec3(0,0,5).xRot(f1).yRot(3.14f+f2).add(position());
            AdvancedParticleBase.spawnParticle(level(), ParticleHandler.PIXEL.get(), getX(), getY(), getZ(), 0, 0, 0, false, 0, 0, 0, 0, 3.5F, 1, 1, 1, 0.75, 1, eTime, true, false, new ParticleComponent[]{
                    new ParticleComponent.Attractor(new Vec3[]{faceVec}, 0.5f, 0.2f, ParticleComponent.Attractor.EnumAttractorBehavior.LINEAR),
                    new RibbonComponent(ParticleHandler.RIBBON_FLAT.get(), 10, 0, 0, 0, 0.12F, 1, 1, 1, 0.75, true, true, new ParticleComponent[]{
                            new RibbonComponent.PropertyOverLength(RibbonComponent.PropertyOverLength.EnumRibbonProperty.SCALE, ParticleComponent.KeyTrack.startAndEnd(1, 0))
                    }),
                    new ParticleComponent.FaceMotion(),
                    new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, new ParticleComponent.KeyTrack(new float[]{0, 0, 1}, new float[]{0, 0.05f, 0.06f}), false),
            });
            level().addParticle(new DemonHoleParticle.DemonHoleData(30,3,(float) f2,f1),vec3.x,vec3.y,vec3.z,0,0,0);
            if(getType1()==3){
                Vec3 faceVec1 = new Vec3(0,0,22).xRot(f1).yRot(3.14f+f2).add(position());
                level().addParticle(new BlackFlatParticle.BlackFlatData(getEtime()+30,3),vec3.x,vec3.y,vec3.z,faceVec1.x,faceVec1.y,faceVec1.z);
            }
        }

        if(tickCount == 1) reVec3 = new Vec3(getX(),getY(),getZ());
        if(this.tickCount > getEtime()&&getType1()==3&&reVec3!=null){
            doDemonAttack();
        }

        BlockState blockState = getBlockStateOn();
        if (!blockState.isAir()) {
            if(getType1()==3) {
                isHit=true;
                setDeltaMovement(0, 0, 0);
            }
            else
                discard();
        }
    }

    private void doDemonAttack(){
        Vec3 line = reVec3.subtract(position()).normalize();
        List<LivingEntity> list = level().getEntitiesOfClass(LivingEntity.class,getBoundingBox().inflate(22),entity -> !(entity instanceof Pursuer));
        for(LivingEntity livingEntity: list){
            Vec3 subtract = livingEntity.position().subtract(position());
            double dot = line.dot(subtract);
            if(dot>0) {
                Vec3 line1 = line.scale(dot);
                float len = (float) line1.subtract(subtract).length();
                if (len<3){
                    livingEntity.setDeltaMovement(0,0,0);
                    livingEntity.setPos(livingEntity.xo,livingEntity.yo,livingEntity.zo);
                    if(caster!=null) {
                        AttributeInstance attribute = caster.getAttribute(Attributes.ATTACK_DAMAGE);
                        if (attribute != null) {
                            float damage = (float) attribute.getValue();
                            livingEntity.hurt(this.damageSources().mobAttack(caster), damage);
                        }
                    }
                }
            }
        }
    }

    private void doHurtTarget() {
        if (!this.level().isClientSide) {
            float add = getType1()==3?0.35f:0f;
            List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.35+add));
            boolean flad = false;
            for (LivingEntity target : entities) {
                //todo
                if (target == caster) continue;
                AttributeInstance attribute = target.getAttribute(Attributes.ATTACK_DAMAGE);
                if(attribute!=null) {
                    float damage = (float) attribute.getValue();
                    flad = target.hurt(this.damageSources().mobAttack(caster), damage);
                }
            }
            if(flad){
                if(getType1()==3) {
                }
                else
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
        controllerRegistrar.add(new AnimationController<BlackSpear>(this, "Controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState<BlackSpear> event) {
        event.getController().setAnimation(RawAnimation.begin().thenPlayAndHold("a"));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public void setType(int type){
        this.entityData.set(TYPE,type);
    }

    public int getType1(){
        return this.entityData.get(TYPE);
    }

    public void setEtime(int type){
        this.entityData.set(ETIME,type);
    }

    public int getEtime(){
        return this.entityData.get(ETIME);
    }
}
