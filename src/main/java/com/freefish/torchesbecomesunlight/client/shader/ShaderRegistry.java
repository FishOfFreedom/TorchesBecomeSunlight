package com.freefish.torchesbecomesunlight.client.shader;

import net.minecraft.client.renderer.ShaderInstance;

public class ShaderRegistry {
    private static ShaderInstance renderTypeStarlightPortal;
    private static ShaderInstance renderTypeDemon;
    private static ShaderInstance renderTypeHole;

    public static void setRenderTypeStarlightPortal(ShaderInstance renderTypeStarlightPortal) {
        ShaderRegistry.renderTypeStarlightPortal = renderTypeStarlightPortal;
    }

    public static ShaderInstance getRenderTypeStarlightPortal() {
        return renderTypeStarlightPortal;
    }

    public static void setRenderTypeHole(ShaderInstance renderTypeHole) {
        ShaderRegistry.renderTypeHole = renderTypeHole;
    }

    public static ShaderInstance getRenderTypeHole() {
        return renderTypeHole;
    }

    public static void setRenderTypeDemon(ShaderInstance renderTypeDemon) {
        ShaderRegistry.renderTypeDemon = renderTypeDemon;
    }

    public static ShaderInstance getRenderTypeDemon() {
        return renderTypeDemon;
    }
}
