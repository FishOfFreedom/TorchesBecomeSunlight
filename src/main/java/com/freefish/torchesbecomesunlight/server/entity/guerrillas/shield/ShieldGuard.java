package com.freefish.torchesbecomesunlight.server.entity.guerrillas.shield;

import com.freefish.torchesbecomesunlight.server.entity.ITeamMemberStorage;
import com.freefish.torchesbecomesunlight.server.entity.ai.FFBodyRotationControl;
import com.freefish.torchesbecomesunlight.server.entity.ai.FFPathNavigateGround;
import com.freefish.torchesbecomesunlight.server.entity.ai.entity.ShieldGuardAttackAI;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.GuerrillasEntity;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.ai.FollowLeaderGoal;
import com.freefish.torchesbecomesunlight.server.util.FFEntityUtils;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationAct;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
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
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;

public class ShieldGuard extends GuerrillasEntity implements ITeamMemberStorage<Patriot> {
    public static final AnimationAct<ShieldGuard> ATTACK = new AnimationAct<ShieldGuard>("attack",33){

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
            if(tick == 22) {
                //entity.playSound(SoundHandle.AXE_SWEPT.get(), 1.0F, 1.0F / (entity.random.nextFloat() * 0.4F + 0.8F));
            }
            else if (tick == 24) {
                entity.doRangeAttack(2.5,90,damage,true);
                FFEntityUtils.doRangeAttackFX(entity,2.5,90,0);
            }

            if(tick == 7) entity.isShield = false;
            else if(tick == 24) entity.isShield = true;
        }
    };
    public static final AnimationAct<ShieldGuard> ATTACK2 = new AnimationAct<ShieldGuard>("heavyattack",40){

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

            if(tick == 24) {
                entity.dashForward(4,0);
            }else if (tick == 26) {
                entity.doRangeAttack(3,60,damage,true);
                FFEntityUtils.doRangeAttackFX(entity,3,60,0);
            }else if (tick == 30) {
                entity.doRangeAttack(3,60,damage,true);
                FFEntityUtils.doRangeAttackFX(entity,3,60,0);
            }

            if(tick == 6) entity.isShield = false;
            else if(tick == 26) entity.isShield = true;
        }
    };
    public static final AnimationAct<ShieldGuard> BACK = new AnimationAct<ShieldGuard>("back",25){

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
            if(tick==6){
                float jumpLen = -2;
                Vec3 direction = new Vec3(0, 0.2, jumpLen).yRot((float) (-entity.getYRot() / 180 * org.joml.Math.PI));
                entity.setDeltaMovement(direction);
            }
        }
    };
    public static final AnimationAct<ShieldGuard> ATTACK3 = new AnimationAct<ShieldGuard>("lianattack",70){

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
            if (tick == 51) {
                entity.dashForward(4,0);
            }
            if (tick == 14) {
                entity.dashForward(4,0);
            }

            if(tick == 15)
                entity.doRangeAttack(3,20,damage,true);
            else if (tick == 29) {
                entity.doRangeAttack(3,90,damage,true);
            }
            else if (tick == 52) {
                entity.doRangeAttack(5.5,20,damage,true);
            }
        }
    };
    public static final AnimationAct<ShieldGuard> ATTACK4 = new AnimationAct<ShieldGuard>("attack3",37){

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
            if(tick == 19) {
                //entity.playSound(SoundHandle.AXE_SWEPT.get(), 1.0F, 1.0F / (entity.random.nextFloat() * 0.4F + 0.8F));
                entity.dashForward(8,0);
            }else if (tick == 24) {
                entity.doRangeAttack(3.5,30,damage*1.5f,true);
                FFEntityUtils.doRangeAttackFX(entity,3.5,30,0);
            }

            if(tick == 24) entity.isShield = false;
            else if(tick == 35) entity.isShield = true;
        }
    };
    public static final AnimationAct<ShieldGuard> RUN_ATTACK3 = new AnimationAct<ShieldGuard>("runattack3",23){

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
            if(tick == 5) {
                //entity.playSound(SoundHandle.AXE_SWEPT.get(), 1.0F, 1.0F / (entity.random.nextFloat() * 0.4F + 0.8F));
                entity.dashForward(8,0);
            }else if (tick == 10) {
                entity.doRangeAttack(3.5,30,damage*1.5f,true);
                FFEntityUtils.doRangeAttackFX(entity,3.5,30,0);
            }

            if(tick == 14) entity.isShield = false;
            else if(tick == 25) entity.isShield = true;
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
                if(target.distanceTo(entity)<5){
                    AnimationActHandler.INSTANCE.sendAnimationMessage(entity,RUN_ATTACK3);
                }
            }
        }
    };
    public static final AnimationAct<ShieldGuard> SHIELD = new AnimationAct<ShieldGuard>("shield_attack",25){

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
                if(target.distanceTo(entity)<2+target.getBbWidth()/2) {
                    target.hurt(entity.damageSources().mobAttack(entity), damage);
                    target.setDeltaMovement(new Vec3(0, 0.25, 1.2).yRot((float) (-entity.yBodyRot / 180 * Math.PI)));
                }
            }
            if(tick == 15) entity.isShield = false;
            else if(tick == 23) entity.isShield = true;
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
                    guerrilla.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,200,1));
                }
            }
        }
    };
    public static final AnimationAct<ShieldGuard> BREAK = new AnimationAct<ShieldGuard>("break_start",30,1){
        @Override
        public void tickUpdate(ShieldGuard entity) {
            entity.setYRot(entity.yRotO);
            int tick = entity.getAnimationTick();
            entity.locateEntity();
            if(tick<=16&&tick%4==0){
                float health = (tick+4)/20.f;
                entity.setHealth(entity.getMaxHealth()*health);
            }
        }
    };
    //public static final AnimationAct<FrostNova> DIE = new AnimationAct<FrostNova>("death",45,1);
    //public ShieldGuard(EntityType<? extends GuerrillasEntity> entityType, Level level) {
    //    super(entityType, level);
    //}

    private static AnimationAct[] ANIMATIONS = new AnimationAct[]{NO_ANIMATION,ATTACK2,ATTACK4,ATTACK,ATTACK3,RUN,BACK,RUN_ATTACK3,SHIELD,STRENGTHEN,BREAK};
    private int noActMode;

    public ShieldGuard(EntityType<? extends GuerrillasEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        super.tick();

        if(!level().isClientSide&&tickCount%2==0){
            if(getTarget()!=null&&getTarget().isAlive()){
                noActMode = 0;
            }else {
                noActMode++;
                if(noActMode==80){
                    this.getNavigation().stop();
                    setAggressive(false);
                }
            }
        }
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
        this.goalSelector.addGoal(7, new FollowLeaderGoal<>(this, 0.3, 8, 12));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this , 0.3));

        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Zombie.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Ravager.class, true));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers(GuerrillasEntity.class));
    }


    @Override
    public void die(DamageSource pDamageSource) {
        Patriot leader1 = getLeader();
        if(leader1!=null){
            leader1.teamMemberStorage.removeMember(this);
        }
        super.die(pDamageSource);
    }

    private final AnimationController<ShieldGuard> shield = new AnimationController<ShieldGuard>(this, "shieldController", 5, this::shieldPredicate);

    private PlayState shieldPredicate(AnimationState<ShieldGuard> event) {
        event.setAnimation(RawAnimation.begin().thenLoop("shieldfx"));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar event) {
        super.registerControllers(event);
        event.add(shield);
    }

    @Override
    public float getStepHeight() {
        return 1.2f;
    }

    public boolean isShield;
    @Override
    public boolean hurt(DamageSource source, float damage) {
        Entity entitySource = source.getEntity();

        if(isShield&&entitySource!=null){
            int arc = 60;
            float entityHitAngle = (float) ((Math.atan2(entitySource.getZ() - getZ(), entitySource.getX() - getX()) * (180 / Math.PI) - 90) % 360);
            float entityAttackingAngle = getYRot() % 360;
            if (entityHitAngle < 0) {
                entityHitAngle += 360;
            }
            if (entityAttackingAngle < 0) {
                entityAttackingAngle += 360;
            }
            if (Math.abs(entityAttackingAngle - entityHitAngle) < arc) {
                playSound(SoundEvents.SHIELD_BREAK, 0.4f, 2);
                damage *= 0.2f;
            }
            return super.hurt(source, damage);
        }
        else
            return super.hurt(source, damage);
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
        if(!isAggressive()){
            if (event.isMoving())
                event.setAnimation(RawAnimation.begin().thenLoop("walk_peace"));
            else
                event.setAnimation(RawAnimation.begin().thenLoop("idle"));
        }else {
            if (event.isMoving())
                event.setAnimation(RawAnimation.begin().thenLoop("march"));
            else
                event.setAnimation(RawAnimation.begin().thenLoop("idle_shield"));
        }
    }

    private Patriot leader;

    @Override
    public Patriot getLeader() {
        return leader;
    }

    @Override
    public void setLeader(Patriot leader) {
        this.leader = leader;
    }
}
