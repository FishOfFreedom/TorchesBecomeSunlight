package com.freefish.torchesbecomesunlight.server.story.data.choose;

import com.freefish.rosmontislib.levelentity.LevelEntityManager;
import com.freefish.rosmontislib.sync.annotation.Persisted;
import com.freefish.torchesbecomesunlight.server.entity.rhodesIsland.PreparationOp;
import com.freefish.torchesbecomesunlight.server.story.dialogueentity.DialogueEntity;
import com.freefish.torchesbecomesunlight.server.world.levelEntity.RhodesIslandLevelEntity;
import com.freefish.torchesbecomesunlight.server.world.levelEntity.TBSLevelEntityHandle;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

public class OpFindRosmontisChoose extends Choose{
    public static final String ID = "op_find_rosmontis";

    @Persisted
    public String opEntityId;
    @Persisted
    public String tnextid;
    @Persisted
    public String fnextid;

    @Override
    public String changeDialogue(DialogueEntity dialogueEntity) {
        LivingEntity chatEntities = dialogueEntity.getChatEntities(opEntityId);
        if(chatEntities instanceof PreparationOp op){
            Level level = dialogueEntity.level();
            LevelEntityManager instance = LevelEntityManager.getInstance(level);
            RhodesIslandLevelEntity instanceLevelEntity = instance.getInstanceLevelEntity(TBSLevelEntityHandle.RHODES_ISLAND_DATA);
            List<RhodesIslandLevelEntity.SpawnBuilding> buildings = instanceLevelEntity.getBUILDINGS();

            RhodesIslandLevelEntity.SpawnBuilding nearBuilding = null;
            for(RhodesIslandLevelEntity.SpawnBuilding building:buildings){
                if(building.center.distSqr(op.blockPosition())<400){
                    nearBuilding = building;
                    break;
                }
            }

            if(nearBuilding!=null){
                return op.trySummonRosmontis(nearBuilding.center)? tnextid:fnextid;
            }else {
                return fnextid;
            }
        }
        return "";
    }

    @Override
    public String getRegisterID() {
        return ID;
    }
}
