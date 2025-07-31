package com.freefish.torchesbecomesunlight.server.block.blockentity;

import net.minecraft.world.entity.player.Player;

public class HookPlayerClick {
    public HookPlayerClick(ElevatorBlockEntity player) {
        this.elevatorBlockEntity = player;
    }

    public void click(Player player){
        elevatorBlockEntity.inter();
    }

    public boolean isLocalPlayerLooked;
    public ElevatorBlockEntity elevatorBlockEntity;
}
