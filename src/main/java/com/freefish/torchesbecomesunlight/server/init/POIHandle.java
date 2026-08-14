package com.freefish.torchesbecomesunlight.server.init;

import net.minecraft.core.registries.BuiltInRegistries;
import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Set;

public class POIHandle {

    public static final DeferredRegister<PoiType> DEF_REG = DeferredRegister.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE, TorchesBecomeSunlight.MOD_ID);

    public static final DeferredHolder<PoiType, PoiType> URSUS_VILLAGE_CENTRE = DEF_REG.register("village_center", () -> new PoiType(getAllStatesOf(BlockHandle.STEW_POT.get()), 0, 6));

    private static Set<BlockState> getAllStatesOf(Block block) {
        return ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates());
    }
} 