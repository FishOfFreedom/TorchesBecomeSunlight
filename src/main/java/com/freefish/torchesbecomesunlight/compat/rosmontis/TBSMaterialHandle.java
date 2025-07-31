package com.freefish.torchesbecomesunlight.compat.rosmontis;

import com.freefish.rosmontislib.client.particle.advance.data.material.CustomShaderMaterial;
import com.freefish.rosmontislib.client.particle.advance.data.material.MaterialType;
import com.freefish.rosmontislib.client.particle.advance.data.material.TextureMaterial;
import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.compat.rosmontis.material.*;
import net.minecraft.resources.ResourceLocation;

public class TBSMaterialHandle {
    public static MaterialType<TextureMaterial> PIXEL;
    public static MaterialType<TextureMaterial> NO_GLOW_PIXEL;
    public static MaterialType<TextureMaterial> BIG_SMOKE;
    public static MaterialType<TextureMaterial> BEAM;
    public static MaterialType<TextureMaterial> ROS_RING;
    public static MaterialType<TextureMaterial> ROS_SAN;
    public static MaterialType<TextureMaterial> FACE;
    public static MaterialType<TextureMaterial> ROMA_TEXTURE_RING;
    public static  MaterialType<CustomShaderMaterial> BIG_CIRCLE;
    public static  MaterialType<FadeShaderMaterial> ROSMONTIS_DIAN;
    public static  MaterialType<GunFadeRingShaderMaterial> GUN_FADE_RING;
    public static  MaterialType<DissolveShaderMaterial> ROMA_RING;
    public static  MaterialType<XingMenMaterial> STARGATE;
    public static  MaterialType<XingMenRingMaterial> STARGATE_RING;
    public static  MaterialType<DemonEyeMaterial> DEMON_EYE;

    public static void init(){
        PIXEL = new MaterialType<>(() -> new TextureMaterial(new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/particle/pixel1.png")));
        FACE = new MaterialType<>(() -> new TextureMaterial(new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/particle/face.png")));
        BIG_SMOKE = new MaterialType<>(() -> new TextureMaterial(new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/particle/big_smoke.png")));
        NO_GLOW_PIXEL = new MaterialType<>(() -> new TextureMaterial(new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/particle/pixel.png")));
        BEAM = new MaterialType<>(() -> new TextureMaterial(new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/particle/ribbon_glow.png")));
        ROS_RING = new MaterialType<>(() -> new TextureMaterial(new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/particle/ros/ros_ring.png")));
        ROS_SAN = new MaterialType<>(() -> new TextureMaterial(new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/particle/ros/ros_san.png")));
        BIG_CIRCLE = new MaterialType<>(() -> new CustomShaderMaterial(new ResourceLocation("rosmontislib:circle")));
        ROSMONTIS_DIAN = new MaterialType<>(() -> new FadeShaderMaterial(new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/entity/rosmontis_installation/dian.png"),30));
        GUN_FADE_RING = new MaterialType<>(() -> new GunFadeRingShaderMaterial(new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/particle/holy_city.png"),600));
        ROMA_RING = new MaterialType<>(() -> new DissolveShaderMaterial(new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/particle/roma_ring.png"),600));
        ROMA_TEXTURE_RING = new MaterialType<>(() -> new TextureMaterial(new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/particle/roma_ring.png")));
        STARGATE = new MaterialType<>(() -> new XingMenMaterial(60));
        STARGATE_RING = new MaterialType<>(XingMenRingMaterial::new);
        DEMON_EYE = new MaterialType<>(DemonEyeMaterial::new);
    }

    public static MaterialType<SectorShaderMaterial> getSector(float radius){
        return new MaterialType<>(()->new SectorShaderMaterial(radius));
    }
}
