package com.freefish.torchesbecomesunlight.client.render.layer;

import com.freefish.torchesbecomesunlight.client.render.model.ManModel;
import com.freefish.torchesbecomesunlight.server.entity.villager.UrsusVillager;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class VillagerItemLayer<T extends UrsusVillager> extends GeoRenderLayer<T> {
    public VillagerItemLayer(GeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        boolean flag = animatable.getMainArm() == HumanoidArm.RIGHT;
        ItemStack mainHandStack = animatable.getMainHandItem();
        ItemStack offHandStack = animatable.getOffhandItem();
        ItemStack itemstack = flag ? offHandStack : mainHandStack;
        ItemStack itemstack1 = flag ? mainHandStack : offHandStack;
        if (!itemstack.isEmpty() || !itemstack1.isEmpty()) {
            poseStack.pushPose();

            this.renderArmWithItem(animatable, itemstack1, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, HumanoidArm.RIGHT, poseStack, bufferSource, packedLight,partialTick);

            this.renderArmWithItem(animatable, itemstack, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, HumanoidArm.LEFT,    poseStack, bufferSource, packedLight,partialTick);

            poseStack.popPose();
        }
        super.render(poseStack, animatable, bakedModel, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
    }

    private void renderArmWithItem(LivingEntity entity, ItemStack itemStack, ItemDisplayContext transformType, HumanoidArm side, PoseStack matrixStack, MultiBufferSource buffer, int packedLightIn,float partialTick) {
        GeoModel<T> geoModel = getGeoModel();
        if (!itemStack.isEmpty()&&geoModel instanceof ManModel) {
            String boneName = side == HumanoidArm.RIGHT ? "RightItem" : "LeftItem";

            Pair<Vector3f, PoseStack> head = MathUtils.getModelPosFromModel(getGeoModel().getBone(boneName).get());

            Matrix4f headPose = head.getSecond().last().pose();
            Vector4f headVec4 = new Vector4f(0,0,0,1f);
            Vector3f headRot = head.getFirst();
            headVec4.mul(headPose);
            matrixStack.last().pose().rotate(MathUtils.quatFromRotationXYZ(0, -Mth.lerp(partialTick, entity.yRotO, entity.getYRot())+180, 0, true));
            matrixStack.pushPose();
            matrixStack.last().pose().translate(headVec4.x,headVec4.y,headVec4.z);
            matrixStack.last().pose().rotate(MathUtils.quatFromRotationXYZ(-90, 0, 0, true));

            matrixStack.pushPose();
            matrixStack.last().pose().rotate(MathUtils.quatFromRotationXYZ(headRot.x, -headRot.y, -headRot.z, false));

            boolean flag = side == HumanoidArm.LEFT;
            Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer().renderItem(entity, itemStack, transformType, flag, matrixStack, buffer, packedLightIn);
            matrixStack.popPose();
            matrixStack.popPose();
        }
    }
}
