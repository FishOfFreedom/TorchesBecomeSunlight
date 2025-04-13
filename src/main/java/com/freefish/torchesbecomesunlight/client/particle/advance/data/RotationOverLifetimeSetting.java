package com.freefish.torchesbecomesunlight.client.particle.advance.data;

import com.freefish.torchesbecomesunlight.client.particle.advance.AdvancedRLParticleBase;
import com.freefish.torchesbecomesunlight.client.particle.advance.data.number.NumberFunction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;
import net.minecraft.util.Mth;

/**
 * @author KilaBash
 * @date 2023/5/30
 * @implNote RotationOverLifetimeSetting
 */
@OnlyIn(Dist.CLIENT)
public class RotationOverLifetimeSetting {
    protected NumberFunction roll = NumberFunction.constant(0);
    protected NumberFunction pitch = NumberFunction.constant(0);
    protected NumberFunction yaw = NumberFunction.constant(0);

    public NumberFunction getRoll() {
        return roll;
    }

    public void setRoll(NumberFunction roll) {
        this.roll = roll;
    }

    public NumberFunction getPitch() {
        return pitch;
    }

    public void setPitch(NumberFunction pitch) {
        this.pitch = pitch;
    }

    public NumberFunction getYaw() {
        return yaw;
    }

    public void setYaw(NumberFunction yaw) {
        this.yaw = yaw;
    }

    public Vector3f getRotation(AdvancedRLParticleBase particle, float partialTicks) {
        var t = particle.getT(partialTicks);
        return new Vector3f(
                roll.get(t, () -> particle.getMemRandom("rol0")).floatValue(),
                pitch.get(t, () -> particle.getMemRandom("rol1")).floatValue(),
                yaw.get(t, () -> particle.getMemRandom("rol2")).floatValue()).mul(Mth.TWO_PI / 360);
    }

}
