package com.freefish.torchesbecomesunlight.compat.oculus;

import net.irisshaders.iris.api.v0.IrisApi;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.ModList;

import java.util.function.Supplier;

public class ForgeOculusHandle {
    static <T> T make(Supplier<T> supplier) {
        return supplier.get();
    }

    public final static ForgeOculusHandle INSTANCE = make(() -> {
        if(!ModList.get().isLoaded("oculus")) return null;
        return new ForgeOculusHandle();
    });

    public boolean underShaderPack() {
        return IrisApi.getInstance().isShaderPackInUse();
    }

    public boolean underShadowPass() {
        return IrisApi.getInstance().isRenderingShadowPass();
    }

    public void bindWriteMain() {
        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
    }

    public int getCompositeId() {
        return Minecraft.getInstance().getMainRenderTarget().getColorTextureId();
    }


}
