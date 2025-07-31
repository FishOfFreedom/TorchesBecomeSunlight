package com.freefish.torchesbecomesunlight.server.entity.ai.entity;

import com.freefish.torchesbecomesunlight.server.entity.FreeFishEntity;
import com.freefish.torchesbecomesunlight.server.entity.ursus.Pursuer;
import com.freefish.torchesbecomesunlight.server.util.FFEntityUtils;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;
import java.util.List;

public class WhileDialogueAI extends Goal {
    private final FreeFishEntity entity;

    public WhileDialogueAI(FreeFishEntity animatedEntity) {
        this.entity = animatedEntity;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void tick() {
        if(entity.tickCount==100){
            List<Player> entitiesOfClass = entity.level().getEntitiesOfClass(Player.class, entity.getBoundingBox().inflate(5));
            Player closestEntity = FFEntityUtils.getClosestEntity(entity, entitiesOfClass);
            if(entity instanceof Pursuer p&&closestEntity!=null){
                p.startDialogue(closestEntity);
            }
        }

        entity.getNavigation().stop();
        entity.getLookControl().setLookAt(entity.getEyePosition().add(-2,1,0));
    }
}
