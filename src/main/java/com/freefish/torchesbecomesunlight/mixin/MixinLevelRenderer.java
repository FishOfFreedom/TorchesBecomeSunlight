package com.freefish.torchesbecomesunlight.mixin;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.enviroment.SkyRenderer;
import com.freefish.torchesbecomesunlight.server.config.ConfigHandler;
import com.freefish.torchesbecomesunlight.server.util.storage.ClientStorage;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;

@Mixin(LevelRenderer.class)
public abstract class MixinLevelRenderer{
    @Shadow
    private static final ResourceLocation SNOW_LOCATION = new ResourceLocation("textures/environment/snow.png");

    @Shadow
    private ClientLevel level;

    @Shadow
    private int ticks;

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

    @Inject(method = "renderSky",at = @At("HEAD"), cancellable = true)
    public void renderDemonSky(PoseStack matrices, Matrix4f pProjectionMatrix, float pPartialTick, Camera pCamera, boolean pIsFoggy, Runnable pSkyFogSetup, CallbackInfo info){
        if(ClientStorage.INSTANCE.demonRadio>=60&&ConfigHandler.CLIENT.demonRender.get()) {
            SkyRenderer.renderDemonSky(level, pPartialTick, matrices.last().pose(), pCamera, pProjectionMatrix, pSkyFogSetup);
            info.cancel();
        }
    }

    @Inject(method = "renderSnowAndRain",at = @At("TAIL"), cancellable = true)
    public void renderDemonWeather(LightTexture pLightTexture, float pPartialTick, double xIn, double yIn, double zIn, CallbackInfo info){
        float demon = ClientStorage.INSTANCE.getDemon(pPartialTick);
        if(demon<=60&& ConfigHandler.CLIENT.demonRender.get()) {
            SkyRenderer.demonWeather(level, pLightTexture, ticks, pPartialTick, xIn, yIn, zIn,info);
        }
    }
}
