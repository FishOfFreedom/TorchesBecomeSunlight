package com.freefish.torchesbecomesunlight.server.world.structure;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class StructureHandle {
    public static final DeferredRegister<StructureType<?>> DEFERRED_REGISTRY_STRUCTURE = DeferredRegister.create(Registries.STRUCTURE_TYPE, TorchesBecomeSunlight.MOD_ID);

    public static final RegistryObject<StructureType<SanktaStatueStructures>> SANKTA_STATUE = DEFERRED_REGISTRY_STRUCTURE.register("sankta_statue", () -> explicitStructureTypeTyping(SanktaStatueStructures.CODEC));
    public static final RegistryObject<StructureType<UrsusVillageStructures>> URSUS_VILLAGE = DEFERRED_REGISTRY_STRUCTURE.register("ursus_village", () -> explicitStructureTypeTyping(UrsusVillageStructures.CODEC));
    public static final RegistryObject<StructureType<RhodeTrainingGroundStructures>> RHODE_ISLAND_OFFICE = DEFERRED_REGISTRY_STRUCTURE.register("rhode_island_office", () -> explicitStructureTypeTyping(RhodeTrainingGroundStructures.CODEC));

    public static final ResourceKey<Structure> RHODE_ISLAND_OFFICE_K = ResourceKey.create(Registries.STRUCTURE, new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "rhode_island_office"));
    public static final ResourceKey<Structure> SANKTA_STATUE_K = ResourceKey.create(Registries.STRUCTURE, new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "sankta_statue"));

    private static <T extends Structure> StructureType<T> explicitStructureTypeTyping(Codec<T> structureCodec) {
        return () -> structureCodec;
    }
}
