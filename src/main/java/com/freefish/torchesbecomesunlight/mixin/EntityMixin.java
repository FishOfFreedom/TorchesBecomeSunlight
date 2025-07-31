package com.freefish.torchesbecomesunlight.mixin;

import com.freefish.torchesbecomesunlight.server.effect.forceeffect.ForceEffectHandle;
import com.freefish.torchesbecomesunlight.server.effect.forceeffect.ForceEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @ModifyVariable(
            method = "move",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private Vec3 move(Vec3 targetPos, MoverType pType) {
        Entity entity = (Entity) (Object) this;
        if(entity instanceof LivingEntity living){
            ForceEffectInstance forceEffect = ForceEffectHandle.getForceEffect(living, ForceEffectHandle.FROZEN_FORCE_EFFECT);
            if (forceEffect != null&&forceEffect.getLevel()>=2) {
                return new Vec3(0,targetPos.y<0? targetPos.y:0,0);
            }
            ForceEffectInstance slowMoveEffect = ForceEffectHandle.getForceEffect(living, ForceEffectHandle.SLOW_MOVE_FORCE_EFFECT);
            if (slowMoveEffect != null) {
                int level = slowMoveEffect.getLevel();
                float v = (10 - level) / 10f;
                return targetPos.scale(Math.max(v,0));
            }
        }
        return targetPos;
    }
}