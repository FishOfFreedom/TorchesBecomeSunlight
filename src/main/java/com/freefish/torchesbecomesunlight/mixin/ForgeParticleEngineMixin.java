package com.freefish.torchesbecomesunlight.mixin;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.TracedGLState;
import com.freefish.torchesbecomesunlight.client.shader.postprocessing.PostProcessing;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleEngine.class)
public abstract class ForgeParticleEngineMixin{
    @ModifyReceiver(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/culling/Frustum;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/particle/ParticleRenderType;begin(Lcom/mojang/blaze3d/vertex/BufferBuilder;Lnet/minecraft/client/renderer/texture/TextureManager;)V"))
    private ParticleRenderType injectRenderPre(ParticleRenderType particlerendertype, BufferBuilder bufferBuilder, TextureManager textureManager) {
        if(!TorchesBecomeSunlight.underShaderPack()){
            PostProcessing postProcessing = PostProcessing.BLOOM_UNREAL;
            postProcessing.getPostTarget(false).bindWrite(false);
            postProcessing.hasParticle();
        }
        return particlerendertype;
    }


    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/culling/Frustum;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/particle/ParticleRenderType;end(Lcom/mojang/blaze3d/vertex/Tesselator;)V",
                    shift = At.Shift.AFTER))
    private void injectRenderPost(CallbackInfo ci) {
        RenderTarget mainRenderTarget = Minecraft.getInstance().getMainRenderTarget();
        if (!TorchesBecomeSunlight.underShaderPack()&& TracedGLState.bindFrameBuffer != mainRenderTarget.frameBufferId){
            mainRenderTarget.bindWrite(false);
        }
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/culling/Frustum;)V",
            at = @At(value = "RETURN"), remap = false)
    private void injectRenderReturn(PoseStack pMatrixStack, MultiBufferSource.BufferSource pBuffer, LightTexture pLightTexture, Camera pActiveRenderInfo, float pPartialTicks, net.minecraft.client.renderer.culling.Frustum clippingHelper, CallbackInfo ci) {
        if(!TorchesBecomeSunlight.underShaderPack()){
            PostProcessing.BLOOM_UNREAL.renderParticlePost();
        }
    }
}

