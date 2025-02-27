package com.freefish.torchesbecomesunlight.server.world.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.Optional;

public class SanktaStatueStructures extends FFStructures {
    public static final Codec<SanktaStatueStructures> CODEC = RecordCodecBuilder.<SanktaStatueStructures>mapCodec(instance ->
            instance.group(SanktaStatueStructures.settingsCodec(instance),
                    StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(FFStructures::getStartPool),
                    ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(FFStructures::getStartJigsawName),
                    Codec.intRange(0, 30).fieldOf("size").forGetter(FFStructures::getSize),
                    HeightProvider.CODEC.fieldOf("start_height").forGetter(FFStructures::getStartHeight),
                    Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(FFStructures::getProjectStartToHeightmap),
                    Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(FFStructures::getMaxDistanceFromCenter)
            ).apply(instance, SanktaStatueStructures::new)).codec();

    public SanktaStatueStructures(StructureSettings config, Holder<StructureTemplatePool> startPool, Optional<ResourceLocation> startJigsawName, int size, HeightProvider startHeight, Optional<Heightmap.Types> projectStartToHeightmap, int maxDistanceFromCenter) {
        super(config, startPool, startJigsawName, size, startHeight, projectStartToHeightmap, maxDistanceFromCenter);
    }

    @Override
    public StructureType<?> type() {
        return StructureHandle.SANKTA_STATUE.get();
    }
}
