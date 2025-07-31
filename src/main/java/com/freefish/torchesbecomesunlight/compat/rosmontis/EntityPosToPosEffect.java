package com.freefish.torchesbecomesunlight.compat.rosmontis;

import com.freefish.rosmontislib.client.particle.advance.base.IFXObject;
import com.freefish.rosmontislib.client.particle.advance.effect.FXEffect;
import com.freefish.torchesbecomesunlight.server.util.FFEntityUtils;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class EntityPosToPosEffect extends FXEffect {
    private final LivingEntity living;
    private final Vec3 vec3offse;
    private final Vec3 vec3offse1;
    private int tickcount;
    private int tickcountO;

    public EntityPosToPosEffect(Level level, LivingEntity living, Vec3 vec3offse,Vec3 vec3offse1) {
        super(level);
        this.living = living;
        this.vec3offse = vec3offse;
        this.vec3offse1 = vec3offse1;
    }

    @Override
    public void updateFXObjectTick(IFXObject fxObject) {
        super.updateFXObjectTick(fxObject);
        tickcountO = tickcount;
        tickcount++;
    }

    @Override
    public void updateFXObjectFrame(IFXObject fxObject, float partialTicks) {
        if(living!=null&&living.isAlive()){
            float lerp = MathUtils.easeOutCubic(Mth.lerp(partialTicks,tickcountO,tickcount)/21);
            Vec3 position1 = FFEntityUtils.getBodyRotVec(living,vec3offse);
            Vec3 position2 = FFEntityUtils.getBodyRotVec(living,vec3offse1);
            Vec3 position = position1.add(position2.subtract(position1).scale(lerp));

            fxObject.updatePos(new Vector3f((float) (position.x + (double) this.offset.x), (float) (position.y + (double) this.offset.y), (float) (position.z + (double) this.offset.z)));
            fxObject.updateRotation(new Vector3f(0, (float) (-living.getYRot() / 180 * Math.PI), 0));
        }
    }

    @Override
    public void start() {
    }
}
