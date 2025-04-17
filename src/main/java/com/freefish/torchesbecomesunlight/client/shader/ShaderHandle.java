package com.freefish.torchesbecomesunlight.client.shader;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterShadersEvent;

import java.io.IOException;

@OnlyIn(Dist.CLIENT)
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
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
