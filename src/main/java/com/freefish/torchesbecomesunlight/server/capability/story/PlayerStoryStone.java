package com.freefish.torchesbecomesunlight.server.capability.story;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.event.packet.toclient.SetStoryStatePacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

public class PlayerStoryStone {
    public int getStoryState() {
        return storyState;
    }

    public void setStoryState(int storyState) {
        this.storyState = storyState;
    }

    //public void setStoryState(int storyState , Player player) {
    //    this.storyState = storyState;
    //    TorchesBecomeSunlight.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SetStoryStatePacket(player.getId(), storyState));
    //}

    private int storyState;

    public PlayerStoryStone() {
    }

    public void saveNBTData(CompoundTag compoundTag){
        compoundTag.putInt("storystate",storyState);
    }

    public void loadNBTData(CompoundTag compoundTag){
        storyState = compoundTag.getInt("storystate");
    }

    public void increasing() {
        storyState++;
    }
}
