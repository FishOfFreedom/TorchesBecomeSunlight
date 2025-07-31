package com.freefish.torchesbecomesunlight.server.world.structure;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.config.ConfigHandler;
import com.freefish.torchesbecomesunlight.server.world.structure.jigsaw.RhodesOfficeJigsawPlacement;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.StructureType;

import java.util.Optional;
import java.util.function.Predicate;

public class RhodeTrainingGroundStructures extends FFStructures {
    public static final Codec<RhodeTrainingGroundStructures> CODEC = simpleCodec(RhodeTrainingGroundStructures::new);

    public RhodeTrainingGroundStructures(StructureSettings settings) {
        super(settings, ConfigHandler.COMMON.GLOBALSETTING.rhodeTrainingGround, true, true, true);
    }

    public static Optional<GenerationStub> createPiecesGenerator(Predicate<GenerationContext> canGeneratePredicate, GenerationContext context, FFStructures ffStructures) {

        if (!canGeneratePredicate.test(context)) {
            return Optional.empty();
        }

        GenerationContext newContext = new GenerationContext(
                context.registryAccess(),
                context.chunkGenerator(),
                context.biomeSource(),
                context.randomState(),
                context.structureTemplateManager(),
                context.random(),
                context.seed(),
                context.chunkPos(),
                context.heightAccessor(),
                context.validBiome()
        );

        BlockPos blockpos = context.chunkPos().getMiddleBlockPosition(0);

        Optional<GenerationStub> structurePiecesGenerator =
                RhodesOfficeJigsawPlacement.addPieces(
                        newContext,
                        Holder.direct(context.registryAccess().registryOrThrow(Registries.TEMPLATE_POOL)
                                .get(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "banshichu/office"))),
                        Optional.empty(),
                        16,
                        blockpos,
                        false,
                        Optional.of(Heightmap.Types.WORLD_SURFACE),
                        128,
                        ffStructures
                );

        return structurePiecesGenerator;
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        return createPiecesGenerator(t -> checkLocation(t), context,this);
    }

    @Override
    public StructureType<?> type() {
        return StructureHandle.RHODE_ISLAND_OFFICE.get();
    }

    @Override
    public GenerationStep.Decoration step() {
        return GenerationStep.Decoration.SURFACE_STRUCTURES;
    }
}
