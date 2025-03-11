package com.freefish.torchesbecomesunlight.client.render.layer;

import com.freefish.torchesbecomesunlight.client.render.model.ManModel;
import com.freefish.torchesbecomesunlight.server.entity.villager.UrsusVillager;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import javax.annotation.Nullable;
import java.util.Map;

public class VillagerArmorLayer<T extends UrsusVillager> extends GeoRenderLayer<T> {
    public VillagerArmorLayer(GeoRenderer<T> entityRendererIn, HumanoidModel pInnerModel, HumanoidModel outerModel) {
        super(entityRendererIn);
        this.innerModel = pInnerModel;
        this.outerModel = outerModel;
    }

    private static final Map<String, ResourceLocation> ARMOR_LOCATION_CACHE = Maps.newHashMap();
    private final HumanoidModel innerModel;
    private final HumanoidModel outerModel;

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        this.renderArmor(poseStack,partialTick,bufferSource,packedLight,animatable);
        super.render(poseStack, animatable, bakedModel, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
    }

    public void renderArmor(PoseStack pMatrixStack,  float partialTick, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity) {
        this.renderArmorPiece(pMatrixStack, pBuffer,partialTick, pLivingEntity, ArmorType.HEAD, pPackedLight, this.getArmorModel(EquipmentSlot.CHEST));
        this.renderArmorPiece(pMatrixStack, pBuffer,partialTick, pLivingEntity, ArmorType.RIGHT_ARM, pPackedLight, this.getArmorModel(EquipmentSlot.LEGS));
        this.renderArmorPiece(pMatrixStack, pBuffer,partialTick, pLivingEntity, ArmorType.LEFT_ARM, pPackedLight, this.getArmorModel(EquipmentSlot.FEET));
        this.renderArmorPiece(pMatrixStack, pBuffer,partialTick, pLivingEntity, ArmorType.RIGHT_BOOT, pPackedLight, this.getArmorModel(EquipmentSlot.HEAD));
        this.renderArmorPiece(pMatrixStack, pBuffer,partialTick, pLivingEntity, ArmorType.LEFT_BOOT, pPackedLight, this.getArmorModel(EquipmentSlot.HEAD));
    }

    protected void renderArmorPiece(PoseStack poseStack, MultiBufferSource bufferSource,float partialTick, T entity, ArmorType pSlot, int pPackedLight, HumanoidModel model1) {
        EquipmentSlot equipmentSlot = armorToEquipment(pSlot);
        ItemStack itemstack = entity.getItemBySlot(equipmentSlot);
        Item $$9 = itemstack.getItem();
        poseStack.pushPose();
        if ($$9 instanceof ArmorItem armoritem) {
            ManModel geoModel = (ManModel) getGeoModel();
            if (armoritem.getEquipmentSlot() == equipmentSlot) {
                net.minecraft.client.model.Model model2 = getArmorModelHook(entity, itemstack, equipmentSlot, model1);
                if(model2 instanceof HumanoidModel<?> model){
                    model.riding = false;
                    model.young = false;
                    poseStack.translate(0, 1.5, 0);
                    poseStack.mulPose(MathUtils.quatFromRotationXYZ(0, 0, 180, true));
                    poseStack.mulPose(MathUtils.quatFromRotationXYZ(0, Mth.lerp(partialTick, entity.yRotO, entity.getYRot())+180, 0, true));
                    setPartVisibility(model, pSlot,poseStack,geoModel);

                    this.renderModel(poseStack, bufferSource, pPackedLight, model, 1.0F, 1.0F, 1.0F, this.getArmorResource(entity, itemstack, equipmentSlot, null));
                }
            }
        }
        poseStack.popPose();
    }

    private void renderModel(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, net.minecraft.client.model.Model pModel, float pRed, float pGreen, float pBlue, ResourceLocation armorResource) {
        VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.armorCutoutNoCull(armorResource));
        pModel.renderToBuffer(pPoseStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, pRed, pGreen, pBlue, 1.0F);
    }

    private HumanoidModel getArmorModel(EquipmentSlot pSlot) {
        return (this.usesInnerModel(pSlot) ? this.innerModel : this.outerModel);
    }

    protected boolean usesInnerModel(EquipmentSlot pSlot) {
        return pSlot == EquipmentSlot.LEGS;
    }

    protected net.minecraft.client.model.Model getArmorModelHook(T entity, ItemStack itemStack, EquipmentSlot slot, HumanoidModel model) {
        return net.minecraftforge.client.ForgeHooksClient.getArmorModel(entity, itemStack, slot, model);
    }

    public ResourceLocation getArmorResource(net.minecraft.world.entity.Entity entity, ItemStack stack, EquipmentSlot slot, @Nullable String type) {
        ArmorItem item = (ArmorItem)stack.getItem();
        String texture = item.getMaterial().getName();
        String domain = "minecraft";
        int idx = texture.indexOf(':');
        if (idx != -1) {
            domain = texture.substring(0, idx);
            texture = texture.substring(idx + 1);
        }
        String s1 = String.format(java.util.Locale.ROOT, "%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, (usesInnerModel(slot) ? 2 : 1), type == null ? "" : String.format(java.util.Locale.ROOT, "_%s", type));

        s1 = net.minecraftforge.client.ForgeHooksClient.getArmorTexture(entity, stack, s1, slot, type);
        ResourceLocation resourcelocation = ARMOR_LOCATION_CACHE.get(s1);

        if (resourcelocation == null) {
            resourcelocation = new ResourceLocation(s1);
            ARMOR_LOCATION_CACHE.put(s1, resourcelocation);
        }

        return resourcelocation;
    }

    private EquipmentSlot armorToEquipment(ArmorType armorType){
        return switch (armorType){
            case HEAD -> EquipmentSlot.HEAD;
            case RIGHT_ARM, LEFT_ARM -> EquipmentSlot.CHEST;
            default -> EquipmentSlot.FEET;
        };
    }

    protected void setPartVisibility(HumanoidModel pModel, ArmorType pSlot, PoseStack poseStack, ManModel geckoModel) {
        pModel.setAllVisible(false);

        switch (pSlot) {
            case HEAD:
                Pair<Vector3f, PoseStack> head = MathUtils.getModelPosFromModel(geckoModel.bipedHead());

                Matrix4f headPose = head.getSecond().last().pose();
                Vector4f headVec4 = new Vector4f(0,0,0,1f);
                Vector3f headRot = head.getFirst();
                headVec4.mul(headPose);
                poseStack.last().pose().translate(-headVec4.x,-(headVec4.y-1.5f),-headVec4.z);
                poseStack.last().pose().rotate(MathUtils.quatFromRotationXYZ(-headRot.x, -headRot.y, -headRot.z, false));
                pModel.head.visible = true;
                pModel.hat.visible = true;
                break;
            case RIGHT_ARM:
                Pair<Vector3f, PoseStack> rightArm = MathUtils.getModelPosFromModel(geckoModel.bipedRightArm());

                Matrix4f rightArmPose = rightArm.getSecond().last().pose();
                Vector4f rightArmVec4 = new Vector4f(0,0,0,1f);
                Vector3f rightArmRot = rightArm.getFirst();
                rightArmVec4.mul(rightArmPose);
                poseStack.last().pose().translate(rightArmVec4.x-0.375f,-(rightArmVec4.y-1.34f),rightArmVec4.z);
                poseStack.last().pose().rotate(MathUtils.quatFromRotationXYZ(-rightArmRot.x, -rightArmRot.y, -rightArmRot.z, false));
                pModel.rightArm.visible = true;
                break;
            case LEFT_ARM:
                Pair<Vector3f, PoseStack> leftArm = MathUtils.getModelPosFromModel(geckoModel.bipedLeftArm());

                Matrix4f leftArmPose = leftArm.getSecond().last().pose();
                Vector4f leftArmVec4 = new Vector4f(0,0,0,1f);
                Vector3f leftArmRot = leftArm.getFirst();
                leftArmVec4.mul(leftArmPose);
                poseStack.last().pose().translate(leftArmVec4.x+0.375f,-(leftArmVec4.y-1.34f),leftArmVec4.z);
                poseStack.last().pose().rotate(MathUtils.quatFromRotationXYZ(-leftArmRot.x, -leftArmRot.y, -leftArmRot.z, false));
                pModel.leftArm.visible = true;
                break;
            case RIGHT_BOOT:
                Pair<Vector3f, PoseStack> rightBoot = MathUtils.getModelPosFromModel(geckoModel.bipedRightLeg());

                Matrix4f rightBootPose = rightBoot.getSecond().last().pose();
                Vector4f rightBootVec4 = new Vector4f(0,0,0,1f);
                Vector3f rightBootRot = rightBoot.getFirst();
                rightBootVec4.mul(rightBootPose);
                poseStack.last().pose().rotate(MathUtils.quatFromRotationXYZ(-rightBootRot.x, -rightBootRot.y, -rightBootRot.z, false));
                Vector3f RLtem=new Vector3f(0,-1.15f,0);
                RLtem.rotate(MathUtils.quatFromRotationXYZ(-rightBootRot.x, -rightBootRot.y, -rightBootRot.z, false));
                poseStack.last().pose().translate(RLtem.x,-(RLtem.y+1.15f),RLtem.z);

                poseStack.last().pose().translate(rightBootVec4.x-0.125f,-(rightBootVec4.y-0.375f),rightBootVec4.z);
                pModel.rightLeg.visible = true;
                break;
            case LEFT_BOOT:
                Pair<Vector3f, PoseStack> leftBoot = MathUtils.getModelPosFromModel(geckoModel.bipedLeftLeg());

                Matrix4f leftBootPose = leftBoot.getSecond().last().pose();
                Vector4f leftBootVec4 = new Vector4f(0,0,0,1f);
                Vector3f leftBootRot = leftBoot.getFirst();
                leftBootVec4.mul(leftBootPose);
                poseStack.last().pose().rotate(MathUtils.quatFromRotationXYZ(-leftBootRot.x, -leftBootRot.y, -leftBootRot.z, false));
                Vector3f LLtem=new Vector3f(0,-1.15f,0);
                LLtem.rotate(MathUtils.quatFromRotationXYZ(-leftBootRot.x, -leftBootRot.y, -leftBootRot.z, false));
                poseStack.last().pose().translate(LLtem.x,-(LLtem.y+1.15f),LLtem.z);

                poseStack.last().pose().translate(leftBootVec4.x+0.125f,-(leftBootVec4.y-0.375f),leftBootVec4.z);
                pModel.leftLeg.visible = true;
                break;
        }

    }

    private enum ArmorType{
        HEAD,RIGHT_ARM,LEFT_ARM,RIGHT_BOOT,LEFT_BOOT
    }
}
