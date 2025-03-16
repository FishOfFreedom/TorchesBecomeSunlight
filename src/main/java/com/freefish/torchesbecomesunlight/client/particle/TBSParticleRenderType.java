package com.freefish.torchesbecomesunlight.client.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureManager;

public class TBSParticleRenderType {
    public static final ParticleRenderType BLOOM = new ParticleRenderType() {
        public void begin(BufferBuilder p_107469_, TextureManager p_107470_) {
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
        }
        public void end(Tesselator p_107472_) {
        }
        public String toString() {
            return "BLOOM";
        }
    };
}
