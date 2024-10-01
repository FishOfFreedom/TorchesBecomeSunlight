package com.freefish.torchesbecomesunlight.server.entity.guerrillas.shield;

import com.bobmowzie.mowziesmobs.client.particle.ParticleHandler;
import com.bobmowzie.mowziesmobs.client.particle.util.AdvancedParticleBase;
import com.bobmowzie.mowziesmobs.client.particle.util.ParticleComponent;
import com.bobmowzie.mowziesmobs.client.particle.util.RibbonComponent;
import com.freefish.torchesbecomesunlight.server.animation.AnimationAct;
import com.freefish.torchesbecomesunlight.server.animation.AnimationActHandler;
import com.freefish.torchesbecomesunlight.server.effect.EffectRegistry;
import com.freefish.torchesbecomesunlight.server.entity.EntityRegistry;
import com.freefish.torchesbecomesunlight.server.entity.ai.FFBodyRotationControl;
import com.freefish.torchesbecomesunlight.server.entity.ai.FFPathNavigateGround;
import com.freefish.torchesbecomesunlight.server.entity.ai.attribute.AttributeRegistry;
import com.freefish.torchesbecomesunlight.server.entity.ai.entity.patriot.PatriotAttackAI;
import com.freefish.torchesbecomesunlight.server.entity.effect.EntityFallingBlock;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.GuerrillasEntity;
import com.freefish.torchesbecomesunlight.server.entity.help.EntityCameraShake;
import com.freefish.torchesbecomesunlight.server.entity.projectile.HalberdOTIEntity;
import com.freefish.torchesbecomesunlight.server.sound.SoundRegistry;
import com.freefish.torchesbecomesunlight.server.util.AnimationWalk;
import com.freefish.torchesbecomesunlight.server.util.Parabola;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
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
            if (tick == 30) {
                entity.doRangeAttack(4.5,140,damage*1.5f,true);
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
            if (tick == 24) {
                entity.doRangeAttack(4.5,140,damage*1.5f,true);
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
                entity.doRangeAttack(4.5,140,damage*1.5f,true);
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
            if (target != null) {
                entity.getLookControl().setLookAt(target, 30F, 30F);
                entity.lookAt(target, 30F, 30F);
            } else {
                entity.setYRot(entity.yRotO);
            }
            if (tick == 4||tick == 10||tick == 16||tick == 22) {
                entity.doRangeAttack(6.5,50,damage*1.5f,true);
            }
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
            if (target != null) {
                entity.getLookControl().setLookAt(target, 30F, 30F);
                entity.lookAt(target, 30F, 30F);
            } else {
                entity.setYRot(entity.yRotO);
            }
            if (tick == 18||tick == 24||tick == 30||tick == 36) {
                entity.doRangeAttack(6.5,50,damage*1.5f,true);
            }
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
        public void tickUpdate(Patriot entity) {
            LivingEntity target = entity.getTarget();
            if(target!=null&&target.distanceTo(entity)<5+target.getBbWidth()/2)
                AnimationActHandler.INSTANCE.sendAnimationMessage(entity,Patriot.PIERCE1);
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
                List<LivingEntity> list = entity.level().getEntitiesOfClass(LivingEntity.class,entity.getBoundingBox().inflate(10),livingEntity ->
                        !(livingEntity instanceof GuerrillasEntity)&&livingEntity.distanceTo(entity)<6+livingEntity.getBbWidth()/2);
                for(LivingEntity entityHit:list) {
                    entityHit.hurt(entity.damageSources().mobAttack(entity), damage*2);
                    if (entityHit instanceof Player player) {
                        ItemStack pPlayerItemStack = player.getUseItem();
                        if (!pPlayerItemStack.isEmpty() && pPlayerItemStack.is(Items.SHIELD)) {
                            player.getCooldowns().addCooldown(Items.SHIELD, 100);
                            entity.level().broadcastEntityEvent(player, (byte) 30);
                        }
                    }
                }
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
                entity.doRangeAttack(40,4.5,damage*0.8f,true);
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
            if (tick == 24) {
                entity.doRangeAttack(40,4.5,damage*1.5f,true);
            }
            if(tick==30) entity.isCanBeAttacking = true;
        }

        @Override
        public void stop(Patriot entity) {
            AnimationActHandler.INSTANCE.sendAnimationMessage(entity,PROPEL3);
        }
    };
    public static final AnimationAct<Patriot> PROPEL3 = new AnimationAct<Patriot>("propel3",62){
        @Override
        public void tickUpdate(Patriot entity) {
            int tick = entity.getAnimationTick();
            entity.setYRot(entity.yRotO);
            entity.locateEntity();
            if(tick==8) entity.isCanBeAttacking = false;
            else if(tick==37) entity.isCanBeAttacking = true;
            else if(tick==50) entity.isCanBeAttacking = false;
        }
        @Override
        public void stop(Patriot entity) {
            entity.isCanBeAttacking = false;
            super.stop(entity);
        }
    };
    public static final AnimationAct<Patriot> SHIELD = new AnimationAct<Patriot>("shield_attack",20){
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
            if (tick == 10&&target != null) {
                if(target.distanceTo(entity)<4+target.getBbWidth()/2)
                    target.hurt(entity.damageSources().mobAttack(entity), damage*2);
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
                    guerrilla.addEffect(new MobEffectInstance(EffectRegistry.SONG_OF_GUERRILLA.get(),400,1));
                }
            }
        }
    };
    public static final AnimationAct<Patriot> HUNT = new AnimationAct<Patriot>("hunt",55){
        @Override
        public void start(Patriot entity) {
            AnimationWalk walk = new AnimationWalk(
                    new int[]{0,10,26,35,38,43,55},
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
            if (tick == 30) {
                entity.doRangeAttack(7,140,damage*2.5f,true,new MobEffectInstance(EffectRegistry.DEEP_FEAR.get()));
                entity.addEffect(new MobEffectInstance(EffectRegistry.WINDIGO.get()));
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
            double facingAngle = entity.yBodyRot;
            entity.setYRot(entity.yRotO);
            double maxDistance = 5d;
            Level world = entity.level();
            int tick = entity.getAnimationTick();
            entity.locateEntity();
            if (tick > 16 && tick <= 31){
                int hitY = Mth.floor(entity.getBoundingBox().minY - 0.5);
                if (tick == 17) {
                    entity.playSound(SoundEvents.GENERIC_EXPLODE, 2, 1F + entity.random.nextFloat() * 0.1F);
                    EntityCameraShake.cameraShake(world, entity.position(), 25, 0.1f, 0, 20);
                }
                if (tick % 2 == 0) {
                    int distance = (tick - 16) / 2;
                    double spread = Math.PI * 2;
                    int arcLen = Mth.ceil(distance * spread * 2);
                    double minY = entity.getBoundingBox().minY;
                    double maxY = entity.getBoundingBox().maxY;

                    for (int i = 0; i < arcLen; i++) {
                        double theta = (i / (arcLen - 1.0) - 0.5) * spread + facingAngle;
                        double vx = Math.cos(theta);
                        double vz = Math.sin(theta);
                        double px = entity.getX() + vx * distance;
                        double pz = entity.getZ() + vz * distance;
                        float factor = 1 - distance / (float) maxDistance;
                        AABB selection = new AABB(px - 1.5, minY, pz - 1.5, px + 1.5, maxY, pz + 1.5);
                        List<LivingEntity> hits = world.getEntitiesOfClass(LivingEntity.class, selection);
                        for (LivingEntity hit : hits) {
                            if (hit.onGround()) {
                                if (hit instanceof GuerrillasEntity) {
                                    continue;
                                }
                                float applyKnockbackResistance = 0;
                                hit.hurt(entity.damageSources().mobAttack(entity), (factor * 5 + 1));
                                applyKnockbackResistance = (float) hit.getAttribute(Attributes.KNOCKBACK_RESISTANCE).getValue();
                                double magnitude = entity.random.nextDouble() * 0.15 + 0.1;
                                float x = 0, y = 0, z = 0;
                                x += vx * factor * magnitude * (1 - applyKnockbackResistance);
                                y += 0.1 * (1 - applyKnockbackResistance) + factor * 0.15 * (1 - applyKnockbackResistance);
                                z += vz * factor * magnitude * (1 - applyKnockbackResistance);
                                hit.setDeltaMovement(hit.getDeltaMovement().add(x, y, z));
                                if (hit instanceof ServerPlayer player) {
                                    player.connection.send(new ClientboundSetEntityMotionPacket(entity));
                                }
                            }
                        }
                        if (world.random.nextBoolean()) {
                            int hitX = Mth.floor(px);
                            int hitZ = Mth.floor(pz);
                            BlockPos pos = new BlockPos(hitX, hitY, hitZ);
                            BlockPos abovePos = new BlockPos(pos).above();
                            BlockState block = world.getBlockState(pos);
                            BlockState blockAbove = world.getBlockState(abovePos);
                            if (!block.isAir() && block.isRedstoneConductor(world, pos) && !block.hasBlockEntity() && !blockAbove.blocksMotion()) {
                                EntityFallingBlock fallingBlock = new EntityFallingBlock(EntityRegistry.FALLING_BLOCK.get(), world, block, (float) (0.4 + factor * 0.2));
                                fallingBlock.setPos(hitX + 0.5, hitY + 1, hitZ + 0.5);
                                world.addFreshEntity(fallingBlock);
                            }
                        }
                    }
                }
            }
        }
        @Override
        public void stop(Patriot entity) {
            entity.isCanBeAttacking = false;
            super.stop(entity);
        }
    };
    private static final AnimationAct[] ANIMATIONS = {
            NO_ANIMATION,ATTACK1,ATTACK2,ATTACK3,PIERCE1,PIERCE2,RUN,CYCLE,SHIELD,THROW,STOMP,PROPEL1,PROPEL2,PROPEL3,STRENGTHEN,HUNT
    };

    public Parabola parabola = new Parabola();
    public boolean isCanBeAttacking = false;

    @OnlyIn(Dist.CLIENT)
    public Vec3[] clientVectors;

    private int timeSinceEnhanced = -1;
    private int maxEnhancedTime;
    private int enhancedTime;
    public int time=0;

    private static final float[][] ATTACK_BLOCK_OFFSETS = {
            {-0.1F, -0.1F},
            {-0.1F, 0.1F},
            {0.1F, 0.1F},
            {0.1F, -0.1F}
    };

    private static final EntityDataAccessor<Float> TARGET_POSX = SynchedEntityData.defineId(Patriot.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TARGET_POSY = SynchedEntityData.defineId(Patriot.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TARGET_POSZ = SynchedEntityData.defineId(Patriot.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> IS_RUNNING = SynchedEntityData.defineId(Patriot.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_ENHANCED = SynchedEntityData.defineId(Patriot.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> PREDICATE = SynchedEntityData.defineId(Patriot.class, EntityDataSerializers.INT);




    public Patriot(EntityType<? extends Patriot> entityType, Level level) {
        super(entityType, level);
        if (level().isClientSide)
            clientVectors = new Vec3[] {new Vec3(0, 0, 0), new Vec3(0, 0, 0)};
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new PatriotAttackAI(this));

        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this , 0.2));

        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Zombie.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Ravager.class, true));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
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

        if(getAnimation()==THROW||getAnimation()==PROPEL3){
            int tick = getAnimationTick();
            Vector3f targetPos = getTargetPos();
            if (tick == 36 || tick ==50) {
                parabola.mathParabola(this, targetPos);
            }
            if (this.level().isClientSide) {
                if (tick >= 36 && tick < 62) {
                    double i = ((double) (tick - 36)) / 27;
                    double i1 = ((double) (tick - 35)) / 27;
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
            if (tick == 58) {
                double i = ((double) (tick - 36)) / 27;
                double x3 = parabola.getX2() * i;
                double posX = getX() + parabola.getX() * i;
                double posZ = getZ() + parabola.getZ() * i;
                double posY = getY() + parabola.getY(x3);
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

        if(getIsEnhanced())
            setDeltaMovement(0, getDeltaMovement().y, 0);

        float moveX = (float) (getX() - xo);
        float moveZ = (float) (getZ() - zo);
        float speed = Mth.sqrt(moveX * moveX + moveZ * moveZ);
        if(this.level().isClientSide && tickCount % 10 == 1 && speed > 0.03 && getAnimation() == NO_ANIMATION)
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundRegistry.GIANT_STEP.get(), this.getSoundSource(), 20F, 1F, false);
        if(getHealth() <=getMaxHealth() && getPredicate()==1) {
            if(!level().isClientSide){
                setPredicateEffect(2);
            }
        }
        if(!level().isClientSide) {
            if(getHealth()>getMaxHealth()&&getPredicate()==2)
                setPredicateEffect(1);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        Entity entitySource = source.getDirectEntity();
        if (entitySource != null){
            return attackWithShield(source, amount);
        }
        else if (source.isCreativePlayer()) {
            return super.hurt(source, amount);
        }
        return false;
    }

    @Override
    public AnimationAct getDeathAnimation() {
        return null;
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
                    if(getAnimation()==NO_ANIMATION&&random.nextInt(2)==0)
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
        return GuerrillasEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 500.0D)
                .add(Attributes.ATTACK_DAMAGE, 30.0f)
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
        if (true) {
            if (event.isMoving())
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
        return SoundRegistry.PATRIOT_UNYIELDING.get();
    }

    @Override
    protected boolean canPlayMusic() {
        return super.canPlayMusic() && getPredicate()==1;
    }

    private void addShieldArmorParticle(){
        if(level().isClientSide&&getAnimation() == NO_ANIMATION) {
            Vec3 motion = getDeltaMovement().scale(2);
            Vec3 shieldVector = clientVectors[1];
            double yaw = -this.yBodyRot / 180 * Math.PI;
            AdvancedParticleBase.spawnParticle(level(), ParticleHandler.RING2.get(), shieldVector.x, shieldVector.y - 1, shieldVector.z, motion.x, 0, motion.z, false, yaw, 0, 0, 0, 1.5F, 204 / 255f, 0f, 0f, 1, 1, 20, true, false, new ParticleComponent[]{
                    new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, ParticleComponent.KeyTrack.startAndEnd(1f, 0f), false)
            });
        }
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
        if(getIsRunning() == isRunning) return;
        if(!level().isClientSide) {
            AttributeInstance speedAttribute = getAttribute(Attributes.MOVEMENT_SPEED);
            if (speedAttribute != null) {
                if (isRunning) {
                    speedAttribute.addTransientModifier(new AttributeModifier("speed", 0.2F, AttributeModifier.Operation.ADDITION));
                } else {
                    speedAttribute.addTransientModifier(new AttributeModifier("speed", -0.2F, AttributeModifier.Operation.ADDITION));
                }
            }
        }
        this.entityData.set(IS_RUNNING, isRunning);
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

}
