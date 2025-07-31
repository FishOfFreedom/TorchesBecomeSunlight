package com.freefish.torchesbecomesunlight.server.partner;

import net.minecraft.world.damagesource.DamageSource;

public interface IMobPartner {
    Partner<?> getPartner();

    void setPartner(Partner<?> partner);

    boolean invokeHurt(DamageSource damageSource,float amount);
}