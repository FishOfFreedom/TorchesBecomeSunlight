package com.freefish.torchesbecomesunlight.server.partner.command;

import com.freefish.torchesbecomesunlight.server.partner.Partner;
import com.freefish.torchesbecomesunlight.server.partner.command.triggertype.TargerTrigger;
import com.freefish.torchesbecomesunlight.server.partner.command.triggertype.TriggerBasicHandle;
import com.freefish.torchesbecomesunlight.server.partner.command.triggertype.TriggerBasicType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

public class AttackTargetCommand<T extends Mob> extends PartnerCommandBasic<T, TargerTrigger>{
    public AttackTargetCommand(Partner<T> partner) {
        super(partner);
    }

    @Override
    public ResourceLocation getIcon() {
        return CommandIcon.TARGET;
    }

    @Override
    public void triggerCommand(TargerTrigger trigger) {
        T user = getUser();
        Level level = user.level();
        Entity entity = level.getEntity(trigger.id);
        if(entity instanceof LivingEntity living){
            user.setTarget(living);
        }
    }

    @Override
    public TriggerBasicType<TargerTrigger> getTriggerType() {
        return TriggerBasicHandle.TARGER_TRIGGER;
    }
}
