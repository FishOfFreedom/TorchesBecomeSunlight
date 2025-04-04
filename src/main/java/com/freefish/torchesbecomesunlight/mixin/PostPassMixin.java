package com.freefish.torchesbecomesunlight.mixin;

import com.freefish.torchesbecomesunlight.client.shader.postprocessing.PostProcessing;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.PostPass;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author KilaBash
 * @date 2022/05/03
 * @implNote PostChainMixin, add more features for vanilla PostChain stuff
 */
@Mixin(PostPass.class)
public abstract class PostPassMixin {
    @Shadow @Final private EffectInstance effect;

    /**
     * @author KilaBash
     */
    @Inject(method = "process",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/EffectInstance;safeGetUniform(Ljava/lang/String;)Lcom/mojang/blaze3d/shaders/AbstractUniform;", ordinal = 1))
    private void injectParseTargetNode(float pPartialTicks, CallbackInfo ci) {
        this.effect.safeGetUniform("iTime").set(PostProcessing.getITime(pPartialTicks));
        this.effect.safeGetUniform("EnableFilter").set(PostProcessing.enableBloomFilter.get() ? 1 : 0);
    }

}
