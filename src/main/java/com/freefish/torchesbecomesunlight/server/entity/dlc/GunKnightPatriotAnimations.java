package com.freefish.torchesbecomesunlight.server.entity.dlc;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.entity.ITwoStateEntity;
import com.freefish.torchesbecomesunlight.server.entity.effect.EntityCameraShake;
import com.freefish.torchesbecomesunlight.server.entity.effect.SacredRealmEntity;
import com.freefish.torchesbecomesunlight.server.entity.effect.StompEntity;
import com.freefish.torchesbecomesunlight.server.entity.projectile.LightingBoom;
import com.freefish.torchesbecomesunlight.server.entity.projectile.LightingHalberd;
import com.freefish.torchesbecomesunlight.server.event.packet.toclient.InitClientEntityMessage;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.init.SoundHandle;
import com.freefish.torchesbecomesunlight.server.util.FFEntityUtils;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationAct;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;

public class GunKnightPatriotAnimations {
    public static final AnimationAct<GunKnightPatriot> ATTACK1 = new AnimationAct<GunKnightPatriot>("attack_1",44){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            LivingEntity target = entity.getTarget();
            if (target != null) {
                entity.lookAtEntity(target);
            } else {
                entity.setYRot(entity.yRotO);
            }
            if (tick == 20) {
                entity.isCanBeAttacking = true;
                entity.dashForward(3,0);
            }
            if (tick == 22) {
                entity.doRangeAttack(4,140,damage,true);
                entity.doRangeKnockBack(4,140,3);
            }
            if(tick==32){
                entity.isCanBeAttacking = false;
            }
            if(tick==32&&target!=null&&target.distanceTo(entity)<=5+target.getBbWidth()){
                AnimationActHandler.INSTANCE.sendAnimationMessage(entity,ATTACK2);
            }

        }

        @Override
        public void stop(GunKnightPatriot entity) {
            entity.isCanBeAttacking = false;
            super.stop(entity);
        }
    };
    public static final AnimationAct<GunKnightPatriot> ATTACK2 = new AnimationAct<GunKnightPatriot>("attack_2",32){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            entity.setYRot(entity.yRotO);
            int tick = entity.getAnimationTick();
            LivingEntity target = entity.getTarget();

            entity.locateEntity();
            if (tick == 14){
                StompEntity stompEntity = new StompEntity(entity.level(),8,entity,5);
                stompEntity.setPos(entity.position());
                entity.level().addFreshEntity(stompEntity);
            }

            if(entity.getGunMod()==2&&tick==20&&target!=null&&target.distanceTo(entity)<=5+target.getBbWidth()){
                AnimationActHandler.INSTANCE.sendAnimationMessage(entity,ATTACK3);
            }
        }
    };
    public static final AnimationAct<GunKnightPatriot> ATTACK3 = new AnimationAct<GunKnightPatriot>("attack_3",39){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            int tick = entity.getAnimationTick();
            LivingEntity target = entity.getTarget();
            if (target != null) {
                entity.lookAtEntity(target);
            } else {
                entity.setYRot(entity.yRotO);
            }
            if (tick == 12) {
                entity.isCanBeAttacking = true;
                entity.doRangeKnockBack(4,140,2);
            }
            else if(tick==23){
                entity.playSound(SoundHandle.SHOT_GUN.get(), 1.5F, 1.0F / (entity.getRandom().nextFloat() * 0.4F + 0.8F));
                entity.shootShotGunBullet(target,entity.getShootPos());
            }
            else if(tick==40){
                entity.isCanBeAttacking = false;
            }
        }

        @Override
        public void stop(GunKnightPatriot entity) {
            entity.isCanBeAttacking = false;
            super.stop(entity);
        }
    };
    public static final AnimationAct<GunKnightPatriot> SUMMON_TURRET = new AnimationAct<GunKnightPatriot>("summon_1",40){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            int tick = entity.getAnimationTick();
            entity.locateEntity();
            LivingEntity target = entity.getTarget();
            if(target!=null)
                entity.lookAtEntity(target);
            else
                entity.setYRot(entity.yRotO);
            RandomSource random = entity.getRandom();

            if (tick == 20) {
                for(int i = 0 ;i<3;i++){
                    Vec3 add = new Vec3(0, 30, 16+random.nextInt(8)).yRot(6.28f * i / 3 +0.5f -random.nextFloat()).add(entity.position());
                    Turret.SpawnTurret(entity.level(),add,entity);
                }
            }
        }
    };
    public static final AnimationAct<GunKnightPatriot> RELOAD = new AnimationAct<GunKnightPatriot>("reload",48){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            int tick = entity.getAnimationTick();
            entity.locateEntity();
            LivingEntity target = entity.getTarget();
            if(target!=null)
                entity.lookAtEntity(target);
            else
                entity.setYRot(entity.yRotO);

            if(tick==30){
                entity.reloadHolyBullet(10);
                entity.setIsGlowing(true);
                entity.playSound(SoundHandle.GLOWING.get(), 1.0F, 1.0F);
            }
        }
    };
    public static final AnimationAct<GunKnightPatriot> SUMMON_CHENG = new AnimationAct<GunKnightPatriot>("summon_2",85){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            int tick = entity.getAnimationTick();
            LivingEntity target = entity.getTarget();
            entity.locateEntity();
            if(target!=null)
                entity.lookAtEntity(target);
            else
                entity.setYRot(entity.yRotO);
            if(tick==76&&target!=null) {
                SacredRealmEntity sacredRealmEntity= new SacredRealmEntity(EntityHandle.SACRED_REALM.get(),entity.level());
                sacredRealmEntity.setCaster(entity);
                Vec3 pos = MathUtils.getFirstBlockAbove(entity.level(),target.position().add(0,-4,0),8);
                sacredRealmEntity.setPos((int)(pos.x) + 0.5,(int)(pos.y)+0.1,(int)(pos.z)+0.5);
                entity.level().addFreshEntity(sacredRealmEntity);
            }
        }
    };
    public static final AnimationAct<GunKnightPatriot> ALL_SHOT = new AnimationAct<GunKnightPatriot>("all_shot",73){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            int tick = entity.getAnimationTick();
            entity.locateEntity();
            LivingEntity target = entity.getTarget();
            if(tick<=20&&target!=null)
                entity.lookAtEntity(target);
            else
                entity.setYRot(entity.yRotO);

            if(target!=null&&tick==39){
                entity.shootAllBullet(target, FFEntityUtils.getBodyRotVec(entity, new Vec3(-0.5, 1.7, 3)));
            }

        }
    };
    public static final AnimationAct<GunKnightPatriot> SKILL_START = new AnimationAct<GunKnightPatriot>("skill_1",58){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            int tick = entity.getAnimationTick();
            entity.locateEntity();
            entity.setYRot(entity.yRotO);
            if(tick==57){
                AnimationActHandler.INSTANCE.sendAnimationMessage(entity,SKILL_LOOP);
            }
        }
    };
    public static final AnimationAct<GunKnightPatriot> SKILL_LOOP = new AnimationAct<GunKnightPatriot>("skill_1_loop",170){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            int tick = entity.getAnimationTick();
            entity.locateEntity();
            entity.setYRot(entity.yRotO);
            LivingEntity target = entity.getTarget();
            RandomSource random = entity.getRandom();
            if(entity.tickCount%4==0)
            {
                Vec3 pos = FFEntityUtils.getBodyRotVec(entity, new Vec3(-0.37, 3.4, 3.5));
                Vec3 pos1 = FFEntityUtils.getBodyRotVec(entity, new Vec3(-0.37, 5.5, 4.25));
                entity.shootBulletWithoutFace(pos1, pos, 3,2.5f,true);
                entity.playSound(SoundHandle.ARTILLERY.get(), 1.6F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
            }
            if(tick>20&&tick%3!=0){
                Vec3 randomPos = entity.position().add(new Vec3(0,40,10+30*random.nextFloat()).yRot(6.28f*random.nextFloat()));
                entity.shootBulletWithoutFace(randomPos.add(0,-1,0), randomPos, 3,1.5f,true);
                if(tick%8==0&&target!=null&&target.distanceTo(entity)<10){
                    Vec3 randomPos1 = target.position().add(new Vec3(0,40,4*random.nextFloat()).yRot(6.28f*random.nextFloat()));
                    entity.shootBulletWithoutFace(randomPos1.add(0,-1,0), randomPos1, 3,2f,true);
                }
            }
            if(tick==169){
                entity.isCanBeAttacking=false;
                AnimationActHandler.INSTANCE.sendAnimationMessage(entity,SKILL_END);
            }
            if(tick==1) {
                entity.isCanBeAttacking = true;
            }
        }
    };
    public static final AnimationAct<GunKnightPatriot> SKILL_END = new AnimationAct<GunKnightPatriot>("end",32){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            int tick = entity.getAnimationTick();
            entity.locateEntity();
            entity.setYRot(entity.yRotO);
        }
    };
    public static final AnimationAct<GunKnightPatriot> STATE_2 = new AnimationAct<GunKnightPatriot>("1to2",120){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            int tick = entity.getAnimationTick();
            if(tick<10){
                entity.setHealth(entity.getMaxHealth()*(tick+1)/10f);
            }
            entity.locateEntity();
            entity.setYRot(entity.yRotO);
        }

        @Override
        public void stop(GunKnightPatriot entity) {
            entity.setSpawnState(ITwoStateEntity.State.TWO);
            TorchesBecomeSunlight.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),new InitClientEntityMessage(entity,InitClientEntityMessage.InitDataType.ISTWOSTATE));
            super.stop(entity);
        }
    };
    public static final AnimationAct<GunKnightPatriot> SHIELD = new AnimationAct<GunKnightPatriot>("shield_attack",25){

        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            LivingEntity target = entity.getTarget();
            if (target != null) {
                entity.getLookControl().setLookAt(target, 30F, 30F);
                entity.lookAt(target, 30F, 30F);
            } else {
                entity.setYRot(entity.yRotO);
            }
            if (tick == 17&&target != null) {
                if(target.distanceTo(entity)<4+target.getBbWidth()/2) {
                    target.hurt(entity.damageSources().mobAttack(entity), damage);
                    target.setDeltaMovement(new Vec3(0, 0.25, 0.8).yRot((float) (-entity.getYRot() / 180 * Math.PI)));
                }
            }
        }

        @Override
        public void stop(GunKnightPatriot entity) {
            entity.isCanBeAttacking = false;
            super.stop(entity);
        }
    };
    public static final AnimationAct<GunKnightPatriot> STOMP = new AnimationAct<GunKnightPatriot>("stomp",31){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
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
        public void stop(GunKnightPatriot entity) {
            entity.isCanBeAttacking = false;
            super.stop(entity);
        }
    };
    public static final AnimationAct<GunKnightPatriot> ARTILLERY_1 = new AnimationAct<GunKnightPatriot>("heavy_artillery_1",35){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            int tick = entity.getAnimationTick();
            LivingEntity target = entity.getTarget();
            if(target!=null&&tick<=3)
                entity.lookAtEntity(target);
            else {
                entity.getLookControl().setLookAt(entity.artilleryForecastPos);
            }
            if(tick==13){
                entity.isCanBeAttacking = true;
                entity.setArtilleryForecastPos(target,entity.getShootPos());
            }
            else if(tick==23){
                entity.playSound(SoundHandle.ARTILLERY.get(), 1.5F, 1.0F / (entity.getRandom().nextFloat() * 0.4F + 0.8F));
                entity.shootArtilleryBullet(target,entity.getShootPos());
            }else if(tick==28){
                entity.isCanBeAttacking = false;
            }
        }

        @Override
        public void stop(GunKnightPatriot entity) {
            entity.isCanBeAttacking = false;
            entity.consumeHolyBullet(4);
            super.stop(entity);
        }
    };
    public static final AnimationAct<GunKnightPatriot> SHOTGUN_1 = new AnimationAct<GunKnightPatriot>("shotgun",40){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            int tick = entity.getAnimationTick();
            LivingEntity target = entity.getTarget();
            RandomSource random = entity.getRandom();

            if(target!=null){
                entity.lookAtEntity(target);
            }
            if((tick==11||tick==31)&&target!=null){
                entity.playSound(SoundHandle.SHOT_GUN.get(), 1.5F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
                entity.shootShotGunBullet(target,entity.getShootPos());
            }
        }

        @Override
        public void stop(GunKnightPatriot entity) {
            entity.consumeHolyBullet(4);
            super.stop(entity);
        }
    };
    public static final AnimationAct<GunKnightPatriot> MACHINE_GUN_1 = new AnimationAct<GunKnightPatriot>("machine_gun",100){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            int tick = entity.getAnimationTick();
            LivingEntity target = entity.getTarget();
            RandomSource random = entity.getRandom();

            if(target!=null)
                entity.lookAtEntity(target);
            if(tick%2==0&&target!=null&&tick>=16&&tick<=80){
                entity.shootMachineBullet(target,entity.getShootPos());
                if(tick==16){
                    entity.playSound(SoundHandle.MACHINE_GUN.get(), 3.0F, 1.0F);
                    entity.isCanBeAttacking = true;
                }
            }
            if(tick==85)
                entity.isCanBeAttacking = false;
        }

        @Override
        public void stop(GunKnightPatriot entity) {
            entity.isCanBeAttacking = false;
            entity.consumeHolyBullet(5);
            super.stop(entity);
        }
    };
    public static final AnimationAct<GunKnightPatriot> GUN1TO2 = new AnimationAct<GunKnightPatriot>("gun1to2",35,1){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            entity.locateEntity();
            LivingEntity target = entity.getTarget();
            if(target!=null){
                entity.lookAtEntity(target);
            }
        }

        @Override
        public void stop(GunKnightPatriot entity) {
            entity.setGunMod(1);
            super.stop(entity);
        }
    };
    public static final AnimationAct<GunKnightPatriot> GUN1TO3 = new AnimationAct<GunKnightPatriot>("gun1to3",24,1){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            entity.locateEntity();
            LivingEntity target = entity.getTarget();
            if(target!=null){
                entity.lookAtEntity(target);
            }
        }

        @Override
        public void stop(GunKnightPatriot entity) {
            entity.setGunMod(2);
            super.stop(entity);
        }

    };
    public static final AnimationAct<GunKnightPatriot> GUN2TO1 = new AnimationAct<GunKnightPatriot>("gun2to1",10,1){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            entity.locateEntity();
            LivingEntity target = entity.getTarget();
            if(target!=null){
                entity.lookAtEntity(target);
            }
        }

        @Override
        public void stop(GunKnightPatriot entity) {
            entity.setGunMod(0);
            super.stop(entity);
        }
    };
    public static final AnimationAct<GunKnightPatriot> GUN3TO1 = new AnimationAct<GunKnightPatriot>("gun3to1",10,1){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            entity.locateEntity();
            LivingEntity target = entity.getTarget();
            if(target!=null){
                entity.lookAtEntity(target);
            }
        }

        @Override
        public void stop(GunKnightPatriot entity) {
            entity.setGunMod(0);
            super.stop(entity);
        }
    };
    public static final AnimationAct<GunKnightPatriot> DIE = new AnimationAct<GunKnightPatriot>("death",45,1);

    public static final AnimationAct<GunKnightPatriot> WIND_MILL = new AnimationAct<GunKnightPatriot>("skill_halberd_1",230){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            entity.setYRot(entity.yRotO);
            int tick = entity.getAnimationTick();
            entity.locateEntity();
            if (tick == 17){
                StompEntity stompEntity = new StompEntity(entity.level(),16,entity,5);
                stompEntity.setPos(entity.position());
                entity.level().addFreshEntity(stompEntity);
            }
        }
    };
    public static final AnimationAct<GunKnightPatriot> SKILL_HALBERD_2 = new AnimationAct<GunKnightPatriot>("skill_halberd_2",135){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
            RandomSource random = entity.getRandom();

            LivingEntity target = entity.getTarget();
            if (target != null) {
                entity.locateEntity();
                entity.lookAtEntity(target);
            } else {
                entity.setYRot(entity.yRotO);
            }

            if (tick == 78||tick == 86||tick == 95||tick == 102||tick==110) {
                FFEntityUtils.doRangeAttackFX(entity,16,360,0);
                List<LivingEntity> list = entity.level().getEntitiesOfClass(LivingEntity.class,entity.getBoundingBox().inflate(16+5), livingEntity ->
                        livingEntity.distanceTo(entity)<16+livingEntity.getBbWidth()/2);
                for(LivingEntity entityHit:list) {
                    if(entityHit == entity) continue;
                    entity.invulnerableTime = 0;
                    float dist = Math.min(4,entityHit.distanceTo(entity)/4);
                    entity.doHurtEntity(entityHit,entity.damageSources().mobAttack(entity),damage*(5-dist));

                    if (entityHit instanceof Player player) {
                        ItemStack pPlayerItemStack = player.getUseItem();
                        if (!pPlayerItemStack.isEmpty() && pPlayerItemStack.is(Items.SHIELD)) {
                            player.getCooldowns().addCooldown(Items.SHIELD, 100);
                            entity.level().broadcastEntityEvent(player, (byte) 30);
                        }
                    }
                }
                EntityCameraShake.cameraShake(entity.level(), entity.position(), 20, 0.06F, 10, 15);
            }
        }
    };
    public static final AnimationAct<GunKnightPatriot> ACK_HALBERD_R = new AnimationAct<GunKnightPatriot>("attack_halberd_right",40){
        @Override
        public void start(GunKnightPatriot entity) {
            super.start(entity);
            entity.normalAttackTime++;
        }

        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
            RandomSource random = entity.getRandom();

            LivingEntity target = entity.getTarget();
            if (target != null&&tick<=22) {
                entity.locateEntity();
                entity.lookAtEntity(target);
            } else {
                entity.setYRot(entity.yRotO);
            }

            if(tick == 31) {
                entity.playSound(SoundHandle.AXE_SWEPT.get(), 1.0F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
                entity.dashForward(1.3f,0);
            }
            else if (tick == 32) {
                FFEntityUtils.doRangeAttackFX(entity,6.5,85,-30);
                entity.doRangeAttackAngle(6.5,85,damage,-30,true);
                EntityCameraShake.cameraShake(entity.level(), entity.position(), 16F, 0.04F, 5, 15);
            }

            if(tick==32){
                if(!(entity.randomRightAct(target))) {
                    AnimationActHandler.INSTANCE.sendAnimationMessage(entity, ACK_HALBERD_CL);
                }
            }
        }
    };
    public static final AnimationAct<GunKnightPatriot> ACK_HALBERD_L = new AnimationAct<GunKnightPatriot>("attack_halberd_left",35){
        @Override
        public void start(GunKnightPatriot entity) {
            super.start(entity);
            entity.normalAttackTime++;
        }

        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
            LivingEntity target = entity.getTarget();
            RandomSource random = entity.getRandom();

            if (target != null&&tick<=16) {
                entity.locateEntity();
                entity.lookAtEntity(target);
            } else {
                entity.setYRot(entity.yRotO);
            }
            if(tick == 22) {
                entity.playSound(SoundHandle.AXE_SWEPT.get(), 1.0F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
                entity.dashForward(1.3f,0);
            }
            else if (tick == 24) {
                entity.doRangeAttackAngle(6.5,90,damage,30,true);
                FFEntityUtils.doRangeAttackFX(entity,6.5,90,30);
            }

            if(tick==26){
                if(!(entity.randomLeftAct(target))) {
                    AnimationActHandler.INSTANCE.sendAnimationMessage(entity, ACK_HALBERD_CR);
                }
            }
        }
    };
    public static final AnimationAct<GunKnightPatriot> ACK_HALBERD_CR = new AnimationAct<GunKnightPatriot>("attack_halberd_cright",42){
        @Override
        public void start(GunKnightPatriot entity) {
            super.start(entity);
            entity.normalAttackTime++;
        }

        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
            RandomSource random = entity.getRandom();

            LivingEntity target = entity.getTarget();
            if (target != null&&tick<=22) {
                entity.locateEntity();
                entity.lookAtEntity(target);
            } else {
                entity.setYRot(entity.yRotO);
            }

            if(tick == 32) {
                entity.playSound(SoundHandle.AXE_SWEPT.get(), 1.0F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
                entity.dashForward(1.3f,0);
            }
            else if (tick == 33) {
                FFEntityUtils.doRangeAttackFX(entity,6.5,85,-30);
                entity.doRangeAttackAngle(6.5,85,damage,-30,true);
                EntityCameraShake.cameraShake(entity.level(), entity.position(), 16F, 0.04F, 5, 15);
            }

            if(tick==33){
                if(!(entity.randomRightAct(target))) {
                    AnimationActHandler.INSTANCE.sendAnimationMessage(entity, ACK_HALBERD_CL);
                }
            }
        }
    };
    public static final AnimationAct<GunKnightPatriot> ACK_HALBERD_CL = new AnimationAct<GunKnightPatriot>("attack_halberd_cleft",37){
        @Override
        public void start(GunKnightPatriot entity) {
            super.start(entity);
            entity.normalAttackTime++;
        }

        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
            LivingEntity target = entity.getTarget();
            RandomSource random = entity.getRandom();

            if (target != null&&tick<=16) {
                entity.locateEntity();
                entity.lookAtEntity(target);
            } else {
                entity.setYRot(entity.yRotO);
            }
            if(tick == 22) {
                entity.playSound(SoundHandle.AXE_SWEPT.get(), 1.0F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
                entity.dashForward(1.3f,0);
            }
            else if (tick == 24) {
                entity.doRangeAttackAngle(6.5,90,damage,30,true);
                FFEntityUtils.doRangeAttackFX(entity,6.5,90,30);
            }

            if(tick==26){
                if(!(entity.randomLeftAct(target))) {
                    AnimationActHandler.INSTANCE.sendAnimationMessage(entity, ACK_HALBERD_CR);
                }
            }
        }
    };
    public static final AnimationAct<GunKnightPatriot> RACK_HALBERD_CHI = new AnimationAct<GunKnightPatriot>("attack_halberd_chi",80){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
            LivingEntity target = entity.getTarget();
            RandomSource random = entity.getRandom();

            if (target != null&&(tick<=13||(tick>=25))) {
                entity.locateEntity();
                entity.lookAtEntity(target);
            } else {
                entity.setYRot(entity.yRotO);
            }
            if(tick == 12) {
                entity.playSound(SoundHandle.AXE_SWEPT.get(), 1.0F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
                entity.dashForward(1.0f,0);
            } else if(tick == 36) {
                entity.dashForward(8.0f,0);
                entity.doRangeAttackAngle(6.5,45,damage,25,true);
                FFEntityUtils.doRangeAttackFX(entity,6.5,45,25);
            }
            if (tick == 13) {
                entity.doRangeAttackAngle(6.5,60,damage,-30,true);
                FFEntityUtils.doRangeAttackFX(entity,6.5,60,-30);
            } else if (tick == 52) {
                entity.doRangeAttackAngle(6.5,60,damage,0,true);
                FFEntityUtils.doRangeAttackFX(entity,6.5,60,0);
            }
        }
    };
    public static final AnimationAct<GunKnightPatriot> LACK_HALBERD_TIAOWIND = new AnimationAct<GunKnightPatriot>("attack_halberd_tiaowind",60){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
            LivingEntity target = entity.getTarget();
            RandomSource random = entity.getRandom();

            if (target != null&&tick<=12) {
                entity.locateEntity();
                entity.lookAtEntity(target);
            } else {
                entity.setYRot(entity.yRotO);
            }
            if (tick == 11) {
                entity.doRangeAttackAngle(6.5,10,damage,0,true);
                FFEntityUtils.doRangeAttackFX(entity,6.5,10,0);
            }

            if (tick == 35) {
                entity.doCycleAttack(7,damage*1.5f);
                FFEntityUtils.doRangeAttackFX(entity,7,360,0);
            }
        }
    };
    public static final AnimationAct<GunKnightPatriot> LACK_HALBERD_DOWNCHI = new AnimationAct<GunKnightPatriot>("attack_halberd_downchi",130){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
            LivingEntity target = entity.getTarget();
            RandomSource random = entity.getRandom();
//todo Locate?
            if (target != null&&(tick<=35||(tick>=50))) {
                entity.lookAtEntity(target);
            } else {
                entity.setYRot(entity.yRotO);
            }
            if(tick == 52) {
                entity.playSound(SoundHandle.AXE_SWEPT.get(), 1.0F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
                entity.dashForward(5f,0);
            }
            if (tick == 37) {
                entity.doRangeAttackAngle(6.5,10,damage,0,true);
                FFEntityUtils.doRangeAttackFX(entity,6.5,10,0);
            } else if (tick == 75) {
                entity.doRangeAttackAngle(6,10,damage,0,true);
                FFEntityUtils.doRangeAttackFX(entity,6,10,0);
            }

            if(tick==88){
                entity.doRangeAttackAngle(6.5,80,damage*2,0,true);
                entity.doRangeKnockBack(6.5,80,4);
                FFEntityUtils.doRangeAttackFX(entity,6.5,80,0);
            }
        }
    };
    public static final AnimationAct<GunKnightPatriot> RACK_HALBERD_HEAVY = new AnimationAct<GunKnightPatriot>("attack_halberd_heavy",65){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
            LivingEntity target = entity.getTarget();
            RandomSource random = entity.getRandom();

            if (target != null&&tick<=25) {
                entity.locateEntity();
                entity.lookAtEntity(target);
            } else {
                entity.setYRot(entity.yRotO);
            }
            if(tick == 25) {
                entity.dashForward(6f,0);
                entity.doCycleAttack(3,damage);
            }
             if (tick == 33) {
                entity.doRangeAttackAngle(6.5,20,damage,0,true);
                FFEntityUtils.doRangeAttackFX(entity,6.5,20,0);
            }
        }
    };
    public static final AnimationAct<GunKnightPatriot> RACK_HALBERD_CYCLE2 = new AnimationAct<GunKnightPatriot>("attack_halberd_cycle2",55){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
            LivingEntity target = entity.getTarget();
            RandomSource random = entity.getRandom();

            if (target != null&&(tick<=14||(tick>=20))) {
                entity.locateEntity();
                entity.lookAtEntity(target);
            } else {
                entity.setYRot(entity.yRotO);
            }
            if(tick == 14) {
                entity.playSound(SoundHandle.AXE_SWEPT.get(), 1.0F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
                entity.dashForward(4f,0);
            }
            else if (tick == 15) {
                entity.doRangeAttackAngle(6.5,80,damage,35,true);
                FFEntityUtils.doRangeAttackFX(entity,6.5,80,35);
            }

            if(tick == 28) {
                entity.playSound(SoundHandle.AXE_SWEPT.get(), 1.0F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
                entity.dashForward(4f,0);
            }
            else if (tick == 29) {
                entity.doRangeAttackAngle(6.5,80,damage,-35,true);
                FFEntityUtils.doRangeAttackFX(entity,6.5,80,-35);
            }
        }
    };
    public static final AnimationAct<GunKnightPatriot> ACK_HALBERD_CHI3 = new AnimationAct<GunKnightPatriot>("attack_halberd_chi3",115){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE)*1.6f;
            LivingEntity target = entity.getTarget();
            RandomSource random = entity.getRandom();

            if (target != null) {
                entity.lookAtEntity(target);
            } else {
                entity.setYRot(entity.yRotO);
            }

            if(tick == 22) {
                entity.playSound(SoundHandle.AXE_SWEPT.get(), 1.0F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
                entity.dashForward(1f,0);
            }
            else if (tick == 24) {
                entity.doRangeAttackAngle(7,20,damage,0,true);
                FFEntityUtils.doRangeAttackFX(entity,7,20,0);
            }

            if(tick == 51) {
                entity.playSound(SoundHandle.AXE_SWEPT.get(), 1.0F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
                entity.dashForward(1f,0);
            }
            else if (tick == 53) {
                entity.doRangeAttackAngle(7,20,damage,0,true);
                FFEntityUtils.doRangeAttackFX(entity,7,20,0);
            }

            if(tick == 81) {
                entity.playSound(SoundHandle.AXE_SWEPT.get(), 1.0F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
                entity.dashForward(1f,0);
            }
            else if (tick == 83) {
                entity.doRangeAttackAngle(7,20,damage,0,true);
                FFEntityUtils.doRangeAttackFX(entity,7,20,0);
            }
        }
    };
    public static final AnimationAct<GunKnightPatriot> ACK_HALBERD_CHILEFT = new AnimationAct<GunKnightPatriot>("attack_halberd_chileft",50){
        @Override
        public void start(GunKnightPatriot entity) {
            super.start(entity);
            entity.normalAttackTime +=3;
        }

        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
            LivingEntity target = entity.getTarget();
            RandomSource random = entity.getRandom();

            if (target != null) {
                entity.lookAtEntity(target);
            } else {
                entity.setYRot(entity.yRotO);
            }

            if(tick == 22) {
                entity.playSound(SoundHandle.AXE_SWEPT.get(), 1.0F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
                entity.dashForward(0.8f,0);
            }
            else if (tick == 24) {
                entity.doRangeAttackAngle(6.5,20,damage,0,true);
                FFEntityUtils.doRangeAttackFX(entity,6.5,20,0);
            }

            if(tick==33){
                if(!(entity.randomLeftAct(target))) {
                    AnimationActHandler.INSTANCE.sendAnimationMessage(entity, ACK_HALBERD_CR);
                }
            }
        }
    };
    public static final AnimationAct<GunKnightPatriot> MOVE_HALBERD_LEFT = new AnimationAct<GunKnightPatriot>("move_halberd_left",45){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            int tick = entity.getAnimationTick();
            LivingEntity target = entity.getTarget();

            if (target != null&&(tick<=14)) {
                entity.lookAtEntity(target);
            } else {
                entity.setYRot(entity.yRotO);
            }

            if(tick==13){
                entity.dashForward(16,1.04f);
            }
        }
    };
    public static final AnimationAct<GunKnightPatriot> MOVE_HALBERD_BACK = new AnimationAct<GunKnightPatriot>("move_halberd_back",84){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            int tick = entity.getAnimationTick();
            LivingEntity target = entity.getTarget();
            float damage = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE);

            if (target != null&&(tick<=14)) {
                entity.locateEntity();
                entity.lookAtEntity(target);
            } else {
                entity.setYRot(entity.yRotO);
            }

            if(tick==20){
                entity.doCycleAttack(5,damage*0.5f);
            }

            if(tick==54){
                entity.doRangeAttackAngle(6.5,30,damage,0,true);
                FFEntityUtils.doRangeAttackFX(entity,6.5,30,0);
            }
            if(tick>=60&&tick<=70){
                entity.dashForwardContinue(-16*(1-MathUtils.easeOutQuad(((tick - 60)/10f))),0);
            }
        }
    };
    public static final AnimationAct<GunKnightPatriot> MOVE_HALBERD_RIGHT = new AnimationAct<GunKnightPatriot>("move_halberd_right",45){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            int tick = entity.getAnimationTick();
            LivingEntity target = entity.getTarget();

            if (target != null&&(tick<=14)) {
                entity.lookAtEntity(target);
            } else {
                entity.setYRot(entity.yRotO);
            }

            if(tick==13){
                entity.dashForward(16,-1.04f);
            }
        }
    };
    public static final AnimationAct<GunKnightPatriot> MOVE_HALBERD_CYCLE = new AnimationAct<GunKnightPatriot>("move_halberd_cycle",60){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            int tick = entity.getAnimationTick();
            LivingEntity target = entity.getTarget();

            if (target != null) {
                entity.lookAtEntity(target);
            } else {
                entity.setYRot(entity.yRotO);
            }

            if(tick>=14&&tick<=20){
                entity.dashForwardContinue(8,0);
            }
            if(tick>20){
                entity.dashForwardContinue(4.0f,0);
            }

            if(tick==59||(target!=null&&target.distanceTo(entity)<8+target.getBbWidth()/2)){
                entity.dashForwardContinue(4,0);
                AnimationActHandler.INSTANCE.sendAnimationMessage(entity,MOVE_HALBERD_CYCLE1);
            }
        }
    };
    public static final AnimationAct<GunKnightPatriot> MOVE_HALBERD_CYCLE1 = new AnimationAct<GunKnightPatriot>("move_halberd_cycle1",70){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            int tick = entity.getAnimationTick();
            LivingEntity target = entity.getTarget();
            float damage = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE);

            if (target != null&&(tick<=18||tick>=35)) {
                entity.lookAtEntity(target);
            } else {
                entity.setYRot(entity.yRotO);
            }

            if(tick==23||tick==24||tick==25){
                entity.dashForwardContinueNoTarget(11,0);
                entity.doCycleAttack(5.5f,damage);
            }

            if(tick==43){
                entity.doRangeAttackAngle(5.5,60,damage,0,true);
                Vec3 bodyRotVec = FFEntityUtils.getBodyRotVec(entity, new Vec3(0, 0.5f, 1));
                LightingBoom.shootLightingBoom(entity.level(),entity,target,bodyRotVec,true);
            }
        }
    };
    public static final AnimationAct<GunKnightPatriot> REMOTE_HALBERD_RL2 = new AnimationAct<GunKnightPatriot>("remote_halberd_rl2",70){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            int tick = entity.getAnimationTick();
            entity.locateEntity();
            LivingEntity target = entity.getTarget();
            if(target!=null) {
                entity.getLookControl().setLookAt(target);
            }
            if (tick == 19) {
                Vec3 bodyRotVec = FFEntityUtils.getBodyRotVec(entity, new Vec3(-1, 0.5f, 1));
                LightingBoom.shootLightingBoom(entity.level(),entity,target,bodyRotVec,true);
            }
            if(tick==44){
                Vec3 bodyRotVec = FFEntityUtils.getBodyRotVec(entity, new Vec3(1, 0.5f, 1));
                LightingBoom.shootLightingBoom(entity.level(),entity,target,bodyRotVec,false);
            }
        }
    };
    public static final AnimationAct<GunKnightPatriot> REMOTE_HALBERD_RZHOU = new AnimationAct<GunKnightPatriot>("remote_halberd_rzhou",70){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            int tick = entity.getAnimationTick();
            LivingEntity target = entity.getTarget();
            float damage = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE);

            if(target!=null&&(tick<38||tick>46)) {
                entity.getLookControl().setLookAt(target);
            }else {
                entity.setYRot(entity.yRotO);
            }

            if (tick == 19||tick == 20) {
                Vec3 bodyRotVec = FFEntityUtils.getBodyRotVec(entity, new Vec3(0, 0.5f, 1));
                LightingBoom.shootLightingBoom(entity.level(),entity,target,bodyRotVec,true);
            }

            if(tick>41&&tick<=46){
                entity.dashForwardContinueNoTarget(20,0);
                entity.doCycleAttack(3,damage);
            }
            if(tick>46&&tick<=54){
                entity.dashForwardContinueNoTarget(1,0);
            }
        }
    };
    public static final AnimationAct<GunKnightPatriot> REMOTE_HALBERD_THROW = new AnimationAct<GunKnightPatriot>("remote_halberd_throw",65){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            int tick = entity.getAnimationTick();
            entity.locateEntity();
            LivingEntity target = entity.getTarget();
            if(target!=null) {
                entity.getLookControl().setLookAt(target);
            }
            if (tick == 36) {
                Vec3 bodyRotVec = FFEntityUtils.getBodyRotVec(entity, new Vec3(-0.875, 3.43, 1.43));
                LightingHalberd.shootLightingBoom(entity.level(),entity,target,bodyRotVec);
            }
        }
    };
    public static final AnimationAct<GunKnightPatriot> REMOTE_HALBERD_SUMMON1 = new AnimationAct<GunKnightPatriot>("remote_halberd_summon1",65){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            entity.setYRot(entity.yRotO);
            int tick = entity.getAnimationTick();
            entity.locateEntity();
        }
    };
    public static final AnimationAct<GunKnightPatriot> SKILL_HALBERD_10 = new AnimationAct<GunKnightPatriot>("skill_halberd_10",66){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            entity.setYRot(entity.yRotO);
            int tick = entity.getAnimationTick();
            entity.locateEntity();

            if(tick==66){
                AnimationActHandler.INSTANCE.sendAnimationMessage(entity,SKILL_HALBERD_11);
            }
        }
    };
    public static final AnimationAct<GunKnightPatriot> SKILL_HALBERD_11 = new AnimationAct<GunKnightPatriot>("skill_halberd_11",130){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            int tick = entity.getAnimationTick();
            LivingEntity target = entity.getTarget();

            if (target != null) {
                entity.lookAtEntity(target);
            } else {
                entity.setYRot(entity.yRotO);
            }

            entity.dashForwardContinue(0.156f*4,0);

            if(tick==129||(target!=null&&target.distanceTo(entity)<4+target.getBbWidth()/2)){
                AnimationActHandler.INSTANCE.sendAnimationMessage(entity,SKILL_HALBERD_12);
            }
        }
    };
    public static final AnimationAct<GunKnightPatriot> SKILL_HALBERD_12 = new AnimationAct<GunKnightPatriot>("skill_halberd_12",95){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            int tick = entity.getAnimationTick();
            LivingEntity target = entity.getTarget();

            if (target != null) {
                entity.lookAtEntity(target);
            } else {
                entity.setYRot(entity.yRotO);
            }

            if(tick>24){
                entity.dashForwardContinue(1.09f*4, 0);
            }

            if(tick==94||(target!=null&&target.distanceTo(entity)<4+target.getBbWidth()/2)){
                AnimationActHandler.INSTANCE.sendAnimationMessage(entity,SKILL_HALBERD_13);
            }
        }
    };
    public static final AnimationAct<GunKnightPatriot> SKILL_HALBERD_13 = new AnimationAct<GunKnightPatriot>("skill_halberd_13",230){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            entity.setYRot(entity.yRotO);
            int tick = entity.getAnimationTick();
            entity.locateEntity();
            if (tick == 17){
                StompEntity stompEntity = new StompEntity(entity.level(),16,entity,5);
                stompEntity.setPos(entity.position());
                entity.level().addFreshEntity(stompEntity);
            }
        }
    };
    public static final AnimationAct<GunKnightPatriot> SKILL_HALBERD_LIAN = new AnimationAct<GunKnightPatriot>("skill_halberd_lian",135){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE)*0.5f;
            LivingEntity target = entity.getTarget();

            if (target != null) {
                entity.lookAtEntity(target);
            } else {
                entity.setYRot(entity.yRotO);
            }

            if(tick == 30||tick == 36||tick == 47||tick == 61||tick == 72||tick == 86||tick == 100) {
                entity.dashForward(4f,0);
            }
            if (tick == 30||tick == 36||tick == 47||tick == 61||tick == 72||tick == 86||tick == 100) {
                entity.doRangeKnockBack(6.5,90,0.6f);
                entity.doRangeAttackAngle(6.5,90,damage,0,true);
                FFEntityUtils.doRangeAttackFX(entity,6.5,90,0);
            }
        }
    };
    public static final AnimationAct<GunKnightPatriot> REMOTE_HALBERD_SUMMON = new AnimationAct<GunKnightPatriot>("remote_halberd_summon1",65){
        @Override
        public void tickUpdate(GunKnightPatriot entity) {
            entity.setYRot(entity.yRotO);
            int tick = entity.getAnimationTick();
            entity.locateEntity();
            if (tick == 17){
                StompEntity stompEntity = new StompEntity(entity.level(),16,entity,5);
                stompEntity.setPos(entity.position());
                entity.level().addFreshEntity(stompEntity);
            }
        }
    };
}
