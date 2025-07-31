package com.freefish.torchesbecomesunlight.server.entity.guerrillas.shield;

import com.freefish.torchesbecomesunlight.client.util.particle.util.AdvancedParticleBase;
import com.freefish.torchesbecomesunlight.client.util.particle.util.ParticleComponent;
import com.freefish.torchesbecomesunlight.client.util.particle.util.RibbonComponent;
import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.PlayerCapability;
import com.freefish.torchesbecomesunlight.server.config.ConfigHandler;
import com.freefish.torchesbecomesunlight.server.entity.IChallengeEntity;
import com.freefish.torchesbecomesunlight.server.entity.TeamMemberStorage;
import com.freefish.torchesbecomesunlight.server.entity.ai.*;
import com.freefish.torchesbecomesunlight.server.entity.ai.entity.patriot.PatriotAttackAI;
import com.freefish.torchesbecomesunlight.server.entity.effect.EntityCameraShake;
import com.freefish.torchesbecomesunlight.server.entity.effect.StompEntity;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.GuerrillasEntity;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.ai.PatriotAutoDialogueGoal;
import com.freefish.torchesbecomesunlight.server.entity.projectile.HalberdOTIEntity;
import com.freefish.torchesbecomesunlight.server.init.*;
import com.freefish.torchesbecomesunlight.server.init.generator.advancement.criterion.TriggerHandler;
import com.freefish.torchesbecomesunlight.server.partner.Partner;
import com.freefish.torchesbecomesunlight.server.partner.PartnerUtil;
import com.freefish.torchesbecomesunlight.server.story.IDialogueEntity;
import com.freefish.torchesbecomesunlight.server.story.PlayerStoryStoneData;
import com.freefish.torchesbecomesunlight.server.story.data.DialogueEntry;
import com.freefish.torchesbecomesunlight.server.story.dialogueentity.DialogueEntity;
import com.freefish.torchesbecomesunlight.server.util.FFEntityUtils;
import com.freefish.torchesbecomesunlight.server.util.Parabola;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationAct;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import com.freefish.torchesbecomesunlight.server.util.bossbar.CustomBossInfoServer;
import com.freefish.torchesbecomesunlight.server.util.bossbar.FFBossInfoServer;
import com.freefish.torchesbecomesunlight.server.util.bossbar.IBossInfoUpdate;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public class Patriot extends GuerrillasEntity implements IDialogueEntity, IChallengeEntity {
    public static final AnimationAct<Patriot> ATTACK1 = new AnimationAct<Patriot>("attack1",40){
        @Override
        public void tickUpdate(Patriot entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
            LivingEntity target = entity.getTarget();
            if (target != null&&tick<=22) {
                entity.locateEntity();
                entity.lookAtEntity(target);
            } else {
                entity.setYRot(entity.yRotO);
            }

            if(tick == 30) {
                entity.playSound(SoundHandle.AXE_SWEPT.get(), 1.0F, 1.0F / (entity.random.nextFloat() * 0.4F + 0.8F));
                entity.dashForward(2,0);
            }
            else if (tick == 31) {
                entity.doRangeAttack(5.5,60,damage,true);
                EntityCameraShake.cameraShake(entity.level(), entity.position(), 16F, 0.04F, 5, 15);
            }
            if(tick == 30)
                entity.isCanBeAttacking = true;
        }

        @Override
        public void stop(Patriot entity) {
            entity.isCanBeAttacking = false;
            AnimationActHandler.INSTANCE.sendAnimationMessage(entity,Patriot.ATTACK2);
        }
    };
    public static final AnimationAct<Patriot> ATTACK2 = new AnimationAct<Patriot>("attack2",33){

        @Override
        public void tickUpdate(Patriot entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
            LivingEntity target = entity.getTarget();
            if (target != null&&tick<=16) {
                entity.locateEntity();
                entity.lookAtEntity(target);
            } else {
                entity.setYRot(entity.yRotO);
            }
            if(tick == 22) {
                entity.playSound(SoundHandle.AXE_SWEPT.get(), 1.0F, 1.0F / (entity.random.nextFloat() * 0.4F + 0.8F));
                entity.dashForward(2,0);
            }
            else if (tick == 24) {
                entity.doRangeAttack(5.5,60,damage,true);
                FFEntityUtils.doRangeAttackFX(entity,5.5,60,0);
                entity.isCanBeAttacking = false;
            }
        }
        @Override
        public void stop(Patriot entity) {
            entity.isCanBeAttacking = false;
            if(entity.random.nextInt(2)==0)
                AnimationActHandler.INSTANCE.sendAnimationMessage(entity,Patriot.ATTACK3);
            else
                AnimationActHandler.INSTANCE.sendAnimationMessage(entity,Patriot.PIERCE2);
        }
    };
    public static final AnimationAct<Patriot> ATTACK3 = new AnimationAct<Patriot>("attack3",60){

        @Override
        public void tickUpdate(Patriot entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
            LivingEntity target = entity.getTarget();
            if (target != null&&tick<=40) {
                entity.locateEntity();
                entity.lookAtEntity(target);
            } else {
                entity.setYRot(entity.yRotO);
            }
            if (tick == 40) {
                entity.doRangeAttack(6.5,30,damage*1.5f,true);
                FFEntityUtils.doRangeAttackFX(entity,6.5,30,0);
                StompEntity stompEntity = new StompEntity(entity.level(),8,entity,3);
                stompEntity.setPos(entity.position().add(new Vec3(0, 0, 4.5).yRot((float) (-entity.yBodyRot / 180 * Math.PI))));
                entity.level().addFreshEntity(stompEntity);
                entity.playSound(SoundHandle.GROUND.get(), 1.3F, 1.0F / (entity.random.nextFloat() * 0.4F + 0.8F));
            }
            if(tick==40) entity.isCanBeAttacking = true;
            else if(tick==50) entity.isCanBeAttacking = false;
        }
        @Override
        public void stop(Patriot entity) {
            entity.isCanBeAttacking = false;
            super.stop(entity);
        }
    };
    public static final AnimationAct<Patriot> PIERCE1 = new AnimationAct<Patriot>("pierce_1",33){

        @Override
        public void tickUpdate(Patriot entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
            LivingEntity target = entity.getTarget();
            if (target != null&&(tick<2||tick>21)) {
                entity.locateEntity();
                entity.lookAtEntity(target);
            } else {
                entity.setYRot(entity.yRotO);
            }
            boolean flad = false;
            if (tick == 7||tick == 10||tick == 15||tick == 20) {
                flad = entity.doRangeAttack(5.7,25,damage,true);
                FFEntityUtils.doRangeAttackFX(entity,5.7,25,0);
            }
            if(tick == 10 && flad)
                entity.playSound(SoundHandle.HIT.get(), 1.0F, 1.0F / (entity.random.nextFloat() * 0.4F + 0.8F));
            if(tick==4) entity.isCanBeAttacking = true;
            else if(tick==30) entity.isCanBeAttacking = false;
            if(tick==20&&entity.random.nextInt(2)==1)
                AnimationActHandler.INSTANCE.sendAnimationMessage(entity,CYCLE);
        }
        @Override
        public void stop(Patriot entity) {
            entity.isCanBeAttacking = false;
            super.stop(entity);
        }
    };
    public static final AnimationAct<Patriot> PIERCE2 = new AnimationAct<Patriot>("pierce_2",46){

        @Override
        public void tickUpdate(Patriot entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
            LivingEntity target = entity.getTarget();
            if (target != null&&(tick<17||tick>35)) {
                entity.locateEntity();
                entity.lookAtEntity(target);
            } else {
                entity.setYRot(entity.yRotO);
            }
            boolean flad = false;
            if (tick == 21||tick == 24||tick == 29||tick == 34) {
                flad = entity.doRangeAttack(5.7,25,damage,true);
                FFEntityUtils.doRangeAttackFX(entity,5.7,25,0);
            }
            if(tick == 24 && flad)
                entity.playSound(SoundHandle.HIT.get(), 1.0F, 1.0F / (entity.random.nextFloat() * 0.4F + 0.8F));
            if(tick==16) entity.isCanBeAttacking = true;
            else if(tick==43) entity.isCanBeAttacking = false;
            if(tick==35&&entity.random.nextInt(2)==1)
                AnimationActHandler.INSTANCE.sendAnimationMessage(entity,CYCLE);
        }
        @Override
        public void stop(Patriot entity) {
            entity.isCanBeAttacking = false;
            super.stop(entity);
        }
    };
    public static final AnimationAct<Patriot> RUN = new  AnimationAct<Patriot>("run",200){
        @Override
        public void tickUpdate(Patriot entity) {
            LivingEntity target = entity.getTarget();
            if(target!=null) {
                entity.getLookControl().setLookAt(target, 30F, 30F);
                entity.lookAt(target, 30F, 30F);
                if(target.distanceTo(entity)<5+target.getBbWidth()/2)
                    AnimationActHandler.INSTANCE.sendAnimationMessage(entity, Patriot.PIERCE1);
            }
        }
    };
    public static final AnimationAct<Patriot> CYCLE = new AnimationAct<Patriot>("cycle",45){

        @Override
        public void tickUpdate(Patriot entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
            LivingEntity target = entity.getTarget();
            entity.setYRot(entity.yRotO);
            if (tick == 24) {
                entity.playSound(SoundHandle.AXE_SWEPT.get(), 1.0F, 1.0F / (entity.random.nextFloat() * 0.4F + 0.8F));
                entity.doCycleAttack(5.8f,damage);
            }
            if(tick==37) entity.isCanBeAttacking = false;
        }
        @Override
        public void stop(Patriot entity) {
            entity.isCanBeAttacking = false;
            super.stop(entity);
        }
    };
    public static final AnimationAct<Patriot> PROPEL1 = new AnimationAct<Patriot>("propel1",30){

        @Override
        public void tickUpdate(Patriot entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
            LivingEntity target = entity.getTarget();
            if (target != null&&tick<=24) {
                entity.locateEntity();
                entity.lookAtEntity(target);
            } else {
                entity.setYRot(entity.yRotO);
            }
            if(tick == 24){
                entity.dashForward(2,0);
            }
            else if (tick == 26) {
                entity.doRangeAttack(4.5,60,damage*0.8f,true);
                if(target!=null)
                    entity.LocateEntity(target,5,10);
            }
        }

        @Override
        public void stop(Patriot entity) {
            AnimationActHandler.INSTANCE.sendAnimationMessage(entity,PROPEL2);
        }
    };
    public static final AnimationAct<Patriot> TOUJI = new AnimationAct<Patriot>("touji",30){

        @Override
        public void tickUpdate(Patriot entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
            LivingEntity target = entity.getTarget();
            if (target != null&&tick<=24) {
                entity.locateEntity();
                entity.lookAtEntity(target);
            } else {
                entity.setYRot(entity.yRotO);
            }
            if(tick == 24){
                entity.dashForward(2,0);
            }
            else if (tick == 26) {
                entity.doRangeAttack(4.5,60,damage*0.8f,true);
                if(target!=null)
                    entity.LocateEntity(target,5,10);
            }
        }

        @Override
        public void stop(Patriot entity) {
            AnimationActHandler.INSTANCE.sendAnimationMessage(entity,PROPEL2);
        }
    };
    public static final AnimationAct<Patriot> PROPEL2 = new AnimationAct<Patriot>("propel2",40){
        @Override
        public void tickUpdate(Patriot entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
            LivingEntity target = entity.getTarget();
            if (target != null) {
                entity.lookAt(target,30f,30f);
            } else {
                entity.setYRot(entity.yRotO);
            }

            if(tick >=10&&tick<=30&&target!=null){
                entity.setDeltaMovement(new Vec3(0,0,0.3).yRot((float) -Math.toRadians(FFEntityUtils.getPosToPosRot(entity.position(),target.position()))));
            }
            if (tick == 39) {
                entity.playSound(SoundHandle.GROUND.get(), 1.0F, 1.0F / (entity.random.nextFloat() * 0.4F + 0.8F));
                entity.setLocateMobId(-1);
                entity.doRangeAttack(5,60,damage*0.8f,true);
                entity.propelEntity(5,60);
            }
            if(tick==30) entity.isCanBeAttacking = true;
        }

        @Override
        public void stop(Patriot entity) {
            entity.setLocateMobId(-1);
            AnimationActHandler.INSTANCE.sendAnimationMessage(entity,PROPEL3);
        }
    };
    public static final AnimationAct<Patriot> PROPEL3 = new AnimationAct<Patriot>("propel3",44){
        @Override
        public void start(Patriot entity) {
            super.start(entity);
            entity.timeSinceThrow = 0;
        }

        @Override
        public void tickUpdate(Patriot entity) {
            int tick = entity.getAnimationTick();
            entity.setYRot(entity.yRotO);
            entity.locateEntity();
            if(tick==5) entity.isCanBeAttacking = false;
            else if(tick==20) entity.isCanBeAttacking = true;
            else if(tick==35) entity.isCanBeAttacking = false;
        }
        @Override
        public void stop(Patriot entity) {
            entity.isCanBeAttacking = false;
            super.stop(entity);
        }
    };
    public static final AnimationAct<Patriot> SHIELD = new AnimationAct<Patriot>("shield_attack",25){

        @Override
        public void tickUpdate(Patriot entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
            LivingEntity target = entity.getTarget();
            if (target != null) {
                entity.getLookControl().setLookAt(target, 30F, 30F);
                entity.lookAt(target, 30F, 30F);
            } else {
                entity.setYRot(entity.yRotO);
            }
            if(tick==14){
                entity.dashForward(2,0);
            }
            else if (tick == 17&&target != null) {
                entity.doRangeAttack(4,60,damage,true);
                entity.doRangeKnockBack(4,60,2);
                FFEntityUtils.doRangeAttackFX(entity,4,60,0);
            }
        }
        @Override
        public void stop(Patriot entity) {
            entity.isCanBeAttacking = false;
            super.stop(entity);
        }
    };
    public static final AnimationAct<Patriot> THROW = new AnimationAct<Patriot>("throw",62){
        @Override
        public void tickUpdate(Patriot entity) {
            int tick = entity.getAnimationTick();
            entity.setYRot(entity.yRotO);
            entity.locateEntity();
            if(tick==37) entity.isCanBeAttacking = true;
            else if(tick==50) entity.isCanBeAttacking = false;
        }
        @Override
        public void stop(Patriot entity) {
            entity.isCanBeAttacking = false;
            super.stop(entity);
        }
    };
    public static final AnimationAct<Patriot> STRENGTHEN = new AnimationAct<Patriot>("strengthen",30){
        @Override
        public void tickUpdate(Patriot entity) {
            int tick = entity.getAnimationTick();
            entity.setYRot(entity.yRotO);
            entity.locateEntity();
            if(tick == 11){
                List<GuerrillasEntity> entitiesNearby = entity.level().getEntitiesOfClass(GuerrillasEntity.class, entity.getBoundingBox().inflate(12));
                for(GuerrillasEntity guerrilla:entitiesNearby){
                    guerrilla.addEffect(new MobEffectInstance(EffectHandle.SONG_OF_GUERRILLA.get(),400,1));
                }
            }
        }
    };
    public static final AnimationAct<Patriot> HUNT = new AnimationAct<Patriot>("hunt",60){

        @Override
        public void tickUpdate(Patriot entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
            LivingEntity target = entity.getTarget();
            if (target != null&&tick<=31) {
                entity.getLookControl().setLookAt(target, 30F, 30F);
                entity.lookAt(target, 30F, 30F);
            } else {
                entity.setYRot(entity.yRotO);
            }
            if(tick == 31){
                float jumpLen;
                if(target!=null){
                    jumpLen = (float) Math.max(0,target.position().subtract(entity.position()).length()-2)/4.5f;
                }else {
                    jumpLen = 2;
                }
                Vec3 direction = new Vec3(0, Math.sqrt(jumpLen)*0.1, jumpLen).yRot((float) (-entity.getYRot() / 180 * org.joml.Math.PI));
                entity.setDeltaMovement(direction);
            }
            else if (tick == 41) {
                entity.playSound(SoundHandle.GROUND.get(), 1.0F, 1.0F / (entity.random.nextFloat() * 0.4F + 0.8F));
                entity.doRangeAttack(6,80,damage*2.5f,true);
                StompEntity stompEntity = new StompEntity(entity.level(),8,entity,3);
                stompEntity.setPos(entity.position().add(new Vec3(0, 0, 5.5).yRot((float) (-entity.yBodyRot / 180 * Math.PI))));
                entity.level().addFreshEntity(stompEntity);
                entity.addEffect(new MobEffectInstance(EffectHandle.WINDIGO.get()));
            }
            if(tick == 6) entity.isCanBeAttacking = true;
            else if(tick == 50) entity.isCanBeAttacking = false;
        }
        @Override
        public void stop(Patriot entity) {
            entity.isCanBeAttacking = false;
            super.stop(entity);
        }
    };
    public static final AnimationAct<Patriot> STOMP = new AnimationAct<Patriot>("stomp",31){
        @Override
        public void tickUpdate(Patriot entity) {
            entity.setYRot(entity.yRotO);
            int tick = entity.getAnimationTick();
            entity.locateEntity();
            if (tick == 17){
                StompEntity stompEntity = new StompEntity(entity.level(),16,entity,5);
                stompEntity.setPos(entity.position());
                entity.level().addFreshEntity(stompEntity);
            }
        }
        @Override
        public void stop(Patriot entity) {
            entity.isCanBeAttacking = false;
            super.stop(entity);
        }
    };
    public static final AnimationAct<Patriot> SHIELD_START = new AnimationAct<Patriot>("shield_start",31){
        @Override
        public void tickUpdate(Patriot entity) {
            entity.locateEntity();
            entity.setYRot(entity.yRotO);
        }
    };
    public static final AnimationAct<Patriot> SHIELD_LOOP = new AnimationAct<Patriot>("shield_loop",31){
        @Override
        public void tickUpdate(Patriot entity) {
            entity.locateEntity();
            entity.setYRot(entity.yRotO);
        }
    };
    public static final AnimationAct<Patriot> BREAK_START = new AnimationAct<Patriot>("break_start",50,2){

        @Override
        public void tickUpdate(Patriot entity) {
            entity.setYRot(entity.yRotO);
            int tick = entity.getAnimationTick();

            entity.setLastHurtByMob(null);
            entity.setTarget(null);

            entity.locateEntity();
            if(tick<=16&&tick%4==0){
                float health = (tick+4)/20.f;
                entity.setHealth(entity.getMaxHealth()*health);
            }
        }
    };

    public LivingEntity waitAct;
    public static final AnimationAct<Patriot> ATTACK_PREPARE = new  AnimationAct<Patriot>("attack_prepare",400,1){
        @Override
        public void tickUpdate(Patriot entity) {
            entity.locateEntity();
            if(entity.waitAct!=null){
                if(entity.waitAct.distanceTo(entity)>7&&entity.waitAct.getY()-entity.getY()<3&&entity.waitAct.getY()-entity.getY()>-3){
                    entity.setTarget(entity.waitAct);
                    stop(entity);
                }
            }
        }

        @Override
        public void stop(Patriot entity) {
            entity.waitAct=null;
            super.stop(entity);
        }
    };

    public static final AnimationAct<Patriot> DIE = new AnimationAct<Patriot>("death",45,1);

    private static final AnimationAct[] ANIMATIONS = {
            NO_ANIMATION,ATTACK1,ATTACK2,ATTACK3,PIERCE1,PIERCE2,RUN,CYCLE,SHIELD,THROW,STOMP,PROPEL1,PROPEL2,PROPEL3,STRENGTHEN,HUNT,
            DIE,ATTACK_PREPARE,BREAK_START,SHIELD_START,SHIELD_LOOP
    };

    public Parabola parabola = new Parabola();
    public boolean isCanBeAttacking = false;

    @OnlyIn(Dist.CLIENT)
    public Vec3[] clientVectors;

    public int timeSinceThrow;
    public TeamMemberStorage<ShieldGuard> teamMemberStorage;
    public int timeSinceShield;

    private final ServerBossEvent bossInfo= ConfigHandler.COMMON.GLOBALSETTING.healthBarIsNearShow.get()?new FFBossInfoServer(this,2): new CustomBossInfoServer(this,2);

    private static final EntityDataAccessor<Integer> LOCATE_MOB_ID = SynchedEntityData.defineId(Patriot.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Float> TARGET_POSX = SynchedEntityData.defineId(Patriot.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TARGET_POSY = SynchedEntityData.defineId(Patriot.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TARGET_POSZ = SynchedEntityData.defineId(Patriot.class, EntityDataSerializers.FLOAT);

    public Patriot(EntityType<? extends Patriot> entityType, Level level) {
        super(entityType, level);
        if (level().isClientSide)
            clientVectors = new Vec3[] {new Vec3(0, 0, 0)};
        else {
            teamMemberStorage = new TeamMemberStorage<>(this);
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PatriotAutoDialogueGoal(this));
        this.goalSelector.addGoal(2, new PatriotAttackAI(this));

        this.goalSelector.addGoal(7, new FFLookAtPlayerTotherGoal<>(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new FFRandomLookAroundGoal<>(this));
        this.goalSelector.addGoal(8, new FFWaterAvoidingRandomStrollGoal<>(this , 0.19));

        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Zombie.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Ravager.class, true));
        this.targetSelector.addGoal(3, new HurtByTargetNoPlayerGoal(this).setAlertOthers(GuerrillasEntity.class));
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
    protected void repelEntities(float x, float y, float z, float radius) {
        int tick = getAnimationTick();
        if(getAnimation()==PROPEL2&&tick>=38&&tick<43){

        }
        else
            super.repelEntities(x, y, z, radius);
    }

    public void spawnShield(){
        if(!level().isClientSide){
            int len = 2 + random.nextInt(2);
            for (int i = 0; i < len; i++) {
                ShieldGuard shieldGuard = new ShieldGuard(EntityHandle.SHIELD_GUARD.get(), level());
                shieldGuard.setPos(position());
                teamMemberStorage.addMember(shieldGuard);
                level().addFreshEntity(shieldGuard);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (tickCount % 4 == 0&& bossInfo instanceof IBossInfoUpdate update) update.update();

        if(!level().isClientSide){
            teamMemberStorage.tick();
        }

        Entity locateEntity = getLocateMob();
        if(locateEntity instanceof LivingEntity){
            Vec3 move = getLocateVec().subtract(locateEntity.position());
            locateEntity.setDeltaMovement(move);
        }

        LivingEntity target = this.getTarget();
        if(target != null && target.isAlive())
            setTargetPos(target.position());

        repelEntities(1.7F, 4.5f, 1.7F, 1.7F);

        if(getAnimation()==THROW){
            ShootHalberd(0);
        }
        else if(getAnimation()==PROPEL3)
            ShootHalberd(-20);

        if(!level().isClientSide){
            if(timeSinceShield<100) timeSinceShield++;
        }

        float moveX = (float) (getX() - xo);
        float moveZ = (float) (getZ() - zo);
        float speed = Mth.sqrt(moveX * moveX + moveZ * moveZ);
        if(this.level().isClientSide && speed > 0.03) {
            if (tickCount % 18 == 1 &&getAnimation() == NO_ANIMATION)
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundHandle.GIANT_STEP.get(), this.getSoundSource(), 20F, 1F, false);
            if (tickCount % 10 == 1 &&getAnimation() == RUN)
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundHandle.GIANT_STEP.get(), this.getSoundSource(), 20F, 1F, false);
        }
        addIceWindParticle();

        if(waitTargetTime>=0){
            if(waitTargetTime == 0){
                if(challengePlayer!=null){
                    setTarget(challengePlayer);
                }
            }
            waitTargetTime--;
        }
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
    protected ConfigHandler.NeutralProtectionConfig getNeutralProtectionConfig() {
        return ConfigHandler.COMMON.MOBS.PATRIOT.neutralProtectionConfig;
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
    public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
        return super.removeWhenFarAway(pDistanceToClosestPlayer);
    }

    @Override
    public void die(DamageSource pDamageSource) {
        if(challengePlayer==null||!challengePlayer.isAlive()){
            super.die(pDamageSource);
            if (!this.isRemoved() && bossInfo instanceof IBossInfoUpdate update) {
                update.update();
            }
            if (teamMemberStorage != null) {
                Collection<ShieldGuard> members = teamMemberStorage.getMembers();
                for (ShieldGuard shieldGuard : members) {
                    shieldGuard.setLeader(null);
                }
            }
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
    public boolean hurt(DamageSource source, float amount) {
        if(getAnimation()==BREAK_START) return false;
        float limit = (float)(getMaxHealth()*ConfigHandler.COMMON.MOBS.PATRIOT.damageConfig.damageCap.get());
        if(amount>limit&&!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) amount = limit;

        if(source.getEntity()!=null&&source.getEntity() == challengePlayer){
            amount*=3.5f;
        }

        Entity entitySource = source.getDirectEntity();
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
        return DIE;
    }

    @Override
    public boolean canCollideWith(Entity pEntity) {
        return false;
    }

    @Override
    public float getStepHeight() {
        return 1.5F;
    }

    @Override
    public void setAnimation(AnimationAct animation) {
        if(level().isClientSide){
            if(animation == NO_ANIMATION) hasTrail = false;
            else if(animation==ATTACK1||animation==ATTACK2||animation==ATTACK3||animation==CYCLE||
                    animation==PIERCE1||animation==PIERCE2||animation==HUNT) hasTrail = true;
        }
        super.setAnimation(animation);
    }

    @Override
    protected boolean canBePushedByEntity(Entity entity) {
        return false;
    }

    @Override
    protected <T extends GeoEntity> PlayState predicate(AnimationState<T> animationState) {
        return super.predicate(animationState);
    }

    public boolean attackWithShield(DamageSource source, float amount){
        Entity entitySource = source.getDirectEntity();
        if (entitySource != null) {
            AnimationAct a = getAnimation();
            if ((!isCanBeAttacking&&isAggressive()&&ConfigHandler.COMMON.MOBS.PATRIOT.isFrontalAttack.get())) {
                int arc = 90;
                float entityHitAngle = (float) ((Math.atan2(entitySource.getZ() - getZ(), entitySource.getX() - getX()) * (180 / Math.PI) - 90) % 360);
                float entityAttackingAngle = getYRot() % 360;
                if (entityHitAngle < 0) {
                    entityHitAngle += 360;
                }
                if (entityAttackingAngle < 0) {
                    entityAttackingAngle += 360;
                }
                if(Math.abs(entityAttackingAngle-entityHitAngle)<arc) {
                    playSound(SoundEvents.SHIELD_BREAK,0.4f,2);
                    addShieldArmorParticle();
                    if(!level().isClientSide&&timeSinceShield>=100&&getAnimation()==NO_ANIMATION&&entitySource == getTarget()&&entitySource.distanceTo(this)<3+getTarget().getBbWidth()/2)
                        AnimationActHandler.INSTANCE.sendAnimationMessage(this,SHIELD);
                    return false;
                }
                else
                    return super.hurt(source, amount);
            } else {
                playSound(SoundEvents.SHIELD_BLOCK,0.4f,2);
                return super.hurt(source, amount);
            }
        }
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

    public static AttributeSupplier.Builder createAttributes() {
        return GuerrillasEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 300.0D)
                .add(Attributes.ATTACK_DAMAGE, 20.0f)
                .add(Attributes.ARMOR, 20.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 10.0D)
                .add(Attributes.FOLLOW_RANGE, 48)
                .add(Attributes.MOVEMENT_SPEED, 1f)
                .add(Attributes.KNOCKBACK_RESISTANCE,1f);
    }

    @Override
    protected ConfigHandler.CombatConfig getCombatConfig() {
        return ConfigHandler.COMMON.MOBS.PATRIOT.combatConfig;
    }

    @Override
    public AnimationAct[] getAnimations() {
        return ANIMATIONS;
    }
    private final AnimationController<Patriot> shield = new AnimationController<Patriot>(this, "shieldController", 5, this::shieldPredicate);

    private PlayState shieldPredicate(AnimationState<Patriot> event) {
        event.setAnimation(RawAnimation.begin().thenLoop("shieldfx"));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar event) {
        event.add(shield);
        super.registerControllers(event);
    }

    @Override
    protected <T extends GeoEntity> void basicAnimation(AnimationState<T> event) {
        float dist = (float) position().subtract(xo,yo,zo).length();
        if (isAggressive()) {
            if (dist>0.02)
                event.getController().setAnimation(RawAnimation.begin().thenLoop("march"));
            else
                event.getController().setAnimation(RawAnimation.begin().thenLoop("idle_aggressive"));
        }
        else{
            if (dist>0.02)
                event.getController().setAnimation(RawAnimation.begin().thenLoop("walk_peace"));
            else
                event.getController().setAnimation(RawAnimation.begin().thenLoop("idle_peace"));
        }
    }

    @Override
    public boolean doHurtEntity(LivingEntity livingEntity, DamageSource source, float damage) {
        if(livingEntity == challengePlayer){
            damage/=2.5f;
        }
        boolean b = super.doHurtEntity(livingEntity, source, damage);

        if(this.getAnimation()== Patriot.PIERCE1||this.getAnimation()==Patriot.PIERCE2){
            livingEntity.invulnerableTime=1;
        }
        return b;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TARGET_POSX, 0f);
        this.entityData.define(TARGET_POSY, 0f);
        this.entityData.define(TARGET_POSZ, 0f);
        this.entityData.define(LOCATE_MOB_ID, -1);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        Vector3f vector3f = getTargetPos();
        compound.putFloat("targetPosX",vector3f.x);
        compound.putFloat("targetPosY",vector3f.y);
        compound.putFloat("targetPosZ",vector3f.z);
        compound.put("menber",teamMemberStorage.serializeNBT());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        float x = compound.getFloat("targetPosX");
        float y = compound.getFloat("targetPosY");
        float z = compound.getFloat("targetPosZ");
        setTargetPos(new Vec3(x,y,z));
        teamMemberStorage.deserializeNBT(compound.getCompound("menber"));
    }

    @Override
    public SoundEvent getBossMusic() {
        return SoundHandle.PATRIOT_UNYIELDING.get();
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false;
    }

    private void addShieldArmorParticle(){
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


    public void setLocateMobId(int i) {
        this.entityData.set(LOCATE_MOB_ID, i);
    }

    public int getLocateMobId() {
        return this.entityData.get(LOCATE_MOB_ID);
    }

    public void LocateEntity(LivingEntity entityHit,double range, double arc){
        float entityHitAngle = (float) ((Math.atan2(entityHit.getZ() - getZ(), entityHit.getX() - getX()) * (180 / Math.PI) - 90) % 360);
        float entityAttackingAngle = yBodyRot % 360;
        if (entityHitAngle < 0) {
            entityHitAngle += 360;
        }
        if (entityAttackingAngle < 0) {
            entityAttackingAngle += 360;
        }
        float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
        float entityHitDistance = (float) Math.sqrt((entityHit.getZ() - getZ()) * (entityHit.getZ() - getZ()) + (entityHit.getX() - getX()) * (entityHit.getX() - getX())) - entityHit.getBbWidth() / 2f;
        if (entityHitDistance <= range && (entityRelativeAngle <= arc / 2 && entityRelativeAngle >= -arc / 2) || (entityRelativeAngle >= 360 - arc / 2 || entityRelativeAngle <= -360 + arc / 2)) {
            setLocateMobId(entityHit.getId());
        }
    }

    public void propelEntity(double range, double arc){
        List<LivingEntity> entitiesHit = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(range, 3, range), e -> e != this && distanceTo(e) <= range + e.getBbWidth() / 2f && e.getY() <= getY() + 3);
        for (LivingEntity entityHit : entitiesHit) {
            float entityHitAngle = (float) ((Math.atan2(entityHit.getZ() - getZ(), entityHit.getX() - getX()) * (180 / Math.PI) - 90) % 360);
            float entityAttackingAngle = yBodyRot % 360;
            if (entityHitAngle < 0) {
                entityHitAngle += 360;
            }
            if (entityAttackingAngle < 0) {
                entityAttackingAngle += 360;
            }
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            float entityHitDistance = (float) Math.sqrt((entityHit.getZ() - getZ()) * (entityHit.getZ() - getZ()) + (entityHit.getX() - getX()) * (entityHit.getX() - getX())) - entityHit.getBbWidth() / 2f;
            if (entityHitDistance <= range && (entityRelativeAngle <= arc / 2 && entityRelativeAngle >= -arc / 2) || (entityRelativeAngle >= 360 - arc / 2 || entityRelativeAngle <= -360 + arc / 2)) {
                Vec3 propel = new Vec3(0, 2.5, 1.5).yRot((float) (-this.yBodyRot / 180 * Math.PI));
                entityHit.setDeltaMovement(propel);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void setClientVectors(int index, Vec3 pos) {
        if (clientVectors != null && clientVectors.length > index) {
            clientVectors[index] = pos;
        }
    }

    public Entity getLocateMob() {
        int id = getLocateMobId();
        return id == -1 ? null : level().getEntity(id);
    }

    public Vec3 getLocateVec(){
        Vec3 locate;
        if(getAnimation()==PROPEL2) {
            int tick = getAnimationTick();
            locate = position().add(new Vec3(0, 0.2, 4.1-tick/40.0f).yRot((float) (-this.yBodyRot / 180 * Math.PI)));
        }
        else
            locate = position().add(new Vec3(0, 0.2, 4.1).yRot((float) (-this.yBodyRot / 180 * Math.PI)));

        return locate;
    }

    public void ShootHalberd(int offset){
        int tick = getAnimationTick();
        Vector3f targetPos = getTargetPos();

        Partner<?> partner = PartnerUtil.getPartner(this);
        if(partner!=null){
            Vec3 instancePos = partner.getInstancePos();
            if(instancePos!=null){
                targetPos = new Vector3f(instancePos.toVector3f());
            }
        }

        if (tick == (36+offset) || tick ==(46+offset)) {
            parabola.mathParabola(this, targetPos);
        }
        if (this.level().isClientSide) {
            if (tick >= (36+offset) && tick < (52+offset)) {
                double i = ((double) (tick - (36+offset))) / 17;
                double i1 = ((double) (tick - (35+offset))) / 17;
                double posX = getX() + parabola.getX() * i;
                double posZ = getZ() + parabola.getZ() * i;
                double targetPosX = getX() + parabola.getX() * i1;
                double targetPosZ = getZ() + parabola.getZ() * i1;
                double x3 = parabola.getX2() * i;
                double targetX3 = parabola.getX2() * i1;
                double posY = getY() + parabola.getY(x3);
                double targetPosY = getY() + parabola.getY(targetX3);
                Vec3 particleMotion = new Vec3(posX-targetPosX, posY-targetPosY,posZ-targetPosZ);
                int smokeNumber = 4 + random.nextInt(5);
                for(int j =1;j<=smokeNumber;j++){
                    double speed = 0.5 + random.nextDouble();
                    Vec3 newParticleMotion = particleMotion.yRot((float)(Math.PI*Math.cos(2*i/smokeNumber*Math.PI)/6)).xRot((float)(Math.PI*Math.sin(2*i/smokeNumber*Math.PI)/6)).scale(speed);
                    level().addParticle(ParticleTypes.SMOKE, posX,posY,posZ,newParticleMotion.x,newParticleMotion.y,newParticleMotion.z);
                }
                AdvancedParticleBase.spawnParticle(level(), ParticleHandler.ARROW_HEAD.get(), posX, posY, posZ, 0, 0, 0, false, 0, 0, 0, 0, 40f, 1, 1, 1, 0.75, 1, 2, true, false, new ParticleComponent[]{
                        new ParticleComponent.Attractor(new Vec3[]{new Vec3(targetPosX,targetPosY,targetPosZ)}, 0.5f, 0.2f, ParticleComponent.Attractor.EnumAttractorBehavior.LINEAR),
                        new RibbonComponent(ParticleHandler.RIBBON_FLAT.get(), 10, 0, 0, 0, 0.8F, 1, 1, 1, 0.75, true, true, new ParticleComponent[]{
                                new RibbonComponent.PropertyOverLength(RibbonComponent.PropertyOverLength.EnumRibbonProperty.SCALE, ParticleComponent.KeyTrack.startAndEnd(1, 0))
                        }),
                        new ParticleComponent.FaceMotion(),
                        new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, new ParticleComponent.KeyTrack(new float[]{0, 0, 1}, new float[]{0, 0.05f, 0.06f}), false),
                });
            }
        }
        LivingEntity target = getTarget();
        if (tick == (50+offset)) {
            double i = ((double) (tick - (36+offset))) / 17;
            double x3 = parabola.getX2() * i;

            Vec3 move = new Vec3(0,0,0);
            if(target!=null) {
                int dist = (int)(target.distanceTo(this));
                move = target.getDeltaMovement().scale(dist);
            }

            double posX = getX()+ move.x + parabola.getX() * i;
            double posZ = getZ()+ move.z + parabola.getZ() * i;
            double posY = getY()+ move.y + parabola.getY(x3);
            Vec3 vector3d = new Vec3(targetPos.x - posX,targetPos.y -posY,targetPos.z -posZ);
            float f = (float) vector3d.horizontalDistance();
            float yaw = (float)(Mth.atan2(vector3d.x, vector3d.z) * (double)(180F / (float)Math.PI));
            float pitch = (float)(Mth.atan2(vector3d.y, (double)f) * (double)(180F / (float)Math.PI));
            HalberdOTIEntity ganRanZheZhiJi = new HalberdOTIEntity(level(), this, new ItemStack(Items.TRIDENT),true);
            ganRanZheZhiJi.absMoveTo(getX() + parabola.getX()*i,getY() + parabola.getY(x3),getZ() + parabola.getZ()*i, -pitch, yaw);
            ganRanZheZhiJi.shootFromRotation(this, -pitch, -yaw, 0.0F, 6F, 0f);
            ganRanZheZhiJi.setNoGravity(true);
            if(!level().isClientSide)level().addFreshEntity(ganRanZheZhiJi);
        }
    }

    private void addIceWindParticle(){
        if(getAnimation() == HUNT) {
            int tick = getAnimationTick();
            if(level().isClientSide) {
            }
        }
    }

    //trail
    private Vec3[][] trailPositions = new Vec3[64][2];
    private int trailPointer = -1;
    public boolean hasTrail = false;

    public Vec3[] getTrailPosition(int pointer, float partialTick) {
        if (this.isRemoved()) {
            partialTick = 1.0F;
        }
        int i = this.trailPointer - pointer & 63;
        int j = this.trailPointer - pointer - 1 & 63;
        Vec3[] d0 = this.trailPositions[j];
        Vec3 t0 = this.trailPositions[i][0].subtract(d0[0]);
        Vec3 t1 = this.trailPositions[i][1].subtract(d0[1]);
        Vec3[] d1 = new Vec3[]{t0,t1};
        Vec3 tt0 = d0[0].add(d1[0].scale(partialTick));
        Vec3 tt1 = d0[1].add(d1[1].scale(partialTick));
        Vec3[] d2 = new Vec3[]{tt1,tt0};

        return d2;
    }

    public void updateTrail(Vec3 trailAt1,Vec3 trailAt2) {
        if (trailPointer == -1) {
            Vec3 backAt1 = trailAt1;
            Vec3 backAt2 = trailAt2;
            for (int i = 0; i < trailPositions.length; i++) {
                trailPositions[i] = new Vec3[]{backAt1,backAt2};
            }
        }
        if (++this.trailPointer == this.trailPositions.length) {
            this.trailPointer = 0;
        }
        this.trailPositions[this.trailPointer] = new Vec3[]{trailAt1,trailAt2};
    }

    public boolean hasTrail() {
        return trailPointer != -1&&hasTrail;
    }

    //Dialogue
    private DialogueEntity dialogueEntity;
    private int startBlack;
    private int startBlackO;

    public float getStartBlack(float pa){
        return Mth.lerp(pa,startBlackO,startBlack);
    }

    public boolean isStartBlack(){
        return startBlack != 0;
    }

    @Override
    public boolean canDialogue() {
        return !isAggressive()&&getDialogueEntity()==null;
    }

    @Override
    public void playDeathAnimationPre(DamageSource source) {
        if(challengePlayer!=null){

            if(challengePlayer instanceof ServerPlayer serverPlayer){
                TriggerHandler.STRING_ADVANCEMENT_TRIGGER.trigger(serverPlayer, "main_8_2_patriot");
            }
            setAggressive(false);
            setHealth(1);
            PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(challengePlayer, CapabilityHandle.PLAYER_CAPABILITY);
            if(capability!=null){
                PlayerStoryStoneData playerStory = capability.getPlayerStory();
                playerStory.setWinOPatriot(true);
                playerStory.setCanSummonOPatriot(true);
            }
            challengePlayer = null;
            AnimationActHandler.INSTANCE.sendAnimationMessage(this,BREAK_START);
        }else {
            Entity entity = source.getEntity();
            if(entity instanceof Player player){
                PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(player, CapabilityHandle.PLAYER_CAPABILITY);
                if(capability!=null){
                    PlayerStoryStoneData playerStory = capability.getPlayerStory();
                }
            }
        }
    }

    @Override
    public void startDialogue(Player player) {
        PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(player, CapabilityHandle.PLAYER_CAPABILITY);
        if(capability!=null){
            PlayerStoryStoneData playerStory = capability.getPlayerStory();
            if(playerStory.isWinOPatriot()){
                DialogueEntity dialogueEntity1 = startTalk("dialogue/patriot/patriot_win.json", this, player);
                DialogueEntry dialogueEntry = dialogueEntity1.getAllDialogue().getDialogueEntry("main2");
                dialogueEntry.setRunnable(()->{
                    Inventory inventory = player.getInventory();
                    inventory.add(new ItemStack(ItemHandle.INFECTED_SHIELD.get()));
                    inventory.add(new ItemStack(ItemHandle.INFECTED_HALBERD.get()));
                    inventory.add(new ItemStack(ItemHandle.TALISMAN.get()));
                    playerStory.setWinOPatriot(false);
                });
            }
            else if(playerStory.isSeenOPatriot()){
                DialogueEntity dialogueEntity1 = startTalk("dialogue/patriot/patriot_fight.json", this, player);
                DialogueEntry dialogueEntry = dialogueEntity1.getAllDialogue().getDialogueEntry("main2");
                dialogueEntry.setRunnable(()->{
                    startChallengePlayer(player);
                });
            }else {
                DialogueEntity dialogueEntity1 = startTalk("dialogue/patriot/patriot_firstmeet.json", this, player);
                DialogueEntry dialogueEntry = dialogueEntity1.getAllDialogue().getDialogueEntry("main2");
                dialogueEntry.setRunnable(()->{
                    playerStory.setSeenOPatriot(true);
                });
            }
        }
    }

    @Override
    public DialogueEntity getDialogueEntity() {
        if(dialogueEntity!=null&&!dialogueEntity.isAlive()) dialogueEntity = null;

        return dialogueEntity;
    }

    @Override
    public void setDialogueEntity(DialogueEntity dialogueEntity) {
        this.dialogueEntity = dialogueEntity;
    }


    public Player challengePlayer;
    private int waitTargetTime = -1;

    public void startChallengePlayer(Player player){
        challengePlayer = player;
        this.waitTargetTime = 120;
    }

    @Override
    public LivingEntity getChallenge() {
        return challengePlayer;
    }
}
