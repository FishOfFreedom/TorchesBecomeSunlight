package com.freefish.torchesbecomesunlight.server.entity.rhodesIsland;

import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.PlayerCapability;
import com.freefish.torchesbecomesunlight.server.entity.ai.FFLookAtPlayerTotherGoal;
import com.freefish.torchesbecomesunlight.server.entity.ai.FFRandomLookAroundGoal;
import com.freefish.torchesbecomesunlight.server.entity.rhodesIsland.ai.PrearatonOpAutoDialogueGoal;
import com.freefish.torchesbecomesunlight.server.entity.rhodesIsland.ai.PreparationOpAttackAI;
import com.freefish.torchesbecomesunlight.server.entity.rhodesIsland.rosmontis.Rosmontis;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.partner.PartnerUtil;
import com.freefish.torchesbecomesunlight.server.story.IDialogueEntity;
import com.freefish.torchesbecomesunlight.server.story.PlayerStoryStoneData;
import com.freefish.torchesbecomesunlight.server.story.dialogueentity.DialogueEntity;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationAct;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;

public class PreparationOp extends RhodesIslandEntity implements IDialogueEntity , IEntityAdditionalSpawnData {
    public static final RawAnimation WALK_ = RawAnimation.begin().then("walk", Animation.LoopType.LOOP);
    public static final RawAnimation IDLE_ = RawAnimation.begin().then("idle", Animation.LoopType.LOOP);

    public static final AnimationAct<PreparationOp> ATTACK = new AnimationAct<PreparationOp>("attack_3",26) {
        @Override
        public void tickUpdate(PreparationOp entity) {
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
    public static final AnimationAct<PreparationOp> DASHATTACK = new AnimationAct<PreparationOp>("attack_4",30) {
        @Override
        public void tickUpdate(PreparationOp entity) {
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

    private static final AnimationAct[] ANIMATIONS = new AnimationAct[]{NO_ANIMATION,ATTACK,DASHATTACK};
    @Override
    public AnimationAct[] getAnimations() {
        return ANIMATIONS;
    }

    public PreparationOp(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new PrearatonOpAutoDialogueGoal(this));
        this.goalSelector.addGoal(2,new PreparationOpAttackAI(this));

        this.goalSelector.addGoal(7, new FFLookAtPlayerTotherGoal<>(this, Player.class, 5.0F));
        this.goalSelector.addGoal(6, new FFRandomLookAroundGoal<>(this));

        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Zombie.class, true));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        CompoundTag tag = new CompoundTag();
        BlockPos spawnPos = getSpawnPos();
        if(spawnPos!=null){
            tag.put("spawn", NbtUtils.writeBlockPos(spawnPos));
        }
        buffer.writeNbt(tag);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        CompoundTag compoundTag = additionalData.readNbt();
        if(compoundTag.contains("spawn")){
            init(NbtUtils.readBlockPos(compoundTag.getCompound("spawn")));
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public boolean trySummonRosmontis(BlockPos blockPos){
        Iterable<Entity> all = ((ServerLevel) level()).getEntities().getAll();
        Rosmontis rosmontis = null;
        boolean isInLevel = false;

        for(Entity entity:all){
            if(entity instanceof Rosmontis r){
                rosmontis = r;
                isInLevel = true;
            }
        }

        if(rosmontis!=null&&(rosmontis.isOnStoryGround|| PartnerUtil.getPartner(rosmontis) !=null))
            return false;

        if(rosmontis == null){
            rosmontis = new Rosmontis(EntityHandle.ROSMONTIS.get(),level());
        }

        rosmontis.init(blockPos);
        rosmontis.setPos(new Vec3(blockPos.getX()+0.5,blockPos.getY(),blockPos.getZ()+0.5));

        if(!isInLevel){
            level().addFreshEntity(rosmontis);
        }
        return true;
    }

    @Override
    public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
        return false;
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        if(pReason==MobSpawnType.STRUCTURE){
            init(blockPosition());
        }
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    protected <T extends GeoEntity> void basicAnimation(AnimationState<T> event) {
        if(event.isMoving())
            event.setAnimation(WALK_);
        else
            event.setAnimation(IDLE_);
    }

    //Dialogue
    private DialogueEntity dialogueEntity;

    @Override
    public boolean canDialogue() {
        BlockPos spawnPos = getSpawnPos();
        return spawnPos!=null&&!isAggressive()&&getDialogueEntity()==null;
    }

    @Override
    public void startDialogue(Player player) {
        BlockPos tile = getSpawnPos();
        PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(player, CapabilityHandle.PLAYER_CAPABILITY);
        if(tile!=null&&capability!=null){
            PlayerStoryStoneData playerStory = capability.getPlayerStory();
            DialogueEntity dialogueEntity1 = startTalk("dialogue/rhodesisland/op_find_rosmontis.json", this, player);
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
