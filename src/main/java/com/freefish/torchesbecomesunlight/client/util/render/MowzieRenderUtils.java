package com.freefish.torchesbecomesunlight.client.util.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import software.bernie.geckolib.cache.object.GeoBone;

public class MowzieRenderUtils {
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
