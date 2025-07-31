package com.freefish.torchesbecomesunlight.server.entity;

import net.minecraft.world.entity.Mob;

public interface ITeamMemberStorage<T extends Mob> {
    T getLeader();

    void setLeader(T leader);
}
