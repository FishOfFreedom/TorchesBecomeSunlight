package com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster;

import com.freefish.rosmontislib.client.particle.advance.base.particle.RLParticle;
import com.freefish.rosmontislib.client.particle.advance.data.EmissionSetting;
import com.freefish.rosmontislib.client.particle.advance.data.UVAnimationSetting;
import com.freefish.rosmontislib.client.particle.advance.data.material.MaterialHandle;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction3;
import com.freefish.rosmontislib.client.particle.advance.data.number.RandomConstant;
import com.freefish.rosmontislib.client.particle.advance.data.number.color.Gradient;
import com.freefish.rosmontislib.client.particle.advance.data.number.curve.RandomLine;
import com.freefish.rosmontislib.client.particle.advance.data.shape.Cone;
import com.freefish.rosmontislib.client.particle.advance.data.shape.Dot;
import com.freefish.rosmontislib.client.particle.advance.effect.BlockEffect;
import com.freefish.rosmontislib.client.utils.GradientColor;
import com.freefish.rosmontislib.client.utils.Range;
import com.freefish.torchesbecomesunlight.compat.rosmontis.EntityPosToPosEffect;
import com.freefish.torchesbecomesunlight.compat.rosmontis.TBSMaterialHandle;
import com.freefish.torchesbecomesunlight.server.effect.forceeffect.ForceEffectHandle;
import com.freefish.torchesbecomesunlight.server.effect.forceeffect.ForceEffectInstance;
import com.freefish.torchesbecomesunlight.server.entity.ITeamMemberStorage;
import com.freefish.torchesbecomesunlight.server.entity.ai.FFBodyRotationControl;
import com.freefish.torchesbecomesunlight.server.entity.ai.FFPathNavigateGround;
import com.freefish.torchesbecomesunlight.server.entity.ai.entity.YetiIcecleaverAttackAI;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.GuerrillasEntity;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.ai.FollowLeaderGoal;
import com.freefish.torchesbecomesunlight.server.init.SoundHandle;
import com.freefish.torchesbecomesunlight.server.util.FFEntityUtils;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationAct;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.ArrayList;
import java.util.List;

public class YetiIcecleaver extends GuerrillasEntity implements ITeamMemberStorage<FrostNova> {
    public static final AnimationAct<YetiIcecleaver> HEAVY_ATTACK = new AnimationAct<YetiIcecleaver>("heavy_attack",36){

        @Override
        public void tickUpdate(YetiIcecleaver entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            LivingEntity target = entity.getTarget();
            
            if (target != null) {
                entity.getLookControl().setLookAt(target, 30F, 30F);
                entity.lookAt(target, 30F, 30F);
            } else {
                entity.setYRot(entity.yRotO);

            }
            if(tick==20){
                entity.dashForward(4,0);
            }
            if(tick == 21)
                entity.playSound(SoundHandle.AXE_SWEPT.get(), 1.0F, 1.0F / (entity.random.nextFloat() * 0.4F + 0.8F));
            else if (tick == 22) {
                entity.doRangeAttack(5,20,damage,true);
                entity.doRangeKnockBack(5.5,140,1);
            }
        }
    };
    public static final AnimationAct<YetiIcecleaver> HEAVY_ATTACK2 = new AnimationAct<YetiIcecleaver>("heavy_attack2",36){

        @Override
        public void tickUpdate(YetiIcecleaver entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            LivingEntity target = entity.getTarget();
            if (target != null) {
                entity.getLookControl().setLookAt(target, 30F, 30F);
                entity.lookAt(target, 30F, 30F);
            } else {
                entity.setYRot(entity.yRotO);

            }
            if(tick==17){
                entity.dashForward(4,0);
            }
            if(tick == 18)
                entity.playSound(SoundHandle.AXE_SWEPT.get(), 1.0F, 1.0F / (entity.random.nextFloat() * 0.4F + 0.8F));
            else if (tick == 19) {
                entity.doRangeAttack(4,90,damage,true);
            }
        }
    };
    public static final AnimationAct<YetiIcecleaver> ATTACK2 = new AnimationAct<YetiIcecleaver>("attak2",56){

        @Override
        public void tickUpdate(YetiIcecleaver entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            LivingEntity target = entity.getTarget();
            if (target != null) {
                entity.getLookControl().setLookAt(target, 30F, 30F);
                entity.lookAt(target, 30F, 30F);
            } else {
                entity.setYRot(entity.yRotO);
            }
            if (tick == 45) {
                entity.dashForward(8,0);
            }
            if (tick == 10) {
                entity.dashForward(4,0);
            }

            if(tick == 12)
                entity.doRangeAttack(3,20,damage,true);
            else if (tick == 27) {
                entity.doRangeAttack(4,90,damage,true);
            }
            else if (tick == 45) {
                entity.doRangeAttack(5.5,20,damage,true);
            }
        }
    };
    public static final AnimationAct<YetiIcecleaver> RUN = new AnimationAct<YetiIcecleaver>("run",200){
        @Override
        public void start(YetiIcecleaver entity) {
            if(entity.getTarget()!=null)
                entity.getNavigation().moveTo(entity.getTarget(),0.8);
        }

        @Override
        public void tickUpdate(YetiIcecleaver entity) {
            LivingEntity target = entity.getTarget();
            if(target!=null) {
                entity.getLookControl().setLookAt(target, 30F, 30F);
                entity.lookAt(target, 30F, 30F);

                if(entity.distanceToSqr(entity)<(3+target.getBbWidth()/2)*(3+target.getBbWidth()/2)){
                    stop(entity);
                }
            }
        }
    };
    public static final AnimationAct<YetiIcecleaver> SKILL = new AnimationAct<YetiIcecleaver>("skill",45){

        @Override
        public void tickUpdate(YetiIcecleaver entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            LivingEntity target = entity.getTarget();
            if (target != null) {
                entity.getLookControl().setLookAt(target, 30F, 30F);
                entity.lookAt(target, 30F, 30F);
            } else {
                entity.setYRot(entity.yRotO);

            }
            if(tick == 29){
                entity.dashForward(4,0);
            }

            if(tick == 31)
                entity.playSound(SoundHandle.AXE_SWEPT.get(), 1.0F, 1.0F / (entity.random.nextFloat() * 0.4F + 0.8F));
            else if (tick == 32) {
                entity.doRangeAttack(4,90,damage,true);
            }
        }
    };
    public static final AnimationAct<YetiIcecleaver> REMOTE_ICE = new AnimationAct<YetiIcecleaver>("remote_ice",22){

        @Override
        public void tickUpdate(YetiIcecleaver entity) {
            int tick = entity.getAnimationTick();
            LivingEntity target = entity.getTarget();
            if (target != null&&tick<5) {
                entity.getLookControl().setLookAt(target, 30F, 30F);
                entity.lookAt(target, 30F, 30F);
            } else {
                entity.setYRot(entity.yRotO);
            }

            if(tick == 9)
                entity.sendRemoteIce(entity.position(),entity.getYRot());

        }
    };
    public static final AnimationAct<YetiIcecleaver> BACK = new AnimationAct<YetiIcecleaver>("back",25){

        @Override
        public void tickUpdate(YetiIcecleaver entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            LivingEntity target = entity.getTarget();
            if (target != null) {
                entity.getLookControl().setLookAt(target, 30F, 30F);
                entity.lookAt(target, 30F, 30F);
            } else {
                entity.setYRot(entity.yRotO);

            }
            if(tick == 22)
                entity.playSound(SoundHandle.AXE_SWEPT.get(), 1.0F, 1.0F / (entity.random.nextFloat() * 0.4F + 0.8F));
            else if (tick == 24) {
                entity.doRangeAttack(5.5,140,damage,true);
            }
        }
    };
    public static final AnimationAct<YetiIcecleaver> ACT_TO_IDLE = new AnimationAct<YetiIcecleaver>("acttoidle",20){

        @Override
        public void tickUpdate(YetiIcecleaver entity) {
            entity.locateEntity();
        }
    };
    public static final AnimationAct<YetiIcecleaver> IDLE_TO_ACT = new AnimationAct<YetiIcecleaver>("idletoact",21){
        @Override
        public void tickUpdate(YetiIcecleaver entity) {
            entity.locateEntity();
        }
    };

    private Vec3 remoteIcePos;
    private float remoteIceAngle;
    private boolean isRemoteIce;
    private int remoteIceTick;
    private int noActMode;

    private static final EntityDataAccessor<CompoundTag> TARGET_POSY = SynchedEntityData.defineId(YetiIcecleaver.class, EntityDataSerializers.COMPOUND_TAG);

    private static AnimationAct[] ANIMATIONS = new AnimationAct[]{NO_ANIMATION,HEAVY_ATTACK,HEAVY_ATTACK2,RUN,ATTACK2,SKILL,
    REMOTE_ICE,BACK,ACT_TO_IDLE,IDLE_TO_ACT};

    public YetiIcecleaver(EntityType<? extends GuerrillasEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public AnimationAct[] getAnimations() {
        return ANIMATIONS;
    }

    @Override
    public boolean doHurtEntity(LivingEntity livingEntity, DamageSource source, float damage) {
        if(getAnimation()==ATTACK2){
            livingEntity.invulnerableTime = 5;
        }
        if(getAnimation()==SKILL){
            livingEntity.invulnerableTime = 0;
            ForceEffectInstance forceEffect = ForceEffectHandle.getForceEffect(livingEntity, ForceEffectHandle.FROZEN_FORCE_EFFECT);
            if(forceEffect!=null&&forceEffect.getLevel()>=2){
                damage*=5;
                forceEffect.discard(livingEntity);
            }
        }
        return super.doHurtEntity(livingEntity, source, damage);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TARGET_POSY,new CompoundTag());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        super.onSyncedDataUpdated(pKey);
        if(pKey==TARGET_POSY){
            CompoundTag compoundTag = this.entityData.get(TARGET_POSY);
            Vec3 pos = new Vec3(compoundTag.getFloat("x"),compoundTag.getFloat("y"),compoundTag.getFloat("z"));
            float angel = compoundTag.getFloat("angel");
            sendRemoteIce(pos,angel);
        }
    }

    public void sendRemoteIce(Vec3 remoteIcePos,float remoteIceAngle){
        isRemoteIce = true;
        remoteIceTick = 0;
        this.remoteIcePos = remoteIcePos;
        this.remoteIceAngle = remoteIceAngle;
        if(!level().isClientSide){
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putFloat("x", (float) remoteIcePos.x);
            compoundTag.putFloat("y", (float) remoteIcePos.y);
            compoundTag.putFloat("z", (float) remoteIcePos.z);
            compoundTag.putFloat("angel", (float) remoteIceAngle);
            this.entityData.set(TARGET_POSY, compoundTag);
        }
    }

    @Override
    public AnimationAct getDeathAnimation() {
        return null;
    }

    @Override
    protected BodyRotationControl createBodyControl() {
        return new FFBodyRotationControl(this);
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        return new FFPathNavigateGround(this,level());
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new YetiIcecleaverAttackAI(this));

        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(7, new FollowLeaderGoal<>(this, 0.3, 8, 12));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this , 0.3));

        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Zombie.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Ravager.class, true));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    @Override
    public float getStepHeight() {
        return 1.2f;
    }

    @Override
    public void tick() {
        super.tick();

        if(!level().isClientSide&&tickCount%2==0){
            if(getTarget()!=null&&getTarget().isAlive()){
                noActMode = 0;
            }else {
                noActMode++;
                if(noActMode==80){
                    if(isAggressive()){
                        AnimationActHandler.INSTANCE.sendAnimationMessage(this,YetiIcecleaver.ACT_TO_IDLE);
                    }
                    this.getNavigation().stop();
                    setAggressive(false);
                }
            }
        }

        if(isRemoteIce&&!level().isClientSide){
            remoteIceTick++;
            float arc = 40;
            List<LivingEntity> entitiesEffect = new ArrayList<>();
            List<LivingEntity> entitiesHit = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().move(remoteIcePos.subtract(position())).inflate(8, 3, 8), e -> e != this && distanceToSqr(remoteIcePos) <= (7 + e.getBbWidth() / 2f)*(7 + e.getBbWidth() / 2f)
                    && e.getY() <= getY() + 3);
            for (LivingEntity entityHit : entitiesHit) {
                float entityHitAngle = (float) ((Math.atan2(entityHit.getZ() - remoteIcePos.z, entityHit.getX() - remoteIcePos.x) * (180 / Math.PI) - 90) % 360);
                float entityAttackingAngle = remoteIceAngle % 360;
                if (entityHitAngle < 0) {
                    entityHitAngle += 360;
                }
                if (entityAttackingAngle < 0) {
                    entityAttackingAngle += 360;
                }
                float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
                float entityHitDistance = (float) Math.sqrt((entityHit.getZ() - remoteIcePos.z) * (entityHit.getZ() - remoteIcePos.z) + (entityHit.getX() - remoteIcePos.x) * (entityHit.getX() - remoteIcePos.x)) - entityHit.getBbWidth() / 2f;
                if (entityHitDistance <= 7 && (entityRelativeAngle <= arc / 2 && entityRelativeAngle >= -arc / 2) || (entityRelativeAngle >= 360 - arc / 2 || entityRelativeAngle <= -360 + arc / 2)) {
                    entitiesEffect.add(entityHit);
                }
            }

            if(remoteIceTick<40){
                entitiesEffect.forEach((e)->{
                    e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,40));
                });
            }else {
                entitiesEffect.forEach((e)->{
                    ForceEffectHandle.addForceEffect(e,new ForceEffectInstance(ForceEffectHandle.FROZEN_FORCE_EFFECT,2,120));
                });
            }

            if(remoteIceTick>80){
                isRemoteIce = false;
            }
        }

        if(getAnimation()==REMOTE_ICE){
            int animationTick = getAnimationTick();
            if(level().isClientSide){
                if(animationTick==11){
                    yetiWindFX();
                }
            }
        } else if(getAnimation()==SKILL){
            int animationTick = getAnimationTick();
            if(level().isClientSide){
                if(animationTick==11){
                    yetiSkillLightFX();
                }
            }
        }
    }

    public void yetiWindFX(){
        RLParticle rlParticle1 = new RLParticle(level());
        rlParticle1.config.setDuration(100);
        rlParticle1.config.setStartLifetime(NumberFunction.constant(100));
        rlParticle1.config.setStartSpeed(new RandomConstant(0.4,7,true));
        rlParticle1.config.setStartSize(new NumberFunction3(0.1));
        rlParticle1.config.setStartColor(new Gradient(new GradientColor(0XFFFFFFFF)));
        rlParticle1.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst = new EmissionSetting.Burst(); burst.setCount(NumberFunction.constant(40));
        rlParticle1.config.getEmission().addBursts(burst);

        rlParticle1.config.getMaterial().setMaterial(TBSMaterialHandle.ROS_SAN.create());

        Cone circle1 = new Cone();circle1.setRadius(0.1f);circle1.setAngle(25);
        rlParticle1.config.getShape().setShape(circle1);
        rlParticle1.config.getShape().setRotation(new NumberFunction3(90,0,0));
        rlParticle1.config.getPhysics().open();
        rlParticle1.config.getPhysics().setFriction(NumberFunction.constant(0.95));
        rlParticle1.config.getPhysics().setHasCollision(false);
        rlParticle1.config.getLights().open();
        rlParticle1.config.getColorOverLifetime().open();
        rlParticle1.config.getColorOverLifetime().setColor(new Gradient(new GradientColor(0XFFFFFFFF,0XFFFFFFFF,0XFFFFFFFF,0XFFFFFFFF,0X00FFFFFF)));
        rlParticle1.config.getRotationOverLifetime().open();
        rlParticle1.config.getRotationOverLifetime().setRoll(new RandomLine(new float[]{0,1},new float[]{180,360},new float[]{-180,-360}));

        rlParticle1.config.getUvAnimation().open();
        rlParticle1.config.getUvAnimation().setTiles(new Range(2,2));
        rlParticle1.config.getUvAnimation().setAnimation(UVAnimationSetting.Animation.SingleRow);

        RLParticle rlParticle2 = new RLParticle(level());
        rlParticle2.config.setDuration(100);
        rlParticle2.config.setStartLifetime(NumberFunction.constant(100));
        rlParticle2.config.setStartSpeed(new RandomConstant(0.4,7,true));
        rlParticle2.config.setStartSize(new NumberFunction3(1.5));
        rlParticle2.config.setStartColor(new Gradient(new GradientColor(0X7FFFFFFF)));
        rlParticle2.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst2 = new EmissionSetting.Burst(); burst2.setCount(NumberFunction.constant(30));
        rlParticle2.config.getEmission().addBursts(burst2);

        rlParticle2.config.getMaterial().setMaterial(MaterialHandle.SMOKE.create());

        Cone circle2 = new Cone();circle2.setRadius(0.5f);circle2.setAngle(15);
        rlParticle2.config.getShape().setShape(circle2);
        rlParticle2.config.getShape().setRotation(new NumberFunction3(90,0,0));
        rlParticle2.config.getPhysics().open();
        rlParticle2.config.getPhysics().setFriction(NumberFunction.constant(0.95));
        rlParticle2.config.getPhysics().setHasCollision(false);
        rlParticle2.config.getLights().open();
        rlParticle2.config.getColorOverLifetime().open();
        rlParticle2.config.getColorOverLifetime().setColor(new Gradient(new GradientColor(0X00FFFFFF,0XFFFFFFFF,0X00FFFFFF)));

        rlParticle2.config.getUvAnimation().open();
        rlParticle2.config.getUvAnimation().setTiles(new Range(2,2));
        rlParticle2.config.getUvAnimation().setAnimation(UVAnimationSetting.Animation.SingleRow);

        BlockEffect blockEffect = new BlockEffect(level(), FFEntityUtils.getBodyRotVec(this, new Vec3(-0.5, 2, 2)));
        rlParticle2.updateRotation(new Vector3f(0,(float) (-this.getYRot() / 180 * Math.PI),0));
        rlParticle1.updateRotation(new Vector3f(0,(float) (-this.getYRot() / 180 * Math.PI),0));

        rlParticle2.emmit(blockEffect);
        rlParticle1.emmit(blockEffect);
    }

    public void yetiSkillLightFX(){
        RLParticle rlParticle1 = new RLParticle(level());
        rlParticle1.config.setDuration(10);
        rlParticle1.config.setStartLifetime(NumberFunction.constant(20));
        rlParticle1.config.setStartSize(new NumberFunction3(1.5,0.1,0.1));
        rlParticle1.config.setStartColor(new Gradient(new GradientColor(0XFFFFFFFF)));
        rlParticle1.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst = new EmissionSetting.Burst(); burst.setCount(NumberFunction.constant(1));
        rlParticle1.config.getEmission().addBursts(burst);

        rlParticle1.config.getMaterial().setMaterial(MaterialHandle.CIRCLE.create());

        rlParticle1.config.getShape().setShape(new Dot());
        rlParticle1.config.getLights().open();
        rlParticle1.config.getRenderer().setBloomEffect(true);
        rlParticle1.config.getColorOverLifetime().open();
        rlParticle1.config.getColorOverLifetime().setColor(new Gradient(new GradientColor(0X00FFFFFF,0XFFFFFFFF,0X00FFFFFF)));

        RLParticle rlParticle2 = new RLParticle(level());
        rlParticle2.config.setDuration(10);
        rlParticle2.config.setStartLifetime(NumberFunction.constant(20));
        rlParticle2.config.setStartSize(new NumberFunction3(0.3));
        rlParticle2.config.setStartColor(new Gradient(new GradientColor(0XFFFFFFFF)));
        rlParticle2.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst1 = new EmissionSetting.Burst(); burst1.setCount(NumberFunction.constant(1));
        rlParticle2.config.getEmission().addBursts(burst1);

        rlParticle2.config.getMaterial().setMaterial(MaterialHandle.CIRCLE.create());

        rlParticle2.config.getShape().setShape(new Dot());
        rlParticle2.config.getLights().open();
        rlParticle1.config.getRenderer().setBloomEffect(true);
        rlParticle2.config.getColorOverLifetime().open();
        rlParticle2.config.getColorOverLifetime().setColor(new Gradient(new GradientColor(0X00FFFFFF,0XFFFFFFFF,0X00FFFFFF)));

        EntityPosToPosEffect blockEffect = new EntityPosToPosEffect(level(),this,  new Vec3(-0.1*0.75, 1.71*0.75, 1.01*0.75),new Vec3(2.63*0.75, 0.55*0.75, 2.01*0.75));
        EntityPosToPosEffect blockEffect1 = new EntityPosToPosEffect(level(),this, new Vec3(-0.1*0.75, 1.71*0.75, 1.01*0.75),new Vec3(2.63*0.75, 0.55*0.75, 2.01*0.75));

        rlParticle1.emmit(blockEffect);
        rlParticle2.emmit(blockEffect1);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return GuerrillasEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 80.0D)
                .add(Attributes.ATTACK_DAMAGE, 10.0f)
                .add(Attributes.ARMOR, 10.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 48)
                .add(Attributes.KNOCKBACK_RESISTANCE,1f);
    }

    @Override
    protected <T extends GeoEntity> void basicAnimation(AnimationState<T> event) {
        if(isAggressive()){
            if (event.isMoving())
                event.setAnimation(RawAnimation.begin().thenLoop("actwalk"));
            else
                event.setAnimation(RawAnimation.begin().thenLoop("actidle"));
        }
        else {
            if (event.isMoving())
                event.setAnimation(RawAnimation.begin().thenLoop("walk_peace"));
            else
                event.setAnimation(RawAnimation.begin().thenLoop("idle"));
        }
    }

    private FrostNova leader;

    @Override
    public FrostNova getLeader() {
        return leader;
    }

    @Override
    public void setLeader(FrostNova leader) {
        this.leader = leader;
    }
}
