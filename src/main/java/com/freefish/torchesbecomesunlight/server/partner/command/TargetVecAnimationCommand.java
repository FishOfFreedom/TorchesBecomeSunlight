package com.freefish.torchesbecomesunlight.server.partner.command;

import com.freefish.torchesbecomesunlight.server.entity.AnimatedEntity;
import com.freefish.torchesbecomesunlight.server.partner.Partner;
import com.freefish.torchesbecomesunlight.server.partner.command.triggertype.TargerTrigger;
import com.freefish.torchesbecomesunlight.server.partner.command.triggertype.TriggerBasicHandle;
import com.freefish.torchesbecomesunlight.server.partner.command.triggertype.TriggerBasicType;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationAct;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class TargetVecAnimationCommand<T extends AnimatedEntity> extends PartnerCommandBasic<T, TargerTrigger> {
    private final ResourceLocation icon;
    private final AnimationAct<T> animationAct;

    public TargetVecAnimationCommand(Partner<T> partner, ResourceLocation resourceLocation, AnimationAct<T> animationAct) {
        super(partner);
        this.icon = resourceLocation;
        this.animationAct = animationAct;
    }

    @Override
    public ResourceLocation getIcon() {
        return icon;
    }

    @Override
    public void triggerCommand(TargerTrigger trigger) {
        T user = getUser();
        Partner<T> userPartner = getUserPartner();
        Entity entity = user.level().getEntity(trigger.id);

        if(entity instanceof LivingEntity living){
            userPartner.setInstanceTarget(living, animationAct.getDuration());
            AnimationActHandler.INSTANCE.sendAnimationMessage(user, animationAct);
        }
    }

    @Override
    public TriggerBasicType<TargerTrigger> getTriggerType() {
        return TriggerBasicHandle.TARGER_TRIGGER;
    }
}
