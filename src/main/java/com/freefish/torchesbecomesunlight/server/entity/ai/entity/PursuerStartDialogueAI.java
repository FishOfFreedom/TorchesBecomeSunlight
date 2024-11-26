package com.freefish.torchesbecomesunlight.server.entity.ai.entity;

import com.freefish.torchesbecomesunlight.server.entity.ursus.Pursuer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class PursuerStartDialogueAI extends Goal {
    private Pursuer pursuer;

    @Nullable
    private Player pendingTarget;

    public PursuerStartDialogueAI(Pursuer pursuer){
        this.pursuer = pursuer;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
    }

    public boolean canUse() {
        this.pendingTarget = this.pursuer.level().getNearestPlayer(this.pursuer,96);
        LivingEntity target = pursuer.getTarget();
        return this.pendingTarget != null&target==null;
    }

    public boolean canContinueToUse() {
        if (this.pendingTarget != null) {
            this.pursuer.lookAt(this.pendingTarget, 10.0F, 10.0F);
            return true;
        } else {
            return false;
        }
    }

    public void tick() {
        if (this.pendingTarget != null) {
            if(pursuer.isLookingAtMe(pendingTarget))
                pursuer.setTarget(pendingTarget);
            this.pendingTarget = null;
        }
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
        this.pendingTarget = null;
    }
}
