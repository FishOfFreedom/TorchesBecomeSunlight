package com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster;

import com.freefish.rosmontislib.client.particle.advance.base.particle.RLParticle;
import com.freefish.rosmontislib.client.particle.advance.data.VelocityOverLifetimeSetting;
import com.freefish.rosmontislib.client.particle.advance.data.material.MaterialHandle;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction3;
import com.freefish.rosmontislib.client.particle.advance.data.number.RandomConstant;
import com.freefish.rosmontislib.client.particle.advance.data.number.color.Gradient;
import com.freefish.rosmontislib.client.particle.advance.data.number.color.GradientHandle;
import com.freefish.rosmontislib.client.particle.advance.data.shape.Circle;
import com.freefish.rosmontislib.client.particle.advance.effect.EntityEffect;
import com.freefish.torchesbecomesunlight.client.util.particle.ParticleCloud;
import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.DialogueCapability;
import com.freefish.torchesbecomesunlight.server.capability.FrozenCapability;
import com.freefish.torchesbecomesunlight.server.capability.PlayerCapability;
import com.freefish.torchesbecomesunlight.server.config.ConfigHandler;
import com.freefish.torchesbecomesunlight.server.init.ParticleHandler;
import com.freefish.torchesbecomesunlight.client.util.particle.util.AdvancedParticleBase;
import com.freefish.torchesbecomesunlight.client.util.particle.util.ParticleComponent;
import com.freefish.torchesbecomesunlight.server.entity.ai.*;
import com.freefish.torchesbecomesunlight.server.entity.ai.entity.snownova.SnowNova1AttackAI;
import com.freefish.torchesbecomesunlight.server.entity.dlc.GunKnightPatriot;
import com.freefish.torchesbecomesunlight.server.entity.effect.EntityCameraShake;
import com.freefish.torchesbecomesunlight.server.entity.effect.dialogueentity.DialogueEntity;
import com.freefish.torchesbecomesunlight.server.entity.IDialogueEntity;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.GuerrillasEntity;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.shield.Patriot;
import com.freefish.torchesbecomesunlight.server.entity.projectile.BigIceCrystal;
import com.freefish.torchesbecomesunlight.server.entity.projectile.IceBlade;
import com.freefish.torchesbecomesunlight.server.entity.projectile.IceCrystal;
import com.freefish.torchesbecomesunlight.server.init.SoundHandle;
import com.freefish.torchesbecomesunlight.server.story.dialogue.Dialogue;
import com.freefish.torchesbecomesunlight.server.story.dialogue.DialogueStore;
import com.freefish.torchesbecomesunlight.server.util.FFEntityUtils;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationAct;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import com.freefish.torchesbecomesunlight.server.util.bossbar.CustomBossInfoServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class FrostNova extends GuerrillasEntity implements IDialogueEntity {
    private static final EntityDataAccessor<Float> TARGET_X = SynchedEntityData.defineId(FrostNova.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TARGET_Y = SynchedEntityData.defineId(FrostNova.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TARGET_Z = SynchedEntityData.defineId(FrostNova.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(FrostNova.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> SECOND_HEALTH = SynchedEntityData.defineId(FrostNova.class, EntityDataSerializers.FLOAT);

    public int timeSinceJump;
    private LivingEntity dialogueLivingEntity;
    public int timeSinceBackJump;
    private int defendWilling;
    public int timeSinceIceBomb;
    public int timeSinceIceJump_1;

    private final CustomBossInfoServer secondHealth = new CustomBossInfoServer(this,3){
        @Override
        public void updateHealth() {
            this.setProgress(getSecondHealth() / (getMaxHealth()/4));
        }
    };

    private final CustomBossInfoServer bossInfo= new CustomBossInfoServer(this,0);

    public static final AnimationAct<FrostNova> WALK_PEACE =new AnimationAct<FrostNova>("walk_forward2",0);
    public static final AnimationAct<FrostNova> IDLE_PEACE = new AnimationAct<FrostNova>("idle_peace",0);
    public static final AnimationAct<FrostNova> WALK_AGGRESSIVE = new AnimationAct<FrostNova>("walk_aggressive2",0);
    public static final AnimationAct<FrostNova> IDLE_AGGRESSIVE = new AnimationAct<FrostNova>("idle_aggressive",0);
    public static final AnimationAct<FrostNova> RIGHT_JUMP =new AnimationAct<FrostNova>("rightjump",15);
    public static final AnimationAct<FrostNova> LEFT_JUMP = new AnimationAct<FrostNova>("leftjump",15);
    public static final AnimationAct<FrostNova> DIE = new AnimationAct<FrostNova>("die",100,1);
    public static final AnimationAct<FrostNova> BACK_JUMP = new AnimationAct<FrostNova>("backjump",16){
        @Override
        public void tickUpdate(FrostNova entity) {
            int tick = entity.getAnimationTick();
            RandomSource random = entity.getRandom();
            Entity target = entity.getTarget();
            if(target!=null)
                entity.getLookControl().setLookAt(target);
            if(tick==1) {
                if(target!=null)
                    entity.absFaceEntity(target);
                Vec3 direction = new Vec3(0, 0.3, -2-random.nextFloat()/2).yRot((float) (-entity.yBodyRot / 180 * Math.PI));
                entity.setPos(new Vec3(0, 0.2, -1.2).yRot((float) (-entity.yBodyRot / 180 * Math.PI)).add(entity.position()));
                entity.setDeltaMovement(direction);
            }
        }

        @Override
        public void stop(FrostNova entity) {
            LivingEntity target = entity.getTarget();
            if(target!=null) {
                entity.cycleRadius = entity.distanceTo(target);
                double dist = entity.getTarget().distanceTo(entity);
                int add = 0;
                if(target instanceof Player) add = 60;
                if(dist>5&&dist<16)
                    entity.startCycle(20+add);
            }
            super.stop(entity);
        }
    };

    public static final AnimationAct<FrostNova> ATTACK_1 = new AnimationAct<FrostNova>("attack_1",25){
        @Override
        public void tickUpdate(FrostNova entity) {
            int tick = entity.getAnimationTick();
            LivingEntity target = entity.getTarget();
            float damage = (float)entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
            if(tick<6&&target!=null) {
                entity.locateEntity();
                entity.lookAtEntity(target);
            }
            else {
                entity.setYRot(entity.yRotO);
            }
            if(tick==9) {
                entity.dashForward(3,0);
            }
            else if (tick == 11) {
                entity.doRangeAttack(2.5,60,damage,true);
                FFEntityUtils.doRangeAttackFX(entity,2.5,60,0);
            }
        }

        @Override
        public void stop(FrostNova entity) {
            LivingEntity target = entity.getTarget();
            if(target!=null&&target.distanceTo(entity)<5)
                AnimationActHandler.INSTANCE.sendAnimationMessage(entity, FrostNova.BACK_JUMP);
            else super.stop(entity);
        }
    };

    public static final AnimationAct<FrostNova> ATTACK_2 = new  AnimationAct<FrostNova>("attack_2",27){
        @Override
        public void tickUpdate(FrostNova entity) {
            int tick = entity.getAnimationTick();
            LivingEntity target = entity.getTarget();
            float damage = (float)entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
            if((tick<2||(tick>8&&tick<13))&&target!=null) {
                entity.lookAtEntity(target);
            }
            else {
                entity.setYRot(entity.yRotO);
            }

            if(tick==7) {
                entity.dashForward(3,0);
            }
            else if (tick == 15) {
                entity.dashForward(6,0);
            }
            else if (tick == 8) {
                entity.doRangeAttack(2.5,60,damage,false);
                FFEntityUtils.doRangeAttackFX(entity,2.5,60,0);
            }
            else if (tick == 16) {
                entity.doRangeAttack(2.5,20,damage,false);
                FFEntityUtils.doRangeAttackFX(entity,2.5,20,0);
            }
        }
        @Override
        public void stop(FrostNova entity) {
            LivingEntity target = entity.getTarget();
            if(target!=null&&target.distanceTo(entity)<5)
                AnimationActHandler.INSTANCE.sendAnimationMessage(entity, FrostNova.BACK_JUMP);
            else super.stop(entity);
        }
    };

    public static final AnimationAct<FrostNova> REMOTE_1 = new  AnimationAct<FrostNova>("remote_1",20){
        @Override
        public void tickUpdate(FrostNova entity) {
            int tick = entity.getAnimationTick();
            entity.locateEntity();
            LivingEntity target = entity.getTarget();
            if(target!=null) {
                entity.getLookControl().setLookAt(target);
                if (tick == 6) {
                    entity.shootIceCrystal(target,entity.position().add(new Vec3(0,1.5,0)));
                }
            }
        }

        @Override
        public void stop(FrostNova entity) {
            LivingEntity target = entity.getTarget();
            if(target!=null&&target.distanceTo(entity)<6+target.getBbWidth()/2) {
                AnimationActHandler.INSTANCE.sendAnimationMessage(entity, FrostNova.BACK_JUMP);
            }
            else
                super.stop(entity);
        }
    };

    public static final AnimationAct<FrostNova> REMOTE_12 = new  AnimationAct<FrostNova>("remote_12",50){
        @Override
        public void tickUpdate(FrostNova entity) {
            int tick = entity.getAnimationTick();
            entity.locateEntity();
            LivingEntity target = entity.getTarget();
            RandomSource random = entity.random;
            if(target!=null) {
                entity.getLookControl().setLookAt(target);
                if (tick == 15) {
                    IceCrystal.spawnWaitCrystal(entity.level(),new Vec3(4-random.nextInt(9),2+random.nextInt(5),4-random.nextInt(9)).add(entity.position()),entity,target);
                }
            }
        }

        @Override
        public void stop(FrostNova entity) {
            LivingEntity target = entity.getTarget();
            if(target!=null&&target.distanceTo(entity)<6+target.getBbWidth()/2) {
                AnimationActHandler.INSTANCE.sendAnimationMessage(entity, FrostNova.BACK_JUMP);
            }
            else
                super.stop(entity);
        }
    };

    public static final AnimationAct<FrostNova> REMOTE_2 = new  AnimationAct<FrostNova>("remote_2",32,2){
        @Override
        public void tickUpdate(FrostNova entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
            if(tick<8)
                entity.locateEntity();
            else if(tick<=15)
                entity.setForwardMotion(0.1f);
            entity.setYRot(entity.yRotO);
            if (tick == 17) {
                entity.bomb1(6,damage);
            }
        }
    };

    public static final AnimationAct<FrostNova> REMOTE_3 = new  AnimationAct<FrostNova>("temp_1",35){
        @Override
        public void tickUpdate(FrostNova entity) {
            int tick = entity.getAnimationTick();
            LivingEntity target = entity.getTarget();
            if(tick==1){
                Vec3 direction = new Vec3(0, 0.15, -0.8-entity.random.nextFloat()/2).yRot((float) (-entity.yBodyRot / 180 * Math.PI));
                entity.setDeltaMovement(direction);
            }
            if(tick>=10&&tick<=20)
                entity.setForwardMotion(0.15f);
            if(target!=null) {
                entity.getLookControl().setLookAt(target);
                if (tick == 27)
                    entity.shootIceBlade(target);
            }
        }

        @Override
        public void stop(FrostNova entity) {
            LivingEntity target = entity.getTarget();
            if(target!=null&&target.distanceTo(entity)<5+target.getBbWidth()/2)
                AnimationActHandler.INSTANCE.sendAnimationMessage(entity, FrostNova.BACK_JUMP);
            else
                super.stop(entity);
        }
    };

    public LivingEntity waitAct;
    public static final AnimationAct<FrostNova> ATTACK_PREPARE = new  AnimationAct<FrostNova>("attack_prepare",400,1){
        @Override
        public void tickUpdate(FrostNova entity) {
            entity.locateEntity();
            //todo dialogueIsOver
            if(entity.waitAct!=null){
                if(entity.waitAct.distanceTo(entity)>10&&entity.waitAct.getY()-entity.getY()<3&&entity.waitAct.getY()-entity.getY()>-3){
                    entity.setTarget(entity.waitAct);
                    stop(entity);
                }
            }
        }

        @Override
        public void stop(FrostNova entity) {
            entity.waitAct=null;
            super.stop(entity);
        }
    };

    public static final AnimationAct<FrostNova> DASH = new  AnimationAct<FrostNova>("dash_2",20,2){
        @Override
        public void tickUpdate(FrostNova entity) {
            int tick = entity.getAnimationTick();
            LivingEntity target = entity.getTarget();
            if(tick>=3&&tick<=10) entity.setYRot(entity.yRotO);
            else if(target!=null) entity.getLookControl().setLookAt(target,30f,30f);
            if(tick>=7&&tick<=8)
                entity.doDash();
        }

        @Override
        public void stop(FrostNova entity) {
            LivingEntity target = entity.getTarget();
            if(target!=null&&target.distanceTo(entity)<6+target.getBbWidth()/2) {
                AnimationActHandler.INSTANCE.sendAnimationMessage(entity, FrostNova.BACK_JUMP);
            }
            else
                super.stop(entity);
        }
    };

    public static final AnimationAct<FrostNova> DASH_RUN = new  AnimationAct<FrostNova>("dash_1",40){
        @Override
        public void tickUpdate(FrostNova entity) {
            LivingEntity target = entity.getTarget();
            if(target!=null&&target.distanceTo(entity)<2+target.getBbWidth()/2)
                AnimationActHandler.INSTANCE.sendAnimationMessage(entity,DASH);
        }
    };

    public static final AnimationAct<FrostNova> ICE_JUMP = new AnimationAct<FrostNova>("temp_2",42,2){
        @Override
        public void tickUpdate(FrostNova entity) {
            int tick = entity.getAnimationTick();
            LivingEntity target = entity.getTarget();
            float temp = 0;
            if(tick==2||tick==1) {
                if(target!=null&&target.distanceTo(entity)<9+target.getBbWidth()/2) {
                    float speed = entity.getTargetMove(target);
                    if(speed>15) temp = (float) Math.PI / 2;
                }
                entity.setPos(entity.position().add(0,0.3,0));
                entity.setDeltaMovement(new Vec3(0, 1, 1.5).yRot((float) (-entity.yBodyRot / 180 * Math.PI + temp)));
            }
            if(target!=null) {
                if (tick == 14 ||tick == 12) {
                    entity.shootIceCrystal(target,entity.position().add(new Vec3(0,1.5,0)));
                }
            }
            if(tick>38) entity.locateEntity();
        }
    };
    public static final AnimationAct<FrostNova> ICE_BOMB = new AnimationAct<FrostNova>("ice_bomb",73,2){
        @Override
        public void tickUpdate(FrostNova entity) {
            int tick = entity.getAnimationTick();
            entity.locateEntity();
            float damage = (float)entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
            entity.setYRot(entity.yRotO);
            //todo
            if(tick==56){
                List<LivingEntity> livingEntities = entity.level().getEntitiesOfClass(LivingEntity.class,entity.getBoundingBox().inflate(15),entity1 ->
                        entity1!=entity&&entity1.distanceTo(entity)<=12&&entity.getY()-entity1.getY()<=3);
                for(LivingEntity livingEntity:livingEntities){
                    float dist = Math.lerp(3f,1,livingEntity.distanceTo(entity) / 12);
                    livingEntity.hurt(entity.damageSources().mobAttack(entity),damage*dist);

                    FrozenCapability.IFrozenCapability data = CapabilityHandle.getCapability(entity, CapabilityHandle.FROZEN_CAPABILITY);
                    if(data!=null) data.setFrozen(livingEntity,100);
                }
            }
        }
    };

    public static final AnimationAct<FrostNova> ICE_GROUND = new AnimationAct<FrostNova>("skill_2",60,2){
        @Override
        public void tickUpdate(FrostNova entity) {
            int tick = entity.getAnimationTick();
            entity.locateEntity();
            if(tick==30){
                int time = 3 + entity.random.nextInt(3);
                LivingEntity target = entity.getTarget();
                if(target!=null){
                    BigIceCrystal bigIceCrystal = new BigIceCrystal(entity.level(),entity);
                    bigIceCrystal.setPos(target.position().add(0,5,0));
                    entity.level().addFreshEntity(bigIceCrystal);
                }
                for(int i = 0;i<time;i++){
                    Vec3 newPosition = new Vec3(0,5,entity.random.nextFloat()*6+2).yRot((float)Math.PI*2*i/time);
                    BigIceCrystal bigIceCrystal = new BigIceCrystal(entity.level(),entity);
                    bigIceCrystal.setPos(entity.position().add(newPosition));
                    entity.level().addFreshEntity(bigIceCrystal);
                }
            }
        }

        @Override
        public void stop(FrostNova entity) {
            LivingEntity target = entity.getTarget();
            if(target!=null&&target.distanceTo(entity)<5)
                AnimationActHandler.INSTANCE.sendAnimationMessage(entity, FrostNova.BACK_JUMP);
            else
                super.stop(entity);
        }
    };

    public static final AnimationAct<FrostNova> KICK = new AnimationAct<FrostNova>("backward_kick",45){
        @Override
        public void tickUpdate(FrostNova entity) {
            int tick = entity.getAnimationTick();
            entity.locateEntity();
            LivingEntity target = entity.getTarget();
            float damage = (float)entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
            if(target!=null) {
                float dist = target.distanceTo(entity);
                entity.getLookControl().setLookAt(target);
                if(tick==14){
                    if(dist<1.5){
                        target.hurt(entity.damageSources().mobAttack(entity),damage*0.8f);
                        Vec3 knock = target.position().subtract(entity.position()).normalize();
                        target.setDeltaMovement(knock);
                    }
                }
            }
        }
    };
    public static final AnimationAct<FrostNova> DEFEND = new AnimationAct<FrostNova>("defend",20,2){
        @Override
        public void tickUpdate(FrostNova entity) {
            entity.defendWilling = 0;
            int tick = entity.getAnimationTick();
            entity.locateEntity();
            LivingEntity target = entity.getTarget();
            if(target!=null) {
                entity.lookAtEntity(target);
            }
        }
    };

    public static final AnimationAct<FrostNova> ICE_WIND = new AnimationAct<FrostNova>("ice_wind",65){
        @Override
        public void tickUpdate(FrostNova entity) {
            int tick = entity.getAnimationTick();
            entity.locateEntity();
            float damage = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
            LivingEntity target = entity.getTarget();

            if(target!=null) {
                entity.lookAtEntity(target);
            }

            if(tick>25&&tick<=55&&tick%5==0){
                entity.doRangeAttack(12,24,damage,false);
                FFEntityUtils.doRangeAttackFX(entity,12,24,0);
            }
        }
    };
    public static final AnimationAct<FrostNova> LULLABYE_2 = new AnimationAct<FrostNova>("lullabye_2",200,2){//465*6
        @Override
        public void tickUpdate(FrostNova entity) {
            entity.locateEntity();
            int tick = entity.getAnimationTick();
            LivingEntity target = entity.getTarget();
            if(target!=null) {
                entity.lookAtEntity(target);
            }
            RandomSource random = entity.random;
            if(target!=null&&tick>=20&&tick<=120&&tick%10==0){
                IceCrystal.spawnWaitCrystal(entity.level(),new Vec3(4-random.nextInt(9),2+random.nextInt(5),4-random.nextInt(9)).add(entity.position()),entity,target);
            }
            if(target!=null&&tick==140){
                for(int i=0;i<6;i++){
                    IceCrystal.spawnWaitCrystal(entity.level(), new Vec3(4 - random.nextInt(9), 2 + random.nextInt(5), 4 - random.nextInt(9)).add(entity.position()), entity, target);
                }
            }
        }
    };
    public static final AnimationAct<FrostNova> REBORN = new AnimationAct<FrostNova>("reborn",105,1){
        @Override
        public void tickUpdate(FrostNova entity) {
            int tick = entity.getAnimationTick();
            entity.locateEntity();
            float damage = (float)entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
            entity.setYRot(entity.yRotO);
            if(tick==88){
                entity.bomb1(12,damage*2);
            }
            if(tick==1) {
                entity.setSecondHealth(entity.getMaxHealth()/4);
                entity.setHealth(entity.getMaxHealth() / 3);
            }
        }

        @Override
        public void stop(FrostNova entity) {
            LivingEntity target = entity.getTarget();
            if(target!=null&&target.distanceTo(entity)<8+target.getBbWidth()/2) {
                AnimationActHandler.INSTANCE.sendAnimationMessage(entity, FrostNova.BACK_JUMP);
            }
            else
                super.stop(entity);
        }
    };
    public static final AnimationAct<FrostNova> CYCLE_WIND = new AnimationAct<FrostNova>("cyclewind",40,2){
        @Override
        public void tickUpdate(FrostNova entity) {
            int tick = entity.getAnimationTick();
            entity.locateEntity();
            entity.setYRot(entity.yRotO);
            if(tick==14)
                entity.setSecondHealth(entity.getMaxHealth()/4);
        }
    };

    @Override
    public AnimationAct[] getAnimations() {
        return new AnimationAct[]{NO_ANIMATION,DEFEND,LULLABYE_2,CYCLE_WIND,ATTACK_1,ATTACK_2,RIGHT_JUMP,LEFT_JUMP,BACK_JUMP,REMOTE_1,REMOTE_12,REMOTE_2,REMOTE_3,ATTACK_PREPARE
                ,DASH,ICE_JUMP,ICE_BOMB,ICE_GROUND,KICK,DIE,ICE_WIND,REBORN,DASH_RUN};
    }

    @Override
    @NotNull
    protected BodyRotationControl createBodyControl() {
        return new FFBodyRotationControl(this);
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false;
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new FFPathNavigateGround(this, level);
    }

    public FrostNova(EntityType<? extends FrostNova> entityType, Level level) {
        super(entityType, level);
        if(!level.isClientSide){
            setSecondHealth(getMaxHealth()/4);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar event) {
        super.registerControllers(event);
        event.add(new AnimationController<FrostNova>(this, "IceController", 5, this::icePredicate));
        event.add(new AnimationController<FrostNova>(this, "B", 5, this::flatPredicate));
    }

    private PlayState icePredicate(AnimationState<FrostNova> event) {
        if(getAnimation()== FrostNova.REBORN)
            event.setAnimation(RawAnimation.begin().thenLoop("icefloatbomb"));
        else {
            if(getState()==0)
                event.setAnimation(RawAnimation.begin().thenLoop("icefloatremove"));
            else if(getState()==1)
                event.setAnimation(RawAnimation.begin().thenLoop("iceflote"));
        }
        return PlayState.CONTINUE;
    }

    private PlayState flatPredicate(AnimationState<FrostNova> event) {
        event.setAnimation(RawAnimation.begin().thenLoop("idle_peace2"));
        return PlayState.CONTINUE;
    }

    int timeSinceWithoutWind;

    @Override
    public void tick() {
        if(!level().isClientSide()&&!donDodge()){
            LivingEntity target = getTarget();
            if(target!=null){
                float speed = getTargetMove(target);
                Vec3 face = getTargetMoveVec(target).normalize();
                Vec3 targetTo = position().subtract(target.position());
                double faceLen = face.length();
                if(faceLen!=0&&target.distanceTo(this)<8) {
                    double len = face.dot(targetTo);
                    if(len>0&&speed>15) {
                        double length = face.multiply(len, 0, len).subtract(targetTo).length();
                        if(length<3+target.getBbWidth()/2&&timeSinceIceJump_1>=120) {
                            timeSinceIceJump_1 = 0;
                            AnimationActHandler.INSTANCE.sendAnimationMessage(this, ICE_JUMP);
                        }
                    }
                }
            }
            if(target!=null) {
                if (position().subtract(target.position()).horizontalDistance() < 6) {
                    if (target.getDeltaMovement().y < -0.1 && target.getY() - getY() > 3&&timeSinceIceJump_1>=120) {
                        timeSinceIceJump_1 = 0;
                        AnimationActHandler.INSTANCE.sendAnimationMessage(this, FrostNova.ICE_JUMP);
                    }
                }
                //todo ICEWALL
                if(defendWilling >4&&!isInIceWall()&&target.distanceTo(this)>6) {
                    defendWilling=0;
                    AnimationActHandler.INSTANCE.sendAnimationMessage(this, FrostNova.DEFEND);
                }
            }
        }

        super.tick();

        for(int i = 0;i<demonCounterList.size();i++){
            DemonCounter demonCounter = demonCounterList.get(i);
            demonCounter.update(level(),this);

            if(demonCounter.disappear()){
                demonCounter.iceBreak(level());
                demonCounterList.remove(i);
                i -= 1;
            }
        }

        if (tickCount % 4 == 0) {
            bossInfo.update();
            secondHealth.update();
        }

        if(!level().isClientSide()){
            if(getSecondHealth()<=10&&timeSinceWithoutWind<300){
                timeSinceWithoutWind++;
            }
            if(timeSinceWithoutWind>=(getState()==1?200:300)&&getAnimation()!=CYCLE_WIND){
                timeSinceWithoutWind=-15;
                AnimationActHandler.INSTANCE.sendAnimationMessage(this,CYCLE_WIND);
            }

            LivingEntity target = getTarget();
            if(getHealth()<=getMaxHealth()/3&&getState()==0) setState(1);

            if(target!=null&&target.isAlive()){
                float dist = target.distanceTo(this);
                if(dist<=5&&cycleTime!=-1)
                    cycleTime = 0;
            }

            if (getAnimation() == NO_ANIMATION) {
                List<Projectile> projectilesNearby = level().getEntitiesOfClass(Projectile.class, getBoundingBox().inflate(8), e -> distanceTo(e) <= 8 + e.getBbWidth() / 2f);
                for (Projectile a : projectilesNearby) {
                    Vec3 aActualMotion = a.getDeltaMovement();
                    if (aActualMotion.length() < 1) {
                        continue;
                    }
                    aActualMotion = aActualMotion.normalize();
                    Vec3 dist = position().add(0, java.lang.Math.min(this.getBbHeight(),a.getY()-this.getY()),0).subtract(a.position());
                    float dot1 = (float) dist.dot(aActualMotion);
                    if(dot1>0){
                        Vec3 line = aActualMotion.scale(dot1);
                        if(line.subtract(dist).length()<0.5&&timeSinceJump>45){
                            timeSinceJump = 0;
                            lookAtEntity(a);
                            if(random.nextBoolean()){
                                dashForward(10,1.04f);
                                AnimationActHandler.INSTANCE.sendAnimationMessage(this, FrostNova.RIGHT_JUMP);
                            }else {
                                dashForward(10,-1.04f);
                                AnimationActHandler.INSTANCE.sendAnimationMessage(this, FrostNova.LEFT_JUMP);
                            }
                        }
                    }
                }
            }

            if(cycleTime==0&&getAnimation() == NO_ANIMATION&&!isInIceWall()){
                if(getState()==1&&random.nextInt(6)==0)
                    AnimationActHandler.INSTANCE.sendAnimationMessage(this,REMOTE_3);
                else
                    AnimationActHandler.INSTANCE.sendAnimationMessage(this,random.nextBoolean()?FrostNova.REMOTE_12:FrostNova.REMOTE_1);
            }

            if(cycleTime>=0)
                cycleTime--;

            //todo dodge
            if(timeSinceJump<=45) timeSinceJump++;
            if(tickCount%10==0&&defendWilling>0) defendWilling--;
        }
        else {
            if(isAggressive()) {
                float v = getSecondHealth() / (getMaxHealth() / 4);
                if (tickCount % 20 == 0&&v>0.01) {
                    RLParticle wind = new RLParticle();
                    wind.config.setDuration(20);
                    wind.config.setStartLifetime(NumberFunction.constant(20));
                    wind.config.setStartSize(new NumberFunction3(0.2));

                    wind.config.getEmission().setEmissionRate(NumberFunction.constant(v));

                    Circle circle = new Circle();circle.setRadius(0.4f);circle.setRadiusThickness(0);
                    wind.config.getShape().setShape(circle);
                    wind.config.getMaterial().setMaterial(MaterialHandle.VOID);
                    wind.config.getVelocityOverLifetime().open();
                    wind.config.getVelocityOverLifetime().setLinear(new NumberFunction3(NumberFunction.constant(0), new RandomConstant(3,4,true),NumberFunction.constant(0)));
                    wind.config.getVelocityOverLifetime().setOrbitalMode(VelocityOverLifetimeSetting.OrbitalMode.FixedVelocity);
                    wind.config.getVelocityOverLifetime().setOrbital(new NumberFunction3(NumberFunction.constant(0), new RandomConstant(7,8,true),NumberFunction.constant(0)));
                    wind.config.trails.open();
                    wind.config.trails.setColorOverLifetime(new Gradient(GradientHandle.CENTER_OPAQUE));

                    wind.emmit(new EntityEffect(level(),this));
                }
                if (tickCount % 2 == 0) {
                    float scale = 2.2f;
                    ParticleComponent.KeyTrack keyTrack = ParticleComponent.KeyTrack.startAndEnd(1, 0);
                    ParticleComponent.KeyTrack keyTrack1 = ParticleComponent.KeyTrack.startAndEnd(8, 0);
                    for (int i = 0; i < 2; i++) {
                        if (random.nextFloat() < v) {
                            float phaseOffset = random.nextFloat();
                            AdvancedParticleBase.spawnParticle(level(), ParticleHandler.SAN.get(), getX(), getY(), getZ(), 0, 0, 0, true, random.nextFloat() * 4, 0, 0, 0, 1F, 1, 1, 1, 1, 1, 20 + random.nextInt(11), true, true, new ParticleComponent[]{
                                    new ParticleComponent.Orbit(new Vec3[]{position().add(random.nextFloat() - 0.5, random.nextFloat() * 2, random.nextFloat() - 0.5)}, ParticleComponent.KeyTrack.startAndEnd(0 + phaseOffset, 1.6f + phaseOffset), new ParticleComponent.KeyTrack(
                                            new float[]{0, 0.5f * scale, 0.7f * scale, 0.85f * scale, 0.95f * scale, scale},
                                            new float[]{0, 0.5f, 0.7f, 0.85f, 0.95f, 1}
                                    ), ParticleComponent.KeyTrack.startAndEnd(random.nextFloat() * 2 - 1, random.nextFloat() * 2 - 1), new ParticleComponent.Constant(1.55f), new ParticleComponent.Constant(0), false),
                                    new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, keyTrack, false),
                                    new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ROLL, keyTrack1, false),
                            });
                        }
                    }
                }
            }
        }
        if(timeSinceBackJump<200) timeSinceBackJump++;
        if(timeSinceIceJump_1<120) timeSinceIceJump_1++;
        addIceBombParticle();
        addRemote2Particle();
        addDefendParticle();
        addLullabyeParticle();
        addRebornParticle();
        addIceWindParticle();
        addDashParticle();


        if(getTarget() instanceof GunKnightPatriot) setTarget(null);
    }

    @Override
    public void die(DamageSource pDamageSource) {
        super.die(pDamageSource);
        if (!this.isRemoved()) {
            bossInfo.update();
            secondHealth.update();
        }
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
        this.secondHealth.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
        this.secondHealth.removePlayer(player);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
            this.secondHealth.setName(this.getDisplayName());
        }
    }

    @Override
    public void setCustomName(Component name) {
        super.setCustomName(name);
        this.bossInfo.setName(this.getDisplayName());
        this.secondHealth.setName(this.getDisplayName());
    }

    @Override
    protected ConfigHandler.CombatConfig getCombatConfig() {
        return ConfigHandler.COMMON.MOBS.FROSTNOVA.combatConfig;
    }

    @Override
    protected <T extends GeoEntity> void basicAnimation(AnimationState<T> event) {
        if (isAggressive()) {
            if (event.isMoving()) {
                if(getState()==0)
                    event.getController().setAnimation(WALK_AGGRESSIVE.getRawAnimation());
                else
                    event.getController().setAnimation(WALK_PEACE.getRawAnimation());
            } else {
                event.getController().setAnimation(IDLE_AGGRESSIVE.getRawAnimation());
            }
        } else {
            if (event.isMoving()) {
                event.getController().setAnimation(WALK_PEACE.getRawAnimation());
            } else {
                event.getController().setAnimation(IDLE_PEACE.getRawAnimation());
            }
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        if(ConfigHandler.COMMON.GLOBALSETTING.damageCap.get())
            this.goalSelector.addGoal(1, new DailyDialogueAI(this));
        this.goalSelector.addGoal(2,new SnowNova1AttackAI(this));

        this.goalSelector.addGoal(7, new FFLookAtPlayerGoal<>(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new FFRandomLookAroundGoal<>(this));
        this.goalSelector.addGoal(8, new FFWaterAvoidingRandomStrollGoal(this , 0.31));

        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Zombie.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Ravager.class, true));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    public boolean donDodge(){
        AnimationAct act = getAnimation();
        return act == REBORN||act == REMOTE_2||act == ICE_BOMB||act == ICE_GROUND||act==LULLABYE_2||act==CYCLE_WIND;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if(getDialogueEntity()!=null) return false;

        if(pSource.getEntity() instanceof GunKnightPatriot) return false;

        float limit = (float)(getMaxHealth()*ConfigHandler.COMMON.MOBS.FROSTNOVA.damageConfig.damageCap.get());
        if(pAmount>limit&&!pSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) pAmount = limit;

        AnimationAct act = getAnimation();
        if(getAnimation() == REBORN) return false;
        else if(act == ICE_BOMB||act == ICE_GROUND||act==LULLABYE_2) return super.hurt(pSource, pAmount/3);

        if(!level().isClientSide()){
            List<LivingEntity> livingEntities = level().getEntitiesOfClass(LivingEntity.class,getBoundingBox().inflate(4),living ->
                    !(living instanceof GuerrillasEntity)&&living.distanceTo(this)<4);
            if(livingEntities.size()>2){
                timeSinceIceBomb +=10*livingEntities.size();
                if(timeSinceIceBomb>=150){
                    timeSinceIceBomb = 0;
                    if(getState()==0) {
                        timeSinceIceBomb = 50;
                        AnimationActHandler.INSTANCE.sendAnimationMessage(this, FrostNova.REMOTE_2);
                    }
                    else
                        AnimationActHandler.INSTANCE.sendAnimationMessage(this, FrostNova.ICE_BOMB);
                }
            }
            LivingEntity target = getTarget();
            if((target!=null&&distanceTo(target)<3&&timeSinceBackJump>=120)||(isInIceWall()&&pSource.getEntity()==target)) {
                timeSinceBackJump = 0;
                AnimationActHandler.INSTANCE.sendAnimationMessage(this, FrostNova.BACK_JUMP);
            }

            if(pSource.getEntity()!=null&&distanceTo(pSource.getEntity())>6&&pSource.getEntity().getY()-getY()<4)
                defendWilling++;
            if(cycleTime!=-1)
                cycleTime = -1;
        }

        if(pSource.getDirectEntity() instanceof Player&&getHasDialogue()){
            DialogueCapability.IDialogueCapability capability = CapabilityHandle.getCapability(this, CapabilityHandle.DIALOGUE_CAPABILITY);
            if(capability!=null&&capability.getDialogueNeedTime()>40){
                if(pSource.getDirectEntity()instanceof Player player1){
                    setDialogueEntity(player1);
                    DialogueEntity dialogueEntity = new DialogueEntity(this,level(),getDialogue(),player1,this);
                    dialogueEntity.setPos(position());
                    level().addFreshEntity(dialogueEntity);
                    return false;
                }
            }
        }

        return super.hurt(pSource, pAmount);
    }

    @Override
    public void actuallyHurt(DamageSource pDamageSource, float pDamageAmount) {
        if (!this.isInvulnerableTo(pDamageSource)) {
            pDamageAmount = net.minecraftforge.common.ForgeHooks.onLivingHurt(this, pDamageSource, pDamageAmount);
            if (pDamageAmount <= 0) return;
            pDamageAmount = this.getDamageAfterArmorAbsorb(pDamageSource, pDamageAmount);
            pDamageAmount = this.getDamageAfterMagicAbsorb(pDamageSource, pDamageAmount);
            float f1 = java.lang.Math.max(pDamageAmount - this.getAbsorptionAmount(), 0.0F);
            this.setAbsorptionAmount(this.getAbsorptionAmount() - (pDamageAmount - f1));

            f1 = net.minecraftforge.common.ForgeHooks.onLivingDamage(this, pDamageSource, f1);
            if (f1 != 0.0F) {
                this.getCombatTracker().recordDamage(pDamageSource, f1);
                this.setAbsorptionAmount(this.getAbsorptionAmount() - f1);

                float f2 = java.lang.Math.max(f1 - this.getSecondHealth(), 0.0F);
                this.setSecondHealth(java.lang.Math.max(0.0F,this.getSecondHealth() - f1));
                this.setHealth(this.getHealth() - f2);


                this.gameEvent(GameEvent.ENTITY_DAMAGE);
            }
        }
    }

    @Override
    public float getStepHeight() {
        return 1.5f;
    }

    @Override
    public AnimationAct getDeathAnimation() {
        return FrostNova.DIE;
    }

    @Override
    public SoundEvent getBossMusic() {
        return SoundHandle.SNOWNOVA_PERMAFROST.get();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return GuerrillasEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 300.0D)
                .add(Attributes.ATTACK_DAMAGE, 12.0f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
                .add(Attributes.FOLLOW_RANGE, 64)
                .add(Attributes.ARMOR, 2.0f);
    }

    @Override
    public boolean doHurtEntity(LivingEntity livingEntity, DamageSource source, float damage) {
        boolean b = super.doHurtEntity(livingEntity, source, damage);
        AnimationAct act = getAnimation();

        if(act ==ATTACK_2|| act ==ICE_WIND){
            livingEntity.invulnerableTime=1;
        }
        if(b&& (act ==REMOTE_2||act==REBORN)&&!(livingEntity instanceof GuerrillasEntity)){
            FrozenCapability.IFrozenCapability data = CapabilityHandle.getCapability(livingEntity, CapabilityHandle.FROZEN_CAPABILITY);
            if(data!=null){
                data.setFrozen(livingEntity, 100);
            }
        }
        return b;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        Vector3f pos = getTargetPos();
        pCompound.putFloat("targetx",pos.x);
        pCompound.putFloat("targety",pos.y);
        pCompound.putFloat("targetz",pos.z);
        pCompound.putFloat("second",getSecondHealth());
        pCompound.putInt("state",getState());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        setTargetPos(new Vector3f(pCompound.getFloat("targetx"),pCompound.getFloat("targetx"),pCompound.getFloat("targetx")));
        setSecondHealth(pCompound.getFloat("second"));
        this.entityData.set(STATE,pCompound.getInt("state"));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TARGET_X,0f);
        this.entityData.define(TARGET_Y,0f);
        this.entityData.define(TARGET_Z,0f);
        this.entityData.define(SECOND_HEALTH,0f);
        this.entityData.define(STATE,0);
    }

    @Override
    public boolean hasBossBar() {
        return true;
    }

    @Override
    public BossEvent.BossBarColor bossBarColor() {
        return BossEvent.BossBarColor.WHITE;
    }

    public void doDash(){
        float teleDist = 0;
        for(int i = 0;i<=3;i++){
            boolean flag = false;
            for(int j = 0;j<=2;j++){
                Vec3 vec3 = new Vec3(0, j, i).yRot((float) (-yBodyRot / 180 * Math.PI)).add(position());
                BlockPos blockPos = new BlockPos((int)vec3.x,(int)vec3.y,(int)vec3.z);
                BlockState blockState = level().getBlockState(blockPos);
                if(!blockState.isAir()&&!blockState.is(Blocks.SNOW))
                    flag = true;
            }
            if(!flag) teleDist = i;
            else break;
        }
        Vec3 telePos = new Vec3(0, 0, teleDist).yRot((float) (-yBodyRot / 180 * Math.PI));
        List<LivingEntity> livingEntities = level().getEntitiesOfClass(LivingEntity.class,getBoundingBox().inflate(6),entity ->
                !(entity instanceof GuerrillasEntity) && entity.distanceTo(this)<2.2+entity.getBbWidth()/2);
        for(LivingEntity livingEntity:livingEntities){
            Vec3 offset = livingEntity.position().subtract(position());
            double t = telePos.length();
            if(t!=0) {
                float len = (float) (offset.dot(telePos) / (t * t));
                if(len>=0) {
                    float dis = (float) telePos.scale(len).subtract(offset).length();
                    if (dis < (livingEntity.getBbWidth() / 2 + 1.5)) {
                        FrozenCapability.IFrozenCapability data = CapabilityHandle.getCapability(livingEntity, CapabilityHandle.FROZEN_CAPABILITY);
                        float damage = (float) getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.5f;

                        if (data!=null&&data.getFrozen()) {
                            data.clearFrozen(livingEntity);
                            damage *= 2;
                        }

                        boolean flad = livingEntity.hurt(damageSources().mobAttack(this), damage);
                        if(flad)
                            playSound(SoundHandle.ICE_DASH.get(), 1.0F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
                    }
                }
            }
        }
        setPos(telePos.add(position()));
    }

    public void shootIceCrystal(LivingEntity target,Vec3 vec3){
        if(target==null) return;

        IceCrystal abstractarrow = new IceCrystal(level(),this);
        abstractarrow.setPos(vec3);
        Vec3 targetMoveVec = target.position();
        Vec3 move = (new Vec3(targetMoveVec.x-vec3.x,target.getY(0.6)-vec3.y,targetMoveVec.z-vec3.z)).normalize().scale(2.5);
        abstractarrow.shoot(move.x, move.y , move.z, 0);
        this.level().addFreshEntity(abstractarrow);
    }

    public void shootIceBlade(Entity target){
        IceBlade abstractarrow = new IceBlade(level(),this);
        Vec3 position = this.position().add(new Vec3(0,1.5,0));
        abstractarrow.setPos(position);
        int dist = (int)(target.distanceTo(this));
        Vec3 move = target.getDeltaMovement().scale(dist/2);
        double d0 = target.getX()+move.x - this.getX();
        double d2 = target.getY(0.6D)+move.y - abstractarrow.getY();
        double d1 = target.getZ()+move.z - this.getZ();
        abstractarrow.shoot(d0, d2 , d1, 3.5F, (float)(9 - this.level().getDifficulty().getId() * 3));
        this.level().addFreshEntity(abstractarrow);
    }

    public void startCycle(int tick){
        cycleTick = 0;
        Vec3 vec3 = position();
        if(getTarget()!=null) {
            Vec3 targetVec = getTarget().position();
            startP = Math.atan2(targetVec.x - vec3.x, vec3.z - targetVec.z) + Math.toRadians(90);
        }
        if(random.nextInt(3)==0)
            isRight =!isRight;
        cycleTime = tick + random.nextInt(40);
    }

    private int cycleTick;
    public boolean isRight = true;
    public int cycleTime=-1;
    public float cycleRadius;
    public double startP;

    public Vec3 updateCyclePosition(float speed){
        if(random.nextInt(200)==0)
            isRight = !isRight;

        if(isRight)
            cycleTick++;
        else
            cycleTick--;

        Vec3 cycle;
        if(getTarget()!=null) {
            cycle = getTarget().position().add(cycleRadius * Math.cos(cycleTick * 0.628 * speed + startP), 0, cycleRadius * Math.sin(cycleTick * 0.628 * speed + startP));
        }
        else
            cycle = position();
        return cycle;
    }



    private void addIceBombParticle(){
        if(getAnimation() == FrostNova.ICE_BOMB) {
            if (level().isClientSide) {
                int tick = getAnimationTick();
                if (tick == 40) {
                    Vec3 vec3 = new Vec3(0, 0, 0.8).yRot((float) (-yBodyRot / 180 * Math.PI));
                    ParticleComponent.KeyTrack keyTrack1 = ParticleComponent.KeyTrack.oscillate(0, 2, 24);
                    ParticleComponent.KeyTrack keyTrack2 = new ParticleComponent.KeyTrack(new float[]{0, 18, 18, 0}, new float[]{0, 0.2f, 0.8f, 1});
                    AdvancedParticleBase.spawnParticle(level(), ParticleHandler.ICEBOMB_1.get(), getX() + vec3.x, getY() + 1.5, getZ() + vec3.z, 0, 0, 0, true, 0, 0, 0, 0, 0F, 1, 1, 1, 1, 1, 15, true, false, new ParticleComponent[]{
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, keyTrack2, false),
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, keyTrack1, true)
                    });
                }
                if (tick == 46) {
                    Vec3 vec3 = new Vec3(0, 0, 0.8).yRot((float) (-yBodyRot / 180 * Math.PI));
                    ParticleComponent.KeyTrack keyTrack1 = ParticleComponent.KeyTrack.oscillate(0, 2, 24);
                    ParticleComponent.KeyTrack keyTrack2 = new ParticleComponent.KeyTrack(new float[]{0, 18, 18, 0}, new float[]{0, 0.2f, 0.8f, 1});
                    AdvancedParticleBase.spawnParticle(level(), ParticleHandler.ICEBOMB_1.get(), getX() + vec3.x, getY() + 1.5, getZ() + vec3.z, 0, 0, 0, true, 0, 0, 0, 0, 0F, 0, 0, 0, 1, 1, 10, true, false, new ParticleComponent[]{
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, keyTrack2, false),
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, keyTrack1, true)
                    });
                }
                if (tick <= 25 && tickCount % 3 == 0) {
                    float temp = random.nextFloat();
                    AdvancedParticleBase.spawnParticle(level(), ParticleHandler.ICE_WHIRLWIND.get(), getX(), getY() + 0.5, getZ(), 0, 0.01, 0, false, 0, Math.toRadians(-90), 0, 0, 50F, 1, 1, 1, 1, 1, 40, true, false, new ParticleComponent[]{
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.YAW, ParticleComponent.KeyTrack.easeInCubic(temp, 10f + temp), false),
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, ParticleComponent.KeyTrack.easeInCubic(50f, 0f), false),
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, ParticleComponent.KeyTrack.easeInCubic(0.1f, 1f), false)
                    });
                }
                if (tick == 60) {
                    for (int i = 0; i < 3; i++) {
                        float ran = random.nextFloat();
                        for (int j = 0; j < 12*i; j++) {
                            Vec3 vec3 = position();
                            Vec3 move = new Vec3(0, 0.1, 1 + i ).yRot((float) Math.PI * 2 * j / (12*i) + ran);
                            level().addParticle(new ParticleCloud.CloudData(ParticleHandler.CLOUD.get(), 0.8f, 0.8f, 1f, (float) (10d + random.nextDouble() * 10d), 40, ParticleCloud.EnumCloudBehavior.SHRINK, 1f), vec3.x, vec3.y + 0.4, vec3.z, move.x, move.y, move.z);
                        }
                    }
                    AdvancedParticleBase.spawnParticle(level(), ParticleHandler.RING_BIG.get(), getX(), getY() + 0.5, getZ(), 0, 0.01, 0, false, 0, Math.toRadians(-90), 0, 0, 50F, 1, 1, 1, 1, 1, 4, true, false, new ParticleComponent[]{
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, ParticleComponent.KeyTrack.startAndEnd(0f, 90f), false),
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, ParticleComponent.KeyTrack.startAndEnd(1f, 0.5f), false)
                    });
                }
            }
            else {
                int tick = getAnimationTick();
                if(tick==20)
                    playSound(SoundHandle.ICE_WHIRLWIND.get(), 1.0F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
            }
        }
    }

    private void addRebornParticle(){
        if(getAnimation() == FrostNova.REBORN) {
            if (level().isClientSide) {
                int tick = getAnimationTick();
                if (tick <= 45 && tickCount % 3 == 0) {
                    float temp = random.nextFloat();
                    AdvancedParticleBase.spawnParticle(level(), ParticleHandler.ICE_WHIRLWIND.get(), getX(), getY() + 0.5, getZ(), 0, 0.01, 0, false, 0, Math.toRadians(-90), 0, 0, 50F, 1, 1, 1, 1, 1, 40, true, false, new ParticleComponent[]{
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.YAW, ParticleComponent.KeyTrack.easeInCubic(temp, 10f + temp), false),
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, ParticleComponent.KeyTrack.easeInCubic(50f, 0f), false),
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, ParticleComponent.KeyTrack.easeInCubic(0.1f, 1f), false)
                    });
                }
                if (tick == 91) {
                    for (int i = 0; i < 6; i++) {
                        float ran = random.nextFloat();
                        for (int j = 0; j < 12; j++) {
                            Vec3 vec3 = position();
                            Vec3 move = new Vec3(0, 0.1, 1 + i / 10f).yRot((float) Math.PI * 2 * j / 12 + ran);
                            level().addParticle(new ParticleCloud.CloudData(ParticleHandler.CLOUD.get(), 0.8f, 0.8f, 1f, (float) (10d + random.nextDouble() * 10d), 40, ParticleCloud.EnumCloudBehavior.SHRINK, 1f), vec3.x, vec3.y + 0.4, vec3.z, move.x, move.y, move.z);
                        }
                    }
                    AdvancedParticleBase.spawnParticle(level(), ParticleHandler.RING_BIG.get(), getX(), getY() + 0.5, getZ(), 0, 0.01, 0, false, 0, Math.toRadians(-90), 0, 0, 50F, 1, 1, 1, 1, 1, 4, true, false, new ParticleComponent[]{
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, ParticleComponent.KeyTrack.startAndEnd(0f, 90f), false),
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, ParticleComponent.KeyTrack.startAndEnd(1f, 0.5f), false)
                    });
                }
            }
            else {
                int tick = getAnimationTick();
                if(tick==40)
                    playSound(SoundHandle.ICE_WHIRLWIND.get(), 1.0F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
                if(tick==88)
                    EntityCameraShake.cameraShake(this.level(), this.position(), 16F, 0.1F, 10, 15);
            }
        }
    }

    private void addDashParticle(){
        if(getAnimation() == FrostNova.DASH) {
            if (level().isClientSide) {
                int tick = getAnimationTick();
                if (tick == 6) {
                    for (int i = 0; i < 8; i++) {
                        Vec3 move = new Vec3(0, 0.1, -0.4).yRot((float) (-yBodyRot / 180 * Math.PI+random.nextFloat()));
                        level().addParticle(new ParticleCloud.CloudData(ParticleHandler.CLOUD.get(), 0.8f, 0.8f, 1f, (float) (10d + random.nextDouble() * 10d), 12, ParticleCloud.EnumCloudBehavior.SHRINK, 1f), getX(), getY() + 0.4, getZ(), move.x, move.y, move.z);
                    }
                }
                ParticleComponent.KeyTrack track = new ParticleComponent.KeyTrack(new float[]{0,1,1,0},new float[]{0,0.25f,0.75f,1});
                if (tick == 7||tick == 8) {
                    for (int i = 0; i < 4; i++) {
                        Vec3 move = new Vec3(0, 0.1, -0.4).yRot((float) (-yBodyRot / 180 * Math.PI+random.nextFloat()));
                        level().addParticle(new ParticleCloud.CloudData(ParticleHandler.CLOUD.get(), 0.8f, 0.8f, 1f, (float) (10d + random.nextDouble() * 10d), 12, ParticleCloud.EnumCloudBehavior.SHRINK, 1f), getX(), getY() + 0.4, getZ(), move.x, move.y, move.z);
                        AdvancedParticleBase.spawnParticle(level(), ParticleHandler.SAN.get(), getX(), getY(), getZ(), move.x, move.y, move.z, true, 0, 0, 0, 0, 1.5F, 1, 1, 1, 0.75, 1, 40+random.nextInt(21), true, false, new ParticleComponent[]{
                                new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, track, false)
                        });
                    }
                }
            }
        }
    }

    private void addLullabyeParticle(){
        if(getAnimation() == FrostNova.LULLABYE_2) {
            if(tickCount%5==0){
                List<LivingEntity> livingEntities = this.level().getEntitiesOfClass(LivingEntity.class,this.getBoundingBox().inflate(8),hit->
                        this.distanceTo(hit)<6+hit.getBbWidth()/2);
                for(LivingEntity livingEntity :livingEntities){
                    if(livingEntity instanceof GuerrillasEntity) continue;

                    Vec3 move = new Vec3(livingEntity.getX()-this.getX(),0,livingEntity.getZ()-this.getZ());
                    float len = (float) move.length();
                    livingEntity.setDeltaMovement(move.normalize().scale(2*Math.max(0,1-len/6)));
                    livingEntity.move(MoverType.SELF,livingEntity.getDeltaMovement());
                }
            }

            if (level().isClientSide) {
                if(tickCount%2==0) {
                    int times = 1 + random.nextInt(2);
                    for (int i = 0; i < times; i++) {
                        ParticleComponent.KeyTrack track = new ParticleComponent.KeyTrack(new float[]{0, 1, 1, 0}, new float[]{0, 0.25f, 0.75f, 1});
                        Vec3 vec3 = position().add(new Vec3(0, 0.5 + random.nextFloat(), 10 * random.nextFloat()).yRot((float) Math.PI * 2 * random.nextFloat()));
                        AdvancedParticleBase.spawnParticle(level(), ParticleHandler.SAN.get(), vec3.x, vec3.y, vec3.z, 0.01 - 0.02 * random.nextFloat(), 0.02, 0.01 - 0.02 * random.nextFloat(), true, 0, 0, 0, 0, 1.5F, 1, 1, 1, 0.75, 1, 40 + random.nextInt(21), true, false, new ParticleComponent[]{
                                new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, track, false)
                        });
                    }
                }
                if(tickCount%10==0){
                    AdvancedParticleBase.spawnParticle(level(), ParticleHandler.RING_BIG.get(), getX(), getY() + 0.5, getZ(), 0, 0.01, 0, false, 0, Math.toRadians(-90), 0, 0, 50F, 1, 1, 1, 1, 1, 40, true, false, new ParticleComponent[]{
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, ParticleComponent.KeyTrack.startAndEnd(0f, 60f), false),
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, ParticleComponent.KeyTrack.startAndEnd(1f, 0f), false)
                    });
                }
            }
            else {
                int tick = getAnimationTick();
            }
        }
    }

    private void addRemote2Particle(){
        if(getAnimation() == FrostNova.REMOTE_2) {
            if (level().isClientSide) {
                int tick = getAnimationTick();
                if (tick == 16) {
                    for (int i = 0; i < 3; i++) {
                        float ran = random.nextFloat();
                        for (int j = 0; j < 8; j++) {
                            Vec3 vec3 = position();
                            Vec3 move = new Vec3(0, 0.1, 0.5 + i / 10f).yRot((float) Math.PI * 2 * j / 8 + ran);
                            level().addParticle(new ParticleCloud.CloudData(ParticleHandler.CLOUD.get(), 0.8f, 0.8f, 1f, (float) (10d + random.nextDouble() * 10d), 40, ParticleCloud.EnumCloudBehavior.SHRINK, 1f), vec3.x, vec3.y + 0.4, vec3.z, move.x, move.y, move.z);
                        }
                    }
                    AdvancedParticleBase.spawnParticle(level(), ParticleHandler.RING_BIG.get(), getX(), getY() + 0.5, getZ(), 0, 0.01, 0, false, 0, Math.toRadians(-90), 0, 0, 50F, 1, 1, 1, 1, 1, 4, true, false, new ParticleComponent[]{
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, ParticleComponent.KeyTrack.startAndEnd(0f, 50f), false),
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, ParticleComponent.KeyTrack.startAndEnd(1f, 0.5f), false)
                    });
                }
            }
        }
    }

    private void addDefendParticle(){
        if(getAnimation() == FrostNova.DEFEND) {
            int tick = getAnimationTick();
            if(tick==5){
                addDemonArea(100,position(),(float) (-getYRot() / 180 * Math.PI));
            }
        }
    }

    private void addIceWindParticle(){
        if(getAnimation() == FrostNova.ICE_WIND) {
            int tick = getAnimationTick();

            if(tick>=10&&tick<=55&&tick%5==0){
                this.doRangeKnockBack(12,24,0.2f);
            }

            if (level().isClientSide) {
                if(tick>=10&&tick<=55){
                    float randff = (tick-10)/25f;
                    if(tickCount%4==0) {
                        if(random.nextFloat()<randff){
                            Vec3 vec3 = new Vec3(0, 0, 1).yRot((float) (-yBodyRot / 180 * Math.PI));
                            AdvancedParticleBase.spawnParticle(level(), ParticleHandler.RING_BIG.get(), getX(), getY() + 1.5, getZ(), vec3.x, 0, vec3.z, false, Math.toRadians(-yBodyRot), 0, 0, 0, 5F, 1, 1, 1, 1, 1, 15, true, false, new ParticleComponent[]{
                                    new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, ParticleComponent.KeyTrack.startAndEnd(1f, 25f), false),
                                    new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, ParticleComponent.KeyTrack.startAndEnd(1f, 0.5f), false)
                            });
                        }
                    }
                    int times = 3+random.nextInt(2);
                    for(int i = 0;i<times;i++) {
                        if(random.nextFloat()<randff){
                            Vec3 vec3 = new Vec3(0, 0, 1).yRot((float) (-yBodyRot / 180 * Math.PI + Math.toRadians(10 - random.nextInt(21)))).xRot((float) Math.toRadians(10 - random.nextInt(21)));
                            AdvancedParticleBase.spawnParticle(level(), ParticleHandler.SAN.get(), getX(), getY() + 1.5, getZ(), vec3.x, vec3.y, vec3.z, true, 0, 0, 0, 0, 1.5F, 1, 1, 1, 0.75, 1, 10 + random.nextInt(11), true, false, new ParticleComponent[]{
                                    new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, ParticleComponent.KeyTrack.startAndEnd(1f, 0.5f), false)
                            });
                        }
                    }
                    for(int i = 0;i<times-3;i++) {
                        if(random.nextFloat()<randff){
                            Vec3 vec3 = new Vec3(0, 0, 3).xRot(0.35f - random.nextFloat() * 0.7f).yRot((float) (-yBodyRot / 180 * Math.PI + (0.35 - random.nextFloat() * 0.7)));
                            level().addParticle(new ParticleCloud.CloudData(ParticleHandler.CLOUD.get(), 1, 1, 1, (float) (10d + random.nextDouble() * 10d), 60, ParticleCloud.EnumCloudBehavior.SHRINK, 1f)
                                    , getX(), getY() + 1.5, getZ(), vec3.x, vec3.y, vec3.z);
                        }
                    }
                }
            }
            else if(tick == 10)
                playSound(SoundHandle.ICE_WIND.get(), 1.0F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
        }
    }

    public float getSecondHealth(){
        return this.entityData.get(SECOND_HEALTH);
    }

    public void setSecondHealth(float secondHealth){
        this.entityData.set(SECOND_HEALTH,secondHealth);
    }

    public Vector3f getTargetPos(){
        return new Vector3f(this.entityData.get(TARGET_X),this.entityData.get(TARGET_Y),this.entityData.get(TARGET_Z));
    }

    public void setTargetPos(Vector3f vec3){
        this.entityData.set(TARGET_X,vec3.x);
        this.entityData.set(TARGET_Y,vec3.y);
        this.entityData.set(TARGET_Z,vec3.z);
    }

    public void setState(int state){
        if(state==1){
            AnimationActHandler.INSTANCE.sendAnimationMessage(this,REBORN);
        }
        this.entityData.set(STATE,state);
    }

    public int getState(){
        return this.entityData.get(STATE);
    }

    public boolean isLoadedInWorld() {
        return this.level().hasChunk(SectionPos.blockToSectionCoord(this.getX()), SectionPos.blockToSectionCoord(this.getZ()));
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

    private final List<DemonCounter> demonCounterList = new ArrayList<>();

    public void addDemonArea(int time,Vec3 pos,float yaw){
        demonCounterList.add(new DemonCounter(time,pos,yaw));
    }

    public boolean isInIceWall(){
        boolean flad = false;
        for (DemonCounter demonCounter : demonCounterList) {
            Vec3 pos = demonCounter.pos;
            if (pos.subtract(position()).length() < 3)
                flad = true;
        }
        return flad;
    }

    static class DemonCounter{
        public DemonCounter(int time,Vec3 pos,float yaw) {
            this.maxTime = time;
            this.pos = pos;
            this.yaw = yaw;
        }

        private final float[] hou = new float[]{0f,-1f,1f};

        int time = 0;
        final int maxTime;
        final float yaw;
        Vec3 pos;

        public void update(Level level, FrostNova pursuer){
            if(!level.isClientSide){
                if(time<=3){
                    for(int i = 0;i<5;i++){
                        int len;
                        if(time<=1) len = 3;
                        else len = 1;
                        for (int i1 = 0;i1<len;i1++){
                            if(time==1&&(i==0||i==4)&&i1!=0) continue;
                            if(time==3&&(i==0||i==4)) continue;

                            Vec3 wall = new Vec3(i - 2, time, 4+hou[i1]).yRot(this.yaw).add(pos);
                            BlockPos blockPos1 = new BlockPos((int) wall.x, (int) wall.y, (int) wall.z);
                            BlockState wallState = level.getBlockState(blockPos1);
                            if (wallState.isAir() || wallState.is(Blocks.SNOW)) {
                                level.setBlock(blockPos1, Blocks.ICE.defaultBlockState(), 3);
                            }
                        }
                    }
                }
            }
            else {
                RandomSource random = pursuer.random;
                if(pursuer.tickCount%2==0){
                    int time = 1+random.nextInt(2);
                    for(int i = 0;i<time;i++) {
                        Vec3 vec3 = pos.add(new Vec3(0,0.5+random.nextFloat(),3.5*random.nextFloat()).yRot((float) java.lang.Math.PI*2*random.nextFloat()));
                        AdvancedParticleBase.spawnParticle(pursuer.level(), ParticleHandler.SAN.get(), vec3.x, vec3.y, vec3.z, 0.01-0.02*random.nextFloat(), 0.02, 0.01-0.02*random.nextFloat(), true, 0, 0, 0, 0, 1.5F, 1, 1, 1, 0.75, 1, 40+random.nextInt(21), true, false, new ParticleComponent[]{
                                new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, ParticleComponent.KeyTrack.startAndEnd(1f, 0f), false)
                        });
                    }
                }
            }

            time+=1;
        }

        public boolean disappear() {
            return time == maxTime;
        }

        public void iceBreak(Level level) {
            if(!level.isClientSide){
                for(int j = 0;j<=3;j++){
                    for(int i = 0;i<5;i++){
                        int len= 3;
                        for (int i1 = 0;i1<len;i1++) {
                            Vec3 wall = new Vec3(i - 2, j, 4+hou[i1]).yRot(this.yaw).add(pos);
                            BlockPos blockPos1 = new BlockPos((int) wall.x, (int) wall.y, (int) wall.z);
                            BlockState wallState = level.getBlockState(blockPos1);
                            if (wallState.is(Blocks.ICE)) {
                                level.destroyBlock(blockPos1, false);
                            }
                        }
                    }
                }
            }
        }
    }

    private static class DailyDialogueAI extends Goal{
        FrostNova frostNova;
        Patriot patriot;
        boolean isDialogue;
        int time;

        public DailyDialogueAI(FrostNova frostNova) {
            this.frostNova = frostNova;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK,Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            List<Patriot> entitiesOfClass = frostNova.level().getEntitiesOfClass(Patriot.class, frostNova.getBoundingBox().inflate(10));
            Patriot patriot1 = MathUtils.getClosestEntity(frostNova,entitiesOfClass);
            this.patriot = patriot1;
            return patriot!=null;
        }

        @Override
        public boolean canContinueToUse() {
            return patriot!=null&&time<400;
        }

        @Override
        public void start() {
            isDialogue = false;
            time=0;
        }

        @Override
        public void stop() {
            patriot=null;
        }

        @Override
        public void tick() {
            if(frostNova == null||patriot==null) return;

            if(isDialogue){
                time++;
            }

            float dist = (float) frostNova.distanceTo(patriot);
            if(dist>5){
                frostNova.getNavigation().moveTo(patriot,0.31);
            }
            else {
                frostNova.getNavigation().stop();
                if(!isDialogue){
                    frostNova.setDialogueEntity(patriot);
                    patriot.setDialogueEntity(frostNova);
                    DialogueEntity dialogueEntity = new DialogueEntity(frostNova,frostNova.level(),DialogueStore.daily_1,frostNova,patriot);
                    dialogueEntity.setPos(frostNova.position());
                    frostNova.level().addFreshEntity(dialogueEntity);
                    isDialogue = true;
                }
            }
        }
    }

}
