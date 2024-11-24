package com.freefish.torchesbecomesunlight.server.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.FillBiomeCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.maven.artifact.versioning.Restriction;
import org.joml.*;
import software.bernie.geckolib.cache.object.GeoBone;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public final class MathUtils {

    public static final float TAU = (float) (2 * StrictMath.PI);
    public static final float PI = (float) StrictMath.PI;

    public static double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    public static void matrixStackFromModel(PoseStack matrixStack, GeoBone geoBone) {
        GeoBone parent = geoBone.getParent();
        if (parent != null) matrixStackFromModel(matrixStack, parent);
        translateRotateGeckolib(geoBone, matrixStack);
    }

    public static int fill(ServerLevel serverLevel, BlockPos pFrom, BlockPos pTo, Holder.Reference<Biome> pBiome) {
        BlockPos blockpos = quantize(pFrom);
        BlockPos blockpos1 = quantize(pTo);
        BoundingBox boundingbox = BoundingBox.fromCorners(blockpos, blockpos1);

        List<ChunkAccess> list = new ArrayList<>();

        for(int k = SectionPos.blockToSectionCoord(boundingbox.minZ()); k <= SectionPos.blockToSectionCoord(boundingbox.maxZ()); ++k) {
            for(int l = SectionPos.blockToSectionCoord(boundingbox.minX()); l <= SectionPos.blockToSectionCoord(boundingbox.maxX()); ++l) {
                ChunkAccess chunkaccess = serverLevel.getChunk(l, k, ChunkStatus.FULL, false);
                list.add(chunkaccess);
            }
        }

        MutableInt mutableint = new MutableInt(0);
        for(ChunkAccess chunkaccess1 : list) {
            chunkaccess1.fillBiomesFromNoise(makeResolver(mutableint, chunkaccess1, boundingbox, pBiome,biomeHolder -> true), serverLevel.getChunkSource().randomState().sampler());
            chunkaccess1.setUnsaved(true);
        }
        serverLevel.getChunkSource().chunkMap.resendBiomesForChunks(list);
        return mutableint.getValue();
    }

    private static BiomeResolver makeResolver(MutableInt pBiomeEntries, ChunkAccess pChunk, BoundingBox pTargetRegion, Holder<Biome> pReplacementBiome, Predicate<Holder<Biome>> pFilter) {
        return (p_262550_, p_262551_, p_262552_, p_262553_) -> {
            int i = QuartPos.toBlock(p_262550_);
            int j = QuartPos.toBlock(p_262551_);
            int k = QuartPos.toBlock(p_262552_);
            Holder<Biome> holder = pChunk.getNoiseBiome(p_262550_, p_262551_, p_262552_);
            if (pTargetRegion.isInside(i, j, k) && pFilter.test(holder)) {
                pBiomeEntries.increment();
                return pReplacementBiome;
            } else {
                return holder;
            }
        };
    }

    private static BlockPos quantize(BlockPos pPos) {
        return new BlockPos(quantize(pPos.getX()), quantize(pPos.getY()), quantize(pPos.getZ()));
    }

    private static int quantize(int pValue) {
        return QuartPos.toBlock(QuartPos.fromBlock(pValue));
    }

    public static Vec3 getWorldPosFromModel(Entity entity, float entityYaw, GeoBone geoBone) {
        PoseStack matrixStack = new PoseStack();
        matrixStack.translate(entity.getX(), entity.getY(), entity.getZ());
        matrixStack.mulPose(MathUtils.quatFromRotationXYZ(0,   -entityYaw - 180, 180, true));
        matrixStack.scale(-1, -1, 1);
        matrixStack.translate(0, -1.5f, 0);
        MathUtils.matrixStackFromModel(matrixStack, geoBone);
        PoseStack.Pose matrixEntry = matrixStack.last();
        Matrix4f matrix4f = matrixEntry.pose();

        Vector4f vec = new Vector4f(0, 0, 0, 1);
        vec.mul(matrix4f);
        return new Vec3(vec.x(), vec.y()+1.5, vec.z());
    }

    public static <T extends Entity> T getClosestEntity(Entity target, List<T> entities) {
        T closestEntity = null;
        double closestDistanceSq = Double.MAX_VALUE;

        for (T entity : entities) {
            if (entity != target) {
                double distanceSq = entity.distanceToSqr(target);
                if (distanceSq < closestDistanceSq) {
                    closestDistanceSq = distanceSq;
                    closestEntity = entity;
                }
            }
        }

        return closestEntity;
    }

    public static <T extends Entity> T getClosestEntity(Vec3 target, List<T> entities) {
        T closestEntity = null;
        double closestDistanceSq = Double.MAX_VALUE;

        for (T entity : entities) {
            double distanceSq = entity.distanceToSqr(target);
            if (distanceSq < closestDistanceSq) {
                closestDistanceSq = distanceSq;
                closestEntity = entity;
            }
        }

        return closestEntity;
    }

    public static Quaternionf quatFromRotationXYZ(float x, float y, float z, boolean degrees) {
        if (degrees) {
            x *= ((float)Math.PI / 180F);
            y *= ((float)Math.PI / 180F);
            z *= ((float)Math.PI / 180F);
        }
        return (new Quaternionf()).rotationXYZ(x, y, z);
    }

    public static void translateRotateGeckolib(GeoBone bone, PoseStack matrixStackIn) {
        GeoBone parent = bone.getParent();
        if(parent != null)
            matrixStackIn.translate((double)((bone.getPivotX()-parent.getPivotX())/ 16.0F),
                                    (double)((bone.getPivotY()-parent.getPivotY()) / 16.0F),
                                    (double)((bone.getPivotZ()-parent.getPivotZ()) / 16.0F));
        else {
            matrixStackIn.translate((double) -(bone.getPosX() / 16.0F), (double) (bone.getPosY() / 16.0F), (double) (bone.getPosZ() / 16.0F));
        }
        if (bone.getRotZ() != 0.0F) {
            matrixStackIn.mulPose(Axis.ZP.rotation(bone.getRotZ()));
        }

        if (bone.getRotY() != 0.0F) {
            matrixStackIn.mulPose(Axis.YP.rotation(bone.getRotY()));
        }

        if (bone.getRotX() != 0.0F) {
            matrixStackIn.mulPose(Axis.XP.rotation(bone.getRotX()));
        }

        matrixStackIn.scale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
    }

    public static float fade(float i){
        i=i/2 + 0.5f;
        return (6*i*i*i*i*i-15*i*i*i*i+10*i*i*i - 0.5f)*2;
    }

    public static float easeInQuint(float x) {
        return x * x * x * x * x;
    }

    public static float easeOutBack(float x) {
        float c1 = 1.70158f;
        float c3 = c1 + 1;
        return 1 + c3 * (float) Math.pow(x - 1, 3) + c1 * (float)Math.pow(x - 1, 2);
    }

    public static float easeOutCirc(float x) {
        return (float) Math.sqrt(1 - Math.pow(x - 1, 2));
    }

    public static float easeInExpo(float x) {
        return (float) (x == 0 ? 0 : Math.pow(2, 10 * x - 10));
    }

    public static float easeOutExpo(float x) {
        return (float) (x == 1 ? 1 : 1 - Math.pow(2, -10 * x));
    }

    public static double wrapDegrees(double value) {
        value %= 360.0;
        if (value >= 180.0) {
            value -= 360.0;
        }

        if (value < -180.0) {
            value += 360.0;
        }

        return value;
    }

    public static boolean isInDemon(LivingEntity livingEntity) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        blockpos$mutableblockpos.set(livingEntity.getX(),livingEntity.getY(),livingEntity.getZ());
        Biome biome = livingEntity.level().getBiome(blockpos$mutableblockpos).value();
        RegistryAccess access = livingEntity.level().registryAccess();
        ResourceLocation biomeLocation = access.registryOrThrow(Registries.BIOME).getKey(biome);
        return biomeLocation.toString().equals("torchesbecomesunlight:demon_biome");
    }

    public static BlockPos getFirstBlockAbove(Level world, BlockPos pos) {
        BlockPos posCurrent = null;
        for (int y = pos.getY() + 1; y < world.getMaxBuildHeight(); y++) {
            posCurrent = new BlockPos(pos.getX(), y, pos.getZ());
            if (world.getBlockState(posCurrent).isAir() &&
                    world.getBlockState(posCurrent.above()).isAir() &&
                    !world.getBlockState(posCurrent.below()).isAir()) {
                return posCurrent;
            }
        }
        return null;
    }

    public static Vec3 getFirstBlockAbove(Level world, Vec3 pos,int limit) {
        BlockPos posCurrent = null;
        int y1 = (int) pos.y;
        int x1 = (int) pos.y;
        int z1 = (int) pos.y;
        for (int y = y1 + 1; y < y1 + limit; y++) {
            posCurrent = new BlockPos(x1, y, z1);
            if (world.getBlockState(posCurrent).isAir() &&
                    world.getBlockState(posCurrent.above()).isAir() &&
                    !world.getBlockState(posCurrent.below()).isAir()) {
                return new Vec3(pos.x,posCurrent.getY(),pos.z);
            }
        }
        return pos;
    }
}
