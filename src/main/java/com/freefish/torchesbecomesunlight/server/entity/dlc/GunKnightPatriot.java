package com.freefish.torchesbecomesunlight.server.entity.dlc;

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
import com.freefish.rosmontislib.client.particle.advance.data.shape.Cone;
import com.freefish.rosmontislib.client.particle.advance.data.shape.Dot;
import com.freefish.rosmontislib.client.particle.advance.data.shape.Sphere;
import com.freefish.rosmontislib.client.particle.advance.effect.BlockEffect;
import com.freefish.rosmontislib.client.particle.advance.effect.EntityEffect;
import com.freefish.rosmontislib.client.utils.GradientColor;
import com.freefish.rosmontislib.client.utils.Range;

import com.freefish.torchesbecomesunlight.client.util.particle.ParticleCloud;
import com.freefish.torchesbecomesunlight.compat.rosmontis.GeoBoneEffect;
import com.freefish.torchesbecomesunlight.compat.rosmontis.TBSMaterialHandle;
import com.freefish.torchesbecomesunlight.server.entity.ITwoStateEntity;
import com.freefish.torchesbecomesunlight.server.entity.ai.entity.HalberdKnightPatriotAttackAI;
import com.freefish.torchesbecomesunlight.server.init.ParticleHandler;
import com.freefish.torchesbecomesunlight.client.util.particle.util.AdvancedParticleBase;
import com.freefish.torchesbecomesunlight.client.util.particle.util.ParticleComponent;
import com.freefish.torchesbecomesunlight.client.util.particle.util.RibbonComponent;
import com.freefish.torchesbecomesunlight.server.config.ConfigHandler;
import com.freefish.torchesbecomesunlight.server.entity.AnimatedEntity;
import com.freefish.torchesbecomesunlight.server.entity.ai.*;
import com.freefish.torchesbecomesunlight.server.entity.ai.entity.GunKnightPatriotAttackAI;
import com.freefish.torchesbecomesunlight.server.entity.effect.EntityCameraShake;
import com.freefish.torchesbecomesunlight.server.entity.effect.FXEntity;
import com.freefish.torchesbecomesunlight.server.entity.IDialogueEntity;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.GuerrillasEntity;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster.FrostNova;
import com.freefish.torchesbecomesunlight.server.entity.projectile.Bullet;
import com.freefish.torchesbecomesunlight.server.init.SoundHandle;
import com.freefish.torchesbecomesunlight.server.story.dialogue.Dialogue;
import com.freefish.torchesbecomesunlight.server.story.dialogue.DialogueStore;
import com.freefish.torchesbecomesunlight.server.util.FFEntityUtils;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationAct;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import com.freefish.torchesbecomesunlight.server.util.bossbar.CustomBossInfoServer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;
import java.util.*;

import static com.freefish.torchesbecomesunlight.server.entity.dlc.GunKnightPatriotAnimations.*;

public class GunKnightPatriot extends AnimatedEntity implements IDialogueEntity, RangedAttackMob , IEntityAdditionalSpawnData, ITwoStateEntity {

    @OnlyIn(Dist.CLIENT)
    public GeoBone halberd;
    @OnlyIn(Dist.CLIENT)
    public GeoBone halberd1;

    private static final AnimationAct[] ANIMATIONS = {
            NO_ANIMATION,SKILL_HALBERD_LIAN,SKILL_HALBERD_10,WIND_MILL,REMOTE_HALBERD_THROW,REMOTE_HALBERD_RL2,MOVE_HALBERD_BACK,SKILL_HALBERD_2,ACK_HALBERD_R,ACK_HALBERD_L,ACK_HALBERD_CR,ACK_HALBERD_CL,RACK_HALBERD_CHI, LACK_HALBERD_DOWNCHI,LACK_HALBERD_TIAOWIND
            ,RACK_HALBERD_HEAVY,RACK_HALBERD_CYCLE2,ACK_HALBERD_CHI3,ACK_HALBERD_CHILEFT,MOVE_HALBERD_LEFT,MOVE_HALBERD_RIGHT,
            MOVE_HALBERD_CYCLE,REMOTE_HALBERD_RZHOU,REMOTE_HALBERD_SUMMON1,SKILL_HALBERD_11,SKILL_HALBERD_12,SKILL_HALBERD_13,
            REMOTE_HALBERD_SUMMON,MOVE_HALBERD_CYCLE1
            ,SKILL_START,SUMMON_CHENG,SUMMON_TURRET,ALL_SHOT,RELOAD
            ,GUN1TO2,GUN1TO3,GUN3TO1,GUN2TO1,ATTACK1,ATTACK2,ATTACK3,SHIELD,STATE_2,STOMP,ARTILLERY_1,SHOTGUN_1,MACHINE_GUN_1,SKILL_LOOP,SKILL_END,DIE
    };

    private static ParticleComponent.KeyTrack SUN = new ParticleComponent.KeyTrack(new float[] {0,1,1,0}, new float[] {0,0.25f,0.75f, 1});

    private static final EntityDataAccessor<Float> TARGET_POSX = SynchedEntityData.defineId(GunKnightPatriot.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TARGET_POSY = SynchedEntityData.defineId(GunKnightPatriot.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TARGET_POSZ = SynchedEntityData.defineId(GunKnightPatriot.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> IS_GLOWING = SynchedEntityData.defineId(GunKnightPatriot.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> GUN_MODE = SynchedEntityData.defineId(GunKnightPatriot.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_RUN = SynchedEntityData.defineId(GunKnightPatriot.class, EntityDataSerializers.BOOLEAN);

    @OnlyIn(Dist.CLIENT)
    public Vec3[] clientVectors;

    private final AnimationController<GunKnightPatriot> animationController1 = new AnimationController<GunKnightPatriot>(this, "HandController", 5, this::basicHandAnimation);
    private final AnimationController<GunKnightPatriot> animationController2 = new AnimationController<GunKnightPatriot>(this, "GunController", 5, this::basicGunAnimation);
    private final AnimationController<GunKnightPatriot> animationController3 = new AnimationController<GunKnightPatriot>(this, "Wind", 5, this::wingAnimation);

    private Map<LivingEntity,DefendCounter> defendCounterMap = new HashMap<>();

    public int time=0;
    public int normalAttackTime = -1;
    private LivingEntity dialogueLivingEntity;
    private final List<DemonCounter> demonCounterList = new ArrayList<>();
    public boolean isCanBeAttacking = false;
    private State spawnState = State.NATURE;
    private int holyBulletAmount = 0;

    private final CustomBossInfoServer bossInfo= new CustomBossInfoServer(this,4);

    public GunKnightPatriot(EntityType<? extends GunKnightPatriot> entityType, Level level) {
        super(entityType, level);
        if (level().isClientSide)
            clientVectors = new Vec3[] {new Vec3(0, 0, 0),new Vec3(0, 0, 0)};
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new GunKnightPatriotAttackAI(this));
        this.goalSelector.addGoal(2, new HalberdKnightPatriotAttackAI(this));

        //this.goalSelector.addGoal(7, new FFLookAtPlayerGoal<>(this, Player.class, 8.0F));
        //this.goalSelector.addGoal(6, new FFRandomLookAroundGoal<>(this));
        this.goalSelector.addGoal(8, new FFWaterAvoidingRandomStrollGoal<>(this , 0.33));

        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Zombie.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Ravager.class, true));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    @Override
    public void tick() {
        super.tick();

        for(int i = 0;i<demonCounterList.size();i++){
            DemonCounter demonCounter = demonCounterList.get(i);
            demonCounter.update(level(),this);

            if(demonCounter.disappear()){
                demonCounterList.remove(i);
                i -= 1;
            }
        }

        if (tickCount % 4 == 0) bossInfo.update();

        LivingEntity target = this.getTarget();

        if(getAnimation()!=ATTACK1&&getAnimation()!=ATTACK3)
            repelEntities(1.7F, 4.5f, 1.7F, 1.7F);

        if(!level().isClientSide){
            Iterator<Map.Entry<LivingEntity, DefendCounter>> it = defendCounterMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<LivingEntity, DefendCounter> item = it.next();
                LivingEntity key = item.getKey();
                if(key == null||!key.isAlive()){
                    it.remove();
                }
                if(item.getValue().effectiveAttackTick>0){
                    item.getValue().effectiveAttackTick-=1;
                }
            }
        }
        else {
            if(isGlowing()){
                if(random.nextBoolean()){
                    Vec3 pos = new Vec3(0, 0, random.nextFloat() * 2.7).yRot(6.28f * random.nextFloat());
                    AdvancedParticleBase.spawnParticle(level(), ParticleHandler.SUN.get(), getX() + pos.x, this.getY() + 0.5F + random.nextFloat() * 2, getZ() + pos.z, 0, 0.05 + random.nextFloat() * 0.05, 0, true, 0, 0, 0, 0, 2 + random.nextFloat(), 1, 0.8, 0, 1, 1, 40, true, false, new ParticleComponent[]{
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, SUN, false),
                    });
                }
            }

            if(getSpawnState()==State.TWO){
                if(tickCount%90==0){
                    normalLightParticle();
                }
            }
        }

        float moveX = (float) (getX() - xo);
        float moveZ = (float) (getZ() - zo);
        float speed = Mth.sqrt(moveX * moveX + moveZ * moveZ);
        if(this.level().isClientSide && speed > 0.03) {
            if (tickCount % 33 == 1 &&getAnimation() == NO_ANIMATION)
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundHandle.GIANT_STEP.get(), this.getSoundSource(), 20F, 1F, false);
        }

        doSummonCheng();
        doShootFX();
        doAllShotCheng();
        doSkillCheng();
        doState2();
        doSkillFeng();
        doSkillHalberd2();
        doTiaoWind();
        doSkillHalberd3();
        doMoveCycle();
        doMoveHalberdBack();
        doMoveHalberdDownChi();

        if(getTarget() instanceof FrostNova) setTarget(null);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        float limit = (float)(getMaxHealth()*ConfigHandler.COMMON.MOBS.PATRIOT.damageConfig.damageCap.get());
        if(amount>limit&&!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) amount = limit;
        Entity entitySource = source.getDirectEntity();
        AnimationAct animation = getAnimation();
        if(animation ==STATE_2||animation ==SKILL_HALBERD_2||animation ==MOVE_HALBERD_LEFT||animation ==MOVE_HALBERD_RIGHT) return false;

        if(getSpawnState()!=State.TWO){
            if (entitySource != null) {
                return attackWithShield(source, amount);
            } else if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                return super.hurt(source, amount);
            }
        }else {
            return attackWithDefendCounter(source,amount);
        }
        return false;
    }

    @Override
    public boolean doHurtEntity(LivingEntity livingEntity, DamageSource source, float damage) {
        if(defendCounterMap.containsKey(livingEntity)){
            DefendCounter defendCounter = defendCounterMap.get(livingEntity);
            defendCounter.effectiveAttackCounter -=1;
        }
        return super.doHurtEntity(livingEntity, source, damage);
    }

    @Override
    public AnimationAct getDeathAnimation() {
        if(getSpawnState()!=State.NATURE){
            return DIE;
        }else {
            setHealth(1);
            return STATE_2;
        }
    }

    public boolean attackWithShield(DamageSource source, float amount){
        Entity entitySource = source.getDirectEntity();
        if (entitySource != null) {
            if (!isCanBeAttacking&&isAggressive()&&ConfigHandler.COMMON.MOBS.GUN_KNIGHT.isFrontalAttack.get()) {
                int arc = 60;
                float entityHitAngle = (float) ((Math.atan2(entitySource.getZ() - getZ(), entitySource.getX() - getX()) * (180 / Math.PI) - 90) % 360);
                float entityAttackingAngle = getYRot() % 360;
                if (entityHitAngle < 0) {
                    entityHitAngle += 360;
                }
                if (entityAttackingAngle < 0) {
                    entityAttackingAngle += 360;
                }
                if(Math.abs(entityAttackingAngle-entityHitAngle)<arc) {
                    playSound(SoundEvents.SHIELD_BREAK,0.4f,2); //playSound(MMSounds.ENTITY_WROUGHT_UNDAMAGED.get(), 0.4F, 2);
                    if(getAnimation()==NO_ANIMATION&&entitySource == getTarget()&&entitySource.distanceTo(this)<3+getTarget().getBbWidth()/2)
                        AnimationActHandler.INSTANCE.sendAnimationMessage(this,SHIELD);
                    return false;
                }
                else
                    return super.hurt(source, amount);
            } else {
                playSound(SoundEvents.SHIELD_BLOCK,0.4f,2); //playSound(MMSounds.ENTITY_WROUGHT_UNDAMAGED.get(), 0.4F, 2);
                return super.hurt(source, amount);
            }
        }
        return false;
    }

    public boolean attackWithDefendCounter(DamageSource source, float amount){
        Entity entitySource = source.getDirectEntity();
        if (entitySource instanceof LivingEntity living) {
            int attackTime = 1;
            if(defendCounterMap.containsKey(living)){
                DefendCounter defendCounter = defendCounterMap.get(living);
                attackTime +=defendCounter.effectiveAttackCounter;

                if(defendCounter.effectiveAttackTick<=0){
                    defendCounter.effectiveAttackCounter+=1;
                    defendCounter.effectiveAttackTick = 20;
                }
            }else {
                defendCounterMap.put(living,new DefendCounter(1,20));
            }
            return super.hurt(source,amount/10*Math.max(1,attackTime));
        }
        return super.hurt(source,amount/20);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TARGET_POSX, 0f);
        this.entityData.define(TARGET_POSY, 0f);
        this.entityData.define(TARGET_POSZ, 0f);
        this.entityData.define(IS_GLOWING, false);
        this.entityData.define(GUN_MODE,0);
        this.entityData.define(IS_RUN,false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        Vector3f vector3f = getTargetPos();
        compound.putFloat("targetPosX",vector3f.x);
        compound.putFloat("targetPosY",vector3f.y);
        compound.putFloat("targetPosZ",vector3f.z);
        compound.putInt("gunmod", getGunMod());
        compound.putBoolean("aggressiveg", isGlowing());
        compound.putBoolean("isrun", isRun());
        addAdditionalSpawnState(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        float x = compound.getFloat("targetPosX");
        float y = compound.getFloat("targetPosY");
        float z = compound.getFloat("targetPosZ");
        setTargetPos(new Vec3(x,y,z));
        setGunMod(compound.getInt("gunmod"));
        setIsGlowing(compound.getBoolean("aggressiveg"));
        setIsRun(compound.getBoolean("isrun"));
        readAddAdditionalSpawnState(compound);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeUtf(getSpawnState().toString());
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        setSpawnState(State.valueOf(additionalData.readUtf()));
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public SoundEvent getBossMusic() {
        return SoundHandle.GUN_KNIGHT_MUSIC.get();
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false;
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new FFPathNavigateGround(this, level);
    }

    @Override
    @NotNull
    protected BodyRotationControl createBodyControl() {
        return new FFBodyRotationControl(this);
    }

    @Override
    public boolean canCollideWith(Entity pEntity) {
        return false;
    }

    @Override
    public float getStepHeight() {
        return 2F;
    }

    @Override
    public boolean hasBossBar() {
        return true;
    }

    @Override
    public BossEvent.BossBarColor bossBarColor() {
        return BossEvent.BossBarColor.WHITE;
    }

    @Override
    public void die(DamageSource pDamageSource) {
        super.die(pDamageSource);
        if (!this.isRemoved()) {
            bossInfo.update();
        }
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
        }
    }

    @Override
    public void setCustomName(Component name) {
        super.setCustomName(name);
        this.bossInfo.setName(this.getDisplayName());
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
    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    protected boolean canBePushedByEntity(Entity entity) {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean canBeLeashed(Player pPlayer) {
        return false;
    }

    @Override
    protected boolean canRide(Entity pVehicle) {
        return false;
    }

    @Override
    public void lookAt(Entity pEntity, float pMaxYRotIncrease, float pMaxXRotIncrease) {
        if(getSpawnState()!=State.TWO){
            double d0 = pEntity.getX() - this.getX();
            double d2 = pEntity.getZ() - this.getZ();
            double d1;
            if (pEntity instanceof LivingEntity livingentity) {
                d1 = livingentity.getEyeY() - (getY() + 1.75);
            } else {
                d1 = (pEntity.getBoundingBox().minY + pEntity.getBoundingBox().maxY) / 2.0D - this.getEyeY();
            }

            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            float f = (float) (Mth.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
            float f1 = (float) (-(Mth.atan2(d1, d3) * (double) (180F / (float) Math.PI)));
            this.setXRot(this.rotlerp(this.getXRot(), f1, pMaxXRotIncrease));
            this.setYRot(this.rotlerp(this.getYRot(), f, pMaxYRotIncrease));
        }
        else {
            super.lookAt(pEntity, pMaxYRotIncrease, pMaxXRotIncrease);
        }
    }

    private float rotlerp(float pAngle, float pTargetAngle, float pMaxIncrease) {
        float f = Mth.wrapDegrees(pTargetAngle - pAngle);
        if (f > pMaxIncrease) {
            f = pMaxIncrease;
        }

        if (f < -pMaxIncrease) {
            f = -pMaxIncrease;
        }

        return pAngle + f;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return GuerrillasEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 300.0D)
                .add(Attributes.ATTACK_DAMAGE, 20.0f)
                .add(Attributes.ARMOR, 20.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 10.0D)
                .add(Attributes.FOLLOW_RANGE, 48)
                .add(Attributes.KNOCKBACK_RESISTANCE,1f);
    }

    @Override
    public AnimationAct[] getAnimations() {
        return ANIMATIONS;
    }

    public void reloadHolyBullet(int amount){
        this.holyBulletAmount = amount;
    }

    public void consumeHolyBullet(int consume){
        if(this.holyBulletAmount>0){
            this.holyBulletAmount-=consume;
            if(this.holyBulletAmount<=0){
                setIsGlowing(false);
            }
        }
        else {
            setIsGlowing(false);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar event) {
        super.registerControllers(event);
        event.add(animationController1);
        event.add(animationController2);
        event.add(animationController3);
    }

    @Override
    protected <T extends GeoEntity> PlayState predicate(AnimationState<T> animationState) {
        AnimationAct animation = getAnimation();
        boolean flad = animation == MACHINE_GUN_1||animation == ARTILLERY_1||animation==SHOTGUN_1;
        if(getAnimation() != NO_ANIMATION&&!flad)
            animationState.setAnimation(getAnimation().getRawAnimation());
        else
            basicAnimation(animationState);
        return PlayState.CONTINUE;
    }

    @Override
    protected <T extends GeoEntity> void basicAnimation(AnimationState<T> event) {
        if(getSpawnState()!=State.TWO){
            if (isAggressive()) {
                if (event.isMoving())
                    event.getController().setAnimation(RawAnimation.begin().thenLoop("march"));
                else
                    event.getController().setAnimation(RawAnimation.begin().thenLoop("idle_aggressive"));
            } else {
                if (event.isMoving())
                    event.getController().setAnimation(RawAnimation.begin().thenLoop("walk_peace"));
                else
                    event.getController().setAnimation(RawAnimation.begin().thenLoop("idle_peace"));
            }
        }else {
            if(!isRun()){
                if (event.isMoving())
                    event.getController().setAnimation(RawAnimation.begin().thenLoop("walk_halberd"));
                else
                    event.getController().setAnimation(RawAnimation.begin().thenLoop("idle_halberd"));
            } else {
                event.getController().setAnimation(RawAnimation.begin().thenLoop("run_halberd"));
            }
        }
    }

    protected PlayState basicHandAnimation(AnimationState<GunKnightPatriot> event) {
        if(getSpawnState()==State.TWO) return PlayState.STOP;
        AnimationAct a = getAnimation();
        boolean flad = true;

        if(a==GUN1TO2||a==GUN1TO3||a==GUN3TO1||a==GUN2TO1
                ||a==ATTACK1||a==ATTACK2||a==ATTACK3
                ||a==SUMMON_TURRET||a==SUMMON_CHENG
                ||a==SKILL_START||a==SKILL_LOOP||a==SKILL_END
                ||a==ALL_SHOT||a==RELOAD||a==STATE_2){
            flad = false;
        }

        if (isAggressive()&&flad) {
            if(a==MACHINE_GUN_1) event.getController().setAnimation(MACHINE_GUN_1.getRawAnimation());
            else if(a==SHOTGUN_1) event.getController().setAnimation(SHOTGUN_1.getRawAnimation());
            else if(a==ARTILLERY_1) event.getController().setAnimation(ARTILLERY_1.getRawAnimation());
            else if (event.isMoving()) {
                event.getController().setAnimation(RawAnimation.begin().thenLoop("march_hand"));
            }
            else
                event.getController().setAnimation(RawAnimation.begin().thenLoop("idle_aggressive_hand"));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    protected PlayState basicGunAnimation(AnimationState<GunKnightPatriot> event) {
        if(getSpawnState()==State.TWO) return PlayState.STOP;
        AnimationAct a = getAnimation();
        boolean flad = true;

        if(a==MACHINE_GUN_1||a==SHOTGUN_1||a==ARTILLERY_1||a==GUN1TO2||a==GUN1TO3||a==GUN3TO1||a==GUN2TO1||a==SKILL_LOOP){
            flad = false;
        }

        if(flad) {
            if(getGunMod()==0)
                event.getController().setAnimation(RawAnimation.begin().thenLoop("gun_model_1"));
            else if(getGunMod()==1)
                event.getController().setAnimation(RawAnimation.begin().thenLoop("gun_model_2"));
            else if(getGunMod()==2)
                event.getController().setAnimation(RawAnimation.begin().thenLoop("gun_model_3"));
            return PlayState.CONTINUE;
        }
        else
            return PlayState.STOP;
    }

    protected PlayState wingAnimation(AnimationState<GunKnightPatriot> event) {
        if(getAnimation()!=ALL_SHOT){
            if (isGlowing()) {
                event.setAnimation(RawAnimation.begin().thenLoop("wing_glow"));
            } else {
                event.setAnimation(RawAnimation.begin().thenLoop("wing_idle"));
            }
            return PlayState.CONTINUE;
        }
        else
            return PlayState.STOP;
    }

    public void transGun(int gun){
        int gunMod = getGunMod();
        if(gunMod==gun) return;
        if(gun==0){
            if(gunMod==1)
                AnimationActHandler.INSTANCE.sendAnimationMessage(this,GUN2TO1);
            else
                AnimationActHandler.INSTANCE.sendAnimationMessage(this,GUN3TO1);
        }
        if(gun==1&&gunMod==0){
            AnimationActHandler.INSTANCE.sendAnimationMessage(this,GUN1TO2);
        }
        if(gun==2&&gunMod==0){
            AnimationActHandler.INSTANCE.sendAnimationMessage(this,GUN1TO3);
        }
    }

    public Vector3f getTargetPos() {
        float x = this.entityData.get(TARGET_POSX);
        float y = this.entityData.get(TARGET_POSY);
        float z = this.entityData.get(TARGET_POSZ);
        return new Vector3f(x,y,z);
    }

    public void setTargetPos(Vec3 vector3f) {
        this.entityData.set(TARGET_POSX, (float)vector3f.x);
        this.entityData.set(TARGET_POSY, (float)vector3f.y);
        this.entityData.set(TARGET_POSZ, (float)vector3f.z);
    }

    public boolean isGlowing() {
        return this.entityData.get(IS_GLOWING);
    }

    public void setIsGlowing(boolean vector3f) {
        this.entityData.set(IS_GLOWING, vector3f);
    }

    public int getGunMod() {
        return this.entityData.get(GUN_MODE);
    }

    public void setGunMod(int gunMod) {
        this.entityData.set(GUN_MODE,gunMod);
    }

    public boolean isRun() {
        return this.entityData.get(IS_RUN);
    }

    public void setIsRun(boolean gunMod) {
        this.entityData.set(IS_RUN,gunMod);
    }

    private void shootBlackSpearSkill(LivingEntity target,Vec3 vec3,int type) {
        if (target==null) return;
        Bullet abstractarrow = new Bullet(level(),this,type);
        abstractarrow.setPos(vec3);

        Vec3 motion = getTargetMoveVec(target).scale(0.5);

        double d0 = motion.x + target.getX() - vec3.x;
        double d1 = motion.y + target.getY(0.4D) - vec3.y;
        double d2 = motion.z + target.getZ() - vec3.z;
        abstractarrow.shoot(d0, d1 , d2, 3.5F, 1);
        abstractarrow.setIsHoly(isGlowing());
        this.level().addFreshEntity(abstractarrow);
    }

    public Vec3 getShootPos(){
        AnimationAct animation = getAnimation();
        float xRot = Mth.clamp(getXRot()%360,-30,30)* -0.017453292F;

        if(animation==MACHINE_GUN_1)
            return new Vec3(-1.1+random.nextFloat()*0.2, 1.75-random.nextFloat()*0.2, 2.5).xRot(xRot).yRot((float) (-getYRot() / 180 * Math.PI)).add(position());
        else if (animation==ARTILLERY_1) {
            return new Vec3(-0.76, 1.75, 3.3).yRot((float) (-getYRot() / 180 * Math.PI)).add(position());
        }else if (animation==SHOTGUN_1) {
            return new Vec3(-0.76, 1.8, 2.3).xRot(xRot).yRot((float) (-getYRot() / 180 * Math.PI)).add(position());
        }

        return new Vec3(-1, 1.75, 2.5).yRot((float) (-getYRot() / 180 * Math.PI)).add(position());
    }

    public void shootMachineBullet(LivingEntity target,Vec3 vec3) {
        if(target==null) return;
        float dist = distanceTo(target);

        float xRot = Mth.clamp(getXRot()%360,-30,30)* -0.017453292F;

        Bullet abstractarrow = new Bullet(level(),this,0);
        abstractarrow.setPos(vec3.add(0.1-random.nextFloat()*0.2,0.1-random.nextFloat()*0.2,0.1-random.nextFloat()*0.2));
        Vec3 motion = new Vec3(0,0,1).xRot(xRot).yRot((float) (-this.getYRot() / 180 * Math.PI+Math.atan2(1,dist))).add(position());

        double d0 = motion.x  - getX();
        double d1 = motion.y  -getY();
        double d2 = motion.z  - getZ();


        abstractarrow.shoot(d0, d1 , d2, 2.5F, 1);
        abstractarrow.setIsHoly(isGlowing());
        this.level().addFreshEntity(abstractarrow);
    }

    public void shootAllBullet(LivingEntity target,Vec3 vec3) {
        if(target==null) return;
        float dist = distanceTo(target);

        Bullet abstractarrow = new Bullet(level(),this,2);
        abstractarrow.setPos(vec3);

        Vec3 motion = new Vec3(0,-0.01,1).yRot((float) (-this.getYRot() / 180 * Math.PI+Math.atan2(1,dist))).add(position());

        double d0 = motion.x  - getX();
        double d1 = motion.y  -getY();
        double d2 = motion.z  - getZ();

        abstractarrow.shoot(d0, d1 , d2, 4f, 0);
        abstractarrow.setIsHoly(true);
        this.level().addFreshEntity(abstractarrow);
    }

    public Vec3 artilleryForecastPos = Vec3.ZERO;

    public void setArtilleryForecastPos(LivingEntity target,Vec3 vec3) {
        if(target==null) return;
        float speed = 60f;

        double d0 = target.getX()  - vec3.x;
        double d2 = target.getZ()  - vec3.z;
        float dist = (float) (Math.sqrt(d0 * d0 + d2 * d2));
        float time = dist/speed+0.5f;

        artilleryForecastPos = getTargetMoveVec(target).scale(time).add(target.position());
    }

    public void shootArtilleryBullet(LivingEntity target,Vec3 vec3) {
        if(target==null||artilleryForecastPos==Vec3.ZERO) return;

        double d0 = target.getX()  - vec3.x;
        double d2 = target.getZ()  - vec3.z;
        float dist = (float) (Math.sqrt(d0 * d0 + d2 * d2));

        Bullet abstractarrow = new Bullet(level(),this,1);
        abstractarrow.setPos(vec3);

        Vec3 move = (new Vec3(artilleryForecastPos.x-vec3.x,0,artilleryForecastPos.z-vec3.z)).normalize().scale(3);
        abstractarrow.setIsHoly(isGlowing());
        this.level().addFreshEntity(abstractarrow);
        abstractarrow.shoot(move.x, Math.sqrt(dist)/60 , move.z, 0);
    }

    public void shootShotGunBullet(LivingEntity target,Vec3 vec3) {
        if(target==null) return;
        float xRot = Mth.clamp(getXRot()%360,-30,30)* -0.017453292F;
        float dist = target.distanceTo(this);
        for(int i = 0 ;i<10;i++) {
            Bullet abstractarrow = new Bullet(level(),this,4);
            abstractarrow.setPos(vec3);
            Vec3 motion = new Vec3(0, 0, 1).xRot(xRot).yRot((float) (-this.getYRot() / 180 * Math.PI)).add(position());

            double d0 = motion.x - getX();
            double d1 = motion.y - getY();
            double d2 = motion.z - getZ();

            abstractarrow.shoot(d0, d1, d2, 3.5F, i);
            abstractarrow.setIsHoly(isGlowing());
            this.level().addFreshEntity(abstractarrow);
        }
    }

    public void shootBulletWithoutFace(Vec3 target,Vec3 vec3,int type,float speed,boolean isGlowing) {
        if(target==null) return;

        Bullet abstractarrow = new Bullet(level(),this,type);
        abstractarrow.setPos(vec3);
        abstractarrow.setIsHoly(isGlowing);

        double d0 = target.x  - vec3.x;
        double d1 = target.y  - vec3.y;
        double d2 = target.z  - vec3.z;

        abstractarrow.shoot(d0, d1 , d2, speed, 1);
        this.level().addFreshEntity(abstractarrow);
    }

    @Override
    public void setClientVectors(int index, Vec3 pos) {
        if (clientVectors != null && clientVectors.length > index) {
            clientVectors[index] = pos;
        }
    }

    @Override
    public Dialogue getDialogue() {
        return DialogueStore.snownova_meet_1;
    }

    @Override
    public LivingEntity getDialogueEntity() {
        return dialogueLivingEntity;
    }

    @Override
    public void setDialogueEntity(LivingEntity dialogueEntity) {
        dialogueLivingEntity = dialogueEntity;
    }

    @Override
    public boolean getHasDialogue() {
        return getDialogue()!=null;
    }

    @Override
    public void performRangedAttack(LivingEntity pTarget, float pVelocity) {
        Vec3 add = new Vec3(-1, 1, 2).yRot((float) (-this.getYRot() / 180 * Math.PI)).add(position());
        shootBlackSpearSkill(pTarget,add,1);
    }

    public boolean randomRightAct(LivingEntity target){
        if(normalAttackTime/5f+0.4 > getRandom().nextFloat()){
            float v = getRandom().nextFloat();
            if (v < 0.5) {
                AnimationActHandler.INSTANCE.sendAnimationMessage(this, LACK_HALBERD_DOWNCHI);
            } else {
                AnimationActHandler.INSTANCE.sendAnimationMessage(this, LACK_HALBERD_TIAOWIND);
            }
            normalAttackTime = -1;
            return true;
        }

        return false;
    }

    public boolean randomLeftAct(LivingEntity taget){
        if(normalAttackTime/5f+0.4 > getRandom().nextFloat()) {
            float v = getRandom().nextFloat();
            if (v < 0.3) {
                AnimationActHandler.INSTANCE.sendAnimationMessage(this, RACK_HALBERD_CHI);
            } else if (v < 0.6) {
                AnimationActHandler.INSTANCE.sendAnimationMessage(this, RACK_HALBERD_HEAVY);
            } else {
                AnimationActHandler.INSTANCE.sendAnimationMessage(this, RACK_HALBERD_CYCLE2);
            }
            normalAttackTime = -1;
            return true;
        }

        return false;
    }

    private void doSummonCheng(){
        if(getAnimation()==SUMMON_CHENG){
            int tick = getAnimationTick();
            if(level().isClientSide){
                Vec3 start = FFEntityUtils.getBodyRotVec(this, new Vec3(-0.75, 6.6, 1.3));
                if(tick==16) {
                    AdvancedParticleBase.spawnParticle(level(), ParticleHandler.SUN.get(), start.x, start.y, start.z, 0, 0, 0, true, 0, 0, 0, 0, 0F, 1,1, 0, 1, 1, 44, true, false, new ParticleComponent[]{
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, ParticleComponent.KeyTrack.startAndEnd(0,2f), false),
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, new ParticleComponent.KeyTrack(new float[]{1,1,3,3,5,5,7,7,8,8,9},new float[]{0,0.1f,0.2f,0.3f,0.4f,0.5f,0.6f,0.7f,0.8f,0.9f,1f}), true)
                    });
                }
                if(tick==60) {
                    ParticleComponent.KeyTrack keyTrack1 = ParticleComponent.KeyTrack.oscillate(0, 2, 24);
                    ParticleComponent.KeyTrack keyTrack3 = new ParticleComponent.KeyTrack(new float[]{0, 0,0, 200, 200,10}, new float[]{0, 0.4f,0.5f, 0.6f,0.9f, 1});
                    ParticleComponent.KeyTrack keyTrack31 = new ParticleComponent.KeyTrack(new float[]{0, 15, 15,0, 0}, new float[]{0, 0.4f,0.5f, 0.6f, 1});
                    AdvancedParticleBase.spawnParticle(level(), ParticleHandler.ICEBOMB_1.get(), start.x, start.y, start.z, 0, 0, 0, true, 0, 0, 0, 0, 0F, 1, 1, 0, 1, 1, 50, true, false, new ParticleComponent[]{
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, keyTrack3, false),
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, keyTrack1, true),
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.MOTION_Y, ParticleComponent.KeyTrack.startAndEnd(4f,0), false)
                    });
                    AdvancedParticleBase.spawnParticle(level(), ParticleHandler.SUN.get(), start.x, start.y, start.z, 0, 0, 0, true, 0, 0, 0, 0, 0F, 1, 1, 0, 1, 1, 50, true, false, new ParticleComponent[]{
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, keyTrack31, false),
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, keyTrack1, true),
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.MOTION_Y, ParticleComponent.KeyTrack.startAndEnd(4f,0), false)
                    });
                }
                if(tick>=16&&tick<=52&&tick%2==0){
                    Vec3 newStart = new Vec3(0,0,4).xRot((-0.25f+0.75f*random.nextFloat())*3.14f).yRot(6.28f*random.nextFloat());
                    AdvancedParticleBase.spawnParticle(level(), ParticleHandler.SUN.get(), start.x+newStart.x, start.y+newStart.y, start.z+newStart.z, 0, 0, 0, true, 0, 0, 0, 0, 3F, 1, 1, 0, 1, 1, 8, true, false, new ParticleComponent[]{
                            new ParticleComponent.Attractor(new Vec3[]{start},3,0.1f, ParticleComponent.Attractor.EnumAttractorBehavior.EXPONENTIAL),
                            new RibbonComponent(ParticleHandler.RIBBON_FLAT.get(), 2, 0, 0, 0, 0.2F, 1,1,1, 1, true, true, new ParticleComponent[]{
                                    new RibbonComponent.PropertyOverLength(RibbonComponent.PropertyOverLength.EnumRibbonProperty.SCALE, ParticleComponent.KeyTrack.startAndEnd(0.2f, 0))
                            }),
                    });
                }
            }
        }
    }

    private void doAllShotCheng(){
        if(getAnimation()==ALL_SHOT){
            int tick = getAnimationTick();
            if(level().isClientSide){
                if(tick==8) {
                    for(int i =1;i<=3;i++){
                        AdvancedParticleBase.spawnParticle(level(), ParticleHandler.RING_BIG.get(),getX(), getY(), getZ(), 0, 0, 0, false, 0, 1.57, 0, 0, 16, 1, 0.8, 0, 1, 0, 20, true, false, new ParticleComponent[]{
                                new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.MOTION_Y, ParticleComponent.KeyTrack.startAndEnd(0.15f*i, -0.15f*i), false),
                                new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, new ParticleComponent.KeyTrack(new float[]{0,1,1},new float[]{0,0.2f*i,1}), false),
                                new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, new ParticleComponent.KeyTrack(new float[]{0,8*3,16},new float[]{0,0.3f*i,1}), false)
                        });
                    }
                }
                if(tick>=28&&tick<=48){
                    Vec3 pos = new Vec3(0, 0, random.nextFloat() * 2.7).yRot(6.28f * random.nextFloat());
                    //AdvancedParticleBase.spawnParticle(level(), ParticleHandler.SUN.get(), , , true, 0, 0, 0, 0, 2 + random.nextFloat(), 1, 0.8, 0, 1, 1, 40, true, false, new ParticleComponent[]{
                    //        new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, SUN, false),
                    //});
                    level().addParticle(new ParticleCloud.CloudData(ParticleHandler.CLOUD.get(), 1, 0.8f, 0, (float) (10d + random.nextDouble() * 10d), 20+random.nextInt(11), ParticleCloud.EnumCloudBehavior.SHRINK, 0.9f), getX() + pos.x, this.getY() + 0.5F + random.nextFloat() * 2, getZ() + pos.z, 0, 0.05 + random.nextFloat() * 0.05, 0);
                }
            }
        }
    }

    private void doSkillCheng(){
        if(getAnimation()==SKILL_LOOP){
            int tick = getAnimationTick();
            if(tick%5==0){
                List<LivingEntity> livingEntities = this.level().getEntitiesOfClass(LivingEntity.class,this.getBoundingBox().inflate(8),hit->
                        this.distanceTo(hit)<6+hit.getBbWidth()/2);
                for(LivingEntity livingEntity :livingEntities){

                    Vec3 move = new Vec3(livingEntity.getX()-this.getX(),0,livingEntity.getZ()-this.getZ());
                    float len = (float) move.length();
                    livingEntity.setDeltaMovement(move.normalize().scale(2* org.joml.Math.max(0,1-len/6)));
                    livingEntity.move(MoverType.SELF,livingEntity.getDeltaMovement());
                }
            }
            if(level().isClientSide){
                Vec3 pos = new Vec3(0, 0, random.nextFloat() * 2.7).yRot(6.28f * random.nextFloat());
                level().addParticle(new ParticleCloud.CloudData(ParticleHandler.CLOUD.get(), 1,1,1, (float) (10d + random.nextDouble() * 10d), 20+random.nextInt(11), ParticleCloud.EnumCloudBehavior.SHRINK, 0.9f), getX() + pos.x, this.getY() + 0.5F + random.nextFloat() * 2, getZ() + pos.z, 0, 0.05 + random.nextFloat() * 0.05, 0);
                if(tickCount%10==0){
                    AdvancedParticleBase.spawnParticle(level(), ParticleHandler.RING_BIG.get(), getX(), getY() + 0.5, getZ(), 0, 0.01, 0, false, 0, org.joml.Math.toRadians(-90), 0, 0, 50F, 1, 1, 1, 1, 1, 40, true, false, new ParticleComponent[]{
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, ParticleComponent.KeyTrack.startAndEnd(0f, 60f), false),
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, ParticleComponent.KeyTrack.startAndEnd(1f, 0f), false)
                    });
                }
            }
        }
    }

    private void doState2(){
        if(getAnimation()==STATE_2){
            int tick = getAnimationTick();
            if(level().isClientSide){
                if(tick>=110&&tick<=120){
                    float smoke = tick-110;
                    int len = (int) (smoke*2*3.14);
                    for(int i =0;i<len;i+=2){
                        if(random.nextBoolean()){
                            Vec3 pos = new Vec3(0, 0.2, smoke + random.nextFloat()).yRot(6.28f * i / len).add(position());
                            level().addParticle(new ParticleCloud.CloudData(ParticleHandler.CLOUD.get(), 0.8f, 0.8f, 1f, (float) (20d + random.nextDouble() * 20d), 30, ParticleCloud.EnumCloudBehavior.SHRINK, 0.9f), pos.x, pos.y, pos.z, 0, 0.05, 0);
                        }
                    }
                }
                if(tick>95){
                    int lightTick = tick%10;
                    float len = 4;
                    float h = 1+random.nextFloat()*3;
                    Vec3 move = new Vec3(0, h, len).yRot(6.28f*lightTick/10);
                    Vec3 move1 = new Vec3(0, h+(random.nextBoolean()?1:-1), len).yRot(6.28f*(lightTick+1)/10);
                    Vec3 finalMove = move1.subtract(move);
                    level().addParticle(ParticleHandler.TESLA_BULB_LIGHTNING.get(), this.getX()+move.x, this.getY()+move.y, this.getZ()+move.z, finalMove.x, finalMove.y, finalMove.z);
                }
            }
            else {
                if(tick==95){
                    EntityCameraShake.cameraShake(level(),position(),20,0.06f,90,10);
                }
                if(tick==142){
                    FXEntity.SpawnFXEntity(level(),0, position().add(0,0.5f,0), this);
                }
            }
        }
    }

    private void doSkillFeng(){
        if(getAnimation()==WIND_MILL){
            int tick = getAnimationTick();
            if(level().isClientSide){

            }
        }
    }

    private void doSkillHalberd3(){
        if(level().isClientSide){
            int tick = getAnimationTick();
            if (getAnimation() == SKILL_HALBERD_10) {
                if (tick == 25) {
                    skillHalberd10();
                }

                if (tick == 63 &&halberd!=null) {
                    skillHalberd11(halberd,1.1f);
                }
                if (tick == 64 &&halberd!=null) {
                    idleLightParticle(halberd);
                    idleLightParticle(halberd);
                    idleLightParticle(halberd);
                }
            }
            if (getAnimation() == SKILL_HALBERD_13) {
                if (tick == 153) {
                    skillHalberd12(FFEntityUtils.getBodyRotVec(this,new Vec3(0,0.5,4)));
                }
            }
        }
    }

    private void doSkillHalberd2(){
        if(getAnimation()==SKILL_HALBERD_2){
            int tick = getAnimationTick();
            if(level().isClientSide){
                if(tick==5){
                    BlockEffect blockEffect = new BlockEffect(level(),new Vec3(getX(),getY()+2,getZ()));

                    RLParticle rlParticle1 = new RLParticle();
                    rlParticle1.config.setDuration(50);
                    rlParticle1.config.setStartLifetime(NumberFunction.constant(30));
                    rlParticle1.config.setStartSpeed(NumberFunction.constant(-8));
                    rlParticle1.config.setStartSize(new NumberFunction3(1.5f));

                    rlParticle1.config.getEmission().setEmissionRate(NumberFunction.constant(3));
                    EmissionSetting.Burst burst = new EmissionSetting.Burst();burst.setCount(NumberFunction.constant(60));
                    rlParticle1.config.getEmission().addBursts(burst);

                    rlParticle1.config.getMaterial().setMaterial(MaterialHandle.SMOKE);

                    Circle circle = new Circle();circle.setRadius(16);circle.setRadiusThickness(0.2f);
                    rlParticle1.config.getShape().setShape(circle);

                    rlParticle1.config.getVelocityOverLifetime().open();
                    rlParticle1.config.getVelocityOverLifetime().setOrbitalMode(VelocityOverLifetimeSetting.OrbitalMode.LinearVelocity);
                    rlParticle1.config.getVelocityOverLifetime().setOrbital(new NumberFunction3(0,-4,0));

                    rlParticle1.config.getColorOverLifetime().open();
                    rlParticle1.config.getColorOverLifetime().setColor(new Gradient(GradientHandle.CENTER_OPAQUE));

                    rlParticle1.config.getUvAnimation().open();
                    rlParticle1.config.getUvAnimation().setTiles(new Range(2,2));
                    rlParticle1.config.getUvAnimation().setAnimation(UVAnimationSetting.Animation.SingleRow);

                    rlParticle1.config.trails.open();
                    rlParticle1.config.trails.config.getMaterial().setMaterial(MaterialHandle.SMOKE);

                    RLParticle rlParticle2 = new RLParticle();
                    rlParticle2.config.setDuration(50);
                    rlParticle2.transform.position(new Vector3f(0,7,0));
                    rlParticle2.config.setStartLifetime(NumberFunction.constant(30));
                    rlParticle2.config.setStartSpeed(NumberFunction.constant(-8));
                    rlParticle2.config.setStartSize(new NumberFunction3(new RandomConstant(0.5,0.1,true)));

                    rlParticle2.config.getEmission().setEmissionRate(NumberFunction.constant(0.5));

                    rlParticle2.config.getMaterial().setMaterial(MaterialHandle.VOID);

                    Circle circle2 = new Circle();circle2.setRadius(16);circle2.setRadiusThickness(0.2f);
                    rlParticle2.config.getShape().setShape(circle2);

                    rlParticle2.config.getVelocityOverLifetime().open();
                    rlParticle2.config.getVelocityOverLifetime().setOrbitalMode(VelocityOverLifetimeSetting.OrbitalMode.LinearVelocity);
                    rlParticle2.config.getVelocityOverLifetime().setOrbital(new NumberFunction3(0,-4,0));
                    rlParticle2.config.getVelocityOverLifetime().setLinear(new NumberFunction3(0,-5,0));

                    rlParticle2.config.getColorOverLifetime().open();
                    rlParticle2.config.getColorOverLifetime().setColor(new Gradient(GradientHandle.CENTER_OPAQUE));

                    rlParticle2.config.trails.open();
                    rlParticle2.config.trails.config.getMaterial().setMaterial(MaterialHandle.CIRCLE);

                    RLParticle rlParticle3 = new RLParticle();
                    rlParticle3.config.setDuration(40);
                    rlParticle3.config.setStartLifetime(NumberFunction.constant(20));
                    rlParticle3.config.setStartSpeed(NumberFunction.constant(-8));
                    rlParticle3.config.setStartSize(new NumberFunction3(NumberFunction.constant(1)));

                    rlParticle3.config.getEmission().setEmissionRate(NumberFunction.constant(0));
                    EmissionSetting.Burst burst3 = new EmissionSetting.Burst();burst3.time = 10;burst3.setCount(NumberFunction.constant(2));
                    burst3.cycles = 0;
                    rlParticle3.config.getEmission().addBursts(burst3);

                    rlParticle3.config.getMaterial().setMaterial(MaterialHandle.SMOKE);

                    Circle circle3 = new Circle();circle3.setRadius(8);circle3.setRadiusThickness(0.2f);
                    rlParticle3.config.getShape().setShape(circle3);

                    rlParticle3.config.getVelocityOverLifetime().open();
                    rlParticle3.config.getVelocityOverLifetime().setOrbitalMode(VelocityOverLifetimeSetting.OrbitalMode.LinearVelocity);
                    rlParticle3.config.getVelocityOverLifetime().setOrbital(new NumberFunction3(0,-4,0));

                    rlParticle3.config.getColorOverLifetime().open();
                    rlParticle3.config.getColorOverLifetime().setColor(new Gradient(GradientHandle.CENTER_OPAQUE));

                    rlParticle3.config.getUvAnimation().open();
                    rlParticle3.config.getUvAnimation().setTiles(new Range(2,2));
                    rlParticle3.config.getUvAnimation().setAnimation(UVAnimationSetting.Animation.SingleRow);

                    rlParticle3.config.trails.open();
                    rlParticle3.config.trails.config.getMaterial().setMaterial(MaterialHandle.SMOKE);


                    rlParticle1.emmit(blockEffect);
                    rlParticle2.emmit(new BlockEffect(level(),new Vec3(getX(),getY()+8,getZ())));
                    rlParticle3.emmit(blockEffect);
                }
                if (tick==76||tick==108){
                    RLParticle rlParticle1 = new RLParticle();
                    rlParticle1.config.setDuration(20);
                    rlParticle1.config.setStartLifetime(NumberFunction.constant(20));
                    rlParticle1.config.setStartSpeed(NumberFunction.constant(0));
                    rlParticle1.config.setStartSize(new NumberFunction3(4));

                    rlParticle1.config.getEmission().setEmissionRate(NumberFunction.constant(1));
                    EmissionSetting.Burst burst1 = new EmissionSetting.Burst();burst1.setCount(NumberFunction.constant(18));
                    rlParticle1.config.getEmission().addBursts(burst1);

                    rlParticle1.config.getMaterial().setMaterial(MaterialHandle.SMOKE);

                    Circle circle1 = new Circle();circle1.setRadius(1);circle1.setRadiusThickness(0);
                    rlParticle1.config.getShape().setShape(circle1);

                    rlParticle1.config.getVelocityOverLifetime().open();
                    rlParticle1.config.getVelocityOverLifetime().setOrbitalMode(VelocityOverLifetimeSetting.OrbitalMode.FixedVelocity);
                    rlParticle1.config.getVelocityOverLifetime().setOrbital(new NumberFunction3(NumberFunction.constant(0),new Line(new float[]{0,0.5f,1},new float[]{78,78,36}),NumberFunction.constant(0)));
                    rlParticle1.config.getVelocityOverLifetime().setLinear(new NumberFunction3(NumberFunction.constant(0),new RandomConstant(0,1,true),NumberFunction.constant(0)));

                    rlParticle1.config.getColorOverLifetime().open();
                    rlParticle1.config.getColorOverLifetime().setColor(new Gradient(GradientHandle.CENTER_OPAQUE));

                    rlParticle1.config.getUvAnimation().open();
                    rlParticle1.config.getUvAnimation().setTiles(new Range(2,2));
                    rlParticle1.config.getUvAnimation().setAnimation(UVAnimationSetting.Animation.SingleRow);

                    rlParticle1.config.trails.open();
                    rlParticle1.config.trails.config.getMaterial().setMaterial(MaterialHandle.SMOKE);

                    RLParticle rlParticle2 = new RLParticle();
                    rlParticle2.config.setDuration(16);
                    rlParticle2.config.setStartLifetime(NumberFunction.constant(20));
                    rlParticle2.config.setStartSpeed(NumberFunction.constant(1));
                    rlParticle2.config.setStartSize(new NumberFunction3(new RandomConstant(16,14,true)));

                    rlParticle2.config.getEmission().setEmissionRate(NumberFunction.constant(0.5));
                    rlParticle2.config.getMaterial().setMaterial(MaterialHandle.RING);

                    ((RendererSetting.Particle)rlParticle2.config.getRenderer()).setRenderMode(RendererSetting.Particle.Mode.Horizontal);

                    rlParticle2.config.getShape().setShape(new Dot());

                    rlParticle2.config.getSizeOverLifetime().open();
                    rlParticle2.config.getSizeOverLifetime().setSize(new NumberFunction3(new Line(new float[]{0,0.5f,1},new float[]{0,1,1})));

                    rlParticle2.config.getColorOverLifetime().open();
                    rlParticle2.config.getColorOverLifetime().setColor(new Gradient(GradientHandle.CENTER_OPAQUE));

                    RLParticle rlParticle3 = new RLParticle();
                    rlParticle3.config.setDuration(20);
                    rlParticle3.config.setStartLifetime(NumberFunction.constant(20));
                    rlParticle3.config.setStartSpeed(NumberFunction.constant(0));
                    rlParticle3.config.setStartSize(new NumberFunction3(0.3));
                    rlParticle3.config.setStartRotation(new NumberFunction3(NumberFunction.constant(0),NumberFunction.constant(0),new RandomConstant(-40 ,40 , true)));

                    rlParticle3.config.getEmission().setEmissionRate(NumberFunction.constant(3));
                    EmissionSetting.Burst burst3 = new EmissionSetting.Burst();burst3.setCount(NumberFunction.constant(24));
                    rlParticle3.config.getEmission().addBursts(burst3);

                    rlParticle3.config.getMaterial().setMaterial(MaterialHandle.GLOW);

                    Circle circle3 = new Circle();circle3.setRadius(1);circle3.setRadiusThickness(0);
                    rlParticle3.config.getShape().setShape(circle3);

                    rlParticle3.config.getVelocityOverLifetime().open();
                    rlParticle3.config.getVelocityOverLifetime().setOrbitalMode(VelocityOverLifetimeSetting.OrbitalMode.FixedVelocity);
                    rlParticle3.config.getVelocityOverLifetime().setOrbital(new NumberFunction3(NumberFunction.constant(0),new Line(new float[]{0,0.5f,1},new float[]{60,60,28}),NumberFunction.constant(0)));
                    rlParticle3.config.getVelocityOverLifetime().setLinear(new NumberFunction3(NumberFunction.constant(0),new RandomConstant(4,1,true),NumberFunction.constant(0)));

                    rlParticle3.config.getColorOverLifetime().open();
                    rlParticle3.config.getColorOverLifetime().setColor(new Gradient(GradientHandle.CENTER_OPAQUE));

                    RLParticle rlParticle4 = new RLParticle();
                    rlParticle4.config.setDuration(16);
                    rlParticle4.config.setStartLifetime(NumberFunction.constant(20));
                    rlParticle4.config.setStartSpeed(NumberFunction.constant(0));
                    rlParticle4.config.setStartSize(new NumberFunction3(1.5));

                    rlParticle4.config.getEmission().setEmissionRate(NumberFunction.constant(1));
                    EmissionSetting.Burst burst4 = new EmissionSetting.Burst();burst4.setCount(NumberFunction.constant(2));
                    rlParticle4.config.getEmission().addBursts(burst4);

                    rlParticle4.config.getMaterial().setMaterial(MaterialHandle.SMOKE);

                    Circle circle4 = new Circle();circle4.setRadius(4);circle4.setRadiusThickness(0.5f);circle4.setArc(180);
                    rlParticle4.config.getShape().setShape(circle4);

                    rlParticle4.config.getVelocityOverLifetime().open();
                    rlParticle4.config.getVelocityOverLifetime().setOrbitalMode(VelocityOverLifetimeSetting.OrbitalMode.LinearVelocity);
                    rlParticle4.config.getVelocityOverLifetime().setOrbital(new NumberFunction3(NumberFunction.constant(0),new Line(new float[]{0,0.5f,1},new float[]{40,40,20}),NumberFunction.constant(0)));
                    rlParticle4.config.getVelocityOverLifetime().setLinear(new NumberFunction3(NumberFunction.constant(0),new RandomConstant(6,4,true),NumberFunction.constant(0)));

                    rlParticle4.config.getColorOverLifetime().open();
                    rlParticle4.config.getColorOverLifetime().setColor(new Gradient(GradientHandle.CENTER_OPAQUE));

                    rlParticle4.config.getUvAnimation().open();
                    rlParticle4.config.getUvAnimation().setTiles(new Range(2,2));
                    rlParticle4.config.getUvAnimation().setAnimation(UVAnimationSetting.Animation.SingleRow);

                    rlParticle4.config.trails.open();
                    rlParticle4.config.trails.config.getMaterial().setMaterial(MaterialHandle.SMOKE);

                    BlockEffect blockEffect = new BlockEffect(level(),new Vec3(getX(),getY()+1,getZ()));
                    rlParticle1.emmit(blockEffect);
                    rlParticle2.emmit(blockEffect);
                    rlParticle3.emmit(blockEffect);
                    rlParticle4.emmit(blockEffect);
                }
            }
        }
    }

    private void doTiaoWind(){
        if(getAnimation()==LACK_HALBERD_TIAOWIND){
            int tick = getAnimationTick();
            if(level().isClientSide){
                if (tick==24&&halberd1!=null){
                    RLParticle rlParticle1 = new RLParticle();
                    rlParticle1.config.setDuration(20);
                    rlParticle1.config.setStartLifetime(NumberFunction.constant(20));
                    rlParticle1.config.setStartSpeed(NumberFunction.constant(0));
                    rlParticle1.config.setStartSize(new NumberFunction3(1.2));
                    rlParticle1.config.setStartColor(new Gradient(new GradientColor(0X67E6FFFF)));

                    rlParticle1.config.getEmission().setEmissionRate(NumberFunction.constant(0.3));
                    EmissionSetting.Burst burst1 = new EmissionSetting.Burst();burst1.setCount(NumberFunction.constant(4));
                    rlParticle1.config.getEmission().addBursts(burst1);

                    rlParticle1.config.getMaterial().setMaterial(MaterialHandle.SMOKE);

                    Circle circle1 = new Circle();circle1.setRadius(0.6f);circle1.setRadiusThickness(0);
                    rlParticle1.config.getShape().setShape(circle1);

                    rlParticle1.config.getVelocityOverLifetime().open();
                    rlParticle1.config.getVelocityOverLifetime().setOrbitalMode(VelocityOverLifetimeSetting.OrbitalMode.LinearVelocity);
                    rlParticle1.config.getVelocityOverLifetime().setOrbital(new NumberFunction3(NumberFunction.constant(0),new Line(new float[]{0,0.5f,1},new float[]{5,5,1.5f}),NumberFunction.constant(0)));
                    rlParticle1.config.getVelocityOverLifetime().setLinear(new NumberFunction3(0,5,0));

                    rlParticle1.config.getColorOverLifetime().open();
                    rlParticle1.config.getColorOverLifetime().setColor(new Gradient(GradientHandle.CENTER_OPAQUE));

                    rlParticle1.config.getUvAnimation().open();
                    rlParticle1.config.getUvAnimation().setTiles(new Range(2,2));
                    rlParticle1.config.getUvAnimation().setAnimation(UVAnimationSetting.Animation.SingleRow);

                    rlParticle1.config.trails.open();
                    rlParticle1.config.trails.config.getMaterial().setMaterial(MaterialHandle.SMOKE);

                    RLParticle rlParticle3 = new RLParticle();
                    rlParticle3.config.setDuration(20);
                    rlParticle3.config.setStartLifetime(NumberFunction.constant(20));
                    rlParticle3.config.setStartSpeed(NumberFunction.constant(0));
                    rlParticle3.config.setStartSize(new NumberFunction3(0.2));
                    rlParticle3.config.setStartRotation(new NumberFunction3(NumberFunction.constant(0),NumberFunction.constant(0),new RandomConstant(-40 ,40 , true)));

                    rlParticle3.config.getEmission().setEmissionRate(NumberFunction.constant(1));

                    rlParticle3.config.getMaterial().setMaterial(TBSMaterialHandle.PIXEL);

                    Circle circle3 = new Circle();circle3.setRadius(0.6f);circle3.setRadiusThickness(0);
                    rlParticle3.config.getShape().setShape(circle3);

                    rlParticle3.config.getVelocityOverLifetime().open();
                    rlParticle3.config.getVelocityOverLifetime().setOrbitalMode(VelocityOverLifetimeSetting.OrbitalMode.LinearVelocity);
                    rlParticle3.config.getVelocityOverLifetime().setOrbital(new NumberFunction3(NumberFunction.constant(0),new Line(new float[]{0,0.5f,1},new float[]{5,5,1.5f}),NumberFunction.constant(0)));
                    rlParticle3.config.getVelocityOverLifetime().setLinear(new NumberFunction3(NumberFunction.constant(0),new RandomConstant(4,1,true),NumberFunction.constant(0)));

                    rlParticle3.config.getColorOverLifetime().open();
                    rlParticle3.config.getColorOverLifetime().setColor(new Gradient(GradientHandle.CENTER_OPAQUE));

                    GeoBoneEffect blockEffect = new GeoBoneEffect(level(),this,halberd1);
                    blockEffect.setOffset(0,-2,0);
                    rlParticle1.emmit(blockEffect);
                    rlParticle3.emmit(blockEffect);
                }
                if (tick==38){
                    RLParticle rlParticle1 = new RLParticle();
                    rlParticle1.config.setDuration(20);
                    rlParticle1.config.setStartLifetime(NumberFunction.constant(30));
                    rlParticle1.config.setStartSpeed(NumberFunction.constant(0));
                    rlParticle1.config.setStartSize(new NumberFunction3(2.8));
                    rlParticle1.config.setStartColor(new Gradient(new GradientColor(0X67E6FFFF)));

                    rlParticle1.config.getEmission().setEmissionRate(NumberFunction.constant(0));
                    EmissionSetting.Burst burst1 = new EmissionSetting.Burst();burst1.setCount(NumberFunction.constant(22));
                    rlParticle1.config.getEmission().addBursts(burst1);

                    rlParticle1.config.getMaterial().setMaterial(MaterialHandle.SMOKE);

                    Circle circle1 = new Circle();circle1.setRadius(0.5f);circle1.setRadiusThickness(0);
                    rlParticle1.config.getShape().setShape(circle1);

                    rlParticle1.config.getVelocityOverLifetime().open();
                    rlParticle1.config.getVelocityOverLifetime().setOrbitalMode(VelocityOverLifetimeSetting.OrbitalMode.FixedVelocity);
                    rlParticle1.config.getVelocityOverLifetime().setOrbital(new NumberFunction3(NumberFunction.constant(0),new Line(new float[]{0,0.08f,0.16f,1},new float[]{60,60,30,30}),NumberFunction.constant(0)));

                    rlParticle1.config.getColorOverLifetime().open();
                    rlParticle1.config.getColorOverLifetime().setColor(new Gradient(new GradientColor(0XFFFFFFFF,0XFFFFFFFF,0X00FFFFFF)));

                    rlParticle1.config.getUvAnimation().open();
                    rlParticle1.config.getUvAnimation().setTiles(new Range(2,2));
                    rlParticle1.config.getUvAnimation().setAnimation(UVAnimationSetting.Animation.SingleRow);

                    rlParticle1.config.trails.open();
                    rlParticle1.config.trails.config.getMaterial().setMaterial(MaterialHandle.SMOKE);

                    RLParticle rlParticle3 = new RLParticle();
                    rlParticle3.config.setDuration(20);
                    rlParticle3.config.setStartLifetime(NumberFunction.constant(12));
                    rlParticle3.config.setStartSpeed(NumberFunction.constant(0));
                    rlParticle3.config.setStartSize(new NumberFunction3(0.1));
                    rlParticle3.config.setStartRotation(new NumberFunction3(NumberFunction.constant(0),NumberFunction.constant(0),new RandomConstant(-40 ,40 , true)));

                    rlParticle3.config.getEmission().setEmissionRate(NumberFunction.constant(3));
                    EmissionSetting.Burst burst3 = new EmissionSetting.Burst();burst3.setCount(NumberFunction.constant(24));
                    rlParticle3.config.getEmission().addBursts(burst3);

                    rlParticle3.config.getMaterial().setMaterial(MaterialHandle.GLOW);

                    Circle circle3 = new Circle();circle3.setRadius(1);circle3.setRadiusThickness(0);
                    rlParticle3.config.getShape().setShape(circle3);

                    rlParticle3.config.getVelocityOverLifetime().open();
                    rlParticle3.config.getVelocityOverLifetime().setOrbitalMode(VelocityOverLifetimeSetting.OrbitalMode.FixedVelocity);
                    rlParticle3.config.getVelocityOverLifetime().setOrbital(new NumberFunction3(NumberFunction.constant(0),new Line(new float[]{0,0.5f,1},new float[]{16,16,7}),NumberFunction.constant(0)));
                    rlParticle3.config.getVelocityOverLifetime().setLinear(new NumberFunction3(NumberFunction.constant(0),new RandomConstant(1,0.5,true),NumberFunction.constant(0)));

                    rlParticle3.config.getColorOverLifetime().open();
                    rlParticle3.config.getColorOverLifetime().setColor(new Gradient(GradientHandle.CENTER_OPAQUE));

                    BlockEffect blockEffect = new BlockEffect(level(),FFEntityUtils.getBodyRotVec(this,new Vec3(0,0.5,2)));
                    rlParticle1.emmit(blockEffect);
                    rlParticle3.emmit(blockEffect);
                }
            }
        }
    }



    private void doMoveCycle(){
        if(getAnimation()==MOVE_HALBERD_CYCLE){
            int tick = getAnimationTick();
            if(level().isClientSide){
                if(tick==15){
                    lightBoomParticle((float) (180-this.getYRot() / 180 * Math.PI),position());
                }
            }
        }
    }

    private void doMoveHalberdBack(){
        if(getAnimation()==MOVE_HALBERD_BACK){
            int tick = getAnimationTick();
            if(level().isClientSide){
                if(tick==20){
                    RLParticle rlParticle1 = new RLParticle();
                    rlParticle1.config.setDuration(10);
                    rlParticle1.config.setStartLifetime(NumberFunction.constant(20));
                    rlParticle1.config.setStartSpeed(NumberFunction.constant(1));

                    rlParticle1.config.getEmission().setEmissionRate(NumberFunction.constant(15));

                    rlParticle1.config.getMaterial().setMaterial(MaterialHandle.VOID);

                    Circle circle1 = new Circle();circle1.setRadius(4);circle1.setRadiusThickness(1);
                    rlParticle1.config.getShape().setShape(circle1);

                    rlParticle1.config.getNoise().open();
                    rlParticle1.config.getNoise().setPosition(new NumberFunction3(0.6));

                    rlParticle1.config.trails.open();
                    rlParticle1.config.trails.setLifetime(NumberFunction.constant(0.2));
                    rlParticle1.config.trails.setColorOverLifetime(new Gradient(new GradientColor(0X00DFEF86,0XFFDFEF86,0XFFDFEF86,0X00DFEF86)));
                    rlParticle1.config.trails.config.getMaterial().setMaterial(MaterialHandle.CIRCLE);
                    rlParticle1.config.trails.config.getRenderer().setBloomEffect(true);

                    rlParticle1.emmit(new BlockEffect(level(),new Vec3(getX(),getY()+1,getZ())));

                    //sLightParticle(new Vector3f());
                }
                if(tick==59){
                    Vec3 bodyRotVec = FFEntityUtils.getBodyRotVec(this, new Vec3(0, 0, 2));
                    lightBoomParticle((float) (-this.getYRot() / 180 * Math.PI),bodyRotVec);
                }
            }
        }
    }

    private void doMoveHalberdDownChi(){
        if(getAnimation()==LACK_HALBERD_DOWNCHI){
            int tick = getAnimationTick();
            if(level().isClientSide){
                if(tick==88){
                    Vec3 bodyRotVec = FFEntityUtils.getBodyRotVec(this, new Vec3(0, 0, 2));
                    lightBoomParticle((float) (-this.getYRot() / 180 * Math.PI),bodyRotVec);
                }
            }
        }
    }

    public void idleLightParticle(GeoBone geoBone){
        RLParticle rlParticle1 = new RLParticle();
        rlParticle1.config.setDuration(200);
        rlParticle1.config.setStartLifetime(NumberFunction.constant(10));
        rlParticle1.config.setStartSpeed(NumberFunction.constant(1));
        rlParticle1.config.setStartColor(new Gradient(new GradientColor(0XFFDFEF86)));
        rlParticle1.config.getEmission().setEmissionRate(NumberFunction.constant(1));
        rlParticle1.config.getMaterial().setMaterial(MaterialHandle.VOID);
        Sphere circle1 = new Sphere();circle1.setRadius(0.1f);
        rlParticle1.config.getShape().setShape(circle1);
        rlParticle1.config.getNoise().open();
        rlParticle1.config.getNoise().setPosition(new NumberFunction3(2));

        rlParticle1.config.trails.open();
        rlParticle1.config.trails.config.getMaterial().setMaterial(MaterialHandle.CIRCLE);
        rlParticle1.config.trails.config.getRenderer().setBloomEffect(true);

        rlParticle1.emmit(new GeoBoneEffect(level(),this,geoBone));
    }

    public void normalLightParticle(){
        RLParticle rlParticle1 = new RLParticle();
        rlParticle1.config.setDuration(40);
        rlParticle1.config.setStartLifetime(NumberFunction.constant(10));
        rlParticle1.config.setStartSpeed(NumberFunction.constant(3));
        rlParticle1.config.setStartColor(new Gradient(new GradientColor(0XFFDFEF86)));
        rlParticle1.config.getEmission().setEmissionRate(NumberFunction.constant(0.1));
        EmissionSetting.Burst burst1 = new EmissionSetting.Burst();burst1.setCount(NumberFunction.constant(10));
        burst1.time =15;
        rlParticle1.config.getEmission().addBursts(burst1);
        rlParticle1.config.getMaterial().setMaterial(MaterialHandle.VOID);
        Sphere circle1 = new Sphere();circle1.setRadius(0.5f);
        rlParticle1.config.getShape().setShape(circle1);
        rlParticle1.config.getNoise().open();
        rlParticle1.config.getNoise().setPosition(new NumberFunction3(1.5));

        rlParticle1.config.trails.open();
        rlParticle1.config.trails.config.getMaterial().setMaterial(MaterialHandle.CIRCLE);
        rlParticle1.config.trails.config.getRenderer().setBloomEffect(true);

        RLParticle rlParticle2 = new RLParticle();
        rlParticle2.config.setDuration(100);
        rlParticle2.config.setStartLifetime(NumberFunction.constant(8));
        rlParticle2.config.setStartSpeed(NumberFunction.constant(2));
        rlParticle2.config.setStartColor(new Gradient(new GradientColor(0XFFDFEF86)));
        rlParticle2.config.getEmission().setEmissionRate(NumberFunction.constant(0.2));
        rlParticle2.config.getMaterial().setMaterial(MaterialHandle.VOID);
        Sphere circle2 = new Sphere();circle2.setRadius(0.5f);
        rlParticle2.config.getShape().setShape(circle2);
        rlParticle2.config.getShape().setPosition(new NumberFunction3(NumberFunction.constant(0),new RandomConstant(2,0,true),NumberFunction.constant(0)));
        rlParticle2.config.getNoise().open();
        rlParticle2.config.getNoise().setPosition(new NumberFunction3(1.5));

        rlParticle2.config.getVelocityOverLifetime().open();
        rlParticle2.config.getVelocityOverLifetime().setLinear(new NumberFunction3(0,6,0));

        rlParticle2.config.trails.open();
        rlParticle2.config.trails.config.getMaterial().setMaterial(MaterialHandle.CIRCLE);
        rlParticle2.config.trails.config.getRenderer().setBloomEffect(true);

        EntityEffect effect = new EntityEffect(level(),this);
        rlParticle1.emmit(effect);
        rlParticle2.emmit(effect);
    }

    public void lightBoomParticle(float yrot, Vec3 blockpos){
        RLParticle rlParticle1 = new RLParticle();
        rlParticle1.config.setDuration(10);
        rlParticle1.config.setStartLifetime(NumberFunction.constant(5));
        rlParticle1.config.setStartSpeed(NumberFunction.constant(3));
        rlParticle1.config.setStartSize(new NumberFunction3(0.2));

        rlParticle1.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst1 = new EmissionSetting.Burst();burst1.setCount(NumberFunction.constant(20));
        rlParticle1.config.getEmission().addBursts(burst1);

        rlParticle1.config.getMaterial().setMaterial(MaterialHandle.VOID);

        Circle circle1 = new Circle();circle1.setRadius(1);circle1.setRadiusThickness(1);
        rlParticle1.config.getShape().setShape(circle1);

        rlParticle1.config.getVelocityOverLifetime().open();
        rlParticle1.config.getVelocityOverLifetime().setLinear(new NumberFunction3(NumberFunction.constant(0),NumberFunction.constant(3),NumberFunction.constant(0)));

        rlParticle1.config.getNoise().open();
        rlParticle1.config.getNoise().setPosition(new NumberFunction3(1));

        rlParticle1.config.trails.open();
        rlParticle1.config.trails.config.getMaterial().setMaterial(MaterialHandle.CIRCLE);
        rlParticle1.config.trails.setColorOverLifetime(new Gradient(new GradientColor(0XFFFFFFFF,0XFFDFEF86)));

        rlParticle1.config.trails.config.getRenderer().setBloomEffect(true);

        RLParticle rlParticle2 = new RLParticle();
        rlParticle2.config.setDuration(8);
        rlParticle2.config.setStartLifetime(NumberFunction.constant(2));
        rlParticle2.config.setStartSpeed(NumberFunction.constant(20));
        rlParticle2.config.setStartSize(new NumberFunction3(0.2));

        rlParticle2.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst2 = new EmissionSetting.Burst();burst2.setCount(NumberFunction.constant(7));
        burst2.cycles = 3;burst2.interval = 2;
        rlParticle2.config.getEmission().addBursts(burst2);

        rlParticle2.config.getMaterial().setMaterial(MaterialHandle.VOID);

        Cone circle2 = new Cone();circle2.setRadius(0.5f);circle2.setAngle(40);
        rlParticle2.config.getShape().setScale(new NumberFunction3(0.4,1,0.4));
        rlParticle2.config.getShape().setShape(circle2);
        rlParticle2.config.getShape().setRotation(new NumberFunction3(50,0,0));
        rlParticle2.updateScale(new Vector3f(1.3f,1.3f,1.3f));

        rlParticle2.config.getNoise().open();
        rlParticle2.config.getNoise().setPosition(new NumberFunction3(1));

        rlParticle2.config.trails.open();
        rlParticle2.config.trails.config.getMaterial().setMaterial(MaterialHandle.CIRCLE);
        rlParticle2.config.trails.setColorOverLifetime(new Gradient(new GradientColor(0XFFFFFFFF,0XFFDFEF86)));

        rlParticle2.config.trails.config.getRenderer().setBloomEffect(true);

        RLParticle rlParticle3 = new RLParticle();
        rlParticle3.config.setDuration(20);
        rlParticle3.config.setStartLifetime(NumberFunction.constant(6));
        rlParticle3.config.setStartSpeed(NumberFunction.constant(3));

        rlParticle3.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst3 = new EmissionSetting.Burst();burst3.setCount(NumberFunction.constant(40));
        burst3.cycles = 1;burst3.interval = 7;burst3.time = 2;
        rlParticle3.config.getEmission().addBursts(burst3);

        rlParticle3.config.getMaterial().setMaterial(MaterialHandle.VOID);

        Circle circle3 = new Circle();circle3.setRadius(2);
        rlParticle3.config.getShape().setShape(circle3);

        rlParticle3.config.getNoise().open();
        rlParticle3.config.getNoise().setPosition(new NumberFunction3(0.5));

        rlParticle3.config.trails.open();
        rlParticle3.config.trails.config.getMaterial().setMaterial(MaterialHandle.CIRCLE);
        rlParticle3.config.trails.setLifetime(NumberFunction.constant(0.5));
        rlParticle3.config.trails.setColorOverLifetime(new Gradient(new GradientColor(0XFFFFFFFF,0XFFDFEF86)));

        rlParticle3.config.trails.config.getRenderer().setBloomEffect(true);

        RLParticle rlParticle4 = new RLParticle();
        rlParticle4.config.setDuration(4);
        rlParticle4.config.setStartLifetime(NumberFunction.constant(6));
        rlParticle4.config.setStartColor(new Gradient(new GradientColor(0X5FDFEF86)));

        rlParticle4.config.getEmission().setEmissionRate(NumberFunction.constant(0.5));

        rlParticle4.config.getMaterial().setMaterial(MaterialHandle.CIRCLE);

        rlParticle4.config.getShape().setShape(new Dot());

        rlParticle4.config.getColorOverLifetime().open();
        rlParticle4.config.getColorOverLifetime().setColor(new Gradient(new GradientColor(0XFFDFEF86,0X00DFEF86)));

        BlockEffect blockEffect = new BlockEffect(level(),blockpos);
        rlParticle1.emmit(blockEffect);
        rlParticle2.updateRotation(new Vector3f(0,yrot,0));
        rlParticle2.emmit(blockEffect);
        rlParticle3.emmit(blockEffect);
        rlParticle4.emmit(blockEffect);
    }

    public void sLightParticle(Vector3f scale){
        RLParticle rlParticle1 = new RLParticle();
        rlParticle1.config.setDuration(10);
        rlParticle1.config.setStartLifetime(NumberFunction.constant(5));
        rlParticle1.config.setStartSpeed(NumberFunction.constant(3));
        rlParticle1.config.setStartSize(new NumberFunction3(0.2));

        rlParticle1.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst1 = new EmissionSetting.Burst();burst1.setCount(NumberFunction.constant(20));
        rlParticle1.config.getEmission().addBursts(burst1);

        rlParticle1.config.getMaterial().setMaterial(MaterialHandle.VOID);

        Circle circle1 = new Circle();circle1.setRadius(1);circle1.setRadiusThickness(1);
        rlParticle1.config.getShape().setShape(circle1);

        rlParticle1.config.getVelocityOverLifetime().open();
        rlParticle1.config.getVelocityOverLifetime().setLinear(new NumberFunction3(NumberFunction.constant(0),NumberFunction.constant(3),NumberFunction.constant(0)));

        rlParticle1.config.getNoise().open();
        rlParticle1.config.getNoise().setPosition(new NumberFunction3(1));

        rlParticle1.config.trails.open();
        rlParticle1.config.trails.config.getMaterial().setMaterial(MaterialHandle.CIRCLE);
        rlParticle1.config.trails.setColorOverLifetime(new Gradient(new GradientColor(0XFFFFFFFF,0XFFDFEF86)));

        rlParticle1.config.trails.config.getRenderer().setBloomEffect(true);

        RLParticle rlParticle2 = new RLParticle();
        rlParticle2.config.setDuration(8);
        rlParticle2.config.setStartLifetime(NumberFunction.constant(2));
        rlParticle2.config.setStartSpeed(NumberFunction.constant(16));
        rlParticle2.config.setStartSize(new NumberFunction3(0.2));

        rlParticle2.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst2 = new EmissionSetting.Burst();burst2.setCount(NumberFunction.constant(7));
        burst2.cycles = 3;burst2.interval = 2;
        rlParticle2.config.getEmission().addBursts(burst2);

        rlParticle2.config.getMaterial().setMaterial(MaterialHandle.VOID);

        Circle circle2 = new Circle();circle2.setRadius(0.2f);circle2.setRadiusThickness(0.2f);
        rlParticle2.config.getShape().setScale(new NumberFunction3(NumberFunction.constant(0.4),NumberFunction.constant(1),NumberFunction.constant(0.4)));
        rlParticle2.config.getShape().setShape(circle2);

        rlParticle2.config.getVelocityOverLifetime().open();
        rlParticle2.config.getVelocityOverLifetime().setLinear(new NumberFunction3(NumberFunction.constant(0),new RandomConstant(30,0,true),NumberFunction.constant(0)));

        rlParticle2.config.getNoise().open();
        rlParticle2.config.getNoise().setPosition(new NumberFunction3(1));

        rlParticle2.config.trails.open();
        rlParticle2.config.trails.config.getMaterial().setMaterial(MaterialHandle.CIRCLE);
        rlParticle2.config.trails.setColorOverLifetime(new Gradient(new GradientColor(0XFFDFEF86)));
        rlParticle2.config.trails.config.getRenderer().setBloomEffect(true);

        RLParticle rlParticle3 = new RLParticle();
        rlParticle3.config.setDuration(8);
        rlParticle3.config.setStartLifetime(NumberFunction.constant(4));
        rlParticle3.config.setStartSpeed(NumberFunction.constant(20));
        rlParticle3.config.setStartSize(new NumberFunction3(0.2));

        rlParticle3.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst3 = new EmissionSetting.Burst();burst3.setCount(NumberFunction.constant(3));
        rlParticle3.config.getEmission().addBursts(burst3);

        rlParticle3.config.getMaterial().setMaterial(MaterialHandle.VOID);

        rlParticle3.config.getShape().setShape(new Dot());

        rlParticle3.config.getVelocityOverLifetime().open();
        rlParticle3.config.getVelocityOverLifetime().setLinear(new NumberFunction3(NumberFunction.constant(0),NumberFunction.constant(40),NumberFunction.constant(0)));

        rlParticle3.config.getNoise().open();
        rlParticle3.config.getNoise().setPosition(new NumberFunction3(0.7));

        rlParticle3.config.trails.open();
        rlParticle3.config.trails.config.getMaterial().setMaterial(MaterialHandle.CIRCLE);
        rlParticle3.config.trails.setColorOverLifetime(new Gradient(new GradientColor(0XFFDFEF86)));
        rlParticle3.config.trails.config.getRenderer().setBloomEffect(true);

        RLParticle rlParticle4 = new RLParticle();
        rlParticle4.config.setDuration(20);
        rlParticle4.config.setStartLifetime(NumberFunction.constant(6));
        rlParticle4.config.setStartSpeed(NumberFunction.constant(6));

        rlParticle4.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst4 = new EmissionSetting.Burst();burst4.setCount(NumberFunction.constant(40));
        rlParticle4.config.getEmission().addBursts(burst4);burst4.time = 2;

        rlParticle4.config.getMaterial().setMaterial(MaterialHandle.VOID);

        Circle circle4 = new Circle();circle4.setRadius(2f);circle4.setRadiusThickness(1f);
        rlParticle4.config.getShape().setShape(circle4);

        rlParticle4.config.getNoise().open();
        rlParticle4.config.getNoise().setPosition(new NumberFunction3(0.5));

        rlParticle4.config.trails.open();
        rlParticle4.config.trails.config.getMaterial().setMaterial(MaterialHandle.CIRCLE);
        rlParticle4.config.trails.setColorOverLifetime(new Gradient(new GradientColor(0XFFFFFFFF,0XFFDFEF86)));
        rlParticle4.config.trails.config.getRenderer().setBloomEffect(true);

        RLParticle rlParticle5 = new RLParticle();
        rlParticle5.config.setDuration(20);
        rlParticle5.config.setStartLifetime(NumberFunction.constant(8));
        rlParticle5.config.setStartSpeed(NumberFunction.constant(1));
        rlParticle5.config.setStartSize(new NumberFunction3(5));

        rlParticle5.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst5 = new EmissionSetting.Burst();burst5.setCount(NumberFunction.constant(2));
        burst5.cycles = 2;burst5.interval = 2;
        rlParticle5.config.getEmission().addBursts(burst5);

        rlParticle5.config.getMaterial().setMaterial(MaterialHandle.RING);
        rlParticle5.config.getShape().setShape(new Dot());

        ((RendererSetting.Particle)rlParticle5.config.getRenderer()).setRenderMode(RendererSetting.Particle.Mode.Horizontal);

        rlParticle5.config.getColorOverLifetime().open();
        rlParticle5.config.getColorOverLifetime().setColor(new Gradient(new GradientColor(0XFFFFFFFF,0X00DFEF86)));

        rlParticle5.config.getSizeOverLifetime().open();
        rlParticle5.config.getSizeOverLifetime().setSize(new NumberFunction3(new Line(new float[]{0,1},new float[]{0,1})));

        RLParticle rlParticle6 = new RLParticle();
        rlParticle6.config.setDuration(4);
        rlParticle6.config.setStartLifetime(NumberFunction.constant(6));
        rlParticle6.config.setStartSpeed(NumberFunction.constant(1));
        rlParticle6.config.setStartSize(new NumberFunction3(4));

        rlParticle6.config.getEmission().setEmissionRate(NumberFunction.constant(0.5));
        rlParticle6.config.setStartColor(new Gradient(new GradientColor(0X48FFFFFF)));
        rlParticle6.config.getMaterial().setMaterial(MaterialHandle.CIRCLE);

        rlParticle6.config.getColorOverLifetime().open();
        rlParticle6.config.getColorOverLifetime().setColor(new Gradient(new GradientColor(0XFFDFEF86,0XFFDFEF86,0X00DFEF86)));

        BlockEffect blockEffect = new BlockEffect(level(), new Vec3(getX(),getY()+1,getZ()));
        rlParticle1.emmit(blockEffect);
        rlParticle2.emmit(blockEffect);
        rlParticle3.emmit(blockEffect);
        rlParticle4.emmit(blockEffect);
        rlParticle5.emmit(blockEffect);
        rlParticle6.emmit(blockEffect);
    }

    public void skillHalberd2(Vector3f scale){
        RLParticle rlParticle1 = new RLParticle();
        rlParticle1.config.setDuration(10);
        rlParticle1.config.setStartLifetime(NumberFunction.constant(5));
        rlParticle1.config.setStartSpeed(NumberFunction.constant(3));
        rlParticle1.config.setStartSize(new NumberFunction3(0.2));

        rlParticle1.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst1 = new EmissionSetting.Burst();burst1.setCount(NumberFunction.constant(20));
        rlParticle1.config.getEmission().addBursts(burst1);

        rlParticle1.config.getMaterial().setMaterial(MaterialHandle.VOID);

        Circle circle1 = new Circle();circle1.setRadius(1);circle1.setRadiusThickness(1);
        rlParticle1.config.getShape().setShape(circle1);

        rlParticle1.config.getVelocityOverLifetime().open();
        rlParticle1.config.getVelocityOverLifetime().setLinear(new NumberFunction3(NumberFunction.constant(0),NumberFunction.constant(3),NumberFunction.constant(0)));

        rlParticle1.config.getNoise().open();
        rlParticle1.config.getNoise().setPosition(new NumberFunction3(1));

        rlParticle1.config.trails.open();
        rlParticle1.config.trails.config.getMaterial().setMaterial(MaterialHandle.CIRCLE);
        rlParticle1.config.trails.setColorOverLifetime(new Gradient(new GradientColor(0XFFFFFFFF,0XFFDFEF86)));

        rlParticle1.config.trails.config.getRenderer().setBloomEffect(true);

        RLParticle rlParticle2 = new RLParticle();
        rlParticle2.config.setDuration(8);
        rlParticle2.config.setStartLifetime(NumberFunction.constant(2));
        rlParticle2.config.setStartSpeed(NumberFunction.constant(16));
        rlParticle2.config.setStartSize(new NumberFunction3(0.2));

        rlParticle2.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst2 = new EmissionSetting.Burst();burst2.setCount(NumberFunction.constant(7));
        burst2.cycles = 3;burst2.interval = 2;
        rlParticle2.config.getEmission().addBursts(burst2);

        rlParticle2.config.getMaterial().setMaterial(MaterialHandle.VOID);

        Circle circle2 = new Circle();circle2.setRadius(0.2f);circle2.setRadiusThickness(0.2f);
        rlParticle2.config.getShape().setScale(new NumberFunction3(NumberFunction.constant(0.4),NumberFunction.constant(1),NumberFunction.constant(0.4)));
        rlParticle2.config.getShape().setShape(circle2);

        rlParticle2.config.getVelocityOverLifetime().open();
        rlParticle2.config.getVelocityOverLifetime().setLinear(new NumberFunction3(NumberFunction.constant(0),new RandomConstant(30,0,true),NumberFunction.constant(0)));

        rlParticle2.config.getNoise().open();
        rlParticle2.config.getNoise().setPosition(new NumberFunction3(1));

        rlParticle2.config.trails.open();
        rlParticle2.config.trails.config.getMaterial().setMaterial(MaterialHandle.CIRCLE);
        rlParticle2.config.trails.setColorOverLifetime(new Gradient(new GradientColor(0XFFDFEF86)));
        rlParticle2.config.trails.config.getRenderer().setBloomEffect(true);

        RLParticle rlParticle3 = new RLParticle();
        rlParticle3.config.setDuration(8);
        rlParticle3.config.setStartLifetime(NumberFunction.constant(4));
        rlParticle3.config.setStartSpeed(NumberFunction.constant(20));
        rlParticle3.config.setStartSize(new NumberFunction3(0.2));

        rlParticle3.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst3 = new EmissionSetting.Burst();burst3.setCount(NumberFunction.constant(3));
        rlParticle3.config.getEmission().addBursts(burst3);

        rlParticle3.config.getMaterial().setMaterial(MaterialHandle.VOID);

        rlParticle3.config.getShape().setShape(new Dot());

        rlParticle3.config.getVelocityOverLifetime().open();
        rlParticle3.config.getVelocityOverLifetime().setLinear(new NumberFunction3(NumberFunction.constant(0),NumberFunction.constant(40),NumberFunction.constant(0)));

        rlParticle3.config.getNoise().open();
        rlParticle3.config.getNoise().setPosition(new NumberFunction3(0.7));

        rlParticle3.config.trails.open();
        rlParticle3.config.trails.config.getMaterial().setMaterial(MaterialHandle.CIRCLE);
        rlParticle3.config.trails.setColorOverLifetime(new Gradient(new GradientColor(0XFFDFEF86)));
        rlParticle3.config.trails.config.getRenderer().setBloomEffect(true);

        RLParticle rlParticle4 = new RLParticle();
        rlParticle4.config.setDuration(20);
        rlParticle4.config.setStartLifetime(NumberFunction.constant(6));
        rlParticle4.config.setStartSpeed(NumberFunction.constant(6));

        rlParticle4.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst4 = new EmissionSetting.Burst();burst4.setCount(NumberFunction.constant(40));
        rlParticle4.config.getEmission().addBursts(burst4);burst4.time = 2;

        rlParticle4.config.getMaterial().setMaterial(MaterialHandle.VOID);

        Circle circle4 = new Circle();circle4.setRadius(2f);circle4.setRadiusThickness(1f);
        rlParticle4.config.getShape().setShape(circle4);

        rlParticle4.config.getNoise().open();
        rlParticle4.config.getNoise().setPosition(new NumberFunction3(0.5));

        rlParticle4.config.trails.open();
        rlParticle4.config.trails.config.getMaterial().setMaterial(MaterialHandle.CIRCLE);
        rlParticle4.config.trails.setColorOverLifetime(new Gradient(new GradientColor(0XFFFFFFFF,0XFFDFEF86)));
        rlParticle4.config.trails.config.getRenderer().setBloomEffect(true);

        RLParticle rlParticle5 = new RLParticle();
        rlParticle5.config.setDuration(20);
        rlParticle5.config.setStartLifetime(NumberFunction.constant(8));
        rlParticle5.config.setStartSpeed(NumberFunction.constant(1));
        rlParticle5.config.setStartSize(new NumberFunction3(5));

        rlParticle5.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst5 = new EmissionSetting.Burst();burst5.setCount(NumberFunction.constant(2));
        burst5.cycles = 2;burst5.interval = 2;
        rlParticle5.config.getEmission().addBursts(burst5);

        rlParticle5.config.getMaterial().setMaterial(MaterialHandle.RING);
        rlParticle5.config.getShape().setShape(new Dot());

        ((RendererSetting.Particle)rlParticle5.config.getRenderer()).setRenderMode(RendererSetting.Particle.Mode.Horizontal);

        rlParticle5.config.getColorOverLifetime().open();
        rlParticle5.config.getColorOverLifetime().setColor(new Gradient(new GradientColor(0XFFFFFFFF,0X00DFEF86)));

        rlParticle5.config.getSizeOverLifetime().open();
        rlParticle5.config.getSizeOverLifetime().setSize(new NumberFunction3(new Line(new float[]{0,1},new float[]{0,1})));

        RLParticle rlParticle6 = new RLParticle();
        rlParticle6.config.setDuration(4);
        rlParticle6.config.setStartLifetime(NumberFunction.constant(6));
        rlParticle6.config.setStartSpeed(NumberFunction.constant(1));
        rlParticle6.config.setStartSize(new NumberFunction3(4));

        rlParticle6.config.getEmission().setEmissionRate(NumberFunction.constant(0.5));
        rlParticle6.config.setStartColor(new Gradient(new GradientColor(0X48FFFFFF)));
        rlParticle6.config.getMaterial().setMaterial(MaterialHandle.CIRCLE);

        rlParticle6.config.getColorOverLifetime().open();
        rlParticle6.config.getColorOverLifetime().setColor(new Gradient(new GradientColor(0XFFDFEF86,0XFFDFEF86,0X00DFEF86)));

        BlockEffect blockEffect = new BlockEffect(level(),new Vec3(getX(),getY()+1,getZ()));
        rlParticle1.emmit(blockEffect);
        rlParticle2.emmit(blockEffect);
        rlParticle3.emmit(blockEffect);
        rlParticle4.emmit(blockEffect);
        rlParticle5.emmit(blockEffect);
        rlParticle6.emmit(blockEffect);
    }

    public void skillHalberd10(){
        RLParticle rlParticle1 = new RLParticle();
        rlParticle1.config.setDuration(70);
        rlParticle1.config.setStartLifetime(NumberFunction.constant(12));
        rlParticle1.config.setStartSpeed(NumberFunction.constant(0));
        rlParticle1.config.setStartColor(new Gradient(new GradientColor(0XFFDFEF86)));

        rlParticle1.config.getEmission().setEmissionRate(NumberFunction.constant(0.6));

        rlParticle1.config.getMaterial().setMaterial(MaterialHandle.VOID);

        Circle circle1 = new Circle();circle1.setRadius(4.5f);circle1.setRadiusThickness(1);
        rlParticle1.config.getShape().setShape(circle1);

        rlParticle1.config.getVelocityOverLifetime().open();
        rlParticle1.config.getVelocityOverLifetime().setLinear(new NumberFunction3(0,6,0));

        rlParticle1.config.getNoise().open();
        rlParticle1.config.getNoise().setPosition(new NumberFunction3(0.6));

        rlParticle1.config.trails.open();
        rlParticle1.config.trails.config.getMaterial().setMaterial(MaterialHandle.CIRCLE);
        rlParticle1.config.trails.setColorOverLifetime(new Gradient(GradientHandle.CENTER_OPAQUE));

        rlParticle1.config.trails.config.getRenderer().setBloomEffect(true);

        RLParticle rlParticle2 = new RLParticle();
        rlParticle2.config.setDuration(40);
        rlParticle2.config.setStartLifetime(NumberFunction.constant(60));
        rlParticle2.config.setStartSpeed(NumberFunction.constant(1));
        rlParticle2.config.setStartSize(new NumberFunction3(6));
        rlParticle2.config.setStartColor(new Gradient(new GradientColor(0XFFDFEF86)));

        rlParticle2.config.getEmission().setEmissionRate(NumberFunction.constant(0.05));
        EmissionSetting.Burst burst2 = new EmissionSetting.Burst();burst2.setCount(NumberFunction.constant(1));
        rlParticle2.config.getEmission().addBursts(burst2);

        rlParticle2.config.getMaterial().setMaterial(MaterialHandle.RING);
        rlParticle2.config.getShape().setShape(new Dot());

        rlParticle2.config.getRenderer().setRenderMode(RendererSetting.Particle.Mode.Horizontal);

        rlParticle2.config.getSizeOverLifetime().open();
        rlParticle2.config.getSizeOverLifetime().setSize(new NumberFunction3(new Line(new float[]{0,1},new float[]{0,1})));

        RLParticle rlParticle3 = new RLParticle();
        rlParticle3.config.setDuration(40);
        rlParticle3.config.setStartLifetime(NumberFunction.constant(50));
        rlParticle3.config.setStartSpeed(NumberFunction.constant(0));
        rlParticle3.config.setStartSize(new NumberFunction3(NumberFunction.constant(1),new RandomConstant(8,11,true),NumberFunction.constant(1)));
        rlParticle3.config.setStartColor(new Gradient(new GradientColor(0X6FDFEF86)));

        rlParticle3.config.getEmission().setEmissionRate(NumberFunction.constant(0.1));
        EmissionSetting.Burst burst3 = new EmissionSetting.Burst();burst3.setCount(NumberFunction.constant(20));
        rlParticle3.config.getEmission().addBursts(burst3);

        rlParticle3.config.getMaterial().setMaterial(MaterialHandle.CIRCLE);

        Circle circle3 = new Circle();circle3.setRadius(5f);circle3.setRadiusThickness(0.8f);
        rlParticle3.config.getShape().setShape(circle3);

        rlParticle3.config.getRenderer().setRenderMode(RendererSetting.Particle.Mode.VerticalBillboard);

        rlParticle3.config.getSizeOverLifetime().open();
        rlParticle3.config.getSizeOverLifetime().setSize(new NumberFunction3(NumberFunction.constant(1),new Line(new float[]{0,0.3f,1},new float[]{0,1,1}),NumberFunction.constant(1)));

        rlParticle3.config.getColorOverLifetime().open();
        rlParticle3.config.getColorOverLifetime().setColor(new Gradient(GradientHandle.CENTER_OPAQUE));

        BlockEffect blockEffect = new BlockEffect(level(), position().add(0,0.2,0));
        rlParticle1.emmit(blockEffect);
        rlParticle2.emmit(blockEffect);
        rlParticle3.emmit(blockEffect);
    }

    public void skillHalberd11(GeoBone geoBone,float scale){
        RLParticle rlParticle1 = new RLParticle();
        rlParticle1.config.setDuration(30);
        rlParticle1.config.setStartLifetime(NumberFunction.constant(6));
        rlParticle1.config.setStartSpeed(NumberFunction.constant(14));
        rlParticle1.config.setStartDelay(NumberFunction.constant(2));
        rlParticle1.config.setStartColor(new Gradient(new GradientColor(0XFFDFEF86)));

        rlParticle1.config.getEmission().setEmissionRate(NumberFunction.constant(0.6));
        EmissionSetting.Burst burst1 = new EmissionSetting.Burst();burst1.setCount(NumberFunction.constant(60));
        rlParticle1.config.getEmission().addBursts(burst1);

        rlParticle1.config.getMaterial().setMaterial(MaterialHandle.VOID);

        Sphere circle1 = new Sphere();
        rlParticle1.config.getShape().setShape(circle1);

        rlParticle1.config.getNoise().open();
        rlParticle1.config.getNoise().setPosition(new NumberFunction3(0.6));

        rlParticle1.config.trails.open();
        rlParticle1.config.trails.config.getMaterial().setMaterial(MaterialHandle.CIRCLE);
        rlParticle1.config.trails.setColorOverLifetime(new Gradient(new GradientColor(new float[]{0,0.95f,1},new int[]{0XFFFFFFFF,0XFFFFFFFF,0X00FFFFFF})));

        rlParticle1.config.trails.config.getRenderer().setBloomEffect(true);

        RLParticle rlParticle2 = new RLParticle();
        rlParticle2.config.setDuration(4);
        rlParticle2.config.setStartLifetime(NumberFunction.constant(8));
        rlParticle2.config.setStartSpeed(NumberFunction.constant(1));
        rlParticle2.config.setStartSize(new NumberFunction3(1.8));
        rlParticle2.config.setStartColor(new Gradient(new GradientColor(0X7FDFEF86)));

        rlParticle2.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst2 = new EmissionSetting.Burst();burst2.setCount(NumberFunction.constant(1));
        burst2.cycles = 0;
        rlParticle2.config.getEmission().addBursts(burst2);

        rlParticle2.config.getMaterial().setMaterial(MaterialHandle.CIRCLE);
        rlParticle2.config.getShape().setShape(new Dot());

        rlParticle2.config.getColorOverLifetime().open();
        rlParticle2.config.getColorOverLifetime().setColor(new Gradient(new GradientColor(0XFFFFFFFF,0XFFFFFFFF,0X00FFFFFF)));

        RLParticle rlParticle3 = new RLParticle();
        rlParticle3.config.setDuration(100);
        rlParticle3.config.setStartLifetime(NumberFunction.constant(6));
        rlParticle3.config.setStartSpeed(NumberFunction.constant(1));
        rlParticle3.config.setStartSize(new NumberFunction3(NumberFunction.constant(4.5),NumberFunction.constant(0.5),NumberFunction.constant(1)));
        rlParticle3.config.setStartColor(new Gradient(new GradientColor(0XFFFFDB7C)));

        rlParticle3.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst3 = new EmissionSetting.Burst();burst3.setCount(NumberFunction.constant(1));
        rlParticle3.config.getEmission().addBursts(burst3);

        rlParticle3.config.getMaterial().setMaterial(MaterialHandle.CIRCLE);

        rlParticle3.config.getShape().setShape(new Dot());

        rlParticle3.config.getSizeOverLifetime().open();
        rlParticle3.config.getSizeOverLifetime().setSize(new NumberFunction3(new Line(new float[]{0,0.15f,1},new float[]{0,1,1})));

        rlParticle3.config.getColorOverLifetime().open();
        rlParticle3.config.getColorOverLifetime().setColor(new Gradient(new GradientColor(0XFFFFFFFF,0XFFFFFFFF,0X00FFFFFF)));

        RLParticle rlParticle4 = new RLParticle();
        rlParticle4.config.setDuration(100);
        rlParticle4.config.setStartLifetime(NumberFunction.constant(6));
        rlParticle4.config.setStartSpeed(NumberFunction.constant(1));
        rlParticle4.config.setStartSize(new NumberFunction3(NumberFunction.constant(5),NumberFunction.constant(0.1),NumberFunction.constant(1)));

        rlParticle4.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst4 = new EmissionSetting.Burst();burst4.setCount(NumberFunction.constant(1));
        rlParticle4.config.getEmission().addBursts(burst4);

        rlParticle4.config.getMaterial().setMaterial(MaterialHandle.CIRCLE);
        rlParticle4.config.getRenderer().setBloomEffect(true);
        rlParticle4.config.getShape().setShape(new Dot());

        rlParticle4.config.getSizeOverLifetime().open();
        rlParticle4.config.getSizeOverLifetime().setSize(new NumberFunction3(new Line(new float[]{0,0.15f,1},new float[]{0,1,1})));

        rlParticle4.config.getColorOverLifetime().open();
        rlParticle4.config.getColorOverLifetime().setColor(new Gradient(new GradientColor(0XFFFFFFFF,0XFFFFFFFF,0X00FFFFFF)));

        GeoBoneEffect geoBoneEffect = new GeoBoneEffect(level(), this, geoBone);

        Vector3f vector3f = new Vector3f(scale, scale, scale);
        rlParticle1.updateScale(vector3f);
        rlParticle2.updateScale(vector3f);
        rlParticle3.updateScale(vector3f);
        rlParticle4.updateScale(vector3f);

        rlParticle1.emmit(geoBoneEffect);
        rlParticle2.emmit(geoBoneEffect);
        rlParticle3.emmit(geoBoneEffect);
        rlParticle4.emmit(geoBoneEffect);
    }

    public void skillHalberd12(Vec3 geoBone){
        RLParticle rlParticle1 = new RLParticle();

        rlParticle1.config.setDuration(100);
        rlParticle1.config.setStartLifetime(NumberFunction.constant(200));
        rlParticle1.config.setStartDelay(NumberFunction.constant(3));
        rlParticle1.config.setStartSpeed(new RandomConstant(90 ,5,true));
        rlParticle1.config.setStartSize(new NumberFunction3(1.1));
        rlParticle1.config.setMaxParticles(4000);
        rlParticle1.config.setStartColor(new Gradient(new GradientColor(0X31DFDDAF)));

        rlParticle1.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst1 = new EmissionSetting.Burst();burst1.setCount(NumberFunction.constant(1400));
        rlParticle1.config.getEmission().addBursts(burst1);

        rlParticle1.config.getMaterial().setMaterial(MaterialHandle.SMOKE);

        Circle circle1 = new Circle();
        rlParticle1.config.getShape().setShape(circle1);

        rlParticle1.config.getPhysics().open();
        rlParticle1.config.getPhysics().setHasCollision(false);
        rlParticle1.config.getPhysics().setFriction(NumberFunction.constant(0.93));

        rlParticle1.config.getVelocityOverLifetime().open();
        rlParticle1.config.getVelocityOverLifetime().setLinear(new NumberFunction3(NumberFunction.constant(0),new RandomLine(new float[]{0,0.083f,1},new float[]{4,0,0},new float[]{0,0,0}),NumberFunction.constant(0)));

        rlParticle1.config.getColorOverLifetime().open();
        rlParticle1.config.getColorOverLifetime().setColor(new Gradient(new GradientColor(0XFFFFFFFF,0XFFFFFFFF,0X00FFFFFF)));

        rlParticle1.config.getUvAnimation().open();
        rlParticle1.config.getUvAnimation().setTiles(new Range(2,2));

        rlParticle1.config.trails.open();
        rlParticle1.config.trails.setColorOverLifetime(new Gradient(new GradientColor(new float[]{1,0.96f,1},new int[]{0XFFFFFFFF,0XFFFFFFFF,0X00FFFFFF})));

        RLParticle rlParticle2 = new RLParticle();
        rlParticle2.config.setDuration(100);
        rlParticle2.config.setStartLifetime(NumberFunction.constant(9));
        rlParticle2.config.setStartSpeed(NumberFunction.constant(90));
        rlParticle2.config.setStartSize(new NumberFunction3(2));
        rlParticle2.config.setStartColor(new Gradient(new GradientColor(0XFFDFDDAF)));

        rlParticle2.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst2 = new EmissionSetting.Burst();burst2.setCount(NumberFunction.constant(200));
        burst2.cycles = 4;
        rlParticle2.config.getEmission().addBursts(burst2);

        rlParticle2.config.getMaterial().setMaterial(MaterialHandle.CIRCLE);

        rlParticle2.config.getShape().setShape(new Circle());

        rlParticle2.config.getVelocityOverLifetime().open();
        rlParticle2.config.getVelocityOverLifetime().setLinear(new NumberFunction3(NumberFunction.constant(0),NumberFunction.constant(1),NumberFunction.constant(0)));

        rlParticle2.config.getColorOverLifetime().open();
        rlParticle2.config.getColorOverLifetime().setColor(new Gradient(new GradientColor(0XFFFFFFFF,0XFFFFFFFF,0XFFFFFFFF,0X00FFFFFF)));

        rlParticle2.config.getSizeOverLifetime().open();
        rlParticle2.config.getSizeOverLifetime().setSize(new NumberFunction3(new Line(new float[]{0,1},new float[]{1,2})));

        RLParticle rlParticle3 = new RLParticle();
        rlParticle3.config.setDuration(100);
        rlParticle3.config.setStartLifetime(NumberFunction.constant(80));
        rlParticle3.config.setStartSpeed(NumberFunction.constant(1.5));
        rlParticle3.config.setStartSize(new NumberFunction3(0.2));
        rlParticle3.config.setStartColor(new Gradient(new GradientColor(0XFFDFDDAF)));

        rlParticle3.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst3 = new EmissionSetting.Burst();burst3.setCount(NumberFunction.constant(250));
        rlParticle3.config.getEmission().addBursts(burst3);

        rlParticle3.config.getMaterial().setMaterial(TBSMaterialHandle.PIXEL);

        Circle circle3 = new Circle();circle3.setRadius(40);
        rlParticle3.config.getShape().setShape(circle3);

        rlParticle3.config.getPhysics().open();
        rlParticle3.config.getPhysics().setFriction(NumberFunction.constant(0.99));
        rlParticle3.config.getPhysics().setBounceChance(NumberFunction.constant(0));
        rlParticle3.config.getPhysics().setGravity(new Line(new float[]{0,0.33f,1},new float[]{0,1,1}));

        rlParticle3.config.getVelocityOverLifetime().open();
        rlParticle3.config.getVelocityOverLifetime().setLinear(new NumberFunction3(NumberFunction.constant(0),new RandomLine(new float[]{0,0.3f,1},new float[]{15,0,0},new float[]{0,0,0}),NumberFunction.constant(0)));

        rlParticle3.config.getRotationOverLifetime().open();
        rlParticle3.config.getRotationOverLifetime().setRoll(new Line(new float[]{0,1},new float[]{0,360}));

        rlParticle3.config.trails.open();
        rlParticle3.config.trails.setColorOverLifetime(new Gradient(new GradientColor(0XFFFFFFFF,0X00FFFFFF,0X00FFFFFF,0X00FFFFFF)));
        rlParticle3.config.trails.open();

        BlockEffect geoBoneEffect = new BlockEffect(level(), geoBone);
        rlParticle1.emmit(geoBoneEffect);
        rlParticle2.emmit(geoBoneEffect);
        rlParticle3.emmit(geoBoneEffect);
    }

    public void addDemonArea(int time, Vec3 pos, int radio){
        demonCounterList.add(new DemonCounter(time,pos,radio));
    }

    @Override
    protected ConfigHandler.CombatConfig getCombatConfig() {
        return ConfigHandler.COMMON.MOBS.GUN_KNIGHT.combatConfig;
    }

    public void doGunShootFX(){
        Vec3 vec3 = clientVectors[0].subtract(clientVectors[1]);
        double d0 = vec3.horizontalDistance();
        float rotY =((float) (Mth.atan2(vec3.x, vec3.z)));
        float rotX =((float) (Mth.atan2(vec3.y, d0)));

        AdvancedParticleBase.spawnParticle(level(), ParticleHandler.BURST_MESSY.get(), clientVectors[0].x, clientVectors[0].y, clientVectors[0].z, 0, 0.01, 0, false, rotY, rotX, 0, 0, 50F, 1, 0.86, 0.12, 1, 1, 4, true, false, new ParticleComponent[]{
                new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, ParticleComponent.KeyTrack.startAndEnd(0f, 10f), false),
                new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, ParticleComponent.KeyTrack.startAndEnd(1f, 0.5f), false)
        });
    }

    private void doShootFX(){
        int tick = getAnimationTick();
        if(level().isClientSide){
            if(getAnimation()==MACHINE_GUN_1&&tickCount%2==0&&tick>=16&&tick<=80){
                doGunShootFX();
            }
        }
    }

    public boolean isInDemon(){
        boolean flad = false;
        for(int i = 0;i<demonCounterList.size();i++){
            DemonCounter demonCounter = demonCounterList.get(i);
            Vec3 pos = demonCounter.pos;
            if(pos.subtract(position()).length()<demonCounter.radio+0.5)
                flad = true;
        }
        return flad;
    }

    @Override
    public void setSpawnState(State spawnState) {
        this.spawnState = spawnState;
    }

    @Override
    public State getSpawnState() {
        return spawnState;
    }

    static class DemonCounter{
        public DemonCounter(int time,Vec3 pos,int radio) {
            this.time = time;
            this.radio = radio;
            this.pos = pos;
        }

        int time;
        int radio;
        Vec3 pos;

        public void update(Level level,GunKnightPatriot pursuer){
            time-=1;
            if(level.isClientSide()){
                if (pursuer.random.nextInt(12) == 0) {
                    level.playLocalSound(pos.x, pos.y, pos.z, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1.0F + pursuer.random.nextFloat(), pursuer.random.nextFloat() * 0.7F + 0.3F, false);
                }

                for (int i = 0; i <= radio; i++) {
                    Vec3 move = new Vec3(0, 0, 0.5 + i+pursuer.random.nextFloat()).yRot((float) 6.28 *pursuer.random.nextFloat());
                    Vec3 vec3 = pos.add(move);
                    float gray = 0.1F+pursuer.random.nextFloat()*0.1f;

                    AdvancedParticleBase.spawnParticle(level, ParticleHandler.PIXEL_GLOW.get(),vec3.x, vec3.y - 0.4, vec3.z, 0, 0.01+pursuer.random.nextFloat()*0.02, 0, false, pursuer.random.nextDouble() * 3.14d, 0, 0, 0, (float) (1d + pursuer.random.nextDouble() * 0.5d), 1, 0.8, 0, 1, 1, 20+pursuer.random.nextInt(16), true, false, new ParticleComponent[]{
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, new ParticleComponent.KeyTrack(new float[]{0,1,1,0},new float[]{0,0.3f,0.9f,1}), false),
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ROLL, ParticleComponent.KeyTrack.startAndEnd(0,1), false),
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.RED, ParticleComponent.KeyTrack.easeInCubic(1,gray), false),
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.GREEN, ParticleComponent.KeyTrack.startAndEnd(0.6f,gray), false)
                    });
                }

                if(pursuer.tickCount%3==0){
                    Vec3 move = new Vec3(0, -0.4, radio * pursuer.random.nextFloat()).yRot((float) 6.28 * pursuer.random.nextFloat()).add(pos);
                    level.addParticle(ParticleTypes.LARGE_SMOKE, move.x, move.y, move.z, 0, 0, 0);
                }
            }
            if(!level.isClientSide){
                Vec3 off = pos.subtract(pursuer.position());
                if(pursuer.tickCount%20==0){
                    List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class,pursuer.getBoundingBox().inflate(10).move(off),livingEntity ->
                            livingEntity != pursuer&&livingEntity.position().subtract(pos).length()<radio);
                    for(LivingEntity livingEntity:list){
                        if(livingEntity instanceof Player player&&player.isCreative()) continue;
                        livingEntity.hurt(pursuer.damageSources().mobAttack(pursuer),10);
                    }
                }
            }
        }

        public boolean disappear() {
            return time == 0;
        }
    }

    class DefendCounter{
        int effectiveAttackTick;

        public DefendCounter(int effectiveAttackCounter,int effectiveAttackTick) {
            this.effectiveAttackCounter = effectiveAttackCounter;
            this.effectiveAttackTick = effectiveAttackTick;
        }

        int effectiveAttackCounter;
    }
}
