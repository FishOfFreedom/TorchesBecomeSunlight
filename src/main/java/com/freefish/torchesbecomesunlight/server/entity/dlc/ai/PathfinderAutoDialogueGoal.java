package com.freefish.torchesbecomesunlight.server.entity.dlc.ai;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.block.blockentity.BigBenBlockEntity;
import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.PlayerCapability;
import com.freefish.torchesbecomesunlight.server.entity.dlc.PathfinderBallistarius;
import com.freefish.torchesbecomesunlight.server.story.DialogueManager;
import com.freefish.torchesbecomesunlight.server.story.PlayerStoryStoneData;
import com.freefish.torchesbecomesunlight.server.story.data.Dialogue;
import com.freefish.torchesbecomesunlight.server.story.data.DialogueEntry;
import com.freefish.torchesbecomesunlight.server.story.dialogueentity.DialogueEntity;
import com.freefish.torchesbecomesunlight.server.util.FFEntityUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.EnumSet;
import java.util.List;

public class PathfinderAutoDialogueGoal extends Goal {
    private final PathfinderBallistarius mob;

    public PathfinderAutoDialogueGoal(PathfinderBallistarius mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        BigBenBlockEntity tile = mob.getTile();
        if(tile!=null){
            if(tile.inTime>0){
                tile.inTime--;
            }
            else {
                Level level = mob.level();
                List<Player> entitiesOfClass = level.getEntitiesOfClass(Player.class, mob.getBoundingBox().inflate(4));
                Player closestEntity = FFEntityUtils.getClosestEntity(mob, entitiesOfClass);
                if (closestEntity != null && !closestEntity.isCreative()) {
                    PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(closestEntity, CapabilityHandle.PLAYER_CAPABILITY);
                    if (capability != null) {
                        PlayerStoryStoneData playerStory = capability.getPlayerStory();
                        if (!playerStory.isSeenGunPatriot()) {
                            Dialogue dialogue = DialogueManager.INSTANCE.readDialogueFromData(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "dialogue/gun_patriot/pathfinder_firstmeet.json"), level);
                            DialogueEntry dialogueEntry = dialogue.getDialogueEntry("main2");
                            dialogueEntry.setRunnable(() -> {
                                playerStory.setSeenGunPatriot(true);
                            });
                            DialogueEntity dialogueEntity = new DialogueEntity(mob, level, dialogue, mob, closestEntity);
                            mob.setDialogueEntity(dialogueEntity);
                            level.addFreshEntity(dialogueEntity);
                            tile.inTime = 2000;
                            return true;
                        }
                    }
                }
            }
        }
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
