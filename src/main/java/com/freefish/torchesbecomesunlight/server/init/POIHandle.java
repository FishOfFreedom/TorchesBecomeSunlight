package com.freefish.torchesbecomesunlight.server.init;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

public class POIHandle {

    public static final DeferredRegister<PoiType> DEF_REG = DeferredRegister.create(ForgeRegistries.POI_TYPES, TorchesBecomeSunlight.MOD_ID);

    public static final RegistryObject<PoiType> URSUS_VILLAGE_CENTRE = DEF_REG.register("village_center", () -> new PoiType(getAllStatesOf(BlockHandle.POT.get()), 0, 6));

    private static Set<BlockState> getAllStatesOf(Block block) {
        return ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates());
    }
} 