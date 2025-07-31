package com.freefish.torchesbecomesunlight.server.story.task;

import com.freefish.rosmontislib.sync.ITagSerializable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public abstract class Task implements ITagSerializable<CompoundTag> {
    public abstract TaskType<?> getTaskType();

    public abstract boolean isCompleted(Player player);
}
