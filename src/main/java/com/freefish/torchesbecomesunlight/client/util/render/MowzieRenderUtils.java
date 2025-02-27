package com.freefish.torchesbecomesunlight.client.util.render;

import com.freefish.torchesbecomesunlight.client.render.model.player.ModelGeckoPlayerThirdPerson;
import com.freefish.torchesbecomesunlight.client.render.model.tools.MathUtils;
import com.freefish.torchesbecomesunlight.client.render.model.tools.geckolib.MowzieGeoBone;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class MowzieRenderUtils {

    public static void translateRotateGeckolib(GeoBone bone, PoseStack matrixStackIn) {
        matrixStackIn.translate((double)(bone.getPivotX() / 16.0F), (double)(bone.getPivotY() / 16.0F), (double)(bone.getPivotZ() / 16.0F));
        if (bone.getRotZ() != 0.0F) {
            matrixStackIn.mulPose(Axis.ZP.rotation(bone.getRotZ()));
        }

        if (bone.getRotY() != 0.0F) {
            matrixStackIn.mulPose(Axis.YP.rotation(bone.getRotY()));
        }

        if (bone.getRotX() != 0.0F) {
            matrixStackIn.mulPose(Axis.XP.rotation(bone.getRotX()));
        }

        matrixStackIn.scale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
    }

    public static void matrixStackFromModel(PoseStack matrixStack, GeoBone geoBone) {
        GeoBone parent = geoBone.getParent();
        if (parent != null) matrixStackFromModel(matrixStack, parent);
        translateRotateGeckolib(geoBone, matrixStack);
    }

    public static Vec3 getWorldPosFromModel(Entity entity, float entityYaw, GeoBone geoBone) {
        PoseStack matrixStack = new PoseStack();
        matrixStack.translate(entity.getX(), entity.getY(), entity.getZ());
        matrixStack.mulPose(MathUtils.quatFromRotationXYZ(0, -entityYaw + 180, 0, true));
        matrixStack.scale(-1, -1, 1);
        matrixStack.translate(0, -1.5f, 0);
        MowzieRenderUtils.matrixStackFromModel(matrixStack, geoBone);
        PoseStack.Pose matrixEntry = matrixStack.last();
        Matrix4f matrix4f = matrixEntry.pose();

        Vector4f vec = new Vector4f(0, 0, 0, 1);
        vec.mul(matrix4f);
        return new Vec3(vec.x(), vec.y(), vec.z());
    }

    // Mirrored render utils
    public static void moveToPivotMirror(PoseStack stack, GeoCube cube) {
        Vec3 pivot = cube.pivot();
        stack.translate((double)(-pivot.x() / 16.0F), (double)(pivot.y() / 16.0F), (double)(pivot.z() / 16.0F));
    }

    public static void translateAwayFromPivotPointMirror(PoseStack stack, GeoCube cube) {
        Vec3 pivot = cube.pivot();
        stack.translate((double)(pivot.x() / 16.0F), (double)(-pivot.y() / 16.0F), (double)(-pivot.z() / 16.0F));
    }

    public static void moveToPivotMirror(PoseStack stack, GeoBone bone) {
        stack.translate((double)(-bone.getPivotX() / 16.0F), (double)(bone.getPivotY() / 16.0F), (double)(bone.getPivotZ() / 16.0F));
    }

    public static void translateAwayFromPivotPointMirror(PoseStack stack, GeoBone bone) {
        stack.translate((double)(bone.getPivotX() / 16.0F), (double)(-bone.getPivotY() / 16.0F), (double)(-bone.getPivotZ() / 16.0F));
    }

    public static void translateMirror(PoseStack stack, GeoBone bone) {
        stack.translate((double)(bone.getPosX() / 16.0F), (double)(bone.getPosY() / 16.0F), (double)(bone.getPosZ() / 16.0F));
    }

    public static void rotateMirror(PoseStack stack, GeoBone bone) {
        if (bone.getRotZ() != 0.0F) {
            stack.mulPose(Axis.ZP.rotation(-bone.getRotZ()));
        }

        if (bone.getRotY() != 0.0F) {
            stack.mulPose(Axis.YP.rotation(-bone.getRotY()));
        }

        if (bone.getRotX() != 0.0F) {
            stack.mulPose(Axis.XP.rotation(bone.getRotX()));
        }

    }
}
