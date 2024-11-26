package com.freefish.torchesbecomesunlight.server.entity.guerrillas.shield;

import com.bobmowzie.mowziesmobs.client.particle.ParticleCloud;
import com.bobmowzie.mowziesmobs.client.particle.ParticleHandler;
import com.bobmowzie.mowziesmobs.client.particle.util.AdvancedParticleBase;
import com.bobmowzie.mowziesmobs.client.particle.util.ParticleComponent;
import com.bobmowzie.mowziesmobs.client.particle.util.RibbonComponent;
import com.freefish.torchesbecomesunlight.client.particle.ParticleWindigoCrack;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster.FrostNova;
import com.freefish.torchesbecomesunlight.server.init.SoundHandle;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationAct;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import com.freefish.torchesbecomesunlight.server.init.EffectHandle;
import com.freefish.torchesbecomesunlight.server.entity.ai.FFBodyRotationControl;
import com.freefish.torchesbecomesunlight.server.entity.ai.FFPathNavigateGround;
import com.freefish.torchesbecomesunlight.server.entity.ai.attribute.AttributeRegistry;
import com.freefish.torchesbecomesunlight.server.entity.ai.entity.patriot.PatriotAttackAI;
import com.freefish.torchesbecomesunlight.server.entity.effect.StompEntity;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.GuerrillasEntity;
import com.freefish.torchesbecomesunlight.server.entity.projectile.HalberdOTIEntity;
import com.freefish.torchesbecomesunlight.server.util.AnimationWalk;
import com.freefish.torchesbecomesunlight.server.util.Parabola;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;

public class Patriot extends GuerrillasEntity {
    public static final AnimationAct<Patriot> ATTACK1 = new AnimationAct<Patriot>("attack1",40){
        @Override
        public void start(Patriot entity) {
            AnimationWalk walk = new AnimationWalk(
                    new int[]{0,13,26,33,40},
                    new float[]{0.06f,0.08f,0.11f,0,0.06f});
            entity.setAnimationWalk(walk);
        }

        @Override
        public void tickUpdate(Patriot entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            LivingEntity target = entity.getTarget();
            if (target != null) {
                entity.getLookControl().setLookAt(target, 30F, 30F);
                entity.lookAt(target, 30F, 30F);
            } else {
                entity.setYRot(entity.yRotO);

            }
            if(tick == 28)
                entity.playSound(SoundHandle.AXE_SWEPT.get(), 1.0F, 1.0F / (entity.random.nextFloat() * 0.4F + 0.8F));
            else if (tick == 30) {
                entity.doRangeAttack(5.5,140,damage,false);
            }
            if(tick == 29)
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
        public void start(Patriot entity) {
            entity.isCanBeAttacking = true;
            AnimationWalk walk = new AnimationWalk(
                    new int[]{0,10,16,20,26,33},
                    new float[]{0.06f,0.02f,0.08f,0.12f,0,0.03f});
            entity.setAnimationWalk(walk);
        }

        @Override
        public void tickUpdate(Patriot entity) {
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
                entity.isCanBeAttacking = false;
            }
        }
        @Override
        public void stop(Patriot entity) {
            entity.isCanBeAttacking = false;
            AnimationActHandler.INSTANCE.sendAnimationMessage(entity,Patriot.ATTACK3);
        }
    };
    public static final AnimationAct<Patriot> ATTACK3 = new AnimationAct<Patriot>("attack3",76){
        @Override
        public void start(Patriot entity) {
            AnimationWalk walk = new AnimationWalk(
                    new int[]{0,11,42,48,53,63,76},
                    new float[]{0.03f,0.007f,0.03f,0.15f,0,0.04f,0});
            entity.setAnimationWalk(walk);
        }

        @Override
        public void tickUpdate(Patriot entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            LivingEntity target = entity.getTarget();
            if (target != null) {
                entity.getLookControl().setLookAt(target, 30F, 30F);
                entity.lookAt(target, 30F, 30F);
            } else {
                entity.setYRot(entity.yRotO);

            }
            if (tick == 56) {
                entity.doRangeAttack(6.5,140,damage*1.5f,true);
                StompEntity stompEntity = new StompEntity(entity.level(),8,entity,3);
                stompEntity.setPos(entity.position().add(new Vec3(0, 0, 4.5).yRot((float) (-entity.yBodyRot / 180 * Math.PI))));
                entity.level().addFreshEntity(stompEntity);
                entity.playSound(SoundHandle.GROUND.get(), 1.3F, 1.0F / (entity.random.nextFloat() * 0.4F + 0.8F));
            }
            if(tick==55) entity.isCanBeAttacking = true;
            else if(tick==70) entity.isCanBeAttacking = false;
        }
        @Override
        public void stop(Patriot entity) {
            entity.isCanBeAttacking = false;
            super.stop(entity);
        }
    };
    public static final AnimationAct<Patriot> PIERCE1 = new AnimationAct<Patriot>("pierce_1",35){
        @Override
        public void start(Patriot entity) {
            AnimationWalk walk = new AnimationWalk(
                    new int[]{0,21,35},
                    new float[]{0,0.1f,0});
            entity.setAnimationWalk(walk);
        }

        @Override
        public void tickUpdate(Patriot entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            LivingEntity target = entity.getTarget();
            if (target != null&&(tick<5||tick>21)) {
                entity.getLookControl().setLookAt(target, 30F, 30F);
                entity.lookAt(target, 30F, 30F);
            } else {
                entity.setYRot(entity.yRotO);
            }
            boolean flad = false;
            if (tick == 5||tick == 10||tick == 15||tick == 20) {
                flad = entity.doRangeAttack(6.5,50,damage,true);
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
    public static final AnimationAct<Patriot> PIERCE2 = new AnimationAct<Patriot>("pierce_2",50){
        @Override
        public void start(Patriot entity) {
            AnimationWalk walk = new AnimationWalk(
                    new int[]{0,35,50},
                    new float[]{0,0.1f,0});
            entity.setAnimationWalk(walk);
        }

        @Override
        public void tickUpdate(Patriot entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            LivingEntity target = entity.getTarget();
            if (target != null&&(tick<19||tick>35)) {
                entity.getLookControl().setLookAt(target, 30F, 30F);
                entity.lookAt(target, 30F, 30F);
            } else {
                entity.setYRot(entity.yRotO);
            }
            boolean flad = false;
            if (tick == 19||tick == 24||tick == 29||tick == 34) {
                flad = entity.doRangeAttack(6.5,50,damage,true);
            }
            if(tick == 24 && flad)
                entity.playSound(SoundHandle.HIT.get(), 1.0F, 1.0F / (entity.random.nextFloat() * 0.4F + 0.8F));
            if(tick==17) entity.isCanBeAttacking = true;
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
        public void start(Patriot entity) {
            if(entity.getTarget()!=null)
                entity.getNavigation().moveTo(entity.getTarget(),0.8);
        }

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
    public static final AnimationAct<Patriot> CYCLE = new AnimationAct<Patriot>("cycle",43){
        @Override
        public void start(Patriot entity) {
            entity.isCanBeAttacking = false;
            AnimationWalk walk = new AnimationWalk(
                    new int[]{0,29,43},
                    new float[]{0,0.03f,0});
            entity.setAnimationWalk(walk);
        }

        @Override
        public void tickUpdate(Patriot entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            LivingEntity target = entity.getTarget();
            entity.setYRot(entity.yRotO);
            if (tick == 24) {
                entity.playSound(SoundHandle.AXE_SWEPT.get(), 1.0F, 1.0F / (entity.random.nextFloat() * 0.4F + 0.8F));
                entity.doCycleAttack(7.2f,damage);
            }
            if(tick==37) entity.isCanBeAttacking = false;
        }
        @Override
        public void stop(Patriot entity) {
            entity.isCanBeAttacking = false;
            super.stop(entity);
        }
    };
    public static final AnimationAct<Patriot> PROPEL1 = new AnimationAct<Patriot>("propel1",16){
        @Override
        public void start(Patriot entity) {
            AnimationWalk walk = new AnimationWalk(
                    new int[]{0,6,10,13,16},
                    new float[]{0.1f,0.35f,0.15f,0,0});
            entity.setAnimationWalk(walk);
        }

        @Override
        public void tickUpdate(Patriot entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            LivingEntity target = entity.getTarget();
            if (target != null) {
                entity.getLookControl().setLookAt(target, 30F, 30F);
                entity.lookAt(target, 30F, 30F);
            } else {
                entity.setYRot(entity.yRotO);
            }
            if (tick == 12) {
                entity.doRangeAttack(4.5,60,damage*0.8f,true);
                if(target!=null)
                    entity.LocateEntity(target,5,5);
            }
        }

        @Override
        public void stop(Patriot entity) {
            AnimationActHandler.INSTANCE.sendAnimationMessage(entity,PROPEL2);
        }
    };
    public static final AnimationAct<Patriot> PROPEL2 = new AnimationAct<Patriot>("propel2",40){
        @Override
        public void start(Patriot entity) {
            AnimationWalk walk = new AnimationWalk(
                    new int[]{0,10,15,28,33,40},
                    new float[]{0.13f,0,0.18f,0.12f,-0.04f,0.1f});
            entity.setAnimationWalk(walk);
        }

        @Override
        public void tickUpdate(Patriot entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            LivingEntity target = entity.getTarget();
            if (target != null) {
                entity.getLookControl().setLookAt(target, 30F, 30F);
                entity.lookAt(target, 30F, 30F);
            } else {
                entity.setYRot(entity.yRotO);
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
        public void start(Patriot entity) {
            AnimationWalk walk = new AnimationWalk(
                    new int[]{0,7,10,12,16,18,25},
                    new float[]{0,0.18f,0,0.4f,0,0.12f,0});
            entity.setAnimationWalk(walk);
        }

        @Override
        public void tickUpdate(Patriot entity) {
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
                    target.setDeltaMovement(new Vec3(0, 0.25, 0.8).yRot((float) (-entity.yBodyRot / 180 * Math.PI)));
                }
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
    public static final AnimationAct<Patriot> HUNT = new AnimationAct<Patriot>("hunt",57){
        @Override
        public void start(Patriot entity) {
            AnimationWalk walk = new AnimationWalk(
                    new int[]{0,10,26,35,38,43,57},
                    new float[]{0.03f,0,0.04f,0.37f,0,0.05f,0});
            entity.setAnimationWalk(walk);
        }

        @Override
        public void tickUpdate(Patriot entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            LivingEntity target = entity.getTarget();
            if (target != null) {
                entity.getLookControl().setLookAt(target, 30F, 30F);
                entity.lookAt(target, 30F, 30F);
            } else {
                entity.setYRot(entity.yRotO);
            }
            if(tick == 31||tick==32){
                float dist;
                if(target==null)
                    dist = 3;
                else
                    dist = target.distanceTo(entity);
                Vec3 hunt = new Vec3(0, 0.25, dist/10).yRot((float) (-entity.yBodyRot / 180 * Math.PI));
                entity.setDeltaMovement(hunt);
            }
            else if (tick == 41) {
                entity.playSound(SoundHandle.GROUND.get(), 1.0F, 1.0F / (entity.random.nextFloat() * 0.4F + 0.8F));
                entity.doRangeTrueAttack(6,140,damage*2.5f,true);
                StompEntity stompEntity = new StompEntity(entity.level(),8,entity,5);
                stompEntity.setPos(entity.position().add(new Vec3(0, 0, 4.5).yRot((float) (-entity.yBodyRot / 180 * Math.PI))));
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
    public static final AnimationAct<FrostNova> DIE = new AnimationAct<FrostNova>("death",45,1);

    private static final AnimationAct[] ANIMATIONS = {
            NO_ANIMATION,ATTACK1,ATTACK2,ATTACK3,PIERCE1,PIERCE2,RUN,CYCLE,SHIELD,THROW,STOMP,PROPEL1,PROPEL2,PROPEL3,STRENGTHEN,HUNT,
            DIE
    };

    public Parabola parabola = new Parabola();
    public boolean isCanBeAttacking = false;

    @OnlyIn(Dist.CLIENT)
    public Vec3[] clientVectors;

    private int timeSinceEnhanced = -1;
    private int maxEnhancedTime;
    private int enhancedTime;
    public int time=0;
    public int timeSinceThrow;

    private static final float[][] ATTACK_BLOCK_OFFSETS = {
            {-0.1F, -0.1F},
            {-0.1F, 0.1F},
            {0.1F, 0.1F},
            {0.1F, -0.1F}
    };

    private static final EntityDataAccessor<Integer> LOCATE_MOB_ID = SynchedEntityData.defineId(Patriot.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Float> TARGET_POSX = SynchedEntityData.defineId(Patriot.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TARGET_POSY = SynchedEntityData.defineId(Patriot.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TARGET_POSZ = SynchedEntityData.defineId(Patriot.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> IS_RUNNING = SynchedEntityData.defineId(Patriot.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_ENHANCED = SynchedEntityData.defineId(Patriot.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> PREDICATE = SynchedEntityData.defineId(Patriot.class, EntityDataSerializers.INT);




    public Patriot(EntityType<? extends Patriot> entityType, Level level) {
        super(entityType, level);
        if (level().isClientSide)
            clientVectors = new Vec3[] {new Vec3(0, 0, 0)};
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new PatriotAttackAI(this));

        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this , 0.3));

        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Zombie.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Ravager.class, true));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        Entity locateEntity = getLocateMob();
        if(locateEntity instanceof LivingEntity){
            Vec3 move = getLocateVec().subtract(locateEntity.position());
            locateEntity.setDeltaMovement(move);
        }


        LivingEntity target = this.getTarget();
        if(target != null && target.isAlive())
            setTargetPos(target.position());

        if(timeSinceEnhanced >= 0) {
            timeSinceEnhanced--;
            if(timeSinceEnhanced == 0){
                //AnimationActHandler.INSTANCE.sendAnimationMessage(this, ENHANCED_2);
            }
        }
        repelEntities(1.7F, 4.5f, 1.7F, 1.7F);

        if(!level().isClientSide && getPredicate()==2){
            double maxHealth = getMaxHealth();
            double health = getHealth();
            if(health <= maxHealth*(maxEnhancedTime-enhancedTime)/maxEnhancedTime){
                enhancedTime++;
                startEnhanced(200);
            }
        }

        if(getAnimation()==THROW){
            ShootHalberd(0);
        }
        else if(getAnimation()==PROPEL3)
            ShootHalberd(-20);

        if(getIsEnhanced())
            setDeltaMovement(0, getDeltaMovement().y, 0);

        float moveX = (float) (getX() - xo);
        float moveZ = (float) (getZ() - zo);
        float speed = Mth.sqrt(moveX * moveX + moveZ * moveZ);
        if(this.level().isClientSide && speed > 0.03) {
            if (tickCount % 18 == 1 &&getAnimation() == NO_ANIMATION)
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundHandle.GIANT_STEP.get(), this.getSoundSource(), 20F, 1F, false);
            if (tickCount % 10 == 1 &&getAnimation() == RUN)
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundHandle.GIANT_STEP.get(), this.getSoundSource(), 20F, 1F, false);
        }
        if(getHealth() <=getMaxHealth() && getPredicate()==1) {
            if(!level().isClientSide){
                setPredicateEffect(2);
            }
        }
        if(!level().isClientSide) {
            if(getHealth()>getMaxHealth()&&getPredicate()==2)
                setPredicateEffect(1);
        }
        addIceWindParticle();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if(amount>getMaxHealth()/10&&!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) amount = getMaxHealth()/10;
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
            if (!isCanBeAttacking) {
                int arc = 80;
                float entityHitAngle = (float) ((Math.atan2(entitySource.getZ() - getZ(), entitySource.getX() - getX()) * (180 / Math.PI) - 90) % 360);
                float entityAttackingAngle = yBodyRot % 360;
                if (entityHitAngle < 0) {
                    entityHitAngle += 360;
                }
                if (entityAttackingAngle < 0) {
                    entityAttackingAngle += 360;
                }
                float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
                if ((entityRelativeAngle <= arc / 2f && entityRelativeAngle >= -arc / 2f) || (entityRelativeAngle >= 360 - arc / 2f || entityRelativeAngle <= -arc + 90f / 2f)) {
                    playSound(SoundEvents.SHIELD_BREAK,0.4f,2); //playSound(MMSounds.ENTITY_WROUGHT_UNDAMAGED.get(), 0.4F, 2);
                    addShieldArmorParticle();
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
                .add(AttributeRegistry.ARMOR_DURABILITY.get(),250f)
                .add(Attributes.KNOCKBACK_RESISTANCE,1f);
    }

    @Override
    public AnimationAct[] getAnimations() {
        return ANIMATIONS;
    }

    @Override
    protected <T extends GeoEntity> void basicAnimation(AnimationState<T> event) {
        float dist = (float) position().subtract(xo,yo,zo).length();
        if (true) {
            if (dist>0.03)
                event.getController().setAnimation(RawAnimation.begin().thenLoop("march"));
            else
                event.getController().setAnimation(RawAnimation.begin().thenLoop("idle_aggressive"));
        }
        else{
            if (event.isMoving())
                event.getController().setAnimation(RawAnimation.begin().thenLoop("walk_peace"));
            else
                event.getController().setAnimation(RawAnimation.begin().thenLoop("idle_peace"));
        }
    }

    private PlayState shieldPredicate(AnimationState<Patriot> event) {
        return PlayState.CONTINUE;
    }

    @Override
    public @org.jetbrains.annotations.Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @org.jetbrains.annotations.Nullable SpawnGroupData pSpawnData, @org.jetbrains.annotations.Nullable CompoundTag pDataTag) {
        maxEnhancedTime = 2;
        enhancedTime = 0;
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TARGET_POSX, 0f);
        this.entityData.define(TARGET_POSY, 0f);
        this.entityData.define(TARGET_POSZ, 0f);
        this.entityData.define(IS_RUNNING, false);
        this.entityData.define(IS_ENHANCED,false);
        this.entityData.define(PREDICATE,1);
        this.entityData.define(LOCATE_MOB_ID, -1);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        Vector3f vector3f = getTargetPos();
        compound.putFloat("targetPosX",vector3f.x);
        compound.putFloat("targetPosY",vector3f.y);
        compound.putFloat("targetPosZ",vector3f.z);
        compound.putBoolean("isRunning",getIsRunning());
        compound.putInt("predicate", getPredicate());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        float x = compound.getFloat("targetPosX");
        float y = compound.getFloat("targetPosY");
        float z = compound.getFloat("targetPosZ");
        setTargetPos(new Vec3(x,y,z));
        setIsRunning(compound.getBoolean("isRunning"));
        setPredicate(compound.getInt("predicate"));
    }

    @Override
    public SoundEvent getBossMusic() {
        return SoundHandle.PATRIOT_UNYIELDING.get();
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false;
    }

    @Override
    protected boolean canPlayMusic() {
        return super.canPlayMusic() && getPredicate()==1;
    }



    private void addShieldArmorParticle(){
    }

    public void startEnhanced(int time){
        if(time <= 30) return;
        this.timeSinceEnhanced = time;
        setIsEnhanced(true);
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

    public Boolean getIsRunning() {
        return this.entityData.get(IS_RUNNING);
    }

    public void setIsRunning(boolean isRunning) {
        if(true) return;
        //if(!level().isClientSide) {
        //    AttributeInstance speedAttribute = getAttribute(Attributes.MOVEMENT_SPEED);
        //    if (speedAttribute != null) {
        //        if (isRunning) {
        //            speedAttribute.addTransientModifier(new AttributeModifier("speed", 0.2F, AttributeModifier.Operation.ADDITION));
        //        } else {
        //            speedAttribute.addTransientModifier(new AttributeModifier("speed", -0.2F, AttributeModifier.Operation.ADDITION));
        //        }
        //    }
        //}
        //this.entityData.set(IS_RUNNING, isRunning);
    }

    public Boolean getIsEnhanced() {
        return this.entityData.get(IS_ENHANCED);
    }

    public void setIsEnhanced(boolean isEnhanced) {
        this.entityData.set(IS_ENHANCED, isEnhanced);
    }

    public int getPredicate() {
        return this.entityData.get(PREDICATE);
    }

    public void setLocateMobId(int i) {
        this.entityData.set(LOCATE_MOB_ID, i);
    }


    public int getLocateMobId() {
        return this.entityData.get(LOCATE_MOB_ID);
    }

    public void setPredicateEffect(int predicate){
        if(predicate == getPredicate()) return;
//todo
        if (predicate == 1) {
            enhancedTime = 0;
            getAttribute(Attributes.ARMOR).addTransientModifier(new AttributeModifier("armor", (double)6, AttributeModifier.Operation.ADDITION));
            getAttribute(Attributes.ATTACK_DAMAGE).addTransientModifier(new AttributeModifier("act", (double)6, AttributeModifier.Operation.ADDITION));
        } else {
            getAttribute(Attributes.ARMOR).addTransientModifier(new AttributeModifier("armor", (double)6, AttributeModifier.Operation.ADDITION));
            getAttribute(Attributes.ATTACK_DAMAGE).addTransientModifier(new AttributeModifier("act", (double)6, AttributeModifier.Operation.ADDITION));
        }
        setPredicate(predicate);
    }

    public void setPredicate(int predicate) {
        this.entityData.set(PREDICATE, predicate);
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
            HalberdOTIEntity ganRanZheZhiJi = new HalberdOTIEntity(level(), this, new ItemStack(Items.TRIDENT));
            ganRanZheZhiJi.absMoveTo(getX() + parabola.getX()*i,getY() + parabola.getY(x3),getZ() + parabola.getZ()*i, -pitch, yaw);
            ganRanZheZhiJi.shootFromRotation(this, -pitch, -yaw, 0.0F, 6F, 0f);
            ganRanZheZhiJi.setNoGravity(true);
            if(!level().isClientSide)level().addFreshEntity(ganRanZheZhiJi);
        }
    }

    public boolean changeHalberd(){
        int tick = getAnimationTick();
        return getAnimation() == HUNT &&(tick>9&&tick<43);
    }

    private void addIceWindParticle(){
        if(getAnimation() == HUNT) {
            int tick = getAnimationTick();
            if(level().isClientSide) {
                if (tick == 26) {
                    for (int i = 0; i < 6; i++) {
                        float ran = random.nextFloat();
                        for (int j = 0; j < 12; j++) {
                            Vec3 vec3 = position();
                            Vec3 move = new Vec3(0, 0.1, 0.1 + i / 12f).yRot((float) org.joml.Math.PI * 2 * j / 12 + ran);
                            level().addParticle(new ParticleCloud.CloudData(ParticleHandler.CLOUD.get(), 0.6f, 0.1f, 0.1f, (float) (10d + random.nextDouble() * 10d), 20, ParticleCloud.EnumCloudBehavior.SHRINK, 1f), vec3.x, vec3.y + 0.4, vec3.z, move.x, move.y, move.z);
                        }
                    }
                }
                if(tick > 26 && tick <46) {
                    Vec3 Halberd = clientVectors[0];
                    for (int i = 0; i < 4; i++) {
                        level().addParticle(new ParticleWindigoCrack.WindigoCrackData(30, false), Halberd.x, Halberd.y + random.nextFloat() - 0.5, Halberd.z, 0, 0, 0);
                    }
                }
            }
        }
    }
}
