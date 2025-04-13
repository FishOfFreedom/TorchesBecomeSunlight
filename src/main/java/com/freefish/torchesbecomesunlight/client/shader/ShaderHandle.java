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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterShadersEvent;
import org.lwjgl.opengl.GL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.mojang.blaze3d.vertex.DefaultVertexFormat.ELEMENT_POSITION;

@OnlyIn(Dist.CLIENT)
public class ShaderHandle {
    private static ShaderInstance renderTypeDemon;
    private static ShaderInstance particleShader;

    public static ShaderInstance getParticleShader() {
        return particleShader;
    }

    public static void setParticleShader(ShaderInstance particleShader) {
        ShaderHandle.particleShader = particleShader;
    }

    public static void setRenderTypeDemon(ShaderInstance renderTypeDemon) {
        ShaderHandle.renderTypeDemon = renderTypeDemon;
    }

    public static ShaderInstance getRenderTypeDemon() {
        return renderTypeDemon;
    }

    public static void registerShaders(final RegisterShadersEvent e) {
        try {
            e.registerShader(new ShaderInstance(e.getResourceProvider(), new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"rendertype_demon"), DefaultVertexFormat.POSITION), ShaderHandle::setRenderTypeDemon);
            e.registerShader(new ShaderInstance(e.getResourceProvider(), new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"particle"), DefaultVertexFormat.POSITION), ShaderHandle::setParticleShader);

            ResourceProvider resourceProvider = e.getResourceProvider();
            e.registerShader(ReloadShaderManager.backupNewShaderInstance(resourceProvider, new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "fast_blit"), DefaultVertexFormat.POSITION), shaderInstance -> RenderUtils.blitShader = shaderInstance);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private static final List<Runnable> reloadListeners = new ArrayList<>();
    public static Shader IMAGE_V;

    public static void init() {
        IMAGE_V = load(Shader.ShaderType.VERTEX, new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "image"));
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
        reloadListeners.forEach(Runnable::run);
    }

    public static Shader load(Shader.ShaderType shaderType, ResourceLocation resourceLocation) {
        return CACHE.computeIfAbsent(new ResourceLocation(resourceLocation.getNamespace(), "shaders/" + resourceLocation.getPath() + shaderType.shaderExtension), key -> {
            try {
                Shader shader = Shader.loadShader(shaderType, key);
                TorchesBecomeSunlight.LOGGER.debug("load shader {} resource {} success", shaderType, resourceLocation);
                return shader;
            } catch (IOException e) {
                TorchesBecomeSunlight.LOGGER.error("load shader {} resource {} failed", shaderType, resourceLocation);
                TorchesBecomeSunlight.LOGGER.error("caused by ", e);
                return IMAGE_V;
            }
        });
    }
}
