package com.freefish.torchesbecomesunlight.server.partner.command.triggertype;

import com.freefish.rosmontislib.sync.ITagSerializable;
import net.minecraft.nbt.CompoundTag;

public abstract class TriggerBasic implements ITagSerializable<CompoundTag> {
    public abstract String getType();
}
