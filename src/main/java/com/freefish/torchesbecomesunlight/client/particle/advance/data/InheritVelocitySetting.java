package com.freefish.torchesbecomesunlight.client.particle.advance.data;

import com.freefish.torchesbecomesunlight.client.particle.advance.AdvancedRLParticleBase;
import com.freefish.torchesbecomesunlight.client.particle.advance.data.number.NumberFunction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

/**
 * @author KilaBash
 * @date 2023/5/30
 * @implNote InheritVelocitySetting
 */
@OnlyIn(Dist.CLIENT)
public class InheritVelocitySetting extends ToggleGroup {
    public enum Mode {
        Position,
        Velocity
    }

    protected Mode mode = Mode.Position;

    protected NumberFunction multiply = NumberFunction.constant(1);

    public NumberFunction getMultiply() {
        return multiply;
    }

    public void setMultiply(NumberFunction multiply) {
        this.multiply = multiply;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Vector3f getVelocityAddition(AdvancedRLParticleBase particle, AdvancedRLParticleBase emitter) {
        var mul = multiply.get(particle.getT(), () -> particle.getMemRandom(this)).floatValue();
        if (mode == Mode.Velocity) {
            return emitter.getVelocity().mul(mul);
        }
        return new Vector3f(0 ,0, 0);
    }

    public Vector3f getPosition(AdvancedRLParticleBase emitter, Vector3f initialPos, float partialTicks) {
        if (mode == Mode.Position) {
            return emitter.getPos(partialTicks).sub(initialPos);
        }
        return new Vector3f(0 ,0, 0);
    }

}
