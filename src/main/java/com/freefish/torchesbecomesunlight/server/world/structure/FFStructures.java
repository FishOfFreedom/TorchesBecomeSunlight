package com.freefish.torchesbecomesunlight.server.world.structure;


import com.freefish.torchesbecomesunlight.server.config.ConfigHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import javax.annotation.Nullable;
import java.util.Optional;

public abstract class FFStructures extends Structure {
    private final Holder<StructureTemplatePool> startPool;
    private final Optional<ResourceLocation> startJigsawName;
    private final int size;
    private final HeightProvider startHeight;
    private final Optional<Heightmap.Types> projectStartToHeightmap;
    private final int maxDistanceFromCenter;

    public FFStructures(StructureSettings config,
                         Holder<StructureTemplatePool> startPool,
                         Optional<ResourceLocation> startJigsawName,
                         int size,
                         HeightProvider startHeight,
                         Optional<Heightmap.Types> projectStartToHeightmap,
                         int maxDistanceFromCenter)
    {

        super(config);
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.size = size;
        this.startHeight = startHeight;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {

        if (!checkLocation(context)) {
            return Optional.empty();
        }

        int startY = this.startHeight.sample(context.random(), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));

        ChunkPos chunkPos = context.chunkPos();
        BlockPos blockPos = new BlockPos(chunkPos.getMinBlockX(), startY, chunkPos.getMinBlockZ());

        Optional<GenerationStub> structurePiecesGenerator =
                JigsawPlacement.addPieces(
                        context,
                        this.startPool,
                        this.startJigsawName,
                        this.size,
                        blockPos,
                        false,
                        this.projectStartToHeightmap,
                        this.maxDistanceFromCenter);

        return structurePiecesGenerator;
    }

    public boolean checkLocation(GenerationContext context) {
        return this.checkLocation(context, true,true);
    }

    protected boolean checkLocation(GenerationContext context, boolean checkHeight, boolean avoidWater) {
        ChunkPos chunkPos = context.chunkPos();
        BlockPos centerOfChunk = new BlockPos((chunkPos.x << 4) + 7, 0, (chunkPos.z << 4) + 7);
        ConfigHandler.GenerationConfig config = getConfig();

        int i = chunkPos.getMiddleBlockX();
        int j = chunkPos.getMiddleBlockZ();
        int k = context.chunkGenerator().getFirstOccupiedHeight(i, j, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());

        if(!isAreaFlat(new BlockPos(i,k,j),32,5,context)){
            return false;
        }

        if (config!=null&&checkHeight) {
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

    public static boolean isAreaFlat(BlockPos center, int range, int maxAllowedHeightDifference, GenerationContext context) {
        int referenceHeight =context.chunkGenerator().getFirstOccupiedHeight(center.getX(), center.getZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());

        for (int dx = -range; dx <= range; dx+=4) {
            for (int dz = -range; dz <= range; dz+=4) {

                int xToCheck = center.getX() + dx;
                int zToCheck = center.getZ() + dz;

                int currentHeight = context.chunkGenerator().getFirstOccupiedHeight(xToCheck, zToCheck, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());

                if (Math.abs(currentHeight - referenceHeight) > maxAllowedHeightDifference) {
                    return false;
                }
            }
        }

        return true;
    }

    public Holder<StructureTemplatePool> getStartPool() {
        return startPool;
    }

    public int getSize() {
        return size;
    }

    public Optional<ResourceLocation> getStartJigsawName() {
        return startJigsawName;
    }

    public HeightProvider getStartHeight() {
        return startHeight;
    }

    public Optional<Heightmap.Types> getProjectStartToHeightmap() {
        return projectStartToHeightmap;
    }

    public int getMaxDistanceFromCenter() {
        return maxDistanceFromCenter;
    }

    @Nullable
    public ConfigHandler.GenerationConfig getConfig() {
        return null;
    }
}
