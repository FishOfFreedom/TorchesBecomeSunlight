package com.freefish.torchesbecomesunlight.client.render.entity.player;

import com.freefish.torchesbecomesunlight.client.render.model.player.ModelBipedAnimated;
import com.freefish.torchesbecomesunlight.client.render.model.player.ModelGeckoPlayerThirdPerson;
import com.freefish.torchesbecomesunlight.client.render.model.tools.geckolib.MowzieGeoBone;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import software.bernie.geckolib.animatable.GeoItem;

public class GeckoArmorLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends HumanoidArmorLayer<T, M, A> {
    private ModelGeckoPlayerThirdPerson geoModel;

    public GeckoArmorLayer(RenderLayerParent<T, M> layerParent, A innerModel, A outerModel, ModelManager modelManager, ModelGeckoPlayerThirdPerson geo) {
        super(layerParent, innerModel, outerModel, modelManager);
        this.geoModel = geo;
    }

    @Override
    protected void renderArmorPiece(PoseStack poseStack, MultiBufferSource bufferSource, T entity, EquipmentSlot equipmentSlot, int p_117123_, A baseModel) {
        ItemStack itemstack = entity.getItemBySlot(equipmentSlot);
        if (itemstack.getItem() instanceof ArmorItem) {
            ArmorItem armoritem = (ArmorItem)itemstack.getItem();
            if (armoritem.getType().getSlot() == equipmentSlot) {
                net.minecraft.client.model.Model model = getArmorModelHook(entity, itemstack, equipmentSlot, baseModel);
                if (model instanceof HumanoidModel<?>) {
                    HumanoidModel<T> humanoidModel = (HumanoidModel<T>) model;
                    this.getParentModel().copyPropertiesTo(baseModel);
                    this.getParentModel().copyPropertiesTo(humanoidModel);
                    this.setPartVisibility(baseModel, equipmentSlot);
                    this.setPartVisibility((A) humanoidModel, equipmentSlot);
                    boolean flag = this.usesInnerModel(equipmentSlot);
                    boolean flag1 = itemstack.hasFoil();
                    ModelBipedAnimated.setUseMatrixMode(humanoidModel, true);

                    if(armoritem instanceof GeoItem){
                        if(armoritem.getType()== ArmorItem.Type.HELMET){
                            MowzieGeoBone bone = geoModel.bipedHead();
                            PoseStack newMatrixStack = new PoseStack();
                            newMatrixStack.last().normal().mul(bone.getWorldSpaceNormal());
                            newMatrixStack.last().pose().mul(bone.getWorldSpaceMatrix());
                            poseStack = newMatrixStack;
                        }
                        if(armoritem.getType()== ArmorItem.Type.CHESTPLATE){
                            MowzieGeoBone bone = geoModel.bipedBody();
                            PoseStack newMatrixStack = new PoseStack();
                            newMatrixStack.last().normal().mul(bone.getWorldSpaceNormal());
                            newMatrixStack.last().pose().mul(bone.getWorldSpaceMatrix());
                            poseStack = newMatrixStack;
                        }
                    }

                    // 1.21 起护甲贴图由 ArmorMaterial.Layer 提供，染色改走 DYED_COLOR 组件；
                    // 原先的 getArmorResource + DyeableLeatherItem 两条路径已不存在
                    int dyeColor = itemstack.is(ItemTags.DYEABLE)
                            ? FastColor.ARGB32.opaque(DyedItemColor.getOrDefault(itemstack, -6265536))
                            : -1;
                    for (ArmorMaterial.Layer layer : armoritem.getMaterial().value().layers()) {
                        this.renderModel(poseStack, bufferSource, p_117123_, model,
                                layer.dyeable() ? dyeColor : -1, layer.texture(flag));
                    }

                    PoseStack finalPoseStack = poseStack;
                    ArmorTrim trim = itemstack.get(DataComponents.TRIM);
                    if (trim != null) {
                        ModelBipedAnimated.setUseMatrixMode(humanoidModel, true);
                        this.renderTrim(armoritem.getMaterial(), finalPoseStack, bufferSource, p_117123_, trim, model, flag);
                    }
                    if (itemstack.hasFoil()) {
                        ModelBipedAnimated.setUseMatrixMode(humanoidModel, true);
                        this.renderGlint(poseStack, bufferSource, p_117123_, model);
                    }
                }
            }
        }
    }

    private void renderModel(PoseStack p_117107_, MultiBufferSource p_117108_, int p_117109_, net.minecraft.client.model.Model p_117112_, int color, ResourceLocation armorResource) {
        VertexConsumer vertexconsumer = p_117108_.getBuffer(RenderType.armorCutoutNoCull(armorResource));
        if (p_117112_ instanceof MowzieGeoArmorRenderer<?>) ((MowzieGeoArmorRenderer<?>) p_117112_).usingCustomPlayerAnimations = true;
        p_117112_.renderToBuffer(p_117107_, vertexconsumer, p_117109_, OverlayTexture.NO_OVERLAY, color);
    }

    private void renderTrim(Holder<ArmorMaterial> p_289690_, PoseStack p_289687_, MultiBufferSource p_289643_, int p_289683_, ArmorTrim p_289692_, net.minecraft.client.model.Model p_289663_, boolean p_289651_) {
        TextureAtlasSprite textureatlassprite = this.armorTrimAtlas.getSprite(p_289651_ ? p_289692_.innerTexture(p_289690_) : p_289692_.outerTexture(p_289690_));
        VertexConsumer vertexconsumer = textureatlassprite.wrap(p_289643_.getBuffer(Sheets.armorTrimsSheet(p_289692_.pattern().value().decal())));
        p_289663_.renderToBuffer(p_289687_, vertexconsumer, p_289683_, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
    }

    private void renderGlint(PoseStack p_289673_, MultiBufferSource p_289654_, int p_289649_, net.minecraft.client.model.Model p_289659_) {
        p_289659_.renderToBuffer(p_289673_, p_289654_.getBuffer(RenderType.armorEntityGlint()), p_289649_, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
    }
}
