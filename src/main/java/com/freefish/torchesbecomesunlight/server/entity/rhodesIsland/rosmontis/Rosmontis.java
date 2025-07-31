package com.freefish.torchesbecomesunlight.server.entity.rhodesIsland.rosmontis;

import com.freefish.rosmontislib.client.RLClientUseUtils;
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
import com.freefish.rosmontislib.client.particle.advance.data.shape.Sphere;
import com.freefish.rosmontislib.client.particle.advance.effect.BlockEffect;
import com.freefish.rosmontislib.client.particle.advance.effect.EntityEffect;
import com.freefish.rosmontislib.client.utils.GradientColor;
import com.freefish.rosmontislib.client.utils.Range;
import com.freefish.rosmontislib.sync.ITagSerializable;
import com.freefish.torchesbecomesunlight.client.util.noise.PerlinNoise;
import com.freefish.torchesbecomesunlight.client.util.particle.util.AdvancedParticleBase;
import com.freefish.torchesbecomesunlight.client.util.particle.util.ParticleComponent;
import com.freefish.torchesbecomesunlight.compat.rosmontis.TBSMaterialHandle;
import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.PlayerCapability;
import com.freefish.torchesbecomesunlight.server.config.ConfigHandler;
import com.freefish.torchesbecomesunlight.server.entity.ai.*;
import com.freefish.torchesbecomesunlight.server.entity.ai.entity.rhodes.RosmontisAttackAI;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.GuerrillasEntity;
import com.freefish.torchesbecomesunlight.server.entity.rhodesIsland.RhodesIslandEntity;
import com.freefish.torchesbecomesunlight.server.entity.rhodesIsland.ai.RosmontisAutoDialogueGoal;
import com.freefish.torchesbecomesunlight.server.init.ItemHandle;
import com.freefish.torchesbecomesunlight.server.init.ParticleHandler;
import com.freefish.torchesbecomesunlight.server.init.SoundHandle;
import com.freefish.torchesbecomesunlight.server.init.generator.advancement.criterion.TriggerHandler;
import com.freefish.torchesbecomesunlight.server.partner.Partner;
import com.freefish.torchesbecomesunlight.server.partner.PartnerUtil;
import com.freefish.torchesbecomesunlight.server.story.IDialogueEntity;
import com.freefish.torchesbecomesunlight.server.story.PlayerStoryStoneData;
import com.freefish.torchesbecomesunlight.server.story.data.DialogueEntry;
import com.freefish.torchesbecomesunlight.server.story.dialogueentity.DialogueEntity;
import com.freefish.torchesbecomesunlight.server.util.ArrayUtil;
import com.freefish.torchesbecomesunlight.server.util.FFEntityUtils;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationAct;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import com.freefish.torchesbecomesunlight.server.util.bossbar.CustomBossInfoServer;
import com.freefish.torchesbecomesunlight.server.util.bossbar.FFBossInfoServer;
import com.freefish.torchesbecomesunlight.server.util.bossbar.IBossInfoUpdate;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Rosmontis extends RhodesIslandEntity implements IDialogueEntity, IEntityAdditionalSpawnData {
    public static final AnimationAct<Rosmontis> IDLE_TO_ACT = new AnimationAct<Rosmontis>("idletoact",46){
        @Override
        public void tickUpdate(Rosmontis entity) {
            int tick = entity.getAnimationTick();
            RandomSource random = entity.getRandom();
            Entity target = entity.getTarget();
            entity.locateEntity();
            if(target==null){
                entity.setYRot(entity.yRotO);
            }else {
                entity.lookAtEntity(target);
            }

            if(tick==25){
                if(isNoExist(entity.installations[0])){
                    RosmontisInstallation rosmontisInstallation = RosmontisInstallation.SpawnInstallation(entity.level(), entity, 0);
                    entity.installations[0] = rosmontisInstallation;
                }
            }else if(tick==30){
                if(isNoExist(entity.installations[1])) {
                    RosmontisInstallation rosmontisInstallation = RosmontisInstallation.SpawnInstallation(entity.level(), entity, 1);
                    entity.installations[1] = rosmontisInstallation;
                }
            }else if(tick==35){
                if(isNoExist(entity.installations[2])) {
                    RosmontisInstallation rosmontisInstallation = RosmontisInstallation.SpawnInstallation(entity.level(), entity, 2);
                    entity.installations[2] = rosmontisInstallation;
                }
            }else if(tick==40){
                if(isNoExist(entity.installations[3])) {
                    RosmontisInstallation rosmontisInstallation = RosmontisInstallation.SpawnInstallation(entity.level(), entity, 3);
                    entity.installations[3] = rosmontisInstallation;
                }
            }
        }

        private boolean isNoExist(RosmontisInstallation rosmontisInstallation){
            return rosmontisInstallation==null||rosmontisInstallation.isRemoved()||rosmontisInstallation.animationType==-1;
        }
    };
    public static final AnimationAct<Rosmontis> MOVE_1 = new AnimationAct<Rosmontis>("move",60){
        @Override
        public void tickUpdate(Rosmontis entity) {
            LivingEntity target = entity.getTarget();
            if(target==null){
                entity.setYRot(entity.yRotO);
            }else {
                entity.lookAtEntity(target);
            }

            BlockPos under = entity.blockPosition().below(3);
            BlockPos nearestGround = MathUtils.getFirstBlockAbove(entity.level(),under);
            if(nearestGround==null) nearestGround = under;

            double targetY = nearestGround.getY() + 1.5; // 地面高+5
            if (Math.abs(entity.getY() - targetY) > 0.1) {
                double dy = targetY - entity.getY();
                entity.setDeltaMovement(entity.getDeltaMovement().x, dy * 0.1, entity.getDeltaMovement().z);
            } else {
                entity.setDeltaMovement(entity.getDeltaMovement().x, 0, entity.getDeltaMovement().z);
            }

            if(target!=null) {
                Vec3 vec3 = entity.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D);
                Vec3 vec31 = new Vec3(target.getX() - entity.getX(), 0.0D, target.getZ() - entity.getZ());
                if (vec31.horizontalDistanceSqr() > 6.0D) {
                    Vec3 vec32 = vec31.normalize().scale(2.2);
                    vec3 = vec3.add(new Vec3(vec32.x * 0.3D - vec3.x * 0.6D, vec3.y, vec32.z * 0.3D - vec3.z * 0.6D));
                }
                entity.setDeltaMovement(vec3);
            }else {
                Vec3 targetPos = FFEntityUtils.getBodyRotVec(entity,new Vec3(0,0,10));
                Vec3 vec3 = entity.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D);
                Vec3 vec31 = new Vec3(targetPos.x - entity.getX(), 0.0D, targetPos.z - entity.getZ());
                if (vec31.horizontalDistanceSqr() > 6.0D) {
                    Vec3 vec32 = vec31.normalize().scale(2.2);
                    vec3 = vec3.add(new Vec3(vec32.x * 0.3D - vec3.x * 0.6D, vec3.y, vec32.z * 0.3D - vec3.z * 0.6D));
                }
                entity.setDeltaMovement(vec3);
            }

            if(target!=null&&target.distanceTo(entity)<8){
                AnimationActHandler.INSTANCE.sendAnimationMessage(entity,MOVE_2);
            }
        }

        @Override
        public void stop(Rosmontis entity) {
            AnimationActHandler.INSTANCE.sendAnimationMessage(entity,MOVE_2);
        }
    };

    public static final AnimationAct<Rosmontis> MOVE_12 = new AnimationAct<Rosmontis>(Animation.LoopType.HOLD_ON_LAST_FRAME,"move",200){
        @Override
        public void tickUpdate(Rosmontis entity) {
        }

        @Override
        public void stop(Rosmontis entity) {
            AnimationActHandler.INSTANCE.sendAnimationMessage(entity,MOVE_2);
        }
    };
    public static final AnimationAct<Rosmontis> MOVE_2 = new AnimationAct<Rosmontis>("move_1",17){
        @Override
        public void tickUpdate(Rosmontis entity) {
            LivingEntity target = entity.getTarget();
            int tick = entity.getAnimationTick();
        }
    };
    public static final AnimationAct<Rosmontis> REMOTE_1 = new AnimationAct<Rosmontis>("remote_1",25){
        @Override
        public void tickUpdate(Rosmontis entity) {
            int tick = entity.getAnimationTick();
            RandomSource random = entity.getRandom();
            Entity target = entity.getTarget();
            entity.locateEntity();
            if(target==null){
                entity.setYRot(entity.yRotO);
            }else {
                entity.lookAtEntity(target);
            }

            if(tick==1){
                if(target!=null){
                    entity.remoteAct1(target.position());
                }
            }
        }
    };
    public static final AnimationAct<Rosmontis> REMOTE_1_FLY = new AnimationAct<Rosmontis>("remote_1_fly",25){
        @Override
        public void tickUpdate(Rosmontis entity) {
            int tick = entity.getAnimationTick();
            RandomSource random = entity.getRandom();
            Entity target = entity.getTarget();
            entity.locateEntity();
            if(target==null){
                entity.setYRot(entity.yRotO);
            }else {
                entity.lookAtEntity(target);
            }

            if(tick==1){
                if(target!=null){
                    entity.remoteAct1(target.position());
                }
            }
        }
    };
    public static final AnimationAct<Rosmontis> REMOTE_2 = new AnimationAct<Rosmontis>("remote_2",30){
        @Override
        public void tickUpdate(Rosmontis entity) {
            int tick = entity.getAnimationTick();
            RandomSource random = entity.getRandom();
            Entity target = entity.getTarget();
            entity.locateEntity();
            if(target==null){
                entity.setYRot(entity.yRotO);
            }else {
                entity.lookAtEntity(target);
            }

            if(tick==1){
                Partner<?> partner = PartnerUtil.getPartner(entity);
                if(partner!=null&&partner.getInstancePos()!=null){
                    entity.remoteAct2(partner.getInstancePos());
                }
                else if(target instanceof LivingEntity living){
                    entity.remoteAct2(target.position());
                }
            }
        }
    };
    public static final AnimationAct<Rosmontis> REMOTE_2_FLY = new AnimationAct<Rosmontis>("remote_2_fly",30){
        @Override
        public void tickUpdate(Rosmontis entity) {
            int tick = entity.getAnimationTick();
            RandomSource random = entity.getRandom();
            Entity target = entity.getTarget();
            entity.locateEntity();
            if(target==null){
                entity.setYRot(entity.yRotO);
            }else {
                entity.lookAtEntity(target);
            }

            if(tick==1){
                if(target instanceof LivingEntity living){
                    entity.remoteAct2(target.position());
                }
            }
        }
    };
    public static final AnimationAct<Rosmontis> REMOTE_3 = new AnimationAct<Rosmontis>("remote_3",40){
        @Override
        public void tickUpdate(Rosmontis entity) {
            int tick = entity.getAnimationTick();
            RandomSource random = entity.getRandom();
            Entity target = entity.getTarget();
            entity.locateEntity();
            if(target==null){
                entity.setYRot(entity.yRotO);
            }else {
                entity.lookAtEntity(target);
            }

            if(tick==8){
                if(target!=null){
                    Vec3 position = target.position();
                    Vec3 horizon = new Vec3(target.getX()-entity.getX(),0,target.getZ()-entity.getZ());
                    if(horizon.length()<12){
                        position = position.add(horizon.normalize().scale(6));
                    }
                    entity.remoteAct3(position);
                }
            }
        }
    };
    public static final AnimationAct<Rosmontis> ATTACK_1 = new AnimationAct<Rosmontis>("attack_1",25){
        @Override
        public void tickUpdate(Rosmontis entity) {
            int tick = entity.getAnimationTick();
            RandomSource random = entity.getRandom();
            Entity target = entity.getTarget();
            entity.locateEntity();
            if(target==null){
                entity.setYRot(entity.yRotO);
            }else {
                entity.lookAtEntity(target);
            }

            if(tick==1){
                if(entity.getState()&&target!=null){
                    if(entity.installations[0]!=null){
                        entity.installations[0].skill4(0,target.position());
                    }
                }
                else
                    entity.attack1();
            }
        }
    };
    public static final AnimationAct<Rosmontis> ATTACK_2 = new AnimationAct<Rosmontis>("attack_2",35){
        @Override
        public void tickUpdate(Rosmontis entity) {
            int tick = entity.getAnimationTick();
            RandomSource random = entity.getRandom();
            Entity target = entity.getTarget();
            entity.locateEntity();
            if(target==null||tick>12){
                entity.setYRot(entity.yRotO);
            }else {
                entity.lookAtEntity(target);
            }

            if(tick==1){
                entity.attack2();
            }
        }
    };
    public static final AnimationAct<Rosmontis> ATTACK_2_FLY = new AnimationAct<Rosmontis>("attack_2_fly",35){
        @Override
        public void tickUpdate(Rosmontis entity) {
            int tick = entity.getAnimationTick();
            RandomSource random = entity.getRandom();
            Entity target = entity.getTarget();
            entity.locateEntity();
            if(target==null||tick>12){
                entity.setYRot(entity.yRotO);
            }else {
                entity.lookAtEntity(target);
            }

            if(tick==1&&target!=null){
                if(entity.installations[0]!=null){
                    entity.installations[0].skill4(0,target.position());
                }
            }
        }
    };
    public static final AnimationAct<Rosmontis> ATTACK_3 = new AnimationAct<Rosmontis>("attack_3",35){
        @Override
        public void tickUpdate(Rosmontis entity) {
            int tick = entity.getAnimationTick();
            RandomSource random = entity.getRandom();
            Entity target = entity.getTarget();
            entity.locateEntity();
            if(target==null||tick>10){
                entity.setYRot(entity.yRotO);
            }else {
                entity.lookAtEntity(target);
            }

            if(tick==1){
                if(entity.getState()){
                    if(target!=null){
                        if (entity.installations[0] != null) {
                            entity.installations[0].skill4(0, target.position());
                        }
                        if (entity.installations[1] != null) {
                            entity.installations[1].skill4(1, target.position());
                        }
                    }
                }
                else
                    entity.attack3();
            }
        }
    };
    public static final AnimationAct<Rosmontis> ATTACK_4 = new AnimationAct<Rosmontis>("attack_4",40){
        @Override
        public void tickUpdate(Rosmontis entity) {
            int tick = entity.getAnimationTick();
            RandomSource random = entity.getRandom();
            Entity target = entity.getTarget();
            entity.locateEntity();
            if(target==null){
                entity.setYRot(entity.yRotO);
            }else {
                entity.lookAtEntity(target);
            }

            if(tick==15){
                if(!entity.getState()) {
                    RosmontisInstallation installation2 = entity.installations[0];
                    if (installation2 != null) {
                        installation2.attack4();
                    }
                }
            }
            if(tick==18){
                if(!entity.getState()) {
                    RosmontisInstallation installation2 = entity.installations[1];
                    if (installation2 != null) {
                        installation2.attack4();
                    }
                }else {
                    if(target!=null){
                        RosmontisInstallation installation2 = entity.installations[3];
                        if (installation2 != null) {
                            installation2.skill4(3,target.position());
                        }
                    }
                }
            }
            if(tick==21){
                if(!entity.getState()) {
                    RosmontisInstallation installation2 = entity.installations[2];
                    if (installation2 != null) {
                        installation2.attack4();
                    }
                }
            }
            if(tick==24){
                if(!entity.getState()){
                    RosmontisInstallation installation2 = entity.installations[3];
                    if (installation2 != null) {
                        installation2.attack4();
                    }
                }else {
                    if(target!=null){
                        RosmontisInstallation installation2 = entity.installations[3];
                        if (installation2 != null) {
                            installation2.skill4(3,target.position());
                        }
                    }
                }
            }
        }
    };
    public static final AnimationAct<Rosmontis> SKILL_1 = new AnimationAct<Rosmontis>("skill_1",25){
        @Override
        public void tickUpdate(Rosmontis entity) {
            int tick = entity.getAnimationTick();
            RandomSource random = entity.getRandom();
            Entity target = entity.getTarget();
            entity.locateEntity();
            if(target==null){
                entity.setYRot(entity.yRotO);
            }else {
                entity.lookAtEntity(target);
            }

            if(tick==11){
                //if(target!=null){
                //    entity.spawnGravitationalTarp(target.position(),100);
                //}
                if(target!=null) {
                    for (int i = 0; i < 3; i++) {
                        entity.spawnGravitationalTarp(target.position().add(new Vec3(0,0,6*(1+random.nextFloat())).yRot(6.28f*random.nextFloat())), 160);
                    }
                } else {
                    for(int i =0;i<3;i++){
                        entity.spawnGravitationalTarp(entity.position().add(8*(1f-random.nextFloat()*2),0,8*(1f-random.nextFloat()*2)),100);
                    }
                }
            }
        }
    };
    public static final AnimationAct<Rosmontis> SKILL_ARMOR = new AnimationAct<Rosmontis>("skill_1",25){
        @Override
        public void tickUpdate(Rosmontis entity) {
            int tick = entity.getAnimationTick();
            RandomSource random = entity.getRandom();
            Entity target = entity.getTarget();
            entity.locateEntity();
            if(target==null){
                entity.setYRot(entity.yRotO);
            }else {
                entity.lookAtEntity(target);
            }

            Vec3 instancePos = null;
            Partner<?> partner = PartnerUtil.getPartner(entity);
            if(partner!=null){
                instancePos = partner.getInstancePos();
            }

            if(tick==11){
                if(instancePos!=null){
                    entity.setSummonArmorRegion(instancePos, 400);
                }
                else {
                    if (target != null) {
                        entity.setSummonArmorRegion(entity.position(), 400);
                    } else {
                        entity.setSummonArmorRegion(entity.position().add(16 * (1f - random.nextFloat() * 2), 0, 16 * (1f - random.nextFloat() * 2)), 1200);
                    }
                }
            }
        }
    };
    public static final AnimationAct<Rosmontis> SKILL_2 = new AnimationAct<Rosmontis>("skill_2",160){
        @Override
        public void tickUpdate(Rosmontis entity) {
            int tick = entity.getAnimationTick();
            RandomSource random = entity.getRandom();
            Entity target = entity.getTarget();
            entity.locateEntity();
            if(target==null){
                entity.setYRot(entity.yRotO);
            }else {
                entity.lookAtEntity(target);
            }

            if(tick==7){
                if(target!=null){
                    entity.skill2(target.position());
                }else {
                    entity.skill2(FFEntityUtils.getBodyRotVec(entity,new Vec3(0,10,0)));
                }
            }
        }
    };
    public static final AnimationAct<Rosmontis> SKILL_2_FLY = new AnimationAct<Rosmontis>("skill_2_fly",160){
        @Override
        public void tickUpdate(Rosmontis entity) {
            int tick = entity.getAnimationTick();
            RandomSource random = entity.getRandom();
            Entity target = entity.getTarget();
            entity.locateEntity();
            if(target==null){
                entity.setYRot(entity.yRotO);
            }else {
                entity.lookAtEntity(target);
            }

            if(tick==7){
                if(target!=null){
                    entity.skill2(target.position());
                }else {
                    entity.skill2(FFEntityUtils.getBodyRotVec(entity,new Vec3(0,10,0)));
                }
            }
        }
    };
    public static final AnimationAct<Rosmontis> SKILL_3 = new AnimationAct<Rosmontis>("skill_3",60){
        @Override
        public void tickUpdate(Rosmontis entity) {
            int tick = entity.getAnimationTick();
            RandomSource random = entity.getRandom();
            Entity target = entity.getTarget();
            entity.locateEntity();
            if(target==null){
                entity.setYRot(entity.yRotO);
            }else {
                entity.lookAtEntity(target);
            }

            if(tick==40){
                RLClientUseUtils.StartCameraShake(entity.level(),entity.position(),32,0.04f,10,10);

                //Vec3 bodyRotVec = FFEntityUtils.getBodyRotVec(entity, new Vec3(0, -3, 10));
                //entity.spawnRosStone(bodyRotVec,3);
                //entity.addSpawnStoneTimer(bodyRotVec,40,3);
                int[] time = new int[]{40,100,160};
                ArrayUtil.shuffleArray(time);
                entity.addSpawnStoneTimer(FFEntityUtils.getBodyRotVec(entity,new Vec3(6,0,6)),random.nextInt(4),1 ,time[0]);
                entity.addSpawnStoneTimer(FFEntityUtils.getBodyRotVec(entity,new Vec3(-6,0,6)),random.nextInt(4),2,time[1]);
                entity.addSpawnStoneTimer(FFEntityUtils.getBodyRotVec(entity,new Vec3(0,0,-4)),random.nextInt(4),3,time[2]);
            }
        }
    };
    public static final AnimationAct<Rosmontis> SKILL_4 = new AnimationAct<Rosmontis>("skill_4",140){
        @Override
        public void tickUpdate(Rosmontis entity) {
            int tick = entity.getAnimationTick();
            RandomSource random = entity.getRandom();
            Entity target = entity.getTarget();
            entity.locateEntity();
            if(target==null){
                entity.setYRot(entity.yRotO);
            }else {
                entity.lookAtEntity(target);
            }

            Vec3 pos;

            if(target!=null){
                Vec3 position = entity.position().subtract(target.position()).normalize();
                pos = target.position().add(position.scale(2));
            }else {
                pos = FFEntityUtils.getBodyRotVec(entity,new Vec3(0,10,0));
            }

            Partner<?> partner = PartnerUtil.getPartner(entity);
            if(partner!=null){
                LivingEntity instancePos = partner.getInstanceTarget();
                if(instancePos!=null){
                    pos = instancePos.position();
                }
            }

            if(tick==10){
                RosmontisInstallation installation2 = entity.installations[0];
                if(installation2!=null){
                    installation2.skill4(0,pos);
                }
            }else if(tick==40){
                RosmontisInstallation installation2 = entity.installations[1];
                if(installation2!=null){
                    installation2.skill4(1,pos);
                }
            }else if(tick==70){
                RosmontisInstallation installation2 = entity.installations[2];
                if(installation2!=null){
                    installation2.skill4(2,pos.add(0,-0.3,0));
                }
            }else if(tick==100){
                RosmontisInstallation installation2 = entity.installations[3];
                if(installation2!=null){
                    installation2.skill4(3, MathUtils.getFirstBlockAbove(entity.level(),pos.add(0,-3f,0),5));
                }
            }
        }
    };
    public LivingEntity reTarget;

    public static final AnimationAct<Rosmontis> STATE_1_TO_2 = new AnimationAct<Rosmontis>("1to2",380,1){
        @Override
        public void start(Rosmontis entity) {
            super.start(entity);
            entity.setHealth(entity.getMaxHealth()/3);
            entity.reTarget = entity.getTarget();
        }

        @Override
        public void tickUpdate(Rosmontis entity) {
            int tick = entity.getAnimationTick();
            RandomSource random = entity.getRandom();
            Entity target = entity.getTarget();
            entity.locateEntity();
            if(target==null||tick<=240){
                entity.setYRot(entity.yRotO);
            }else {
                entity.lookAtEntity(target);
            }
            if(tick==268){
                RLClientUseUtils.StartCameraShake(entity.level(),entity.position(),32,0.02f,100,20);
            }
            if(tick==320){
                PerlinNoise perlinNoise = new PerlinNoise();
                int scale = 4;
                float[] randomF = new float[81];
                int timeCounder = 0;
                for(int i=0;i<81;i++){
                    randomF[i] = random.nextFloat();
                    if(randomF[i]<=0.3)
                        timeCounder++;
                }
                int[] randomInt = new int[timeCounder];
                for(int i=0;i<timeCounder;i++){
                    randomInt[i] = 40+i*80;
                }
                ArrayUtil.shuffleArray(randomInt);

                for(int i=-scale;i<=scale;i++){
                    for(int i1=-scale;i1<=scale;i1++){
                        if((i==0&&i1==0))continue;

                        int index = (i+4)*9+(i1+4);
                        if(randomF[index]>0.1) continue;

                        timeCounder--;

                        int i11 = (int) ((perlinNoise.noise(i*16,i1*16)*3+3)/2);
                        //entity.spawnRosStone(entity.position().add(i*4,0,i1*4),i11+1);
                        entity.addSpawnStoneTimer(entity.position().add(i*4,0,i1*4),40+random.nextInt(11),i11+1,randomInt[timeCounder]);
                    }
                }
            }
            if(tick==360){
                RLClientUseUtils.StartCameraShake(entity.level(),entity.position(),32,0.04f,10,10);
                if(isNoExist(entity.installations[0])){
                    RosmontisInstallation rosmontisInstallation = RosmontisInstallation.SpawnInstallation(entity.level(), entity, 0);
                    entity.installations[0] = rosmontisInstallation;
                }
                if(isNoExist(entity.installations[1])){
                    RosmontisInstallation rosmontisInstallation = RosmontisInstallation.SpawnInstallation(entity.level(), entity, 1);
                    entity.installations[1] = rosmontisInstallation;
                }
                if(isNoExist(entity.installations[2])){
                    RosmontisInstallation rosmontisInstallation = RosmontisInstallation.SpawnInstallation(entity.level(), entity, 2);
                    entity.installations[2] = rosmontisInstallation;
                }
                if(isNoExist(entity.installations[3])){
                    RosmontisInstallation rosmontisInstallation = RosmontisInstallation.SpawnInstallation(entity.level(), entity, 3);
                    entity.installations[3] = rosmontisInstallation;
                }
            }
            if(tick==370){
                entity.setState(true);
            }

            if(tick>=1&&tick<=240&&tick%80==0){
                float damage = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
                List<LivingEntity> entitiesOfClass = entity.level().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(11), entity1 -> entity1.distanceTo(entity) < 11);
                for(LivingEntity living :entitiesOfClass){
                    if(living == entity) continue;
                    if(living.distanceTo(entity)>5.5&&!living.onGround()) continue;

                    living.hurt(living.damageSources().mobAttack(living),damage*2);
                }
            }
        }

        private boolean isNoExist(RosmontisInstallation rosmontisInstallation){
            return rosmontisInstallation==null||rosmontisInstallation.isRemoved()||rosmontisInstallation.animationType==-1;
        }

        @Override
        public void stop(Rosmontis entity) {
            super.stop(entity);
            if(entity.reTarget!=null&&entity.reTarget.isAlive()){
                entity.setTarget(entity.reTarget);
            }
            entity.setState(true);
            if(entity.timeSinceRemote3<370){
                entity.timeSinceRemote3 = 370;
            }
        }
    };

    public static final AnimationAct<Rosmontis> STORY_MOVE = new AnimationAct<Rosmontis>("story_move",53,1){
        @Override
        public void tickUpdate(Rosmontis entity) {
            int tick = entity.getAnimationTick();
            Level level = entity.level();
            if(tick==33){
                entity.entityData.set(START_BLACK,true,true);
            }
            if(tick==50){
                BlockPos spawnPos = entity.getSpawnPos();
                if(spawnPos!=null){
                    BlockPos offset = spawnPos.offset(-36+58, -38, 26);
                    if(level.getBlockState(offset).isAir()||level.getBlockState(offset.above()).isAir()){
                        entity.setPos(offset.getX()+0.5,offset.getY()+0.5,offset.getZ()+0.5);
                        entity.isOnStoryGround = true;
                    }
                }
            }
        }
    };
    public static final AnimationAct<Rosmontis> BREAK = new AnimationAct<Rosmontis>("break",50,1){
        @Override
        public void start(Rosmontis entity) {
            entity.setTarget(null);
            for (RosmontisInstallation installation : entity.installations) {
                if (installation != null && !installation.isRemoved()) {
                    installation.setAnimation(-1,10);
                }
            }
            if(entity.livingInstallation!=null) entity.livingInstallation.discard();
            entity.setState(false);
            super.start(entity);
        }

        @Override
        public void tickUpdate(Rosmontis entity) {
            entity.setYRot(entity.yRotO);
            int tick = entity.getAnimationTick();
            entity.locateEntity();
            entity.setLastHurtByMob(null);
            entity.setTarget(null);
            if(tick<=16&&tick%4==0){
                float health = (tick+4)/20.f;
                entity.setHealth(entity.getMaxHealth()*health);
            }
        }
    };
    public static final AnimationAct<Rosmontis> DIE = new AnimationAct<Rosmontis>("die",40,1);

    private static final EntityDataAccessor<Boolean> RUN = SynchedEntityData.defineId(Rosmontis.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> STATE = SynchedEntityData.defineId(Rosmontis.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> START_BLACK = SynchedEntityData.defineId(Rosmontis.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_FLAYING = SynchedEntityData.defineId(Rosmontis.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<CompoundTag> GRAVITATION = SynchedEntityData.defineId(Rosmontis.class, EntityDataSerializers.COMPOUND_TAG);
    private static final EntityDataAccessor<CompoundTag> ARMOR_REGION = SynchedEntityData.defineId(Rosmontis.class, EntityDataSerializers.COMPOUND_TAG);


    @Override
    public AnimationAct[] getAnimations() {
        return new AnimationAct[]{NO_ANIMATION,SKILL_4,IDLE_TO_ACT,MOVE_1,MOVE_12,MOVE_2,SKILL_ARMOR,
                REMOTE_1_FLY,REMOTE_2_FLY,SKILL_2_FLY,STORY_MOVE,BREAK,DIE,
                SKILL_1,SKILL_2,SKILL_3,REMOTE_1,REMOTE_2,REMOTE_3,ATTACK_1,ATTACK_2,ATTACK_2_FLY,ATTACK_3,ATTACK_4,STATE_1_TO_2};
    }

    private final ServerBossEvent bossInfo= ConfigHandler.COMMON.GLOBALSETTING.healthBarIsNearShow.get()?new FFBossInfoServer(this,5): new CustomBossInfoServer(this,5);

    public RosmontisInstallation[] installations = new RosmontisInstallation[4];
    public RosmontisLivingInstallation livingInstallation;
    public List<GravitationalTrap> gravitationalTraps = new ArrayList<>();
    public SummonArmorRegion summonArmorRegion ;
    public List<SpawnStoneTimer> spawnStoneTimers = new ArrayList<>();
    public int timeSinceRemote3;
    private int noActMode;

    public Rosmontis(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        super.tick();

        startBlackO = startBlack;
        if(startBlack>0)startBlack--;

        if (!level().isClientSide){
            if(isAggressive()||getAnimation() == STATE_1_TO_2){
                noActMode = 0;
            }else {
                noActMode++;
                if(noActMode==200){
                    for (RosmontisInstallation installation : installations) {
                        if (installation != null && !installation.isRemoved()) {
                            installation.setAnimation(-1,10);
                        }
                    }
                    if(livingInstallation!=null) livingInstallation.kill();
                    setState(false);
                }
            }
            //LivingEntity target = getTarget();
            //if(target instanceof Mob mob){
            //    if(livingInstallation!=null&&livingInstallation.isAlive()){
            //        mob.setTarget(livingInstallation);
            //    }
            //}
            if(isAggressive()&&!getState()&&getAnimation()!=STATE_1_TO_2){
                float ratio = getHealth() / getMaxHealth();
                if(ratio<0.333){
                    AnimationActHandler.INSTANCE.sendAnimationMessage(this,STATE_1_TO_2);
                }
            }
            if(getState()){
                if(level().isClientSide){
                    if(tickCount%20==0){
                        ros_state_2_idle();
                    }
                }
            }
        }


        if (tickCount % 4 == 0&&bossInfo instanceof IBossInfoUpdate update) update.update();

        tickGravitation();
        state1to2fx();

        if(waitTargetTime>=0){
            if(waitTargetTime == 0){
                if(challengePlayer!=null){
                    setTarget(challengePlayer);
                }
            }
            waitTargetTime--;
        }
    }

    private void tickGravitation(){
        Iterator<GravitationalTrap> iterator = gravitationalTraps.iterator();
        while (iterator.hasNext()){
            GravitationalTrap next = iterator.next();
            if(next.count>next.maxTime){
                iterator.remove();
            }else {
                next.tick();
            }
        }
        Iterator<SpawnStoneTimer> iterator1 = spawnStoneTimers.iterator();
        while (iterator1.hasNext()){
            SpawnStoneTimer next = iterator1.next();
            if(next.maxTime<=0){
                spawnRosStone(next.position,next.scale,next.timeToShoot);
                iterator1.remove();
            }else {
                if(!level().isClientSide){
                    //((ServerLevel)level()).sendParticles(ParticleTypes.FLAME,next.position.x,next.position.y,next.position.z,1,0,0,0,0);
                }
                next.tick();
            }
        }
        if(summonArmorRegion!=null){
            summonArmorRegion.tick();
            if(summonArmorRegion.maxTime<=0){
                summonArmorRegion = null;
            }
        }
        if(!level().isClientSide){
            if(allGravitation!=null){
                CompoundTag compoundTag = new CompoundTag();
                compoundTag.put("list",allGravitation);
                this.entityData.set(GRAVITATION,compoundTag,true);
                allGravitation = null;
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        AnimationAct animation = getAnimation();
        if(animation==BREAK) return false;
        if(animation ==STATE_1_TO_2||animation==IDLE_TO_ACT) return false;
        if(animation ==SKILL_1) amount*=0.4f;

        float limit = (float)(getMaxHealth()*ConfigHandler.COMMON.MOBS.ROSMONTIS.damageConfig.damageCap.get());
        if(amount>limit&&!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) amount = limit;

        if(summonArmorRegion!=null) {
            if(summonArmorRegion.position.subtract(position()).length()<8){
                amount*=0.6f;
            }
        }

        if(livingInstallation!=null&&livingInstallation.isAlive()) {
            amount = amount*0.4f;
            livingInstallation.hurt(source,amount);
            Entity entity = source.getEntity();
            if(entity instanceof Mob mob){
                if(mob.distanceTo(this)>8){
                    livingInstallation.invulnerableTime = 0;
                    livingInstallation.hurt(source,amount*0.5f);
                    amount = 0;
                }
                mob.setTarget(livingInstallation);
            }
        }

        return super.hurt(source, amount);
    }
    //转圈圈的方法
    public void startCycle(int tick){
        cycleTick = 0;
        Vec3 vec3 = position();
        if(getTarget()!=null) {
            Vec3 targetVec = getTarget().position();
            startP = org.joml.Math.atan2(targetVec.x - vec3.x, vec3.z - targetVec.z) + org.joml.Math.toRadians(90);
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
            cycle = getTarget().position().add(cycleRadius * org.joml.Math.cos(cycleTick * 0.628 * speed + startP), 0, cycleRadius * org.joml.Math.sin(cycleTick * 0.628 * speed + startP));
        }
        else
            cycle = position();
        return cycle;
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

    @Override
    public SoundEvent getIntroMusic() {
        return SoundHandle.ROSMONTIS_INTRO.get();
    }

    @Override
    public int timeToLoop() {
        return 523;
    }

    @Override
    public SoundEvent getLoopMusic() {
        return SoundHandle.ROSMONTIS_LOOP.get();
    }

    @Override
    public void die(DamageSource pDamageSource) {
        if (challengePlayer==null||!challengePlayer.isAlive()){
            super.die(pDamageSource);
            if (!this.isRemoved() && bossInfo instanceof IBossInfoUpdate update) {
                update.update();
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

    public boolean isOnStoryGround;

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("state",getState());
        pCompound.putBoolean("isOnStoryGround",isOnStoryGround);
        pCompound.putBoolean("flaying",getIsFlaying());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        setState(pCompound.getBoolean("state"));
        isOnStoryGround = pCompound.getBoolean("isOnStoryGround");
        setIsFlaying(pCompound.getBoolean("flaying"));
    }


    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        CompoundTag tag = new CompoundTag();
        BlockPos spawnPos = getSpawnPos();
        if(spawnPos!=null){
            tag.put("spawn", NbtUtils.writeBlockPos(spawnPos));
        }
        buffer.writeNbt(tag);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        CompoundTag compoundTag = additionalData.readNbt();
        if(compoundTag.contains("spawn")){
            init(NbtUtils.readBlockPos(compoundTag.getCompound("spawn")));
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(RUN,false);
        this.entityData.define(STATE,false);
        this.entityData.define(START_BLACK,false);
        //this.entityData.define(IS_FLAYING,false);
        this.entityData.define(GRAVITATION,new CompoundTag());
        this.entityData.define(ARMOR_REGION,new CompoundTag());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        super.onSyncedDataUpdated(pKey);
        if(pKey.equals(GRAVITATION)){
            CompoundTag compoundTag = this.entityData.get(GRAVITATION);
            ListTag list = compoundTag.getList("list", 10);
            for(int i=0;i<list.size();i++){
                GravitationalTrap gravitationalTrap = new GravitationalTrap();
                gravitationalTrap.deserializeNBT(list.getCompound(i));
                gravitationalTraps.add(gravitationalTrap);
            }
        }else if(pKey.equals(ARMOR_REGION)){
            SummonArmorRegion armorRegion = new SummonArmorRegion();
            armorRegion.deserializeNBT(this.entityData.get(ARMOR_REGION));
            summonArmorRegion = armorRegion;
        }else if(pKey.equals(START_BLACK)){
            startBlack = startBlackO = 20;
        }
    }

    @Override
    protected ConfigHandler.CombatConfig getCombatConfig() {
        return ConfigHandler.COMMON.MOBS.ROSMONTIS.combatConfig;
    }


    @Override
    protected ConfigHandler.NeutralProtectionConfig getNeutralProtectionConfig() {
        return ConfigHandler.COMMON.MOBS.ROSMONTIS.neutralProtectionConfig;
    }

    @Override
    protected <T extends GeoEntity> void basicAnimation(AnimationState<T> event) {
        if(getState()){
            if (event.isMoving()) {
                event.getController().setAnimation(RawAnimation.begin().thenLoop("act_walk_1"));
            } else {
                event.getController().setAnimation(RawAnimation.begin().thenLoop("act_idle_1"));
            }
        } else if (isAggressive()) {
            if (event.isMoving()) {
                event.getController().setAnimation(RawAnimation.begin().thenLoop("act_walk"));
            } else {
                event.getController().setAnimation(RawAnimation.begin().thenLoop("act_idle"));
            }
        } else {
            if (event.isMoving()) {
                event.getController().setAnimation(RawAnimation.begin().thenLoop("walk"));
            } else {
                event.getController().setAnimation(RawAnimation.begin().thenLoop("idle"));
            }
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RosmontisAutoDialogueGoal(this));
        this.goalSelector.addGoal(2,new RosmontisAttackAI(this));

        this.goalSelector.addGoal(7, new FFLookAtPlayerTotherGoal<>(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new FFRandomLookAroundGoal(this));

        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Zombie.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Ravager.class, true));
        this.targetSelector.addGoal(3, new HurtByTargetNoPlayerGoal(this).setAlertOthers(RhodesIslandEntity.class));
    }

    @Override
    public AnimationAct getDeathAnimation() {
        return DIE;
    }

    @Override
    public boolean hasBossBar() {
        return true;
    }

    @Override
    public BossEvent.BossBarColor bossBarColor() {
        return BossEvent.BossBarColor.WHITE;
    }

    public static BlockPos getFirstBlockAbove(Level world, BlockPos pos) {
        BlockPos posCurrent = null;
        int i = 0;
        for (int y = pos.getY() + 1; y < world.getMaxBuildHeight(); y++) {
            if(i>6) break;
            posCurrent = new BlockPos(pos.getX(), y, pos.getZ());
            if (
                    (
                            (world.getBlockState(posCurrent).isAir()) &&
                            (world.getBlockState(posCurrent.above()).isAir()) &&
                            !world.getBlockState(posCurrent.below()).isAir())
                    ) {
                return posCurrent;
            }
            i++;
        }
        return null;
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (getState()||getIsFlaying()) {
            this.setNoGravity(true);

            BlockPos under = this.blockPosition().below(7);
            BlockPos nearestGround = getFirstBlockAbove(this.level(),under);
            if(nearestGround==null) nearestGround = under;

            double targetY = nearestGround.getY() + 5.1; // 地面高+5
            if (Math.abs(this.getY() - targetY) > 0.1) {
                double dy = targetY - this.getY();
                this.setDeltaMovement(this.getDeltaMovement().x, dy * 0.5, this.getDeltaMovement().z);
            } else {
                this.setDeltaMovement(this.getDeltaMovement().x, 0, this.getDeltaMovement().z);
            }

            super.travel(new Vec3(travelVector.x, 0, travelVector.z));
        } else {
            this.setNoGravity(false);
            super.travel(travelVector);
        }
    }

    public void remoteAct1(Vec3 pos){
        RosmontisInstallation installation = installations[0];
        if(installation!=null){
            installation.remoteAct1(pos);
        }
    }
    public void remoteActDamage1(int type,double Radio,float damageMul){
        RosmontisInstallation installation = installations[type];
        if(installation!=null){
            float damage = (float) getAttributeValue(Attributes.ATTACK_DAMAGE);
            List<LivingEntity> entitiesOfClass = level().getEntitiesOfClass(LivingEntity.class, installation.getBoundingBox().inflate(4),entity->{
                return entity.distanceTo(installation)<Radio;
            });
            for(LivingEntity living:entitiesOfClass){
                if(living == this) return;
                living.invulnerableTime=0;
                doHurtEntity(living,damageSources().mobAttack(this),damage*damageMul);
            }
        }
    }

    public void spawnRosStone(Vec3 pos,int scale,int timeToShoot){
        if(level().isClientSide) return;

        pos = pos.add(0,-scale-0.1,0);

        RosmontisBlock block = new RosmontisBlock(this,level());
        if(scale==1){
            BlockPos blockPos = new BlockPos((int) pos.x, (int) pos.y, (int) pos.z);
            BlockPos[] blockPoss = new BlockPos[]{new BlockPos(0,0,0)};
            BlockState blockState = level().getBlockState(blockPos);
            if(!blockState.isAir()) {
                BlockState[] blockStates = new BlockState[]{blockState};
                block.setMultiBlock(blockStates,blockPoss,1);
            }else {
                BlockState[] blockStates = new BlockState[]{Blocks.DIRT.defaultBlockState()};
                block.setMultiBlock(blockStates,blockPoss,1);
            }
        }
        else if(scale==2){
            BlockPos[] blockPoss = new BlockPos[8];
            BlockState[] blockStates = new BlockState[8];
            for(int i = 0;i<2;i++){
                for(int i1 = 0;i1<2;i1++){
                    for(int i2 = 0;i2<2;i2++){
                        blockPoss[i*4+i1*2+i2] = new BlockPos((int) pos.x+i, (int) pos.y+i1, (int) pos.z+i2);
                    }
                }
            }

            BlockPos[] blockPoss1 = new BlockPos[8];
            for(int i = 0;i<2;i++){
                for(int i1 = 0;i1<2;i1++){
                    for(int i2 = 0;i2<2;i2++){
                        blockPoss1[i*4+i1*2+i2] = new BlockPos(i, i1, i2);
                    }
                }
            }

            for(int i =0;i<blockPoss.length;i++){
                BlockState blockState = level().getBlockState(blockPoss[i]);
                if(!blockState.isAir()){
                    blockStates[i] = blockState;
                }else {
                    blockStates[i] = Blocks.DIRT.defaultBlockState();
                }
            }
            block.setMultiBlock(blockStates,blockPoss1,2);
        }
        else if(scale==3){
            BlockPos[] blockPoss = new BlockPos[27];
            BlockState[] blockStates = new BlockState[27];
            for(int i = 0;i<3;i++){
                for(int i1 = 0;i1<3;i1++){
                    for(int i2 = 0;i2<3;i2++){
                        blockPoss[i*9+i1*3+i2] = new BlockPos((int) pos.x+i, (int) pos.y+i1, (int) pos.z+i2);
                    }
                }
            }

            BlockPos[] blockPoss1 = new BlockPos[27];
            for(int i = 0;i<3;i++){
                for(int i1 = 0;i1<3;i1++){
                    for(int i2 = 0;i2<3;i2++){
                        blockPoss1[i*9+i1*3+i2] = new BlockPos(i, i1, i2);
                    }
                }
            }

            for(int i =0;i<blockPoss.length;i++){
                BlockState blockState = level().getBlockState(blockPoss[i]);
                if(!blockState.isAir()){
                    blockStates[i] = blockState;
                }else {
                    blockStates[i] = Blocks.DIRT.defaultBlockState();
                }
            }
            block.setMultiBlock(blockStates,blockPoss1,3);
        }
        block.setTimeToShoot(timeToShoot);
        block.setPos(pos);
        level().addFreshEntity(block);
    }

    public void remoteAct2(Vec3 pos){
        RosmontisInstallation installation1 = installations[2];
        if(installation1!=null){
            installation1.remoteAct2(pos.add(0,0,0.5));
        }
        RosmontisInstallation installation2 = installations[3];
        if(installation2!=null){
            installation2.remoteAct2(pos.add(0,0,-0.5));
        }
    }

    public void remoteAct3(Vec3 pos){
        RosmontisInstallation installation1 = installations[1];
        if(installation1!=null){
            pos = MathUtils.getFirstBlockAbove(level(),pos.add(0,-2,0),12);
            installation1.remoteAct3(pos);
            RosmontisLivingInstallation rosmontisLivingInstallation = RosmontisLivingInstallation.SpawnInstallation(level(), this, pos.add(0,5,0));
            rosmontisLivingInstallation.setDeltaMovement(0,-3,0);
            livingInstallation = rosmontisLivingInstallation;
        }
    }
    public void remoteAct3Appear(Vec3 pos){
        RosmontisInstallation installation1 = installations[1];
        if(installation1!=null){
            installation1.remoteAct3Appear(pos);
        }
    }

    public boolean installationIsAble(int index){
        RosmontisInstallation rosmontisInstallation = installations[index];
        return rosmontisInstallation!=null&&rosmontisInstallation.animationType == 0;
    }

    public void attack1(){
        RosmontisInstallation installation1 = installations[0];
        if(installation1!=null){
            installation1.attack1();
        }
    }

    public void attack2(){
        RosmontisInstallation installation1 = installations[0];
        if(installation1!=null){
            installation1.attack2();
        }
    }

    public void attack3(){
        RosmontisInstallation installation1 = installations[3];
        if(installation1!=null){
            installation1.attack3();
        }
        RosmontisInstallation installation2 = installations[2];
        if(installation2!=null){
            installation2.attack31();
        }
    }

    public void skill2(Vec3 pos){
        RosmontisInstallation installation1 = installations[3];
        if(installation1!=null){
            installation1.skill2(pos);
        }
        RosmontisInstallation installation2 = installations[2];
        if(installation2!=null){
            installation2.skill2(pos);
        }
    }

    public boolean getRun(){
        return this.entityData.get(RUN);
    }

    public void setRun( boolean run){
        this.entityData.set(RUN,run);
    }

    public boolean getState(){
        return this.entityData.get(STATE);
    }

    public void setState( boolean run){
        this.entityData.set(STATE,run);
    }

    public boolean getStartBlack(){
        return this.entityData.get(START_BLACK);
    }

    public void setStartBlack( boolean run){
        this.entityData.set(START_BLACK,run);
    }

    boolean isFlaying;

    public boolean getIsFlaying(){
        return isFlaying;
    }

    public void setIsFlaying( boolean run){
        isFlaying = run;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return GuerrillasEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 300.0D)
                .add(Attributes.ATTACK_DAMAGE, 12.0f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
                .add(Attributes.FOLLOW_RANGE, 64)
                .add(Attributes.ARMOR, 2.0f);
    }

    private ListTag allGravitation;

    public void spawnGravitationalTarp(Vec3 position,int maxTime){
        if(!level().isClientSide){
            GravitationalTrap gravitationalTrap = new GravitationalTrap(position, maxTime);
            gravitationalTraps.add(gravitationalTrap);

            if(allGravitation==null){
                allGravitation = new ListTag();
                allGravitation.add(gravitationalTrap.serializeNBT());
            }else {
                allGravitation.add(gravitationalTrap.serializeNBT());
            }
        }
    }

    public void addSpawnStoneTimer(Vec3 position,int maxTime,int scale,int timeToShoot){
        if(!level().isClientSide){
            if(getState()){
                position = position.add(0,-5.1,0);
            }
            spawnStoneTimers.add(new SpawnStoneTimer(position,maxTime,scale,timeToShoot));
        }
    }

    public void setSummonArmorRegion(Vec3 position,int maxTime){
        if(!level().isClientSide){
            summonArmorRegion = new SummonArmorRegion(position,maxTime);
            this.entityData.set(ARMOR_REGION,summonArmorRegion.serializeNBT(),true);
        }
    }

    public void ros_gravatationFX(int time, Vec3 pos, Vector3f scale){
        Gradient toAlpha = new Gradient(new GradientColor(0XFFFFFFFF, 0XFFFFFFFF, 0X00FFFFFF));

        RLParticle rlParticle = new RLParticle(level());
        rlParticle.config.setDuration(time);
        rlParticle.config.setStartLifetime(NumberFunction.constant(20));
        rlParticle.config.setStartSize(new NumberFunction3(0.5,6,0.5));
        rlParticle.config.setStartColor(new Gradient(new GradientColor(0X5FB2F5FF)));
        rlParticle.config.getEmission().setEmissionRate(NumberFunction.constant(0.5));
        rlParticle.config.setStartSpeed(NumberFunction.constant(0));
        EmissionSetting.Burst burst = new EmissionSetting.Burst();burst.time=time-1;burst.setCount(NumberFunction.constant(8));
        EmissionSetting.Burst burstEnd = new EmissionSetting.Burst();burstEnd.setCount(NumberFunction.constant(8));
        rlParticle.config.getEmission().addBursts(burst);rlParticle.config.getEmission().addBursts(burstEnd);
        Circle circle = new Circle();circle.setRadius(2);
        rlParticle.config.getShape().setShape(circle);
        rlParticle.config.getMaterial().setMaterial(MaterialHandle.CIRCLE.create());
        rlParticle.config.getRenderer().setRenderMode(RendererSetting.Particle.Mode.VerticalBillboard);
        rlParticle.config.getLights().open();
        rlParticle.config.getColorOverLifetime().open();
        rlParticle.config.getColorOverLifetime().setColor(toAlpha);
        rlParticle.config.getSizeOverLifetime().open();
        rlParticle.config.getSizeOverLifetime().setSize(new NumberFunction3(NumberFunction.constant(1),new Line(new float[]{0,0.3f,1},new float[]{0,1,1}),NumberFunction.constant(1)));

        RLParticle rlParticle1 = new RLParticle(level());
        rlParticle1.config.setDuration(time);
        rlParticle1.config.setStartLifetime(NumberFunction.constant(20));
        rlParticle1.config.setStartSize(new NumberFunction3(0.1,6,0.1));
        rlParticle1.config.setStartColor(new Gradient(new GradientColor(0X8FB2F5FF)));
        rlParticle1.config.setStartSpeed(NumberFunction.constant(0));
        rlParticle1.config.getEmission().setEmissionRate(NumberFunction.constant(0.5));
        EmissionSetting.Burst burst1 = new EmissionSetting.Burst();burst1.time=time-1;burst1.setCount(NumberFunction.constant(8));
        EmissionSetting.Burst burstEnd1 = new EmissionSetting.Burst();burstEnd1.setCount(NumberFunction.constant(8));
        rlParticle1.config.getEmission().addBursts(burst1);rlParticle1.config.getEmission().addBursts(burstEnd1);
        Circle circle1 = new Circle();circle1.setRadius(2);
        rlParticle1.config.getShape().setShape(circle1);
        rlParticle1.config.getMaterial().setMaterial(MaterialHandle.CIRCLE.create());
        rlParticle1.config.getRenderer().setRenderMode(RendererSetting.Particle.Mode.VerticalBillboard);
        rlParticle1.config.getRenderer().setBloomEffect(true);
        rlParticle1.config.getLights().open();
        rlParticle1.config.getColorOverLifetime().open();
        rlParticle1.config.getColorOverLifetime().setColor(toAlpha);
        rlParticle1.config.getSizeOverLifetime().open();
        rlParticle1.config.getSizeOverLifetime().setSize(new NumberFunction3(NumberFunction.constant(1),new Line(new float[]{0,0.3f,1},new float[]{0,1,1}),NumberFunction.constant(1)));

        RLParticle rlParticle2 = new RLParticle(level());
        rlParticle2.config.setDuration(time);
        rlParticle2.config.setStartLifetime(NumberFunction.constant(40));
        rlParticle2.config.setStartSize(new NumberFunction3(0.1));
        rlParticle2.config.setStartColor(new Gradient(new GradientColor(0XFFB2F5FF)));
        rlParticle2.config.setStartSpeed(NumberFunction.constant(0));
        rlParticle2.config.getEmission().setEmissionRate(NumberFunction.constant(0.5));
        EmissionSetting.Burst burstEnd2 = new EmissionSetting.Burst();burstEnd2.setCount(NumberFunction.constant(20));
        rlParticle2.config.getEmission().addBursts(burst);rlParticle2.config.getEmission().addBursts(burstEnd2);
        Circle circle2 = new Circle();circle2.setRadius(2);
        rlParticle2.config.getShape().setShape(circle2);
        rlParticle2.config.getMaterial().setMaterial(TBSMaterialHandle.PIXEL.create());
        rlParticle2.config.getLights().open();
        rlParticle2.config.getColorOverLifetime().open();
        rlParticle2.config.getColorOverLifetime().setColor(new Gradient(GradientHandle.CENTER_OPAQUE));
        rlParticle2.config.getVelocityOverLifetime().open();
        rlParticle2.config.getVelocityOverLifetime().setLinear(new NumberFunction3(NumberFunction.constant(0),new RandomLine(new float[]{0,1},new float[]{2,0},new float[]{0.15f,0.15f}),NumberFunction.constant(0)));
        rlParticle2.config.getRotationOverLifetime().open();
        rlParticle2.config.getRotationOverLifetime().setYaw(new RandomLine(new float[]{0,1},new float[]{0,360},new float[]{0,60}));
        rlParticle2.config.getNoise().open();
        rlParticle2.config.getNoise().setPosition(new NumberFunction3(0.05));

        RLParticle rlParticle3 = new RLParticle(level());
        rlParticle3.config.setDuration(time);
        rlParticle3.config.setStartLifetime(NumberFunction.constant(20));
        rlParticle3.config.setStartColor(new Gradient(new GradientColor(0X5FB2F5FF)));
        rlParticle3.config.setStartSpeed(NumberFunction.constant(0));
        rlParticle3.config.getEmission().setEmissionRate(NumberFunction.constant(0.5));
        EmissionSetting.Burst burstEnd3 = new EmissionSetting.Burst();burstEnd3.setCount(NumberFunction.constant(20));
        rlParticle3.config.getEmission().addBursts(burst);rlParticle3.config.getEmission().addBursts(burstEnd3);
        Circle circle3 = new Circle();circle3.setRadius(2);
        rlParticle3.config.getShape().setShape(circle3);
        rlParticle3.config.getMaterial().setMaterial(MaterialHandle.VOID.create());
        rlParticle3.config.getLights().open();
        rlParticle3.config.getColorOverLifetime().open();
        rlParticle3.config.getColorOverLifetime().setColor(toAlpha);
        rlParticle3.config.getVelocityOverLifetime().open();
        rlParticle3.config.getVelocityOverLifetime().setLinear(new NumberFunction3(NumberFunction.constant(0),new RandomConstant(5,3,true),NumberFunction.constant(0)));
        rlParticle3.config.getSizeOverLifetime().open();
        rlParticle3.config.getSizeOverLifetime().setSize(new NumberFunction3(NumberFunction.constant(1),new Line(new float[]{0,0.3f,1},new float[]{0,1,1}),NumberFunction.constant(1)));
        rlParticle3.config.getNoise().open();
        rlParticle3.config.getNoise().setPosition(new NumberFunction3(0.05));
        rlParticle3.config.trails.open();
        rlParticle3.config.trails.config.getLights().open();

        BlockEffect blockEffect = new BlockEffect(level(), pos);
        rlParticle.updateScale(scale);
        rlParticle1.updateScale(scale);
        rlParticle2.updateScale(scale);
        rlParticle3.updateScale(scale);

        rlParticle.emmit(blockEffect);
        rlParticle1.emmit(blockEffect);
        rlParticle2.emmit(blockEffect);
        rlParticle3.emmit(blockEffect);
    }

    public void ros_instationTrailFx(Vec3 pos, Vec3 targetPos){
        Vec3 vec3 = targetPos.subtract(pos);
        double d0 = vec3.horizontalDistance();
        float yRot = (float) (Mth.atan2(vec3.x, vec3.z) * (double) (180F / (float) Math.PI));
        float xRot = (float)(Mth.atan2(vec3.y, d0) * (double)(180F / (float)Math.PI));

        RLParticle rlParticle = new RLParticle(level());
        rlParticle.config.setDuration(40);
        rlParticle.config.setStartLifetime(NumberFunction.constant(16));
        rlParticle.config.setStartSize(new NumberFunction3(2));
        rlParticle.config.setStartColor(new Gradient(new GradientColor(0XFFB2F5FF)));
        rlParticle.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst = new EmissionSetting.Burst();burst.setCount(NumberFunction.constant(1));
        rlParticle.config.getEmission().addBursts(burst);
        rlParticle.config.setStartSpeed(NumberFunction.constant(0));
        rlParticle.config.getShape().setShape(new Dot());
        rlParticle.config.getShape().setPosition(new NumberFunction3(0,0,-20));
        rlParticle.config.getMaterial().setMaterial(MaterialHandle.VOID.create());
        rlParticle.config.getLights().open();
        rlParticle.config.getVelocityOverLifetime().open();
        rlParticle.config.getVelocityOverLifetime().setLinear(new NumberFunction3(NumberFunction.constant(0),NumberFunction.constant(0),new Line(new float[]{0,0.5f,1},new float[]{160,0,0})));
        rlParticle.config.trails.open();
        rlParticle.config.trails.config.material.setMaterial(TBSMaterialHandle.BIG_SMOKE.create());
        rlParticle.config.trails.setColorOverLifetime(new Gradient(GradientHandle.CENTER_OPAQUE));
        rlParticle.config.trails.setColorOverLifetime(new Gradient(GradientHandle.CENTER_OPAQUE));
        rlParticle.config.trails.config.lights.open();

        RLParticle rlParticle1 = new RLParticle(level());
        rlParticle1.config.setDuration(8);
        rlParticle1.config.setStartLifetime(NumberFunction.constant(20));
        rlParticle1.config.setStartSize(new NumberFunction3(0.6,0.1,0.1));
        rlParticle1.config.setStartRotation(new NumberFunction3(0,90,0));
        rlParticle1.config.setStartColor(new Gradient(new GradientColor(0XFFB2F5FF)));
        EmissionSetting.Burst burst1 = new EmissionSetting.Burst();burst1.setCount(NumberFunction.constant(1.6));
        burst1.cycles = 7;
        rlParticle1.config.setStartSpeed(NumberFunction.constant(0));
        rlParticle1.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        rlParticle1.config.getEmission().addBursts(burst1);
        Sphere circle1 = new Sphere();circle1.setRadius(1);
        rlParticle1.config.getShape().setShape(circle1);
        rlParticle1.config.getShape().setPosition(new NumberFunction3(NumberFunction.constant(0),NumberFunction.constant(0),new Line(new float[]{0,1},new float[]{0,16})));
        rlParticle1.config.getMaterial().setMaterial(TBSMaterialHandle.NO_GLOW_PIXEL.create());
        rlParticle1.config.getMaterial().setCull(false);
        rlParticle1.config.getRenderer().setRenderMode(RendererSetting.Particle.Mode.Vertical);
        rlParticle1.config.getRenderer().setBloomEffect(true);
        rlParticle1.config.getLights().open();
        rlParticle1.config.getColorOverLifetime().open();
        rlParticle1.config.getColorOverLifetime().setColor(new Gradient(GradientHandle.CENTER_OPAQUE));
        rlParticle1.config.getVelocityOverLifetime().open();
        rlParticle1.config.getVelocityOverLifetime().setLinear(new NumberFunction3(0,0,1));

        BlockEffect blockEffect = new BlockEffect(level(),pos);
        rlParticle.updateRotation(new Vector3f(0,yRot,xRot));

        rlParticle.emmit(blockEffect);
        rlParticle1.emmit(blockEffect);
    }

    public void ros_state_1_idle(){

    }

    public void ros_state_1to2(Vec3 pos){
        Gradient toAlpha = new Gradient(new GradientColor(0X00FFFFFF, 0XFFFFFFFF, 0XFFFFFFFF));

        RLParticle rlParticle = new RLParticle(level());
        rlParticle.config.setDuration(240);
        rlParticle.config.setStartLifetime(NumberFunction.constant(120));
        rlParticle.config.setStartSize(new NumberFunction3(8));
        rlParticle.config.setStartColor(new Gradient(new GradientColor(0X5FB2F5FF)));
        rlParticle.config.getEmission().setEmissionRate(NumberFunction.constant(0.05));
        rlParticle.config.getShape().setShape(new Dot());
        rlParticle.config.getMaterial().setMaterial(TBSMaterialHandle.ROS_RING.create());
        rlParticle.config.getRenderer().setRenderMode(RendererSetting.Particle.Mode.Horizontal);
        rlParticle.config.getLights().open();
        rlParticle.config.getColorOverLifetime().open();
        rlParticle.config.getColorOverLifetime().setColor(toAlpha);
        rlParticle.config.getSizeOverLifetime().open();
        rlParticle.config.getSizeOverLifetime().setSize(new NumberFunction3(new Line(new float[]{0,1},new float[]{1,0})));
        rlParticle.config.getRotationOverLifetime().open();
        rlParticle.config.getRotationOverLifetime().setYaw(new RandomLine(new float[]{0,1},new float[]{0,60},new float[]{0,20}));

        RLParticle rlParticle1 = new RLParticle(level());
        rlParticle1.config.setDuration(240);
        rlParticle1.config.setStartLifetime(NumberFunction.constant(20));
        rlParticle1.config.setStartSize(new NumberFunction3(9));
        rlParticle1.config.setStartColor(new Gradient(new GradientColor(0XBFB2F5FF)));
        rlParticle1.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        EmissionSetting.Burst burst1 = new EmissionSetting.Burst();
        burst1.setCount(NumberFunction.constant(1));burst1.cycles = 0;burst1.interval = 30;
        rlParticle1.config.getEmission().addBursts(burst1);
        rlParticle1.config.getShape().setShape(new Dot());
        rlParticle1.config.getMaterial().setMaterial(MaterialHandle.RING.create());
        rlParticle1.config.getRenderer().setRenderMode(RendererSetting.Particle.Mode.Horizontal);
        rlParticle1.config.getLights().open();
        rlParticle1.config.getColorOverLifetime().open();
        rlParticle1.config.getColorOverLifetime().setColor(toAlpha);
        rlParticle1.config.getSizeOverLifetime().open();
        rlParticle1.config.getSizeOverLifetime().setSize(new NumberFunction3(new Line(new float[]{0,1},new float[]{1,0})));
        rlParticle1.config.getRotationOverLifetime().open();
        rlParticle1.config.getRotationOverLifetime().setYaw(new RandomLine(new float[]{0,1},new float[]{0,60},new float[]{0,20}));

        RLParticle rlParticle2 = new RLParticle(level());
        rlParticle2.config.setDuration(240);
        rlParticle2.config.setStartLifetime(NumberFunction.constant(100));
        rlParticle2.config.setStartSpeed(NumberFunction.constant(-1));
        rlParticle2.config.setStartColor(new Gradient(new GradientColor(0XFFB2F5FF)));
        rlParticle2.config.getEmission().setEmissionRate(NumberFunction.constant(0.2));
        Circle circle2 = new Circle();circle2.setRadius(7.5f);circle2.setRadiusThickness(0.1f);
        rlParticle2.config.getShape().setShape(circle2);
        rlParticle2.config.getShape().setPosition(new NumberFunction3(0,0.5,0));
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

        BlockEffect blockEffect = new BlockEffect(level(), pos);
        rlParticle.emmit(blockEffect);
        rlParticle1.emmit(blockEffect);
        rlParticle2.emmit(blockEffect);
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

    public void ros_state_2_idle(){
        RLParticle rlParticle = new RLParticle(level());
        rlParticle.config.setDuration(20);
        rlParticle.config.setStartLifetime(new RandomConstant(10,35,true));
        rlParticle.config.setStartSize(new NumberFunction3(0.09));
        rlParticle.config.setStartColor(new Gradient(new GradientColor(0XAFB2F5FF)));
        rlParticle.config.getEmission().setEmissionRate(NumberFunction.constant(0.3));
        Circle circle = new Circle();circle.setRadius(2);circle.setRadiusThickness(0.8f);
        rlParticle.config.getShape().setShape(circle);
        rlParticle.config.getMaterial().setMaterial(TBSMaterialHandle.NO_GLOW_PIXEL.create());
        rlParticle.config.getRenderer().setRenderMode(RendererSetting.Particle.Mode.VerticalBillboard);
        rlParticle.config.getRenderer().setBloomEffect(true);
        rlParticle.config.getLights().open();
        rlParticle.config.getColorOverLifetime().open();
        rlParticle.config.getColorOverLifetime().setColor(new Gradient(GradientHandle.CENTER_OPAQUE));
        rlParticle.config.getSizeOverLifetime().open();
        rlParticle.config.getSizeOverLifetime().setSize(new NumberFunction3(NumberFunction.constant(0),new RandomLine(new float[]{0,1},new float[]{0,7},new float[]{0,4}),NumberFunction.constant(0)));
        rlParticle.config.getVelocityOverLifetime().open();
        rlParticle.config.getVelocityOverLifetime().setLinear(new NumberFunction3(NumberFunction.constant(0),new RandomConstant(1,3,true),NumberFunction.constant(0)));

        EntityEffect blockEffect = new EntityEffect(level(), this);
        rlParticle.emmit(blockEffect);
    }

    public void ros_state_2_idle_pos(Vec3 pos){
        RLParticle rlParticle = new RLParticle(level());
        rlParticle.config.setDuration(100);
        rlParticle.config.setStartLifetime(new RandomConstant(10,35,true));
        rlParticle.config.setStartSize(new NumberFunction3(0.09));
        rlParticle.config.setStartColor(new Gradient(new GradientColor(0XAFB2F5FF)));
        rlParticle.config.getEmission().setEmissionRate(NumberFunction.constant(0.3));
        Circle circle = new Circle();circle.setRadius(2);circle.setRadiusThickness(0.8f);
        rlParticle.config.getShape().setShape(circle);
        rlParticle.config.getMaterial().setMaterial(TBSMaterialHandle.NO_GLOW_PIXEL.create());
        rlParticle.config.getRenderer().setRenderMode(RendererSetting.Particle.Mode.VerticalBillboard);
        rlParticle.config.getRenderer().setBloomEffect(true);
        rlParticle.config.getLights().open();
        rlParticle.config.getColorOverLifetime().open();
        rlParticle.config.getColorOverLifetime().setColor(new Gradient(GradientHandle.CENTER_OPAQUE));
        rlParticle.config.getSizeOverLifetime().open();
        rlParticle.config.getSizeOverLifetime().setSize(new NumberFunction3(NumberFunction.constant(0),new RandomLine(new float[]{0,1},new float[]{0,7},new float[]{0,4}),NumberFunction.constant(0)));
        rlParticle.config.getVelocityOverLifetime().open();
        rlParticle.config.getVelocityOverLifetime().setLinear(new NumberFunction3(NumberFunction.constant(0),new RandomConstant(1,3,true),NumberFunction.constant(0)));

        BlockEffect blockEffect = new BlockEffect(level(), pos);
        rlParticle.emmit(blockEffect);
    }

    private void state1to2fx(){
        if(getAnimation()==STATE_1_TO_2){
            int tick = getAnimationTick();
            if(level().isClientSide){
                if(tick==1){
                    ros_state_1to2(position().add(0,0.2,0));
                }else if(tick==270){
                    ros_state_1to2_boom(position().add(0,0.2,0));
                    ros_gravatationFX(90,position().add(0,0.2,0),new Vector3f(4));
                }
                if(tick>=1&&tick<=240&&tick%80==0){
                    ros_state_1to2_boom(position().add(0,0.2,0));
                }
            }
        }
    }

    public class GravitationalTrap implements ITagSerializable<CompoundTag> {
        public Vec3 position;
        public int maxTime;
        public int count;
        public boolean isTriggered;

        public GravitationalTrap(Vec3 position, int maxTime) {
            this.position = position;
            this.maxTime = maxTime;
        }

        public GravitationalTrap() {
        }

        private void tick (){
            if (level().isClientSide) {
                if(count%50==0){
                    for(int i=0;i<=6;i++){
                        BlockEffect blockEffect = new BlockEffect(level(), position.add(0, 0.2+random.nextFloat()*0.4f, 0));
                        RLParticle rlParticle = new RLParticle(level());
                        rlParticle.config.setDuration(100);
                        rlParticle.config.setStartLifetime(NumberFunction.constant(50));
                        rlParticle.config.setStartSize(new NumberFunction3(0.1));
                        rlParticle.config.setStartColor(new Gradient(new GradientColor(0X5FB2F5FF)));
                        rlParticle.config.getEmission().setEmissionRate(NumberFunction.constant(0));
                        EmissionSetting.Burst burst = new EmissionSetting.Burst();
                        burst.setCount(NumberFunction.constant(1));
                        rlParticle.config.getEmission().addBursts(burst);
                        rlParticle.config.setStartSpeed(NumberFunction.constant(0));
                        Circle circle = new Circle();circle.setArc(0);
                        circle.setRadius(2);
                        circle.setRadiusThickness(0);
                        rlParticle.config.getShape().setShape(circle);
                        rlParticle.config.getMaterial().setMaterial(MaterialHandle.VOID.create());
                        rlParticle.config.getLights().open();
                        rlParticle.config.getVelocityOverLifetime().open();
                        rlParticle.config.getVelocityOverLifetime().setOrbitalMode(VelocityOverLifetimeSetting.OrbitalMode.AngularVelocity);
                        rlParticle.config.getVelocityOverLifetime().setOrbital(new NumberFunction3(0, 0.5, 0));
                        rlParticle.config.trails.open();
                        rlParticle.config.trails.setLifetime(NumberFunction.constant(0.55));
                        rlParticle.config.trails.config.getMaterial().setMaterial(TBSMaterialHandle.NO_GLOW_PIXEL.create());
                        rlParticle.updateRotation(new Vector3f(0,3.14f*i/3,0));
                        rlParticle.emmit(blockEffect);
                    }
                }
                if(tickCount%20==0){
                    RLParticle rlParticle1 = new RLParticle(level());
                    rlParticle1.config.setDuration(100);
                    rlParticle1.config.setStartLifetime(NumberFunction.constant(20));
                    rlParticle1.config.setStartSize(new NumberFunction3(4));
                    rlParticle1.config.setStartColor(new Gradient(new GradientColor(0X6FB2F5FF)));
                    rlParticle1.config.getEmission().setEmissionRate(NumberFunction.constant(0));
                    EmissionSetting.Burst burst1 = new EmissionSetting.Burst(); burst1.setCount(NumberFunction.constant(1));
                    rlParticle1.config.getEmission().addBursts(burst1);
                    rlParticle1.config.setStartSpeed(NumberFunction.constant(0));
                    rlParticle1.config.getShape().setShape(new Dot());
                    rlParticle1.config.getMaterial().setMaterial(MaterialHandle.CIRCLE.create());
                    rlParticle1.config.getRenderer().setRenderMode(RendererSetting.Particle.Mode.Horizontal);
                    rlParticle1.config.getLights().open();
                    rlParticle1.config.getColorOverLifetime().open();
                    rlParticle1.config.getColorOverLifetime().setColor(new Gradient(GradientHandle.CENTER_OPAQUE));

                    BlockEffect blockEffect = new BlockEffect(level(), position.add(0, 0.2, 0));
                    rlParticle1.emmit(blockEffect);
                }
            }
            if(count%10==0){
                if(!level().isClientSide) {
                    AABB move = getBoundingBox().move(position.subtract(position()));
                    AABB inflate = move.inflate(1);
                    List<LivingEntity> entitiesOfClass = level().getEntitiesOfClass(LivingEntity.class, inflate);
                    for (LivingEntity living : entitiesOfClass) {
                        if (living == Rosmontis.this || living == livingInstallation) continue;
                        if(living instanceof Player player&&player.isCreative()) continue;
                        living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,100,2));
                        isTriggered = true;
                    }
                }
            }
            count++;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putFloat("posx", (float) position.x);
            compoundTag.putFloat("posy", (float) position.y);
            compoundTag.putFloat("posz", (float) position.z);

            compoundTag.putInt("maxTime",maxTime);
            compoundTag.putInt("count",count);

            return compoundTag;
        }

        @Override
        public void deserializeNBT(CompoundTag compoundTag) {
            position = new Vec3(compoundTag.getFloat("posx"),compoundTag.getFloat("posy"),compoundTag.getFloat("posz"));
            maxTime = compoundTag.getInt("maxTime");
            count = compoundTag.getInt("count");
        }
    }

    public class SummonArmorRegion implements ITagSerializable<CompoundTag> {
        public Vec3 position;
        public int maxTime;

        public SummonArmorRegion(Vec3 position, int maxTime) {
            this.position = position;
            this.maxTime = maxTime;
        }

        public SummonArmorRegion() {
        }

        private void tick (){
            if(level().isClientSide){
                int armorTick = tickCount%25;
                if(armorTick<=9&&armorTick%3==0){
                    float dist = 5 - armorTick/3f;
                    float height = (float) Math.sqrt(25 - dist*dist);
                    int Max = (int)(dist*2*3.14);

                    for (int j = 0; j < Max+1; j++) {
                        Vec3 offset = new Vec3(0, height,  dist).yRot(6.28f * j / Max);
                        float y = (float) (Mth.atan2(offset.x, offset.z));
                        float p = (float) (Mth.atan2(offset.y, offset.horizontalDistance()));
                        ParticleComponent.KeyTrack keyTrack2 = new ParticleComponent.KeyTrack(new float[]{0, 1, 0}, new float[]{0, 0.5f, 1});
                        AdvancedParticleBase.spawnParticle(Rosmontis.this.level(), ParticleHandler.ARMOR.get(), position.x + offset.x, position.y+offset.y, position.z + offset.z, 0, 0, 0, false, y, -p, 0, 0, 4F, 0.6, 0.8, 1, 0.2, 1, 10, true, false, new ParticleComponent[]{
                                new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, keyTrack2, false)
                        });
                    }
                }
            }else {
            }
            maxTime--;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putFloat("posx", (float) position.x);
            compoundTag.putFloat("posy", (float) position.y);
            compoundTag.putFloat("posz", (float) position.z);

            compoundTag.putInt("maxTime",maxTime);

            return compoundTag;
        }

        @Override
        public void deserializeNBT(CompoundTag compoundTag) {
            position = new Vec3(compoundTag.getFloat("posx"),compoundTag.getFloat("posy"),compoundTag.getFloat("posz"));
            maxTime = compoundTag.getInt("maxTime");
        }
    }

    public class SpawnStoneTimer implements ITagSerializable<CompoundTag> {
        public Vec3 position;
        public int maxTime;
        public int timeToShoot;
        public int scale;

        public SpawnStoneTimer(Vec3 position, int maxTime,int scale,int timeToShoot) {
            this.position = position;
            this.maxTime = maxTime;
            this.scale = scale;
            this.timeToShoot = timeToShoot;
        }

        public SpawnStoneTimer() {
        }

        private void tick (){
            maxTime--;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putFloat("posx", (float) position.x);
            compoundTag.putFloat("posy", (float) position.y);
            compoundTag.putFloat("posz", (float) position.z);

            compoundTag.putInt("maxTime",maxTime);
            compoundTag.putInt("scale",scale);
            compoundTag.putInt("timeToShoot",timeToShoot);

            return compoundTag;
        }

        @Override
        public void deserializeNBT(CompoundTag compoundTag) {
            position = new Vec3(compoundTag.getFloat("posx"),compoundTag.getFloat("posy"),compoundTag.getFloat("posz"));
            maxTime = compoundTag.getInt("maxTime");
            scale = compoundTag.getInt("scale");
            timeToShoot = compoundTag.getInt("timeToShoot");
        }
    }

    @Override
    public void playDeathAnimationPre(DamageSource source) {
        if(challengePlayer !=null){
            setAggressive(false);
            setHealth(1);
            setTarget(null);
            PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(challengePlayer, CapabilityHandle.PLAYER_CAPABILITY);
            if(capability!=null){
                PlayerStoryStoneData playerStory = capability.getPlayerStory();
                playerStory.setWinRosmontis(true);
                if(challengePlayer instanceof ServerPlayer player){
                    TriggerHandler.STRING_ADVANCEMENT_TRIGGER.trigger(player, "rhodes_3_rosmontis");
                }
                playerStory.setCanSummonRosmontis(true);
            }
            challengePlayer = null;
            setLastHurtByMob(null);
            AnimationActHandler.INSTANCE.sendAnimationMessage(this,BREAK);
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
        BlockPos spawnPos = getSpawnPos();
        return spawnPos!=null&&!isAggressive()&&getDialogueEntity()==null;
    }

    @Override
    public void startDialogue(Player player) {
        BlockPos tile = getSpawnPos();
        PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(player, CapabilityHandle.PLAYER_CAPABILITY);
        if(tile!=null&&capability!=null){
            PlayerStoryStoneData playerStory = capability.getPlayerStory();
            if(playerStory.isWinRosmontis()){
                DialogueEntity dialogueEntity1 = startTalk("dialogue/rhodesisland/rosmontis_win.json", this, player);
                DialogueEntry dialogueEntry = dialogueEntity1.getAllDialogue().getDialogueEntry("main2");
                dialogueEntry.setRunnable(()->{
                    if(player instanceof ServerPlayer serverPlayer){
                        TriggerHandler.STRING_ADVANCEMENT_TRIGGER.trigger(serverPlayer, "rhodes_4_trust");
                    }
                    Inventory inventory = player.getInventory();
                    inventory.add(new ItemStack(ItemHandle.ROSMONTIS_EMBRACE.get()));
                    inventory.add(new ItemStack(ItemHandle.ROSMONTIS_IPAD.get()));
                    inventory.add(new ItemStack(ItemHandle.PHANTOM_GRASP.get()));
                    playerStory.setWinRosmontis(false);
                });
            }
            else if(playerStory.isSeenRosmontis()&&isOnStoryGround){
                DialogueEntity dialogueEntity1 = startTalk("dialogue/rhodesisland/rosmontis_fight.json", this, player);
                DialogueEntry dialogueEntry = dialogueEntity1.getAllDialogue().getDialogueEntry("main2");
                dialogueEntry.setRunnable(()->{
                    startChallengePlayer(player);
                });
            }
            else {
                playerStory.setSeenRosmontis(true);
                DialogueEntity dialogueEntity1 = startTalk("dialogue/rhodesisland/find_rosmontis.json", this, player);
                DialogueEntry dialogueEntry = dialogueEntity1.getAllDialogue().getDialogueEntry("main3");
                dialogueEntry.setRunnable(()->{
                    AnimationActHandler.INSTANCE.sendAnimationMessage(this,STORY_MOVE);
                    if(player instanceof ServerPlayer serverPlayer){
                        TriggerHandler.STRING_ADVANCEMENT_TRIGGER.trigger(serverPlayer, "rhodes_2_c");
                    }
                });
            }
            //else {
            //    DialogueEntity dialogueEntity1 = startTalk("dialogue/rhodesisland/nofind_rosmontis.json", this, player);
            //    DialogueEntry dialogueEntry = dialogueEntity1.getAllDialogue().getDialogueEntry("main2");
            //    dialogueEntry.setRunnable(()->{
            //        playerStory.setSeenRosmontis(true);
            //    });
            //}
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
}
