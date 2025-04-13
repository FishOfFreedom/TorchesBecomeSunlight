package com.freefish.torchesbecomesunlight.client.particle.advance.data.material;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import static com.mojang.blaze3d.platform.GlStateManager.SourceFactor;

@OnlyIn(Dist.CLIENT)
public class BlendMode {
    public enum BlendFuc {
        ADD(32774),
        sub(32778),
        REVERSE_sub(32779),
        MIN(32775),
        MAX(32776);
        public final int op;

        BlendFuc(int op) {
            this.op = op;
        }
    }

    private boolean enableBlend;

    private SourceFactor srcColorFactor;

    private DestFactor dstColorFactor;

    private SourceFactor srcAlphaFactor;

    private DestFactor dstAlphaFactor;

    private BlendFuc blendFunc;

    public boolean isEnableBlend() {
        return enableBlend;
    }

    public void setEnableBlend(boolean enableBlend) {
        this.enableBlend = enableBlend;
    }

    public BlendFuc getBlendFunc() {
        return blendFunc;
    }

    public void setBlendFunc(BlendFuc blendFunc) {
        this.blendFunc = blendFunc;
    }

    public DestFactor getDstAlphaFactor() {
        return dstAlphaFactor;
    }

    public void setDstAlphaFactor(DestFactor dstAlphaFactor) {
        this.dstAlphaFactor = dstAlphaFactor;
    }

    public SourceFactor getSrcAlphaFactor() {
        return srcAlphaFactor;
    }

    public void setSrcAlphaFactor(SourceFactor srcAlphaFactor) {
        this.srcAlphaFactor = srcAlphaFactor;
    }

    public DestFactor getDstColorFactor() {
        return dstColorFactor;
    }

    public void setDstColorFactor(DestFactor dstColorFactor) {
        this.dstColorFactor = dstColorFactor;
    }

    public SourceFactor getSrcColorFactor() {
        return srcColorFactor;
    }

    public void setSrcColorFactor(SourceFactor srcColorFactor) {
        this.srcColorFactor = srcColorFactor;
    }

    private BlendMode(boolean enableBlend, SourceFactor srcColorFactor, DestFactor dstColorFactor, SourceFactor srcAlphaFactor, DestFactor dstAlphaFactor, BlendFuc blendFunc) {
        this.srcColorFactor = srcColorFactor;
        this.dstColorFactor = dstColorFactor;
        this.srcAlphaFactor = srcAlphaFactor;
        this.dstAlphaFactor = dstAlphaFactor;
        this.enableBlend = enableBlend;
        this.blendFunc = blendFunc;
    }

    public BlendMode() {
        this(true, SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO, BlendFuc.ADD);
    }

    public BlendMode(SourceFactor srcFactor, DestFactor dstFactor, BlendFuc blendFunc) {
        this(true, srcFactor, dstFactor, srcFactor, dstFactor, blendFunc);
    }

    public BlendMode(SourceFactor srcColorFactor, DestFactor dstColorFactor, SourceFactor srcAlphaFactor, DestFactor dstAlphaFactor, BlendFuc blendFunc) {
        this(true, srcColorFactor, dstColorFactor, srcAlphaFactor, dstAlphaFactor, blendFunc);
    }

    public void apply() {
        if (!this.enableBlend) {
            RenderSystem.disableBlend();
            return;
        }
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendEquation(this.blendFunc.op);
        RenderSystem.blendFuncSeparate(this.srcColorFactor, this.dstColorFactor, this.srcAlphaFactor, this.dstAlphaFactor);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof BlendMode blendMode)) {
            return false;
        }
        if (this.blendFunc != blendMode.blendFunc) {
            return false;
        }
        if (this.dstAlphaFactor != blendMode.dstAlphaFactor) {
            return false;
        }
        if (this.dstColorFactor != blendMode.dstColorFactor) {
            return false;
        }
        if (this.enableBlend != blendMode.enableBlend) {
            return false;
        }
        if (this.srcAlphaFactor != blendMode.srcAlphaFactor) {
            return false;
        }
        return this.srcColorFactor == blendMode.srcColorFactor;
    }

    public int hashCode() {
        int i = this.srcColorFactor.value;
        i = 31 * i + this.srcAlphaFactor.value;
        i = 31 * i + this.dstColorFactor.value;
        i = 31 * i + this.dstAlphaFactor.value;
        i = 31 * i + this.blendFunc.op;
        i = 31 * i + (this.enableBlend ? 1 : 0);
        return i;
    }

}