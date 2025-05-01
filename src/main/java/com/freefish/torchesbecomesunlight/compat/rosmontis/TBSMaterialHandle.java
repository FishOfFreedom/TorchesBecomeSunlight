package com.freefish.torchesbecomesunlight.compat.rosmontis;

import com.freefish.rosmontislib.client.particle.advance.data.material.CustomShaderMaterial;
import com.freefish.rosmontislib.client.particle.advance.data.material.TextureMaterial;
import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraft.resources.ResourceLocation;

public class TBSMaterialHandle {
    public static final TextureMaterial PIXEL = new TextureMaterial(new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/particle/pixel1.png"));

    public static final CustomShaderMaterial BIG_CIRCLE = new CustomShaderMaterial(new ResourceLocation("rosmontislib:circle"));
}
