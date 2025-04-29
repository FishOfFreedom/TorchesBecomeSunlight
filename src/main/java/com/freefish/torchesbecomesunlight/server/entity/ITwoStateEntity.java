package com.freefish.torchesbecomesunlight.server.entity;

import net.minecraft.nbt.CompoundTag;

public interface ITwoStateEntity {
    void transSpawnState(State spawnState);

    void setSpawnState(State spawnState);

    State getSpawnState();

    enum State{
        NATURE,ONE,TWO
    }

    default void addAdditionalSpawnState(CompoundTag compound){
        compound.putString("spawnstate", getSpawnState().toString());
    }

    default void readAddAdditionalSpawnState(CompoundTag compound){
        setSpawnState(State.valueOf(compound.getString("spawnstate")));
    }
}
