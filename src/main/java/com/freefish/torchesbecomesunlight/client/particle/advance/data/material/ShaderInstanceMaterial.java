package com.freefish.torchesbecomesunlight.client.particle.advance.data.material;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.shader.management.ShaderManager;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author KilaBash
 * @date 2023/5/29
 * @implNote MaterialsResource
 */
@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
public abstract class ShaderInstanceMaterial implements IMaterial {

    abstract public ShaderInstance getShader();

    public void setupUniform() {
    }

    @Override
    public void begin(boolean isInstancing) {
        if (TorchesBecomeSunlight.isUsingShaderPack()) {
            var lastShader = RenderSystem.getShader();

            ShaderManager.getTempTarget().clear(false);
            ShaderManager.getTempTarget().bindWrite(true);
            int lastID = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);

            float imageU = 0;
            float imageV = 0;
            float imageWidth = 1;
            float imageHeight = 1;
            Tesselator tessellator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuilder();
            RenderSystem.setShader(this::getShader);
            setupUniform();

            RenderSystem.backupProjectionMatrix();
            RenderSystem.setProjectionMatrix(new Matrix4f(), VertexSorting.DISTANCE_TO_ORIGIN);

            var stack = RenderSystem.getModelViewStack();
            stack.pushPose();
            stack.setIdentity();
            RenderSystem.applyModelViewMatrix();

            var lightTexture = Minecraft.getInstance().gameRenderer.lightTexture();
            lightTexture.turnOnLightLayer();
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);

            bufferbuilder.vertex(-1, -1, 0).uv(imageU, imageV).color(-1).uv2(LightTexture.FULL_BRIGHT).endVertex();
            bufferbuilder.vertex(1, -1, 0).uv(imageU + imageWidth, imageV).color(-1).uv2(LightTexture.FULL_BRIGHT).endVertex();
            bufferbuilder.vertex(1, 1, 0).uv(imageU + imageWidth, imageV + imageHeight).color(-1).uv2(LightTexture.FULL_BRIGHT).endVertex();
            bufferbuilder.vertex(-1, 1, 0).uv(imageU, imageV + imageHeight).color(-1).uv2(LightTexture.FULL_BRIGHT).endVertex();

            tessellator.end();
            lightTexture.turnOffLightLayer();
            RenderSystem.restoreProjectionMatrix();

            stack.popPose();
            RenderSystem.applyModelViewMatrix();

            GlStateManager._glBindFramebuffer(36160, lastID);
            if (!ShaderManager.getInstance().hasViewPort()) {
                var mainTarget = Minecraft.getInstance().getMainRenderTarget();
                GlStateManager._viewport(0, 0, mainTarget.viewWidth, mainTarget.viewHeight);
            }

            RenderSystem.setShaderTexture(0, ShaderManager.getTempTarget().getColorTextureId());
            RenderSystem.setShader(() -> lastShader);
        } else {
            RenderSystem.setShader(this::getShader);
            setupUniform();
        }
    }

    @Override
    public void end(boolean isInstancing) {
    }
}
