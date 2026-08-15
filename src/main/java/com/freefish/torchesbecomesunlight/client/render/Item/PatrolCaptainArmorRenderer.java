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
        super(new DefaultedItemGeoModel<>(ResourceLocation.fromNamespaceAndPath(TorchesBecomeSunlight.MOD_ID, "armor/patrol_captain_armor")));
    }

    @Override
    public void renderCubesOfBone(PoseStack poseStack, GeoBone bone, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        float red = net.minecraft.util.FastColor.ARGB32.red(color) / 255.0F;
        float green = net.minecraft.util.FastColor.ARGB32.green(color) / 255.0F;
        float blue = net.minecraft.util.FastColor.ARGB32.blue(color) / 255.0F;
        float alpha = net.minecraft.util.FastColor.ARGB32.alpha(color) / 255.0F;
        super.renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, net.minecraft.util.FastColor.ARGB32.colorFromFloat(alpha, red, green, blue));
    }
}
