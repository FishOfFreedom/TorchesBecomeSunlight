package com.freefish.torchesbecomesunlight.client.shader;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.shader.shader.ReloadShaderManager;
import com.freefish.torchesbecomesunlight.client.shader.shader.RenderUtils;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraftforge.client.event.RegisterShadersEvent;

import java.io.IOException;

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
}
