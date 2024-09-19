package com.freefish.torchesbecomesunlight.client.render.model;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.util.AnimationGeoModel;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster.SnowNova1;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Math;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.data.EntityModelData;

public class SnowNova1Model extends AnimationGeoModel<SnowNova1> {
    @Override
    public ResourceLocation getModelResource(SnowNova1 object) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "geo/snownova.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SnowNova1 object) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "textures/entity/snownova.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SnowNova1 animatable) {
        return new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "animations/snownova.animation.json");
    }

    @Override
    public void setCustomAnimations(SnowNova1 entity, long instanceId, AnimationState<SnowNova1> animationState) {
        super.setCustomAnimations(entity, instanceId, animationState);
        CoreGeoBone head = this.getAnimationProcessor().getBone("Head");
        EntityModelData extraData = (EntityModelData) animationState.getExtraData().get(DataTickets.ENTITY_MODEL_DATA);
        head.setRotX(extraData.headPitch() * 0.017453292F);
        head.setRotY(extraData.netHeadYaw() * 0.017453292F);

        float limbSwing = animationState.getLimbSwing();
        float limbSwingAmount = animationState.getLimbSwingAmount();

        Vec3 moveVec = entity.getDeltaMovement().normalize().yRot((float) Math.toRadians(entity.yBodyRot + 90.0));
        float forward = (float) Math.max(0, new Vec3(1.0, 0, 0).dot(moveVec));
        float backward = (float) java.lang.Math.max(0, new Vec3(-1.0, 0, 0).dot(moveVec));
        float left = (float) java.lang.Math.max(0, new Vec3(0, 0, -1.0).dot(moveVec));
        float right = (float) java.lang.Math.max(0, new Vec3(0, 0, 1.0).dot(moveVec));


        limbSwingAmount = Math.min(0.6f, limbSwingAmount);
        limbSwingAmount *= 1.8;
        float isBaseAnimation = getValue("IsBaseAnimation");

        walkForward(forward*isBaseAnimation , limbSwing, limbSwingAmount, 1.5f);
        walkBackward(backward*isBaseAnimation , limbSwing, limbSwingAmount, 1f);
        walkLeft(left*isBaseAnimation , limbSwing, limbSwingAmount, 1);
        walkRight(right*isBaseAnimation , limbSwing, limbSwingAmount, 1);
    }

    private void walkForward(float blend, float limbSwing, float limbSwingAmount, float speed) {
        GeoBone main = getFFBone("main");
        GeoBone tao = getFFBone("tao");
        GeoBone tao1 = getFFBone("tao1");
        GeoBone rightForeArm = getFFBone("RightForeArm");
        GeoBone rightArm = getFFBone("RightArm");
        GeoBone leftForeArm = getFFBone("LeftForeArm");
        GeoBone leftArm = getFFBone("LeftArm");
        GeoBone rightear = getFFBone("rightear");
        GeoBone leftear = getFFBone("leftear");
        GeoBone hl_m = getFFBone("hl_m");
        GeoBone head = getFFBone("Head");
        GeoBone upBody = getFFBone("UpBody");
        GeoBone waist = getFFBone("waist");
        GeoBone rightLowerLeg = getFFBone("RightLowerLeg");
        GeoBone rightLeg = getFFBone("RightLeg");
        GeoBone leftLowerLeg = getFFBone("LeftLowerLeg");
        GeoBone leftLeg = getFFBone("LeftLeg");

        addPosY(main,blend,limbSwing,limbSwingAmount,speed*2,2f,0);
        addRotZ(tao,blend,limbSwing,limbSwingAmount,speed,0.15f,-1.2f);
        addRotX(tao1,blend,limbSwing,limbSwingAmount,speed*2,0.1f,-1.2f);
        addRotX(rightForeArm,blend,limbSwing,limbSwingAmount,speed,0.5f,2.3f);
        addRotX(rightArm,blend,limbSwing,limbSwingAmount,speed,0.5f,3.1f);
        addRotX(leftForeArm,blend,limbSwing,limbSwingAmount,speed,0.5f,0);
        addRotX(leftArm,blend,limbSwing,limbSwingAmount,speed,0.5f,-0.83f);
        addRotX(leftear,blend,limbSwing,limbSwingAmount,speed,0.25f,-0.83f);
        addRotX(rightear,blend,limbSwing,limbSwingAmount,speed,0.25f,2.30f);
        addRotZ(hl_m,blend,limbSwing,limbSwingAmount,speed,0.15f,-1.25f);
        addRotY(head,blend,limbSwing,limbSwingAmount,speed,0.1f,-1.67f);
        addRotY(upBody,blend,limbSwing,limbSwingAmount,speed,0.1f,3.14f);
        addRotY(waist,blend,limbSwing,limbSwingAmount,speed,0.1f,2.3f);
        addRotX(rightLowerLeg, -Math.clamp(0,(float) Math.PI*2,Math.toRadians(17.5f)*blend+animationCycle(blend,limbSwing,limbSwingAmount,speed,-1f,5.23f)));
        addRotX(rightLeg,blend,limbSwing,limbSwingAmount,speed,1.0f,0);
        addRotX(leftLowerLeg, -Math.clamp(0,(float) Math.PI*2,Math.toRadians(17.5f)*blend+animationCycle(blend,limbSwing,limbSwingAmount,speed,-1f,2.09f)));
        addRotX(leftLeg,blend,limbSwing,limbSwingAmount,speed,1.0f,3.14f);
    }

    private void walkBackward(float blend, float limbSwing, float limbSwingAmount, float speed) {
        GeoBone main = getFFBone("main");
        GeoBone tao = getFFBone("tao");
        GeoBone tao1 = getFFBone("tao1");
        GeoBone rightForeArm = getFFBone("RightForeArm");
        GeoBone rightArm = getFFBone("RightArm");
        GeoBone leftForeArm = getFFBone("LeftForeArm");
        GeoBone leftArm = getFFBone("LeftArm");
        GeoBone rightear = getFFBone("rightear");
        GeoBone leftear = getFFBone("leftear");
        GeoBone hl_m = getFFBone("hl_m");
        GeoBone head = getFFBone("Head");
        GeoBone upBody = getFFBone("UpBody");
        GeoBone waist = getFFBone("waist");
        GeoBone rightLowerLeg = getFFBone("RightLowerLeg");
        GeoBone rightLeg = getFFBone("RightLeg");
        GeoBone leftLowerLeg = getFFBone("LeftLowerLeg");
        GeoBone leftLeg = getFFBone("LeftLeg");

        limbSwing *= -1;
        addPosY(main,blend,limbSwing,limbSwingAmount,speed*2,2f,0);
        addRotZ(tao,blend,limbSwing,limbSwingAmount,speed,0.15f,-1.2f);
        addRotX(tao1,blend,limbSwing,limbSwingAmount,speed*2,0.1f,-1.2f);
        addRotX(rightForeArm,blend,limbSwing,limbSwingAmount,speed,0.1f,2.3f);
        addRotX(rightArm,blend,limbSwing,limbSwingAmount,speed,0.25f,3.1f);
        addRotX(leftForeArm,blend,limbSwing,limbSwingAmount,speed,0.15f,0);
        addRotX(leftArm,blend,limbSwing,limbSwingAmount,speed,0.25f,-0.83f);
        addRotX(leftear,blend,limbSwing,limbSwingAmount,speed,0.25f,-0.83f);
        addRotX(rightear,blend,limbSwing,limbSwingAmount,speed,0.25f,2.30f);
        addRotZ(hl_m,blend,limbSwing,limbSwingAmount,speed,0.15f,-1.25f);
        addRotY(head,blend,limbSwing,limbSwingAmount,speed,0.1f,-1.67f);
        addRotY(upBody,blend,limbSwing,limbSwingAmount,speed,0.1f,3.14f);
        addRotY(waist,blend,limbSwing,limbSwingAmount,speed,0.1f,2.3f);
        addRotX(rightLowerLeg, -Math.clamp(0,(float) Math.PI*2,Math.toRadians(17.5f)*blend+animationCycle(blend,limbSwing,limbSwingAmount,speed,-1f,5.23f)));
        addRotX(rightLeg,blend,limbSwing,limbSwingAmount,speed,1.0f,0);
        addRotX(leftLowerLeg, -Math.clamp(0,(float) Math.PI*2,Math.toRadians(17.5f)*blend+animationCycle(blend,limbSwing,limbSwingAmount,speed,-1f,2.09f)));
        addRotX(leftLeg,blend,limbSwing,limbSwingAmount,speed,1.0f,3.14f);
    }

    private void walkLeft(float blend, float limbSwing, float limbSwingAmount, float speed) {
        GeoBone main = getFFBone("main");
        GeoBone tao = getFFBone("tao");
        GeoBone rightForeArm = getFFBone("RightForeArm");
        GeoBone rightArm = getFFBone("RightArm");
        GeoBone leftForeArm = getFFBone("LeftForeArm");
        GeoBone leftArm = getFFBone("LeftArm");
        GeoBone rightear = getFFBone("rightear");
        GeoBone leftear = getFFBone("leftear");
        GeoBone hl_m = getFFBone("hl_m");
        GeoBone head = getFFBone("Head");
        GeoBone upBody = getFFBone("UpBody");
        GeoBone waist = getFFBone("waist");
        GeoBone rightLowerLeg = getFFBone("RightLowerLeg");
        GeoBone rightLeg = getFFBone("RightLeg");
        GeoBone leftLowerLeg = getFFBone("LeftLowerLeg");
        GeoBone leftLeg = getFFBone("LeftLeg");

        addPosY(main,blend,limbSwing,limbSwingAmount,speed,2f,0);
        addRotX(tao,blend,limbSwing,limbSwingAmount,speed,0.2f,2.30f);
        addRotX(rightForeArm,blend,limbSwing,limbSwingAmount,speed,0.2f,-0.83f);
        addRotZ(rightArm,blend,limbSwing,limbSwingAmount,speed,0.2f,2.72f);
        addRotX(leftForeArm,blend,limbSwing,limbSwingAmount,speed,0.2f,-0.83f);
        addRotZ(leftArm,blend,limbSwing,limbSwingAmount,speed,0.1f,-0.41f);
        addRotX(leftear,blend,limbSwing,limbSwingAmount,speed,0.2f,-0.83f);
        addRotX(rightear,blend,limbSwing,limbSwingAmount,speed,0.2f,-0.83f);
        addRotX(hl_m,blend,limbSwing,limbSwingAmount,speed,0.2f,2.30f);
        addRotX(head,blend,limbSwing,limbSwingAmount,speed,0.1f,-0.41f);
        addRotY(upBody,blend,-limbSwing,limbSwingAmount,speed,0.1f,0.00f);
        addRotY(waist,blend,-limbSwing,limbSwingAmount,speed,0.2f,2.72f);
        addRotX(rightLowerLeg, -Math.clamp(0,(float) Math.PI*2,Math.toRadians(17.5f)*blend+animationCycle(blend,-limbSwing,limbSwingAmount,speed,0.75f,4.39f)));
        addRotY(rightLeg,blend,-limbSwing,limbSwingAmount,speed,0.6f,0.00f);
        addRotZ(rightLeg,blend,-limbSwing,limbSwingAmount,speed,0.6f,3.14f);
        addRotX(leftLowerLeg, -Math.clamp(0,(float) Math.PI*2,Math.toRadians(17.5f)*blend+animationCycle(blend,-limbSwing,limbSwingAmount,speed,0.75f,1.88f)));
        addRotY(leftLeg,blend,-limbSwing,limbSwingAmount,speed,0.6f,3.14f);
        addRotZ(leftLeg,blend,-limbSwing,limbSwingAmount,speed,0.6f,0.00f);
    }

    private void walkRight(float blend, float limbSwing, float limbSwingAmount, float speed) {
        GeoBone main = getFFBone("main");
        GeoBone tao = getFFBone("tao");
        GeoBone rightForeArm = getFFBone("RightForeArm");
        GeoBone rightArm = getFFBone("RightArm");
        GeoBone leftForeArm = getFFBone("LeftForeArm");
        GeoBone leftArm = getFFBone("LeftArm");
        GeoBone rightear = getFFBone("rightear");
        GeoBone leftear = getFFBone("leftear");
        GeoBone hl_m = getFFBone("hl_m");
        GeoBone head = getFFBone("Head");
        GeoBone upBody = getFFBone("UpBody");
        GeoBone waist = getFFBone("waist");
        GeoBone rightLowerLeg = getFFBone("RightLowerLeg");
        GeoBone rightLeg = getFFBone("RightLeg");
        GeoBone leftLowerLeg = getFFBone("LeftLowerLeg");
        GeoBone leftLeg = getFFBone("LeftLeg");

        addPosY(main,blend,limbSwing,limbSwingAmount,speed,2f,0);
        addRotX(tao,blend,limbSwing,limbSwingAmount,speed,0.2f,2.30f);
        addRotX(rightForeArm,blend,limbSwing,limbSwingAmount,speed,0.2f,-0.83f);
        addRotZ(rightArm,blend,limbSwing,limbSwingAmount,speed,0.2f,2.72f);
        addRotX(leftForeArm,blend,limbSwing,limbSwingAmount,speed,0.2f,-0.83f);
        addRotZ(leftArm,blend,limbSwing,limbSwingAmount,speed,0.1f,-0.41f);
        addRotX(leftear,blend,limbSwing,limbSwingAmount,speed,0.2f,-0.83f);
        addRotX(rightear,blend,limbSwing,limbSwingAmount,speed,0.2f,-0.83f);
        addRotX(hl_m,blend,limbSwing,limbSwingAmount,speed,0.2f,2.30f);
        addRotX(head,blend,limbSwing,limbSwingAmount,speed,0.1f,-0.41f);
        addRotY(upBody,blend,limbSwing,limbSwingAmount,speed,0.1f,0.00f);
        addRotY(waist,blend,limbSwing,limbSwingAmount,speed,0.2f,2.72f);
        addRotX(rightLowerLeg, -Math.clamp(0,(float) Math.PI*2,Math.toRadians(17.5f)*blend+animationCycle(blend,limbSwing,limbSwingAmount,speed,0.75f,4.39f)));
        addRotY(rightLeg,blend,limbSwing,limbSwingAmount,speed,0.6f,0.00f);
        addRotZ(rightLeg,blend,limbSwing,limbSwingAmount,speed,0.6f,3.14f);
        addRotX(leftLowerLeg, -Math.clamp(0,(float) Math.PI*2,Math.toRadians(17.5f)*blend+animationCycle(blend,limbSwing,limbSwingAmount,speed,0.75f,1.88f)));
        addRotY(leftLeg,blend,limbSwing,limbSwingAmount,speed,0.6f,3.14f);
        addRotZ(leftLeg,blend,limbSwing,limbSwingAmount,speed,0.6f,0.00f);
    }
}
