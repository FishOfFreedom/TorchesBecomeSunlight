package com.freefish.torchesbecomesunlight.server.world.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MyStructureGenerator {
    public static final Map<ResourceLocation, Set<BlockPos>> GENERATED_STRUCTURES = new ConcurrentHashMap<>();

    public static void markStructureGenerated(ResourceLocation structureId, BlockPos chunkPos) {
        GENERATED_STRUCTURES.computeIfAbsent(structureId, k -> ConcurrentHashMap.newKeySet())
            .add(chunkPos);
    }
}