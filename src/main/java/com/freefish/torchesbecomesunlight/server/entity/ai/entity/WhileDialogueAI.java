package com.freefish.torchesbecomesunlight.server.entity.ai.entity;

import com.freefish.torchesbecomesunlight.server.entity.FreeFishEntity;
import com.freefish.torchesbecomesunlight.server.entity.effect.dialogueentity.DialogueEntity;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;

public class WhileDialogueAI extends Goal {
    private final FreeFishEntity entity;

    public WhileDialogueAI(FreeFishEntity animatedEntity) {
        this.entity = animatedEntity;
    }

    @Override
    public boolean canUse() {
        DialogueEntity dialogueEntity = MathUtils.getClosestEntity(entity,entity.level().getEntitiesOfClass(DialogueEntity.class,entity.getBoundingBox().inflate(5)));
        return dialogueEntity != null&&dialogueEntity.isAlive();
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void tick() {
        entity.getNavigation().stop();
        DialogueEntity dialogueEntity = MathUtils.getClosestEntity(entity,entity.level().getEntitiesOfClass(DialogueEntity.class,entity.getBoundingBox().inflate(5)));
        if(dialogueEntity!=null&&dialogueEntity.isAlive()){
            if(dialogueEntity.getChatEntities()!=null){
                Entity play = dialogueEntity.getChatEntities()[0];
                if(play!=null) {
                    entity.getLookControl().setLookAt(play,30f,30f);
                }
            }
        }
    }
}
