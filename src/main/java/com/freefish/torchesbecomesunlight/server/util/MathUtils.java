package com.freefish.torchesbecomesunlight.server.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.*;
import software.bernie.geckolib.cache.object.GeoBone;

import java.lang.Math;
import java.util.List;

/**
 * @author Paul Fulham
 */
public final class MathUtils {

    public static final float TAU = (float) (2 * StrictMath.PI);
    public static final float PI = (float) StrictMath.PI;

    public static double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    public static double easeOutCirc(double t){
        return Math.sqrt(1 - Math.pow(t - 1, 2));
    }

    public static Vector3f VectorDTOVectorF(Vector3d vector3d){
        return new Vector3f((float) vector3d.x,(float)vector3d.y,(float)vector3d.z);
    }

    public static int ceil(double f){
        return (int) Math.ceil(f);
    }

    public static <T extends Entity> T getClosestEntity(Entity target, List<T> entities) {
        T closestEntity = null;
        double closestDistanceSq = Double.MAX_VALUE;

        for (T entity : entities) {
            if (entity != target) {
                double distanceSq = entity.distanceToSqr(target);
                if (distanceSq < closestDistanceSq) {
                    closestDistanceSq = distanceSq;
                    closestEntity = entity;
                }
            }
        }

        return closestEntity;
    }

    public static void matrixStackFromModel(PoseStack matrixStack, GeoBone geoBone) {
        GeoBone parent = geoBone.getParent();
        if (parent != null) matrixStackFromModel(matrixStack, parent);
        translateRotateGeckolib(geoBone, matrixStack);
    }

    public static Vec3 getWorldPosFromModel(Entity entity, float entityYaw, GeoBone geoBone) {
        PoseStack matrixStack = new PoseStack();
        matrixStack.translate(entity.getX(), entity.getY(), entity.getZ());
        matrixStack.mulPose(new Quaternionf(0,   -entityYaw - 180, 180, 1));
        matrixStack.scale(-1, -1, 1);
        matrixStackFromModel(matrixStack, geoBone);
        PoseStack.Pose matrixEntry = matrixStack.last();
        Matrix4f matrix4f = matrixEntry.pose();

        Vector4f vec = new Vector4f(0, 0, 0, 1);
        vec.mulTranspose(matrix4f);
        return new Vec3(vec.x, vec.y, vec.z);
    }

    public static void translateRotateGeckolib(GeoBone bone, PoseStack matrixStackIn) {
        if(bone.getParent() != null)
            matrixStackIn.translate((double)((bone.getRotX()-bone.getParent().getRotX() + bone.getPosX())/ 16.0F),
                    (double)((bone.getRotY()-bone.getParent().getRotY() + bone.getPosY()) / 16.0F),
                    (double)((bone.getRotZ()-bone.getParent().getRotZ() + bone.getPosZ()) / 16.0F));
        else
            matrixStackIn.translate((double)(bone.getRotX() / 16.0F), (double)(bone.getRotY() / 16.0F), (double)(bone.getRotZ() / 16.0F));

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

    public static double linearTransformd(double x, double domainMin, double domainMax, double rangeMin, double rangeMax) {
        x = x < domainMin ? domainMin : x > domainMax ? domainMax : x;
        return (rangeMax - rangeMin) * (x - domainMin) / (domainMax - domainMin) + rangeMin;
    }

    public static float fade(float i){
        i=i/2 + 0.5f;
        return (6*i*i*i*i*i-15*i*i*i*i+10*i*i*i - 0.5f)*2;
    }

    public static float sampleNoise3D(int x, int y, int z, float simplexSampleRate) {
        return (float) ((SimplexNoise.noise((x + simplexSampleRate) / simplexSampleRate, (y + simplexSampleRate) / simplexSampleRate, (z + simplexSampleRate) / simplexSampleRate)));
    }

    public static float cosFromSin(float sin, float angle) {
        float PI_f = (float) Math.PI;
        float PIHalf_f = (float) (Math.PI / 2);
        float PI2_f = PI_f * 2.0f;
        float cos = (float) Math.sqrt(1.0f - sin * sin);
        float a = angle + PIHalf_f;
        float b = a - (int)(a / PI2_f) * PI2_f;
        if (b < 0.0)
            b = PI2_f + b;
        if (b >= PI_f)
            return -cos;
        return cos;
    }

    public static class ABGR32 {
        public static int alpha(int p_267257_) {
            return p_267257_ >>> 24;
        }

        public static int red(int p_267160_) {
            return p_267160_ & 255;
        }

        public static int green(int p_266784_) {
            return p_266784_ >> 8 & 255;
        }

        public static int blue(int p_267087_) {
            return p_267087_ >> 16 & 255;
        }

        public static int transparent(int p_267248_) {
            return p_267248_ & 16777215;
        }

        public static int opaque(int p_268288_) {
            return p_268288_ | -16777216;
        }

        public static int color(int p_267196_, int p_266895_, int p_266779_, int p_267206_) {
            return p_267196_ << 24 | p_266895_ << 16 | p_266779_ << 8 | p_267206_;
        }

        public static int color(int p_267230_, int p_266708_) {
            return p_267230_ << 24 | p_266708_ & 16777215;
        }
    }
}
