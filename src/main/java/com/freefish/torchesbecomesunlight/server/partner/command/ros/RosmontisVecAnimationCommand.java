package com.freefish.torchesbecomesunlight.server.partner.command.ros;

import com.freefish.torchesbecomesunlight.server.entity.rhodesIsland.rosmontis.Rosmontis;
import com.freefish.torchesbecomesunlight.server.partner.Partner;
import com.freefish.torchesbecomesunlight.server.partner.command.PartnerCommandBasic;
import com.freefish.torchesbecomesunlight.server.partner.command.triggertype.BlockTrigger;
import com.freefish.torchesbecomesunlight.server.partner.command.triggertype.TriggerBasicHandle;
import com.freefish.torchesbecomesunlight.server.partner.command.triggertype.TriggerBasicType;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationAct;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import net.minecraft.resources.ResourceLocation;

public class RosmontisVecAnimationCommand extends PartnerCommandBasic<Rosmontis, BlockTrigger> {
    private final ResourceLocation icon;
    private final AnimationAct<Rosmontis> animationAct;

    public RosmontisVecAnimationCommand(Partner<Rosmontis> partner, ResourceLocation resourceLocation, AnimationAct<Rosmontis> animationAct) {
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
        Rosmontis user = getUser();
        Partner<Rosmontis> userPartner = getUserPartner();
        userPartner.setInstancePos(trigger.vec3,animationAct.getDuration());
        AnimationActHandler.INSTANCE.sendAnimationMessage(user,animationAct);
    }

    @Override
    public TriggerBasicType<BlockTrigger> getTriggerType() {
        return TriggerBasicHandle.BLOCK_TRIGGER;
    }
}
