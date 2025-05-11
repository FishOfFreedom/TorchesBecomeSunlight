package com.freefish.torchesbecomesunlight.server.util;

import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class FFEntityUtils {
    public static boolean isBeneficial(MobEffect effect) {
        return effect != null && (effect.getCategory() == MobEffectCategory.BENEFICIAL || effect.isBeneficial());
    }
    /**
     * @Date 2024/12/29 7:29
     * @Description 从列表中选取一个里实体最近的实体;
     * @Param [target, entities]
     * @Return T
     */

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

    /**
     * @Date 2024/12/28 22:29
     * @Description 获取相位实体身体朝向位移后的坐标
     * @Param [entity, vec3]
     * @Return net.minecraft.world.phys.Vec3
     */

    public static Vec3 getBodyRotVec(Entity entity,Vec3 vec3){
        return vec3.yRot((float) (-entity.getYRot() / 180 * Math.PI)).add(entity.position());
    }

    /**
     * @Date 2024/12/28 22:29
     * @Description 获取相位实体头朝向位移后的坐标
     * @Param [entity, vec3]
     * @Return net.minecraft.world.phys.Vec3
     */

    public static Vec3 getHeadRotVec(Entity entity,Vec3 vec3){
        return vec3.xRot((float) (-entity.getXRot() / 180 * Math.PI)).yRot((float) (-entity.getYRot() / 180 * Math.PI)).add(entity.position());
    }

    /**
     * @Date 2024/12/28 22:30
     * @Description 获取一个坐标到目的坐标的yaw(0-360)
     * @Param [pos, target]
     * @Return float
     */

    public static float getPosToPosRot(Vec3 pos,Vec3 target){
        float entityHitAngle = (float) ((Math.atan2(target.z - pos.z, target.x - pos.x) * (180 / Math.PI) - 90) % 360);
        if (entityHitAngle < 0) {
            entityHitAngle += 360;
        }
        return entityHitAngle;
    }

    /**
     * @Date 2024/12/31 1:21
     * @Description 判断一个生物是否看向其他生物
     * @Param [livingEntity, pPlayer]
     * @Return boolean
     */

    public static boolean isLookingAtMe(LivingEntity livingEntity, LivingEntity pPlayer) {
        Vec3 vec3 = pPlayer.getViewVector(1.0F).normalize();
        Vec3 vec31 = new Vec3(livingEntity.getX() - pPlayer.getX(), livingEntity.getEyeY() - pPlayer.getEyeY(), livingEntity.getZ() - pPlayer.getZ());
        double d0 = vec31.length();
        vec31 = vec31.normalize();
        double d1 = vec3.dot(vec31);
        return d1 > 1D - 0.025D / d0 ? pPlayer.hasLineOfSight(livingEntity) : false;
    }

    /**
     * @Date 2025/5/1 8:14
     * @Description 判断一个生物到一个点之间是否有方块挡住整个生物
     * @Param
     * @Return boolean
     */

    public static boolean isLargeNumberBlockBlocking(LivingEntity livingEntity, Vec3 pos) {
        HitResult hitresult = livingEntity.level().clip(new ClipContext(pos, livingEntity.getEyePosition(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, livingEntity));
        if(hitresult.getType() == HitResult.Type.MISS){
            return false;
        }else {
            return true;
        }
    }

    /**
     * @Date 2024/12/31 1:26
     * @Description 从一个向量获取rotX-pirch rotY-yaw
     * @Param [vec3]
     * @Return net.minecraft.world.phys.Vec3
     */

    public static Vec3 getRotFromVec(Vec3 vec3) {
        double d0 = vec3.horizontalDistance();
        float rotY =((float) (Mth.atan2(vec3.x, vec3.z)));
        float rotX =((float) (Mth.atan2(vec3.y, d0)));
        return new Vec3(rotX,rotY,0);
    }


    public static void doRangeAttackFX(Entity attacker,double range, double arc,float yRot){
        if(false){
            int RANGE = (int) Math.ceil(range);
            for (int i = -RANGE; i <= RANGE; i++) {
                for (int j = -RANGE; j <= RANGE; j++) {
                    float entityHitAngle = (float) ((Math.atan2(i, j) * (180 / Math.PI) - 90)+ yRot % 360) ;
                    float entityAttackingAngle = attacker.getYRot() % 360;
                    if (entityHitAngle < 0) {
                        entityHitAngle += 360;
                    }
                    if (entityAttackingAngle < 0) {
                        entityAttackingAngle += 360;
                    }
                    double len = Math.sqrt((double) i * i + (double) j * j);
                    if (Math.abs(entityAttackingAngle - entityHitAngle) < arc && len <= range) {
                        ServerLevel level = (ServerLevel) attacker.level();
                        level.sendParticles(ParticleTypes.FLAME, attacker.getX() + j, attacker.getY() + 1, attacker.getZ() + i, 2, 0, 0, 0, 0);
                    }
                }
            }
        }
    }
}
