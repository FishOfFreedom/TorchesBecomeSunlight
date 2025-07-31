package com.freefish.torchesbecomesunlight.server.world.structure.processor;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class BaseDecoProcessor extends StructureProcessor {
    public static final BaseDecoProcessor INSTANCE = new BaseDecoProcessor();
    public static final Codec<BaseDecoProcessor> CODEC = Codec.unit(() -> INSTANCE);

    protected StructureProcessorType<?> getType() {
        return ProcessorHandler.BASE_DECO_PROCESSOR;
    }

    @Override
    public StructureTemplate.StructureBlockInfo process(LevelReader levelReader, BlockPos jigsawPiecePos, BlockPos pos,
                                                        StructureTemplate.StructureBlockInfo blockInfoLocal, StructureTemplate.StructureBlockInfo blockInfoGlobal,
                                                        StructurePlaceSettings structurePlacementData, StructureTemplate template) {
        BlockState state = blockInfoLocal.state();

        BlockPos blockPos = blockInfoGlobal.pos();

        boolean waterAt = levelReader.isWaterAt(blockPos);
        if(waterAt){
            if(levelReader instanceof WorldGenRegion worldGenRegion){
                worldGenRegion.setBlock(blockPos, Blocks.AIR.defaultBlockState(),3);
            }
        }
        return blockInfoGlobal;
    }
}
