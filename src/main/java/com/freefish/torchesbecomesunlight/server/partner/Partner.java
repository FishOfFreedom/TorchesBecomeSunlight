package com.freefish.torchesbecomesunlight.server.partner;

import com.freefish.rosmontislib.sync.ITagSerializable;
import com.freefish.torchesbecomesunlight.server.config.ConfigHandler;
import com.freefish.torchesbecomesunlight.server.partner.command.AttackTargetCommand;
import com.freefish.torchesbecomesunlight.server.partner.command.MoveToPosCommand;
import com.freefish.torchesbecomesunlight.server.partner.command.PartnerCommandManager;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class Partner<T extends Mob> implements ITagSerializable<CompoundTag> {
    private static final UUID HEALTH_CONFIG_MODIFIER_UUID = UUID.fromString("4579bbaa-d8b8-4a47-f126-b077ae1257e3");
    private static final UUID ATTACK_CONFIG_MODIFIER_UUID = UUID.fromString("c8ad7e2d-1c58-1725-c772-0eedc3be4a62");


    @Getter
    private final PartnerType<?> partnerType;
    public GoalSelector goalSelector;
    public GoalSelector targetSelector;
    @Getter
    protected final PartnerCommandManager skillManager = new PartnerCommandManager();
    private Player player;
    private T partnerMob;
    @Getter
    private boolean isInited;
    @Getter
    private boolean removed;
    private TargetChange targetChange;

    public UUID playerUUid,mobUUid;

    private int instancePosCount,instanceTargetCount;
    private Vec3 instancePos;
    private LivingEntity instanceTarget;

    public Partner(PartnerType<?> partnerType) {
        this.partnerType = partnerType;
    }

    public void removeFromWorld(){
    };

    @Nullable
    public Vec3 getInstancePos() {
        if(instancePosCount>0) {
            return instancePos;
        }else{
            return null;
        }
    }

    @Nullable
    public LivingEntity getInstanceTarget() {
        if(instanceTargetCount>0) {
            return instanceTarget;
        }else{
            return null;
        }
    }

    public void setInstancePos(Vec3 instancePos,int instancePosCount) {
        this.instancePos = instancePos;
        this.instancePosCount = instancePosCount;
    }

    public void setInstanceTarget(LivingEntity instanceTarget,int instanceTargetCount) {
        this.instanceTarget = instanceTarget;
        this.instanceTargetCount = instanceTargetCount;
    }

    public void init(Player player, T partnerMob){
        this.player = player;
        this.partnerMob = partnerMob;

        if(playerUUid==null){
            playerUUid = player.getUUID();
        }
        if(mobUUid==null){
            mobUUid = partnerMob.getUUID();
        }

        Level level = player.level();
        if(!level.isClientSide){
            this.goalSelector = new GoalSelector(level.getProfilerSupplier());
            this.targetSelector = new GoalSelector(level.getProfilerSupplier());
            registerGoal();
        }
        registerCommand();
        isInited = true;
        ConfigHandler.PartnerConfig partnerConfig = getPartnerConfig();
        if (partnerConfig != null) {
            AttributeInstance maxHealthAttr = partnerMob.getAttribute(Attributes.MAX_HEALTH);
            if (maxHealthAttr != null) {
                double difference = maxHealthAttr.getBaseValue() * partnerConfig.healthMultiplier.get() - maxHealthAttr.getBaseValue();
                maxHealthAttr.addTransientModifier(new AttributeModifier(HEALTH_CONFIG_MODIFIER_UUID, "Health config multiplier", difference, AttributeModifier.Operation.ADDITION));
                partnerMob.setHealth(partnerMob.getMaxHealth());
            }

            AttributeInstance attackDamageAttr = partnerMob.getAttribute(Attributes.ATTACK_DAMAGE);
            if (attackDamageAttr != null) {
                double difference = attackDamageAttr.getBaseValue() * partnerConfig.attackMultiplier.get() - attackDamageAttr.getBaseValue();
                attackDamageAttr.addTransientModifier(new AttributeModifier(ATTACK_CONFIG_MODIFIER_UUID, "Attack config multiplier", difference, AttributeModifier.Operation.ADDITION));
            }
        }
    }

    protected ConfigHandler.PartnerConfig getPartnerConfig() {
        return null;
    }

    public void aiStep(){
        T mob = getPartnerMob();
        Level level = mob.level();

        int i = level.getServer().getTickCount() + mob.getId();
        if (i % 2 != 0 && mob.tickCount > 1) {
            this.targetSelector.tickRunningGoals(false);
            this.goalSelector.tickRunningGoals(false);
        } else {
            this.targetSelector.tick();
            this.goalSelector.tick();
        }
    }

    public void tick(){
        if(targetChange!=null){
            targetChange.targetTime--;
            if (targetChange.targetTime <= 0) {
                targetChange = null;
            }
        }
    }

    public boolean playerHurt(LivingHurtEvent event){
        return true;
    }

    public void registerCommand(){
        skillManager.addCommand(new MoveToPosCommand<>(this));
        skillManager.addCommand(new AttackTargetCommand<>(this));
    }

    public boolean isReplaceGoal(){
        return true;
    }

    public void remove(){
        removed = true;
    }

    public void registerGoal(){
    }

    public LivingEntity getChangeTarget(){
        if(targetChange!=null)
            return targetChange.target;
        else
            return null;
    }

    public T getPartnerMob(){
        return  partnerMob;
    }

    public Player getPlayer(){
        return  player;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putUUID("player",playerUUid);
        compoundTag.putUUID("mob",mobUUid);

        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        playerUUid = compoundTag.getUUID("player");
        mobUUid = compoundTag.getUUID("mob");
    }

    private void setTargetChange(LivingEntity target,int targetTime){
        targetChange = new TargetChange(target,targetTime);
    }

    public class TargetChange{
        public TargetChange(LivingEntity target, int targetTime) {
            this.target = target;
            this.targetTime = targetTime;
        }

        public LivingEntity target;
        public int targetTime;
    }
}
