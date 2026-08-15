package com.freefish.torchesbecomesunlight.client.shader;

import com.freefish.rosmontislib.client.shader.ShaderHandle;
import com.freefish.rosmontislib.client.shader.management.Shader;
import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;

@OnlyIn(Dist.CLIENT)
public class TBSShaderHandle {
    private static ShaderInstance renderTypeDemon;

    public static Shader FLABELLATE;
    public static Shader RL_GRAY_FADE;

    public static void init() {
        FLABELLATE = ShaderHandle.load(Shader.ShaderType.FRAGMENT, ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "round_box"));
        RL_GRAY_FADE = ShaderHandle.load(Shader.ShaderType.FRAGMENT, ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "rl_gray_fade"));
    }

    public static void setRenderTypeDemon(ShaderInstance renderTypeDemon) {
        TBSShaderHandle.renderTypeDemon = renderTypeDemon;
    }

    public static ShaderInstance getRenderTypeDemon() {
        return renderTypeDemon;
    }

    public static void registerShaders(final RegisterShadersEvent e) {
        try {
            e.registerShader(new ShaderInstance(e.getResourceProvider(), ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID,"rendertype_demon"), DefaultVertexFormat.POSITION), TBSShaderHandle::setRenderTypeDemon);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
