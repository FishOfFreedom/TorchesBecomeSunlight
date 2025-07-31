package com.freefish.torchesbecomesunlight.mixin;

import com.freefish.torchesbecomesunlight.server.partner.IMobPartner;
import com.freefish.torchesbecomesunlight.server.partner.Partner;
import com.freefish.torchesbecomesunlight.server.partner.PartnerUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity implements IMobPartner {
    private Partner<?> partner;

    @Shadow
    protected PathNavigation navigation;
    @Shadow
    protected LookControl lookControl;
    @Shadow
    protected MoveControl moveControl;
    @Shadow
    protected JumpControl jumpControl;

    protected MobMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "serverAiStep"
            ,at = @At("HEAD"), cancellable = true)
    public void partnerServerAi(CallbackInfo info){
        Mob self = selfMixin();
        Partner<?> partnerControl = PartnerUtil.getPartner(self);
        if(partnerControl != null&&partnerControl.isReplaceGoal()){
            partnerControl.aiStep();

            this.navigation.tick();
            this.moveControl.tick();
            this.lookControl.tick();
            this.jumpControl.tick();

            info.cancel();
        }
    }

    @Inject(method = "tick"
            ,at = @At("HEAD"), cancellable = true)
    public void tick(CallbackInfo info){
        Mob self = selfMixin();
        Partner<?> partnerControl = PartnerUtil.getPartner(self);
        if(partnerControl != null){
            partnerControl.tick();

            if(partnerControl.getPlayer()==null||!partnerControl.getPlayer().isAlive()){
                PartnerUtil.setPartner(self,null);
            }
        }

    }

    //@Inject(method = "getTarget"
    //        ,at = @At("HEAD"), cancellable = true)
    //public void getTarget(CallbackInfoReturnable<LivingEntity> cir) {
    //    Mob self = self();
    //    Partner<?> partnerControl = PartnerUtil.getPartner(self);
    //    if(partnerControl!=null){
    //        LivingEntity returnValue = cir.getReturnValue();
    //        if(returnValue == partnerControl.getPlayer()){
    //            cir.setReturnValue(null);
    //        }
    //    }
    //}

    @Override
    public Partner<?> getPartner() {
        return partner;
    }

    @Override
    public void setPartner(Partner<?> partner) {
        this.partner = partner;
    }

    @Override
    public boolean invokeHurt(DamageSource damageSource, float amount) {
        return super.hurt(damageSource,amount);
    }

    private Mob selfMixin(){
        return (Mob) (Object) this;
    }
}