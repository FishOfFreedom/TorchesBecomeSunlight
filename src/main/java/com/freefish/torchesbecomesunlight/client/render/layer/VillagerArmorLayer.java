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
import org.joml.Vector3f;
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
                    poseStack.last().pose().rotate(MathUtils.quatFromRotationXYZ(0, 0, 180, true));
                    poseStack.last().pose().rotate(MathUtils.quatFromRotationXYZ(0, Mth.lerp(partialTick, entity.yRotO, entity.getYRot())+180, 0, true));
                    poseStack.pushPose();
                    model.setAllVisible(false);

                    switch (pSlot) {
                        case HEAD:
                            Pair<Vector3f, PoseStack> head = MathUtils.getModelPosFromModel(poseStack,geoModel.bipedHead());
                            model.head.visible = true;
                            model.hat.visible = true;
                            break;
                        case RIGHT_ARM:
                            Pair<Vector3f, PoseStack> rightArm = MathUtils.getModelPosFromModel(poseStack,geoModel.bipedRightArm());
                            poseStack.last().pose().translate(0.3f,-0.1f,0f);
                            model.rightArm.visible = true;
                            break;
                        case LEFT_ARM:
                            Pair<Vector3f, PoseStack> leftArm = MathUtils.getModelPosFromModel(poseStack,geoModel.bipedLeftArm());
                            poseStack.last().pose().translate(-0.3f,-0.1f,0f);
                            model.leftArm.visible = true;
                            break;
                        case RIGHT_BOOT:
                            Pair<Vector3f, PoseStack> rightBoot = MathUtils.getModelPosFromModel(poseStack,geoModel.bipedRightLeg());
                            poseStack.last().pose().translate(0.125f,-1.125f,0f);

                            model.rightLeg.visible = true;
                            break;
                        case LEFT_BOOT:
                            Pair<Vector3f, PoseStack> leftBoot = MathUtils.getModelPosFromModel(poseStack,geoModel.bipedLeftLeg());
                            poseStack.last().pose().translate(-0.125f,-1.125f,0f);

                            model.leftLeg.visible = true;
                            break;
                    }
                    this.renderModel(poseStack, bufferSource, pPackedLight, model, 1.0F, 1.0F, 1.0F, this.getArmorResource(entity, itemstack, equipmentSlot, null));

                    poseStack.popPose();
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

    private enum ArmorType{
        HEAD,RIGHT_ARM,LEFT_ARM,RIGHT_BOOT,LEFT_BOOT
    }
}
