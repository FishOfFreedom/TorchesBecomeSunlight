package com.freefish.torchesbecomesunlight.compat.rosmontis;

import com.freefish.rosmontislib.client.particle.advance.base.IFXObject;
import com.freefish.rosmontislib.client.particle.advance.effect.FXEffect;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.GeoBone;

public class EntityPosRotEffect extends FXEffect {
    private final LivingEntity living;

    public EntityPosRotEffect(Level level, LivingEntity living) {
        super(level);
        this.living = living;
    }

    @Override
    public void updateFXObjectFrame(IFXObject fxObject, float partialTicks) {
        if(living!=null&&living.isAlive()){
            Vec3 position = this.living.getPosition(partialTicks);
            fxObject.updatePos(new Vector3f((float) (position.x + (double) this.offset.x), (float) (position.y + (double) this.offset.y), (float) (position.z + (double) this.offset.z)));
            fxObject.updateRotation(new Vector3f(0, (float) (-living.getYRot() / 180 * Math.PI), 0));
        }
    }

    @Override
    public void start() {
    }
}
