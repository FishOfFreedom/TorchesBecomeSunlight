package com.freefish.torchesbecomesunlight.server.partner.self;

import com.freefish.torchesbecomesunlight.server.config.ConfigHandler;
import com.freefish.torchesbecomesunlight.server.entity.ai.FFRandomLookAroundGoal;
import com.freefish.torchesbecomesunlight.server.entity.ai.FFWaterAvoidingRandomStrollGoal;
import com.freefish.torchesbecomesunlight.server.entity.ai.entity.rhodes.RosmontisAttackAI;
import com.freefish.torchesbecomesunlight.server.entity.rhodesIsland.rosmontis.Rosmontis;
import com.freefish.torchesbecomesunlight.server.partner.Partner;
import com.freefish.torchesbecomesunlight.server.partner.PartnerHandler;
import com.freefish.torchesbecomesunlight.server.partner.ai.HurtByPartnerTargetGoal;
import com.freefish.torchesbecomesunlight.server.partner.command.CommandIcon;
import com.freefish.torchesbecomesunlight.server.partner.command.ros.RosmontisTargetVecAnimationCommand;
import com.freefish.torchesbecomesunlight.server.partner.command.ros.RosmontisVecAnimationCommand;
import com.freefish.torchesbecomesunlight.server.partner.goal.FollowPlayerGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class RosmontisPartner extends Partner<Rosmontis> {
    public int continueTime;

    public RosmontisPartner() {
        super(PartnerHandler.ROSMONTIS_PARTNER);
    }

    @Override
    public void removeFromWorld() {
        Rosmontis partnerMob = getPartnerMob();
        if(partnerMob!=null){
            partnerMob.discard();
        }
    }

    @Override
    public void tick() {
        super.tick();
        Level level = getPartnerMob().level();
        Rosmontis partnerMob = getPartnerMob();

        continueTime++;
        if(continueTime>6000){
            if(!level.isClientSide){
                remove();
                partnerMob.discard();
            }else {
                remove();
            }
        }
    }

    @Override
    protected ConfigHandler.PartnerConfig getPartnerConfig() {
        return ConfigHandler.COMMON.MOBS.ROSMONTIS.partnerConfig;
    }

    @Override
    public boolean playerHurt(LivingHurtEvent event) {
        Rosmontis partnerMob = getPartnerMob();
        Player player = getPlayer();
        float amount = event.getAmount();

        Rosmontis.SummonArmorRegion summonArmorRegion = partnerMob.summonArmorRegion;

        if(summonArmorRegion!=null) {
            if(summonArmorRegion.position.subtract(player.position()).length()<8){
                amount*=0.6f;
                event.setAmount(amount);
            }
        }

        return super.playerHurt(event);
    }

    @Override
    public CompoundTag serializeNBT() {
        return super.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        super.deserializeNBT(compoundTag);
    }

    @Override
    public void registerCommand() {
        super.registerCommand();
        this.skillManager.addCommand(new RosmontisVecAnimationCommand(this, CommandIcon.ROS_ARMOR,Rosmontis.SKILL_ARMOR));
        this.skillManager.addCommand(new RosmontisVecAnimationCommand(this, CommandIcon.ROS_REMOTE_2,Rosmontis.REMOTE_2));
        this.skillManager.addCommand(new RosmontisTargetVecAnimationCommand(this, CommandIcon.ROS_SKILL4,Rosmontis.SKILL_4));
    }

    @Override
    public void registerGoal() {
        Rosmontis partnerMob = getPartnerMob();
        this.goalSelector.addGoal(0, new FloatGoal(partnerMob));
        this.goalSelector.addGoal(3,new FollowPlayerGoal(getPartnerMob(),getPlayer(),0.31,8));
        this.goalSelector.addGoal(2,new RosmontisAttackAI(partnerMob));

        this.goalSelector.addGoal(6, new FFRandomLookAroundGoal(partnerMob));
        this.goalSelector.addGoal(8, new FFWaterAvoidingRandomStrollGoal(partnerMob , 0.31));

        this.targetSelector.addGoal(3, new HurtByPartnerTargetGoal(partnerMob,getPlayer()));
    }
}
