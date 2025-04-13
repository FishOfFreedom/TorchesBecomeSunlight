package com.freefish.torchesbecomesunlight.client.particle.advance.data;

import com.freefish.torchesbecomesunlight.client.particle.advance.data.material.BlendMode;
import com.freefish.torchesbecomesunlight.client.particle.advance.data.material.IMaterial;
import com.freefish.torchesbecomesunlight.client.particle.advance.data.material.TextureMaterial;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author KilaBash
 * @date 2023/5/29
 * @implNote Material
 */
@OnlyIn(Dist.CLIENT)
public class MaterialSetting{
    protected final BlendMode blendMode = new BlendMode();

    protected boolean cull = true;

    protected boolean depthTest = true;

    protected boolean depthMask = false;

    protected IMaterial material = new TextureMaterial();

    public void pre() {
        blendMode.apply();
        if (cull) RenderSystem.enableCull(); else RenderSystem.disableCull();
        if (depthTest) RenderSystem.enableDepthTest(); else RenderSystem.disableDepthTest();
        RenderSystem.depthMask(depthMask);
    }

    public void post() {
        if (blendMode.getBlendFunc() != BlendMode.BlendFuc.ADD) {
            RenderSystem.blendEquation(BlendMode.BlendFuc.ADD.op);
        }
        if (!cull) RenderSystem.enableCull();
        if (!depthTest) RenderSystem.enableDepthTest();
        if (!depthMask) RenderSystem.depthMask(true);
    }

    public boolean isDepthTest() {
        return depthTest;
    }

    public void setDepthTest(boolean depthTest) {
        this.depthTest = depthTest;
    }

    public IMaterial getMaterial() {
        return material;
    }

    public void setMaterial(IMaterial material) {
        this.material = material;
    }

    public boolean isDepthMask() {
        return depthMask;
    }

    public void setDepthMask(boolean depthMask) {
        this.depthMask = depthMask;
    }

    public boolean isCull() {
        return cull;
    }

    public void setCull(boolean cull) {
        this.cull = cull;
    }
}