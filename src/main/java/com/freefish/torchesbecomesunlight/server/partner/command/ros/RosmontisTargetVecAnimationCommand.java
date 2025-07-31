package com.freefish.torchesbecomesunlight.server.partner.command.ros;

import com.freefish.torchesbecomesunlight.server.entity.rhodesIsland.rosmontis.Rosmontis;
import com.freefish.torchesbecomesunlight.server.partner.Partner;
import com.freefish.torchesbecomesunlight.server.partner.command.PartnerCommandBasic;
import com.freefish.torchesbecomesunlight.server.partner.command.triggertype.TargerTrigger;
import com.freefish.torchesbecomesunlight.server.partner.command.triggertype.TriggerBasicHandle;
import com.freefish.torchesbecomesunlight.server.partner.command.triggertype.TriggerBasicType;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationAct;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class RosmontisTargetVecAnimationCommand extends PartnerCommandBasic<Rosmontis, TargerTrigger> {
    private final ResourceLocation icon;
    private final AnimationAct<Rosmontis> animationAct;

    public RosmontisTargetVecAnimationCommand(Partner<Rosmontis> partner, ResourceLocation resourceLocation, AnimationAct<Rosmontis> animationAct) {
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
        Rosmontis user = getUser();
        Partner<Rosmontis> userPartner = getUserPartner();
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
