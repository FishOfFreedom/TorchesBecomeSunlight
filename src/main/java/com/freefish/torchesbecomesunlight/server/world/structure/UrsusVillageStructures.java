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
import net.minecraft.world.level.levelgen.structure.StructureType;

import java.util.Optional;
import java.util.function.Predicate;

public class UrsusVillageStructures extends FFStructures {
    public static final Codec<UrsusVillageStructures> CODEC = simpleCodec(UrsusVillageStructures::new);

    public UrsusVillageStructures(StructureSettings settings) {
        super(settings, ConfigHandler.COMMON.GLOBALSETTING.sanktaStatue, true, true, true);
    }

    public static Optional<GenerationStub> createPiecesGenerator(Predicate<GenerationContext> canGeneratePredicate, GenerationContext context,FFStructures ffStructures) {

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
                TBSJigsawPlacement.addPieces(
                        newContext,
                        Holder.direct(context.registryAccess().registryOrThrow(Registries.TEMPLATE_POOL)
                                .get(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "ursus_village/center"))),
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
        return StructureHandle.URSUS_VILLAGE.get();
    }
}
