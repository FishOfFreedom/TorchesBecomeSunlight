package com.freefish.torchesbecomesunlight.server.story;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.story.data.Dialogue;
import com.freefish.torchesbecomesunlight.server.story.dialogueentity.DialogueEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface IDialogueEntity {
    default Entity selfDialogue(){
        return (Entity) this;
    }

    default DialogueEntity startTalk(String dialoguePath, LivingEntity ... livingEntities) {
        Entity self = selfDialogue();
        Level level = self.level();
        Dialogue dialogue = DialogueManager.INSTANCE.readDialogueFromData(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, dialoguePath), level);
        DialogueEntity dialogueEntity = new DialogueEntity(self, level, dialogue,  livingEntities);
        setDialogueEntity(dialogueEntity);
        level.addFreshEntity(dialogueEntity);
        return dialogueEntity;
    }


    boolean canDialogue();

    void startDialogue(Player player);

    DialogueEntity getDialogueEntity();

    void setDialogueEntity(DialogueEntity dialogueEntity);
}
