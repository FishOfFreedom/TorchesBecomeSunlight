package com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster;

import com.freefish.torchesbecomesunlight.server.animation.AnimationAct;
import com.freefish.torchesbecomesunlight.server.animation.AnimationActHandler;
import com.freefish.torchesbecomesunlight.server.entity.AnimatedEntity;
import com.freefish.torchesbecomesunlight.server.entity.ai.attribute.AttributeRegistry;
import com.freefish.torchesbecomesunlight.server.entity.ai.entity.snownova.SnowNovaAttackAI;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.GuerrillasEntity;
import com.freefish.torchesbecomesunlight.server.entity.projectile.IceCrystal;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationState;

public class SnowNova1 extends AnimatedEntity {
    public int timeSinceJump;
    public final int maxTimeSinceJump = 80;

    public static final AnimationAct<SnowNova1> WALK_PEACE =new AnimationAct<SnowNova1>("walk_peace",0);
    public static final AnimationAct<SnowNova1> IDLE_PEACE = new AnimationAct<SnowNova1>("idle_peace",0);
    public static final AnimationAct<SnowNova1> WALK_AGGRESSIVE = new AnimationAct<SnowNova1>("walk_aggressive",0);
    public static final AnimationAct<SnowNova1> IDLE_AGGRESSIVE = new AnimationAct<SnowNova1>("idle_aggressive",0);
    public static final AnimationAct<SnowNova1> RIGHT_JUMP =new AnimationAct<SnowNova1>("rightjump",15){
        @Override
        public void tickUpdate(SnowNova1 entity) {
            int tick = entity.getAnimationTick();
            RandomSource random = entity.getRandom();
            if(tick==1) {
                Vec3 direction = new Vec3(0, 0.3, -1-random.nextFloat()/2).yRot((float) ((-90-entity.yBodyRot) / 180 * Math.PI));
                entity.setDeltaMovement(direction);
            }
        }
    };
    public static final AnimationAct<SnowNova1> LEFT_JUMP = new AnimationAct<SnowNova1>("leftjump",15){
        @Override
        public void tickUpdate(SnowNova1 entity) {
            int tick = entity.getAnimationTick();
            RandomSource random = entity.getRandom();
            if(tick==1) {
                Vec3 direction = new Vec3(0, 0.3, -1-random.nextFloat()/2).yRot((float) ((90-entity.yBodyRot) / 180 * Math.PI));
                entity.setDeltaMovement(direction);
            }
        }
    };
    public static final AnimationAct<SnowNova1> BACK_JUMP = new AnimationAct<SnowNova1>("backjump",16){
        @Override
        public void tickUpdate(SnowNova1 entity) {
            int tick = entity.getAnimationTick();
            RandomSource random = entity.getRandom();
            if(tick==1) {
                Vec3 direction = new Vec3(0, 0.3, -1-random.nextFloat()/2).yRot((float) (-entity.yBodyRot / 180 * Math.PI));
                entity.setDeltaMovement(direction);
            }
        }

        @Override
        public void stop(SnowNova1 entity) {
            entity.timeSinceJump = 0;
            if(entity.getTarget()!=null)
                entity.cycleRadius = entity.distanceTo(entity.getTarget());
            entity.startCycle();
            super.stop(entity);
        }
    };
    public static final AnimationAct<SnowNova1> ATTACK_1 = new AnimationAct<SnowNova1>("attack_1",25){
        @Override
        public void tickUpdate(SnowNova1 entity) {
            int tick = entity.getAnimationTick();
            LivingEntity target = entity.getTarget();
            float damage = (float)entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            if(target!=null&&target.distanceTo(entity)<=2.2) {
                entity.lookAt(target,30.0F, 30.0F);
                if (tick == 5) {
                    target.hurt(entity.damageSources().mobAttack(entity),damage);
                }
                else if(tick == 12){
                    target.hurt(entity.damageSources().mobAttack(entity),damage);
                }
            }
        }

        @Override
        public void stop(SnowNova1 entity) {
            RandomSource random = entity.getRandom();
            if(random.nextFloat()>0.33)
                AnimationActHandler.INSTANCE.sendAnimationMessage(entity,SnowNova1.BACK_JUMP);
            else
                super.stop(entity);
        }
    };
    public static final AnimationAct<SnowNova1> ATTACK_2 = new  AnimationAct<SnowNova1>("attack_2",20){
        @Override
        public void tickUpdate(SnowNova1 entity) {
            int tick = entity.getAnimationTick();
            LivingEntity target = entity.getTarget();
            float damage = (float)entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            if(target!=null&&target.distanceTo(entity)<=2.2) {
                entity.lookAt(target,30f,30f);
                if (tick == 3) {
                    target.hurt(entity.damageSources().mobAttack(entity),damage);
                }
                else if(tick == 9){
                    target.hurt(entity.damageSources().mobAttack(entity),damage);
                }
                else if(tick == 15){
                    target.hurt(entity.damageSources().mobAttack(entity),damage);
                }
            }
        }
        @Override
        public void stop(SnowNova1 entity) {
            RandomSource random = entity.getRandom();
            if(random.nextFloat()>0.33)
                AnimationActHandler.INSTANCE.sendAnimationMessage(entity,SnowNova1.BACK_JUMP);
            else
                super.stop(entity);
        }
    };
    public static final AnimationAct<SnowNova1> REMOTE_1 = new  AnimationAct<SnowNova1>("remote_1",30){
        @Override
        public void tickUpdate(SnowNova1 entity) {
            int tick = entity.getAnimationTick();
            entity.setDeltaMovement(0,entity.getDeltaMovement().y,0);
            entity.setPos(entity.xo,entity.yo,entity.zo);
            LivingEntity target = entity.getTarget();
            float damage = (float)entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            if(target!=null) {
                entity.lookAt(target,0f,0f);
                if (tick == 7) {
                    entity.shootTarget(target);
                }
            }
        }
    };

    @Override
    public AnimationAct[] getAnimations() {
        return new AnimationAct[]{ATTACK_1,ATTACK_2,RIGHT_JUMP,LEFT_JUMP,BACK_JUMP,REMOTE_1};
    }

    public SnowNova1(EntityType<? extends SnowNova1> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar event) {
        super.registerControllers(event);
    }

    @Override
    public void tick() {
        super.tick();
        if(!level().isClientSide()){
            if(cycleTime==0){
                AnimationActHandler.INSTANCE.sendAnimationMessage(this,REMOTE_1);
            }
            if(timeSinceJump < maxTimeSinceJump)
                timeSinceJump++;
            if(cycleTime>=0)
                cycleTime--;
        }
    }

    @Override
    public AnimationAct getDeathAnimation() {
        return null;
    }

    @Override
    protected <T extends GeoEntity> void basicAnimation(AnimationState<T> event) {
        if (isAggressive()) {
            event.getController().transitionLength(4);
            if (event.isMoving()) {
                event.getController().setAnimation(WALK_AGGRESSIVE.getRawAnimation());
            } else {
                event.getController().setAnimation(IDLE_AGGRESSIVE.getRawAnimation());
            }
        } else {
            event.getController().transitionLength(4);
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

        this.goalSelector.addGoal(1,new SnowNovaAttackAI(this));

        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this , 0.3F));

        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Zombie.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Ravager.class, true));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return GuerrillasEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 50.0D)
                .add(Attributes.ATTACK_DAMAGE, 20.0f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
                .add(AttributeRegistry.ARMOR_DURABILITY.get(),10f);
    }

    public void shootTarget(Entity target){
        IceCrystal abstractarrow = new IceCrystal(level(),this);
        Vec3 position = this.position().add(new Vec3(0,this.getBoundingBox().getYsize()/2,1).yRot((float)(-this.yBodyRot/180*Math.PI)));
        abstractarrow.setPos(position);
        double d0 = target.getX() - this.getX();
        double d1 = target.getY(0.3333333333333333D) - abstractarrow.getY();
        double d2 = target.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        abstractarrow.shoot(d0, d1 + d3 * (double)0.2F, d2, 1.6F, (float)(14 - this.level().getDifficulty().getId() * 4));
        this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(abstractarrow);
    }

    public void startCycle(){
        cycleTick = 0;
        Vec3 vec3 = position();
        if(getTarget()!=null) {
            Vec3 targetVec = getTarget().position();
            startP = Math.atan2(targetVec.x - vec3.x, vec3.z - targetVec.z) + Math.toRadians(90);
        }
        if(random.nextInt(3)==0)
            isRight =!isRight;
        cycleTime = 80 + random.nextInt(40);
    }

    private int cycleTick;
    public boolean isRight = true;
    public int cycleTime=-1;
    public double cycleRadius;
    public double startP;

    public Vec3 updateCyclePosition(float speed){
        if(random.nextInt(100)==0)
            isRight = !isRight;

        if(isRight)
            cycleTick++;
        else
            cycleTick--;

        Vec3 cycle;
        if(getTarget()!=null)
            cycle = getTarget().position().add(cycleRadius*Math.cos(cycleTick*0.314*speed+startP),0,cycleRadius*Math.sin(cycleTick*0.314f*speed+startP));
        else
            cycle = position();
        return cycle;

    }
}
