package com.freefish.torchesbecomesunlight.server.capability.frozen;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FrozenCapabilityProvider implements ICapabilityProvider, INBTSerializable {
    private FrozenCapability storyStone;

    public static final Capability<FrozenCapability> FROZEN_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    private final LazyOptional<FrozenCapability> lazyOptional = LazyOptional.of(() ->this.storyStone);

    public FrozenCapabilityProvider(){
        storyStone = new FrozenCapability();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return getCapability(cap);
    }
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        if(cap == FROZEN_CAPABILITY){
            return lazyOptional.cast();
        }
        else return LazyOptional.empty();
    }


    @Override
    public Tag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        storyStone.saveNBTData(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        storyStone.loadNBTData((CompoundTag) nbt);
    }
}
