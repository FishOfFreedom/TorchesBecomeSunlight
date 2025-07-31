package com.freefish.torchesbecomesunlight.server.partner.command;

import com.freefish.torchesbecomesunlight.server.partner.Partner;
import com.freefish.torchesbecomesunlight.server.partner.command.triggertype.BlockTrigger;
import com.freefish.torchesbecomesunlight.server.partner.command.triggertype.TriggerBasicHandle;
import com.freefish.torchesbecomesunlight.server.partner.command.triggertype.TriggerBasicType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

public class MoveToPosCommand<T extends Mob> extends PartnerCommandBasic<T, BlockTrigger>{
    public  MoveToPosCommand(Partner<T> partner) {
        super(partner);
    }

    @Override
    public ResourceLocation getIcon() {
        return CommandIcon.MOVE_TO_POS;
    }

    @Override
    public void triggerCommand(BlockTrigger trigger) {
        T user = getUser();
        user.moveTo(trigger.vec3);
    }

    @Override
    public TriggerBasicType<BlockTrigger> getTriggerType() {
        return TriggerBasicHandle.BLOCK_TRIGGER;
    }
}
