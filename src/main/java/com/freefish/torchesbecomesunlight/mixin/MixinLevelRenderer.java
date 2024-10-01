package com.freefish.torchesbecomesunlight.mixin;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.util.storage.ClientStorage;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LevelRenderer.class)
public abstract class MixinLevelRenderer{

    @Shadow
    private static final ResourceLocation SNOW_LOCATION = new ResourceLocation("textures/environment/snow.png");

    @ModifyVariable(method = "renderSnowAndRain",at = @At(value = "STORE"),ordinal = 5)
    public float ingect(float f5) {
        if(ClientStorage.INSTANCE.isBossActive())
            return f5*45;
        else
            return f5;
    }

    @ModifyArg(method = "renderSnowAndRain",at = @At(value = "INVOKE",target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V"),index = 1)
    public ResourceLocation renderSnowAndRain(ResourceLocation resourceLocation){
        if(ClientStorage.INSTANCE.isBossActive()&&resourceLocation == SNOW_LOCATION)
            return new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/environment/blizzard.png");
        else
            return resourceLocation;
    }
}
