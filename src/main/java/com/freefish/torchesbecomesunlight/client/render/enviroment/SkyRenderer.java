package com.freefish.torchesbecomesunlight.client.render.enviroment;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import com.freefish.torchesbecomesunlight.server.util.storage.ClientStorage;
import com.freefish.torchesbecomesunlight.server.world.gen.biome.ModBiomes;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeRenderTypes;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

import static net.minecraft.client.renderer.LevelRenderer.getLightColor;

@OnlyIn(value = Dist.CLIENT)
public class SkyRenderer {

    public static final ResourceLocation EYES = new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/environment/eyes.png");
    public static final ResourceLocation DEMON = new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/environment/darkstream.png");

    public static final float[] rainxs = new float[1024];
    public static final float[] rainzs = new float[1024];

    public SkyRenderer() {
        for (int i = 0; i < 32; ++i) {
            for (int j = 0; j < 32; ++j) {
                float f  = j - 16;
                float f1 = i - 16;
                float f2 = Mth.sqrt(f * f + f1 * f1);
                rainxs[i << 5 | j] = -f1 / f2;
                rainzs[i << 5 | j] =   f / f2;
            }
        }
    }

    private static void renderEyes(PoseStack poseStack , BufferBuilder bufferbuilder , float demonA,float rotX,float rotY,float f12,float start,float end){
        if(demonA>= start) {
            float scale;
            if(demonA<=end)
                scale = MathUtils.easeOutExpo((demonA - start)/(end-start));
            else
                scale = 1;

            poseStack.pushPose();
            Quaternionf quatX = com.bobmowzie.mowziesmobs.client.model.tools.MathUtils.quatFromRotationXYZ(rotX, 0, 0, false);
            Quaternionf quatY = com.bobmowzie.mowziesmobs.client.model.tools.MathUtils.quatFromRotationXYZ(0, rotY, 0, false);
            Matrix4f matrix4f1 = poseStack.last().pose();
            matrix4f1.rotate(quatY);
            matrix4f1.rotate(quatX);
            bufferbuilder.vertex(matrix4f1, -f12, 100.0F, -f12*scale).uv(0.0F, 0.0F).color(1, 1, 1, 1).endVertex();
            bufferbuilder.vertex(matrix4f1, f12, 100.0F, -f12*scale).uv(1.0F, 0.0F).color(1, 1, 1, 1).endVertex();
            bufferbuilder.vertex(matrix4f1, f12, 100.0F, f12*scale).uv(1.0F, 1.0F).color(1, 1, 1, 1).endVertex();
            bufferbuilder.vertex(matrix4f1, -f12, 100.0F, f12*scale).uv(0.0F, 1.0F).color(1, 1, 1, 1).endVertex();
            poseStack.popPose();
        }
    }

    private static Random random1 = new Random(1024);

    public static boolean renderDemonSky(ClientLevel level, float partialTicks, Matrix4f modelViewMatrix, Camera camera, Matrix4f matrix, Runnable setupFog) {
        float demon = ClientStorage.INSTANCE.getDemon(partialTicks);
        float demonA;
        if(demon<60){
            demonA = 0;
        }
        else {
            demonA = (demon - 60)/100;
        }
        Minecraft minecraft = Minecraft.getInstance();
        LevelRenderer levelRenderer = minecraft.levelRenderer;
        setupFog.run();

        FogType fogType = camera.getFluidInCamera();
        if (fogType != FogType.POWDER_SNOW && fogType != FogType.LAVA) {

            PoseStack poseStack = new PoseStack();
            poseStack.mulPoseMatrix(modelViewMatrix);

            Vec3 vec3 = level.getSkyColor(minecraft.gameRenderer.getMainCamera().getPosition(), partialTicks);
            float g = (float) vec3.x;
            float h = (float) vec3.y;
            float i = (float) vec3.z;
            FogRenderer.levelFogColor();
            RenderSystem.depthMask(false);
            RenderSystem.setShaderColor(g, h, i, 1.0F);
            ShaderInstance shaderInstance = RenderSystem.getShader();
            levelRenderer.skyBuffer.bind();
            levelRenderer.skyBuffer.drawWithShader(poseStack.last().pose(), matrix, shaderInstance);
            VertexBuffer.unbind();
            RenderSystem.enableBlend();

            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);


            BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1);


            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, EYES);
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

            renderEyes(poseStack,bufferbuilder,demonA,0,0,120.0F,0.90f,1);
            //renderEyes(poseStack,bufferbuilder,demonA,1,3.14f,25.0F,0.2f,0.4f);
            //renderEyes(poseStack,bufferbuilder,demonA,1,2.14f,25.0F,0.2f,0.4f);
            //renderEyes(poseStack,bufferbuilder,demonA,1,1.14f,20.0F,0.2f,0.4f);
            //renderEyes(poseStack,bufferbuilder,demonA,1,1.54f,28.0F,0.2f,0.4f);
            //renderEyes(poseStack,bufferbuilder,demonA,1,0.54f,28.0F,0.2f,0.4f);
            //renderEyes(poseStack,bufferbuilder,demonA,1.4f,0.5f,20.0F,0,0.2f);
            //renderEyes(poseStack,bufferbuilder,demonA,1.4f,0.7f,24.0F,0,0.2f);
            //renderEyes(poseStack,bufferbuilder,demonA,1.4f,1.2f,24.0F,0,0.2f);
            //renderEyes(poseStack,bufferbuilder,demonA,1.4f,1.9f,20.0F,0,0.2f);
            //renderEyes(poseStack,bufferbuilder,demonA,1.4f,2.4f,20.0F,0,0.2f);

            BufferUploader.drawWithShader(bufferbuilder.end());

            float v = 1.0f;
            RenderSystem.setShaderColor(v, 0, 0, demonA);
            FogRenderer.setupNoFog();
            VertexBuffer.unbind();
            setupFog.run();

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();

            RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.depthMask(true);
        }
        return true;
    }

    public static void demonWeather(ClientLevel level,LightTexture pLightTexture,int ticks, float pPartialTick, double pCamX, double pCamY, double pCamZ, CallbackInfo info) {
            float demonRadio = ClientStorage.INSTANCE.getDemon(pPartialTick);
            float demonA;
            if(demonRadio>60){
                demonA = 0;
            }
            else {
                demonA = MathUtils.easeOutCubic(1 - demonRadio / 60);
            }

            pLightTexture.turnOnLightLayer();
            int i = Mth.floor(pCamX);
            int j = Mth.floor(pCamY);
            int k = Mth.floor(pCamZ);
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tesselator.getBuilder();
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            int l = 10;
            if (Minecraft.useFancyGraphics()) {
                l = 10;
            }

            RenderSystem.depthMask(Minecraft.useShaderTransparency());
            int i1 = -1;
            RenderSystem.setShader(GameRenderer::getParticleShader);
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for(int j1 = k - l; j1 <= k + l; ++j1) {
                for(int k1 = i - l; k1 <= i + l; ++k1) {
                    int l1 = (j1 - k + 16) * 32 + k1 - i + 16;
                    double rx = (double)rainxs[l1] * 2D;
                    double ry = (double)rainzs[l1] * 0.5D;
                    blockpos$mutableblockpos.set((double)k1, pCamY, (double)j1);
                    Biome biome = level.getBiome(blockpos$mutableblockpos).value();
                    RegistryAccess access = level.registryAccess();
                    ResourceLocation biomeLocation = access.registryOrThrow(Registries.BIOME).getKey(biome);

                    if (biomeLocation.toString().equals("torchesbecomesunlight:demon_biome")) {
                        int groundY = 0;
                        int minY = j - l;
                        int maxY = j + l;

                        if (minY < groundY) {
                            minY = groundY;
                        }

                        if (maxY < groundY) {
                            maxY = groundY;
                        }


                        if (minY != maxY) {
                            random1.setSeed((long) k1 * k1 * 3121 + k1 * 45238971L ^ (long) j1 * j1 * 418711 + j1 * 13761L);

                            blockpos$mutableblockpos.set(k1, minY, j1);
                                if (i1 != 0) {
                                    if (i1 >= 0) {
                                        tesselator.end();
                                    }

                                    i1 = 0;
                                    RenderSystem.setShaderTexture(0, DEMON);
                                    bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
                                }

                            float d8 = -((ticks & 511) + pPartialTick) / 512.0F;
                            float d9 = 0;
                            float d10 = random1.nextFloat() + ((ticks+pPartialTick) * (float) random1.nextGaussian()) * 0.001F;
                            double d11 = k1 + 0.5F - pCamX;
                            double d12 = j1 + 0.5F - pCamZ;
                            float f6 = Mth.sqrt((float) (d11 * d11 + d12 * d12)) / l;
                            float f5 = Mth.clamp(((float)Math.sin(f6*2.7f)) * random1.nextFloat(),0,1)*demonA;
                            int i4 = 15 << 20 | 15 << 4;
                            int j4 = i4 >> 16 & 65535;
                            int k4 = i4 & 65535;
                            float z1 = (float)pCamY * 0.125F;
                            bufferbuilder.vertex(k1 - pCamX - rx + 0.5D, l, j1 - pCamZ - ry + 0.5D).uv(0.0F + d9, d8 + d10 - z1).color(1.0F, 1.0F, 1.0F, f5).uv2(j4, k4).endVertex();
                            bufferbuilder.vertex(k1 - pCamX + rx + 0.5D, l, j1 - pCamZ + ry + 0.5D).uv(1.0F + d9, d8 + d10 - z1).color(1.0F, 1.0F, 1.0F, f5).uv2(j4, k4).endVertex();
                            float pV = l * 0.25F + d8 + d10 - z1;
                            bufferbuilder.vertex(k1 - pCamX + rx + 0.5D, -l, j1 - pCamZ + ry + 0.5D).uv(1.0F + d9, pV).color(1.0F, 1.0F, 1.0F, f5).uv2(j4, k4).endVertex();
                            bufferbuilder.vertex(k1 - pCamX - rx + 0.5D, -l, j1 - pCamZ - ry + 0.5D).uv(0.0F + d9, pV).color(1.0F, 1.0F, 1.0F, f5).uv2(j4, k4).endVertex();
                        }
                    }
                }
            }
            if (i1 >= 0) {
                tesselator.end();
            }
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
            pLightTexture.turnOffLightLayer();
    }
}
