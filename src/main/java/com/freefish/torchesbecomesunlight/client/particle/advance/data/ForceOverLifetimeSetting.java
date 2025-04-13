package com.freefish.torchesbecomesunlight.client.particle.advance.data;

import com.freefish.torchesbecomesunlight.client.particle.advance.AdvancedRLParticleBase;
import com.freefish.torchesbecomesunlight.client.particle.advance.data.number.NumberFunction3;
import org.joml.Vector3f;

/**
 * @author KilaBash
 * @date 2023/5/30
 * @implNote LifetimeByEmitterSpeed
 */
public class ForceOverLifetimeSetting extends ToggleGroup {

    public NumberFunction3 getForce() {
        return force;
    }

    public void setForce(NumberFunction3 force) {
        this.force = force;
    }

    protected NumberFunction3 force = new NumberFunction3(0, 0, 0);

    public Vector3f getForce(AdvancedRLParticleBase particle) {
        return force.get(particle.getT(), () -> particle.getMemRandom(this)).mul(0.05f);
    }

}
