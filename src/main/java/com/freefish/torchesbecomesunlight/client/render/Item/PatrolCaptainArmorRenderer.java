package com.freefish.torchesbecomesunlight.client.render.Item;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.item.armor.patrolcaptain.PatrolCaptainArmorItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public final class PatrolCaptainArmorRenderer extends GeoArmorRenderer<PatrolCaptainArmorItem> {
    public PatrolCaptainArmorRenderer() {
        super(new DefaultedItemGeoModel<>(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "armor/patrol_captain_armor")));
    }

    @Override
    public void renderCubesOfBone(PoseStack poseStack, GeoBone bone, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
