package com.freefish.torchesbecomesunlight.server.partner.vanilla;

import com.freefish.torchesbecomesunlight.server.partner.Partner;
import com.freefish.torchesbecomesunlight.server.partner.PartnerHandler;
import com.freefish.torchesbecomesunlight.server.partner.command.AttackTargetCommand;
import com.freefish.torchesbecomesunlight.server.partner.command.MoveToPosCommand;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;

public class WolfPartner extends Partner<Wolf> {
    public WolfPartner() {
        super(PartnerHandler.WOLF_PARTNER);
    }

    @Override
    public void registerCommand() {
        super.registerCommand();
        skillManager.addCommand(new MoveToPosCommand<>(this));
        skillManager.addCommand(new AttackTargetCommand<>(this));
        skillManager.addCommand(new AttackTargetCommand<>(this));
    }

    @Override
    public void registerGoal() {
        Wolf partnerMob = getPartnerMob();
        this.goalSelector.addGoal(1, new FloatGoal(partnerMob));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(partnerMob));
        this.goalSelector.addGoal(4, new LeapAtTargetGoal(partnerMob, 0.4F));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(partnerMob, 1.0D, true));
        this.goalSelector.addGoal(6, new FollowOwnerGoal(partnerMob, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(7, new BreedGoal(partnerMob, 1.0D));
        //this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(partnerMob, 1.0D));
        this.goalSelector.addGoal(9, new BegGoal(partnerMob, 8.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(partnerMob, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(partnerMob));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(partnerMob));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(partnerMob));
        this.targetSelector.addGoal(3, (new HurtByTargetGoal(partnerMob)).setAlertOthers());
    }
}
