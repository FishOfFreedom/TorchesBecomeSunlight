package com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster;

import com.bobmowzie.mowziesmobs.client.particle.ParticleCloud;
import com.bobmowzie.mowziesmobs.client.particle.ParticleHandler;
import com.bobmowzie.mowziesmobs.client.particle.util.AdvancedParticleBase;
import com.bobmowzie.mowziesmobs.client.particle.util.ParticleComponent;
import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.init.SoundHandle;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationAct;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import com.freefish.torchesbecomesunlight.server.capability.frozen.FrozenCapabilityProvider;
import com.freefish.torchesbecomesunlight.server.entity.ai.FFBodyRotationControl;
import com.freefish.torchesbecomesunlight.server.entity.ai.FFPathNavigateGround;
import com.freefish.torchesbecomesunlight.server.entity.ai.attribute.AttributeRegistry;
import com.freefish.torchesbecomesunlight.server.entity.ai.entity.snownova.SnowNova1AttackAI;
import com.freefish.torchesbecomesunlight.server.entity.effect.dialogueentity.DialogueEntity;
import com.freefish.torchesbecomesunlight.server.entity.effect.dialogueentity.IDialogue;
import com.freefish.torchesbecomesunlight.server.entity.effect.IceWallEntity;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.GuerrillasEntity;
import com.freefish.torchesbecomesunlight.server.entity.effect.EntityCameraShake;
import com.freefish.torchesbecomesunlight.server.entity.projectile.BigIceCrystal;
import com.freefish.torchesbecomesunlight.server.entity.projectile.IceBlade;
import com.freefish.torchesbecomesunlight.server.entity.projectile.IceCrystal;
import com.freefish.torchesbecomesunlight.server.event.packet.toclient.UpdateBossBlizzard;
import com.freefish.torchesbecomesunlight.server.story.dialogue.Dialogue;
import com.freefish.torchesbecomesunlight.server.story.dialogue.DialogueStore;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import com.freefish.torchesbecomesunlight.server.util.storage.TBSWorldData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
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

import java.util.List;

public class FrostNova extends GuerrillasEntity implements IDialogue {
    private static final EntityDataAccessor<Float> TARGET_X = SynchedEntityData.defineId(FrostNova.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TARGET_Y = SynchedEntityData.defineId(FrostNova.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TARGET_Z = SynchedEntityData.defineId(FrostNova.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(FrostNova.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_FIRST = SynchedEntityData.defineId(FrostNova.class, EntityDataSerializers.BOOLEAN);

    public int timeSinceJump;
    public final int maxTimeSinceJump = 80;
    public LivingEntity tempTarget;
    private int timeSinceDodge;
    public int timeSinceBackJump;
    private int defendWilling;
    public int timeSinceIceBomb;
    public int timeSinceIceJump_1;

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
            entity.timeSinceJump = 0;
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

    public static final AnimationAct<FrostNova> ATTACK_1 = new AnimationAct<FrostNova>("attack_1",20){
        @Override
        public void tickUpdate(FrostNova entity) {
            int tick = entity.getAnimationTick();
            LivingEntity target = entity.getTarget();
            float damage = (float)entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            if(tick<10)
                entity.setDeltaMovement(0,entity.getDeltaMovement().y,0);
            else if(tick<=15)
                entity.setForwardMotion(0.15f);
            if(target!=null) {
                entity.getLookControl().setLookAt(target);
                if(target.distanceTo(entity)<=2.2+ target.getBbWidth()/2) {
                    if (tick == 4) {
                        target.hurt(entity.damageSources().mobAttack(entity), damage);
                    } else if (tick == 13) {
                        target.hurt(entity.damageSources().mobAttack(entity), damage*1.25f);
                    }
                }
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

    public static final AnimationAct<FrostNova> ATTACK_2 = new  AnimationAct<FrostNova>("attack_2",18){
        @Override
        public void tickUpdate(FrostNova entity) {
            int tick = entity.getAnimationTick();
            LivingEntity target = entity.getTarget();
            float damage = (float)entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            if(tick<6)
                entity.setDeltaMovement(0,entity.getDeltaMovement().y,0);
            else if(tick<=14)
                entity.setForwardMotion(0.2f);
            if(target!=null) {
                entity.getLookControl().setLookAt(target);
                if(target.distanceTo(entity)<=2.2+ target.getBbWidth()/2) {
                    if (tick == 3) {
                        target.hurt(entity.damageSources().mobAttack(entity), damage*0.83f);
                    } else if (tick == 7) {
                        target.hurt(entity.damageSources().mobAttack(entity), damage);
                    }else if (tick == 13) {
                        target.hurt(entity.damageSources().mobAttack(entity), damage*1.25f);
                    }
                }
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
                if (tick == 10) {
                    entity.shootIceCrystal(target,false);
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

    public static final AnimationAct<FrostNova> REMOTE_2 = new  AnimationAct<FrostNova>("remote_2",20,2){
        @Override
        public void tickUpdate(FrostNova entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            if(tick<8)
                entity.locateEntity();
            else if(tick<=15)
                entity.setForwardMotion(0.1f);
            entity.setYRot(entity.yRotO);
            if (tick == 12) {
                List<LivingEntity> livingEntities = entity.level().getEntitiesOfClass(LivingEntity.class,entity.getBoundingBox().inflate(15),entity1 ->
                        entity1!=entity&&entity1.distanceTo(entity)<=8&&entity.getY()-entity1.getY()<=3);
                for(LivingEntity livingEntity:livingEntities) {
                    float dist = Math.lerp(1.66f,0.8f,livingEntity.distanceTo(entity) / 8);
                    livingEntity.hurt(entity.damageSources().mobAttack(entity), damage * dist);
                }
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

    public static final AnimationAct<FrostNova> ATTACK_PREPARE = new  AnimationAct<FrostNova>("attack_prepare",400){
        @Override
        public void tickUpdate(FrostNova entity) {
            entity.locateEntity();
            if(entity.tempTarget!=null){
                if(entity.tempTarget.distanceTo(entity)>10&&entity.tempTarget.getY()-entity.getY()<3&&entity.tempTarget.getY()-entity.getY()>-3){
                    entity.setTarget(entity.tempTarget);
                    stop(entity);
                }
            }
        }

        @Override
        public void stop(FrostNova entity) {
            entity.tempTarget=null;
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
    public static final AnimationAct<FrostNova> DASH_RUN = new  AnimationAct<FrostNova>("dash_1",21){
        @Override
        public void tickUpdate(FrostNova entity) {
            LivingEntity target = entity.getTarget();
            if(target!=null&&target.distanceTo(entity)<6+target.getBbWidth()/2)
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
                    entity.shootIceCrystal(target,true);
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
            float damage = (float)entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            entity.setYRot(entity.yRotO);
            if(tick==56){
                List<LivingEntity> livingEntities = entity.level().getEntitiesOfClass(LivingEntity.class,entity.getBoundingBox().inflate(15),entity1 ->
                        entity1!=entity&&entity1.distanceTo(entity)<=12&&entity.getY()-entity1.getY()<=3);
                for(LivingEntity livingEntity:livingEntities){
                    float dist = Math.lerp(3f,1,livingEntity.distanceTo(entity) / 12);
                    livingEntity.hurt(entity.damageSources().mobAttack(entity),damage*dist);
                    livingEntity.getCapability(FrozenCapabilityProvider.FROZEN_CAPABILITY).ifPresent(data ->{
                        data.setFrozen(livingEntity,100);
                    });
                }
            }
        }
    };

    public static final AnimationAct<FrostNova> COUNTERATTACK = new AnimationAct<FrostNova>("skill_1",26){
        @Override
        public void tickUpdate(FrostNova entity) {
            int tick = entity.getAnimationTick();
            entity.locateEntity();
            LivingEntity target = entity.getTarget();
            float damage = (float)entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            if(target!=null)
                entity.getLookControl().setLookAt(target);
            if(tick==17){
                entity.doRangeAttack(2,160,damage*2f,true);
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
            float damage = (float)entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
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
    public static final AnimationAct<FrostNova> DEFEND = new AnimationAct<FrostNova>("defend",20){
        @Override
        public void tickUpdate(FrostNova entity) {
            entity.defendWilling = 0;
            int tick = entity.getAnimationTick();
            entity.locateEntity();
            LivingEntity target = entity.getTarget();
            float face;
            if(target!=null) {
                entity.getLookControl().setLookAt(target);
                face = (float)(Mth.atan2(entity.getX()-target.getX(), target.getZ()-entity.getZ()) * (double)(180F / (float)Math.PI));
            }
            else face = entity.yBodyRot;
            if(tick==5) {
                IceWallEntity iceWallEntity = new IceWallEntity(entity.level(),face,entity.getOnPos());
                iceWallEntity.setPos(entity.position());
                entity.level().addFreshEntity(iceWallEntity);
            }
            if(tick>=6&&tick<=10){
                entity.setPos(new Vec3(0,0.02,-0.2).yRot(face).add(entity.position()));
            }
        }
    };
    public static final AnimationAct<FrostNova> ICE_WIND = new AnimationAct<FrostNova>("ice_wind",65){
        @Override
        public void tickUpdate(FrostNova entity) {
            int tick = entity.getAnimationTick();
            entity.locateEntity();
            LivingEntity target = entity.getTarget();
            if(target!=null) {
                entity.getLookControl().setLookAt(target,30f,30f);
            }
            if(tick>=12&&tick<=55){
                float damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
                float range = 10;
                float arc = 40;
                List<LivingEntity> entitiesHit = entity.level().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(range, 3, range), e -> e != entity && entity.distanceTo(e) <= range + e.getBbWidth() / 2f && e.getY() <= entity.getY() + 3);
                for (LivingEntity entityHit : entitiesHit) {
                    float entityHitAngle = (float) ((Math.atan2(entityHit.getZ() - entity.getZ(), entityHit.getX() - entity.getX()) * (180 /Math.PI) - 90) % 360);
                    float entityAttackingAngle = entity.yBodyRot % 360;
                    if (entityHitAngle < 0) {
                        entityHitAngle += 360;
                    }
                    if (entityAttackingAngle < 0) {
                        entityAttackingAngle += 360;
                    }
                    float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
                    float entityHitDistance = (float) Math.sqrt((entityHit.getZ() -entity.getZ()) * (entityHit.getZ() - entity.getZ()) + (entityHit.getX() - entity.getX()) * (entityHit.getX() - entity.getX())) - entityHit.getBbWidth() / 2f;
                    if (entityHitDistance <= range && (entityRelativeAngle <= arc / 2 && entityRelativeAngle >= -arc / 2) || (entityRelativeAngle >= 360 - arc / 2 || entityRelativeAngle <= -360 + arc / 2)) {
                        if(entity.tickCount%2==0)
                            entityHit.hurt(entity.damageSources().mobAttack(entity),damage/2);
                        Vec3 move = new Vec3(entityHit.getX()-entity.getX(),0,entityHit.getZ()-entity.getZ()).normalize().scale(0.1);
                        entityHit.setDeltaMovement(entityHit.getDeltaMovement().x +move.x, entityHit.getDeltaMovement().y+0.02, entityHit.getDeltaMovement().z+move.z);
                    }
                }
            }
        }
    };
    public static final AnimationAct<FrostNova> LULLABYE_1 = new AnimationAct<FrostNova>("lullabye_1",190,1){
        @Override
        public void tickUpdate(FrostNova entity) {
            int tick = entity.getAnimationTick();
            entity.setYRot(entity.yRotO);
            if(tick==80) {
                ((ServerLevel) entity.level()).setWeatherParameters(0, 20000, true, false);
                entity.toggleServerBlizzard(true);
            }
            entity.locateEntity();
        }

        @Override
        public void stop(FrostNova entity) {
            AnimationActHandler.INSTANCE.sendAnimationMessage(entity, FrostNova.LULLABYE_2);
        }
    };
    public static final AnimationAct<FrostNova> LULLABYE_2 = new AnimationAct<FrostNova>("lullabye_2",660,1){//465*6
        @Override
        public void tickUpdate(FrostNova entity) {
            entity.locateEntity();
            if(entity.getTarget()!=null) {
                entity.getLookControl().setLookAt(entity.getTarget(),30f,30f);
                entity.lookAt(entity.getTarget(),30f,30f);
            }
        }

        @Override
        public void stop(FrostNova entity) {
            super.stop(entity);
            entity.kill();
            ((ServerLevel) entity.level()).setWeatherParameters(0, 20000, false, false);
        }
    };
    public static final AnimationAct<FrostNova> REBORN = new AnimationAct<FrostNova>("reborn",105,2){
        @Override
        public void tickUpdate(FrostNova entity) {
            int tick = entity.getAnimationTick();
            entity.locateEntity();
            float damage = (float)entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            entity.setYRot(entity.yRotO);
            if(tick==88){
                List<LivingEntity> livingEntities = entity.level().getEntitiesOfClass(LivingEntity.class,entity.getBoundingBox().inflate(15),entity1 ->
                        entity1!=entity&&entity1.distanceTo(entity)<=14&&entity.getY()-entity1.getY()<=3);
                for(LivingEntity livingEntity:livingEntities){
                    float dist = Math.lerp(3f,1,livingEntity.distanceTo(entity) / 14);
                    livingEntity.hurt(entity.damageSources().mobAttack(entity),damage*dist);
                    livingEntity.getCapability(FrozenCapabilityProvider.FROZEN_CAPABILITY).ifPresent(data ->{
                        data.setFrozen(livingEntity,300);
                    });
                }
            }
            if(tick==1)
                entity.setHealth(entity.getMaxHealth()/3);

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

    @Override
    public AnimationAct[] getAnimations() {
        return new AnimationAct[]{NO_ANIMATION,ATTACK_1,ATTACK_2,RIGHT_JUMP,LEFT_JUMP,BACK_JUMP,REMOTE_1,REMOTE_2,REMOTE_3,ATTACK_PREPARE
                ,DASH,ICE_JUMP,ICE_BOMB,COUNTERATTACK,ICE_GROUND,KICK,DIE,DEFEND,ICE_WIND,LULLABYE_1,REBORN,LULLABYE_2,DASH_RUN};
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
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar event) {
        super.registerControllers(event);
        event.add(new AnimationController<FrostNova>(this, "IceController", 5, this::icePredicate));
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

    @Override
    public void tick() {
        if(!level().isClientSide()&&getAnimation()!= FrostNova.REBORN){
            LivingEntity target = getTarget();
            if(target!=null&&target.isAlive()&&tickCount%2==0&&timeSinceIceJump_1>=80){
                timeSinceIceJump_1 = 0;
                float speed = getTargetMove(target);
                Vec3 face = getTargetMoveVec(target);
                Vec3 targetTo = position().subtract(target.position());
                double faceLen = face.length();
                if(faceLen!=0) {
                    double len = face.dot(targetTo) / (face.length() * face.length());
                    if(len>0&&speed>15) {
                        double length = face.multiply(len, 0, len).subtract(targetTo).length();
                        if(length<3+target.getBbWidth()/2)
                            AnimationActHandler.INSTANCE.sendAnimationMessage(this,ICE_JUMP);
                    }
                }
            }
            if(target!=null) {
                if (position().subtract(target.position()).horizontalDistance() < 6) {
                    if (target.getDeltaMovement().y < -0.1 && target.getY() - getY() > 3&&timeSinceIceJump_1>=80) {
                        timeSinceIceJump_1 = 0;
                        AnimationActHandler.INSTANCE.sendAnimationMessage(this, FrostNova.ICE_JUMP);
                    }
                }
                List<IceWallEntity> iceWall = level().getEntitiesOfClass(IceWallEntity.class,getBoundingBox().inflate(3));
                if(defendWilling >4&&iceWall.isEmpty()&&target.distanceTo(this)>6)
                    AnimationActHandler.INSTANCE.sendAnimationMessage(this, FrostNova.DEFEND);
            }
        }

        super.tick();

        if(!level().isClientSide()){
            LivingEntity target = getTarget();
            if(getHealth()<=getMaxHealth()/3&&getState()==0) setState(1);
            else if(getHealth()>getMaxHealth()/3&&getState()==1) setState(0);

            if(target!=null&&target.isAlive()){
                float dist = target.distanceTo(this);
                if(dist<=5&&cycleTime!=-1)
                    cycleTime = 0;
            }
            //todo dodge

            if (getAnimation() == NO_ANIMATION) {

                List<Projectile> projectilesNearby = level().getEntitiesOfClass(Projectile.class, getBoundingBox().inflate(8), e -> distanceTo(e) <= 8 + e.getBbWidth() / 2f);
                for (Projectile a : projectilesNearby) {
                    Vec3 aActualMotion = new Vec3(  a.xo-a.getX(), a.yo-a.getY()  , a.zo-a.getZ());
                    if (aActualMotion.length() < 0.1) {
                        continue;
                    }

                    float dot1 = ((float)(Mth.atan2(a.xo-a.getX(), a.getZ()-a.zo) * (double)(180F / (float)Math.PI)))+180;
                    float dot2 = yBodyRot%360-dot1;
                    if(dot2>180)
                        dot2 = -180+(dot2-180);
                    if(dot2<-180)
                        dot2 = 180+(dot2+180);
                    //left plus
                    if(Math.abs(dot2)<=80&&timeSinceDodge>=220){
                        timeSinceDodge = 0;
                        Vec3 direction1= new Vec3(a.getX()-a.xo,0,a.getZ()-a.zo).normalize().scale(1.5);
                        if(random.nextInt(2)==0){
                            direction1 = direction1.yRot((float) Math.toRadians(90)).add(0,0.4,0);
                            AnimationActHandler.INSTANCE.sendAnimationMessage(this, FrostNova.RIGHT_JUMP);
                        }else {
                            direction1 = direction1.yRot((float) Math.toRadians(-90)).add(0,0.4,0);
                            AnimationActHandler.INSTANCE.sendAnimationMessage(this, FrostNova.LEFT_JUMP);
                        }
                        this.setDeltaMovement(direction1);
                    }
                }
            }
            List<IceWallEntity> iceWall = level().getEntitiesOfClass(IceWallEntity.class,getBoundingBox().inflate(3));
            if(cycleTime==0&&getAnimation() == NO_ANIMATION&&iceWall.isEmpty()){
                if(getState()==1&&random.nextInt(6)==0)
                    AnimationActHandler.INSTANCE.sendAnimationMessage(this,REMOTE_3);
                else
                    AnimationActHandler.INSTANCE.sendAnimationMessage(this,REMOTE_1);
            }
            if(timeSinceJump < maxTimeSinceJump)
                timeSinceJump++;
            if(cycleTime>=0)
                cycleTime--;

            setHasDialogue(getTarget()==null);
            if(timeSinceDodge<=400) timeSinceDodge++;
            if(tickCount%10==0&&defendWilling>0) defendWilling--;
        }
        if(timeSinceBackJump<120) timeSinceBackJump++;
        if(timeSinceIceJump_1<80) timeSinceIceJump_1++;
        addIceBombParticle();
        addRemote2Particle();
        addLullabyeParticle();
        addLullabyeWindParticle();
        addRebornParticle();
        addIceWindParticle();
        addDashParticle();
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
        this.goalSelector.addGoal(2,new SnowNova1AttackAI(this));

        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this , 0.31));

        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Zombie.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Ravager.class, true));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if(pAmount>getMaxHealth()/10&&!pSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) pAmount = getMaxHealth()/10;
        if(getAnimation() == FrostNova.REBORN||getAnimation() == FrostNova.LULLABYE_1||getAnimation() == FrostNova.LULLABYE_2) return false;
        else if(getAnimation()== FrostNova.REMOTE_2||getAnimation()== FrostNova.ICE_BOMB|getAnimation()== FrostNova.ICE_GROUND) return super.hurt(pSource, pAmount/3);

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
            List<IceWallEntity> iceWall = level().getEntitiesOfClass(IceWallEntity.class,getBoundingBox().inflate(3));
            if((target!=null&&distanceTo(target)<3&&timeSinceBackJump>=120)||(!iceWall.isEmpty()&&pSource.getEntity()==target)) {
                timeSinceBackJump = 0;
                AnimationActHandler.INSTANCE.sendAnimationMessage(this, FrostNova.BACK_JUMP);
            }
            if(target!=null&&distanceTo(target)>6&&target.getY()-getY()<4)
                defendWilling++;
            if(cycleTime!=-1)
                cycleTime = -1;
        }
        if(pSource.getDirectEntity() instanceof Player&&getHasDialogue()){
            Player player = MathUtils.getClosestEntity(this,level().getEntitiesOfClass(Player.class,getBoundingBox().inflate(5)));
            DialogueEntity dialogueEntity = new DialogueEntity(this,level(),DialogueStore.snownova_meet_1,player,this);
            dialogueEntity.setPos(position());
            level().addFreshEntity(dialogueEntity);
            return false;
        }
        if(getState()==0)
            return super.hurt(pSource, pAmount);
        else
            return super.hurt(pSource, pAmount/2);
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
        if(getAnimation()== FrostNova.LULLABYE_1||getAnimation()== FrostNova.LULLABYE_2)
            return SoundHandle.SNOWNOVA_LULLABYE.get();
        else
            return SoundHandle.SNOWNOVA_PERMAFROST.get();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return GuerrillasEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 300.0D)
                .add(Attributes.ATTACK_DAMAGE, 12.0f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
                .add(Attributes.FOLLOW_RANGE, 64)
                .add(Attributes.ARMOR, 2.0f)
                .add(AttributeRegistry.ARMOR_DURABILITY.get(),10f);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        Vector3f pos = getTargetPos();
        pCompound.putFloat("targetx",pos.x);
        pCompound.putFloat("targety",pos.y);
        pCompound.putFloat("targetz",pos.z);
        pCompound.putInt("state",getState());
        pCompound.putBoolean("isfirst",getIsFirst());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        setTargetPos(new Vector3f(pCompound.getFloat("targetx"),pCompound.getFloat("targetx"),pCompound.getFloat("targetx")));
        this.entityData.set(STATE,pCompound.getInt("state"));
        setIsFirst(pCompound.getBoolean("isfirst"));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TARGET_X,0f);
        this.entityData.define(TARGET_Y,0f);
        this.entityData.define(TARGET_Z,0f);
        this.entityData.define(STATE,0);
        this.entityData.define(IS_FIRST,false);
    }

    @Override
    public boolean hasBossBar() {
        return true;
    }

    @Override
    public BossEvent.BossBarColor bossBarColor() {
        return BossEvent.BossBarColor.WHITE;
    }

    public void stopSeenByPlayer(ServerPlayer serverPlayer) {
        super.stopSeenByPlayer(serverPlayer);
        toggleServerBlizzard(false);
    }

    public void remove(RemovalReason removalReason) {
        super.remove(removalReason);
        toggleServerBlizzard(false);
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
                        livingEntity.getCapability(FrozenCapabilityProvider.FROZEN_CAPABILITY).ifPresent(data -> {
                            float damage = (float) getAttribute(Attributes.ATTACK_DAMAGE).getValue() * 1.5f;
                            if (data.isFrozen) {
                                data.clearFrozen(livingEntity);
                                damage *= 2;
                            }
                            boolean flad = livingEntity.hurt(damageSources().mobAttack(this), damage);
                            if(flad)
                                playSound(SoundHandle.ICE_DASH.get(), 1.0F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
                        });
                    }
                }
            }
        }
        setPos(telePos.add(position()));
    }

    public void shootIceCrystal(Entity target,boolean forecast){
        IceCrystal abstractarrow = new IceCrystal(level(),this);
        Vec3 position = this.position().add(new Vec3(0,1.5,0));
        abstractarrow.setPos(position);
        double d0 ;
        double d1 ;
        double d2 ;
        if(forecast){
            int dist = (int)(target.distanceTo(this));
            Vec3 move = target.getDeltaMovement().scale(dist);
            d0 = target.getX()+move.x - this.getX();
            d1 = target.getY(0.4D)+move.y - abstractarrow.getY();
            d2 = target.getZ()+move.z - this.getZ();
        }
        else {
            d0 = target.getX() - this.getX();
            d1 = target.getY(0.4D) - abstractarrow.getY();
            d2 = target.getZ() - this.getZ();
        }
        abstractarrow.shoot(d0, d1 , d2, 2F, (float)(9 - this.level().getDifficulty().getId() * 3));
        //this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
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
        if(getAnimation() == FrostNova.LULLABYE_1||getAnimation() == FrostNova.LULLABYE_2) {
            if (level().isClientSide && tickCount%2==0) {
                int times = 1+random.nextInt(2);
                for(int i = 0;i<times;i++) {
                    ParticleComponent.KeyTrack track = new ParticleComponent.KeyTrack(new float[]{0,1,1,0},new float[]{0,0.25f,0.75f,1});
                    Vec3 vec3 = position().add(new Vec3(0,0.5+random.nextFloat(),10*random.nextFloat()).yRot((float)Math.PI*2*random.nextFloat()));
                    AdvancedParticleBase.spawnParticle(level(), ParticleHandler.SAN.get(), vec3.x, vec3.y, vec3.z, 0.01-0.02*random.nextFloat(), 0.02, 0.01-0.02*random.nextFloat(), true, 0, 0, 0, 0, 1.5F, 1, 1, 1, 0.75, 1, 40+random.nextInt(21), true, false, new ParticleComponent[]{
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, track, false)
                    });
                }
            }
            else {
                int tick = getAnimationTick();
            }
        }
    }

    private void addLullabyeWindParticle(){
        if((getAnimation() == FrostNova.LULLABYE_1&&getAnimationTick()>=80)||getAnimation() == FrostNova.LULLABYE_2) {
            if (level().isClientSide && tickCount%2==0) {
                List<Player> players = level().getEntitiesOfClass(Player.class,getBoundingBox().inflate(20));
                for(Player player:players) {
                    if(!player.isCreative()) {
                        int times = 10 + random.nextInt(10);
                        for (int i = 0; i < times; i++) {
                            Vec3 move = player.position().subtract(position()).normalize().scale(random.nextFloat()+3);
                            level().addParticle(new ParticleCloud.CloudData(ParticleHandler.CLOUD.get(), 0.6f, 0.6f, 0.6f, (float) (10d + random.nextDouble() * 15d), 20, ParticleCloud.EnumCloudBehavior.SHRINK, 1f), getX()+random.nextFloat()*4-2, getY(0.5+random.nextFloat()), getZ()+random.nextFloat()*4-2, move.x, move.y, move.z);
                        }
                    }
                }
            }
        }
    }

    private void addRemote2Particle(){
        if(getAnimation() == FrostNova.REMOTE_2) {
            if (level().isClientSide) {
                int tick = getAnimationTick();
                if (tick == 12) {
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

    private void addIceWindParticle(){
        if(getAnimation() == FrostNova.ICE_WIND) {
            int tick = getAnimationTick();
            if (level().isClientSide) {
                if(tick>=12&&tick<=55){
                    if(tickCount%4==0) {
                        Vec3 vec3 = new Vec3(0, 0, 1).yRot((float) (-yBodyRot / 180 * Math.PI));
                        AdvancedParticleBase.spawnParticle(level(), ParticleHandler.RING_BIG.get(), getX(), getY() + 1.5, getZ(), vec3.x, 0, vec3.z, false, Math.toRadians(-yBodyRot), 0, 0, 0, 5F, 1, 1, 1, 1, 1, 15, true, false, new ParticleComponent[]{
                                new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, ParticleComponent.KeyTrack.startAndEnd(1f, 25f), false),
                                new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, ParticleComponent.KeyTrack.startAndEnd(1f, 0.5f), false)
                        });
                    }
                    if(tickCount%2==0) {
                        int times = 1+random.nextInt(2);
                        for(int i = 0;i<times;i++) {
                            Vec3 vec3 = new Vec3(0, 0, 1).yRot((float)(-yBodyRot / 180 * Math.PI+Math.toRadians(10 - random.nextInt(21)))).xRot((float) Math.toRadians(10 - random.nextInt(21)));
                            AdvancedParticleBase.spawnParticle(level(), ParticleHandler.SAN.get(), getX(), getY() + 1.5, getZ(), vec3.x, vec3.y, vec3.z, true, 0, 0, 0, 0, 1.5F, 1, 1, 1, 0.75, 1, 10+random.nextInt(11), true, false, new ParticleComponent[]{
                                    new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, ParticleComponent.KeyTrack.startAndEnd(1f, 0.5f), false)
                            });
                        }
                    }
                }
            }
            else if(tick == 10)
                playSound(SoundHandle.ICE_WIND.get(), 1.0F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
        }
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
        if(state == 1){
            getAttribute(Attributes.ARMOR).addTransientModifier(new AttributeModifier("armor", (double)6, AttributeModifier.Operation.ADDITION));
            getAttribute(Attributes.ATTACK_DAMAGE).addTransientModifier(new AttributeModifier("act", (double)6, AttributeModifier.Operation.ADDITION));
            AnimationActHandler.INSTANCE.sendAnimationMessage(this, FrostNova.REBORN);
        }
        else {
            getAttribute(Attributes.ARMOR).addTransientModifier(new AttributeModifier("armor", (double)-6, AttributeModifier.Operation.ADDITION));
            getAttribute(Attributes.ATTACK_DAMAGE).addTransientModifier(new AttributeModifier("act", (double)-6, AttributeModifier.Operation.ADDITION));
        }
        this.entityData.set(STATE,state);
    }

    public int getState(){
        return this.entityData.get(STATE);
    }

    public void setIsFirst(boolean isFirst){
        this.entityData.set(IS_FIRST,isFirst);
    }

    public boolean getIsFirst(){
        return this.entityData.get(IS_FIRST);
    }

    private void toggleServerBlizzard(boolean blizzard) {
        if (!level().isClientSide) {
            TBSWorldData worldData = TBSWorldData.get(level());
            if (worldData != null) {
                worldData.trackPrimordialBoss(this.getId(), blizzard);
                TorchesBecomeSunlight.sendMSGToAll(new UpdateBossBlizzard(this.getId(), worldData.isBossActive(level())));
            }
        }
    }

    public boolean isLoadedInWorld() {
        return this.level().hasChunk(SectionPos.blockToSectionCoord(this.getX()), SectionPos.blockToSectionCoord(this.getZ()));
    }

    @Override
    public Dialogue getDialogue() {
        return DialogueStore.snownova_meet_1;
    }
}
