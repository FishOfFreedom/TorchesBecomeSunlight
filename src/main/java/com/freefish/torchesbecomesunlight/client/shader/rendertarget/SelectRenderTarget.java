package com.freefish.torchesbecomesunlight.client.shader.rendertarget;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Minecraft;

/**
 * only used for "intarget": "shimmer:composite_source",
 */
public class SelectRenderTarget extends RenderTarget {

    public SelectRenderTarget() {
        super(false);
    }

    @Override
    public int getColorTextureId() {
        return  Minecraft.getInstance().getMainRenderTarget().getColorTextureId();
    }
}
