package com.freefish.torchesbecomesunlight.client.render.gui;

import com.freefish.rosmontislib.client.shader.ShaderHandle;
import com.freefish.rosmontislib.client.shader.management.ShaderProgram;
import com.freefish.rosmontislib.client.shader.uniform.UniformCache;
import com.freefish.rosmontislib.client.utils.Rect;
import com.freefish.torchesbecomesunlight.client.shader.TBSShaderHandle;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import javax.annotation.Nonnull;

public class TBSDrawerHelper {
    public static ShaderProgram FLABELLATE;
    public static ShaderProgram RL_GRAY_FADE;

    public static void init() {
        FLABELLATE = Util.make(new ShaderProgram(), program
                -> program.attach(TBSShaderHandle.FLABELLATE).attach(ShaderHandle.SCREEN_V));
        RL_GRAY_FADE = Util.make(new ShaderProgram(), program
                -> program.attach(TBSShaderHandle.RL_GRAY_FADE).attach(ShaderHandle.SCREEN_V));
    }

    public static void drawFlabelate(@Nonnull GuiGraphics graphics, Rect square, int color, float radio, float radioOffset, float r) {
        FLABELLATE.use(uniform -> {
            updateScreenVshUniform(graphics, uniform);
            uniform.glUniformMatrix4F("PoseStack", new Matrix4f());
            var point1 = new Vector4f(square.left - 0.25f, square.up - 0.25f, 0, 1);
            var point2 = new Vector4f(square.right - 0.25f, square.down - 0.25f, 0, 1);
            var matrix = graphics.pose().last().pose();
            point1.mul(matrix);
            point2.mul(matrix);

            uniform.glUniform4F("SquareVertex", point1.x, point1.y, point2.x, point2.y);
            uniform.fillRGBAColor("Color", color);
            uniform.glUniform1F("radia", radio);
            uniform.glUniform1F("radiaOffset", radioOffset);
            uniform.glUniform1F("r", r);
        });

        RenderSystem.enableBlend();
        uploadScreenPosVertex();
    }

    public static void drawRLGrayFade(@Nonnull GuiGraphics graphics, Rect square, int color,float animation) {
        RL_GRAY_FADE.use(uniform -> {
            updateScreenVshUniform(graphics, uniform);
            uniform.glUniformMatrix4F("PoseStack", new Matrix4f());
            var point1 = new Vector4f(square.left - 0.25f, square.up - 0.25f, 0, 1);
            var point2 = new Vector4f(square.right - 0.25f, square.down - 0.25f, 0, 1);
            var matrix = graphics.pose().last().pose();
            point1.mul(matrix);
            point2.mul(matrix);

            uniform.glUniform4F("SquareVertex", point1.x, point1.y, point2.x, point2.y);
            uniform.fillRGBAColor("Color", color);
            uniform.glUniform1F("animation", animation);
        });

        RenderSystem.enableBlend();
        uploadScreenPosVertex();
    }

    public static void drawTetragon(int posX1, int posX2, int posY1, int posY2, int width1, int width2, int height1, int height2, int color) {
        if (color == -1)
            return;
        if (width1 < 0) width1 = 0;
        if (width2 < 0) width2 = 0;
        float f3;
        if (color <= 0xFFFFFF && color >= 0)
            f3 = 1.0F;
        else
            f3 = (color >> 24 & 255) / 255.0F;
        float f = (color >> 16 & 255) / 255.0F;
        float f1 = (color >> 8 & 255) / 255.0F;
        float f2 = (color & 255) / 255.0F;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableDepthTest();//VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
        BufferBuilder vertexbuffer = Tesselator.getInstance().getBuilder();
        vertexbuffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        vertexbuffer.vertex(posX1, (double) posY1 + height1, 0.0D).color(f, f1, f2, f3).endVertex();
        vertexbuffer.vertex((double) posX2 + width2, (double) posY2 + height2, 0.0D).color(f, f1, f2, f3).endVertex();
        vertexbuffer.vertex((double) posX1 + width1, posY2, 0.0D).color(f, f1, f2, f3).endVertex();
        vertexbuffer.vertex(posX2, posY1, 0.0D).color(f, f1, f2, f3).endVertex();

        BufferUploader.drawWithShader(vertexbuffer.end());
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
    }

    public static void updateScreenVshUniform(@Nonnull GuiGraphics graphics, UniformCache uniform) {
        var window = Minecraft.getInstance().getWindow();

        uniform.glUniform1F("GuiScale", (float) window.getGuiScale());
        uniform.glUniform2F("ScreenSize", (float) window.getWidth(), (float) window.getHeight());
        uniform.glUniformMatrix4F("PoseStack",graphics.pose().last().pose());
        uniform.glUniformMatrix4F("ProjMat", RenderSystem.getProjectionMatrix());
    }

    private static void uploadScreenPosVertex() {
        var builder = Tesselator.getInstance().getBuilder();

        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        builder.vertex(-1.0, 1.0,  0.0).endVertex();
        builder.vertex(-1.0, -1.0, 0.0).endVertex();
        builder.vertex(1.0, -1.0,  0.0).endVertex();
        builder.vertex(1.0, 1.0,   0.0).endVertex();
        BufferUploader.draw(builder.end());
    }

    private static void uploadScreenPosVertex1(Rect square) {
        var builder = Tesselator.getInstance().getBuilder();
        var window = Minecraft.getInstance().getWindow();

        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        float left = Mth.lerp((float) square.left / window.getGuiScaledWidth(), -1, 1);
        float right = Mth.lerp((float) (square.left+square.right) / window.getGuiScaledWidth(), -1, 1);
        float up = Mth.lerp((float) square.up / window.getGuiScaledHeight(), -1, 1);
        float down = Mth.lerp((float) (square.up+square.down) / window.getGuiScaledHeight(), -1, 1);

        builder.vertex(left, down, 0.0).endVertex();
        builder.vertex(left, up, 0.0).endVertex();
        builder.vertex(right, up, 0.0).endVertex();
        builder.vertex(right, down, 0.0).endVertex();
        BufferUploader.draw(builder.end());
    }
}
