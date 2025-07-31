package com.freefish.torchesbecomesunlight.server.partner.command.triggertype;

import net.minecraft.nbt.CompoundTag;

public class TargerTrigger extends TriggerBasic {
    public static final String ID = "target";
    public int id;

    public TargerTrigger(int id) {
        this.id = id;
    }

    public TargerTrigger() {
    }

    @Override
    public String getType() {
        return ID;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putInt("vec3x",id);
        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        id = compoundTag.getInt("vec3x");
    }
}
