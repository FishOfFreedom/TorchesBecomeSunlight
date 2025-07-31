package com.freefish.torchesbecomesunlight.server.partner.command;

import com.freefish.torchesbecomesunlight.server.partner.Partner;
import com.freefish.torchesbecomesunlight.server.partner.command.triggertype.TriggerBasic;
import com.freefish.torchesbecomesunlight.server.partner.command.triggertype.TriggerBasicType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

public abstract class PartnerCommandBasic<T extends Mob,B extends TriggerBasic> {
    private final Partner<T> partner;

    public PartnerCommandBasic(Partner<T> partner) {
        this.partner = partner;
    }

    public abstract ResourceLocation getIcon();

    public abstract void triggerCommand(B trigger);

    public T getUser(){
        return partner.getPartnerMob();
    }

    public Partner<T> getUserPartner(){
        return partner;
    }

    public abstract TriggerBasicType<B> getTriggerType();
}
