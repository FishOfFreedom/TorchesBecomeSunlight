package com.freefish.torchesbecomesunlight.server.entity.dlc.ai;

import com.freefish.torchesbecomesunlight.server.entity.dlc.GunKnightPatriot;
import com.freefish.torchesbecomesunlight.server.story.dialogueentity.DialogueEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class GunPatriotAutoDialogueGoal extends Goal {
    private final GunKnightPatriot mob;

    public GunPatriotAutoDialogueGoal(GunKnightPatriot mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return mob.getDialogueEntity()!=null;
    }

    @Override
    public boolean canContinueToUse() {
        DialogueEntity dialogueEntity = mob.getDialogueEntity();
        return dialogueEntity!=null&&dialogueEntity.isAlive()&&mob.hurtTime<=0;
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
        DialogueEntity dialogueEntity = mob.getDialogueEntity();
        if(dialogueEntity!=null){
            dialogueEntity.discard();
            mob.setDialogueEntity(null);
        }
    }

    @Override
    public void tick() {
        DialogueEntity dialogueEntity = mob.getDialogueEntity();
        if(dialogueEntity!=null&&dialogueEntity.getPlayer()!=null){
            mob.getLookControl().setLookAt(dialogueEntity.getPlayer());
        }
        super.tick();
    }
}
