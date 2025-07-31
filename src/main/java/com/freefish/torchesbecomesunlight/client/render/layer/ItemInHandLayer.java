package com.freefish.torchesbecomesunlight.client.render.layer;

import com.freefish.torchesbecomesunlight.server.entity.ursus.PatrolCaptain;
import com.freefish.torchesbecomesunlight.server.init.ItemHandle;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

@OnlyIn(Dist.CLIENT)
public class ItemInHandLayer extends GeoRenderLayer<PatrolCaptain> {

    public ItemInHandLayer(GeoRenderer<PatrolCaptain> entityRendererIn) {
        super(entityRendererIn);
    }

    public static final ItemStack MAIN = new ItemStack(ItemHandle.MACHETE.get());

    @Override
    public void render(PoseStack poseStack, PatrolCaptain animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

        poseStack.pushPose();
        this.renderArmWithItem(animatable, MAIN, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, HumanoidArm.RIGHT, poseStack, bufferSource, packedLight,partialTick);
        //this.renderArmWithItem(animatable, itemstack, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, HumanoidArm.LEFT, matrixStackIn, bufferIn, packedLightIn);
        poseStack.popPose();
    }

    private void renderArmWithItem(LivingEntity entity, ItemStack itemStack, ItemDisplayContext transformType, HumanoidArm side, PoseStack matrixStack, MultiBufferSource buffer, int packedLightIn,float partialTick) {
        GeoModel<PatrolCaptain> geoModel = getGeoModel();
        if (!itemStack.isEmpty()&&entity.isAlive()) {
            String boneName = side == HumanoidArm.RIGHT ? "RightHandLocator" : "LeftHandLocator";
            matrixStack.last().pose().rotate(MathUtils.quatFromRotationXYZ(0, 0, 180, true));

            matrixStack.last().pose().rotate(MathUtils.quatFromRotationXYZ(0, entity.getYRot()+180, 0, true));
            matrixStack.pushPose();
            Pair<Vector3f, PoseStack> head = MathUtils.getModelPosFromModel(matrixStack,getGeoModel().getBone(boneName).get());
            boolean flag = side == HumanoidArm.LEFT;
            matrixStack.last().pose().rotate(MathUtils.quatFromRotationXYZ(-90, 180, 0, true));
            Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer().renderItem(entity, itemStack, transformType, flag, matrixStack, buffer, packedLightIn);
            matrixStack.popPose();
        }
    }
}
