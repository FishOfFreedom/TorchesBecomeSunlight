package com.freefish.torchesbecomesunlight.server.entity.ursus;

import com.bobmowzie.mowziesmobs.client.particle.ParticleCloud;
import com.bobmowzie.mowziesmobs.client.particle.ParticleHandler;
import com.bobmowzie.mowziesmobs.client.particle.util.AdvancedParticleBase;
import com.bobmowzie.mowziesmobs.client.particle.util.ParticleComponent;
import com.freefish.torchesbecomesunlight.client.particle.BlackSpearParticle;
import com.freefish.torchesbecomesunlight.client.particle.DemonHoleParticle;
import com.freefish.torchesbecomesunlight.client.particle.DemonParticle;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationAct;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import com.freefish.torchesbecomesunlight.server.entity.ai.FFBodyRotationControl;
import com.freefish.torchesbecomesunlight.server.entity.ai.FFPathNavigateGround;
import com.freefish.torchesbecomesunlight.server.entity.ai.entity.PursuerAttackAI;
import com.freefish.torchesbecomesunlight.server.entity.effect.BlackHoleEntity;
import com.freefish.torchesbecomesunlight.server.entity.effect.PursuerEffectEntity;
import com.freefish.torchesbecomesunlight.server.entity.effect.StompEntity;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.GuerrillasEntity;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.shield.Patriot;
import com.freefish.torchesbecomesunlight.server.entity.help.EntityCameraShake;
import com.freefish.torchesbecomesunlight.server.entity.projectile.BlackSpear;
import com.freefish.torchesbecomesunlight.server.entity.projectile.IceBlade;
import com.freefish.torchesbecomesunlight.server.init.SoundHandle;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import com.freefish.torchesbecomesunlight.server.world.gen.biome.ModBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class Pursuer extends UrsusEntity{
    public static final AnimationAct<Pursuer> BATTACK1 = new AnimationAct<Pursuer>("attackB1",17){

        @Override
        public void tickUpdate(Pursuer entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            LivingEntity target = entity.getTarget();
            if(target!=null&&tick<=10) {
                entity.lookAtEntity(target);
            }else {
                entity.setYRot(entity.yRotO);
            }

            if(tick == 10) {
                entity.playSound(SoundHandle.AXE_SWEPT.get(), 1.0F, 1.0F / (entity.random.nextFloat() * 0.4F + 0.8F));
                entity.dashForward(0.9f,0);
            }
            else if (tick == 12) {
                entity.doRangeAttack(4.5,140,damage,false);
            }
        }

        @Override
        public void stop(Pursuer entity) {
            AnimationActHandler.INSTANCE.sendAnimationMessage(entity,Pursuer.BATTACK2);
        }
    };
    public static final AnimationAct<Pursuer> BATTACK2 = new AnimationAct<Pursuer>("attackB2",20){

        @Override
        public void tickUpdate(Pursuer entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            LivingEntity target = entity.getTarget();
            if(target!=null&&tick<=10) {
                entity.lookAtEntity(target);
            }else {
                entity.setYRot(entity.yRotO);
            }

            if(tick == 8) {
                entity.playSound(SoundHandle.AXE_SWEPT.get(), 1.0F, 1.0F / (entity.random.nextFloat() * 0.4F + 0.8F));
                entity.dashForward(0.3f,0);
            }
            else if (tick == 10) {
                entity.doRangeAttack(4,140,damage,false);
            }
            else if (tick == 13) {
                entity.dashForward(0.7f,0);
            }
            else if (tick == 15) {
                entity.doRangeAttack(4,140,damage,true);
            }
        }
        @Override
        public void stop(Pursuer entity) {
            AnimationActHandler.INSTANCE.sendAnimationMessage(entity,Pursuer.BATTACK3);
        }
    };
    public static final AnimationAct<Pursuer> BATTACK3 = new AnimationAct<Pursuer>("attackB3",32){
        @Override
        public void tickUpdate(Pursuer entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            LivingEntity target = entity.getTarget();
            if(target!=null&&tick<8) {
                entity.lookAtEntity(target);
            }else {
                entity.setYRot(entity.yRotO);
            }

            if (tick == 6) {
                entity.dashForward(1.2f,0);
            }
            else if (tick == 18) {
                entity.doRangeAttack(5.5,140,damage*1.5f,true);
                StompEntity stompEntity = new StompEntity(entity.level(),8,entity,3);
                stompEntity.setPos(entity.position().add(new Vec3(0, 0, 4.5).yRot((float) (-entity.getYRot() / 180 * Math.PI))));
                entity.level().addFreshEntity(stompEntity);
                entity.playSound(SoundHandle.GROUND.get(), 1.3F, 1.0F / (entity.random.nextFloat() * 0.4F + 0.8F));
            }
        }
    };
    public static final AnimationAct<Pursuer> BATTACK21 = new AnimationAct<Pursuer>("attackB21",31){

        @Override
        public void tickUpdate(Pursuer entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            LivingEntity target = entity.getTarget();
            if(target!=null&&tick<15) {
                entity.lookAtEntity(target);
            }else {
                entity.setYRot(entity.yRotO);
            }

            if(tick<11){
                entity.locateEntity();
            }
            if (tick == 12) {
                Vec3 direction = new Vec3(0, 0.1, 0.8).yRot((float) ((-entity.getYRot()) / 180 * org.joml.Math.PI));
                entity.setDeltaMovement(direction);
            }
            else if (tick == 19||tick==15) {
                entity.doRangeAttack(4,140,damage,false);
            }
        }
    };
    public static final AnimationAct<Pursuer> BATTACK31 = new AnimationAct<Pursuer>("attackB31",53){

        @Override
        public void tickUpdate(Pursuer entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            LivingEntity target = entity.getTarget();
            if(target!=null&&tick<38) {
                entity.lookAtEntity(target);
            }else {
                entity.setYRot(entity.yRotO);
            }

            if(tick<15||(tick<38&&tick>30)){
                entity.locateEntity();
            }
            if (tick == 16||tick==38) {
                Vec3 direction = new Vec3(0, 0.1, 0.7).yRot((float) ((-entity.getYRot()) / 180 * org.joml.Math.PI));
                entity.setDeltaMovement(direction);
            }
            else if (tick == 20||tick==42) {
                entity.doRangeAttack(5.0,140,damage*1.5f,true);
            }
        }
    };
    public static final AnimationAct<Pursuer> REMOTE_1 = new  AnimationAct<Pursuer>("remote_1",31){
        @Override
        public void tickUpdate(Pursuer entity) {
            int tick = entity.getAnimationTick();
            RandomSource random = entity.getRandom();
            entity.locateEntity();
            LivingEntity target = entity.getTarget();
            if(target!=null) {
                entity.getLookControl().setLookAt(target);
                if (tick == 16) {
                    Vec3 vec3 = new Vec3(0,0,7+random.nextFloat()*3).xRot(1+2*random.nextFloat()).yRot(random.nextFloat()*6).add(target.position());
                    entity.shootBlackSpear(target,vec3,1);
                }
            }
        }
    };
    public static final AnimationAct<Pursuer> REMOTE_2 = new  AnimationAct<Pursuer>("remote_2",46){
        @Override
        public void tickUpdate(Pursuer entity) {
            int tick = entity.getAnimationTick();
            entity.locateEntity();
            entity.setYRot(entity.yRotO);
            if (tick == 30) {
                entity.bomb1(10);
            } else if (tick==18) {
                Vec3 move = new Vec3(0, 1.9, 1.6).yRot((float) (-entity.yBodyRot / 180 * org.joml.Math.PI)).add(entity.position());
                entity.setSVec(move);
            }
        }
    };
    public static final AnimationAct<Pursuer> REMOTE_3 = new  AnimationAct<Pursuer>("remote_3",51,2){
        @Override
        public void tickUpdate(Pursuer entity) {
            int tick = entity.getAnimationTick();
            LivingEntity target = entity.getTarget();
            entity.locateEntity();
            if(target!=null&&tick<20) {
                entity.lookAtEntity(target);
            }else {
                entity.setYRot(entity.yRotO);
            }

            if(target!=null) {
                if (tick == 42)
                    entity.shootIceBlade(target);
            }
        }
    };
    public static final AnimationAct<Pursuer> ATTACK_1 = new  AnimationAct<Pursuer>("attack_1",32){
        @Override
        public void tickUpdate(Pursuer entity) {
            int tick = entity.getAnimationTick();
            RandomSource random = entity.getRandom();
            entity.locateEntity();
            LivingEntity target = entity.getTarget();
            if(target!=null) {
                entity.getLookControl().setLookAt(target);
                Vec3 vec3 = entity.position().subtract(target.position()).scale(0.5).add(0,2+random.nextFloat(),0);

                if (tick == 7) {
                    entity.shootBlackSpear(target,vec3.add(target.position()),0);
                }else if (tick == 15) {
                    entity.shootBlackSpear(target,vec3.yRot(1.2f).add(target.position()),0);
                }else if (tick == 25) {
                    entity.shootBlackSpear(target,vec3.yRot(-1.2f).add(target.position()),0);
                }
            }
        }
    };
    public static final AnimationAct<Pursuer> ATTACK_2 = new  AnimationAct<Pursuer>("attack_2",24){
        @Override
        public void tickUpdate(Pursuer entity) {
            int tick = entity.getAnimationTick();
            RandomSource random = entity.getRandom();
            entity.locateEntity();
            LivingEntity target = entity.getTarget();
            if(target!=null) {
                entity.getLookControl().setLookAt(target);
                if (tick == 8) {
                    Vec3 vec3 = entity.position().subtract(target.position()).scale(0.5).add(0,2+random.nextFloat(),0);
                    entity.shootBlackSpear(target,vec3.add(target.position()),0);
                }else if (tick == 19) {
                    Vec3 vec3 = entity.position().subtract(target.position()).scale(-0.5).add(0,2+random.nextFloat(),0);
                    entity.shootBlackSpear(target,vec3.add(target.position()),0);
                }
            }
        }
    };
    public static final AnimationAct<Pursuer> JUMP = new  AnimationAct<Pursuer>("jump",39){
        @Override
        public void tickUpdate(Pursuer entity) {
            int tick = entity.getAnimationTick();
            RandomSource random = entity.getRandom();
            float damage =(float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
            LivingEntity target = entity.getTarget();
            if(target!=null) {
                entity.lookAtEntity(target);
            }else {
                entity.setYRot(entity.yRotO);
            }

            if (tick == 18) {
                float jumpLen;
                if(target!=null){
                    jumpLen = (float) target.position().subtract(entity.position()).length()/3.5f;
                }else {
                    jumpLen = 2;
                }
                Vec3 direction = new Vec3(0, 0.3, jumpLen).yRot((float) (-entity.getYRot() / 180 * org.joml.Math.PI));
                entity.setDeltaMovement(direction);
            }
            if(tick==22){
                entity.setDeltaMovement(entity.getDeltaMovement().scale(0.5));
            }
            if(tick==25){
                if(entity.getState()==1) {
                    entity.doRangeAttack(5.5,140,damage*1.5f,true);
                    AnimationActHandler.INSTANCE.sendAnimationMessage(entity, JUMPATTACK);
                }
                else {
                    int len = 3+entity.random.nextInt(2);
                    float i1 = 1;
                    if(target!=null) i1 = target.getBbWidth();
                    Vec3 targetPos =  new Vec3(0, 0, 2).yRot((float) (-entity.yBodyRot / 180 * org.joml.Math.PI)).add(entity.position());
                    for(int i=0;i<len;i++){
                        Vec3 blackPos = targetPos.add(new Vec3(0,0.4,i1*1.1).yRot(6.28f*i/len));
                        entity.shootBlackSpear(targetPos.add(0,2,0),blackPos,0);
                    }
                }
            }
        }
    };
    public static final AnimationAct<Pursuer> JUMPATTACK = new  AnimationAct<Pursuer>("jumpAttack1",66){
        @Override
        public void tickUpdate(Pursuer entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            LivingEntity target = entity.getTarget();
            if(target!=null&&((tick<15)||(tick>25&&tick<40))) {
                entity.lookAtEntity(target);
            }else {
                entity.setYRot(entity.yRotO);
            }

            if (tick == 13) {
                entity.dashForward(0.9f,0);
            }
            else if (tick == 15) {
                entity.doRangeAttack(5.5,140,damage*1.5f,true);
            }
            else if (tick == 40) {
                entity.dashForward(1.2f,0);
            }else if (tick == 42) {
                entity.doRangeAttack(5.5,140,damage*1.5f,true);
            }
        }
    };
    public static final AnimationAct<Pursuer> BACKJUMP = new  AnimationAct<Pursuer>("back_jump",27){
        @Override
        public void tickUpdate(Pursuer entity) {
            int tick = entity.getAnimationTick();
            RandomSource random = entity.getRandom();
            LivingEntity target = entity.getTarget();
            if(target!=null) {
                entity.lookAtEntity(target);
            }else {
                entity.setYRot(entity.yRotO);
            }

            if (tick == 8) {
                Vec3 direction = new Vec3(0, 0.3, -2-random.nextFloat()/2).yRot((float) (-entity.getYRot() / 180 * org.joml.Math.PI));
                entity.setDeltaMovement(direction);
            }
        }
    };
    public static final AnimationAct<Pursuer> BATTACKM1 = new  AnimationAct<Pursuer>("Battackm1",36){
        @Override
        public void tickUpdate(Pursuer entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            LivingEntity target = entity.getTarget();
            if(target!=null) {
                entity.lookAtEntity(target);
            }else {
                entity.setYRot(entity.yRotO);
            }

            if(tick<16){
                entity.locateEntity();
            }
            if (tick == 16) {
                float ran = entity.random.nextFloat();
                Vec3 direction = new Vec3(0, 0.15+ran*0.1, 1.2+ran).yRot((float) ((-45-entity.getYRot()) / 180 * org.joml.Math.PI));
                entity.setDeltaMovement(direction);
            }
            else if (tick >= 16&&tick<=22&&tick%2==0) {
                entity.doCycleAttack(3,damage);
            }
            else if(tick==25&&entity.random.nextFloat()<0.5){
                AnimationActHandler.INSTANCE.sendAnimationMessage(entity,PIERCE);
            }
        }
    };

    public static final AnimationAct<Pursuer> BATTACKM2 = new  AnimationAct<Pursuer>("tele2",34,2){
        @Override
        public void tickUpdate(Pursuer entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            LivingEntity target = entity.getTarget();
            Entity pee = entity.getPee();
            if(pee instanceof PursuerEffectEntity entity1){
                entity.lookAtEntity(entity1);
                entity.absFaceEntity(entity1);
            }
            if(tick<5){
                entity.locateEntity();
            }
            if(tick<=18&&target!=null&&(tick&2)==0){
                entity.locateEntity(target,4,180);
            }
            if (tick == 5) {
                Vec3 direction = new Vec3(0, 0.2, 2).yRot((float) ((-entity.getYRot()) / 180 * org.joml.Math.PI));
                entity.setDeltaMovement(direction);
            }
            else if (tick == 18) {
                entity.setLocateMobId(-1);
            }
            else if (tick == 19) {
                entity.doRangeAttack(5.2f,140,damage,3,true);
            }
        }

        @Override
        public void stop(Pursuer entity) {
            entity.setLocateMobId(-1);
            super.stop(entity);
        }
    };
    public static final AnimationAct<Pursuer> PIERCE = new  AnimationAct<Pursuer>("pierce",26){
        @Override
        public void tickUpdate(Pursuer entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            LivingEntity target = entity.getTarget();
            if(target!=null) {
                entity.lookAtEntity(target);
            }else {
                entity.setYRot(entity.yRotO);
            }

            if(tick<4){
                entity.locateEntity();
            }
            if (tick == 4||tick==14) {
                Vec3 direction = new Vec3(0, 0.2, 1.1).yRot((float) ((-entity.getYRot()) / 180 * org.joml.Math.PI));
                entity.setDeltaMovement(direction);
            }
            else if (tick >= 8&&tick<=16&&tick%2==0) {
                entity.doCycleAttack(3f,damage);
            }
        }
    };
    public static final AnimationAct<Pursuer> PEACETOACT = new  AnimationAct<Pursuer>("peacetoact2",114){
        @Override
        public void tickUpdate(Pursuer entity) {
            int tick = entity.getAnimationTick();
            entity.locateEntity();
            LivingEntity target = entity.getTarget();
            if(target!=null) {
                entity.lookAtEntity(target);
            }else {
                entity.setYRot(entity.yRotO);
            }
            if(tick == 1) {
                PursuerEffectEntity pee = new PursuerEffectEntity(entity.level(),1,entity);
                pee.setPos(entity.position());
                entity.level().addFreshEntity(pee);
                entity.pee = pee.getUUID();
                entity.setHealth(entity.getMaxHealth() / 4 * 3 - 1);
            }
            if(tick == 10)
                entity.setPredicate(1);
        }
    };
    public static final AnimationAct<Pursuer> SKILL = new  AnimationAct<Pursuer>("peacetoact",191){
        @Override
        public void tickUpdate(Pursuer entity) {
            int tick = entity.getAnimationTick();
            RandomSource random = entity.random;
            entity.locateEntity();
            LivingEntity target = entity.getTarget();
            if(target!=null) {
                entity.lookAtEntity(target);
            }else {
                entity.setYRot(entity.yRotO);
            }
            if(target!=null&&tick>=30&&tick<=166&&tick%2==0){
                Vec3 vec3 = new Vec3(0,0,7+random.nextFloat()*3).xRot(1+2*random.nextFloat()).yRot(random.nextFloat()*6).add(target.position());
                entity.shootBlackSpear(target,vec3,2);
            }
        }
    };
    public static final AnimationAct<Pursuer> BLACKHOLE = new AnimationAct<Pursuer>("blackhole",117,2){

        @Override
        public void tickUpdate(Pursuer entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            LivingEntity target = entity.getTarget();
            if(target!=null) {
                entity.lookAtEntity(target);
            }else {
                entity.setYRot(entity.yRotO);
            }

            if(tick<82) entity.locateEntity();

            if(tick==32){
                BlackHoleEntity blackHoleEntity = new BlackHoleEntity(entity.level(), entity);
                blackHoleEntity.setPos(new Vec3(0, 1.5, 9).yRot((float) (-entity.getYRot() / 180 * org.joml.Math.PI)).add(entity.position()));
                entity.level().addFreshEntity(blackHoleEntity);
            }
            else if(tick == 82||tick==83){
                float dist;
                if(target==null)
                    dist = 10;
                else
                    dist = target.distanceTo(entity);
                Vec3 hunt = new Vec3(0, 0.15, dist*0.15).yRot((float) (-entity.getYRot() / 180 * Math.PI));
                entity.setDeltaMovement(hunt);
            }
            else if(tick==96){
                entity.doRangeTrueAttack(4,140,damage*7f,true);
            }
            else if(tick==105){
                entity.doRangeTrueAttack(4,140,damage*9f,true);
            }
        }
    };
    public static final AnimationAct<Pursuer> FASTMOVE = new  AnimationAct<Pursuer>("fastmove",25){
        @Override
        public void tickUpdate(Pursuer entity) {
            int tick = entity.getAnimationTick();
            RandomSource random = entity.getRandom();
            if(tick==1){
                LivingEntity target = entity.getTarget();
                if(target!=null) {
                    float dist = Mth.clamp((float) target.position().subtract(entity.position()).horizontalDistance()+4,6,12);
                    Vec3 vec3 = new Vec3(target.getX()-entity.getX(),0,target.getZ()-entity.getZ()).normalize().scale(dist/9).add(0,random.nextBoolean()?0.7f:-0.7f,0);
                    vec3 = vec3.yRot((float) vec3.y);
                    entity.setSVec(vec3);
                }
            }
            if(tick>1){
                Vec3 move = entity.getSVec();
                entity.getLookControl().setLookAt(move);
            }
            if(tick<=11||tick>=21)
                entity.locateEntity();
            else {
                Vec3 move = entity.getSVec();
                if(move!=null) {
                    entity.setDeltaMovement(move.x,-entity.getAttributeValue(ForgeMod.ENTITY_GRAVITY.get()) * 5.0F,move.z);
                }
                if(tick%2==0){
                    entity.setSVec(move.yRot(move.y>0?-0.35f:0.35f));
                }
            }
        }

        @Override
        public void stop(Pursuer entity) {
            entity.locateEntity();
            super.stop(entity);
        }
    };
    public static final AnimationAct<Pursuer> TELE = new  AnimationAct<Pursuer>("tele",27,2){
        @Override
        public void tickUpdate(Pursuer entity) {
            int tick = entity.getAnimationTick();
            RandomSource random = entity.getRandom();
            LivingEntity target = entity.getTarget();
            entity.setYRot(entity.yRotO);
            if(tick==1) {
                if(target==null) {
                    double d0 = entity.getX() + (random.nextDouble() - 0.5D) * 24.0D;
                    double d1 = entity.getY() + (-6);
                    double d2 = entity.getZ() + (random.nextDouble() - 0.5D) * 24.0D;
                    BlockPos firstBlockAbove = MathUtils.getFirstBlockAbove(entity.level(), new BlockPos((int) d0, (int) d1, (int) d2));
                    Vec3 vec3 = new Vec3(d0, firstBlockAbove != null ? firstBlockAbove.getY() : d1 + 7, d2);
                    entity.setSVec(vec3);
                }
                else {
                    Entity pee = entity.getPee();
                    if(pee instanceof PursuerEffectEntity entity1){
                        float dist = entity1.distanceTo(target);
                        Vec3 vec3 = new Vec3(target.getX()-entity1.getX(),0,target.getZ()-entity1.getZ()).normalize().scale(dist+9).add(entity1.position());
                        entity.setSVec(vec3);
                    }
                    else {
                        Vec3 vec3 = new Vec3(target.getX() - entity.getX(), 0, target.getZ() - entity.getZ()).normalize().scale(random.nextFloat() * 3 + 8);
                        vec3 = vec3.yRot(2 - random.nextFloat() * 4).add(target.getX(), target.getY() - 6, target.getZ());
                        BlockPos firstBlockAbove = MathUtils.getFirstBlockAbove(entity.level(), new BlockPos((int) vec3.x, (int) vec3.y, (int) vec3.z));
                        vec3 = new Vec3(vec3.x, firstBlockAbove != null ? firstBlockAbove.getY()-1 : vec3.y + 6, vec3.z);
                        entity.setSVec(vec3);
                    }
                }
            }
            if(tick==1){
                entity.setDeltaMovement(0,entity.getDeltaMovement().y,0);
            }
            else if(tick<=15)
                entity.move(MoverType.SELF, new Vec3(0, 0,-0.1).yRot((float) (-entity.getYRot() / 180 * Math.PI)));
            else {
                entity.locateEntity();
            }
            if(tick==15&&target!=null){
                if (entity.canSinceRemote3) {
                    entity.canSinceRemote3 = false;
                    AnimationActHandler.INSTANCE.sendAnimationMessage(entity, Pursuer.BATTACKM2);
                }
                else {
                    AnimationActHandler.INSTANCE.sendAnimationMessage(entity, Pursuer.REMOTE_3);
                }
            }
        }
    };
    public static final AnimationAct<Pursuer> TELE1 = new  AnimationAct<Pursuer>("tele1",51,2){
        @Override
        public void tickUpdate(Pursuer entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            LivingEntity target = entity.getTarget();
            entity.setYRot(entity.yRotO);
            if(tick==1) {
                if(target==null) {
                    stop(entity);
                }
                else {
                    Vec3 vec3 = new Vec3(target.getX() - entity.getX(), 0, target.getZ() - entity.getZ()).normalize();
                    vec3 = vec3.add(target.getX(), target.getY() +target.getBbHeight()+ 2, target.getZ());
                    entity.setSVec(vec3);
                }
            }
            if(tick<=15)
                entity.move(MoverType.SELF, new Vec3(0, 0,-0.1).yRot((float) (-entity.getYRot() / 180 * Math.PI)));
            else {
                entity.locateEntity();
            }
            if(tick==30){
                StompEntity stompEntity = new StompEntity(entity.level(),8,entity,3);
                stompEntity.setPos(entity.position().add(new Vec3(0, 0, 0).yRot((float) (-entity.getYRot() / 180 * Math.PI))));
                entity.level().addFreshEntity(stompEntity);
                entity.playSound(SoundHandle.GROUND.get(), 1.3F, 1.0F / (entity.random.nextFloat() * 0.4F + 0.8F));
            }
        }
    };

    private static final EntityDataAccessor<Integer> PREDICATE = SynchedEntityData.defineId(Pursuer.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> SX = SynchedEntityData.defineId(Pursuer.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> SY = SynchedEntityData.defineId(Pursuer.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> SZ = SynchedEntityData.defineId(Pursuer.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> LOCATE_MOB_ID = SynchedEntityData.defineId(Patriot.class, EntityDataSerializers.INT);

    private UUID pee;
    public boolean canSinceRemote3;

    @OnlyIn(Dist.CLIENT)
    public Vec3 DemonCentre;

    @Override
    public AnimationAct[] getAnimations() {
        return new AnimationAct[]{NO_ANIMATION,SKILL,BATTACKM1,TELE,REMOTE_3,BLACKHOLE,JUMP,FASTMOVE,PEACETOACT,JUMPATTACK,PIERCE
                ,REMOTE_1,REMOTE_2,ATTACK_1,ATTACK_2,BATTACK21,BATTACK31,BACKJUMP,TELE1,BATTACKM2,BATTACK1,BATTACK2,BATTACK3};
    }

    public Pursuer(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new PursuerAttackAI(this));

        //this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        //this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        //this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this , 0.3));

        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Zombie.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Ravager.class, true));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        if(getAnimation()==FASTMOVE) return false;
        return super.hurt(source, damage);
    }

    @Override
    public void tick() {
        super.tick();
        int tick = getAnimationTick();

        Entity locateEntity = getLocateMob();
        if(locateEntity instanceof LivingEntity){
            Vec3 move = getLocateVec().subtract(locateEntity.position());
            locateEntity.setDeltaMovement(move);
        }

        if(!level().isClientSide()) {
            if (getHealth() <= getMaxHealth() / 4*3 && getState() == 0&&getAnimation()!=PEACETOACT) setState(1);
            else if (getHealth() > getMaxHealth() / 4*3 && getState() == 1) setState(0);
        }
        if((getAnimation()==TELE||getAnimation()==TELE1)&&(tick==12||tick==13||tick==14)){
            Vec3 telePos = getSVec();
            if(!(!level().isClientSide&&(tick==12||tick==14)))
                absMoveTo(telePos.x,telePos.y,telePos.z);
        }
        //clientLook();
        addDemonWarn();
        addToState2FX();
        remoteFX();
        bombFX();
        teleFX();
        skillFX();
        blackHoleFX();
        fastMoveFX();
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
    public float getStepHeight() {
        return 1.5f;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PREDICATE,0);
        this.entityData.define(SX,0f);
        this.entityData.define(SY,0f);
        this.entityData.define(SZ,0f);
        this.entityData.define(LOCATE_MOB_ID, -1);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.entityData.set(PREDICATE,pCompound.getInt("predicate"));
        if (pCompound.hasUUID("pee"))
            pee =  pCompound.getUUID("pee");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("predicate",getState());
        if(pee!=null)
            pCompound.putUUID("pee",pee);
    }

    @Nullable
    public Entity getPee() {
         if (this.pee != null && this.level() instanceof ServerLevel) {
            return ((ServerLevel)this.level()).getEntity(this.pee);
        } else {
            return null;
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return GuerrillasEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 400.0D)
                .add(Attributes.ATTACK_DAMAGE, 16.0f)
                .add(Attributes.ARMOR, 10.0D)
                .add(Attributes.FOLLOW_RANGE, 48)
                .add(Attributes.KNOCKBACK_RESISTANCE,1f);
    }

    @Override
    protected <T extends GeoEntity> void basicAnimation(AnimationState<T> event) {
        float dist = (float) position().subtract(xo,yo,zo).length();
        if (true) {
            if (dist>0.03)
                event.getController().setAnimation(RawAnimation.begin().thenLoop("walk_peace"));
            else
                event.getController().setAnimation(RawAnimation.begin().thenLoop("idle_peace"));
        }
    }

    public void setLocateMobId(int i) {
        this.entityData.set(LOCATE_MOB_ID, i);
    }


    public int getLocateMobId() {
        return this.entityData.get(LOCATE_MOB_ID);
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

    private void shootBlackSpear(LivingEntity target,Vec3 vec3,int type) {
        BlackSpear abstractarrow = new BlackSpear(level(),this,type);
        abstractarrow.setPos(vec3);
        double d0 = target.getX() - vec3.x;
        double d1 = target.getY(0.4D) - vec3.y;
        double d2 = target.getZ() - vec3.z;
        abstractarrow.shoot(d0, d1 , d2, 0.2F, (float)(9 - this.level().getDifficulty().getId() * 3));
        this.level().addFreshEntity(abstractarrow);
    }

    private void shootBlackSpear(Vec3 target,Vec3 vec3,int type) {
        BlackSpear abstractarrow = new BlackSpear(level(),this,type);
        abstractarrow.setPos(vec3);
        double d0 = target.x - vec3.x;
        double d1 = target.y - vec3.y;
        double d2 = target.z - vec3.z;
        abstractarrow.shoot(d0, d1 , d2, 0.2F, (float)(9 - this.level().getDifficulty().getId() * 3));
        this.level().addFreshEntity(abstractarrow);
    }

    private void bomb1(float r) {
        playSound(SoundHandle.AXE_SWEPT.get(), 1.0F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
        float damage = (float) getAttribute(Attributes.ATTACK_DAMAGE).getValue();
        List<LivingEntity> list = level().getEntitiesOfClass(LivingEntity.class,getBoundingBox().inflate(10), livingEntity ->
                !(livingEntity instanceof UrsusEntity)&&livingEntity.distanceTo(this)<r+livingEntity.getBbWidth()/2);
        for(LivingEntity entityHit:list) {
            entityHit.hurt(damageSources().mobAttack(this), damage);
            if (entityHit instanceof Player player) {
                ItemStack pPlayerItemStack = player.getUseItem();
                if (!pPlayerItemStack.isEmpty() && pPlayerItemStack.is(Items.SHIELD)) {
                    player.getCooldowns().addCooldown(Items.SHIELD, 100);
                    level().broadcastEntityEvent(player, (byte) 30);
                }
            }
        }
    }

    public void locateEntity(LivingEntity entityHit,double range, double arc){
        float entityHitAngle = (float) ((Math.atan2(entityHit.getZ() - getZ(), entityHit.getX() - getX()) * (180 / Math.PI) - 90) % 360);
        float entityAttackingAngle = getYRot() % 360;
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

    public Entity getLocateMob() {
        int id = getLocateMobId();
        return id == -1 ? null : level().getEntity(id);
    }

    private void remoteFX(){
        if(getAnimation()==REMOTE_1){
            int tick = getAnimationTick();
            if(level().isClientSide) {
                if (tick == 16) {
                    float yaw = (float) (-getYRot() / 180 * Math.PI);
                    Vec3 vec3 = position().add(new Vec3(2, 2 + random.nextFloat(), 1).yRot(yaw));

                    level().addParticle(new BlackSpearParticle.BlackSpearData(15, false, yaw - 1.55f, 0), vec3.x, vec3.y, vec3.z, 0, 0, 0);
                    level().addParticle(new DemonHoleParticle.DemonHoleData(30, 3, (float) yaw, 0), vec3.x, vec3.y, vec3.z, 0, 0, 0);
                }
            }
        }
    }

    private void bombFX(){
        if(getAnimation()==REMOTE_2){
            int tick = getAnimationTick();
            if(level().isClientSide) {
                Vec3 sVec = getSVec();
                if(tick==28) {
                    for (int i = 0; i < 6; i++) {
                        float ran = random.nextFloat()*2;
                        int len = (int) ((i+1)*6.28f);
                        for (int j = 0; j < len; j++) {
                            Vec3 move = new Vec3(0, 0.4, 0.5 + i/2f).yRot((float) 6.28 * j / len + ran);
                            Vec3 vec3 = position().add(move);
                            level().addParticle(new ParticleCloud.CloudData(ParticleHandler.CLOUD.get(), 0f, 0f, 0f, (float) (10d + random.nextDouble() * 10d), 40, ParticleCloud.EnumCloudBehavior.SHRINK, 1f), vec3.x, vec3.y, vec3.z, 0.1 /move.x, 0.1, 0.1 /move.z);
                        }
                    }
                }
                if(tick==20) {
                    ParticleComponent.KeyTrack keyTrack1 = ParticleComponent.KeyTrack.oscillate(0, 2, 24);
                    ParticleComponent.KeyTrack keyTrack2 = new ParticleComponent.KeyTrack(new float[]{0, 40, 40, 0}, new float[]{0, 0.2f, 0.8f, 1});
                    AdvancedParticleBase.spawnParticle(level(), ParticleHandler.ICEBOMB_1.get(), sVec.x, sVec.y, sVec.z, 0, 0, 0, true, 0, 0, 0, 0, 0F, 0.62, 0.18, 0.1, 1, 1, 12, true, false, new ParticleComponent[]{
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, keyTrack2, false),
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, keyTrack1, true)
                    });
                }
                if(tick==24) {
                    ParticleComponent.KeyTrack keyTrack1 = ParticleComponent.KeyTrack.oscillate(0, 2, 24);
                    ParticleComponent.KeyTrack keyTrack2 = new ParticleComponent.KeyTrack(new float[]{0, 40, 40, 0}, new float[]{0, 0.2f, 0.8f, 1});
                    AdvancedParticleBase.spawnParticle(level(), ParticleHandler.ICEBOMB_1.get(), sVec.x, sVec.y, sVec.z, 0, 0, 0, true, 0, 0, 0, 0, 0F, 0, 0, 0, 1, 1, 9, true, false, new ParticleComponent[]{
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, keyTrack2, false),
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, keyTrack1, true)
                    });
                }
            }
        }
    }

    private void skillFX(){
        if(getAnimation()==SKILL){
            int tick = getAnimationTick();
            if(level().isClientSide) {
                if(tick==13||tick==53||tick==33) {
                    for (int i = 0; i < 16; i++) {
                        int len = (int) ((i+1)*6.28f);
                        for (int j = 0; j < len; j++) {
                            Vec3 move = new Vec3(0, 0, 0.5 + i).yRot((float) 6.28 * j / len+random.nextFloat()*0.2f-0.1f);
                            Vec3 vec3 = position().add(move);
                            level().addParticle(new ParticleCloud.CloudData(ParticleHandler.CLOUD.get(), 0f, 0f, 0f, (float) (10d + random.nextDouble() * 10d), 220, ParticleCloud.EnumCloudBehavior.CONSTANT, 1f), vec3.x, vec3.y+0.2, vec3.z, 0, 0.01, 0);
                        }
                    }
                }
            }
        }
    }

    private void teleFX(){
        if(getAnimation()==TELE||getAnimation()==TELE1){
            int tick = getAnimationTick();
            if(level().isClientSide) {
                if(tick==5||tick==3||tick==1) {
                    Vec3 vec3 = new Vec3(0, 0, -0.5).yRot((float) (-getYRot() / 180 * org.joml.Math.PI)).add(position());
                    for(int i=0;i<=6;i++){
                        for(int j=0;j<=5;j++){
                            level().addParticle(new ParticleCloud.CloudData(ParticleHandler.CLOUD.get(), 0, 0, 0, (float) (10d + random.nextDouble() * 10d), 50, ParticleCloud.EnumCloudBehavior.SHRINK, 1f), vec3.x+1-2*random.nextFloat(), vec3.y+i/2f , vec3.z+1-2*random.nextFloat(), 0, 0, 0);
                        }
                    }
                }
                if(tick==7||tick==5||tick==3) {
                    Vec3 vec3 = getSVec();
                    for(int i=0;i<=6;i++){
                        for(int j=0;j<8;j++){
                            Vec3 offVec = new Vec3(0,0,1.2).yRot(6.28f*j/8+random.nextFloat()*0.3f);
                            level().addParticle(new ParticleCloud.CloudData(ParticleHandler.CLOUD.get(), 0, 0, 0, (float) (10d + random.nextDouble() * 10d), 90, ParticleCloud.EnumCloudBehavior.SHRINK, 1f), vec3.x+offVec.x, vec3.y+i/2f , vec3.z+offVec.z, 0, 0, 0);
                        }
                    }
                }
                if(tick==7&&getAnimation()==TELE) {
                    int len = 3 + random.nextInt(4);
                    Vec3 vec3 = getSVec();
                    for (int i1 = 0; i1 < len; i1++) {
                        Vec3 inPos = vec3.add(position()).scale(0.5).add(new Vec3(0, 0, 7 + random.nextFloat() * 6).yRot(6.28f * i1 / len + random.nextFloat() * 2 - 1));
                        for (int i = 0; i <= 6; i++) {
                            for (int j = 0; j <= 5; j++) {
                                level().addParticle(new ParticleCloud.CloudData(ParticleHandler.CLOUD.get(), 0, 0, 0, (float) (10d + random.nextDouble() * 10d), 90, ParticleCloud.EnumCloudBehavior.SHRINK, 1f), inPos.x + 1 - 2 * random.nextFloat(), inPos.y + i / 2f, inPos.z + 1 - 2 * random.nextFloat(), 0, 0, 0);
                            }
                        }
                    }
                }
            }
        }
    }

    private void fastMoveFX(){
        if(getAnimation()==FASTMOVE){
            int tick = getAnimationTick();
            if(level().isClientSide) {
                if(tick>11&&tick<24){
                    Vec3 offset = position().subtract(xo,yo,zo);
                    int len =(int) offset.length()+1;
                    for(int i = 0;i<len;i++){
                        Vec3 vec3 = position().lerp(new Vec3(xo,yo,zo),i/(float)len);
                        level().addParticle(new ParticleCloud.CloudData(ParticleHandler.CLOUD.get(), random.nextFloat()*0.5f, 0f, 0f, (float) (10d + random.nextDouble() * 10d)
                                , 40, ParticleCloud.EnumCloudBehavior.SHRINK, 1f), vec3.x, vec3.y + 0.4, vec3.z, 0,0,0);
                    }
                }
            }
        }
    }

    private void blackHoleFX(){
        if(getAnimation()==BLACKHOLE){
            int tick = getAnimationTick();
            if(level().isClientSide) {
                if(tick==18) {
                    Vec3 vec3 = new Vec3(0, 0, 9).yRot((float) (-getYRot() / 180 * org.joml.Math.PI));
                    ParticleComponent.KeyTrack keyTrack1 = ParticleComponent.KeyTrack.oscillate(0, 2, 24);
                    ParticleComponent.KeyTrack keyTrack2 = new ParticleComponent.KeyTrack(new float[]{0, 40, 40, 0}, new float[]{0, 0.2f, 0.8f, 1});
                    AdvancedParticleBase.spawnParticle(level(), ParticleHandler.ICEBOMB_1.get(), getX() + vec3.x, getY() + 1.5, getZ() + vec3.z, 0, 0, 0, true, 0, 0, 0, 0, 0F, 1, 1, 1, 1, 1, 12, true, false, new ParticleComponent[]{
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, keyTrack2, false),
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, keyTrack1, true)
                    });
                }
                if(tick==22) {
                    Vec3 vec3 = new Vec3(0, 0, 9).yRot((float) (-getYRot() / 180 * org.joml.Math.PI));
                    ParticleComponent.KeyTrack keyTrack1 = ParticleComponent.KeyTrack.oscillate(0, 2, 24);
                    ParticleComponent.KeyTrack keyTrack2 = new ParticleComponent.KeyTrack(new float[]{0, 40, 40, 0}, new float[]{0, 0.2f, 0.8f, 1});
                    AdvancedParticleBase.spawnParticle(level(), ParticleHandler.ICEBOMB_1.get(), getX() + vec3.x, getY() + 1.5, getZ() + vec3.z, 0, 0, 0, true, 0, 0, 0, 0, 0F, 0, 0, 0, 1, 1, 9, true, false, new ParticleComponent[]{
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, keyTrack2, false),
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, keyTrack1, true)
                    });
                }
            }
        }
    }

    public int getPredicate(){
        return this.entityData.get(PREDICATE);
    }

    public void setPredicate(int predicate){
        this.entityData.set(PREDICATE,predicate);
    }

    public void setSVec(Vec3 vec3){
        this.entityData.set(SX,(float)vec3.x);
        this.entityData.set(SY,(float)vec3.y);
        this.entityData.set(SZ,(float)vec3.z);
    }

    public Vec3 getSVec(){
        return new Vec3(this.entityData.get(SX),this.entityData.get(SY),this.entityData.get(SZ));
    }

    public void setState(int state){
        if(state == 1){
            AnimationActHandler.INSTANCE.sendAnimationMessage(this, Pursuer.PEACETOACT);
        }
    }

    public int getState(){
        return this.entityData.get(PREDICATE);
    }

    public Vec3 getLocateVec(){
        Vec3 locate =new Vec3(0, 0.2, 2).yRot((float) ((-getYRot()) / 180 * org.joml.Math.PI)).add(position());;

        return locate;
    }

    private void addToState2FX(){
        if(getAnimation()==PEACETOACT){
            int tick = getAnimationTick();
            if(level().isClientSide){
                if(tick==10){
                    for (int i = 0; i < 6; i++) {
                        float ran = random.nextFloat();
                        for (int j = 0; j < 12; j++) {
                            Vec3 vec3 = position();
                            Vec3 move = new Vec3(0, 0.1, 1 + i / 10f).yRot((float) org.joml.Math.PI * 2 * j / 12 + ran);
                            level().addParticle(new ParticleCloud.CloudData(ParticleHandler.CLOUD.get(), random.nextFloat()*0.5f, 0f, 0f, (float) (10d + random.nextDouble() * 10d)
                                    , 40, ParticleCloud.EnumCloudBehavior.SHRINK, 1f), vec3.x, vec3.y + 0.4, vec3.z, move.x, move.y, move.z);
                        }
                    }
                }
                if(tick>=10&tick<=105&&tick%3==0){
                    Vec3 demonPos = new Vec3(0,0,16).xRot(random.nextFloat()*2f).yRot(random.nextFloat()*6.28f);
                    double d0 = demonPos.horizontalDistance();
                    float rotx =((float)(Math.atan2(demonPos.x, demonPos.z)));
                    float roty =-((float)(Math.atan2(demonPos.y, d0)));
                    demonPos = demonPos.add(position());
                    level().addParticle(new  DemonParticle.DemonData(200,random.nextFloat()*4+2,rotx,roty),demonPos.x,demonPos.y,demonPos.z,0,0,0);
                }
            }
            else {
                if(tick==10){
                    EntityCameraShake.cameraShake(this.level(), this.position(), 20F, 0.03F, 100, 0);
                }
                //if(tick>=10&&tick<=70)
                //    if((tick-10)%10==0)
                //        spawnDemonGround((int)(tick/5f+2));
                //if(tick==100)
                //    spawnDemonGround(24);
            }
        }
    }

    private void addDemonWarn(){
        if(getAnimation()==REMOTE_3){
            int tick = getAnimationTick();
            if(level().isClientSide){
                boolean flad = MathUtils.isInDemon(this);
                 flad = true;
                if(tick==5&&DemonCentre!=null&&!flad) {
                    Vec3 demonPos = position().subtract(DemonCentre).scale(0.8);
                    float rotx = ((float) (Math.atan2(demonPos.x, demonPos.z)));
                    demonPos = demonPos.add(DemonCentre);
                    level().addParticle(new DemonParticle.DemonData(100, random.nextFloat() * 4 + 2, rotx, 0), demonPos.x, demonPos.y, demonPos.z, 0, 0, 0);
                }
            }
        }
    }

    private void clientLook(){
        if(level().isClientSide){
            if(getAnimation()==FASTMOVE&&getAnimationTick()>1){
                Vec3 move = getSVec();
               getLookControl().setLookAt(move.add(position()));
            }
        }
    }

    private void spawnDemonGround(int radio){
        Holder.Reference<Biome> holderOrThrow = level().registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(ModBiomes.DEMON_BIOME);
        BlockPos blockPos1 = blockPosition();
        MathUtils.fill((ServerLevel) level(),blockPos1.offset(radio,-6,radio),blockPos1.offset(-radio,10,-radio),holderOrThrow);
    }

    //trail
    private Vec3[][] trailPositions = new Vec3[64][2];
    private int trailPointer = -1;

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
        Vec3[] d2 = new Vec3[]{tt0,tt1};

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
        return trailPointer != -1;
    }
}
