package com.freefish.torchesbecomesunlight.server.partner.command.triggertype;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

public class BlockTrigger extends TriggerBasic {
    public static final String ID = "block";
    public Vec3 vec3 = Vec3.ZERO;

    public BlockTrigger(Vec3 vec3) {
        this.vec3 = vec3;
    }

    public BlockTrigger() {
    }

    @Override
    public String getType() {
        return ID;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putFloat("vec3x",(float) vec3.x);
        compoundTag.putFloat("vec3y",(float) vec3.y);
        compoundTag.putFloat("vec3z",(float) vec3.z);
        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        vec3  = new Vec3(compoundTag.getFloat("vec3x"),compoundTag.getFloat("vec3y"),compoundTag.getFloat("vec3z"));
    }
}
