package com.freefish.torchesbecomesunlight.server.entity.guerrillas.shield;

import com.freefish.torchesbecomesunlight.server.entity.ai.FFBodyRotationControl;
import com.freefish.torchesbecomesunlight.server.entity.ai.FFPathNavigateGround;
import com.freefish.torchesbecomesunlight.server.entity.ai.attribute.AttributeRegistry;
import com.freefish.torchesbecomesunlight.server.entity.ai.entity.ShieldGuardAttackAI;
import com.freefish.torchesbecomesunlight.server.entity.effect.StompEntity;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.GuerrillasEntity;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster.FrostNova;
import com.freefish.torchesbecomesunlight.server.init.EffectHandle;
import com.freefish.torchesbecomesunlight.server.init.SoundHandle;
import com.freefish.torchesbecomesunlight.server.util.AnimationWalk;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationAct;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import net.minecraft.world.effect.MobEffectInstance;
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
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.List;

public class ShieldGuard extends GuerrillasEntity {
    public static final AnimationAct<ShieldGuard> ATTACK2 = new AnimationAct<ShieldGuard>("attack",33){
        @Override
        public void start(ShieldGuard entity) {
            AnimationWalk walk = new AnimationWalk(
                    new int[]{0,10,16,20,26,33},
                    new float[]{0.06f,0.02f,0.08f,0.12f,0,0.03f});
            entity.setAnimationWalk(walk);
        }

        @Override
        public void tickUpdate(ShieldGuard entity) {
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
    public static final AnimationAct<ShieldGuard> RUN = new  AnimationAct<ShieldGuard>("run",200){
        @Override
        public void start(ShieldGuard entity) {
            if(entity.getTarget()!=null)
                entity.getNavigation().moveTo(entity.getTarget(),0.8);
        }

        @Override
        public void tickUpdate(ShieldGuard entity) {
            LivingEntity target = entity.getTarget();
            if(target!=null) {
                entity.getLookControl().setLookAt(target, 30F, 30F);
                entity.lookAt(target, 30F, 30F);
            }
        }
    };
    public static final AnimationAct<ShieldGuard> SHIELD = new AnimationAct<ShieldGuard>("shield_attack",25){
        @Override
        public void start(ShieldGuard entity) {
            AnimationWalk walk = new AnimationWalk(
                    new int[]{0,7,10,12,16,18,25},
                    new float[]{0,0.18f,0,0.4f,0,0.12f,0});
            entity.setAnimationWalk(walk);
        }

        @Override
        public void tickUpdate(ShieldGuard entity) {
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
    };
    public static final AnimationAct<ShieldGuard> STRENGTHEN = new AnimationAct<ShieldGuard>("strengthen",30){
        @Override
        public void tickUpdate(ShieldGuard entity) {
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
    public static final AnimationAct<ShieldGuard> STOMP = new AnimationAct<ShieldGuard>("stomp",31){
        @Override
        public void tickUpdate(ShieldGuard entity) {
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
    //public static final AnimationAct<FrostNova> DIE = new AnimationAct<FrostNova>("death",45,1);
    //public ShieldGuard(EntityType<? extends GuerrillasEntity> entityType, Level level) {
    //    super(entityType, level);
    //}

    private static AnimationAct[] ANIMATIONS = new AnimationAct[]{NO_ANIMATION,ATTACK2,RUN,SHIELD,STRENGTHEN,STOMP};

    public ShieldGuard(EntityType<? extends GuerrillasEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public AnimationAct[] getAnimations() {
        return ANIMATIONS;
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
        this.goalSelector.addGoal(2, new ShieldGuardAttackAI(this));

        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this , 0.3));

        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Zombie.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Ravager.class, true));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return GuerrillasEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 80.0D)
                .add(Attributes.ATTACK_DAMAGE, 10.0f)
                .add(Attributes.ARMOR, 20.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 10.0D)
                .add(Attributes.FOLLOW_RANGE, 48)
                .add(Attributes.KNOCKBACK_RESISTANCE,1f);
    }

    @Override
    protected <T extends GeoEntity> void basicAnimation(AnimationState<T> event) {
        if(event.isMoving())
            event.setAnimation(RawAnimation.begin().thenLoop("walk_peace"));
        else
            event.setAnimation(RawAnimation.begin().thenLoop("idle"));
    }
}
