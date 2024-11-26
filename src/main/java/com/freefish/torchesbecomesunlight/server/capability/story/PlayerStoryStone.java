package com.freefish.torchesbecomesunlight.server.capability.story;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.event.packet.toclient.SetStoryStatePacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

public class PlayerStoryStone {
    public boolean isCanDialogue;

    public boolean isCanDialogue() {
        return isCanDialogue;
    }

    public void setCanDialogue(boolean canDialogue) {
        isCanDialogue = canDialogue;
    }

    public int getStoryState() {
        return storyState;
    }

    public void setStoryState(int storyState) {
        this.storyState = storyState;
    }

    private int storyState;

    public int getDialogueTime() {
        return dialogueTime;
    }

    public void setDialogueTime(int dialogueTime) {
        this.dialogueTime = dialogueTime;
    }

    private int dialogueTime;

    public PlayerStoryStone() {
    }

    public void saveNBTData(CompoundTag compoundTag){
        compoundTag.putInt("storystate",storyState);
        compoundTag.putInt("daloguetiem",dialogueTime);
        compoundTag.putBoolean("iscand",isCanDialogue);
    }

    public void loadNBTData(CompoundTag compoundTag){
        storyState = compoundTag.getInt("storystate");
        dialogueTime = compoundTag.getInt("daloguetiem");
        isCanDialogue = compoundTag.getBoolean("iscand");
    }

    public void increasing() {
        storyState++;
    }
}
