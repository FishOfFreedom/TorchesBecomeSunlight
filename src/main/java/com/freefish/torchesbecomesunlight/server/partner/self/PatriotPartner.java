package com.freefish.torchesbecomesunlight.server.partner.self;

import com.freefish.torchesbecomesunlight.server.config.ConfigHandler;
import com.freefish.torchesbecomesunlight.server.entity.ai.FFRandomLookAroundGoal;
import com.freefish.torchesbecomesunlight.server.entity.ai.FFWaterAvoidingRandomStrollGoal;
import com.freefish.torchesbecomesunlight.server.entity.ai.entity.patriot.PatriotAttackAI;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.shield.Patriot;
import com.freefish.torchesbecomesunlight.server.partner.Partner;
import com.freefish.torchesbecomesunlight.server.partner.PartnerHandler;
import com.freefish.torchesbecomesunlight.server.partner.ai.HurtByPartnerTargetGoal;
import com.freefish.torchesbecomesunlight.server.partner.command.CommandIcon;
import com.freefish.torchesbecomesunlight.server.partner.command.VecAnimationCommand;
import com.freefish.torchesbecomesunlight.server.partner.goal.FollowPlayerGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class PatriotPartner extends Partner<Patriot> {
    public int continueTime;

    public PatriotPartner() {
        super(PartnerHandler.PATRIOT_PARTNER);
    }

    @Override
    public void removeFromWorld() {
        Patriot partnerMob = getPartnerMob();
        if(partnerMob!=null){
            partnerMob.discard();
        }
    }

    @Override
    public void tick() {
        super.tick();
        Level level = getPartnerMob().level();
        Patriot partnerMob = getPartnerMob();

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
        return ConfigHandler.COMMON.MOBS.PATRIOT.partnerConfig;
    }

    @Override
    public boolean playerHurt(LivingHurtEvent event) {
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
        this.skillManager.addCommand(new VecAnimationCommand<>(this, CommandIcon.PATRIOT_THROW,Patriot.THROW));
    }

    @Override
    public void registerGoal() {
        Patriot partnerMob = getPartnerMob();
        this.goalSelector.addGoal(0, new FloatGoal(partnerMob));
        this.goalSelector.addGoal(3,new FollowPlayerGoal(getPartnerMob(),getPlayer(),0.31,8));
        this.goalSelector.addGoal(2,new PatriotAttackAI(partnerMob));

        this.goalSelector.addGoal(6, new FFRandomLookAroundGoal(partnerMob));
        this.goalSelector.addGoal(8, new FFWaterAvoidingRandomStrollGoal(partnerMob , 0.31));

        this.targetSelector.addGoal(3, new HurtByPartnerTargetGoal(partnerMob,getPlayer()));
    }
}
