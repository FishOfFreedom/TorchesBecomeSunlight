package com.freefish.torchesbecomesunlight.client.render.entity.player;

import com.freefish.torchesbecomesunlight.client.render.model.tools.MathUtils;
import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.PlayerCapability;
import com.freefish.torchesbecomesunlight.server.init.ItemHandle;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class SakataLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    
    private final ItemInHandRenderer itemInHandRenderer;
    private static final ItemStack HEAD_ITEM = new ItemStack(Items.DIAMOND);
    private static final ItemStack HEAD_ITEM_1 = new ItemStack(ItemHandle.SANKTA_RING.get());
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("torchesbecomesunlight:textures/item/sankta_wind.png");

    public SakataLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer,
                       ItemInHandRenderer itemInHandRenderer) {
        super(renderer);
        this.itemInHandRenderer = itemInHandRenderer;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       AbstractClientPlayer player, float limbSwing, float limbSwingAmount,
                       float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean flad;
        PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(player, CapabilityHandle.PLAYER_CAPABILITY);
        if(capability!=null&&capability.isSankta()){
            flad = true;
        }else{
            flad = false;
        }
        
        if (player.isInvisible() || !flad ||
                (player == Minecraft.getInstance().player &&
             Minecraft.getInstance().options.getCameraType().isFirstPerson())) {
            return;
        }

        poseStack.pushPose();
        
        // 使用头部模型的变换
        this.getParentModel().head.translateAndRotate(poseStack);
        
        // 调整位置到头顶中心
        poseStack.translate(0.0D, -0.7, 0.0D);
        
        // 调整方向
        //poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F)); // 旋转180度
        poseStack.mulPose(MathUtils.quatFromRotationXYZ(90,0,0,true)); // 上下翻转

        // 缩放
        float scale = 1.F;
        poseStack.scale(scale, scale, scale);
        
        // 渲染物品
        itemInHandRenderer.renderItem(
            player,
                HEAD_ITEM_1,
                ItemDisplayContext.GUI,
            false, 
            poseStack, 
            buffer,
                0xF000F0
        );
        
        poseStack.popPose();

        renderBackImage(poseStack,buffer,packedLight,partialTicks);
    }

    public void renderBackImage(PoseStack poseStack, MultiBufferSource buffer, int packedLight, float partialTicks) {
        poseStack.pushPose();

        // 1. 应用玩家身体变换
        this.getParentModel().body.translateAndRotate(poseStack);

        // 2. 调整到背部位置
        poseStack.translate(0.0D, 12.0D / 16.0D, -0.5D); // Y: 玩家身体中心高度，Z: 背后偏移

        // 3. 调整方向（面向玩家后方）
        //poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F)); // 旋转180度面向背后

        // 4. 设置图片尺寸
        float width = 2F;  // 图片宽度（单位：格）
        float height = 2F; // 图片高度
        float offset = 0.8F; // 轻微偏移防止Z-fighting

        // 5. 获取顶点构建器
        VertexConsumer vertexBuilder = buffer.getBuffer(RenderType.entityTranslucent(TEXTURE_LOCATION));
        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        // 6. 计算四个顶点位置
        Vector3f[] vertices = {
                new Vector3f(-width/2, -height/2, offset), // 左下
                new Vector3f(-width/2, height/2, offset),  // 左上
                new Vector3f(width/2, height/2, offset),   // 右上
                new Vector3f(width/2, -height/2, offset)   // 右下
        };

        // 7. 应用当前变换
        //for (Vector3f vertex : vertices) {
        //    vertex.transform(normal);
        //}
//
        // 8. 设置纹理坐标 (UV)
        float uMin = 0.0F;
        float uMax = 1.0F;
        float vMin = 0.0F;
        float vMax = 1.0F;

        // 9. 设置颜色和发光
        float r = 1.0F, g = 1.0F, b = 1.0F, alpha = 1.0F; // 白色不透明
        int light = 0xF000F0; // 最大发光值

        // 10. 构建两个三角形（组成矩形）
        // 三角形1 (左下 -> 左上 -> 右上)
        vertexBuilder.vertex(pose, vertices[0].x(), vertices[0].y(), vertices[0].z()).color(r, g, b, alpha).uv(uMin, vMax).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 0, 1).endVertex();
        vertexBuilder.vertex(pose, vertices[1].x(), vertices[1].y(), vertices[1].z()).color(r, g, b, alpha).uv(uMin, vMin).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 0, 1).endVertex();
        vertexBuilder.vertex(pose, vertices[2].x(), vertices[2].y(), vertices[2].z()).color(r, g, b, alpha).uv(uMax, vMin).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 0, 1).endVertex();
        vertexBuilder.vertex(pose, vertices[3].x(), vertices[3].y(), vertices[3].z()).color(r, g, b, alpha).uv(uMax, vMax).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 0, 1).endVertex();

        poseStack.popPose();
    }
}