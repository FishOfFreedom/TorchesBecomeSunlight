package com.freefish.torchesbecomesunlight.server.story;

import com.freefish.torchesbecomesunlight.server.capability.story.PlayerStoryStone;
import com.freefish.torchesbecomesunlight.server.capability.story.PlayerStoryStoneProvider;
import com.freefish.torchesbecomesunlight.server.entity.villager.Man;
import com.freefish.torchesbecomesunlight.server.entity.effect.dialogueentity.quest.QuestBase;
import com.freefish.torchesbecomesunlight.server.entity.effect.dialogueentity.quest.TalkWithEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ProcessManage {
    public static ProcessManage INSTANCE = new ProcessManage();

    public List<QuestBase> storyLine = new ArrayList<>();

    public QuestBase getCurrentTask(Player player){
        AtomicReference<QuestBase> questBaseRef = new AtomicReference<>(null);

        player.getCapability(PlayerStoryStoneProvider.PLAYER_STORY_STONE_CAPABILITY).ifPresent(storyStone -> {
            int size = storyLine.size();
            if(storyStone.getStoryState()<size) {
                QuestBase currentTask = storyLine.get(storyStone.getStoryState());
                questBaseRef.set(currentTask);
            }
        });

        return questBaseRef.get();
    }

    public void nextTask(Player player){
        player.getCapability(PlayerStoryStoneProvider.PLAYER_STORY_STONE_CAPABILITY).ifPresent(PlayerStoryStone::increasing);
    }

    public void TaskTrigger(){

    }

    private ProcessManage(){
        storyLine.add(new TalkWithEntity("winter"){
            @Override
            public boolean isTalkEntity(Entity entity) {
                return entity instanceof Man;
            }
        });
    }
}
