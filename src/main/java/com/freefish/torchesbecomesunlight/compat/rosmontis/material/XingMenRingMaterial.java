package com.freefish.torchesbecomesunlight.compat.rosmontis.material;

import com.freefish.rosmontislib.client.particle.advance.AdvancedRLParticleBase;
import com.freefish.rosmontislib.client.particle.advance.data.material.ShaderInstanceMaterial;
import com.freefish.rosmontislib.client.shader.ShaderHandle;
import com.freefish.rosmontislib.mixin.accessor.ShaderInstanceAccessor;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;

@ParametersAreNonnullByDefault
public class XingMenRingMaterial extends ShaderInstanceMaterial {
    private static final Map<ResourceLocation, ShaderInstance> COMPILED_SHADERS = new HashMap();
    public ResourceLocation shader = new ResourceLocation("torchesbecomesunlight:xingmen");
    private String compiledErrorMessage = "";
    private Runnable uniformCache = null;

    public XingMenRingMaterial() {

    }

    public boolean isCompiledError() {
        return this.getShader() == ShaderHandle.getParticleShader();
    }

    public void recompile() {
        this.uniformCache = null;
        this.compiledErrorMessage = "";
        ShaderInstance removed = (ShaderInstance)COMPILED_SHADERS.remove(this.shader);
        if (removed != null && removed != ShaderHandle.getParticleShader()) {
            removed.close();
        }

    }

    public ShaderInstance getShader() {
        return (ShaderInstance)COMPILED_SHADERS.computeIfAbsent(this.shader, (shader) -> {
            try {
                return new ShaderInstance(Minecraft.getInstance().getResourceManager(), shader.toString(), DefaultVertexFormat.PARTICLE);
            } catch (Throwable var3) {
                Throwable e = var3;
                this.compiledErrorMessage = e.getMessage();
                return ShaderHandle.getParticleShader();
            }
        });
    }

    private Runnable combineRunnable(Runnable a, Runnable b) {
        return () -> {
            a.run();
            b.run();
        };
    }

    public void setupUniform(AdvancedRLParticleBase particleBase) {
        if (!this.isCompiledError()) {
            ShaderInstance var3 = this.getShader();
            if (var3 instanceof ShaderInstanceAccessor) {
                ShaderInstanceAccessor shaderInstance = (ShaderInstanceAccessor)var3;
                this.uniformCache = () -> {
                };
                this.uniformCache = this.combineRunnable(this.uniformCache, () -> {
                });
                Map<String, Uniform> uniformMap = shaderInstance.getUniformMap();
            }

            if (this.uniformCache != null) {
                this.uniformCache.run();
            }
        }

    }
}