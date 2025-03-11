package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.model.player.ModelBipedAnimated;
import com.freefish.torchesbecomesunlight.client.render.model.player.ModelGeckoPlayerThirdPerson;
import com.freefish.torchesbecomesunlight.client.render.model.tools.ModelPartMatrix;
import com.freefish.torchesbecomesunlight.client.render.model.tools.geckolib.MowzieGeoBone;
import com.freefish.torchesbecomesunlight.server.entity.villager.MaleVillager;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;
import software.bernie.geckolib.renderer.GeoRenderer;

import java.util.Optional;

public class ManModel extends GeoModel<MaleVillager> {
    private static final ResourceLocation playerModel = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/male_villager.geo.json");
    public static final ResourceLocation TEXTURE_1 = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/entity/villager/male_1.png");
    public static final ResourceLocation TEXTURE_2 = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/entity/villager/male_2.png");
    public static final ResourceLocation TEXTURE_3 = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/entity/villager/male_3.png");
    public static final ResourceLocation TEXTUREHEAD_1 = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/entity/villager/malehead_1.png");
    public static final ResourceLocation TEXTUREHEAD_2 = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/entity/villager/malehead_2.png");
    public static final ResourceLocation TEXTUREHEAD_3 = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/entity/villager/malehead_3.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "animations/male_villager.animation.json");

    public ManModel() {
    }

    @Override
    public ResourceLocation getModelResource(MaleVillager animatable) {
        return playerModel;
    }

    @Override
    public ResourceLocation getTextureResource(MaleVillager animatable) {
        int body = animatable.getBody();
        if(body==0)
            return TEXTURE_1;
        else if(body==1)
            return TEXTURE_2;
        else
            return TEXTURE_3;
    }

    @Override
    public ResourceLocation getAnimationResource(MaleVillager animatable) {
        return ANIMATION;
    }

    @Override
    public void setCustomAnimations(MaleVillager animatable, long instanceId, AnimationState<MaleVillager> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        CoreGeoBone head = this.getAnimationProcessor().getBone("Head");
        EntityModelData extraData = (EntityModelData) animationState.getExtraData().get(DataTickets.ENTITY_MODEL_DATA);

        float headPitch = extraData.headPitch();
        float headYaw = extraData.netHeadYaw();


        head.setRotX(headPitch * 0.017453292F);
        head.setRotY(headYaw * 0.017453292F);

        CoreGeoBone rightEye = this.getAnimationProcessor().getBone("RightEyelidBase");
        CoreGeoBone leftEye = this.getAnimationProcessor().getBone("LeftEyelidBase");

        rightEye.setPosX(headYaw * 0.017453292F);
        leftEye.setPosX(headYaw * 0.017453292F);
    }

    @Override
    public void applyMolangQueries(MaleVillager animatable, double animTime) {
        super.applyMolangQueries(animatable, animTime);
    }

    public MowzieGeoBone bipedHead() {
        return getMowzieBone("Head");
    }

    public MowzieGeoBone bipedHeadwear() {
        return getMowzieBone("Hat");
    }

    public MowzieGeoBone bipedBody() {
        return getMowzieBone("Body");
    }

    public MowzieGeoBone bipedRightArm() {
        return getMowzieBone("RightArm");
    }

    public MowzieGeoBone bipedLeftArm() {
        return getMowzieBone("LeftArm");
    }

    public MowzieGeoBone bipedRightLeg() {
        return getMowzieBone("RightLowerLeg");
    }

    public MowzieGeoBone bipedLeftLeg() {
        return getMowzieBone("LeftLowerLeg");
    }

    public MowzieGeoBone getMowzieBone(String boneName) {
        Optional<GeoBone> bone = this.getBone(boneName);
        return (MowzieGeoBone) bone.orElse(null);
    }
}
