package com.freefish.torchesbecomesunlight.server.init;

import com.freefish.torchesbecomesunlight.server.config.ConfigHandler;
import com.freefish.torchesbecomesunlight.server.entity.FreeFishEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

public class SpawnHandler {

    public static void registerSpawnPlacementTypes() {
        //SpawnPlacements.Type.create("GTBS_SPAWN", new TriPredicate<LevelReader, BlockPos, EntityType<? extends Mob>>() {
        //    @Override
        //    public boolean test(LevelReader t, BlockPos pos, EntityType<? extends Mob> entityType) {
        //        BlockState block = t.getBlockState(pos.below());
        //        if (block.getBlock() == Blocks.BEDROCK || block.getBlock() == Blocks.BARRIER || !block.blocksMotion())
        //            return false;
        //        BlockState iblockstateUp = t.getBlockState(pos);
        //        BlockState iblockstateUp2 = t.getBlockState(pos.above());
        //        return NaturalSpawner.isValidEmptySpawnBlock(t, pos, iblockstateUp, iblockstateUp.getFluidState(), entityType) && NaturalSpawner.isValidEmptySpawnBlock(t, pos.above(), iblockstateUp2, iblockstateUp2.getFluidState(), entityType);
        //    }
        //});
        //SpawnPlacements.Type mmSpawn = SpawnPlacements.Type.valueOf("GTBS_SPAWN");
        //if (mmSpawn != null) {
        //    SpawnPlacements.register(EntityHandle.FROST_NOVA.get(), mmSpawn, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, FreeFishEntity::spawnPredicate);
        //}
    }

    public static void addBiomeSpawns(Holder<Biome> biomeKey, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if(biomeKey.is(Biomes.SNOWY_PLAINS)) {
            registerEntityWorldSpawn(builder, EntityHandle.FROST_NOVA.get(), ConfigHandler.COMMON.MOBS.FROSTNOVA.spawnConfig, MobCategory.MONSTER);
            registerEntityWorldSpawn(builder, EntityHandle.PATRIOT.get(), ConfigHandler.COMMON.MOBS.PATRIOT.spawnConfig, MobCategory.MONSTER);
        }
        if(biomeKey.is(BiomeTags.HAS_IGLOO)) {
            registerEntityWorldSpawn(builder, EntityHandle.PURSUER.get(), ConfigHandler.COMMON.MOBS.PURSUER.spawnConfig, MobCategory.MONSTER);
        }
    }
    private static void registerEntityWorldSpawn(ModifiableBiomeInfo.BiomeInfo.Builder builder, EntityType<?> entity, ConfigHandler.SpawnConfig spawnConfig, MobCategory classification) {
        builder.getMobSpawnSettings().getSpawner(classification).add(new MobSpawnSettings.SpawnerData(entity, spawnConfig.spawnRate.get(), 1, 1));
    }
}
