package com.freefish.torchesbecomesunlight.client.render.layer;

import com.freefish.torchesbecomesunlight.server.entity.guerrillas.shield.Patriot;
import com.freefish.torchesbecomesunlight.server.util.storage.ClientStorage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class PatriotLocateLayer extends GeoRenderLayer<Patriot> {
    public PatriotLocateLayer(GeoRenderer<Patriot> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack matrixStackIn, Patriot patriot, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTicks, int packedLight, int packedOverlay) {
        Entity heldMob = null;
        if (heldMob != null) {
            ClientStorage.INSTANCE.releaseRenderingEntity(heldMob.getUUID());
            float vehicleRot = patriot.yBodyRotO + (patriot.yBodyRot - patriot.yBodyRotO) * partialTicks;
            float riderRot = 0;
            Vec3 handPosition = patriot.position().add(0,1,0);
            matrixStackIn.translate(handPosition.x, handPosition.y, handPosition.z);
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180F));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(vehicleRot - riderRot));
            if (! ClientStorage.INSTANCE.isFirstPersonPlayer(heldMob)) {
                //renderEntity(heldMob, 0, 0, 0, 0, partialTicks, matrixStackIn, bufferSource, packedLight);
            }
            matrixStackIn.popPose();
            ClientStorage.INSTANCE.blockRenderingEntity(heldMob.getUUID());
        }
    }

    public <E extends Entity> void renderEntity(E entityIn, double x, double y, double z, float yaw, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int packedLight) {
        EntityRenderer<? super E> render;
        EntityRenderDispatcher manager = Minecraft.getInstance().getEntityRenderDispatcher();
        try {
            render = manager.getRenderer(entityIn);
            if (render != null) {
                try {
                    render.render(entityIn, yaw, partialTicks, matrixStack, bufferIn, packedLight);
                } catch (Throwable throwable1) {
                }
            }
        } catch (Throwable throwable3) {
        }
    }
}
