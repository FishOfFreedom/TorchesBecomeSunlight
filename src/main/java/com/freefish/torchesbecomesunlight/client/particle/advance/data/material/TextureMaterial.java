package com.freefish.torchesbecomesunlight.client.particle.advance.data.material;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.shader.ShaderHandle;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author KilaBash
 * @date 2023/5/29
 * @implNote TextureMaterial
 */
@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
public class TextureMaterial extends ShaderInstanceMaterial {

    public ResourceLocation texture = new ResourceLocation("textures/particle/glow.png");

    public float discardThreshold = 0.01f;

    public TextureMaterial() {
    }

    public TextureMaterial(ResourceLocation texture) {
        this.texture = texture;
    }

    @Override
    public ShaderInstance getShader() {
        return ShaderHandle.getParticleShader();
    }

    @Override
    public void setupUniform() {
        RenderSystem.setShaderTexture(0, texture);
        ShaderHandle.getParticleShader().safeGetUniform("DiscardThreshold").set(discardThreshold);
    }

    @Override
    public void begin(boolean isInstancing) {
        if (TorchesBecomeSunlight.isUsingShaderPack()) {
            RenderSystem.setShaderTexture(0, texture);
        } else {
            RenderSystem.setShader(this::getShader);
            setupUniform();
        }
    }
}
