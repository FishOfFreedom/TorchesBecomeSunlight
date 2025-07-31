package com.freefish.torchesbecomesunlight.server.partner.self;

import com.freefish.torchesbecomesunlight.server.config.ConfigHandler;
import com.freefish.torchesbecomesunlight.server.entity.ai.FFRandomLookAroundGoal;
import com.freefish.torchesbecomesunlight.server.entity.ai.FFWaterAvoidingRandomStrollGoal;
import com.freefish.torchesbecomesunlight.server.entity.ai.entity.snownova.SnowNova1AttackAI;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster.FrostNova;
import com.freefish.torchesbecomesunlight.server.partner.Partner;
import com.freefish.torchesbecomesunlight.server.partner.PartnerHandler;
import com.freefish.torchesbecomesunlight.server.partner.ai.HurtByPartnerTargetGoal;
import com.freefish.torchesbecomesunlight.server.partner.command.CommandIcon;
import com.freefish.torchesbecomesunlight.server.partner.command.TargetVecAnimationCommand;
import com.freefish.torchesbecomesunlight.server.partner.goal.FollowPlayerGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class FrostNovaPartner extends Partner<FrostNova> {
    public int continueTime;

    public FrostNovaPartner() {
        super(PartnerHandler.FROSTNOVA_PARTNER);
    }

    @Override
    public void removeFromWorld() {
        FrostNova partnerMob = getPartnerMob();
        if(partnerMob!=null){
            partnerMob.discard();
        }
    }

    @Override
    public void tick() {
        super.tick();
        Level level = getPartnerMob().level();
        FrostNova partnerMob = getPartnerMob();

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
        return ConfigHandler.COMMON.MOBS.FROSTNOVA.partnerConfig;
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
        this.skillManager.addCommand(new TargetVecAnimationCommand<>(this, CommandIcon.FROSTNOVA_ICE,FrostNova.LULLABYE_2));
    }

    @Override
    public void registerGoal() {
        FrostNova partnerMob = getPartnerMob();
        this.goalSelector.addGoal(0, new FloatGoal(partnerMob));
        this.goalSelector.addGoal(3,new FollowPlayerGoal(getPartnerMob(),getPlayer(),0.31,8));
        this.goalSelector.addGoal(2,new SnowNova1AttackAI(partnerMob));

        this.goalSelector.addGoal(6, new FFRandomLookAroundGoal(partnerMob));
        this.goalSelector.addGoal(8, new FFWaterAvoidingRandomStrollGoal(partnerMob , 0.31));

        this.targetSelector.addGoal(3, new HurtByPartnerTargetGoal(partnerMob,getPlayer()));
    }
}
