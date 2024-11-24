package com.freefish.torchesbecomesunlight.client.render.enviroment;

import com.freefish.torchesbecomesunlight.server.world.gen.biome.ModBiomes;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import javax.annotation.Nullable;
import java.util.Optional;

public class DemonRenderInfo extends DimensionSpecialEffects {

    public DemonRenderInfo(float cloudHeight, boolean placebo, SkyType fogType, boolean brightenLightMap, boolean entityLightingBottomsLit) {
        super(cloudHeight, placebo, fogType, brightenLightMap, entityLightingBottomsLit);
    }

    @Nullable
    @Override
    public float[] getSunriseColor(float daycycle, float partialTicks) { // Fog color
        return null;
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 biomeFogColor, float daylight) { // For modifying biome fog color with daycycle
        return biomeFogColor.multiply(daylight * 0.94F + 0.06F, (daylight * 0.94F + 0.06F), (daylight * 0.91F + 0.09F));
    }

    @Override
    public boolean isFoggyAt(int x, int y) { // true = nearFog
        Player player = Minecraft.getInstance().player;

        if (player != null) {
            Optional<ResourceKey<Biome>> biome = player.level().getBiome(player.blockPosition()).unwrapKey();
            if (biome.isPresent()) {
                //boolean spooky = biome.get() == TFBiomes.SPOOKY_FOREST;
//
                //if (player.position().y > 20 && !spooky) {
                //    return false; // If player is above the dark forest then no need to make it so spooky. The darkwood leaves cover everything as low as y42.
                //}

                return biome.get() == ModBiomes.DEMON_BIOME;
            }
        }

        return false;

        //Make the fog on these biomes much much darker, maybe pitch black even. Do we keep this harsher fog underground too?
    }

    @Override
    public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
        return SkyRenderer.renderSky(level, partialTick, poseStack, camera, projectionMatrix, setupFog);
    }
}
