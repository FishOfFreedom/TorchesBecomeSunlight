package com.freefish.torchesbecomesunlight.server.world.structure;


import com.freefish.torchesbecomesunlight.server.config.ConfigHandler;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public abstract class FFStructures extends Structure {
    @Getter
    private final ConfigHandler.GenerationConfig config;
    private boolean doCheckHeight;
    private boolean doAvoidWater;
    private boolean doAvoidStructures;
    private int flatRadius = 32;
    private int flatlevel = 1;

    public FFStructures(Structure.StructureSettings settings, ConfigHandler.GenerationConfig config, boolean doCheckHeight, boolean doAvoidWater, boolean doAvoidStructures,int flatRadius,int flatlevel) {
        super(settings);
        this.config = config;
        this.doCheckHeight = doCheckHeight;
        this.doAvoidWater = doAvoidWater;
        this.doAvoidStructures = doAvoidStructures;
        this.flatRadius = flatRadius;
        this.flatlevel = flatlevel;
    }

    public FFStructures(Structure.StructureSettings settings, ConfigHandler.GenerationConfig config, boolean doCheckHeight, boolean doAvoidWater, boolean doAvoidStructures) {
        super(settings);
        this.config = config;
        this.doCheckHeight = doCheckHeight;
        this.doAvoidWater = doAvoidWater;
        this.doAvoidStructures = doAvoidStructures;
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        if(this.checkLocation(context)) {
            return onTopOfChunkCenter(context, Heightmap.Types.WORLD_SURFACE_WG, (builder) -> {
                this.generatePieces(builder, context);
            });
        }
        return Optional.empty();
    }

    public void generatePieces(StructurePiecesBuilder builder, Structure.GenerationContext context) {

    }

    public boolean checkLocation(GenerationContext context) {
        return this.checkLocation(context, config, doCheckHeight, doAvoidWater, doAvoidStructures);
    }

    protected boolean checkLocation(GenerationContext context, ConfigHandler.GenerationConfig config, boolean checkHeight, boolean avoidWater, boolean avoidStructures) {
        ChunkPos chunkPos = context.chunkPos();
        BlockPos centerOfChunk = new BlockPos((chunkPos.x << 4) + 7, 0, (chunkPos.z << 4) + 7);

        int i = chunkPos.getMiddleBlockX();
        int j = chunkPos.getMiddleBlockZ();
        int k = context.chunkGenerator().getFirstOccupiedHeight(i, j, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
        //Holder<Biome> biome = context.chunkGenerator().getBiomeSource().getNoiseBiome(QuartPos.fromBlock(i), QuartPos.fromBlock(k), QuartPos.fromBlock(j), context.randomState().sampler());
        //if (!allowedBiomes.contains(biome)) {
        //    return false;
        //}
        BlockPos blockPos = new BlockPos(i, k, j);
        if(!isAreaFlat(blockPos,flatRadius,5,context,avoidWater)){
            return false;
        }

        ResourceLocation key = context.registryAccess().registryOrThrow(Registries.STRUCTURE_TYPE).getKey(this.type());
        if(key != null){
            if(!checkMinDistance(blockPos,key,512)) return false;
        }

        if (checkHeight) {
            double minHeight = config.heightMin.get();
            double maxHeight = config.heightMax.get();
            int landHeight = getLowestY(context, 16, 16);
            if (minHeight != -65 && landHeight < minHeight) return false;
            if (maxHeight != -65 && landHeight > maxHeight) return false;
        }

        if (avoidWater) {
            ChunkGenerator chunkGenerator = context.chunkGenerator();
            LevelHeightAccessor heightLimitView = context.heightAccessor();
            int centerHeight = chunkGenerator.getBaseHeight(centerOfChunk.getX(), centerOfChunk.getZ(), Heightmap.Types.WORLD_SURFACE_WG, heightLimitView, context.randomState());
            NoiseColumn columnOfBlocks = chunkGenerator.getBaseColumn(centerOfChunk.getX(), centerOfChunk.getZ(), heightLimitView, context.randomState());
            BlockState topBlock = columnOfBlocks.getBlock(centerHeight);
            if (!topBlock.getFluidState().isEmpty()) return false;
        }

        return true;
    }

    private boolean checkMinDistance(BlockPos blockPos, ResourceLocation structureId, int minDistanceChunks) {
        if (minDistanceChunks <= 0) return true;

        Collection<Set<BlockPos>> values = MyStructureGenerator.GENERATED_STRUCTURES.values();
        for(Set<BlockPos> generatedChunks:values){
            for (BlockPos existingChunk : generatedChunks) {
                int dx = Math.abs(existingChunk.getX() - blockPos.getX());
                int dz = Math.abs(existingChunk.getZ() - blockPos.getZ());
                if (dx <= minDistanceChunks && dz <= minDistanceChunks) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isAreaFlat(BlockPos center, int range, int maxAllowedHeightDifference, GenerationContext context,boolean avoidWater) {
        int referenceHeight =context.chunkGenerator().getFirstOccupiedHeight(center.getX(), center.getZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());

        int rangeLevel = range/flatlevel;

        for (int dx = -range; dx <= range; dx+=rangeLevel) {
            for (int dz = -range; dz <= range; dz+=rangeLevel) {

                int xToCheck = center.getX() + dx;
                int zToCheck = center.getZ() + dz;

                int currentHeight = context.chunkGenerator().getFirstOccupiedHeight(xToCheck, zToCheck, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());

                if (avoidWater) {
                    ChunkGenerator chunkGenerator = context.chunkGenerator();
                    LevelHeightAccessor heightLimitView = context.heightAccessor();
                    //int centerHeight = chunkGenerator.getBaseHeight(xToCheck, zToCheck, Heightmap.Types.WORLD_SURFACE_WG, heightLimitView, context.randomState());
                    NoiseColumn columnOfBlocks = chunkGenerator.getBaseColumn(xToCheck, zToCheck, heightLimitView, context.randomState());
                    BlockState topBlock = columnOfBlocks.getBlock(currentHeight);
                    if (!topBlock.getFluidState().isEmpty()) return false;
                }

                if (Math.abs(currentHeight - referenceHeight) > maxAllowedHeightDifference) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public GenerationStep.Decoration step() {
        return GenerationStep.Decoration.SURFACE_STRUCTURES;
    }
}
