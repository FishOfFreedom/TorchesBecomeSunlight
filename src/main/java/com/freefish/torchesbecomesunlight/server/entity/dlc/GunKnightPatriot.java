package com.freefish.torchesbecomesunlight.server.entity.dlc;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.util.particle.ParticleCloud;
import com.freefish.torchesbecomesunlight.server.entity.ITwoStateEntity;
import com.freefish.torchesbecomesunlight.server.entity.ai.entity.HalberdKnightPatriotAttackAI;
import com.freefish.torchesbecomesunlight.server.event.packet.toclient.InitClientEntityMessage;
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
import net.minecraft.network.chat.Component;
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
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.freefish.torchesbecomesunlight.server.entity.dlc.GunKnightPatriotAnimations.*;

public class GunKnightPatriot extends AnimatedEntity implements IDialogueEntity, RangedAttackMob , ITwoStateEntity {

    private static final AnimationAct[] ANIMATIONS = {
            NO_ANIMATION,WIND_MILL,ACK_HALBERD_R,ACK_HALBERD_L,ACK_HALBERD_CR,ACK_HALBERD_CL,RACK_HALBERD_CHI, LACK_HALBERD_DOWNCHI,LACK_HALBERD_TIAOWIND
            ,RACK_HALBERD_HEAVY,RACK_HALBERD_CYCLE2,ACK_HALBERD_CHI3,ACK_HALBERD_CHILEFT,MOVE_HALBERD_LEFT,MOVE_HALBERD_RIGHT,
            MOVE_HALBERD_CYCLE
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
        //this.goalSelector.addGoal(8, new FFWaterAvoidingRandomStrollGoal<>(this , 0.33));

        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Zombie.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Ravager.class, true));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        if(tickCount == 1&&!level().isClientSide)
            TorchesBecomeSunlight.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this),new InitClientEntityMessage(this,InitClientEntityMessage.InitDataType.ISTWOSTATE));

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

        if(getTarget() instanceof FrostNova) setTarget(null);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        float limit = (float)(getMaxHealth()*ConfigHandler.COMMON.MOBS.PATRIOT.damageConfig.damageCap.get());
        if(amount>limit&&!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) amount = limit;
        Entity entitySource = source.getDirectEntity();

        if(getAnimation()==STATE_2) return false;

        if (entitySource != null){
            return attackWithShield(source, amount);
        }
        else if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return super.hurt(source, amount);
        }
        return false;
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
        double d0 = pEntity.getX() - this.getX();
        double d2 = pEntity.getZ() - this.getZ();
        double d1;
        if (pEntity instanceof LivingEntity livingentity) {
            d1 = livingentity.getEyeY() - (getY()+1.75);
        } else {
            d1 = (pEntity.getBoundingBox().minY + pEntity.getBoundingBox().maxY) / 2.0D - this.getEyeY();
        }

        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        float f = (float)(Mth.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
        float f1 = (float)(-(Mth.atan2(d1, d3) * (double)(180F / (float)Math.PI)));
        this.setXRot(this.rotlerp(this.getXRot(), f1, pMaxXRotIncrease));
        this.setYRot(this.rotlerp(this.getYRot(), f, pMaxYRotIncrease));
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

    @OnlyIn(Dist.CLIENT)
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
}
