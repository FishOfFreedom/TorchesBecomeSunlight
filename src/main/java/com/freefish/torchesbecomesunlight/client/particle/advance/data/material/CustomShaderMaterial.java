package com.freefish.torchesbecomesunlight.client.particle.advance.data.material;

import com.freefish.torchesbecomesunlight.client.shader.ShaderHandle;
import com.freefish.torchesbecomesunlight.mixin.accessor.ShaderInstanceAccessor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author KilaBash
 * @date 2023/5/29
 * @implNote CustomShaderMaterial
 */
@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
public class CustomShaderMaterial extends ShaderInstanceMaterial {
    private static final Map<ResourceLocation, ShaderInstance> COMPILED_SHADERS = new HashMap<>();

    public ResourceLocation shader = new ResourceLocation("photon:circle");

    protected CompoundTag uniformTag = new CompoundTag();

    //runtime
    private String compiledErrorMessage = "";
    private Runnable uniformCache = null;

    public CustomShaderMaterial() {
        var uniforms = new CompoundTag();
        var list = new ListTag();
        list.add(FloatTag.valueOf(0.3f));
        uniforms.put("Radius", list);
        uniformTag.put("uniforms", uniforms);
    }

    public CustomShaderMaterial(ResourceLocation shader) {
        this.shader = shader;
    }

    public boolean isCompiledError() {
        return getShader() == ShaderHandle.getParticleShader();
    }

    public void recompile() {
        uniformTag = new CompoundTag();
        uniformCache = null;
        compiledErrorMessage = "";
        var removed = COMPILED_SHADERS.remove(this.shader);
        if (removed != null && removed != ShaderHandle.getParticleShader()) {
            removed.close();
        }
    }

    @Override
    public ShaderInstance getShader() {
        return COMPILED_SHADERS.computeIfAbsent(shader, shader -> {
            try {
                return new ShaderInstance(Minecraft.getInstance().getResourceManager(), shader.toString(), DefaultVertexFormat.PARTICLE);
            } catch (Throwable e) {
                compiledErrorMessage = e.getMessage();
            }
            return ShaderHandle.getParticleShader();
        });
    }

    private Runnable combineRunnable(Runnable a, Runnable b) {
        return () -> {
            a.run();
            b.run();
        };
    }

    @Override
    public void setupUniform() {
        if (!isCompiledError()) {
            if (uniformCache != null) {
                uniformCache.run();
            } else if (!uniformTag.isEmpty() && getShader() instanceof ShaderInstanceAccessor shaderInstance) {
                // compile
                uniformCache = () -> {};
                if (uniformTag.contains("samplers")) {
                    var samplers = uniformTag.getCompound("samplers");
                    var samplerNames = new HashSet<>(shaderInstance.getSamplerNames());
                    for (String key : samplers.getAllKeys()) {
                        var index = -1;
                        ResourceLocation texture = null;
                        try {
                            index = Integer.parseInt(key);
                            texture = new ResourceLocation(samplers.getString(key));
                        } catch (Exception ignored) {}
                        if (index >= 0 && texture != null && samplerNames.contains("Sampler" + index)) {
                            final int finalIndex = index;
                            final ResourceLocation finalTexture = texture;
                            uniformCache = combineRunnable(uniformCache, () -> RenderSystem.setShaderTexture(finalIndex, finalTexture));
                        }
                    }
                }
                if (uniformTag.contains("uniforms")) {
                    var uniforms = uniformTag.getCompound("uniforms");
                    var uniformMap = shaderInstance.getUniformMap();
                    for (String key : uniforms.getAllKeys()) {
                        var data = uniforms.getList(key, Tag.TAG_FLOAT);
                        if (uniformMap.containsKey(key) && !data.isEmpty()) {
                            var u = uniformMap.get(key);
                            if (u.getCount() == data.size()) {
                                var type = u.getType();
                                if (type == 4) { // UT_FLOAT1
                                    final float value = data.getFloat(0);
                                    uniformCache = combineRunnable(uniformCache, () -> getShader().safeGetUniform(key).set(value));
                                }
                                if (type == 5) { // UT_FLOAT2
                                    final float[] value = new float[] {data.getFloat(0), data.getFloat(1)};
                                    uniformCache = combineRunnable(uniformCache, () -> getShader().safeGetUniform(key).set(value[0], value[1]));
                                }
                                if (type == 6) { // UT_FLOAT3
                                    final float[] value = new float[] {data.getFloat(0), data.getFloat(1), data.getFloat(2)};
                                    uniformCache = combineRunnable(uniformCache, () -> getShader().safeGetUniform(key).set(value[0], value[1], value[2]));
                                }
                                if (type == 7) { // UT_FLOAT4
                                    final float[] value = new float[] {data.getFloat(0), data.getFloat(1), data.getFloat(2), data.getFloat(3)};
                                    uniformCache = combineRunnable(uniformCache, () -> getShader().safeGetUniform(key).set(value[0], value[1], value[2], value[3]));
                                }
                            }
                        }
                    }
                }
            } else {
                uniformCache = () -> {};
            }
        }
    }
}
