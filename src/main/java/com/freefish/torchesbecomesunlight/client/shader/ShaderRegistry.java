package com.freefish.torchesbecomesunlight.client.shader;

import net.minecraft.client.renderer.ShaderInstance;

public class ShaderRegistry {
    private static ShaderInstance renderTypeDemon;

    public static void setRenderTypeDemon(ShaderInstance renderTypeDemon) {
        ShaderRegistry.renderTypeDemon = renderTypeDemon;
    }

    public static ShaderInstance getRenderTypeDemon() {
        return renderTypeDemon;
    }
}
