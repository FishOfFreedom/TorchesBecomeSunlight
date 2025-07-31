package com.freefish.torchesbecomesunlight.server.partner.command;

import com.freefish.torchesbecomesunlight.server.entity.AnimatedEntity;
import com.freefish.torchesbecomesunlight.server.partner.Partner;
import com.freefish.torchesbecomesunlight.server.partner.command.triggertype.BlockTrigger;
import com.freefish.torchesbecomesunlight.server.partner.command.triggertype.TriggerBasicHandle;
import com.freefish.torchesbecomesunlight.server.partner.command.triggertype.TriggerBasicType;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationAct;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import net.minecraft.resources.ResourceLocation;

public class VecAnimationCommand<T extends AnimatedEntity> extends PartnerCommandBasic<T, BlockTrigger> {
    private final ResourceLocation icon;
    private final AnimationAct<T> animationAct;

    public VecAnimationCommand(Partner<T> partner, ResourceLocation resourceLocation, AnimationAct<T> animationAct) {
        super(partner);
        this.icon = resourceLocation;
        this.animationAct = animationAct;
    }

    @Override
    public ResourceLocation getIcon() {
        return icon;
    }

    @Override
    public void triggerCommand(BlockTrigger trigger) {
        T user = getUser();
        Partner<T> userPartner = getUserPartner();
        userPartner.setInstancePos(trigger.vec3,animationAct.getDuration());
        AnimationActHandler.INSTANCE.sendAnimationMessage(user,animationAct);
    }

    @Override
    public TriggerBasicType<BlockTrigger> getTriggerType() {
        return TriggerBasicHandle.BLOCK_TRIGGER;
    }
}