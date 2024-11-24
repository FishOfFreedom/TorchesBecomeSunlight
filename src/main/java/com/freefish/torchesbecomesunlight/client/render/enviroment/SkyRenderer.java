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

import java.util.Random;

import static net.minecraft.client.renderer.LevelRenderer.getLightColor;

@OnlyIn(value = Dist.CLIENT)
public class SkyRenderer {

    private static VertexBuffer starBuffer;

    public static final ResourceLocation EYES = new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/environment/eyes.png");
    public static final ResourceLocation DEMON = new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/environment/darkstream.png");
            //new ResourceLocation[]{tem(1),tem(2),tem(3),tem(4),tem(5),
            //tem(6),tem(7),tem(8),tem(9),tem(10)};

    private static ResourceLocation tem(int i){
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/environment/demon/TransparentRectangle"+i+".png");
    }

    public static final float[] rainxs = new float[1024];
    public static final float[] rainzs = new float[1024];

    public SkyRenderer() {
        this.createStars();
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
    public static boolean renderSky(ClientLevel level, float partialTicks, PoseStack stack, Camera camera, Matrix4f projectionMatrix, Runnable setupFog) {
        LevelRenderer levelRenderer = Minecraft.getInstance().levelRenderer;

        setupFog.run();
        Vec3 vec3 = level.getSkyColor(camera.getPosition(), partialTicks);
        float f = (float) vec3.x();
        float f1 = (float) vec3.y();
        float f2 = (float) vec3.z();
        FogRenderer.levelFogColor();
        RenderSystem.depthMask(false);
        RenderSystem.setShaderColor(f, f1, f2, 1.0F);
        ShaderInstance shaderinstance = RenderSystem.getShader();
        levelRenderer.skyBuffer.bind();
        levelRenderer.skyBuffer.drawWithShader(stack.last().pose(), projectionMatrix, shaderinstance);
        VertexBuffer.unbind();
        RenderSystem.enableBlend();

        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        stack.pushPose();
        float f11 = 1.0F - level.getRainLevel(partialTicks);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, f11);
        stack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        stack.mulPose(Axis.XP.rotationDegrees(level.getTimeOfDay(partialTicks) * 360.0F));
        float f10 = 1.0F;
        RenderSystem.setShaderColor(f10, f10, f10, f10);
        RenderSystem.setShaderColor(f10, f10, f10, f10);
        FogRenderer.setupNoFog();
        starBuffer.bind();
        starBuffer.drawWithShader(stack.last().pose(), projectionMatrix, GameRenderer.getPositionShader());
        VertexBuffer.unbind();
        setupFog.run();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        stack.popPose();
        RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
        double d0 = camera.getEntity().getEyePosition(partialTicks).y();
        if (d0 < 0.0D) {
            stack.pushPose();
            stack.translate(0.0F, 12.0F, 0.0F);
            levelRenderer.darkBuffer.bind();
            levelRenderer.darkBuffer.drawWithShader(stack.last().pose(), projectionMatrix, shaderinstance);
            VertexBuffer.unbind();
            stack.popPose();
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.depthMask(true);
        return true;
    }

    private static void renderEyes(Matrix4f view){
        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer portalStatic = multibuffersource$buffersource.getBuffer(ForgeRenderTypes.getUnlitTranslucent(EYES));
        PoseStack posestack = new PoseStack();
        posestack.mulPoseMatrix(view);
        PoseStack.Pose pose = posestack.last();
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();
        float l = 60.0F;
        portalStatic.vertex(matrix4f, -l, 100.0F, -l).color(1,1,1,1).uv(0.0F, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
        portalStatic.vertex(matrix4f,  l, 100.0F, -l).color(1,1,1,1).uv(1.0F, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
        portalStatic.vertex(matrix4f,  l, 100.0F,  l).color(1,1,1,1).uv(1.0F, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
        portalStatic.vertex(matrix4f, -l, 100.0F,  l).color(1,1,1,1).uv(0.0F, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();

        multibuffersource$buffersource.endBatch();
    }

    // [VanillaCopy] LevelRenderer.createStars
    private void createStars() {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionShader);
        if (starBuffer != null) {
            starBuffer.close();
        }

        starBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        BufferBuilder.RenderedBuffer renderedBuffer = this.drawStars(bufferbuilder);
        starBuffer.bind();
        starBuffer.upload(renderedBuffer);
        VertexBuffer.unbind();
    }

    // [VanillaCopy] of LevelRenderer.drawStars but with double the number of them
    private BufferBuilder.RenderedBuffer drawStars(BufferBuilder bufferBuilder) {
        RandomSource random = RandomSource.create(10842L);

        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

        // TF - 1500 -> 3000
        for (int i = 0; i < 1500; ++i) {
            double d0 = random.nextFloat() * 2.0F - 1.0F;
            double d1 = random.nextFloat() * 2.0F - 1.0F;
            double d2 = random.nextFloat() * 2.0F - 1.0F;
            double d3 = 0.15F + random.nextFloat() * 0.1F;
            double d4 = d0 * d0 + d1 * d1 + d2 * d2;
            if (d4 < 1.0D && d4 > 0.01D) {
                d4 = 1.0D / Math.sqrt(d4);
                d0 *= d4;
                d1 *= d4;
                d2 *= d4;
                double d5 = d0 * 100.0D;
                double d6 = d1 * 100.0D;
                double d7 = d2 * 100.0D;
                double d8 = Math.atan2(d0, d2);
                double d9 = Math.sin(d8);
                double d10 = Math.cos(d8);
                double d11 = Math.atan2(Math.sqrt(d0 * d0 + d2 * d2), d1);
                double d12 = Math.sin(d11);
                double d13 = Math.cos(d11);
                double d14 = random.nextDouble() * Math.PI * 2.0D;
                double d15 = Math.sin(d14);
                double d16 = Math.cos(d14);

                for (int j = 0; j < 4; ++j) {
                    double d18 = ((j & 2) - 1) * d3;
                    double d19 = ((j + 1 & 2) - 1) * d3;
                    double d21 = d18 * d16 - d19 * d15;
                    double d22 = d19 * d16 + d18 * d15;
                    double d23 = d21 * d12 + 0.0D * d13;
                    double d24 = 0.0D * d12 - d21 * d13;
                    double d25 = d24 * d9 - d22 * d10;
                    double d26 = d22 * d9 + d24 * d10;
                    bufferBuilder.vertex(d5 + d25, d6 + d23, d7 + d26).endVertex();
                }
            }
        }

        return bufferBuilder.end();
    }

    private static final float TIME = 12500f;
    private static final float FIXED_TIME = (float) (Mth.frac(TIME / 24000.0 - 0.25) * 2.0 + 0.5 - Math.cos(Mth.frac(TIME / 24000.0 - 0.25) * Math.PI) / 2.0) / 3.0F;
    private static Random random1 = new Random(1024);

    public static boolean renderDemonSky(ClientLevel level, float partialTicks, Matrix4f modelViewMatrix, Camera camera, Matrix4f matrix, Runnable setupFog) {
        int demonRadio = ClientStorage.INSTANCE.demonRadio;
        float demonA;
        if(demonRadio<40){
            demonA = 0;
        }
        else {
            demonA = MathUtils.fade(((demonRadio+partialTicks-40)/81));
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
            Tesselator tesselator = Tesselator.getInstance();
            RenderSystem.depthMask(false);
            RenderSystem.setShaderColor(g, h, i, 1.0F);
            ShaderInstance shaderInstance = RenderSystem.getShader();
            levelRenderer.skyBuffer.bind();
            levelRenderer.skyBuffer.drawWithShader(poseStack.last().pose(), matrix, shaderInstance);
            VertexBuffer.unbind();
            RenderSystem.enableBlend();
            float[] fs = level.effects().getSunriseColor(FIXED_TIME, partialTicks);
            float j;
            float l;
            float p;
            float q;
            float r;
            if (fs != null) {
                RenderSystem.setShader(GameRenderer::getPositionColorShader);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                poseStack.pushPose();
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                j = Mth.sin(level.getSunAngle(partialTicks)) < 0.0F ? 180.0F : 0.0F;
                poseStack.mulPose(Axis.ZP.rotationDegrees(j));
                poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
                float k = fs[0];
                l = fs[1];
                float m = fs[2];
                Matrix4f matrix4f3 = poseStack.last().pose();
                BufferBuilder bufferBuilder = tesselator.getBuilder();
                bufferBuilder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
                bufferBuilder.vertex(matrix4f3, 0.0F, 100.0F, 0.0F).color(k, l, m, fs[3]);

                for (int o = 0; o <= 16; ++o) {
                    p = (float) o * 6.2831855F / 16.0F;
                    q = Mth.sin(p);
                    r = Mth.cos(p);
                    bufferBuilder.vertex(matrix4f3, q * 120.0F, r * 120.0F, -r * 40.0F * fs[3]).color(fs[0], fs[1], fs[2], 0.0F);
                }

                BufferUploader.drawWithShader(bufferBuilder.end());
                poseStack.popPose();
            }

            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            poseStack.pushPose();

            BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, demonA);

            float f12 = 160.0F;
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, EYES);
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            Matrix4f matrix4f1 = poseStack.last().pose();
            bufferbuilder.vertex(matrix4f1, -f12, 100.0F, -f12).uv(0.0F, 0.0F).color(1,1,1,demonA).endVertex();
            bufferbuilder.vertex(matrix4f1, f12, 100.0F, -f12).uv(1.0F, 0.0F) .color(1,1,1,demonA).endVertex();
            bufferbuilder.vertex(matrix4f1, f12, 100.0F, f12).uv(1.0F, 1.0F)  .color(1,1,1,demonA).endVertex();
            bufferbuilder.vertex(matrix4f1, -f12, 100.0F, f12).uv(0.0F, 1.0F) .color(1,1,1,demonA).endVertex();
            BufferUploader.drawWithShader(bufferbuilder.end());

            float v = 1.0f;
            RenderSystem.setShaderColor(v, 0, 0, demonA);
            FogRenderer.setupNoFog();
            starBuffer.bind();
            starBuffer.drawWithShader(poseStack.last().pose(), matrix, GameRenderer.getPositionShader());
            VertexBuffer.unbind();
            setupFog.run();

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
            poseStack.popPose();
            RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.depthMask(true);
        }

        return true;
    }

    public static void demonWeather(ClientLevel level,LightTexture pLightTexture,int ticks, float pPartialTick, double pCamX, double pCamY, double pCamZ) {
            int demonRadio = ClientStorage.INSTANCE.demonRadio;
            float demonA = 1;
            if(demonRadio>80){
                demonA = 0;
            }
            else {
                demonA = MathUtils.fade(((81-demonRadio+pPartialTick)/81));
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
