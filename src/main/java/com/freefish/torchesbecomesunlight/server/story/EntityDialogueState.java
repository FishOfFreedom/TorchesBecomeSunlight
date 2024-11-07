package com.freefish.torchesbecomesunlight.server.story;

import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.story.dialogue.Dialogue;
import com.freefish.torchesbecomesunlight.server.story.dialogue.DialogueStore;
import net.minecraft.world.entity.EntityType;

import java.util.HashMap;

public class EntityDialogueState {
    private EntityDialogueState(){
        hashMap = new HashMap<>();
        hashMap.put(EntityHandle.SNOWNOVA.get(),new Dialogue[]{DialogueStore.snownova_meet_1});
    }

    public static EntityDialogueState entityDialogueState = new EntityDialogueState();

    private HashMap<EntityType, Dialogue[]> hashMap;
}
