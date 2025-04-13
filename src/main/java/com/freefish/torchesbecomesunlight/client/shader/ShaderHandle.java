package com.freefish.torchesbecomesunlight.client.shader;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.shader.management.Shader;
import com.freefish.torchesbecomesunlight.client.shader.shader.ReloadShaderManager;
import com.freefish.torchesbecomesunlight.client.shader.shader.RenderUtils;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraftforge.client.event.RegisterShadersEvent;
import org.lwjgl.opengl.GL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.mojang.blaze3d.vertex.DefaultVertexFormat.ELEMENT_POSITION;

public class ShaderHandle {
    private static ShaderInstance renderTypeDemon;

    public static void setRenderTypeDemon(ShaderInstance renderTypeDemon) {
        ShaderHandle.renderTypeDemon = renderTypeDemon;
    }

    public static ShaderInstance getRenderTypeDemon() {
        return renderTypeDemon;
    }

    public static void registerShaders(final RegisterShadersEvent e) {
        try {
            e.registerShader(new ShaderInstance(e.getResourceProvider(), new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"rendertype_demon"), DefaultVertexFormat.POSITION), ShaderHandle::setRenderTypeDemon);

            ResourceProvider resourceProvider = e.getResourceProvider();
            e.registerShader(ReloadShaderManager.backupNewShaderInstance(resourceProvider, new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "fast_blit"), DefaultVertexFormat.POSITION), shaderInstance -> RenderUtils.blitShader = shaderInstance);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private static final List<Runnable> reloadListeners = new ArrayList<>();
    public static Shader IMAGE_F;
    public static Shader IMAGE_V;
    public static Shader GUI_IMAGE_V;
    public static Shader SCREEN_V;
    public static Shader ROUND_F;
    public static Shader PANEL_BG_F;
    public static Shader ROUND_BOX_F;
    public static Shader PROGRESS_ROUND_BOX_F;
    public static Shader FRAME_ROUND_BOX_F;
    public static Shader ROUND_LINE_F;

    public static void init() {
        IMAGE_F = load(Shader.ShaderType.FRAGMENT, new ResourceLocation(LDLib.MOD_ID, "image"));
        IMAGE_V = load(Shader.ShaderType.VERTEX, new ResourceLocation(LDLib.MOD_ID, "image"));
        GUI_IMAGE_V = load(Shader.ShaderType.VERTEX, new ResourceLocation(LDLib.MOD_ID, "gui_image"));
        SCREEN_V = load(Shader.ShaderType.VERTEX, new ResourceLocation(LDLib.MOD_ID, "screen"));
        ROUND_F = load(Shader.ShaderType.FRAGMENT, new ResourceLocation(LDLib.MOD_ID, "round"));
        PANEL_BG_F = load(Shader.ShaderType.FRAGMENT, new ResourceLocation(LDLib.MOD_ID, "panel_bg"));
        ROUND_BOX_F = load(Shader.ShaderType.FRAGMENT, new ResourceLocation(LDLib.MOD_ID, "round_box"));
        PROGRESS_ROUND_BOX_F = load(Shader.ShaderType.FRAGMENT, new ResourceLocation(LDLib.MOD_ID, "progress_round_box"));
        FRAME_ROUND_BOX_F = load(Shader.ShaderType.FRAGMENT, new ResourceLocation(LDLib.MOD_ID, "frame_round_box"));
        ROUND_LINE_F = load(Shader.ShaderType.FRAGMENT, new ResourceLocation(LDLib.MOD_ID, "round_line"));
    }

    public static Map<ResourceLocation, Shader> CACHE = new HashMap<>();

    public static void addReloadListener(Runnable runnable) {
        reloadListeners.add(runnable);
    }

    public static void reload() {
        for (Shader shader : CACHE.values()) {
            if (shader != null) {
                shader.deleteShader();
            }
        }
        CACHE.clear();
        init();
        DrawerHelper.init();
        ShaderTexture.clearCache();
        reloadListeners.forEach(Runnable::run);
    }

    public static Shader load(Shader.ShaderType shaderType, ResourceLocation resourceLocation) {
        return CACHE.computeIfAbsent(new ResourceLocation(resourceLocation.getNamespace(), "shaders/" + resourceLocation.getPath() + shaderType.shaderExtension), key -> {
            try {
                Shader shader = Shader.loadShader(shaderType, key);
                LDLib.LOGGER.debug("load shader {} resource {} success", shaderType, resourceLocation);
                return shader;
            } catch (IOException e) {
                LDLib.LOGGER.error("load shader {} resource {} failed", shaderType, resourceLocation);
                LDLib.LOGGER.error("caused by ", e);
                return IMAGE_F;
            }
        });
    }
}
