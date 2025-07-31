package com.freefish.torchesbecomesunlight.server.entity.dlc;

import com.freefish.torchesbecomesunlight.server.block.blockentity.BigBenBlockEntity;
import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.PlayerCapability;
import com.freefish.torchesbecomesunlight.server.entity.ai.FFBodyRotationControl;
import com.freefish.torchesbecomesunlight.server.entity.ai.FFPathNavigateGround;
import com.freefish.torchesbecomesunlight.server.entity.ai.HurtByTargetNoPlayerGoal;
import com.freefish.torchesbecomesunlight.server.entity.ai.entity.PathfinderBallistariusAttackAI;
import com.freefish.torchesbecomesunlight.server.entity.dlc.ai.PathfinderAutoDialogueGoal;
import com.freefish.torchesbecomesunlight.server.entity.effect.StompEntity;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.GuerrillasEntity;
import com.freefish.torchesbecomesunlight.server.entity.projectile.Bullet;
import com.freefish.torchesbecomesunlight.server.init.SoundHandle;
import com.freefish.torchesbecomesunlight.server.story.IDialogueEntity;
import com.freefish.torchesbecomesunlight.server.story.PlayerStoryStoneData;
import com.freefish.torchesbecomesunlight.server.story.data.Dialogue;
import com.freefish.torchesbecomesunlight.server.story.data.DialogueEntry;
import com.freefish.torchesbecomesunlight.server.story.dialogueentity.DialogueEntity;
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

public class PathfinderBallistarius extends GunKnightEntity implements IDialogueEntity {
    public static final AnimationAct<PathfinderBallistarius> ATTACK = new AnimationAct<PathfinderBallistarius>("attack",33){

        @Override
        public void tickUpdate(PathfinderBallistarius entity) {
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
    public static final AnimationAct<PathfinderBallistarius> SHOOT = new AnimationAct<PathfinderBallistarius>("shoot",45){

        @Override
        public void tickUpdate(PathfinderBallistarius entity) {
            int tick = entity.getAnimationTick();
            LivingEntity target = entity.getTarget();
            if(target!=null&&tick<=3)
                entity.lookAtEntity(target);
            else {
                entity.getLookControl().setLookAt(entity.artilleryForecastPos);
            }
            if(tick==10){
                entity.setArtilleryForecastPos(target,entity.getShootPos());
            }
            else if(tick==20){
                entity.playSound(SoundHandle.ARTILLERY.get(), 1.5F, 1.0F / (entity.getRandom().nextFloat() * 0.4F + 0.8F));
                entity.shootArtilleryBullet(target,entity.getShootPos());
            }
        }
    };
    public static final AnimationAct<PathfinderBallistarius> ATTACK2 = new AnimationAct<PathfinderBallistarius>("attack2",45){

        @Override
        public void tickUpdate(PathfinderBallistarius entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            LivingEntity target = entity.getTarget();
            if (target != null&&(tick>20||tick<8)) {
                entity.getLookControl().setLookAt(target, 30F, 30F);
                entity.lookAt(target, 30F, 30F);
            } else {
                entity.setYRot(entity.yRotO);

            }
            if(tick == 12) {
                //entity.playSound(SoundHandle.AXE_SWEPT.get(), 1.0F, 1.0F / (entity.random.nextFloat() * 0.4F + 0.8F));
                entity.dashForward(4,0);
            }else if (tick == 15) {
                entity.doRangeAttack(2.5,30,damage,true);
                FFEntityUtils.doRangeAttackFX(entity,2.5,30,0);
            }else if (tick == 32) {
                entity.doCycleAttack(4,damage);
                FFEntityUtils.doRangeAttackFX(entity,4,140,0);
            }

            if(tick == 6) entity.isShield = false;
            else if(tick == 30) entity.isShield = true;
        }
    };
    public static final AnimationAct<PathfinderBallistarius> BACK = new AnimationAct<PathfinderBallistarius>("back",25){

        @Override
        public void tickUpdate(PathfinderBallistarius entity) {
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
    public static final AnimationAct<PathfinderBallistarius> ATTACK3 = new AnimationAct<PathfinderBallistarius>("attack3",37){

        @Override
        public void tickUpdate(PathfinderBallistarius entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            LivingEntity target = entity.getTarget();
            if (target != null&&tick<15) {
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
    public static final AnimationAct<PathfinderBallistarius> RUN_ATTACK3 = new AnimationAct<PathfinderBallistarius>("runattack3",23){

        @Override
        public void tickUpdate(PathfinderBallistarius entity) {
            int tick = entity.getAnimationTick();
            float damage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            LivingEntity target = entity.getTarget();
            if (target != null&&tick<1) {
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
    public static final AnimationAct<PathfinderBallistarius> RUN = new  AnimationAct<PathfinderBallistarius>("run",200){
        @Override
        public void start(PathfinderBallistarius entity) {
            if(entity.getTarget()!=null)
                entity.getNavigation().moveTo(entity.getTarget(),0.8);
        }

        @Override
        public void tickUpdate(PathfinderBallistarius entity) {
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
    public static final AnimationAct<PathfinderBallistarius> SHIELD = new AnimationAct<PathfinderBallistarius>("shield_attack",25){

        @Override
        public void tickUpdate(PathfinderBallistarius entity) {
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
    public static final AnimationAct<PathfinderBallistarius> STRENGTHEN = new AnimationAct<PathfinderBallistarius>("strengthen",30){
        @Override
        public void tickUpdate(PathfinderBallistarius entity) {
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
    public static final AnimationAct<PathfinderBallistarius> STOMP = new AnimationAct<PathfinderBallistarius>("stomp",31){
        @Override
        public void tickUpdate(PathfinderBallistarius entity) {
            entity.setYRot(entity.yRotO);
            int tick = entity.getAnimationTick();
            entity.locateEntity();
            if (tick == 17){
                StompEntity stompEntity = new StompEntity(entity.level(),12,entity,3);
                stompEntity.setPos(entity.position());
                entity.level().addFreshEntity(stompEntity);
            }
        }
    };
    public static final AnimationAct<PathfinderBallistarius> BREAK = new AnimationAct<PathfinderBallistarius>("break_start",30,1){
        @Override
        public void tickUpdate(PathfinderBallistarius entity) {
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
    //public static final AnimationAct<FrostNova> DIE = new AnimationAct<FrostNova>("death",45,1);
    //public ShieldGuard(EntityType<? extends GuerrillasEntity> entityType, Level level) {
    //    super(entityType, level);
    //}

    private static AnimationAct[] ANIMATIONS = new AnimationAct[]{NO_ANIMATION,BREAK,ATTACK,SHOOT,ATTACK2,ATTACK3, RUN_ATTACK3,BACK,RUN,SHIELD,STRENGTHEN,STOMP};

    public PathfinderBallistarius(EntityType<? extends PathfinderBallistarius> entityType, Level level) {
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
        this.goalSelector.addGoal(1, new PathfinderAutoDialogueGoal(this));
        this.goalSelector.addGoal(2, new PathfinderBallistariusAttackAI(this));

        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Zombie.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Ravager.class, true));
        this.targetSelector.addGoal(3, new HurtByTargetNoPlayerGoal(this));
    }

    @Override
    public void playDeathAnimationPre(DamageSource source) {
        BigBenBlockEntity tile1 = getTile();
        if(tile1!=null&&tile1.isChallengeType(BigBenBlockEntity.ChallengeType.PATH)){
            setHealth(1);
            tile1.endChallenge();
            AnimationActHandler.INSTANCE.sendAnimationMessage(this,BREAK);
        }
    }

    public boolean isShield;

    @Override
    public boolean hurt(DamageSource source, float damage) {
        Entity entitySource = source.getEntity();

        if(getAnimation()==BREAK) return false;
        if(source.getEntity() instanceof Turret) return false;

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
        return GuerrillasEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 200.0D)
                .add(Attributes.ATTACK_DAMAGE, 16.0f)
                .add(Attributes.ARMOR, 12.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 8.0D)
                .add(Attributes.FOLLOW_RANGE, 48)
                .add(Attributes.KNOCKBACK_RESISTANCE,1f);
    }

    @Override
    public void tick() {
        super.tick();

        if(level().isClientSide){
            int animationTick = getAnimationTick();
            AnimationAct animation = getAnimation();
            if(animation==ATTACK2){
                if(animationTick==32){

                }
            }
        }
    }

    public Vec3 getShootPos(){
        return new Vec3(-0.76, 1.75, 3.3).yRot((float) (-getYRot() / 180 * Math.PI)).add(position());
    }

    public Vec3 artilleryForecastPos = Vec3.ZERO;

    public void setArtilleryForecastPos(LivingEntity target,Vec3 vec3) {
        if(target==null) return;
        float speed = 60f;

        double d0 = target.getX()  - vec3.x;
        double d2 = target.getZ()  - vec3.z;
        float dist = (float) (Math.sqrt(d0 * d0 + d2 * d2));
        float time = dist/speed+0.5f;

        artilleryForecastPos = getTargetMoveVec(target).scale(time).add(target.position());
    }

    public void shootArtilleryBullet(LivingEntity target,Vec3 vec3) {
        if(target==null||artilleryForecastPos==Vec3.ZERO) return;

        double d0 = target.getX()  - vec3.x;
        double d2 = target.getZ()  - vec3.z;
        float dist = (float) (Math.sqrt(d0 * d0 + d2 * d2));

        Bullet abstractarrow = new Bullet(level(),this,1);
        abstractarrow.setPos(vec3);

        Vec3 move = (new Vec3(artilleryForecastPos.x-vec3.x,0,artilleryForecastPos.z-vec3.z)).normalize().scale(3);
        this.level().addFreshEntity(abstractarrow);
        abstractarrow.shoot(move.x, Math.sqrt(dist)/60 , move.z, 0);
    }

    @Override
    public void die(DamageSource pDamageSource) {
        BigBenBlockEntity tile = getTile();
        if(tile==null||tile.challengePlayer==null||!tile.challengePlayer.isAlive()) {
            super.die(pDamageSource);
        }
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
    //Dialogue
    private DialogueEntity dialogueEntity;

    @Override
    public boolean canDialogue() {
        return getSpawnPos()!=null&&!isAggressive()&&getDialogueEntity()==null;
    }

    @Override
    public void startDialogue(Player player) {
        BigBenBlockEntity tile = getTile();
        PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(player, CapabilityHandle.PLAYER_CAPABILITY);
        if(tile!=null&&capability!=null){
            PlayerStoryStoneData playerStory = capability.getPlayerStory();
            if(playerStory.isSeenGunPatriot()) {
                DialogueEntity dialogueEntity1 = startTalk("dialogue/gun_patriot/pathfinder_fight.json", this, player);
                Dialogue dialogue = dialogueEntity1.getAllDialogue();
                DialogueEntry dialogueEntry = dialogue.getDialogueEntry("main2");
                dialogueEntry.setRunnable(() -> {
                    tile.startChallengePlayer(this, BigBenBlockEntity.ChallengeType.PATH,player);
                });
            }else {
                DialogueEntity dialogueEntity1 = startTalk("dialogue/gun_patriot/pathfinder_findgun.json", this, player);
            }
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
}
