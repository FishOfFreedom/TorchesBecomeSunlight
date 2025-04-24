package com.freefish.torchesbecomesunlight.server.entity;

import com.freefish.torchesbecomesunlight.client.sound.BossMusicPlayer;
import com.freefish.torchesbecomesunlight.server.config.ConfigHandler;
import com.freefish.torchesbecomesunlight.server.entity.ai.entity.WhileDialogueAI;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.GuerrillasEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

public abstract class FreeFishEntity extends PathfinderMob {
    private static final byte MUSIC_PLAY_ID = 67;
    private static final byte MUSIC_STOP_ID = 68;
    private static final byte MAKE_PARTICLE_ID = 60;

    private static final UUID HEALTH_CONFIG_MODIFIER_UUID = UUID.fromString("45ba79ba-88db-4a47-f126-b7a7e107e253");
    private static final UUID ATTACK_CONFIG_MODIFIER_UUID = UUID.fromString("c7e8da2d-1c58-1725-c277-0d3ebece4a62");

    public FreeFishEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);

        ConfigHandler.CombatConfig combatConfig = getCombatConfig();
        if (combatConfig != null) {
            AttributeInstance maxHealthAttr = getAttribute(Attributes.MAX_HEALTH);
            if (maxHealthAttr != null) {
                double difference = maxHealthAttr.getBaseValue() * getCombatConfig().healthMultiplier.get() - maxHealthAttr.getBaseValue();
                maxHealthAttr.addTransientModifier(new AttributeModifier(HEALTH_CONFIG_MODIFIER_UUID, "Health config multiplier", difference, AttributeModifier.Operation.ADDITION));
                this.setHealth(this.getMaxHealth());
            }

            AttributeInstance attackDamageAttr = getAttribute(Attributes.ATTACK_DAMAGE);
            if (attackDamageAttr != null) {
                double difference = attackDamageAttr.getBaseValue() * getCombatConfig().attackMultiplier.get() - attackDamageAttr.getBaseValue();
                attackDamageAttr.addTransientModifier(new AttributeModifier(ATTACK_CONFIG_MODIFIER_UUID, "Attack config multiplier", difference, AttributeModifier.Operation.ADDITION));
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if(!level().isClientSide){
        }
        if (!level().isClientSide && getBossMusic() != null) {
            if (canPlayMusic()) {
                this.level().broadcastEntityEvent(this, MUSIC_PLAY_ID);
            }
            else {
                this.level().broadcastEntityEvent(this, MUSIC_STOP_ID);
            }
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1,new WhileDialogueAI(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        yRotO = getYRot();
        super.readAdditionalSaveData(pCompound);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == MUSIC_PLAY_ID) {
            BossMusicPlayer.playBossMusic(this);
        }
        else if (id == MUSIC_STOP_ID) {
            BossMusicPlayer.stopBossMusic(this);
        }if (id == MAKE_PARTICLE_ID) {
            makeParticles();
        }
        else super.handleEntityEvent(id);
    }

    protected void makeParticles() {
        for (int i = 0; i < 20; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;
            this.level().addParticle(ParticleTypes.POOF, this.getRandomX(1.0D), this.getRandomY(), this.getRandomZ(1.0D), d0, d1, d2);
        }
    }

    public boolean hasBossBar() {
        return false;
    }

    public BossEvent.BossBarColor bossBarColor() {
        return BossEvent.BossBarColor.PURPLE;
    }

    public void absFaceEntity(Entity entity){
        this.getLookControl().setLookAt(entity);
        setYRot((float)(Mth.atan2(getX()-entity.getX(), entity.getZ()-getZ()) * (double)(180F / (float)Math.PI)));
    }

    public void doRangeTrueAttack(double range, double arc,float damage,boolean isBreakingShield){
        List<LivingEntity> entitiesHit = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(range+5, 3, range+5), e -> e != this && distanceTo(e) <= range + e.getBbWidth() / 2f && e.getY() <= getY() + 3);
        for (LivingEntity entityHit : entitiesHit) {
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
                if(!(entityHit instanceof Player player&& player.isCreative())) {
                    entityHit.actuallyHurt(damageSources().mobAttack(this), damage);
                    doHurtEntity(entityHit,damageSources().mobAttack(this),damage);
                }
            }
        }
    }

    public void doRangeKnockBack(double range, double arc,float knockback){
        List<LivingEntity> entitiesHit = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(range+5, 3, range+5), e -> e != this && distanceTo(e) <= range + e.getBbWidth() / 2f && e.getY() <= getY() + 3);
        for (LivingEntity entityHit : entitiesHit) {
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
                Vec3 direction = new Vec3(0, knockback*0.1, knockback).yRot((float) ((-getYRot()) / 180 * org.joml.Math.PI));
                entityHit.setDeltaMovement(entityHit.getDeltaMovement().x + direction.x, entityHit.getDeltaMovement().y+ direction.y, entityHit.getDeltaMovement().z + direction.z);
                entityHit.move(MoverType.SELF,entityHit.getDeltaMovement());
            }
        }
    }

    public void doCycleAttack(float range,float damage){
        List<LivingEntity> list = level().getEntitiesOfClass(LivingEntity.class,getBoundingBox().inflate(range+5),livingEntity ->
                !(livingEntity instanceof GuerrillasEntity)&&livingEntity.distanceTo(this)<range+livingEntity.getBbWidth()/2);
        for(LivingEntity entityHit:list) {
            if(entityHit == this) return;
            doHurtEntity(entityHit,damageSources().mobAttack(this),damage);
            if (entityHit instanceof Player player) {
                ItemStack pPlayerItemStack = player.getUseItem();
                if (!pPlayerItemStack.isEmpty() && pPlayerItemStack.is(Items.SHIELD)) {
                    player.getCooldowns().addCooldown(Items.SHIELD, 100);
                    level().broadcastEntityEvent(player, (byte) 30);
                }
            }
        }
    }

    public boolean doRangeAttack(double range, double arc,float damage,boolean isBreakingShield){
        boolean flag = false;
        List<LivingEntity> entitiesHit = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(range+5, 3, range+5), e -> e != this && distanceTo(e) <= range + e.getBbWidth() / 2f && e.getY() <= getY() + 3);
        for (LivingEntity entityHit : entitiesHit) {
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
                if(doHurtEntity(entityHit,damageSources().mobAttack(this),damage)) {
                    flag = true;
                }
                if(isBreakingShield&&entityHit instanceof Player player){
                    ItemStack pPlayerItemStack = player.getUseItem();
                    if (!pPlayerItemStack.isEmpty() && pPlayerItemStack.is(Items.SHIELD)) {
                        player.getCooldowns().addCooldown(Items.SHIELD, 100);
                        player.stopUsingItem();
                        this.level().broadcastEntityEvent(player, (byte)30);
                    }
                }
            }
        }
        return  flag;
    }

    public boolean doRangeAttackAngle(double range, double arc,float damage,float yRot,boolean isBreakingShield){
        boolean flag = false;
        List<LivingEntity> entitiesHit = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(range+5, 3, range+5), e -> e != this && distanceTo(e) <= range + e.getBbWidth() / 2f && e.getY() <= getY() + 3);
        for (LivingEntity entityHit : entitiesHit) {
            float entityHitAngle = (float) ((Math.atan2(entityHit.getZ() - getZ(), entityHit.getX() - getX()) * (180 / Math.PI) - 90)+ yRot % 360) ;
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
                if(doHurtEntity(entityHit,damageSources().mobAttack(this),damage)) {
                    flag = true;
                }
                if(isBreakingShield&&entityHit instanceof Player player){
                    ItemStack pPlayerItemStack = player.getUseItem();
                    if (!pPlayerItemStack.isEmpty() && pPlayerItemStack.is(Items.SHIELD)) {
                        player.getCooldowns().addCooldown(Items.SHIELD, 100);
                        player.stopUsingItem();
                        this.level().broadcastEntityEvent(player, (byte)30);
                    }
                }
            }
        }
        return  flag;
    }

    public void lookAtEntity(Entity livingEntity){
        getLookControl().setLookAt(livingEntity,30f,30f);
        lookAt(livingEntity,30f,30f);
    }

    public void dashForward(float maxLen,float yawOffset){
        float jumpLen;
        LivingEntity target = getTarget();
        if(target!=null){
            float dist = target.distanceTo(this);
            if(dist<=maxLen) jumpLen = dist/4f;
            else jumpLen = maxLen / 4;
        } else {
            jumpLen = maxLen / 4;
        }
        Vec3 direction = new Vec3(0, Math.sqrt(jumpLen)*0.1, jumpLen).yRot((float) (yawOffset-getYRot() / 180 * org.joml.Math.PI));
        setDeltaMovement(direction);
    }

    public void dashForwardContinue(float maxLen,float yawOffset){
        float jumpLen;
        LivingEntity target = getTarget();
        if(target!=null){
            float dist = target.distanceTo(this);
            if(dist<=maxLen) jumpLen = dist/4f;
            else jumpLen = maxLen / 4;
        } else {
            jumpLen = maxLen / 4;
        }
        Vec3 direction = new Vec3(0, 0, jumpLen).yRot((float) (yawOffset-getYRot() / 180 * org.joml.Math.PI));
        setDeltaMovement(direction);
    }

    public void dashForwardContinueNoTarget(float maxLen,float yawOffset){
        float jumpLen = maxLen / 4;
        Vec3 direction = new Vec3(0, 0, jumpLen).yRot((float) (yawOffset-getYRot() / 180 * org.joml.Math.PI));
        setDeltaMovement(direction);
    }

    public boolean doHurtEntity(LivingEntity livingEntity,DamageSource source,float damage){
        return livingEntity.hurt(source,damage);
    }
//todo
    public void setForwardMotion(float speed) {
        this.setDeltaMovement(new Vec3(0, getDeltaMovement().y, speed).yRot((float) (-this.getYRot() / 180 * Math.PI)));
    }

    public void locateEntity(){
        this.setDeltaMovement(0, this.onGround() ? 0 : this.getDeltaMovement().y, 0);
        this.setPos(xo, this.onGround() ? yo : getY(), zo);
    }

    public SoundEvent getBossMusic() {
        return null;
    }

    protected boolean canPlayMusic() {
        return !isSilent() && getTarget() instanceof Player;
    }

    public boolean canPlayerHearMusic(Player player) {
        return player != null
                && canAttack(player)
                && distanceTo(player) < 2500;
    }

    public double getAngleBetweenEntities(Entity first, Entity second) {
        return Math.atan2(second.getZ() - first.getZ(), second.getX() - first.getX()) * (180 / Math.PI) + 90;
    }

    protected void repelEntities(float x, float y, float z, float radius) {
        List<LivingEntity> nearbyEntities = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(x, y, z), e -> e != this && distanceTo(e) <= radius + e.getBbWidth() / 2f && e.getY() <= getY() + y);
        for (Entity entity : nearbyEntities) {
            if (entity.isPickable() && !entity.noPhysics) {
                double angle = (getAngleBetweenEntities(this, entity) + 90) * Math.PI / 180;
                entity.setDeltaMovement(-0.1 * Math.cos(angle), entity.getDeltaMovement().y, -0.1 * Math.sin(angle));
            }
        }
    }
//todo
    protected boolean canBePushedByEntity(Entity entity) {
        return true;
    }

    protected ConfigHandler.SpawnConfig getSpawnConfig() {
        return null;
    }

    protected ConfigHandler.CombatConfig getCombatConfig() {
        return null;
    }

    @Override
    public void push(Entity entityIn) {
        if (!this.isSleeping()) {
            if (!this.isPassengerOfSameVehicle(entityIn)) {
                if (!entityIn.noPhysics && !this.noPhysics) {
                    double d0 = entityIn.getX() - this.getX();
                    double d1 = entityIn.getZ() - this.getZ();
                    double d2 = Mth.absMax(d0, d1);
                    if (d2 >= (double) 0.01F) {
                        d2 = Math.sqrt(d2);
                        d0 = d0 / d2;
                        d1 = d1 / d2;
                        double d3 = 1.0D / d2;
                        if (d3 > 1.0D) {
                            d3 = 1.0D;
                        }

                        d0 = d0 * d3;
                        d1 = d1 * d3;
                        d0 = d0 * (double) 0.05F;
                        d1 = d1 * (double) 0.05F;
                        if (!this.isVehicle()) {
                            if (canBePushedByEntity(entityIn)) {
                                this.push(-d0, 0.0D, -d1);
                            }
                        }
                        if (!entityIn.isVehicle()) {
                            entityIn.push(d0, 0.0D, d1);
                        }
                    }
                }
            }
        }
    }

    public static boolean spawnPredicate(EntityType type, LevelAccessor world, MobSpawnType reason, BlockPos spawnPos, RandomSource rand) {
        return true;
    }
}
