package com.freefish.torchesbecomesunlight.server.entity.ursus;

import com.freefish.torchesbecomesunlight.server.entity.ai.FFLookAtPlayerGoal;
import com.freefish.torchesbecomesunlight.server.entity.ai.FFRandomLookAroundGoal;
import com.freefish.torchesbecomesunlight.server.entity.ai.FFWaterAvoidingRandomStrollGoal;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.GuerrillasEntity;
import com.freefish.torchesbecomesunlight.server.entity.ursus.ai.PatrolCaptainAttackAI;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationAct;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;

public class PatrolCaptain extends UrsusEntity{
    public static final RawAnimation WALK_ = RawAnimation.begin().then("walk", Animation.LoopType.LOOP);
    public static final RawAnimation IDLE_ = RawAnimation.begin().then("idle", Animation.LoopType.LOOP);

    public static final AnimationAct<PatrolCaptain> ATTACK = new AnimationAct<PatrolCaptain>("attack_3",26) {
        @Override
        public void tickUpdate(PatrolCaptain entity) {
            int tick = entity.getAnimationTick();
            LivingEntity target = entity.getTarget();
            float damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            entity.locateEntity();

            if (target != null) {
                entity.getLookControl().setLookAt(target);
                if (target.distanceTo(entity) <= 2 + target.getBbWidth() / 2) {
                    if (tick == 14) {
                        target.hurt(entity.damageSources().mobAttack(entity), damage);
                    }
                }
            }
        }
    };
    public static final AnimationAct<PatrolCaptain> DASHATTACK = new AnimationAct<PatrolCaptain>("attack_4",30) {
        @Override
        public void tickUpdate(PatrolCaptain entity) {
            int tick = entity.getAnimationTick();
            LivingEntity target = entity.getTarget();
            float damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();

            if (target != null) {
                entity.getLookControl().setLookAt(target);
                if(tick==10){
                    entity.dashForward(8,0);
                    entity.setDeltaMovement(entity.getDeltaMovement().add(0,0.3,0));
                }
                if (target.distanceTo(entity) <= 2 + target.getBbWidth() / 2) {
                    if (tick == 19) {
                        target.hurt(entity.damageSources().mobAttack(entity), damage*2);
                    }
                }
            }
        }
    };

    public PatrolCaptain(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        if(pReason==MobSpawnType.NATURAL){
            if(!level().isClientSide){
                int count = 1+ getRandom().nextInt(3);
                for(int i=0;i<count;i++){
                    PatrolCaptain patrolCaptain = new PatrolCaptain(EntityHandle.PATROL_CAPTAIN.get(), level());
                    patrolCaptain.setPos(position());
                    level().addFreshEntity(patrolCaptain);
                }
            }
        }
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    public void tick() {
        super.tick();
        setYRot(yBodyRot);
    }

    private static final AnimationAct[] ANIMATIONS = new AnimationAct[]{NO_ANIMATION,ATTACK,DASHATTACK};
    @Override
    public AnimationAct[] getAnimations() {
        return ANIMATIONS;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2,new PatrolCaptainAttackAI(this));

        this.goalSelector.addGoal(7, new FFLookAtPlayerGoal<>(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new FFRandomLookAroundGoal<>(this));

        this.goalSelector.addGoal(8, new FFWaterAvoidingRandomStrollGoal<>(this,0.35));

        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Zombie.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, GuerrillasEntity.class, true));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    @Override
    protected <T extends GeoEntity> void basicAnimation(AnimationState<T> event) {
        if(event.isMoving())
            event.setAnimation(WALK_);
        else
            event.setAnimation(IDLE_);
    }
}
