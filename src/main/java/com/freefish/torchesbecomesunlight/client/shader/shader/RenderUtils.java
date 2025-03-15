package com.freefish.torchesbecomesunlight.client.shader.shader;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import org.lwjgl.opengl.GL46;

/**
 * @author KilaBash
 * @date 2022/5/6
 * @implNote RenderUtils
 */
public class RenderUtils {
    public static ShaderInstance blitShader;

    public static void fastBlit(RenderTarget from, RenderTarget to) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._colorMask(true, true, true, true);
        GlStateManager._disableDepthTest();
        GlStateManager._depthMask(false);

        to.bindWrite(true);

        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);

        blitShader.setSampler("DiffuseSampler", from.getColorTextureId());

        blitShader.apply();//iris will disable color mask here, open here
        GL46.glColorMaski(0,true, true,true,true);
        GlStateManager._enableBlend();
        RenderSystem.defaultBlendFunc();

        Tesselator tesselator = RenderSystem.renderThreadTesselator();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        bufferbuilder.vertex(-1, 1, 0).endVertex();
        bufferbuilder.vertex(-1, -1, 0).endVertex();
        bufferbuilder.vertex(1, -1, 0).endVertex();
        bufferbuilder.vertex(1, 1, 0).endVertex();
        BufferUploader.draw(bufferbuilder.end());
        blitShader.clear();

        GlStateManager._depthMask(true);
        GlStateManager._colorMask(true, true, true, true);
        GlStateManager._enableDepthTest();
    }
}
