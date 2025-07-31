package com.freefish.torchesbecomesunlight.server.entity.rhodesIsland.rosmontis;

import com.freefish.rosmontislib.client.particle.advance.base.particle.RLParticle;
import com.freefish.rosmontislib.client.particle.advance.data.EmissionSetting;
import com.freefish.rosmontislib.client.particle.advance.data.RendererSetting;
import com.freefish.rosmontislib.client.particle.advance.data.UVAnimationSetting;
import com.freefish.rosmontislib.client.particle.advance.data.VelocityOverLifetimeSetting;
import com.freefish.rosmontislib.client.particle.advance.data.material.MaterialHandle;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction3;
import com.freefish.rosmontislib.client.particle.advance.data.number.RandomConstant;
import com.freefish.rosmontislib.client.particle.advance.data.number.color.Gradient;
import com.freefish.rosmontislib.client.particle.advance.data.number.color.GradientHandle;
import com.freefish.rosmontislib.client.particle.advance.data.number.curve.Line;
import com.freefish.rosmontislib.client.particle.advance.data.number.curve.RandomLine;
import com.freefish.rosmontislib.client.particle.advance.data.shape.Circle;
import com.freefish.rosmontislib.client.particle.advance.data.shape.Dot;
import com.freefish.rosmontislib.client.particle.advance.effect.BlockEffect;
import com.freefish.rosmontislib.client.particle.advance.effect.EntityEffect;
import com.freefish.rosmontislib.client.utils.GradientColor;
import com.freefish.rosmontislib.client.utils.Range;
import com.freefish.torchesbecomesunlight.compat.rosmontis.TBSMaterialHandle;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.GuerrillasEntity;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.init.SoundHandle;
import com.freefish.torchesbecomesunlight.server.util.FFEntityUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class RosmontisLivingInstallation extends Mob implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private UUID owner_UUID;
    private LivingEntity owner;
    private Vec3 selfMotion = Vec3.ZERO;

    public RosmontisLivingInstallation(EntityType<RosmontisLivingInstallation> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    int noOwnerTime;

    @Override
    protected @org.jetbrains.annotations.Nullable SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.ANVIL_HIT;
    }

    @Override
    public void tick() {
        super.tick();

        if(tickCount<=7){
            selfMotion = new Vec3(0,-3*(1 - tickCount/7f),0);
        }else if(tickCount==8){
            selfMotion=Vec3.ZERO;
        }

        if(!level().isClientSide){
            LivingEntity owner1 = getOwner();
            if(owner1==null||!owner1.isAlive()||(owner1 instanceof Rosmontis r&&r.livingInstallation != this)){
                noOwnerTime++;
                if(noOwnerTime==20) discard();
            }else {
                noOwnerTime = 0;
            }
            if(tickCount%120==0){
                if(owner1!=null){
                    float value = (float) owner1.getAttributeValue(Attributes.ATTACK_DAMAGE);
                    List<LivingEntity> entitiesOfClass = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(8),
                            (e)-> e.distanceTo(this)<8);
                    for(LivingEntity living : entitiesOfClass){
                        if(living == owner1||living == this)continue;

                        if(living.onGround()||!(living instanceof Player))
                            living.hurt(owner1.damageSources().mobAttack(owner1),value*2);
                    }
                }
            }
            if(tickCount==6){
                playSound(SoundHandle.ROS_SKILL_3.get(),2,1.0F / (random.nextFloat() * 0.4F + 0.8F));
            }
        }else {
            if(tickCount==1){
                RosmontisInstallation.ros_instationTrailFx(this,FFEntityUtils.getBodyRotVec(this,new Vec3(0,10,0)),position());
            }

            if((tickCount+43)%120==0){
                //Gradient toAlpha = new Gradient(new GradientColor(0X00FFFFFF, 0XFFFFFFFF, 0XFFFFFFFF));
                BlockEffect blockEffect = new BlockEffect(level(),this.position());

                for(int i1=0;i1<3;i1++) {
                    RLParticle rlParticle1 = new RLParticle(level());
                    int len =i1*5;

                    rlParticle1.config.setDuration(40-len);
                    rlParticle1.config.setStartDelay(NumberFunction.constant(len));
                    rlParticle1.config.setStartLifetime(NumberFunction.constant(40-len));
                    rlParticle1.config.setStartSize(new NumberFunction3(1.6));
                    rlParticle1.config.setStartColor(new Gradient(new GradientColor(0XFFB2F5FF)));
                    rlParticle1.config.getEmission().setEmissionRate(NumberFunction.constant(0));
                    EmissionSetting.Burst burst1 = new EmissionSetting.Burst();
                    burst1.setCount(NumberFunction.constant(1));
                    rlParticle1.config.getEmission().addBursts(burst1);

                    rlParticle1.config.getShape().setShape(new Dot());
                    rlParticle1.config.getShape().setPosition(new NumberFunction3(0,5-i1,0));
                    rlParticle1.config.getMaterial().setMaterial(MaterialHandle.RING.create());
                    rlParticle1.config.getMaterial().setCull(false);
                    rlParticle1.config.getRenderer().setRenderMode(RendererSetting.Particle.Mode.Horizontal);
                    rlParticle1.config.getLights().open();
                    //rlParticle1.config.getColorOverLifetime().open();
                    //rlParticle1.config.getColorOverLifetime().setColor(toAlpha);
                    rlParticle1.config.getVelocityOverLifetime().open();
                    rlParticle1.config.getVelocityOverLifetime().setLinear(new NumberFunction3(NumberFunction.constant(0),new Line(new float[]{0,(40f-len-5)/(40f-len), 1}, new float[]{-1,-1,-20}),NumberFunction.constant(0)));
                    rlParticle1.config.getSizeOverLifetime().open();
                    rlParticle1.config.getSizeOverLifetime().setSize(new NumberFunction3(new Line(new float[]{0,5f/(40f-len), 1}, new float[]{0,1,1})));
                    rlParticle1.emmit(blockEffect);
                    //rlParticle1.config.getRotationOverLifetime().open();
                    //rlParticle1.config.getRotationOverLifetime().setYaw(new RandomLine(new float[]{0, 1}, new float[]{0, 60}, new float[]{0, 20}));
                }
            }
            if((tickCount+3)%120==0){
                ros_state_1to2_boom(position().add(0,0.2,0));
                //RLParticle rlParticle = new RLParticle(level());
                //rlParticle.config.setDuration(30);
                //rlParticle.config.setStartLifetime(NumberFunction.constant(30));
                //rlParticle.config.setStartSize(new NumberFunction3(10));
                //rlParticle.config.getRenderer().setRenderMode(RendererSetting.Particle.Mode.Horizontal);
                //rlParticle.config.getShape().setShape(new Dot());
                //rlParticle.config.getMaterial().setMaterial(TBSMaterialHandle.ROSMONTIS_DIAN.create());
                //rlParticle.config.getEmission().setEmissionRate(NumberFunction.constant(0));
                //EmissionSetting.Burst burst = new EmissionSetting.Burst();burst.setCount(NumberFunction.constant(1));
                //rlParticle.config.getEmission().addBursts(burst);
                //rlParticle.emmit(new BlockEffect(level(),position().add(0,0.2,0)));
            }
            if(tickCount%20==0){
                ringFX();
            }
            if(tickCount==10){
                for(int i=0;i<=10;i++){
                    EntityEffect blockEffect = new EntityEffect(level(), this);
                    RLParticle rlParticle = new RLParticle(level());
                    rlParticle.config.setDuration(50);
                    rlParticle.config.setStartLifetime(NumberFunction.constant(50));
                    rlParticle.config.setStartSize(new NumberFunction3(0.1));
                    rlParticle.config.setStartColor(new Gradient(new GradientColor(0X5FB2F5FF)));
                    rlParticle.config.getEmission().setEmissionRate(NumberFunction.constant(0));
                    EmissionSetting.Burst burst = new EmissionSetting.Burst();
                    burst.setCount(NumberFunction.constant(1));
                    rlParticle.config.getEmission().addBursts(burst);
                    rlParticle.config.setStartSpeed(NumberFunction.constant(0));
                    Circle circle = new Circle();circle.setArc(0);
                    circle.setRadius(8);
                    circle.setRadiusThickness(0);
                    rlParticle.config.getShape().setShape(circle);
                    rlParticle.config.getShape().setPosition(new NumberFunction3(0,0.2+random.nextFloat()*0.4f,0));
                    rlParticle.config.getMaterial().setMaterial(MaterialHandle.VOID.create());
                    rlParticle.config.getLights().open();
                    rlParticle.config.getVelocityOverLifetime().open();
                    rlParticle.config.getVelocityOverLifetime().setOrbitalMode(VelocityOverLifetimeSetting.OrbitalMode.AngularVelocity);
                    rlParticle.config.getVelocityOverLifetime().setOrbital(new NumberFunction3(0, 0.5, 0));
                    rlParticle.config.trails.open();
                    rlParticle.config.trails.setLifetime(NumberFunction.constant(0.4));
                    rlParticle.config.trails.config.getMaterial().setMaterial(TBSMaterialHandle.NO_GLOW_PIXEL.create());
                    rlParticle.updateRotation(new Vector3f(0,3.14f*i/5,0));
                    rlParticle.emmit(blockEffect);
                }
            }
            if(tickCount%50==0){
                for(int i=0;i<=10;i++){
                    EntityEffect blockEffect = new EntityEffect(level(), this);
                    RLParticle rlParticle = new RLParticle(level());
                    rlParticle.config.setDuration(100);
                    rlParticle.config.setStartLifetime(NumberFunction.constant(100));
                    rlParticle.config.setStartSize(new NumberFunction3(0.1));
                    rlParticle.config.setStartColor(new Gradient(new GradientColor(0X5FB2F5FF)));
                    rlParticle.config.getEmission().setEmissionRate(NumberFunction.constant(0));
                    EmissionSetting.Burst burst = new EmissionSetting.Burst();
                    burst.setCount(NumberFunction.constant(1));
                    rlParticle.config.getEmission().addBursts(burst);
                    rlParticle.config.setStartSpeed(NumberFunction.constant(0));
                    Circle circle = new Circle();circle.setArc(0);
                    circle.setRadius(8);
                    circle.setRadiusThickness(0);
                    rlParticle.config.getShape().setShape(circle);
                    rlParticle.config.getShape().setPosition(new NumberFunction3(0,0.2+random.nextFloat()*0.4f,0));
                    rlParticle.config.getMaterial().setMaterial(MaterialHandle.VOID.create());
                    rlParticle.config.getLights().open();
                    rlParticle.config.getVelocityOverLifetime().open();
                    rlParticle.config.getVelocityOverLifetime().setOrbitalMode(VelocityOverLifetimeSetting.OrbitalMode.AngularVelocity);
                    rlParticle.config.getVelocityOverLifetime().setOrbital(new NumberFunction3(0, 0.5, 0));
                    rlParticle.config.trails.open();
                    rlParticle.config.trails.setLifetime(NumberFunction.constant(0.4));
                    rlParticle.config.trails.config.getMaterial().setMaterial(TBSMaterialHandle.NO_GLOW_PIXEL.create());
                    rlParticle.updateRotation(new Vector3f(0,3.14f*i/5,0));
                    rlParticle.emmit(blockEffect);
                }
            }
        }
    }

    @Override
    public void move(MoverType pType, Vec3 pPos) {
        super.move(pType, selfMotion);
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        LivingEntity owner1 = getOwner();
        if(owner1!=null&&owner1 == pSource.getEntity()) return false;
        return super.hurt(pSource, pAmount);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if(pCompound.contains("uuid")){
            owner_UUID = pCompound.getUUID("uuid");
        }
        tickCount = pCompound.getInt("tickCount");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        if(owner_UUID!=null){
            pCompound.putUUID("uuid", owner_UUID);
        }
        pCompound.putInt("tickCount",tickCount);
    }

    private void ringFX(){

        RLParticle rlParticle1 = new RLParticle(level());
        rlParticle1.config.setDuration(100);
        rlParticle1.config.setStartLifetime(NumberFunction.constant(20));
        rlParticle1.config.setStartSize(new NumberFunction3(16));
        rlParticle1.config.setStartColor(new Gradient(new GradientColor(0X6FB2F5FF)));
        rlParticle1.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst1 = new EmissionSetting.Burst(); burst1.setCount(NumberFunction.constant(1));
        rlParticle1.config.getEmission().addBursts(burst1);
        rlParticle1.config.setStartSpeed(NumberFunction.constant(0));
        rlParticle1.config.getShape().setShape(new Dot());
        rlParticle1.config.getShape().setPosition(new NumberFunction3(0,0.2,0));
        rlParticle1.config.getMaterial().setMaterial(MaterialHandle.CIRCLE.create());
        rlParticle1.config.getRenderer().setRenderMode(RendererSetting.Particle.Mode.Horizontal);
        rlParticle1.config.getLights().open();
        rlParticle1.config.getColorOverLifetime().open();
        rlParticle1.config.getColorOverLifetime().setColor(new Gradient(GradientHandle.CENTER_OPAQUE));
        EntityEffect blockEffect = new EntityEffect(level(), this);
        rlParticle1.emmit(blockEffect);
    }

    //@Override
    //public void move(MoverType pType, Vec3 pPos) {
    //}

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false;
    }

    @Override
    public boolean addEffect(MobEffectInstance effectInstance, @Nullable Entity entity) {
        return FFEntityUtils.isBeneficial(effectInstance.getEffect()) && super.addEffect(effectInstance, entity);
    }

    @Override
    public void forceAddEffect(MobEffectInstance effectInstance, @Nullable Entity entity) {
        if (FFEntityUtils.isBeneficial(effectInstance.getEffect()))
            super.forceAddEffect(effectInstance, entity);
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return FFEntityUtils.isBeneficial(effectInstance.getEffect()) && super.canBeAffected(effectInstance);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        super.onSyncedDataUpdated(pKey);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public LivingEntity getOwner(){
        if(owner==null&&!level().isClientSide){
            owner = (LivingEntity) ((ServerLevel)level()).getEntity(owner_UUID);
            return owner;
        }else {
            return owner;
        }
    }

    @Override
    public void remove(RemovalReason pReason) {
        if(owner instanceof Rosmontis rosmontis&&!level().isClientSide){
            rosmontis.remoteAct3Appear(position());
        }
        super.remove(pReason);
    }

    @Override
    public boolean canCollideWith(Entity pEntity) {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    public void setOwner(LivingEntity living){
        owner = living;
        owner_UUID = living.getUUID();
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    public void remoteActDamage1(double radio, float damageMul){
        LivingEntity livingOwner = getOwner();
        if(livingOwner!=null){
            float damage = (float) livingOwner.getAttributeValue(Attributes.ATTACK_DAMAGE);
            List<LivingEntity> entitiesOfClass = level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4), entity->{
                return entity.distanceTo(this)<radio;
            });
            for(LivingEntity living:entitiesOfClass){
                if(living == livingOwner) continue;
                living.invulnerableTime=0;
                //doHurtEntity(living,);
                living.hurt(damageSources().mobAttack(livingOwner),damage*damageMul);
            }
        }
    }

    public static RosmontisLivingInstallation SpawnInstallation(Level level, LivingEntity owner,Vec3 pos){
        RosmontisLivingInstallation rosmontisInstallation = new RosmontisLivingInstallation(EntityHandle.ROSMONTIS_LIVING_INSTALLATION.get(), level);
        rosmontisInstallation.setOwner(owner);
        rosmontisInstallation.setPos(pos);
        level.addFreshEntity(rosmontisInstallation);
        return rosmontisInstallation;
    }

    public void ros_state_1to2_boom(Vec3 pos){
        Gradient toAlpha = new Gradient(new GradientColor(0XFFFFFFFF, 0XFFFFFFFF, 0X00FFFFFF));

        RLParticle rlParticle = new RLParticle(level());
        rlParticle.config.setDuration(20);
        rlParticle.config.setStartLifetime(NumberFunction.constant(20));
        rlParticle.config.setStartSize(new NumberFunction3(12));
        rlParticle.config.setStartColor(new Gradient(new GradientColor(0XCFB2F5FF)));
        rlParticle.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst = new EmissionSetting.Burst(); burst.setCount(NumberFunction.constant(1));
        rlParticle.config.getEmission().addBursts(burst);
        rlParticle.config.getShape().setShape(new Dot());
        rlParticle.config.getMaterial().setMaterial(MaterialHandle.RING.create());
        rlParticle.config.getRenderer().setRenderMode(RendererSetting.Particle.Mode.Horizontal);
        rlParticle.config.getLights().open();
        rlParticle.config.getColorOverLifetime().open();
        rlParticle.config.getColorOverLifetime().setColor(toAlpha);
        rlParticle.config.getSizeOverLifetime().open();
        rlParticle.config.getSizeOverLifetime().setSize(new NumberFunction3(new Line(new float[]{0,0.33f,1},new float[]{0,0.7f,1})));

        RLParticle rlParticle1 = new RLParticle(level());
        rlParticle1.config.setDuration(20);
        rlParticle1.config.setStartLifetime(NumberFunction.constant(20));
        rlParticle1.config.setStartSize(new NumberFunction3(9));
        rlParticle1.config.setStartColor(new Gradient(new GradientColor(0XCFB2F5FF)));
        rlParticle1.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst1 = new EmissionSetting.Burst(); burst1.setCount(NumberFunction.constant(1));
        rlParticle1.config.getEmission().addBursts(burst1);
        rlParticle1.config.getShape().setShape(new Dot());
        rlParticle1.config.getMaterial().setMaterial(TBSMaterialHandle.ROS_RING.create());
        rlParticle1.config.getRenderer().setRenderMode(RendererSetting.Particle.Mode.Horizontal);
        rlParticle1.config.getLights().open();
        rlParticle1.config.getColorOverLifetime().open();
        rlParticle1.config.getColorOverLifetime().setColor(toAlpha);
        rlParticle1.config.getSizeOverLifetime().open();
        rlParticle1.config.getSizeOverLifetime().setSize(new NumberFunction3(new Line(new float[]{0,0.33f,1},new float[]{0,0.7f,1})));

        RLParticle rlParticle2 = new RLParticle(level());
        rlParticle2.config.setDuration(20);
        rlParticle2.config.setStartLifetime(NumberFunction.constant(20));
        rlParticle2.config.setStartSpeed(new RandomConstant(0.4,7,true));
        rlParticle2.config.setStartColor(new Gradient(new GradientColor(0XFFB2F5FF)));
        rlParticle2.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst2 = new EmissionSetting.Burst(); burst2.setCount(NumberFunction.constant(20));burst2.time=3;
        rlParticle2.config.getEmission().addBursts(burst2);
        Circle circle2 = new Circle();circle2.setRadius(6f);circle2.setRadiusThickness(0.4f);
        rlParticle2.config.getShape().setShape(circle2);
        rlParticle2.config.getMaterial().setMaterial(TBSMaterialHandle.ROS_SAN.create());
        rlParticle2.config.getRenderer().setBloomEffect(true);
        rlParticle2.config.getLights().open();
        rlParticle2.config.getColorOverLifetime().open();
        rlParticle2.config.getColorOverLifetime().setColor(new Gradient(GradientHandle.CENTER_OPAQUE));
        rlParticle2.config.getRotationOverLifetime().open();
        rlParticle2.config.getRotationOverLifetime().setYaw(new RandomLine(new float[]{0,1},new float[]{0,60},new float[]{0,30}));
        rlParticle2.config.getUvAnimation().open();
        rlParticle2.config.getUvAnimation().setTiles(new Range(2,2));
        rlParticle2.config.getUvAnimation().setAnimation(UVAnimationSetting.Animation.SingleRow);
        rlParticle2.config.getVelocityOverLifetime().open();
        rlParticle2.config.getVelocityOverLifetime().setLinear(new NumberFunction3(NumberFunction.constant(0),new RandomConstant(1,0.5,true),NumberFunction.constant(0)));

        RLParticle rlParticle3 = new RLParticle(level());
        rlParticle3.config.setDuration(20);
        rlParticle3.config.setStartLifetime(NumberFunction.constant(20));
        rlParticle3.config.setStartSpeed(NumberFunction.constant(20));
        rlParticle3.config.setStartColor(new Gradient(new GradientColor(0X8FB2F5FF)));
        rlParticle3.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst3 = new EmissionSetting.Burst(); burst3.setCount(NumberFunction.constant(30));burst3.time=3;
        rlParticle3.config.getEmission().addBursts(burst3);
        Circle circle3 = new Circle();circle3.setRadius(0.5f);circle3.setRadiusThickness(1f);
        rlParticle3.config.getShape().setShape(circle3);
        rlParticle3.config.getMaterial().setMaterial(MaterialHandle.SMOKE.create());
        rlParticle3.config.getColorOverLifetime().open();
        rlParticle3.config.getColorOverLifetime().setColor(new Gradient(GradientHandle.CENTER_OPAQUE));
        rlParticle3.config.getPhysics().open();
        rlParticle3.config.getPhysics().setHasCollision(false);
        rlParticle3.config.getPhysics().setFriction(NumberFunction.constant(0.9));
        rlParticle3.config.getUvAnimation().open();
        rlParticle3.config.getUvAnimation().setTiles(new Range(2,2));
        rlParticle3.config.getUvAnimation().setAnimation(UVAnimationSetting.Animation.SingleRow);

        BlockEffect blockEffect = new BlockEffect(level(), pos);
        rlParticle.emmit(blockEffect);
        rlParticle1.emmit(blockEffect);
        rlParticle2.emmit(blockEffect);
        rlParticle3.emmit(blockEffect);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return GuerrillasEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 100.0D)
                .add(Attributes.ATTACK_DAMAGE, 1.0f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1)
                .add(Attributes.FOLLOW_RANGE, 64)
                .add(Attributes.ARMOR, 10.0f);
    }
}
