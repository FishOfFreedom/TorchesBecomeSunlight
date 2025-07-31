package com.freefish.torchesbecomesunlight.server.world.structure;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.config.ConfigHandler;
import com.freefish.torchesbecomesunlight.server.world.structure.jigsaw.TBSJigsawPlacement;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

import java.util.Optional;
import java.util.function.Predicate;

public class SanktaStatueStructures extends FFStructures {
    public static final Codec<SanktaStatueStructures> CODEC = simpleCodec(SanktaStatueStructures::new);

    public SanktaStatueStructures(StructureSettings settings) {
        super(settings, ConfigHandler.COMMON.GLOBALSETTING.sanktaStatue, true, true, true,66,2);
    }

    public static Optional<Structure.GenerationStub> createPiecesGenerator(Predicate<GenerationContext> canGeneratePredicate, Structure.GenerationContext context,FFStructures ffStructures) {

        if (!canGeneratePredicate.test(context)) {
            return Optional.empty();
        }

        Structure.GenerationContext newContext = new Structure.GenerationContext(
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

        Optional<Structure.GenerationStub> structurePiecesGenerator =
                TBSJigsawPlacement.addPieces(
                        newContext,
                        Holder.direct(context.registryAccess().registryOrThrow(Registries.TEMPLATE_POOL)
                                .get(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "sankta_statue/center"))),
                        Optional.empty(),
                        12,
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
        return StructureHandle.SANKTA_STATUE.get();
    }
}
