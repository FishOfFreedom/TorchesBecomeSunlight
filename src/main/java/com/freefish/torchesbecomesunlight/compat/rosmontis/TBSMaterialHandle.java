package com.freefish.torchesbecomesunlight.compat.rosmontis;

import com.freefish.rosmontislib.client.particle.advance.data.material.CustomShaderMaterial;
import com.freefish.rosmontislib.client.particle.advance.data.material.MaterialType;
import com.freefish.rosmontislib.client.particle.advance.data.material.TextureMaterial;
import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraft.resources.ResourceLocation;

public class TBSMaterialHandle {
    public static MaterialType<TextureMaterial> PIXEL;
    public static  MaterialType<CustomShaderMaterial> BIG_CIRCLE;

    public static void init(){
        PIXEL = new MaterialType<>(() -> new TextureMaterial(new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/particle/pixel1.png")));
        BIG_CIRCLE = new MaterialType<>(() -> new CustomShaderMaterial(new ResourceLocation("rosmontislib:circle")));
    }
}
